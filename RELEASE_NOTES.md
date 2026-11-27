# Release notes (draft)

**Version:** 3.0.0 (the current version is `3.0.0-SNAPSHOT`).

## Upgrade notes: please read first

**1. The default Salesforce API version is now `v68.0`** (it was `v37.0`).
Any application that never calls `setVersion` now sends every request to `/services/data/v68.0/...`. To stay on an older version, call `apiOperations().setVersion("v40.0")` or similar.

**2. API versions below 31 are rejected.**
`setVersion(...)` and `getServices(version)` now throw `InvalidSalesforceApiVersionException` for anything older than `v31.0`:
`v30.0 is below the minimum supported Salesforce Api version (v31.0).`
The limit is exposed as `ApiOperations.MINIMUM_API_VERSION` (`31`). Version strings are also checked strictly now: `v` plus one to three digits, a dot, and one digit, with nothing else around them. `getServices(version)` used to accept any string, including null.

**3. Error exceptions now follow the HTTP status.**
Before, the exception depended on the response body being a JSON array, so gateway errors lost their status. Now:
- A **503 or 429 with any body** (empty, HTML, anything) is a `RateLimitExceededException`. Before, it was a raw `IOException` or an `UncategorizedApiException`.
- A **500 with a non-JSON body** is an `InternalServerErrorException`.
- A **404 with a JSON object body** (not an array) is a `ResourceNotFoundException`.
- **Messages are no longer null.** If Salesforce gave none, the message is the status, for example `404 Not Found`.
- A **non-standard status** (such as 599 from a proxy) is an `UncategorizedApiException`. It used to be a `NullPointerException`.
- A **403 with no `errorCode`** is an `InsufficientPermissionException`. It used to be a `NullPointerException`.
- **OAuth token errors:** a 400 with an empty, non-JSON or array body is an `OperationNotPermittedException`, with a message taken from the description, then the error code, then a generic note. An unrecognized OAuth error value is now an `OperationNotPermittedException` too, where it used to fall through to Spring's `HttpClientErrorException`.

**4. Invalid path values are rejected before any request.**
A null, blank, `.` or `..` sObject name, record id, field name, Chatter user id, community id or query locator now throws `IllegalArgumentException`.

**5. `SimpleCachingSalesforceFactory` re-logs-in when credentials change.**
It used to keep the first login for a username forever, even if you passed a new password or token. It now replaces the entry when the password or security token changes, and it never caches a failed login. Only a digest of the credentials is kept. New methods `evict(username)` and `clear()` force a fresh login.

**6. Removed:**
- `org.springframework.salesforce.api.impl.HeaderAddingInterceptor` was unused. The Chatter header it once supplied is set per call.
- `AbstractSalesForceOperations.BASE_URL`, a protected static that held a hard-coded `na7` URL, was removed.

**7. Models that were silently wrong are now correct.** If you read these flags, your results change:
- `SObjectSummary` and `SObjectDetail`: `isLayoutable()`, `isDeprecatedAndHidden()` and `isRetrieveable()` were **always false**, because of misspelled JSON names. They now return Salesforce's values. If you serialize those models, the JSON names are now `layoutable`, `deprecatedAndHidden` and `retrieveable`.
- `CommunityUser.isInThisCommunity()` was always false. It is now populated, and both `isInThisCommunity` and `inThisCommunity` are accepted.

**8. `QueryOperations.queryAll(String)` now includes deleted and archived records.**
Despite its name it used the `query` resource and excluded them. It now uses the `queryAll` resource, so a query can return records from the Recycle Bin. For the old result, call the new `queryAll(String, boolean)` with `false`, which pages through the `query` resource and excludes them. `query(String, true)` is unchanged. The method also no longer appends later pages to the first page's own list.

## Deprecated

- `ChatterOperations.getStatus(...)` and `updateStatus(...)`, and `SalesforceProfileMapper.updateStatus`. Salesforce retired the user status resource in API 25, below this library's minimum of 31, so these always fail with `ResourceNotFoundException`. There is no replacement; they will be removed in a future major version.
- `MissingAuthorizationException` and `InvalidIDException`. The library effectively never throws them: operations objects always have their `Salesforce` instance, and invalid record ids are rejected with `IllegalArgumentException`. Neither signals a missing token; catch `InvalidAuthorizationException` for that. They will be removed in a future major version.

## Bug fixes

- **Queries containing `+` or `&` were altered.** `Email = 'a+b@x.com'` reached Salesforce as `a b@x.com`, and `Name = 'AT&T'` was cut at the `&`. Both are now sent intact, for SOQL and SOSL. Ordinary queries produce the same URL as before.
- **`getRow` and `getCommunityUsers` built URLs from raw text,** so a value like `../../limits?x=1` could reach other endpoints. Values are now encoded.
- **A response header with `per-app-api-usage` caused every call to fail** with `NumberFormatException`, after the request had already succeeded server-side. The usage header is now parsed safely and a malformed one is ignored. Used and max are updated together.
- **`getBlob` threw a `NullPointerException`** on an empty or 204 response. It now returns an empty stream, never null.
- **`search` failed on API version 37 and later.** From v37.0 Salesforce returns SOSL results as `{"searchRecords": [...]}`; versions 31 to 36 return a bare array. The library only understood the array, so it threw an `IllegalArgumentException` on every newer version, including the old default of v37.0. Both shapes are now accepted, and a search with no hits (or an empty response) returns an empty list instead of null. Found and verified by running against a live org at every version from 31 to 68.
- **A query that selected a compound field failed.** `SELECT BillingAddress FROM Account` (or any address or geolocation field) threw a `NullPointerException` inside the response deserializer, surfacing as a `RestClientException`, because the nested object has no `attributes` entry. The compound field is now returned in the record's attributes as a `Map<String, Object>` with the keys Salesforce sends (`city`, `street`, and so on). Found by running against a live org.
- **`getSObjects`, `getCommunities` and `getCommunityUsers` returned `null`** when the response had no `sobjects`, `communities` or `users` entry, though they returned an empty list for an empty body. They now return an empty list in both cases. Code that checked these results for `null` should check `isEmpty()` instead.
- **`BaseSalesforceFactory.create` failed obscurely on a bad token response.** An empty body caused a `NullPointerException`, and a body with no `access_token` silently produced a binding with no token that failed on first use. Both now throw `InvalidAuthorizationException` ("The token response contained no access token."). The login also now honors the `getAuthorizeUrl()` fallback, so `setAuthorizeUrl(null)` restores the production token URL instead of failing.
- **`SalesforceOAuth2Client.buildAuthorizeUrl` produced broken URLs for values with a space, `&` or `#`.** A multi-scope value such as `api refresh_token` was put into the URL as is. Such characters are now percent-encoded. Values you encoded yourself are left alone (`%20` and `+` pass through unchanged), so existing code that pre-encodes keeps working. A literal `%` or `+` can't be sent through this method.
- **`SalesforceUserDetails.getPhoto()` and `getUsername()` were always `null`.** The userinfo endpoint sends the photo as `photos` (`picture` and `thumbnail`) and the username as `preferred_username`, neither of which was mapped. `getPhoto()` now returns the large (`picture`) and small (`thumbnail`) URLs, and `getUsername()` returns the preferred username, the same value as `getPreferredUsername()`.
- **`QueryTemplate.nextPage`** built a `null/services/...` URL when no instance URL was set. It now goes to the same host as the first page.

## New

- `LimitsResults` keeps limits it doesn't model, so limits Salesforce adds later appear in `getAdditionalProperties()`. `getAdditionalLimit(name)` returns one as an `Optional<LimitResult>`, empty if it isn't shaped like a limit.
- `ApiOperations.MINIMUM_API_VERSION` and `InvalidSalesforceApiVersionException(String version, int minimum)`.
- `QueryOperations.queryAll(String query, boolean includeDeletedItems)`: pages through every result like `queryAll(String)`, and `false` excludes deleted and archived records. It is a new abstract method, so a class that implements `QueryOperations` itself must add it.
- **PKCE support (RFC 7636, `S256`)**, which External Client Apps can require (the default for new apps). New `Pkce.generateCodeVerifier()` and `Pkce.codeChallenge(String)`, `SalesforceOAuth2Client.buildAuthorizeUrl(redirectUri, scope, state, codeChallenge)` and `exchangeForAccess(code, redirectUri, codeVerifier)`. The existing three- and two-argument methods are unchanged. A blank challenge or verifier is treated as none. The library does not store the verifier: keep it between the redirect and the callback.
- **Public clients.** A `null` or blank client secret is no longer sent with the authorization-code and refresh-token requests. Client credentials requests still send it, because that flow needs one.

## Build and internal

- JaCoCo now runs on `mvn test`, and the **build fails below 85% instruction coverage**. This also applies to `mvn package` and the publish workflow (`mvn deploy`). The threshold is the `jacoco.minimum.instruction.coverage` property. CI uploads the coverage report and writes a summary.
- Every class and member, including private ones, now has Javadoc, and the build enforces it: `maven-javadoc-plugin` runs with `doclint=all` and `failOnWarnings`, so `mvn package` (and CI and the publish workflow) fails if new code is undocumented or has invalid Javadoc. A second, check-only pass includes private members; the published Javadoc still lists only public and protected members.
- Mockito moved from 2.11.0 to 5.24.0, test scope only.
- Tests grew from 47 to 261 `@Test` methods.
- A manual live check (`src/test/java/.../live/LiveSalesforceCheck.java`) runs the library against a real org. It is not part of `mvn test` and skips itself without credentials. See the README.
- Dead code was removed from the query, search and API templates, the error handlers and the cache.

## Known limitations (not changed in this release)

- **A 503 is always reported as `RateLimitExceededException`,** though it can also mean an outage. Chatter REST resources do return 503 when their hourly limit is exceeded.
- With no instance URL set, requests still default to `https://na1.salesforce.com`. Failing fast instead would be a breaking change.
- Building SOQL from untrusted input is still the caller's responsibility. The library only guarantees the text arrives unaltered.
- `SalesforceTemplate` holds mutable per-instance state (instance URL and API version). Assume single-threaded use per instance.
- `getBlob` reads the whole blob into memory. Use `downloadBlob` for large content.
