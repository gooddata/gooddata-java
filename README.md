# GoodData Java SDK

[![Build Status](https://github.com/gooddata/gooddata-java/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/gooddata/gooddata-java/actions/workflows/build.yml)
[![Coverage Status](https://codecov.io/gh/gooddata/gooddata-java/branch/master/graph/badge.svg)](https://app.codecov.io/gh/gooddata/gooddata-java/branch/master)
[![Stability: Active](https://masterminds.github.io/stability/active.svg)](https://masterminds.github.io/stability/active.html)
[![License](https://img.shields.io/badge/license-BSD-blue.svg)](LICENSE.txt)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fgooddata%2Fgooddata-java.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fgooddata%2Fgooddata-java?ref=badge_shield)

[![Javadocs](http://javadoc.io/badge/com.gooddata/gooddata-java.svg)](http://javadoc.io/doc/com.gooddata/gooddata-java)
[![Javadocs Model](https://javadoc.io/badge2/com.gooddata/gooddata-java-model/javadoc--model.svg)](https://javadoc.io/doc/com.gooddata/gooddata-java-model)
[![Maven Central](https://img.shields.io/maven-central/v/com.gooddata/gooddata-java)](https://central.sonatype.com/artifact/com.gooddata/gooddata-java)
[![Release](https://img.shields.io/github/v/release/gooddata/gooddata-java?sort=semver)](https://github.com/gooddata/gooddata-java/releases)

The *GoodData Java SDK* encapsulates the REST API of the **GoodData Platform**.

**The project is currently NOT in "active development". Meaning that feature request may or may not be implemented.
You are welcomed to [contribute your code](CONTRIBUTING.md) and create
an [issue](https://github.com/gooddata/gooddata-java/issues).**

The first version was implemented during the [All Data Hackathon](http://hackathon.gooddata.com) April 10 - 11 2014.
It is free and open-source software provided "as-is" under the [BSD License](LICENSE.txt) as an official project
by [GoodData Corporation](http://www.gooddata.com).

## Supported versions

In order to make the user experience with integrating GoodData Java SDK as smooth and secure as possible and to ensure
that the SDK is using the latest features of the platform, we only provide support to the most recent major version of
Java SDK.

The most recent major will be supported in the following mode:

- The latest major version will receive new functionality and bug fixes. These changes will be applied on top of last
  released version.
- GoodData customer support will provide support for the latest major version only.

- The customers are encouraged to always use the latest version of the Java SDK.
- In case of using older versions, the user might face API incompatibility, performance or security issues.

Please follow the [upgrade instructions](https://github.com/gooddata/gooddata-java/wiki/Upgrading) to update to the
newest version.

## Modules

The *GoodData Java SDK* contains following modules:

* [gooddata-java](./gooddata-java) - The GoodData API client (depends on `gooddata-java-model`).
* [gooddata-java-model](./gooddata-java-model) - Lightweight library containing only GoodData API structures.
* [gooddata-java-parent](./pom.xml) - Parent for *GoodData Java SDK* libraries (just a wrapper around `gooddata-java`
  and `gooddata-java-model`).

## Usage

The *GoodData Java SDK* is available in Maven Central Repository, to use it from Maven add to `pom.xml`:

```xml
<dependency>
    <groupId>com.gooddata</groupId>
    <artifactId>gooddata-java</artifactId>
    <version>{MAJOR}.{MINOR}.{PATCH}+api{API}</version>
</dependency>
```

See [releases page](https://github.com/gooddata/gooddata-java/releases) for information about versions and notable
changes,
the [Upgrading Guide](https://github.com/gooddata/gooddata-java/wiki/Upgrading) will navigate you
through changes between major versions.

See [Javadocs](http://javadoc.io/doc/com.gooddata/gooddata-java)
or [Wiki](https://github.com/gooddata/gooddata-java/wiki) for
[Code Examples](https://github.com/gooddata/gooddata-java/wiki/Code-Examples)
and [Extensibility How-To](https://github.com/gooddata/gooddata-java/wiki/Extending).

### API version

Since *GoodData Java SDK* version *2.32.0* API versioning is supported. The API version, GoodData Java is compatible
with, is marked in artifact version using `+api<NUMBER>` suffix (i.e. `2.32.0+api1` is compatible with API version `1`).

### Dependencies

The *GoodData Java SDK* uses:

* the [GoodData HTTP client](https://github.com/gooddata/gooddata-http-client) version 3.0.0
* the *Apache HTTP Client* version 5.5.1
* the *Apache HTTP Client* version 4.5.13 (for compatibility with Sardine WebDAV library)
* the *Spring Framework* version 6.0.15
* the *Jackson JSON Processor* version 2.13.4
* the *Slf4j API* version 2.0.17
* the *Java Development Kit (JDK)* version 17 or later to build

##### Retry of failed API calls

You can retry your failed requests since version *2.34.0*. Turn it on by configuring
[RetrySettings](https://github.com/gooddata/gooddata-java/blob/master/src/main/java/com/gooddata/retry/RetrySettings.java)
and add [Spring retry](https://github.com/spring-projects/spring-retry) to your classpath:

```xml
        <dependency>
            <groupId>org.springframework.retry</groupId>
            <artifactId>spring-retry</artifactId>
            <version>2.0.12</version>
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

For releasing see [Releasing How-To](https://github.com/gooddata/gooddata-java/wiki/Releasing).

## Contribute

Found a bug? Please create an [issue](https://github.com/gooddata/gooddata-java/issues). Missing functionality?
[Contribute your code](CONTRIBUTING.md). Any questions about GoodData or this library? Check
out [the GoodData community website](http://community.gooddata.com/).
