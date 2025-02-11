Sample runner repo Jakarta Data 1.0 TCK against Hibernate Data Repositories
========================================
A sample runner for validating the Hibernate Data Repositories implementation against the Jakarta Data 1.0 TCK.

This uses the Hibernate ORM 6.6.6.Final release

## Dependencies:
### Java SE
The Java SE version in use needs to be 17 or higher.

### Jakarta Data API and TCK 1.0.0-RC1
1. download https://www.eclipse.org/downloads/download.php?file=/ee4j/data/jakartaee/staged/eftl/data-tck-1.0.0.zip
1. unizip data-tck-1.0.0.zip
1. cd data-tck-1.0.0/artifacts
2. bash artifact-install.sh 1.0.0

### Build the Jakarta Data Tools Annotation Processor
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
