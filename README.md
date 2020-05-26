# DSpace Example Modules

This is an experiment

***## Getting started

### Run the Docker container for PostgreSQL
```
docker run --name dspace-test-postgres -p 55432:5432 -e POSTGRES_USER=dspace -e POSTGRES_PASSWORD=dspace -d postgres:9.0
```

### Install the Maven dependencies and build the resources
```
mvn dependency:go-offline
mvn process-test-resources
```

## Development

### Running an integration test:
```
mvn test -Dmaven.test.skip=false -Dtest=ExampleTaskIntegrationTest
```

### Running an integration test:
```
mvn test -Dmaven.test.skip=false -Dtest=ExampleTaskUnitTest
```

```
Debugging:
```
mvn -Dmaven.surefire.debug test
```
