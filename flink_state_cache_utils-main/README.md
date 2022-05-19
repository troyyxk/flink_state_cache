Extra Demo Run Instructions

1. Clone the Team 9/Flink-Simplified. Build flink from source code.

2. Clone the flink_state_cache_utils/RocksDB-TestDemo.

3. There are five demo packages for different Keyed States. But the tasks are the same: sum values for the same key. 

-  For value state demo, there are two different examples. Class StreamingJob.class is an word count example that can listen to the port we set. You should open the port in terminal first before runing this demo. eg. "nc -l 18081"

-  Anthoer value state demo is still sum value demo, but the data source is a random source. We used it to test our cache system and showed the results in sildes.

4. Pick one of the demo as Flink Job, and use maven to package.

5. Start cluster of the flink you just built.

6. Open flink ui at localhost 8081

7. Submit flink job

* Our default cache policy is LRU, and defalut cache size is 4000. If you want to change cache configuration, please check the RocksDBCache.class, which is in /flink-state-backends/flink-statebackend-rocksdb/src/main/java/org.apache.flink.contrib.streaming.state/cache/. After Change settings, you should rebuild it and restart flink cluster.
