# Jakarta Data TCK Runner for Hibernate + WildFly

This project is a Jakarta Data TCK runner for Hibernate + WildFly. It is a simple project that demonstrates how to run
the Jakarta Data TCK tests against Hibernate on WildFly.

## Initialize the WildFly server
There is a install-wildfly profile that will configure a local target/wildfly install with patched org.hibernate,
jakarta.data.api and org.jboss.as.jpa modules. To use it, run:

```shell
mvn -Pinstall-wildfly clean process-sources
```

## Run the ValidationTests
Load the ee.jakarta.tck.data.web.validation.ValidationTests class in IntelliJ and run the tests.
