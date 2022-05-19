import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;


// bunch
public class wcBunch {
    public static void main(String[] args) throws Exception {
        // create env
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        // read data
        String inputPath = "src/main/resources/hello.txt";
        DataSource<String> stringDataSource = env.readTextFile(inputPath);

        // process data
        DataSet<Tuple2<String, Integer>> resultSet = stringDataSource.flatMap(new wcBunchFlatMapper())
                .groupBy(0)
                .sum(1);

        System.out.println("gate 1");
        resultSet.print();
        System.out.println(resultSet);
        System.out.println("gate 2");

    }

    public static class wcBunchFlatMapper implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
            String[] words = s.split(" ");
            for (String word : words) {
                collector.collect(new Tuple2<>(word, 1));
            }
        }
    }

}
