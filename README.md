# Spring Salesforce
Spring Salesforce is a Spring extension that provides connection support and API binding for the Salesforce
REST API.

## Usage
The library can leverage Spring Security or implement the OAuth2 flow independently of it.

There is a Spring Boot Example app available at https://github.com/jottley/spring-salesforce-quickstart

It gives examples of both use cases.

### Authorization code flow
For flows where a user signs in to Salesforce, create a Connected App (or External Client App) with a callback URL, then redirect the user to the authorize URL and exchange the returned code for a token:

```java
SalesforceServiceProvider provider = new SalesforceServiceProvider(clientId, clientSecret); // or (clientId, clientSecret, true) for a sandbox
SalesforceOAuth2Client oauth = provider.getOAuth2Client();

// 1. Send the user here (state should be a random value you verify on return).
//    Keep the PKCE verifier (for example in the user's session) until the callback.
String verifier = Pkce.generateCodeVerifier();
String authorizeUrl = oauth.buildAuthorizeUrl("https://myapp.example.com/callback", "api refresh_token", state,
        Pkce.codeChallenge(verifier));

// 2. In your callback handler, exchange the code Salesforce sends back, with the same verifier
AccessGrant grant = oauth.exchangeForAccess(code, "https://myapp.example.com/callback", verifier);
Salesforce salesforce = provider.getApi(grant.getAccessToken());

// 3. When the access token expires (requires the refresh_token scope)
AccessGrant refreshed = oauth.refreshAccess(grant.getRefreshToken());
```

External Client Apps can require PKCE (it is the default for new apps), and Salesforce recommends it for every app, so pass the code challenge and the verifier as shown. If you send a challenge you must send the verifier, and the reverse. The older `buildAuthorizeUrl(redirectUri, scope, state)` and `exchangeForAccess(code, redirectUri)` still work for apps that do not require PKCE. If your app is a public client, create the client with a `null` secret and none is sent with the authorization-code and refresh requests; whether Salesforce accepts that depends on the app's settings. The library does not store the verifier; keep it between the redirect and the callback, as you do for `state`.

The redirect URI must match the one configured on the app. The `refresh_token` scope must be granted to receive a refresh token.

### Client credentials flow
For server-to-server use with no user interaction, configure an External Client App (or Connected App) with the client credentials flow enabled and a run-as user, then point the provider at your My Domain token URL (the flow is not available on login.salesforce.com):

```java
SalesforceServiceProvider provider = new SalesforceServiceProvider(
        clientId, clientSecret,
        "https://mydomain.my.salesforce.com/services/oauth2/authorize",
        "https://mydomain.my.salesforce.com/services/oauth2/token");

AccessGrant grant = provider.getOAuth2Client().clientCredentialsAccess(null);
Salesforce salesforce = provider.getApi(grant.getAccessToken());
```

No refresh token is returned; request a new token when the current one expires.

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

## API versions
Requests use API version `v68.0` by default (`ApiOperations.DEFAULT_API_VERSION`). Call `salesforce.apiOperations().setVersion("v40.0")` to use another. Versions below `v31.0` are rejected with an `InvalidSalesforceApiVersionException`, and so is anything that is not of the form `v` plus a number, a dot and one digit.

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
 - List the limits of an org
 - Check the current API limit and usage
 - Get a list of Communities (Digital Experience)
 - Get a list of Community (Digital Experience) users

The Chatter user status operations (`getStatus`, `updateStatus`) are deprecated: Salesforce retired that resource in API 25, so they always fail.

## Checking against a real org
`src/test/java/org/springframework/salesforce/live/LiveSalesforceCheck.java` is a manual check that exercises the library against a Salesforce org. It is not part of `mvn test` and skips itself without credentials. It reads by default; writes need `SF_ENABLE_WRITES=true` and only run against a sandbox, scratch org or Developer Edition org.

```bash
export SF_MY_DOMAIN_URL=https://yourdomain.my.salesforce.com
export SF_CLIENT_ID=...
export SF_CLIENT_SECRET=...
mvn -Dtest=LiveSalesforceCheck -Djacoco.skip=true -DfailIfNoSpecifiedTests=false test
```

See the class comment for the optional variables (`SF_API_VERSION`, `SF_ENABLE_WRITES`, `SF_APEX_PATH`, `SF_PAGING_OBJECT`, `SF_ALLOW_WRITES_IN_PRODUCTION`).

## Historical Notes
The project maintains part of the package naming for historical reasons. It should be thought of as deprecated and will eventually be removed. It is not associated with Spring beyond that; it leverages libraries from Spring and is intended to be used by projects that use the Spring framework.
