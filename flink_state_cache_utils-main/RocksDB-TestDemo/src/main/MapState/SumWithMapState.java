package main.MapState;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public class SumWithMapState extends RichFlatMapFunction<Tuple2<Long,Long>, Tuple2<Long,Long>> {

    private MapState<Long,Long> mapState;

    @Override
    public void open(Configuration parameters) throws Exception {
        MapStateDescriptor<Long,Long> descriptor = new MapStateDescriptor<Long, Long>("sum",Long.class,Long.class);

        mapState = getRuntimeContext().getMapState(descriptor);
    }


    @Override
    public void flatMap(Tuple2<Long, Long> element, Collector<Tuple2<Long, Long>> out) throws Exception {
        Long currentValue = mapState.get(element.f0);

        if(currentValue==null){
            currentValue=0L;
        }

        currentValue+=element.f1;

        mapState.put(element.f0, currentValue);

        out.collect(Tuple2.of(element.f0,currentValue));
    }
}
