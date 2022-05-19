# Design Document for Project: A state caching system for Apache Flink

## 1. Problem statement
- Our project targets to resolve such a problem: How to build a cache layer in Flink data stream, s.t. desired state can be fetched from it with a high hit rate, therefore saves the effort to serialize/deserailize state, and provide better performance metrics?
- The problem is important because previous studies have revealed the significant degree of locality of Flink state access workloads, yet this locality is not properly utilized. By building a state cache, we can utilize this locality and expect to boost the performance of the system.
- Developers who create Flink data stream applications will benefit from this, as it is expected provide better performance.

## 2. Proposed Solution
- Our basic approach is to first implement a LRU cache on Apache Flink states. This should act as the first milestone for this project as this marks the completion of the augmented cached Flink source code as well as the first testing of cached Flink vs blank Flink for efficiency. This should provide a perspective of the final outcome. We would expand the project by adding different cache policies such as FIFO, CLOCK etc. We would require different types of stream data with differences in characteristics such as size, frequency etc. Baselines to test against include the blank Flink, and augmented Flink with Redis for memory management.
- Depending on the outcome, we may see pattern of different cache policy been more efficient for different dataset, eg. some are good for large dataset with high frequency, some may be good for small dataset with low frequency. We may implement a dynamic cache policy and add the data for this one into the final result of cache efficiency testing.
- We plan to develop a set of cache APIs, which may or may not implement existing Flink interfaces, and insert the API invocation at places where operators would retrieve states from RocksDB.
- Our current idea on how the insertion can be done: Put a cache member field inside operator class, and query the cache each time before querying RocksDB. Time permits, we may turn caching into a tunable feature.
- Python scripts would be used to run the Flink system. It will also collect related data such as time consumed, cache hit rate etc.
- We would require help regarding gathering different types of dataset.


## 3. Expectations
- We expect we can implement, at least, the LRU cache strategy, and add more cache eviction strategies, such as LIFO, FIFO, if we have time.
- We will run test data against our Flink implementations with cache, and collect the timing and other indicator information on efficiency as expermental group. We will run against the original Apache Flink ("blank") the same test data and collect indicator information as control group. We expect data on the control groups showing our Flink impementations with cache to be more efficent than the original Apache Flink ("blank"), ceteris paribus, with statistically significance and confidence level 95%.
- We expect our implementations can generate correct outputs.
- We expect our implementations reasonably robust.
- We will have a delivery schedule and expect the code and corresponding reports delivered on time.

## 4. Experimental Plan
- We are going to primarily take measurements on the query latency and throughput in terms of state access queries per time unit of Apache flink with and without our caching system. 
- We plan to conduct simulation utilizing an existing benchmark, idealy SysX as persented in the paper, as it is both open source and accurately simulates real-life stream state access workloads. 
- Several experiments are to be taken on a couple of datasets, Borg, Taxi, and Azure for instance, that have different workload characteristics. Experiments are also going to be conducted under various system configurations to test the performance under, for example, different block eviction policy, varying cache size, and different input stream configurations (watermark frequency, out-of-orderliness, etc.). Time permits, we will also study the performance of a cache system with dynamic configuration capabilities that collects statistics and actively adjusts itself to the workload.

- We will compare the performance measurements of vanilla Flink with our system with a cache. If the cached system turns out to have a noticeable improvement, then we can conclude that the gain from utilizing the strong locality of stream state access queries covers the overhead to maintain the cache, otherwise the conclusion is it may not be worthwhile building a cache and the current system is just good enough. 
- We would need access to SysX and related documents, and might need access to cloud services such as AWS or Azure to produce convincing experiment results.

## 5. Success Indicators
The ultimate goals of this project includes:
- Experimenting the pros and cons in performance of different cache policies under various simulation conditions
- Design and implement a dynamic cache policies that will perform at least on par with the static cache policies and in expectation outperform other policies in complicate environment with rapid change data stream

The milestone are set to proof of concepts for next milestones and eventually leads to the final product:
- We will first implement an LRU cache policy and show its difference in performance in comparison to the blank Apache Flink we use as a baseline. This step is important as it should prove that locality exists in the stream data system.
- We will then implement other cache policy and show that they have different performance under same condition
- We will gather data of different types (magnitude, frequency) and gather performance data of different policies and write first report
- Based on the difference of performance, we wish to have a policy that out-perform other policy under certain conditions and we will implement a dynamic policy that will switch policy when facing different types of data stream
- (optional) we would try data mining to refine the dynamic cache system


## 6. Task assignment

- We plan to break down the project into tasks based on different caching policies.  In each experiment for one caching policy, we will first do evaluation, and then implement it. Finally test it and compare it with others.  After each we will also evaluate the result and see what parameters can be changed in the next experiments. By collecting all possible caching policies data, we will compare them and give a final conclusion. 
- The very first step we take is read Flink source code and figure out the operation model. Then we find out where in the code operators fetches states.

- After that we will initialize all the environments set to make sure every team member could handle a task. Then these tasks will be assigned to team members by their selections of caching policies. Different experiments for caching policies could be developed individually and in parallel. But they are really connected and we will have a meeting once a week to update information from each other. 
