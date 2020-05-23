# DSpace Example Modules

This is an experiment

***## Getting started

```
docker run --name dspace-test-postgres -p 55432:5432 -e POSTGRES_USER=dspace -e POSTGRES_PASSWORD=dspace -d postgres
```

```
mvn dependency:go-offline
mvn verify
```

Debugging:
```
mvn -Dmaven.surefire.debug test
```
