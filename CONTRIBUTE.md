# GoodData Java SDK - Contribution guide

Below are few rules, recommendations and best practices we try to follow when developing GoodData Java SDK.

## Design

### Structure
* Create packages for API parts containing DTOs and services related to API part.
* Don't create single implementation interfaces for services.

### DTOs
* All DTOs which can be updated should be mutable. Please keep mutable only the fields which are subject of change, the rest should be immutable.
* Create method named `String getUri()` to provide link to self.
* Introduce constants:
```java
public static final String URI = "/gdc/someresource/{resource-id}";
public static final UriTemplate TEMPLATE = new UriTemplate(URI);
```
* Consider the visibility - use _package protected_ when DTO is not intended for SDK user, but is needed in related service.

### Services
* When programming client for some polling return [FutureResult](src/main/java/com/gooddata/FutureResult.java) to enable user asynchronous call.
* Create custom [GoodDataException](src/main/java/com/gooddata/GoodDataException.java) when you feel the case is specific enough.
* Prefer DTOs as arguments instead of Strings or primitives.
* Methods preferred naming:
  * `get*()` when searching form single object (throwing exception when no or multiple objects found)
  * `find*()` when searching for multiple objects (collection of objects)
  * `remove*()` (i.e. `remove(Project project)`) instead od `delete*()` 
* Test class naming:
  * `*Test` unit tests
  * `*IT` integration tests (see AbstractGoodDataIT)

## Best practices
* Test all DTOs using [json-unit](https://github.com/lukas-krecan/JsonUnit).
* Write integration tests for services using [jadler](https://github.com/jadler-mocking/jadler/wiki).
* Everything public should be documented using _javadoc_.
* When you need some utility code, look for handy utilities in used libraries first (e.g. Spring has its StreamUtils, FileCopyUtils, ...). When you decide to create new utility class, use _abstract utility class pattern_.
