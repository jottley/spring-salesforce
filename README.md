# Spring Salesforce
Spring Salesforce is a Spring extension that provides connection support and API binding for Salesforces
REST API.

## Usage
The library can leverage Spring Security or implement the OAuth2 flow independently of it.

There is a Spring Boot Example app available at https://github.com/jottley/spring-salesforce-quickstart

It gives examples of both use cases.

## Maven
To include in your Maven project, use the following repository and dependency

```xml
    <repositories>
    ...
        <repository>
            <id>spring-salesforce</id>
            <url>https://repo.repsy.io/mvn/jottley/spring-salesforce</url>
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
```

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


## Historical Notes
The project maintains part of the package naming for historical reasons. It should be thought of as deprecated and will eventually be removed. It is not associated with Spring beyond that; it leverages libraries from Spring and is intended to be used by projects that use the Spring framework.
