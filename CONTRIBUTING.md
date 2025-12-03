# GoodData Java SDK - Contribution guide

Below are few **rules, recommendations and best practices** we try to follow when developing the _GoodData Java SDK_.

## Paperwork

### Committer

* The **commit message**:
    * must be written in the **imperative mood**
    * must clearly **explain rationale** behind this change (describe _why_ you are doing the change rather than _what_
      you are changing)
* The **pull request**:
    * with non-trivial, non-dependencies change must be *
      *[associated with an issue](https://help.github.com/articles/closing-issues-via-commit-messages/)**
* Add usage examples of new high level functionality to
  [documentation](https://github.com/gooddata/gooddata-java/wiki/Code-Examples).

### Reviewer

Ensure pull request and issues are

* **properly labeled** (trivial/bug/enhancement/backward incompatible/...)
* marked with exactly one **milestone version**

## Design

### Structure

* **Package names** for DTOs and services should be named in the same manner as REST API.
* Don't create single implementation interfaces for services.

### DTOs

* All DTOs which can be updated should be **mutable**. Please keep mutable only the fields which are subject of change,
  the rest should be immutable.
* Create method named `String getUri()` to provide **URI string to self**.
* Introduce **constant** with URI:
    * ```java
    public static final String URI = "/gdc/someresource/{resource-id}";
    ```
    * If you need also constants with `UriTemplate`, do not put them into DTOs not to drag Spring dependency into model
      module.
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
* In addition to unit tests, write also **integration tests** and **acceptance tests** if possible. See "What to test
  where" in "Best practices" below.
* Update [documentation](https://github.com/gooddata/gooddata-java/wiki/Code-Examples) with usage examples.

## Best practices

* **What to test where**:
    * `*Test` = unit tests
        * focus on verifying bussiness logic, corner cases, various input combinations, etc.
        * avoid service tests using mocked `RestTemplate` - use integration tests with mocked API responses instead
    * `*IT` = integration tests
        * focus on verifying all possible outcomes of API calls
        * see common ancestor [`AbstractGoodDataIT`](src/test/java/com/gooddata/AbstractGoodDataIT.java) setting
          up [Jadler](https://github.com/jadler-mocking/jadler/wiki) for API mocking
    * `*AT` = acceptance tests
        * focus on verifying the happy path against the real backend (so we're sure mocks in ITs are correct)
        * see common ancestor [`AbstractGoodDataAT`](src/test/java/com/gooddata/AbstractGoodDataAT.java) setting up
          GoodData endpoint based on passed environment variables
* Everything public should be **documented** using _javadoc_.
* When you need some **utility code**, look for handy utilities in used libraries first (e.g. _Spring_ has
  its `StreamUtils`, `FileCopyUtils`, ...). When you decide to create new utility class,
  use _abstract utility class pattern_.

## Release candidates

We're using branches like "3.0.0-RC" for development of new major releases containing backward incompatible changes.
Such RC branch is forked from master branch and has to be kept in sync later, when something is committed into master so
RC can be merged to master when new major version is ready.

Do not use **cherry-pick**s to keep branches in sync! Always use **merge** from master to RC.
