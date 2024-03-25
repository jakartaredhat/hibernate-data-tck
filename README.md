Sample runner repo Jakarta Data 1.0 TCK against Hibernate Data Repositories
========================================
A sample runner for validating the Hibernate Data Repositories implementation against the Jakarta Data 1.0 TCK.

Currently incubating in Hibernate 6.5.

This is a work in progress that probably contains extraneous code and dependencies.

## Dependencies:
### Java SE
The Java SE version in use needs to be 17 or higher.

### Hibernate 6.5.0-SNAPSHOT of hibernate-core and hibernate-jpamodelgen
1. git clone	https://github.com/hibernate/hibernate-orm.git
1. cd hibernate-orm
1. ./gradlew :hibernate-core:publishToMavenLocal :hibernate-jpamodelgen:publishToMavenLocal

### Jakarta Data API and TCK 1.0.0-SNAPSHOT
1. git clone https://github.com/jakartaee/data.git
1. cd data
1. mvn install

## Running the TCK
1. mvn test