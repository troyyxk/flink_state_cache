# Apache Flink With RocksDB Cache

Apache Flink is an open source stream processing framework with powerful stream- and batch-processing capabilities.

Learn more about Flink at [https://flink.apache.org/](https://flink.apache.org/)

We add a Cache System to RocksDB backend to improve Flink performance when using RocksDB


### implementation

* our cache system code is in flink-state-backends/flink-statebackend-rocksdb/src/main/java/org.apache.flink.contrib.streaming.state/cache/

* we add our cache as RocksDBCache to AbstractRocksDBState.class

* There are two versions of implementation. 
First one is using generics key and namespace, which avoid serialization. 
Another one is store serialized key and namespace byte[] instead, which solves generics problem but has more serialization.

* For the version using generics instead of serialization, please checkout last_try branch.

### Change Settings

* You could change cache settings in RocksDBCache.class which at 'flink-state-backends/flink-statebackend-rocksdb/src/main/java/org.apache.flink.contrib.streaming.state/cache/'

* 'defaultSize' corresponding to cache size, and 'get***CacheManager' means using one cache policy, eg. 'getLRUCacheManager'. 
```
private final int defaultSize = 4000;

    public RocksDBStateCache() {
        this.cacheManager = CacheManagerFactory.getLRUCacheManager(defaultSize);
    }

```
* Other Cache Policies please see CacheManagerFactory.class

### Test Example
* See Team 9/flink_cache_test

## Building Apache Flink from Source

Prerequisites for building Flink:

* Unix-like environment (we use Linux, Mac OS X, Cygwin, WSL)
* Git
* Maven (we recommend version 3.2.5 and require at least 3.1.1)
* Java 8 or 11 (Java 9 or 10 may work)

```
git clone https://cs551-gitlab.bu.edu/cs551/spring22/team-9/flink-simplified-cache.git
cd flink-simplified-cache
mvn clean package -DskipTests # this will take up to 10 minutes
```

Flink is now installed in `build-target`.

*NOTE: Maven 3.3.x can build Flink, but will not properly shade away certain dependencies. Maven 3.1.1 creates the libraries properly.
To build unit tests with Java 8, use Java 8u51 or above to prevent failures in unit tests that use the PowerMock runner.*

## Developing Flink

The Flink committers use IntelliJ IDEA to develop the Flink codebase.
We recommend IntelliJ IDEA for developing projects that involve Scala code.

Minimal requirements for an IDE are:
* Support for Java and Scala (also mixed projects)
* Support for Maven with Java and Scala


### IntelliJ IDEA

The IntelliJ IDE supports Maven out of the box and offers a plugin for Scala development.

* IntelliJ download: [https://www.jetbrains.com/idea/](https://www.jetbrains.com/idea/)
* IntelliJ Scala Plugin: [https://plugins.jetbrains.com/plugin/?id=1347](https://plugins.jetbrains.com/plugin/?id=1347)

Check out our [Setting up IntelliJ](https://nightlies.apache.org/flink/flink-docs-master/flinkDev/ide_setup.html#intellij-idea) guide for details.

### Eclipse Scala IDE

**NOTE:** From our experience, this setup does not work with Flink
due to deficiencies of the old Eclipse version bundled with Scala IDE 3.0.3 or
due to version incompatibilities with the bundled Scala version in Scala IDE 4.4.1.

**We recommend to use IntelliJ instead (see above)**

## Support

Don’t hesitate to ask!

Contact the developers and community on the [mailing lists](https://flink.apache.org/community.html#mailing-lists) if you need any help.

[Open an issue](https://issues.apache.org/jira/browse/FLINK) if you found a bug in Flink.


## Documentation

The documentation of Apache Flink is located on the website: [https://flink.apache.org](https://flink.apache.org)
or in the `docs/` directory of the source code.


## Fork and Contribute

This is an active open-source project. We are always open to people who want to use the system or contribute to it.
Contact us if you are looking for implementation tasks that fit your skills.
This article describes [how to contribute to Apache Flink](https://flink.apache.org/contributing/how-to-contribute.html).


## About

Apache Flink is an open source project of The Apache Software Foundation (ASF).
The Apache Flink project originated from the [Stratosphere](http://stratosphere.eu) research project.
