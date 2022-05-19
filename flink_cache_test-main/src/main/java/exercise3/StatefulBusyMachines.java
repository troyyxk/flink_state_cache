package exercise3;

import datatypes.EventType;
import datatypes.TaskEvent;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import sources.TaskEventSource;
import utils.AppBase;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.io.IOException;

/**
 * Write a program that identifies every 5 minutes the busiest machines in the cluster during the last 15 minutes.
 * For every window period, the program must then output the number of busy machines during that last period.
 * <p>
 * A machine is considered busy if more than a threshold number of tasks have been scheduled on it
 * during the specified window period (size).
 */
public class StatefulBusyMachines extends AppBase {

    public static void main(String[] args) throws Exception {

        //long start = System.currentTimeMillis();

        ParameterTool params = ParameterTool.fromArgs(args);
        String input = params.get("input", pathToTaskEventData);
        final int busyThreshold = params.getInt("threshold", 30);

        final int servingSpeedFactor = 60000; // events of 10 minutes are served in 1 second

        // set up streaming execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStateBackend(new EmbeddedRocksDBStateBackend());

        // start the data generator
        DataStream<TaskEvent> taskEvents = env
                .addSource(taskSourceOrTest(new TaskEventSource(input, 0, servingSpeedFactor)))
                .setParallelism(1);

        // machineId - window_end - task_count
        DataStream<Tuple3<Long, Long, Long>> busyMachines = taskEvents
                // only keep SCHEDULE events
                .filter((FilterFunction<TaskEvent>) taskEvent -> taskEvent.eventType.equals(EventType.SCHEDULE))
                // group stream per machine
                .keyBy(new KeySelector<TaskEvent, Long>() {
                    @Override
                    public Long getKey(TaskEvent te) throws Exception {
                        return te.machineId;
                    }
                })
                // apply sliding window
                .window(SlidingEventTimeWindows.of(Time.minutes(30), Time.minutes(1)))
                .apply(new TaskCounter())
                // apply the threshold
                .filter((Tuple3<Long, Long, Long> count) -> (count.f2 >= busyThreshold));

        // count how many machines are busy during each time window
        // window_end - count
        DataStream<Tuple2<Long, Integer>> busyMachinesPerWindow = busyMachines
                // group by timestamp (window end)
                .keyBy((KeySelector<Tuple3<Long, Long, Long>, Long>) t -> t.f1)
                .process(new CountBusyMachinesPerWindow());

        printOrTest(busyMachinesPerWindow);
        env.execute("Busy machines every 5 minutes");
        //long end = System.currentTimeMillis();
        //System.out.println(end - start);
    }

    /**
     * Keep a count of busy machines per window and register timers to output the count only once.
     */
    private static final class CountBusyMachinesPerWindow extends KeyedProcessFunction<Long, Tuple3<Long, Long, Long>, Tuple2<Long, Integer>> {

        private ValueState<Integer> countState;

        @Override
        public void open(Configuration parameters) {
            countState = getRuntimeContext().getState(new ValueStateDescriptor<>("count-state", Integer.class));
        }

        @Override
        public void processElement(Tuple3<Long, Long, Long> inWindow, Context ctx, Collector<Tuple2<Long, Integer>> out) throws IOException {

            Integer currentCount = countState.value();

            if (currentCount != null) {
                countState.update(++currentCount);
            } else {
                countState.update(1);
            }
            ctx.timerService().registerEventTimeTimer(inWindow.f1);
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple2<Long, Integer>> out) throws IOException {
            int machineCnt = countState.value();
            countState.clear(); // always clean up state!
            out.collect(new Tuple2<>(ctx.getCurrentKey(), machineCnt));
        }
    }

    /**
     * Counts the number of tasks scheduled.
     * Output <machineId, window_end, count>
     */
    private static class TaskCounter implements WindowFunction<
            TaskEvent,                  // input type
            Tuple3<Long, Long, Long>,   // output type
            Long,                       // key type
            TimeWindow>                 // window type
    {
        @Override
        public void apply(Long machineId, TimeWindow window, Iterable<TaskEvent> input, Collector<Tuple3<Long, Long, Long>> out) {

            long windowTime = window.getEnd();
            long count = 0;

            for (TaskEvent ignored : input) {
                count += 1;
            }
            out.collect(new Tuple3<>(machineId, windowTime, count));
        }
    }
}
