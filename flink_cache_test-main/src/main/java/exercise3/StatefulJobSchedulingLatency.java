package exercise3;

import datatypes.EventType;
import datatypes.JobEvent;
import sources.JobEventSource;
import utils.AppBase;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.util.Collector;

/**
 * Measure the time between submitting and scheduling each job in the cluster.
 * For each jobID, record the timestamp of the SUBMIT(0) event and look for a subsequent SCHEDULE(1) event.
 * Once received, output their time difference.
 * <p>
 * Note: If a job is submitted multiple times, then measure the latency since the first submission.
 */
public class StatefulJobSchedulingLatency extends AppBase {

    public static void main(String[] args) throws Exception {

        ParameterTool params = ParameterTool.fromArgs(args);
        final String input = params.get("input", AppBase.pathToJobEventData);
        final int servingSpeedFactor = params.getInt("servingSpeedFactor", 6000);

        // set up streaming execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // set latency tracking every 1s
        env.getConfig().setLatencyTrackingInterval(1000);

        // start the data generator
        DataStream<JobEvent> events = env
        .addSource(jobSourceOrTest(new JobEventSource(input, servingSpeedFactor)))
        .setParallelism(1);

        //Filter events and only keep submit and schedule
        DataStream<JobEvent> filteredEvents = events.filter(new SubmitOrScheduleFilter());

        // key-by jobId and watch for the SUBMIT -> SCHEDULE sequence
        DataStream<Tuple2<Long, Long>> jobIdWithLatency = filteredEvents.keyBy(taskEvents -> taskEvents.jobId).flatMap(new LatencyFlatMap());

        jobIdWithLatency.addSink(new SinkFunction<>() {
            @Override
            public void invoke(Tuple2<Long, Long> value, Context context) throws Exception {
                // no-op sink
            }
        });

        //printOrTest(jobIdWithLatency);

        // execute the dataflow
        env.execute("Job Scheduling Latency");
    }

    private static final class SubmitOrScheduleFilter implements FilterFunction<JobEvent> {
        @Override
        public boolean filter(JobEvent jobEvent) throws Exception {
            return jobEvent.eventType.equals(EventType.SCHEDULE)
                    || jobEvent.eventType.equals(EventType.SUBMIT);
        }
    }

    // every time we get a new event, check if it's a submit or schedule
    // if a new submit, add to the hashmap, if a schedule that matches a submit, then output latency
    // output (jobId, latency)
    private static final class LatencyFlatMap extends RichFlatMapFunction<JobEvent, Tuple2<Long, Long>> {

        private ValueState<JobEvent> eventsState;

        @Override
        public void open(Configuration config) {
            // initialize the state descriptors here
            eventsState = getRuntimeContext().getState(new ValueStateDescriptor<>("events", JobEvent.class));
        }

        @Override
        public void flatMap(JobEvent jobEvent, Collector<Tuple2<Long, Long>> out) throws Exception {

            JobEvent stored = eventsState.value();

            if (stored != null) { // we have a matching event
                eventsState.clear(); // clear state
                if (stored.eventType.equals(EventType.SUBMIT) && jobEvent.eventType.equals(EventType.SCHEDULE)) {
                    // output <jobId, latency>
                    out.collect(new Tuple2<>(jobEvent.jobId, jobEvent.timestamp - stored.timestamp));
                } else {
                    out.collect(new Tuple2<>(jobEvent.jobId, stored.timestamp - jobEvent.timestamp));
                }
            } else {
                // first event for this jobId => just store it
                eventsState.update(jobEvent);
            }
        }
    }
}
