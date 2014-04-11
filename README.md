# GoodData Java SDK

[![Build Status](https://travis-ci.org/martiner/gooddata-java.png?branch=master)](https://travis-ci.org/martiner/gooddata-java)

## Usage

```xml
<dependency>
    <groupId>com.gooddata</groupId>
    <artifactId>gooddata-java</artifactId>
    <version>${gooddata-java.version}</version>
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
String projectModelData = ".....";
ModelDiff projectModelDiff = modelService.getProjectModelDiff(project, projectModelData);
modelService.updateProjectModel(project, projectModelDiff);
```

### Metadata API

```java
MetadataService md = gd.getMetadataService();
Metric metric = new Metric("my sum", "SELECT SUM([/gdc/md/PROJECT_ID/obj/ID])", "#,##0");
Metric m = md.createObj(project, metric);

ReportDefinition definition = GridReportDefinition.create(
        "my report",
        asList("metricGroup"),
        asList(new AttributeItem("/gdc/md/vra1wg1m6r0gzl8i8r8y3h1bk0kkzkpo/obj/29")),
        asList(new Item("/gdc/md/vra1wg1m6r0gzl8i8r8y3h1bk0kkzkpo/obj/41"))
);
md.createMd(project, definition);
```

### DataStore API

```java
DataStoreService dataStoreService = gd.getDataStoreService();
dataStoreService.upload("/dir/file.txt", new FileInputStream("file.txt"));
InputStream stream = dataStoreService.download("/dir/file.txt");
dataStoreService.delete("/dir/file.txt");

```

### Dataset API

```java
final DatasetService datasetService = gd.getDatasetService();
final DatasetManifest manifest = datasetService.getDatasetManifest(project, "datasetId");
datasetService.loadDataset(project, new FileInputStream("/person.csv"), manifest);

```

