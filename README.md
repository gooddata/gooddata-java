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
    <version>0.15.0</version>
</dependency>
```

See [releases page](https://github.com/martiner/gooddata-java/releases) for information about versions and notable changes.

### Dependencies

The *GoodData Java SDK* uses:
* the [GoodData HTTP client](https://github.com/gooddata/gooddata-http-client) (version 0.8.2 or later)
* the *Apache HTTP Client* (version 4.3 or later, for white-labeled domains at least version 4.3.2 is required)
* the *Spring Framework* (version 3.x)
* the *Jackson JSON Processor* (version 1.9)

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

Validate project
```java
Set<ProjectValidationType> types = projectService.getAvailableProjectValidationTypes(project);
ProjectValidationResults results = projectService.validateProject(project, types).get();
```

List project users
```java
ProjectService projectService = gd.getProjectService();
List<User> users = new ArrayList<>();
List<User> page;
while (!(page = projectService.listUsers(project, new PageRequest(users.size(), 100))).isEmpty()) {
    users.addAll(page);
}
```

List project user roles
```java
ProjectService projectService = gd.getProjectService();
Set<Role> roles = projectService.getRoles(project);
```

### Project Model API

Create and update the project model, execute MAQL DDL,...

```java
ModelService modelService = gd.getModelService();
ModelDiff diff = modelService.getProjectModelDiff(project,
    new InputStreamReader(getClass().getResourceAsStream("/person.json"))).get();
modelService.updateProjectModel(project, diff).get();

modelService.updateProjectModel(project, "MAQL DDL EXPRESSION").get();
```

### Metadata API

Query, create, update and remove project metadata - attributes, facts, metrics, reports,...

```java
MetadataService md = gd.getMetadataService();

String fact = md.getObjUri(project, Fact.class, identifier("fact.person.shoesize"));
Metric m = md.createObj(project, new Metric("Avg shoe size", "SELECT AVG([" + fact + "])", "#,##0"));

Attribute attr = md.getObj(project, Attribute.class, identifier("attr.person.department"));

ReportDefinition definition = GridReportDefinitionContent.create(
        "Department avg shoe size",
        asList("metricGroup"),
        asList(new AttributeInGrid(attr.getDefaultDisplayForm().getUri())),
        asList(new GridElement(m.getUri(), "Avg shoe size"))
);
definition = md.createObj(project, definition);
Report report = md.createObj(project, new Report(definition.getTitle(), definition));

md.removeObj(report)
```

Create and retrieve scheduled mails on reports and dashboards:

```java
Project project = ...
ReportDefinition reportDefinition = ...

MetadataService md = gd.getMetadataService();
ScheduledMail scheduledMail = md.createObj(
    project,
    (new ScheduledMail("Scheduled Mail Title", "Scheduled Mail Summary"))
        .setRecurrency("0:0:0:1*12:0:0")
        .setStartDate(new LocalDate(2012, 6, 5))
        .setTimeZone("America/Los_Angeles")
        .addToAddress("user_in_project@example.com")
        .addBccAddress("another_user_in_project@example.com")
        .setSubject("Mail subject")
        .setBody("Mail body")
        .addReportAttachment(reportDefinition,
                             Collections.singletonMap("pageOrientation", "landscape"),
                             pdf, xls)
);

Collection<Entry> result = md.find(project, ScheduledMail.class);
for (Entry e : result) {
    ScheduledMail schedule = md.getObjByUri(e.getLink(), ScheduledMail.class);
}
```

### Dataset API

Upload data to datasets,..

```java
DatasetService datasetService = gd.getDatasetService();
datasetService.loadDataset(project, "datasetId", new FileInputStream("data.csv")).get();
```
Upload data to datasets using batch upload ,..

```java
DatasetService datasetService = gd.getDatasetService();
final DatasetManifest personManifest = datasetService.getDatasetManifest(project, "dataset.person");
personManifest.setSource(getClass().getResourceAsStream("/person.csv"));

final DatasetManifest cityManifest = datasetService.getDatasetManifest(project, "dataset.city");
cityManifest.setSource(getClass().getResourceAsStream("/city.csv"));

datasetService.loadDatasets(project, personManifest, cityManifest).get();
```

Update data in dataset
```java
DatasetService datasetService = gd.getDatasetService();
datasetService.updateProjectData(project, "DELETE FROM {attr.person.name} WHERE {label.person.name} = \"not exists\";");
```

### Report API

Execute and export reports.

```java
ReportService reportService = gd.getReportService();
reportService.exportReport(definition, PNG, new FileOutputStream("report.png")).get();
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
Warehouse warehouse = warehouseService.createWarehouse(new Warehouse("title", "authToken", "description")).get();
String jdbc = warehouse.getJdbcConnectionString();

Collection<Warehouse> warehouseList = warehouseService.listWarehouses();
warehouseService.removeWarehouse(warehouse);
```

### Dataload processes API
Manage dataload processes - create, update, list, delete, and process executions - execute, get logs, schedules, ...
```java
ProcessService processService = gd.getProcessService();
DataloadProcess process = processService.createProcess(project, new DataloadProcess("name", "GRAPH"), new File("path/to/processdatadir"));

ProcessExecutionDetail executionDetail = processService.executeProcess(new ProcessExecution(process, "myGraph.grf")).get();
processService.getExecutionLog(executionDetail, new FileOutputStream("file/where/the/log/willbewritten"));

processService.createSchedule(project, new Schedule(process, "myGraph.grf", "0 0 * * *"));
```

##Contribute
Missing functionality? Found a BUG? 

Please create an [issue](https://github.com/martiner/gooddata-java/issues) or simply [contribute your code](CONTRIBUTING.md).
