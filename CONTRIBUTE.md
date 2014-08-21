# GoodData Java SDK - Contribution guide

Below are few rules, recommendations and best practices, we try to follow when developing GoodData Java SDK.

## Design

### Structure
* Create packages for API parts containing dtos and services related to API part.
* Don't create single implementation interfaces for services.

### DTOs
* All DTOs which can be updated should be mutable. Please keep mutable only the fields which are subject of change, the rest should be immutable.
* Create method named `String getUri()` to provide link to self.
* introduce constants
```java
public static final String URI = "/gdc/someresource/{resource-id}";
public static final UriTemplate TEMPLATE = new UriTemplate(URI);
```
* Consider the visibility - use _package protected_ when dto is not intended for SDK user use, but is needed in related service.

### Services
* When programming client for some polling return [FutureResult](src/main/java/com/gooddata/FutureResult.java) to enable user asynchronous call.
* Create custom [GoodDataException](src/main/java/com/gooddata/GoodDataException.java) when you feel the case is specific enough.
* Prefer dtos as arguments instead of Strings or primitives.
* methods prefered naming:
  * `get*()` (i.e. `get(Project project)` or `getByUri(String uri)`) instead of `find*()`
  * `remove*()` (i.e. `remove(Project project)`) instead od `delete*()` 

## Best practices
* test all DTOs using [json-unit](https://github.com/lukas-krecan/JsonUnit)
* write integration tests for services using [jadler](https://github.com/jadler-mocking/jadler/wiki)
* javadoc everything public
* when you need some utility code, look for handy utilities in used libraries first (i.e. spring has its StreamUtils, FileCopyUtils, ...), then when you decide to create new utility class, do it using _abstract utility class pattern_
