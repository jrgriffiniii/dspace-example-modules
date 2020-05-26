# Example Modules for DSpace
[![CircleCI](https://circleci.com/gh/jrgriffiniii/dspace-example-modules.svg?style=svg)](https://circleci.com/gh/jrgriffiniii/dspace-example-modules)

This are example modules for DSpace 5.3 releases, targeting Java 8.

## Getting started

### Run the Docker container for PostgreSQL
```
docker run --name dspace-test-postgres -p 5432:5432 -e POSTGRES_USER=dspace -e POSTGRES_PASSWORD=dspace -d postgres:9.0
```

### Install the Maven dependencies and build the resources
```
mvn dependency:go-offline
mvn process-test-resources
```

## Development

### Listing the Maven build phases

```
mvn buildplan:list
```

### Ensuring that Classes comply with the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

```
mvn com.coveo:fmt-maven-plugin:format
```

### Running an integration test:
```
mvn test -Dmaven.test.skip=false -Dtest=ExampleTaskIntegrationTest
```

### Running an integration test:
```
mvn test -Dmaven.test.skip=false -Dtest=ExampleTaskUnitTest
```

### Debugging tests:
```
mvn test -Dmaven.test.skip=false -Dtest=ExampleTaskUnitTest -Dmaven.surefire.debug
```

This should produce the following output:
```
Listening for transport dt_socket at address: 5005
```

Then, in another terminal, please open the `jdb` using the following:
```
jdb -attach 5005
```

Documentation for the jdb can be found on [the Oracle Java 8 documentation](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/jdb.html). There is also support for this in [Vim](https://gitlab.com/Dica-Developer/vim-jdb), [Sublime](https://github.com/jdebug/JDebug), [Visual Studio Code](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-debug), and [IntelliJ IDEA](https://www.jetbrains.com/help/idea/debugging-your-first-java-application.html).

## Deployment

### Building the WAR:

```
mvn war:war
```

### Deploying the WAR:

```
mvn war:exploded
ant -Dconfig=/dspace/config/dspace.cfg fresh_install
# This may vary, depending upon the version of Tomcat or the servlet engine being used
service tomcat8 restart
```

### Running the Example CLI Task:

```
bash scripts/dsrun.sh -etest@localhost.localdomain
```
