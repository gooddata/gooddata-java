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

### General

```java
GoodData gd = new GoodData("roman@gooddata.com", "Roman1");
gd.logout();
```

### Project API

```java
ProjectService projectService = gd.getProjectService();
Collection<Project> projects = projectService.getProjects();
Project project = projectService.createProject(new Project("my project", "MyAuthToken"));
```

### Project Model API
```java
ModelService modelService = gd.getModelService();
TODO
```

### Metadata API

```java
MetadataService md = gd.getMetadataService();
Metric metric = new Metric("my sum", "SELECT SUM([/gdc/md/PROJECT_ID/obj/ID])", "#,##0");
Metric m = md.create(project, metric);
```

### DataStore API

```java
DataStoreService dataStoreService = gd.getDataStoreService();
dataStoreService.upload("/dir/file.txt", new FileInputStream("file.txt"));
InputStream stream = dataStoreService.download("/dir/file.txt");

```

### Dataset API

```java
final DatasetService datasetService = gd.getDatasetService();
final DatasetManifest manifest = datasetService.getDatasetManifest(project, "datasetId");
datasetService.loadDataset(project, new FileInputStream("/person.csv"), manifest);

```

