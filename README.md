# GoodData Java SDK
[![Build Status](https://travis-ci.org/gooddata/gooddata-java.png?branch=master)](https://travis-ci.org/gooddata/gooddata-java)
[![Javadocs](http://javadoc.io/badge/com.gooddata/gooddata-java.svg)](http://javadoc.io/doc/com.gooddata/gooddata-java)
[![Javadocs Model](https://javadoc.io/badge2/com.gooddata/gooddata-java-model/javadoc--model.svg)](https://javadoc.io/doc/com.gooddata/gooddata-java-model)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.gooddata/gooddata-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.gooddata/gooddata-java)
[![Release](https://img.shields.io/github/release/gooddata/gooddata-java.svg)](https://github.com/gooddata/gooddata-java/releases)
[![Stability: Active](https://masterminds.github.io/stability/active.svg)](https://masterminds.github.io/stability/active.html)
[![License](https://img.shields.io/badge/license-BSD-blue.svg)](LICENSE.txt)

The *GoodData Java SDK* encapsulates the REST API of the **GoodData Platform**.
The first version was implemented during the [All Data Hackathon](http://hackathon.gooddata.com) April 10 - 11 2014.
It is free and open-source software provided "as-is" under the [BSD License](LICENSE.txt) as an official project by [GoodData Corporation](http://www.gooddata.com).

## Supported versions
 
In order to make the user experience with integrating GoodData Java SDK as smooth and secure as possible and to ensure that the SDK is using the latest features of the platform, we only provide support to the most recent major version of Java SDK. 
 
The most recent major will be supported in the following mode:
 
- The latest major version will receive new functionality and bug fixes. These changes will be applied on top of last released version.
- GoodData customer support will provide support for the latest major version only.

- The customers are encouraged to always use the latest version of the Java SDK.
- In case of using older versions, the user might face API incompatibility, performance or security issues.
 
Please follow the [upgrade instructions](https://github.com/gooddata/gooddata-java/wiki/Upgrading) to update to the newest version.

## Modules

The *GoodData Java SDK* contains following modules:
* [gooddata-java](./gooddata-java) - The GoodData API client (depends on `gooddata-java-model`).
* [gooddata-java-model](./gooddata-java-model) - Lightweight library containing only GoodData API structures.
* [gooddata-java-parent](./pom.xml) - Parent for *GoodData Java SDK* libraries (just a wrapper around `gooddata-java` and `gooddata-java-model`).

## Usage

The *GoodData Java SDK* is available in Maven Central Repository, to use it from Maven add to `pom.xml`:

```xml
<dependency>
    <groupId>com.gooddata</groupId>
    <artifactId>gooddata-java</artifactId>
    <version>3.5.0+api3</version>
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
