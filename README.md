# Spring Salesforce [![Maven Package](https://github.com/jottley/spring-salesforce/actions/workflows/maven-publish.yml/badge.svg?event=release)](https://github.com/jottley/spring-salesforce/actions/workflows/maven-publish.yml)

Spring Salesforce is a Spring extension that provides connection support and API binding for the Salesforce
REST API.

To check out the project and build from source, do the following:

    git clone git://github.com/jottley/spring-salesforce.git
    cd spring-salesforce
    mvn clean install
    
## Maven
To include in your Maven project, use the following repository and dependency

    <repositories>
    ...
        <repository>
            <id>spring-salesforce</id>
            <url>https://maven.pkg.github.com/jottley/spring-social-salesforce</url>
        </repository>
    ...
    </repositories>
    
    <dependencies>
    ...
        <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-salesforce</artifactId>
          <version>2.0.0</version>
        </dependency>
    ...
    </dependencies>
    
## Quickstart
There is a Spring Boot Example app available at https://github.com/jottley/spring-salesforce-quickstart

## Supported Operations
 - Retrieve all available API versions
 - Retrieve services supported by a specific version of the API
 - Retrieve the list of sObjects
 - Retrieve summary metadata of a sObject
 - Retrieve full metadata of a sObject
 - Retrieve a row from a sObject
 - Retrieve a blob from a row in a sObject
 - Create a new sObject
 - Update an existing sObject
 - Retrieve recent changes feed
 - Execute a SOSL search and retrieve the results (with paging or all)
 - Run a SOQL query and retrieve the results (with paging or all)
   - query results can optionally include deleted records
 - Retrieve user profile
 - Retrieve user status
 - Update user status
 - List the limits of an org
 - Check the current API limit and usage
 - Get a list of Communities (Digital Experience)
 - Get a list of Community (Digital Experience) users 
