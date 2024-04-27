Sample runner repo Jakarta Data 1.0 TCK against Hibernate Data Repositories
========================================
A sample runner for validating the Hibernate Data Repositories implementation against the Jakarta Data 1.0 TCK.

Currently incubating in Hibernate 6.6.

This is a work in progress that probably contains extraneous code and dependencies.

## Dependencies:
### Java SE
The Java SE version in use needs to be 17 or higher.

### Hibernate 6.6.0.Alpha1 of hibernate-core and hibernate-jpamodelgen
1. git clone	https://github.com/gavinking/hibernate-orm.git
1. cd hibernate-orm
1. ./gradlew :hibernate-core:publishToMavenLocal :hibernate-jpamodelgen:publishToMavenLocal

### Jakarta Data API and TCK 1.0.0-RC1
1. git clone https://github.com/jakartaee/data.git
1. cd data
1. mvn install

## Jakart Data Tools fork
1. cd tools
2. mvn install

## Build the augmented TCK test jar in this repo
1. cd testjar
1. mvn install

## Running the Standalone TCK
1. cd runner
1. mvn test

## Running the TCK in WildFly
1. cd runner-web
2. mvn -Pinstall-wildfly clean process-sources
3. mvn test
