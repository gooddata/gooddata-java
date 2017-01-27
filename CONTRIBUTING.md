# GoodData Java SDK - Contribution guide

Below are few **rules, recommendations and best practices** we try to follow when developing the _GoodData Java SDK_.

## Paperwork
* Every pull request with non-trivial change must be **associated with an issue**.
* Every closed non-rejected pull request and issue must be marked with exactly **one milestone version**.
* Issue must be **properly labeled** (bug/enhancement/backward incompatible/...). 
* Add usage examples of new high level functionality to
[documentation](https://github.com/gooddata/gooddata-java/wiki/Code-Examples).

## Design

### Structure
* **Package names** for DTOs and services should be named in the same manner as REST API.
* Don't create single implementation interfaces for services.

### DTOs
* All DTOs which can be updated should be **mutable**. Please keep mutable only the fields which are subject of change,
the rest should be immutable.
* Create method named `String getUri()` to provide **URI string to self**.
* Introduce **constants**:
```java
public static final String URI = "/gdc/someresource/{resource-id}";
public static final UriTemplate TEMPLATE = new UriTemplate(URI);
```
* Put _Jackson_ annotations on getters rather then on fields.
* Consider the **visibility** - use `package protected` when DTO is not intended for SDK user, but is needed
in related service.
* Test all DTOs using _[JsonUnit](https://github.com/lukas-krecan/JsonUnit)_.
* **Naming**:
  * `Uri` for _URI string_ of some resource
  * `Link` for structure containing at least _category_ (e.g. "self") and _URI string_
    (can contain also others like _title_, _summary_, etc.)

### Enums
* Use enums sparingly, because they don't work with REST API changes (eg. new value added on the backend) **never use
them when deserializing**.
* Where make sense, use overloaded methods with an enum argument as well as `String` argument.

### Services
* When programming client for some polling return [`FutureResult`](src/main/java/com/gooddata/FutureResult.java)
to enable user asynchronous call.
* Create custom [`GoodDataException`](src/main/java/com/gooddata/GoodDataException.java) when you feel the case
is specific enough.
* Prefer DTOs to `String` or primitive arguments.
* **Method naming**:
  * `get*()` when searching form single object (throw exception when no or multiple objects are found,
  never return `null`)
  * `find*()` when searching for multiple objects (collection of objects, never return `null`)
  * `list*()` when listing whole or paged collection of objects (return collection or collection wrapped by DTO)
  * `remove*()` (i.e. `remove(Project project)`) instead od `delete*()`
* Write **integration tests** for services using _[Jadler](https://github.com/jadler-mocking/jadler/wiki)_.
* If it is possible write **acceptance tests** to be run with the real backend.
* Update [documentation](https://github.com/gooddata/gooddata-java/wiki/Code-Examples) with usage examples.

## Best practices
* **Test class naming**:
  * `*Test` unit tests
  * `*IT` integration tests (see [`AbstractGoodDataIT`](src/test/java/com/gooddata/AbstractGoodDataIT.java))
  * `*AT` acceptance tests
* Everything public should be **documented** using _javadoc_.
* When you need some **utility code**, look for handy utilities in used libraries first (e.g. _Spring_ has
its `StreamUtils`, `FileCopyUtils`, ...). When you decide to create new utility class,
use _abstract utility class pattern_.
