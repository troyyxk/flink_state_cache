package utils;

import datatypes.JobEvent;
import datatypes.TaskEvent;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class AppBase {
    public static SourceFunction<JobEvent> jobEvents = null;
    public static SourceFunction<TaskEvent> taskEvents = null;
    public static SourceFunction<String> strings = null;
    public static SinkFunction out = null;
    public static int parallelism = 4;

    public final static String pathToJobEventData = "/home/jingyusu/Desktop/BU_Courses/SP2022/CS551/test_test/data/job_events";
    public final static String pathToTaskEventData = "/home/jingyusu/Desktop/BU_Courses/SP2022/CS551/test_test/data/task_events";


    public static SourceFunction<JobEvent> jobSourceOrTest(SourceFunction<JobEvent> source) {
        if (jobEvents == null) {
            return source;
        }
        return jobEvents;
    }

    public static SourceFunction<TaskEvent> taskSourceOrTest(SourceFunction<TaskEvent> source) {
        if (taskEvents == null) {
            return source;
        }
        return taskEvents;
    }

    public static SinkFunction<?> sinkOrTest(SinkFunction<?> sink) {
        if (out == null) {
            return sink;
        }
        return out;
    }

    public static SourceFunction<String> stringSourceOrTest(SourceFunction<String> source) {
        if (strings == null) {
            return source;
        }
        return strings;
    }

    public static void printOrTest(org.apache.flink.streaming.api.datastream.DataStream<?> ds) {
        if (out == null) {
            ds.print();
        } else {
            ds.addSink(out);
        }
    }
}
