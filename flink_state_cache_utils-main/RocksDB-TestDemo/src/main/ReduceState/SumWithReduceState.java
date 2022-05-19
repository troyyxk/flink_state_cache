package main.ReduceState;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public class SumWithReduceState extends RichFlatMapFunction<Tuple2<Long, Long>, Tuple2<Long, Long>> {

    ReducingState<Long> reducingState;

    @Override
    public void open(Configuration parameters) throws Exception {
        ReducingStateDescriptor<Long> descriptor = new ReducingStateDescriptor<>(
                "sum",
                new ReduceFunction<Long>() {
                    @Override
                    public Long reduce(Long v1, Long v2) throws Exception {
                        return v1 + v2;
                    }
                }, Long.class);
        reducingState = getRuntimeContext().getReducingState(descriptor);
    }

    @Override
    public void flatMap(Tuple2<Long, Long> element, Collector<Tuple2<Long, Long>> out) throws Exception {
        reducingState.add(element.f1);
        out.collect(new Tuple2(element.f0,reducingState.get()));
    }
}
