# GoodData Java SDK - Contribution guide

Below are few **rules, recommendations and best practices** we try to follow when developing the _GoodData Java SDK_.

## Paperwork
* Every pull request with non-trivial change must be **associated with an issue**.
* Every closed non-rejected pull request and issue must be marked with exactly **one milestone version**.
* Issue must be **properly labeled** (bug/enhancement/backward incompatible/...). 

## Design

### Structure
* **Package names** for DTOs and services should be named in the same manner as REST API.
* Don't create single implementation interfaces for services.

### DTOs
* All DTOs which can be updated should be **mutable**. Please keep mutable only the fields which are subject of change, the rest should be immutable.
* Create method named `String getUri()` to provide **link to self**.
* Introduce **constants**:
```java
public static final String URI = "/gdc/someresource/{resource-id}";
public static final UriTemplate TEMPLATE = new UriTemplate(URI);
```
* Consider the **visibility** - use `package protected` when DTO is not intended for SDK user, but is needed in related service.

### Enums
* Use enums sparingly, because they don't work with REST API changes (eg. new value added on the backend) **never use them when deserializing**.
* Where make sense, use overloaded methods with an enum argument as well as `String` argument.

### Services
* When programming client for some polling return [`FutureResult`](src/main/java/com/gooddata/FutureResult.java)` to enable user asynchronous call.
* Create custom [`GoodDataException`](src/main/java/com/gooddata/GoodDataException.java)` when you feel the case is specific enough.
* Prefer DTOs to `String` or primitive arguments.
* **Method naming**:
  * `get*()` when searching form single object (throw exception when no or multiple objects are found, never return `null`)
  * `find*()` when searching for multiple objects (collection of objects, never return `null`)
  * `remove*()` (i.e. `remove(Project project)`) instead od `delete*()` 
* **Test class naming**:
  * `*Test` unit tests
  * `*IT` integration tests (see [`AbstractGoodDataIT`](src/test/java/com/gooddata/AbstractGoodDataIT.java))

## Best practices
* Test all DTOs using _[JsonUnit`](https://github.com/lukas-krecan/JsonUnit)_. TODO -- this is questionable, because almost nothing is tested by JsonUnit now.
* Write **integration tests** for services using _[Jadler](https://github.com/jadler-mocking/jadler/wiki)_.
* Everything public should be **documented** using _javadoc_.
* When you need some **utility code**, look for handy utilities in used libraries first (e.g. _Spring_ has its `StreamUtils`, `FileCopyUtils`, ...). When you decide to create new utility class, use _abstract utility class pattern_.
