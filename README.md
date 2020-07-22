# GoodData Java SDK
[![Build Status](https://travis-ci.org/gooddata/gooddata-java.png?branch=master)](https://travis-ci.org/gooddata/gooddata-java)
[![Javadocs](http://javadoc.io/badge/com.gooddata/gooddata-java.svg)](http://javadoc.io/doc/com.gooddata/gooddata-java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.gooddata/gooddata-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.gooddata/gooddata-java)
[![Release](https://img.shields.io/github/release/gooddata/gooddata-java.svg)](https://github.com/gooddata/gooddata-java/releases)
[![Stability: Active](https://masterminds.github.io/stability/active.svg)](https://masterminds.github.io/stability/active.html)
[![License](https://img.shields.io/badge/license-BSD-blue.svg)](LICENSE.txt)

The *GoodData Java SDK* encapsulates the REST API of the **GoodData Platform**.
The first version was implemented during the [All Data Hackathon](http://hackathon.gooddata.com) April 10 - 11 2014.
It is free and open-source software provided "as-is" under the [BSD License](LICENSE.txt) as an official project by [GoodData Corporation](http://www.gooddata.com).

## Usage

The *GoodData Java SDK* is available in Maven Central Repository, to use it from Maven add to `pom.xml`:

```xml
<dependency>
    <groupId>com.gooddata</groupId>
    <artifactId>gooddata-java</artifactId>
    <version>3.0.1+api3</version>
</dependency>
```
See [releases page](https://github.com/gooddata/gooddata-java/releases) for information about versions and notable changes,
the [Upgrading Guide](https://github.com/gooddata/gooddata-java/wiki/Upgrading) will navigate you
through changes between major versions.

See [Javadocs](http://javadoc.io/doc/com.gooddata/gooddata-java)
or [Wiki](https://github.com/gooddata/gooddata-java/wiki) for
[Code Examples](https://github.com/gooddata/gooddata-java/wiki/Code-Examples)
and [Extensibility How-To](https://github.com/gooddata/gooddata-java/wiki/Extending).

### API version
Since *GoodData Java SDK* version *2.32.0* API versioning is supported. The API version, GoodData Java is compatible with, is marked in artifact version using `+api<NUMBER>` suffix (i.e. `2.32.0+api1` is compatible with API version `1`).

### Dependencies

The *GoodData Java SDK* uses:
* the [GoodData HTTP client](https://github.com/gooddata/gooddata-http-client) version 0.9.3 or later
* the *Apache HTTP Client* version 4.5 or later (for white-labeled domains at least version 4.3.2 is required)
* the *Spring Framework* version 5* (can be used with spring 4.3.* as well)
* the *Jackson JSON Processor* version 2.*
* the *Slf4j API* version 1.7.*
* the *Java Development Kit (JDK)* version 11 or later to build, can run on 8 and later

##### Retry of failed API calls

You can retry your failed requests since version *2.34.0*. Turn it on by configuring
[RetrySettings](https://github.com/gooddata/gooddata-java/blob/master/src/main/java/com/gooddata/retry/RetrySettings.java)
and add [Spring retry](https://github.com/spring-projects/spring-retry) to your classpath:
```xml
        <dependency>
            <groupId>org.springframework.retry</groupId>
            <artifactId>spring-retry</artifactId>
            <version>${spring.retry.version}</version>
        </dependency>
```

### Logging

The *GoodData Java SDK* logs using `slf4j-api`. Please adjust your logging configuration for 
`com.gooddata.sdk.*` loggers or alternatively turn the logging off using following dependency:

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-nop</artifactId>
</dependency>
```

### Date/Time
The *GoodData Java SDK* is using Java 8 Date/Time API (JSR 310) for all Date / Time / Zone public facing types.
Good SO thread about differences between various types in Java Date/Time API: https://stackoverflow.com/a/32443004

## Development

Build the library with `mvn package`, see the
[Testing](https://github.com/gooddata/gooddata-java/wiki/Testing) page for different testing methods.

## Contribute

Found a bug? Please create an [issue](https://github.com/gooddata/gooddata-java/issues). Missing functionality?
[Contribute your code](CONTRIBUTING.md). Any questions about GoodData or this library? Check out [the GoodData community website](http://community.gooddata.com/).
