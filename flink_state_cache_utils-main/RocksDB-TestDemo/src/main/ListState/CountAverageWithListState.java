package main.ListState;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava30.com.google.common.collect.Lists;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Collections;

public class CountAverageWithListState extends RichFlatMapFunction<Tuple2<Long,Long>, Tuple2<Long, Long>> {
    ListState<Long> elementByKey;

    @Override
    public void open(Configuration parameters) throws Exception {

        ListStateDescriptor<Long> descriptor =
                new ListStateDescriptor<Long>(
                        "average",
                        Types.LONG);

        elementByKey = getRuntimeContext().getListState(descriptor);
    }

    @Override
    public void flatMap(Tuple2<Long, Long> element, Collector<Tuple2<Long, Long>> out) throws Exception {
        Iterable<Long> currentState = elementByKey.get();

        if (currentState.equals(Collections.emptyList())) {
            elementByKey.addAll(Collections.emptyList());
        }

        elementByKey.add(element.f1);

        ArrayList<Long> allElement = Lists.newArrayList(elementByKey.get());
        long sum=0;
        for(Long ele:allElement){
            sum+=ele;
        }
        out.collect(new Tuple2<Long, Long>(element.f0,sum));

    }
}
