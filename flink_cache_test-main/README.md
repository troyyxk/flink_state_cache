Final Demo Run Instructions


1. Clone the Team 9/Flink-Simplified. Build flink from source code.


2. Clone flink_state_cache_test.


3. There are two tests in this repository. The first one named exercise3.StatefulBusyMachines is a job using sliding window using google cluster data, the second one named ValueSate.ValueStateDemo is a synthetic workload of key count. Both use ValueState.


4. Pick one of the demo as Flink Job, and use maven to package.


5. Start cluster of the flink you just built.


6. open flink ui at localhost 8081


7. submit flink job


* Our default cache policy is LRU, and default cache size is 4000. If you want to change cache configuration, please check the RocksDBCache.java, which is in 
'/flink-state-backends/flink-statebackend-rocksdb/src/main/java/org.apache.flink.contrib.streaming.state/cache/'. After Change settings, you should rebuild it and restart flink cluster.


* For the version using generics instead of serialization, please checkout last_try branch in Team 9/Flink-Simplified
