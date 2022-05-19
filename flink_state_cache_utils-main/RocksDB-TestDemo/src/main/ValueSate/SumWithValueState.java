package main.ValueSate;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public class SumWithValueState
        extends RichFlatMapFunction<Tuple2<Integer, Long>, Tuple2<Integer, Long>> {
    private ValueState<Long> Sum;

    @Override
    public void open(Configuration parameters) throws Exception {

        ValueStateDescriptor<Long> descriptor =
                new ValueStateDescriptor<Long>(
                        "average",  // 状态的名字
                        Types.LONG); // 状态存储的数据类型
        Sum = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void flatMap(Tuple2<Integer, Long> element, Collector<Tuple2<Integer, Long>> out) throws Exception {
        Long currentState = Sum.value();
        System.out.println("");

        if (currentState == null) {
            currentState = 0L;
        }


        currentState+= element.f1;
        Sum.update(currentState);

        out.collect(Tuple2.of(element.f0, currentState));
    }
}
