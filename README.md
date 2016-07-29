# GoodData Java SDK [![Build Status](https://travis-ci.org/martiner/gooddata-java.png?branch=master)](https://travis-ci.org/martiner/gooddata-java) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.gooddata/gooddata-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.gooddata/gooddata-java)

The *GoodData Java SDK* encapsulates the REST API provided by the [GoodData](http://www.gooddata.com) platform.
The first version was implemented during the [All Data Hackathon](http://hackathon.gooddata.com) April 10 - 11 2014
and currently the SDK is transitioned to be an official GoodData project.

## Usage

See [Wiki](https://github.com/martiner/gooddata-java/wiki) for
[Upgrading Guide](https://github.com/martiner/gooddata-java/wiki/Upgrading-GoodData-Java-SDK),
[Code Examples](https://github.com/martiner/gooddata-java/wiki/Code-Examples)
and [Extensibility How-To](https://github.com/martiner/gooddata-java/wiki/Extending-GoodData-Java-SDK).

The *GoodData Java SDK* is available in Maven Central Repository, to use it from Maven add to `pom.xml`:

```xml
<dependency>
    <groupId>com.gooddata</groupId>
    <artifactId>gooddata-java</artifactId>
    <version>2.0.0.RC</version>
</dependency>
```

See [releases page](https://github.com/martiner/gooddata-java/releases) for information about versions and notable changes.

### Dependencies

The *GoodData Java SDK* uses:
* the [GoodData HTTP client](https://github.com/gooddata/gooddata-http-client) version 0.9.3 or later
* the *Apache HTTP Client* version 4.3 or later (for white-labeled domains at least version 4.3.2 is required)
* the *Spring Framework* version 3.x
* the *Jackson JSON Processor* version 2.7.0 (*GoodData Java SDK* version 0.x requires version 1.9)
* the *Java Development Kit (JDK)* version 7 or later

## License
The *GoodData Java SDK* is free and open-source software under [BSD License](LICENSE.txt).

##Contribute
Missing functionality? Found a BUG? Please create an [issue](https://github.com/martiner/gooddata-java/issues)
or simply [contribute your code](CONTRIBUTING.md).
