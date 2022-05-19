package main.AggregateState;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.AggregatingState;
import org.apache.flink.api.common.state.AggregatingStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public class SumWithAggregateState extends RichFlatMapFunction<Tuple2<Long,Long>, Tuple2<Long,Long>> {
    AggregatingState<Long, Long> aggregatingState;

    @Override
    public void open(Configuration parameters) throws Exception {
        AggregatingStateDescriptor<Long, Long, Long> descriptor = new AggregatingStateDescriptor<>("sum",
                new AggregateFunction<Long, Long, Long>() {

                    @Override
                    public Long createAccumulator() {
                        return 0L;
                    }

                    @Override
                    public Long add(Long l, Long accumulator) {
                        return l+accumulator;
                    }

                    @Override
                    public Long getResult(Long accumulator) {
                        return accumulator;
                    }

                    @Override
                    public Long merge(Long aLong, Long acc1) {
                        return null;
                    }
                },Long.class);

        aggregatingState = getRuntimeContext().getAggregatingState(descriptor);
    }


    @Override
    public void flatMap(Tuple2<Long, Long> element, Collector<Tuple2<Long, Long>> out) throws Exception {
        aggregatingState.add(element.f1);
        out.collect(new Tuple2<>(element.f0,aggregatingState.get()));
    }
}
