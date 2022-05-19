package main.ValueSate;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;



public class ValueStateDemo {
    public static void main(String[] args) throws  Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStateBackend(new EmbeddedRocksDBStateBackend());
        env.setParallelism(1);
        env.getCheckpointConfig().setCheckpointStorage("file:///checkpoint-dir");
        DataStreamSource<Tuple2<Integer, Long>> dataStreamSource = env.addSource( new RandomSource());
        long time1  = System.currentTimeMillis();
        dataStreamSource
                .keyBy(0)
                .flatMap(new SumWithValueState())
                .print();
        env.execute("TestStatefulApi");
        long time2 = System.currentTimeMillis();

        System.out.println("Cost time = "+(time2 - time1));
    }

    private static class RandomSource implements SourceFunction<Tuple2<Integer, Long>> {
        @Override
        public void run(SourceContext<Tuple2<Integer, Long>> ctx) throws Exception {
            int counter = 0;
            while (counter<10000) {
                for (int key = 0; key < 7; key++) {
                    ctx.collect(Tuple2.of(key, 1l));
                }
                counter+=1;
            }
        }

        @Override
        public void cancel() {

        }
    }
}
