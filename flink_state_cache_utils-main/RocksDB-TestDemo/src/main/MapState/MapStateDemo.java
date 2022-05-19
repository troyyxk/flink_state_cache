package main.MapState;

import main.AggregateState.SumWithAggregateState;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

public class MapStateDemo {
    public static void main(String[] args) throws  Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStateBackend(new EmbeddedRocksDBStateBackend());
        env.getCheckpointConfig().setCheckpointStorage("file:///checkpoint-dir");
        env.setParallelism(1);

        List<Tuple2<Long,Long>> list = new ArrayList<>();
        list.add(Tuple2.of(1L, 3L));
        list.add(Tuple2.of(1L, 5L));
        list.add(Tuple2.of(2L, 7L));
        list.add(Tuple2.of(2L, 4L));
        list.add(Tuple2.of(3L, 2L));
        list.add(Tuple2.of(3L, 5L));
        list.add(Tuple2.of(4L, 1L));
        list.add(Tuple2.of(4L, 2L));
        DataStreamSource<Tuple2<Long, Long>> dataStreamSource = env.fromCollection(list);

        dataStreamSource
                .keyBy(0)
                .flatMap(new SumWithMapState())
                .print();
        env.execute("TestReduceCache");
    }
}
