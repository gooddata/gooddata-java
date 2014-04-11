# GoodData Java SDK

[![Build Status](https://travis-ci.org/martiner/gooddata-java.png?branch=master)](https://travis-ci.org/martiner/gooddata-java)

## Usage

```xml
<dependency>
    <groupId>com.gooddata</groupId>
    <artifactId>gooddata-java</artifactId>
    <version>${gooddata-java.version</version>
</dependency>
```

```java
GoodData gd = new GoodData("roman@gooddata.com", "Roman1");

ProjectService projectService = gd.getProjectService();
Collection<Project> projects = projectService.getProjects();

Project project = projectService.createProject(new Project("my project", "MyAuthToken"));

MetadataService md = gd.getMetadataService();
Metric metric = new Metric("my sum", "SELECT SUM([/gdc/md/PROJECT_ID/obj/ID])", "#,##0");
Metric m = md.create(project, metric);

gd.logout();
```
