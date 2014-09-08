# GoodData Java SDK

[![Build Status](https://travis-ci.org/martiner/gooddata-java.png?branch=master)](https://travis-ci.org/martiner/gooddata-java)

The *GoodData Java SDK* encapsulates the REST API provided by the [GoodData](http://www.gooddata.com) platform.
The first version was implemented during the [All Data Hackathon](http://hackathon.gooddata.com) April 10 - 11 2014
and currently the SDK is transitioned to be an official GoodData project.

## Usage

The *GoodData Java SDK* is available in Maven Central Repository, to use it from Maven add to `pom.xml`:

```xml
<dependency>
    <groupId>cz.geek</groupId>
    <artifactId>gooddata-java</artifactId>
    <version>0.9.0</version>
</dependency>
```

The *GoodData Java SDK* uses the [GoodData HTTP client](https://github.com/gooddata/gooddata-http-client) (version 0.8.2 or later)
and the *Apache HTTP Client* (version 4.3 or later).

### General

```java
GoodData gd = new GoodData("roman@gooddata.com", "Roman1");
gd.logout();
```

### Project API

List projects, create a project,...
```java
ProjectService projectService = gd.getProjectService();
Collection<Project> projects = projectService.getProjects();
Project project = projectService.createProject(new Project("my project", "MyToken")).get();
```

### Project Model API

Create and update the project model, execute MAQL DDL,...

```java
ModelService modelService = gd.getModelService();
ModelDiff diff = modelService.getProjectModelDiff(project, new InputStreamReader(getClass().getResourceAsStream("/model.json"))).get();
modelService.updateProjectModel(project, diff);

modelService.updateProjectModel(project, "MAQL DDL EXPRESSION");
```

### Metadata API

Query, create and update project metadata - attributes, facts, metrics, reports,...

```java
MetadataService md = gd.getMetadataService();

String factUri = md.getObjUri(project, Fact.class, Restriction.title("myfact"));
Metric m = new Metric("my sum", "SELECT SUM([" + factUri + "])", "#,##0");
Metric metric = md.createObj(project, m);
Attribute attr = md.getObj(project, Attribute.class, Restriction.title("myattr"));

Metric m = md.createObj(project, new Metric("My Sum", "SELECT SUM([" + factUri + "])", "#,##0"));

ReportDefinition definition = GridReportDefinitionContent.create(
        "my report",
        asList("metricGroup"),
        asList(new AttributeInGrid("/gdc/md/PROJECT_ID/obj/ATTR_DISPLAY_FORM_ID")),
        asList(new GridElement(m.getUri(), "My Sum"))
);
definition = md.createObj(project, definition);
Report report = md.createObj(project, new Report(definition.getTitle(), definition.getUri(), null));
```

### Dataset API

Upload data to datasets,..

```java
DatasetService datasetService = gd.getDatasetService();
datasetService.loadDataset(project, "datasetId", new FileInputStream("data.csv")).get();
```

### Report API

Execute and export reports.

```java
ReportService reportService = gd.getReportService();
String imgUri = reportService.exportReport(reportDef, ReportExportFormat.PNG);
```

### DataStore API

Manage files on the data store (currently backed by WebDAV) - user staging area.

```java
DataStoreService dataStoreService = gd.getDataStoreService();
dataStoreService.upload("/dir/file.txt", new FileInputStream("file.txt"));
InputStream stream = dataStoreService.download("/dir/file.txt");
dataStoreService.delete("/dir/file.txt");
```

### Warehouse API
Manage warehouses - create, update, list and delete.
```java
WarehouseService warehouseService = gd.getWarehouseService();
Warehouse warehouse = warehouseService.createWarehouse(new Warehouse("title", "authToken", "description"));
String jdbc = warehouse.getJdbcConnectionString();

Collection<Warehouse> warehouseList = warehouseService.listInstances();
warehouseService.removeWarehouse(warehouse);
```

### Dataload processes API
Manage dataload processes - create, update, list, delete, and process executions - execute, get logs,...
```java
ProcessService processService = gd.getProcessService();
Process process = processService.createProcess(project, new Process("name", "GRAPH"), new File("path/to/processdatadir")).get();

ProcessExecutionDetail executionDetail = processService.executeProcess(new ProcessExecution(process, "myGraph.grf")).get();
processService.getExecutionLog(executionDetail, new FileOutputStream("file/where/the/log/willbewritten");
```
