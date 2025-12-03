# GoodData Java SDK

[![Build Status](https://github.com/gooddata/gooddata-java/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/gooddata/gooddata-java/actions/workflows/build.yml)
[![Coverage Status](https://codecov.io/gh/gooddata/gooddata-java/branch/master/graph/badge.svg)](https://app.codecov.io/gh/gooddata/gooddata-java/branch/master)
[![Stability: Active](https://masterminds.github.io/stability/active.svg)](https://masterminds.github.io/stability/active.html)
[![License](https://img.shields.io/badge/license-BSD-blue.svg)](LICENSE.txt)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fgooddata%2Fgooddata-java.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fgooddata%2Fgooddata-java?ref=badge_shield)

[![Javadocs](https://javadoc.io/badge/com.gooddata/gooddata-java.svg)](https://javadoc.io/doc/com.gooddata/gooddata-java)
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

### Version 5.0 Release Notes

**Version 5.0.0** represents a major modernization release with the following key updates:

- **Java 17**: Minimum runtime requirement upgraded from Java 11
- **Spring 6**: Framework upgraded from Spring 5.x to Spring 6.0.15  
- **Apache HTTP Client 5**: Primary HTTP client upgraded from version 4.5.x to 5.5.1
- **Enhanced Compatibility**: Improved support for modern Java environments and cloud platforms

This release maintains API compatibility while modernizing the underlying infrastructure for better performance, 
security, and future extensibility.

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

### Migration from version 4.x to 5.0

**Version 5.0 introduces several significant upgrades that may require code changes in your application:**

#### Breaking Changes

1. **Java Runtime Requirement**: Minimum Java version is now **Java 17** (previously Java 11)
2. **Spring Framework**: Upgraded from Spring 5.x to **Spring 6.0.15**
3. **Apache HTTP Client**: Primary HTTP client upgraded from version 4.5.x to **version 5.5.1**
4. **SLF4J**: Upgraded from version 1.7.x to **version 2.0.17**

#### Migration Steps

**1. Update Java Runtime**
- Ensure your application runs on Java 17 or later
- Update your build tools (Maven/Gradle) to use Java 17

**2. Spring Framework Compatibility**
- If your application uses Spring Framework, upgrade to Spring 6.x or later
- Review Spring 6 migration guide for additional breaking changes: https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x
- Update Spring Boot to version 3.0+ if applicable

**3. Dependency Updates**
Update your `pom.xml` or `build.gradle`:

```xml
<!-- Update GoodData Java SDK -->
<dependency>
    <groupId>com.gooddata</groupId>
    <artifactId>gooddata-java</artifactId>
    <version>5.0.0+api3</version>
</dependency>

<!-- If you use Spring, upgrade to 6.x -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>6.0.15</version>
</dependency>

<!-- Update SLF4J if you use it directly -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.17</version>
</dependency>
```

**4. HTTP Client Configuration**
- The SDK now internally uses Apache HTTP Client 5.x
- If you customize HTTP client settings via `GoodDataSettings`, the API remains compatible
- Custom HTTP interceptors or low-level HTTP client configurations may need updates

**5. Testing**
- Thoroughly test your application after upgrade
- Pay special attention to HTTP client behavior, especially with authentication and connection pooling
- Verify logging functionality works as expected with SLF4J 2.x

#### Compatibility Notes

- **API Compatibility**: The public SDK API remains largely backward compatible
- **Internal Changes**: HTTP client implementation has been modernized but SDK interfaces are unchanged
- **Jakarta EE**: Spring 6 uses Jakarta EE namespaces instead of Java EE (affects annotations if you use them directly)

#### Known Migration Issues and Solutions

**1. ClassNotFoundException for Apache HTTP Client classes**
If you see errors like `ClassNotFoundException: org.apache.http.impl.client.HttpClientBuilder`:
- Remove any direct dependencies on Apache HTTP Client 4.x from your project
- The SDK now uses HTTP Client 5.x internally, which has different package names

**2. Spring Boot Applications**
- Ensure you're using Spring Boot 3.0+ which includes Spring 6
- Update your `@SpringBootApplication` and other Spring annotations if needed
- Check Spring Boot 3.0 migration guide for additional changes

**3. Logging Configuration**
- SLF4J 2.x has some configuration changes compared to 1.7.x
- Update your `logback.xml` or `log4j2.xml` if you use advanced logging features
- Basic logging configuration should work without changes

**4. Build Tools**
```xml
<!-- Maven: Ensure Java 17 in your pom.xml -->
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

```gradle
// Gradle: Ensure Java 17 in your build.gradle
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
```

For additional help, please refer to the [upgrading wiki page](https://github.com/gooddata/gooddata-java/wiki/Upgrading) 
or [create an issue](https://github.com/gooddata/gooddata-java/issues) if you encounter problems during migration.

##### Retry of failed API calls

You can retry your failed requests since *GoodData Java SDK* version *2.34.0*. Turn it on by configuring
[RetrySettings](https://github.com/gooddata/gooddata-java/blob/master/src/main/java/com/gooddata/retry/RetrySettings.java)
and add [Spring retry](https://github.com/spring-projects/spring-retry) to your classpath:

```xml
        <dependency>
            <groupId>org.springframework.retry</groupId>
            <artifactId>spring-retry</artifactId>
            <version>2.0.12</version>
        </dependency>
```

**Note**: Spring Retry 2.0.12 is compatible with Spring 6. If you're upgrading from prior versions of the SDK, 
ensure you also upgrade your Spring Retry dependency to version 2.x for compatibility.

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
