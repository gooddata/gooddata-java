# GoodData Java SDK [![Build Status](https://travis-ci.org/martiner/gooddata-java.png?branch=master)](https://travis-ci.org/martiner/gooddata-java) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.gooddata/gooddata-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.gooddata/gooddata-java)

The *GoodData Java SDK* encapsulates the REST API provided by the [GoodData](http://www.gooddata.com) platform.
The first version was implemented during the [All Data Hackathon](http://hackathon.gooddata.com) April 10 - 11 2014
and currently the SDK is transitioned to be an official GoodData project.

## Usage

The *GoodData Java SDK* is available in Maven Central Repository, to use it from Maven add to `pom.xml`:

```xml
<dependency>
    <groupId>com.gooddata</groupId>
    <artifactId>gooddata-java</artifactId>
    <version>2.1.0</version>
</dependency>
```
See [releases page](https://github.com/martiner/gooddata-java/releases) for information about versions and notable changes,
the [Upgrading Guide](https://github.com/martiner/gooddata-java/wiki/Upgrading-GoodData-Java-SDK) will navigate you
through changes between major versions.

See [Wiki](https://github.com/martiner/gooddata-java/wiki) for
[Code Examples](https://github.com/martiner/gooddata-java/wiki/Code-Examples)
and [Extensibility How-To](https://github.com/martiner/gooddata-java/wiki/Extending-GoodData-Java-SDK).

### Dependencies

The *GoodData Java SDK* uses:
* the [GoodData HTTP client](https://github.com/gooddata/gooddata-http-client) version 0.9.3 or later
* the *Apache HTTP Client* version 4.3 or later (for white-labeled domains at least version 4.3.2 is required)
* the *Spring Framework* version 4.3.*
* the *Jackson JSON Processor* version 2.8.*
* the *Java Development Kit (JDK)* version 8 or later

## License and conditions

The *GoodData Java SDK* is free and open-source software provided by GoodData Corporation "as-is" under [BSD License](LICENSE.txt).

## Contribute

Found a bug? Please create an [issue](https://github.com/martiner/gooddata-java/issues). Missing functionality? 
[Contribute your code](CONTRIBUTING.md).
