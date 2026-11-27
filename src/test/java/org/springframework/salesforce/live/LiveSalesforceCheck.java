/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-social-salesforce
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.salesforce.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.salesforce.api.ApiException;
import org.springframework.salesforce.api.ApiOperations;
import org.springframework.salesforce.api.ApiVersion;
import org.springframework.salesforce.api.Community;
import org.springframework.salesforce.api.CommunityUser;
import org.springframework.salesforce.api.Field;
import org.springframework.salesforce.api.InvalidSalesforceApiVersionException;
import org.springframework.salesforce.api.LimitResult;
import org.springframework.salesforce.api.LimitsResults;
import org.springframework.salesforce.api.PickListEntry;
import org.springframework.salesforce.api.QueryResult;
import org.springframework.salesforce.api.RecordTypeInfo;
import org.springframework.salesforce.api.Relationship;
import org.springframework.salesforce.api.ResourceNotFoundException;
import org.springframework.salesforce.api.SObjectDetail;
import org.springframework.salesforce.api.SObjectSummary;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.api.SalesforceProfile;
import org.springframework.salesforce.api.impl.SalesforceTemplate;
import org.springframework.salesforce.connect.AccessGrant;
import org.springframework.salesforce.connect.SalesforceOAuth2Client;
import org.springframework.salesforce.connect.SalesforceServiceProvider;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * A manual check of the library against a real Salesforce org. It is NOT part of the normal build:
 * the class name does not match Surefire's test patterns, so {@code mvn test} and CI never run it,
 * and it skips itself unless credentials are present in the environment.
 *
 * <h2>Running</h2>
 *
 * <pre>
 * export SF_MY_DOMAIN_URL=https://yourdomain.my.salesforce.com
 * export SF_CLIENT_ID=...
 * export SF_CLIENT_SECRET=...
 * mvn -Dtest=LiveSalesforceCheck -Djacoco.skip=true -DfailIfNoSpecifiedTests=false test
 * </pre>
 *
 * {@code -Djacoco.skip=true} is needed because a single test class cannot meet the coverage gate.
 *
 * <h2>Environment variables</h2>
 *
 * <ul>
 *   <li>{@code SF_MY_DOMAIN_URL}, {@code SF_CLIENT_ID}, {@code SF_CLIENT_SECRET}: authenticate with
 *       the OAuth client credentials flow. The connected app needs that flow enabled and a "run as"
 *       user.
 *   <li>Or {@code SF_ACCESS_TOKEN} and {@code SF_INSTANCE_URL}: use an existing token (for example
 *       from {@code sf org display}) and skip the OAuth step.
 *   <li>{@code SF_SCOPE} (optional): scope to request with client credentials.
 *   <li>{@code SF_API_VERSION} (optional): run against this version instead of the library default.
 *   <li>{@code SF_ENABLE_WRITES=true} (optional): also create, update and delete a test Account and
 *       a ContentVersion. Without it the check only reads. Use a disposable org.
 *   <li>Writes only run against a sandbox, scratch org or Developer Edition org; the check asks the
 *       org ({@code Organization.IsSandbox} and {@code OrganizationType}) and skips writes for
 *       anything else, or if it cannot tell. Set {@code SF_ALLOW_WRITES_IN_PRODUCTION=true} to
 *       override that.
 *   <li>{@code SF_APEX_PATH} (optional): an Apex REST path such as {@code /MyService/} to GET.
 *   <li>{@code SF_PAGING_OBJECT} (optional): an sObject with more than 2000 rows, to exercise
 *       paging (defaults to ObjectPermissions, then common objects).
 * </ul>
 *
 * Tokens, secrets and the org's host name are never printed; the report masks them.
 *
 * @author Jared Ottley
 */
public class LiveSalesforceCheck {

  private interface Check {
    /** Runs the check and returns a short detail line. Throwing means the check failed. */
    String run() throws Exception;
  }

  private enum Status {
    PASS,
    FAIL,
    WARN,
    SKIP,
    INFO
  }

  private record Result(Status status, String name, String detail) {}

  private static final Pattern API_USAGE = Pattern.compile("api-usage=(\\d+)/(\\d+)");

  private final ObjectMapper json = new ObjectMapper();
  private final List<Result> results = new ArrayList<>();
  private final Set<String> secrets = new LinkedHashSet<>();
  private final Set<String> hosts = new LinkedHashSet<>();
  private final Deque<Map.Entry<String, Runnable>> cleanup = new ArrayDeque<>();

  private Salesforce api;
  private RestTemplate raw;
  private String instanceUrl;
  private String gatewayUrl;
  private boolean authenticated;

  /** The access token the run itself used; never printed. */
  private String runToken;

  private String firstAccountId;
  private String firstAccountName;

  // ------------------------------------------------------------------ the run

  @Test
  public void run() {
    Assume.assumeTrue(
        "Set SF_MY_DOMAIN_URL + SF_CLIENT_ID + SF_CLIENT_SECRET (or SF_ACCESS_TOKEN + SF_INSTANCE_URL) "
            + "to run the live check; see the class comment.",
        haveCredentials());
    try {
      authenticate();
      versionAndFloor();
      metadata();
      dataAccess();
      searchAcrossVersions();
      limitsAndRecent();
      optionalFeatures();
      writes();
    } finally {
      runCleanup();
      // After the cleanup, because the token it revokes may be the one the run used
      revocation();
      printReport();
    }
    long failed = results.stream().filter(r -> r.status() == Status.FAIL).count();
    if (failed > 0) {
      fail(failed + " live check(s) failed; see the report above.");
    }
  }

  // ------------------------------------------------------------------ steps

  private void authenticate() {
    core(
        "authenticate",
        () -> {
          String token = env("SF_ACCESS_TOKEN");
          String instance = env("SF_INSTANCE_URL");
          String myDomain = trimSlash(env("SF_MY_DOMAIN_URL"));
          String how;
          if (token != null) {
            secrets.add(token);
            how = "supplied access token";
          } else {
            String clientId = env("SF_CLIENT_ID");
            String clientSecret = env("SF_CLIENT_SECRET");
            secrets.add(clientId);
            secrets.add(clientSecret);
            rememberHost(myDomain);
            SalesforceServiceProvider provider =
                new SalesforceServiceProvider(
                    clientId,
                    clientSecret,
                    myDomain + "/services/oauth2/authorize",
                    myDomain + "/services/oauth2/token");
            AccessGrant grant = provider.getOAuth2Client().clientCredentialsAccess(env("SF_SCOPE"));
            token = grant.getAccessToken();
            secrets.add(token);
            instance = grant.getInstanceUrl() != null ? grant.getInstanceUrl() : myDomain;
            how = "client credentials";
          }
          assertTrue("no access token was returned", token != null && !token.isBlank());
          instanceUrl = trimSlash(instance);
          rememberHost(instanceUrl);
          gatewayUrl = myDomain != null ? myDomain : instanceUrl;

          SalesforceTemplate template = new SalesforceTemplate(token);
          template.setInstanceUrl(instanceUrl);
          template.setAuthGatewayBaseUrl(gatewayUrl);
          api = template;

          runToken = token;
          final String bearer = token;
          raw = new RestTemplate();
          raw.getInterceptors()
              .add(
                  (request, body, execution) -> {
                    request.getHeaders().setBearerAuth(bearer);
                    return execution.execute(request, body);
                  });
          authenticated = true;
          return "authenticated with " + how;
        });
  }

  /**
   * Revokes a token and checks the org's answer. It only runs with client credentials, never with a
   * supplied access token, so it cannot destroy a token the user brought. It runs after the cleanup
   * because the client credentials flow may hand back the token the run itself used.
   */
  private void revocation() {
    if (!authenticated) {
      return;
    }
    optional(
        "token revocation (client credentials only)",
        () -> {
          String clientId = env("SF_CLIENT_ID");
          String clientSecret = env("SF_CLIENT_SECRET");
          String myDomain = trimSlash(env("SF_MY_DOMAIN_URL"));
          if (env("SF_ACCESS_TOKEN") != null
              || clientId == null
              || clientSecret == null
              || myDomain == null) {
            throw new Skip(
                "needs SF_CLIENT_ID, SF_CLIENT_SECRET and SF_MY_DOMAIN_URL, and no SF_ACCESS_TOKEN");
          }
          SalesforceOAuth2Client oauth =
              new SalesforceServiceProvider(
                      clientId,
                      clientSecret,
                      myDomain + "/services/oauth2/authorize",
                      myDomain + "/services/oauth2/token")
                  .getOAuth2Client();
          AccessGrant grant = oauth.clientCredentialsAccess(env("SF_SCOPE"));
          String token = grant.getAccessToken();
          secrets.add(token);
          SalesforceTemplate probe = new SalesforceTemplate(token);
          probe.setInstanceUrl(instanceUrl);
          // The token works before it is revoked
          probe.limitsOperations().getLimits();

          oauth.revokeToken(token);

          Exception afterwards = null;
          try {
            probe.limitsOperations().getLimits();
          } catch (Exception e) {
            afterwards = e;
          }
          assertNotNull("the revoked token still works", afterwards);
          HttpClientErrorException again =
              assertThrows(HttpClientErrorException.class, () -> oauth.revokeToken(token));
          String body = again.getResponseBodyAsString();
          return "revoked; a call with it then failed with "
              + afterwards.getClass().getSimpleName()
              + "; revoking again gave HTTP "
              + again.getStatusCode().value()
              + " "
              + "body: "
              + redact(body.replace("\n", " "))
              + " (content type "
              + again.getResponseHeaders().getContentType()
              + ")"
              + "; the token was "
              + (token.equals(runToken) ? "" : "not ")
              + "the one the run used";
        });
  }

  private void versionAndFloor() {
    core(
        "version in use",
        () -> {
          String wanted = env("SF_API_VERSION");
          if (wanted != null) {
            api.apiOperations().setVersion(wanted);
          }
          String version = api.apiOperations().getVersion();
          if (wanted == null) {
            assertEquals("library default", ApiOperations.DEFAULT_API_VERSION, version);
          }
          return version + (wanted == null ? " (library default)" : " (SF_API_VERSION)");
        });
    core(
        "floor: versions below 31 are rejected locally",
        () -> {
          String before = api.apiOperations().getVersion();
          assertThrows(
              InvalidSalesforceApiVersionException.class,
              () -> api.apiOperations().setVersion("v30.0"));
          assertThrows(
              InvalidSalesforceApiVersionException.class,
              () -> api.apiOperations().getServices("v30.0"));
          assertEquals(before, api.apiOperations().getVersion());
          return "setVersion and getServices both refused v30.0, version unchanged";
        });
    optional(
        "floor: the org still serves v31.0",
        () -> {
          Map<String, String> services = api.apiOperations().getServices("v31.0");
          assertTrue("empty services document", !services.isEmpty());
          return "v31.0 services document has " + services.size() + " entries";
        });
    core(
        "available versions include the one in use",
        () -> {
          List<ApiVersion> versions = api.apiOperations().getVersions();
          String wanted = api.apiOperations().getVersion().substring(1);
          assertTrue(
              "versions list does not contain " + wanted,
              versions.stream().anyMatch(v -> wanted.equals(v.getVersion())));
          return versions.size()
              + " versions; newest is "
              + versions.get(versions.size() - 1).getVersion();
        });
    core(
        "services document",
        () -> {
          Map<String, String> services = api.apiOperations().getServices();
          assertTrue("no sobjects entry", services.containsKey("sobjects"));
          assertTrue("no query entry", services.containsKey("query"));
          return services.size() + " entries";
        });
  }

  private void metadata() {
    core(
        "sObject list and Account summary",
        () -> {
          List<Map> sobjects = api.sObjectsOperations().getSObjects();
          assertTrue(
              "Account missing from the sObject list",
              sobjects.stream().anyMatch(s -> "Account".equals(s.get("name"))));
          SObjectSummary account = api.sObjectsOperations().getSObjectSummary("Account");
          assertNotNull(account);
          assertEquals("Account", account.getName());
          assertTrue("Account should be queryable", account.isQueryable());
          return sobjects.size() + " sObjects";
        });
    core(
        "describe Account",
        () -> {
          SObjectDetail detail = api.sObjectsOperations().describeSObject("Account");
          assertTrue("no fields", !detail.getFields().isEmpty());
          assertTrue(
              "no Name field",
              detail.getFields().stream().anyMatch(f -> "Name".equals(f.getName())));
          assertTrue("no child relationships", !detail.getChildRelationships().isEmpty());
          // The three flags that were always false because of misspelled JSON names
          assertTrue(
              "Account should be layoutable (was always false before the fix)",
              detail.isLayoutable());
          assertTrue(
              "Account should be retrieveable (was always false before the fix)",
              detail.isRetrieveable());
          assertTrue("Account should not be deprecatedAndHidden", !detail.isDeprecatedAndHidden());
          return detail.getFields().size()
              + " fields, "
              + detail.getChildRelationships().size()
              + " child relationships";
        });
    core(
        "models match the raw JSON: sObject list",
        () ->
            parity("SObjectSummary", SObjectSummary.class, items(rawGet("/sobjects"), "sobjects")));
    core(
        "models match the raw JSON: describe, fields, picklists, relationships",
        () -> {
          JsonNode describe = rawGet("/sobjects/Account/describe");
          List<JsonNode> picklists = new ArrayList<>();
          describe.get("fields").forEach(f -> f.path("picklistValues").forEach(picklists::add));
          String detail = parity("SObjectDetail", SObjectDetail.class, List.of(describe));
          detail += "; " + parity("Field", Field.class, items(describe, "fields"));
          detail +=
              "; "
                  + parity(
                      "Relationship", Relationship.class, items(describe, "childRelationships"));
          if (!picklists.isEmpty()) {
            detail += "; " + parity("PickListEntry", PickListEntry.class, picklists);
          }
          if (describe.has("recordTypeInfos") && describe.get("recordTypeInfos").size() > 0) {
            detail +=
                "; "
                    + parity(
                        "RecordTypeInfo", RecordTypeInfo.class, items(describe, "recordTypeInfos"));
          }
          return detail;
        });
    core(
        "models match the raw JSON: API versions",
        () ->
            parity("ApiVersion", ApiVersion.class, items(rawGetAbsolute("/services/data/"), null)));
  }

  private void dataAccess() {
    core(
        "query",
        () -> {
          QueryResult result = api.queryOperations().query("SELECT Id, Name FROM Account LIMIT 5");
          assertTrue("more rows than the LIMIT", result.getRecords().size() <= 5);
          if (!result.getRecords().isEmpty()) {
            firstAccountId = String.valueOf(result.getRecords().get(0).getAttributes().get("Id"));
            firstAccountName =
                String.valueOf(result.getRecords().get(0).getAttributes().get("Name"));
          }
          return result.getRecords().size() + " rows";
        });
    core(
        "getRow with fields",
        () -> {
          if (firstAccountId == null) {
            throw new Skip("the org has no Account rows");
          }
          Map row = api.sObjectsOperations().getRow("Account", firstAccountId, "Id", "Name");
          assertEquals(firstAccountId, row.get("Id"));
          return "returned the requested row";
        });
    core(
        "query text with + and & is accepted",
        () -> {
          // Before the fix this was altered in transit and Salesforce rejected it as malformed
          QueryResult result =
              api.queryOperations().query("SELECT Id FROM Account WHERE Name = 'live+check&text'");
          return result.getRecords().size()
              + " rows (the point is that Salesforce accepted the query)";
        });
    core(
        "compound field (BillingAddress) in a query result",
        () -> {
          // A compound field arrives as a nested object with no "attributes" entry
          QueryResult result =
              api.queryOperations()
                  .query(
                      "SELECT Id, BillingAddress FROM Account WHERE BillingCity != null LIMIT 1");
          if (result.getRecords().isEmpty()) {
            throw new Skip("no Account has a billing address; enable writes to create one");
          }
          assertNotNull(
              "BillingAddress missing from the record",
              result.getRecords().get(0).getAttributes().get("BillingAddress"));
          return "the record with a compound field was read";
        });
    core(
        "query with a parent, a child subquery and a compound field",
        () -> {
          QueryResult result =
              api.queryOperations()
                  .query(
                      "SELECT Id, Name, Owner.Name, BillingAddress, (SELECT Id, LastName FROM Contacts) FROM Account LIMIT 25");
          if (result.getRecords().isEmpty()) {
            throw new Skip("the org has no Account rows");
          }
          int parents = 0, children = 0, compounds = 0;
          for (var record : result.getRecords()) {
            Object owner = record.getAttributes().get("Owner");
            if (owner != null) {
              assertTrue(
                  "Owner should be a ResultItem but was " + owner.getClass().getSimpleName(),
                  owner instanceof org.springframework.salesforce.api.ResultItem);
              parents++;
            }
            Object contacts = record.getAttributes().get("Contacts");
            if (contacts != null) {
              assertTrue(
                  "Contacts should be a QueryResult but was " + contacts.getClass().getSimpleName(),
                  contacts instanceof QueryResult);
              children++;
            }
            Object address = record.getAttributes().get("BillingAddress");
            if (address != null) {
              assertTrue(
                  "BillingAddress should be a Map but was " + address.getClass().getSimpleName(),
                  address instanceof Map);
              compounds++;
            }
          }
          return result.getRecords().size()
              + " rows; "
              + parents
              + " with a parent record, "
              + children
              + " with a child result, "
              + compounds
              + " with a compound field";
        });
    core(
        "queryAll pages through a large result",
        () -> {
          String object = env("SF_PAGING_OBJECT");
          List<String> candidates =
              object != null
                  ? List.of(object)
                  : List.of(
                      "ObjectPermissions", "Account", "Contact", "Lead", "Opportunity", "Case");
          List<String> notes = new ArrayList<>();
          String nextShape = "";
          for (String candidate : candidates) {
            int count;
            try {
              count =
                  api.queryOperations().query("SELECT COUNT() FROM " + candidate).getTotalSize();
            } catch (ApiException e) {
              if (object != null) {
                throw e; // the user named it, so a failure is a real failure
              }
              notes.add(candidate + " not queryable (" + describe(e) + ")");
              continue;
            }
            if (count > 2000 && count <= 50000) {
              // Report the shape of the next-page URL the queryAll resource hands out (the locator
              // itself is masked)
              JsonNode firstPage =
                  json.readTree(
                      raw.getForObject(
                          instanceUrl
                              + "/services/data/"
                              + api.apiOperations().getVersion()
                              + "/queryAll?q={q}",
                          String.class,
                          "SELECT Id FROM " + candidate));
              String next = firstPage.path("nextRecordsUrl").asText("");
              assertTrue(
                  "the queryAll resource returned no nextRecordsUrl for " + count + " rows",
                  !next.isEmpty());
              nextShape = next.replaceAll("[^/]+$", "<locator>");
              int collected =
                  api.queryOperations().queryAll("SELECT Id FROM " + candidate).getRecords().size();
              // Rows can be added or removed between the COUNT and the fetch in a shared org, so
              // allow a little drift
              assertTrue(
                  "queryAll collected " + collected + " rows but COUNT() said " + count,
                  collected > 2000 && Math.abs(collected - count) <= Math.max(5, count / 100));
              return candidate
                  + ": "
                  + collected
                  + " rows collected across pages (COUNT() said "
                  + count
                  + "); queryAll first page's nextRecordsUrl has the shape "
                  + nextShape;
            }
            notes.add(candidate + " has " + count + " rows");
          }
          throw new Skip(
              "no candidate object has between 2001 and 50000 rows ("
                  + String.join("; ", notes)
                  + "); set SF_PAGING_OBJECT to test paging");
        });
    core(
        "search",
        () -> {
          List<?> hits =
              api.searchOperations().search("FIND {test} IN ALL FIELDS RETURNING Account(Id)");
          return hits.size() + " hits";
        });
    core(
        "search response shape (information)",
        () -> {
          JsonNode root =
              json.readTree(
                  raw.getForObject(
                      instanceUrl
                          + "/services/data/"
                          + api.apiOperations().getVersion()
                          + "/search/?q={q}",
                      String.class,
                      "FIND {test} IN ALL FIELDS RETURNING Account(Id)"));
          return "raw response is a JSON "
              + (root.isArray() ? "array" : "object with keys " + keysOf(root));
        });
  }

  private void searchAcrossVersions() {
    core(
        "search at every API version the org serves, from the floor up",
        () -> {
          // The response shape is the question here. Documentation does not say which versions
          // return a bare
          // array and which return {"searchRecords": [...]}, so ask the org about every version.
          String term = "test";
          if (firstAccountName != null) {
            String word = firstAccountName.split("[^A-Za-z0-9]+")[0];
            if (word.length() >= 2) {
              term = word;
            }
          }
          String sosl = "FIND {" + term + "} IN NAME FIELDS RETURNING Account(Id)";
          String original = api.apiOperations().getVersion();
          List<String> arrays = new ArrayList<>();
          List<String> objects = new ArrayList<>();
          List<String> other = new ArrayList<>();
          List<String> problems = new ArrayList<>();
          int arrayHits = 0;
          int objectHits = 0;
          try {
            for (ApiVersion served : api.apiOperations().getVersions()) {
              int major = (int) Double.parseDouble(served.getVersion());
              if (major < 31) {
                continue;
              }
              String version = "v" + served.getVersion();
              JsonNode root =
                  json.readTree(
                      raw.getForObject(
                          instanceUrl + "/services/data/" + version + "/search/?q={q}",
                          String.class,
                          sosl));
              int expected;
              if (root.isArray()) {
                arrays.add(served.getVersion());
                expected = root.size();
                arrayHits += expected;
              } else if (root.has("searchRecords")) {
                objects.add(served.getVersion());
                expected = root.get("searchRecords").size();
                objectHits += expected;
              } else {
                other.add(served.getVersion());
                problems.add(version + " returned neither shape: " + keysOf(root));
                continue;
              }
              api.apiOperations().setVersion(version);
              int actual = api.searchOperations().search(sosl).size();
              if (actual != expected) {
                problems.add(
                    version
                        + ": library returned "
                        + actual
                        + " but the raw response has "
                        + expected);
              }
            }
          } finally {
            api.apiOperations().setVersion(original);
          }
          assertTrue(String.join("; ", problems), problems.isEmpty());
          String summary =
              "bare array at "
                  + (arrays.isEmpty() ? "no version" : arrays)
                  + "; searchRecords object at "
                  + (objects.isEmpty()
                      ? "no version"
                      : objects.size()
                          + " versions ("
                          + objects.get(0)
                          + " to "
                          + objects.get(objects.size() - 1)
                          + ")");
          // With zero hits an empty array and an empty list always agree, so record parsing was not
          // exercised
          List<String> untested = new ArrayList<>();
          if (!arrays.isEmpty() && arrayHits == 0) {
            untested.add("bare array");
          }
          if (!objects.isEmpty() && objectHits == 0) {
            untested.add("searchRecords object");
          }
          if (!untested.isEmpty()) {
            throw new Warn(
                summary
                    + "; but the search for '"
                    + term
                    + "' returned no hits in the "
                    + String.join(" and ", untested)
                    + " shape, so record parsing was not exercised there. Create an Account whose name contains a distinctive word, or enable writes.");
          }
          return summary
              + "; the library read every one ("
              + arrayHits
              + " and "
              + objectHits
              + " hits parsed)";
        });
  }

  private void limitsAndRecent() {
    core(
        "limits: modeled and unmodeled limits",
        () -> {
          // getLimits() exercises the real call. The comparison below uses the raw JSON it is
          // compared with,
          // because every API call changes the remaining counts and two separate calls would never
          // agree.
          LimitsResults fromLibrary = api.limitsOperations().getLimits();
          assertNotNull("DailyApiRequests", fromLibrary.getDailyApiRequests());
          assertTrue(
              "DailyApiRequests.Max should be positive",
              fromLibrary.getDailyApiRequests().getMax() > 0);
          JsonNode rawLimits = rawGet("/limits");
          LimitsResults limits = api.readObject(rawLimits, LimitsResults.class);

          List<String> problems = new ArrayList<>();
          List<String> unmodeled = new ArrayList<>();
          for (Iterator<Map.Entry<String, JsonNode>> it = rawLimits.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            Method getter = findGetter(LimitsResults.class, entry.getKey());
            if (getter != null) {
              LimitResult modeled = (LimitResult) getter.invoke(limits);
              if (modeled == null) {
                problems.add(entry.getKey() + " was not populated");
              } else {
                scalarProblems(
                    "LimitsResults." + entry.getKey(), modeled, entry.getValue(), problems);
              }
            } else if (limits.getAdditionalLimit(entry.getKey()).isEmpty()) {
              problems.add(
                  entry.getKey() + " is not modeled and not available from getAdditionalLimit");
            } else {
              unmodeled.add(entry.getKey());
            }
          }
          assertTrue(String.join("; ", problems), problems.isEmpty());
          return rawLimits.size()
              + " limits; "
              + unmodeled.size()
              + " not modeled but kept in additionalProperties"
              + (unmodeled.isEmpty()
                  ? ""
                  : " (e.g. " + unmodeled.subList(0, Math.min(5, unmodeled.size())) + ")");
        });
    core(
        "limits: usage header parsed",
        () -> {
          ResponseEntity<String> response =
              raw.exchange(
                  instanceUrl + "/services/data/" + api.apiOperations().getVersion() + "/limits",
                  HttpMethod.GET,
                  null,
                  String.class);
          String header = response.getHeaders().getFirst("Sforce-Limit-Info");
          api.limitsOperations().getLimits();
          if (header == null) {
            throw new Skip("this response carried no Sforce-Limit-Info header");
          }
          Matcher matcher = API_USAGE.matcher(header);
          assertTrue("unexpected header format: " + header, matcher.find());
          assertEquals(
              "daily limit from the header",
              Integer.parseInt(matcher.group(2)),
              api.limitsOperations().getDailyApiLimit());
          // The header and the parsed value come from different calls and the org counts every
          // call, so allow drift
          int headerUsed = Integer.parseInt(matcher.group(1));
          int parsedUsed = api.limitsOperations().getDailyApiUsed();
          assertTrue(
              "used count from the header was " + headerUsed + " but the library has " + parsedUsed,
              Math.abs(parsedUsed - headerUsed) <= 25);
          return "header '"
              + header
              + "' parsed as "
              + parsedUsed
              + " of "
              + api.limitsOperations().getDailyApiLimit()
              + (header.contains("per-app-api-usage")
                  ? " (carries per-app-api-usage, the case that used to throw)"
                  : " (no per-app-api-usage entry in this response, so that case was not exercised live)");
        });
    core("recent items", () -> api.recentOperations().recent().size() + " items");
  }

  @SuppressWarnings(
      "deprecation") // getStatus is deprecated on purpose; the check confirms it still reports not
  // found
  private void optionalFeatures() {
    optional(
        "user info",
        () -> {
          assertNotNull(api.userOperations(gatewayUrl).getSalesforceUserDetails());
          return "user details returned";
        });
    optional(
        "user info: photo and username are bound from the userinfo response",
        () -> {
          JsonNode info =
              json.readTree(
                  raw.getForObject(gatewayUrl + "/services/oauth2/userinfo", String.class));
          JsonNode photos = info.get("photos");
          var details = api.userOperations(gatewayUrl).getSalesforceUserDetails();
          // Only key names and whether a value is present are reported, never the values
          if (photos != null && photos.hasNonNull("picture")) {
            assertNotNull("photos.picture was sent but getPhoto() is null", details.getPhoto());
            assertEquals(photos.get("picture").asText(), details.getPhoto().getLargePhotoUrl());
          }
          if (photos != null && photos.hasNonNull("thumbnail")) {
            assertNotNull("photos.thumbnail was sent but getPhoto() is null", details.getPhoto());
            assertEquals(photos.get("thumbnail").asText(), details.getPhoto().getSmallPhotoUrl());
          }
          if (info.hasNonNull("preferred_username")) {
            assertEquals(info.get("preferred_username").asText(), details.getUsername());
          }
          return "raw photos keys="
              + (photos == null ? "absent" : keysOf(photos))
              + "; getPhoto()="
              + (details.getPhoto() == null ? "null" : "set")
              + ", getUsername()="
              + (details.getUsername() == null ? "null" : "set");
        });
    optional(
        "chatter profile",
        () -> {
          SalesforceProfile profile = api.chatterOperations().getUserProfile();
          assertNotNull(profile);
          assertTrue(
              "profile has no name", profile.getName() != null && !profile.getName().isBlank());
          return "profile returned";
        });
    optional(
        "models match the raw JSON: chatter profile",
        () ->
            parity(
                "SalesforceProfile",
                SalesforceProfile.class,
                List.of(rawGet("/chatter/users/me"))));
    optional(
        "chatter status (this resource was retired in API 25, so not found is expected)",
        () -> {
          try {
            api.chatterOperations().getStatus();
            return "the call unexpectedly succeeded";
          } catch (ResourceNotFoundException e) {
            return "reported as not found, which is what a retired resource should give";
          }
        });
    optional(
        "communities",
        () -> {
          List<Community> communities = api.connectOperations().getCommunities();
          if (communities.isEmpty()) {
            return "no communities in this org";
          }
          List<CommunityUser> users =
              api.connectOperations().getCommunityUsers(communities.get(0).getId());
          String detail = communities.size() + " communities; first has " + users.size() + " users";
          detail +=
              "; "
                  + parity(
                      "Community",
                      Community.class,
                      items(rawGet("/connect/communities"), "communities"));
          return detail;
        });
    optional(
        "custom Apex REST (SF_APEX_PATH)",
        () -> {
          String path = env("SF_APEX_PATH");
          if (path == null) {
            throw new Skip("set SF_APEX_PATH to an Apex REST path such as /MyService/");
          }
          String body = api.customApiOperations().getForApexObject(path, String.class);
          return "GET returned " + (body == null ? 0 : body.length()) + " characters";
        });
  }

  private void writes() {
    if (!"true".equalsIgnoreCase(env("SF_ENABLE_WRITES"))) {
      skip(
          "writes: Account create, update, delete and blob round trip",
          "set SF_ENABLE_WRITES=true to run these (use a disposable org)");
      return;
    }
    if (!targetOrgAllowsWrites()) {
      return;
    }
    String stamp = String.valueOf(System.currentTimeMillis());
    String name = "spring-salesforce live check a+b&c " + stamp;
    String[] accountId = new String[1];

    core(
        "write: create Account",
        () -> {
          Map<String, Object> created =
              api.sObjectsOperations().create("Account", Map.of("Name", name));
          accountId[0] = String.valueOf(created.get("id"));
          assertEquals(Boolean.TRUE, created.get("success"));
          cleanup.push(
              Map.entry(
                  "Account " + accountId[0],
                  () -> api.sObjectsOperations().delete("Account", accountId[0])));
          return "created " + accountId[0];
        });
    core(
        "write: read it back",
        () -> {
          requireValue(accountId[0], "the Account was not created");
          assertEquals(
              name, api.sObjectsOperations().getRow("Account", accountId[0], "Name").get("Name"));
          return "name matches";
        });
    core(
        "write: query by a name containing + and &",
        () -> {
          requireValue(accountId[0], "the Account was not created");
          QueryResult result =
              api.queryOperations().query("SELECT Id FROM Account WHERE Name = '" + name + "'");
          assertEquals("rows matching the exact name", 1, result.getRecords().size());
          assertEquals(
              accountId[0], String.valueOf(result.getRecords().get(0).getAttributes().get("Id")));
          return "found exactly the record just created, so + and & reached Salesforce intact";
        });
    core(
        "write: update",
        () -> {
          requireValue(accountId[0], "the Account was not created");
          Map<String, Object> updated =
              api.sObjectsOperations()
                  .update("Account", accountId[0], Map.of("Name", name + " updated"));
          assertEquals(Boolean.TRUE, updated.get("success"));
          assertEquals(
              name + " updated",
              api.sObjectsOperations().getRow("Account", accountId[0], "Name").get("Name"));
          return "update applied";
        });
    core(
        "write: delete",
        () -> {
          requireValue(accountId[0], "the Account was not created");
          api.sObjectsOperations().delete("Account", accountId[0]);
          cleanup.removeIf(e -> e.getKey().equals("Account " + accountId[0]));
          assertThrows(
              ResourceNotFoundException.class,
              () -> api.sObjectsOperations().getRow("Account", accountId[0]));
          return "deleted; a later read reports not found";
        });

    String[] compoundId = new String[1];
    core(
        "write: compound field (BillingAddress) round trip",
        () -> {
          Map<String, Object> fields = new LinkedHashMap<>();
          fields.put("Name", "spring-salesforce live check compound " + stamp);
          fields.put("BillingStreet", "1 Main St");
          fields.put("BillingCity", "Springfield");
          Map<String, Object> created = api.sObjectsOperations().create("Account", fields);
          compoundId[0] = String.valueOf(created.get("id"));
          cleanup.push(
              Map.entry(
                  "Account " + compoundId[0],
                  () -> api.sObjectsOperations().delete("Account", compoundId[0])));
          QueryResult result =
              api.queryOperations()
                  .query(
                      "SELECT Id, BillingAddress FROM Account WHERE Id = '" + compoundId[0] + "'");
          assertEquals(1, result.getRecords().size());
          assertNotNull(
              "BillingAddress missing from the record",
              result.getRecords().get(0).getAttributes().get("BillingAddress"));
          return "the compound field was read back";
        });
    core(
        "write: queryAll(String) includes deleted records, query(String) does not",
        () -> {
          requireValue(compoundId[0], "the Account was not created");
          api.sObjectsOperations().delete("Account", compoundId[0]);
          cleanup.removeIf(e -> e.getKey().equals("Account " + compoundId[0]));
          String soql = "SELECT Id FROM Account WHERE Id = '" + compoundId[0] + "'";
          int viaQuery = api.queryOperations().query(soql).getRecords().size();
          int viaQueryAllMethod = api.queryOperations().queryAll(soql).getRecords().size();
          int viaIncludeDeleted = api.queryOperations().query(soql, true).getRecords().size();
          assertEquals("query(soql) should not see the deleted record", 0, viaQuery);
          assertEquals("queryAll(soql) should see the deleted record", 1, viaQueryAllMethod);
          assertEquals(
              "queryAll(soql, false) should not see the deleted record",
              0,
              api.queryOperations().queryAll(soql, false).getRecords().size());
          assertEquals("query(soql, true) should see the deleted record", 1, viaIncludeDeleted);
          return "deleted record: query()="
              + viaQuery
              + ", queryAll(String)="
              + viaQueryAllMethod
              + ", query(String, true)="
              + viaIncludeDeleted;
        });

    core(
        "write: the queryAll resource keeps returning deleted records on later pages (raw)",
        () -> {
          // 250 deleted rows with a batch size of 200 force a second page; if the next-page cursor
          // lost the
          // "include deleted" mode, the second page would be empty
          String prefix = "spring-salesforce live paging " + stamp;
          String data = instanceUrl + "/services/data/" + api.apiOperations().getVersion();
          List<String> ids = new ArrayList<>();
          for (int from = 0; from < 250; from += 200) {
            List<Map<String, Object>> records = new ArrayList<>();
            for (int i = from; i < Math.min(from + 200, 250); i++) {
              records.add(
                  Map.of("attributes", Map.of("type", "Account"), "Name", prefix + " " + i));
            }
            org.springframework.http.HttpHeaders jsonHeaders =
                new org.springframework.http.HttpHeaders();
            jsonHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            JsonNode created =
                json.readTree(
                    raw.postForObject(
                        data + "/composite/sobjects",
                        new org.springframework.http.HttpEntity<>(
                            json.writeValueAsString(Map.of("allOrNone", true, "records", records)),
                            jsonHeaders),
                        String.class));
            created.forEach(r -> ids.add(r.path("id").asText()));
          }
          cleanup.push(Map.entry("paging Accounts", () -> deleteAccounts(data, ids)));
          assertEquals("created Accounts", 250, ids.size());
          deleteAccounts(data, ids);

          org.springframework.http.HttpHeaders batch = new org.springframework.http.HttpHeaders();
          batch.set("Sforce-Query-Options", "batchSize=200");
          String soql = "SELECT Id, IsDeleted FROM Account WHERE Name LIKE '" + prefix + "%'";
          JsonNode first =
              json.readTree(
                  raw.exchange(
                          data + "/queryAll?q={q}",
                          HttpMethod.GET,
                          new org.springframework.http.HttpEntity<>(batch),
                          String.class,
                          soql)
                      .getBody());
          assertEquals(
              "total deleted rows found by queryAll", 250, first.path("totalSize").asInt());
          assertEquals("first page size", 200, first.path("records").size());
          String next = first.path("nextRecordsUrl").asText("");
          assertTrue(
              "no nextRecordsUrl for a result of 250 rows with batch size 200", !next.isEmpty());
          JsonNode second = json.readTree(raw.getForObject(instanceUrl + next, String.class));
          assertEquals("rows on the second page", 50, second.path("records").size());
          assertTrue(
              "second page rows should be deleted",
              second.path("records").get(0).path("IsDeleted").asBoolean());
          return "the second page held "
              + second.path("records").size()
              + " deleted rows; nextRecordsUrl shape "
              + next.replaceAll("[^/]+$", "<locator>");
        });

    byte[] content = new byte[64 * 1024];
    for (int i = 0; i < content.length; i++) {
      content[i] = (byte) (i * 7 + (i >>> 8));
    }
    String[] versionId = new String[1];
    core(
        "write: create ContentVersion (blob)",
        () -> {
          Map<String, Object> fields = new LinkedHashMap<>();
          fields.put("Title", "spring-salesforce live check " + stamp);
          fields.put("PathOnClient", "live-check-" + stamp + ".bin");
          fields.put("VersionData", Base64.getEncoder().encodeToString(content));
          Map<String, Object> created = api.sObjectsOperations().create("ContentVersion", fields);
          versionId[0] = String.valueOf(created.get("id"));
          cleanup.push(
              Map.entry(
                  "ContentDocument of ContentVersion " + versionId[0],
                  () -> {
                    QueryResult doc =
                        api.queryOperations()
                            .query(
                                "SELECT ContentDocumentId FROM ContentVersion WHERE Id = '"
                                    + versionId[0]
                                    + "'");
                    if (!doc.getRecords().isEmpty()) {
                      api.sObjectsOperations()
                          .delete(
                              "ContentDocument",
                              String.valueOf(
                                  doc.getRecords()
                                      .get(0)
                                      .getAttributes()
                                      .get("ContentDocumentId")));
                    }
                  }));
          return "created " + versionId[0];
        });
    core(
        "write: getBlob returns the exact bytes",
        () -> {
          requireValue(versionId[0], "the ContentVersion was not created");
          try (InputStream in =
              api.sObjectsOperations().getBlob("ContentVersion", versionId[0], "VersionData")) {
            assertTrue("blob bytes differ", java.util.Arrays.equals(content, in.readAllBytes()));
          }
          return content.length + " bytes identical";
        });
    core(
        "write: downloadBlob streams the exact bytes",
        () -> {
          requireValue(versionId[0], "the ContentVersion was not created");
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          long written =
              api.sObjectsOperations()
                  .downloadBlob("ContentVersion", versionId[0], "VersionData", out);
          assertEquals(content.length, written);
          assertTrue("blob bytes differ", java.util.Arrays.equals(content, out.toByteArray()));
          return written + " bytes identical";
        });
  }

  /**
   * Asks the org what it is and refuses writes unless it is a sandbox, scratch org or Developer
   * Edition org (or the caller set SF_ALLOW_WRITES_IN_PRODUCTION=true). If the org cannot be
   * identified, writes are refused.
   */
  private boolean targetOrgAllowsWrites() {
    String name = "write guard: the target org is a sandbox or Developer Edition";
    if (!authenticated) {
      skip(name, "authentication did not succeed");
      return false;
    }
    boolean override = "true".equalsIgnoreCase(env("SF_ALLOW_WRITES_IN_PRODUCTION"));
    try {
      QueryResult org =
          api.queryOperations().query("SELECT IsSandbox, OrganizationType FROM Organization");
      if (org.getRecords().isEmpty()) {
        throw new IllegalStateException("the Organization query returned no rows");
      }
      Map<String, Object> row = org.getRecords().get(0).getAttributes();
      boolean sandbox = Boolean.parseBoolean(String.valueOf(row.get("IsSandbox")));
      String type = String.valueOf(row.get("OrganizationType"));
      boolean disposable = sandbox || "Developer Edition".equalsIgnoreCase(type);
      String facts = "OrganizationType=" + type + ", IsSandbox=" + sandbox;
      if (disposable) {
        results.add(new Result(Status.PASS, name, facts));
        return true;
      }
      if (override) {
        results.add(
            new Result(
                Status.WARN,
                name,
                facts + "; writing anyway because SF_ALLOW_WRITES_IN_PRODUCTION=true"));
        return true;
      }
      skip(name, facts + "; writes refused. Set SF_ALLOW_WRITES_IN_PRODUCTION=true to override");
    } catch (Throwable t) {
      skip(name, "could not identify the org, so writes are refused (" + describe(t) + ")");
    }
    return false;
  }

  // ------------------------------------------------------------------ check plumbing

  /** Thrown from a check to mark it as skipped rather than passed or failed. */
  private static final class Skip extends RuntimeException {
    private static final long serialVersionUID = 1L;

    Skip(String reason) {
      super(reason);
    }
  }

  /** Thrown from a check that ran but could not prove what it set out to prove. */
  private static final class Warn extends RuntimeException {
    private static final long serialVersionUID = 1L;

    Warn(String reason) {
      super(reason);
    }
  }

  /** A check that must pass. Any exception fails it. */
  /**
   * Deletes Accounts through the sObject Collections resource, 200 per request, ignoring ones
   * already gone.
   */
  private void deleteAccounts(String dataUrl, List<String> ids) {
    for (int from = 0; from < ids.size(); from += 200) {
      String joined = String.join(",", ids.subList(from, Math.min(from + 200, ids.size())));
      raw.exchange(
          dataUrl + "/composite/sobjects?ids=" + joined, HttpMethod.DELETE, null, String.class);
    }
  }

  private void core(String name, Check check) {
    execute(name, check, false);
  }

  /**
   * A check that depends on org features or permissions. A Salesforce API error is a warning,
   * anything else fails.
   */
  private void optional(String name, Check check) {
    execute(name, check, true);
  }

  private void execute(String name, Check check, boolean optional) {
    if (!authenticated && !name.equals("authenticate")) {
      skip(name, "authentication did not succeed");
      return;
    }
    try {
      results.add(new Result(Status.PASS, name, check.run()));
    } catch (Skip skip) {
      skip(name, skip.getMessage());
    } catch (Warn warn) {
      results.add(new Result(Status.WARN, name, warn.getMessage()));
    } catch (ApiException e) {
      results.add(new Result(optional ? Status.WARN : Status.FAIL, name, describe(e)));
    } catch (Throwable t) {
      results.add(new Result(Status.FAIL, name, describe(t)));
    }
  }

  private void skip(String name, String reason) {
    results.add(new Result(Status.SKIP, name, reason));
  }

  private void runCleanup() {
    while (!cleanup.isEmpty()) {
      Map.Entry<String, Runnable> entry = cleanup.pop();
      try {
        entry.getValue().run();
        results.add(new Result(Status.INFO, "cleanup: " + entry.getKey(), "removed"));
      } catch (Throwable t) {
        results.add(
            new Result(
                Status.WARN,
                "cleanup: " + entry.getKey(),
                "could not remove it, delete it by hand: " + describe(t)));
      }
    }
  }

  private void printReport() {
    StringBuilder out = new StringBuilder("\n===== Live Salesforce check =====\n");
    for (Result r : results) {
      out.append(String.format("[%-4s] %s%n       %s%n", r.status(), r.name(), r.detail()));
    }
    Map<Status, Long> counts = new LinkedHashMap<>();
    for (Status s : Status.values()) {
      counts.put(s, results.stream().filter(r -> r.status() == s).count());
    }
    out.append("-----\n").append(counts).append("\n");
    System.out.println(out);
  }

  // ------------------------------------------------------------------ helpers

  private boolean haveCredentials() {
    return (env("SF_ACCESS_TOKEN") != null && env("SF_INSTANCE_URL") != null)
        || (env("SF_MY_DOMAIN_URL") != null
            && env("SF_CLIENT_ID") != null
            && env("SF_CLIENT_SECRET") != null);
  }

  private static String env(String name) {
    String value = System.getenv(name);
    return value == null || value.isBlank() ? null : value.trim();
  }

  private static String trimSlash(String url) {
    return url == null ? null : url.replaceAll("/+$", "");
  }

  private void rememberHost(String url) {
    if (url != null) {
      try {
        String host = URI.create(url).getHost();
        if (host != null) {
          hosts.add(host);
        }
      } catch (IllegalArgumentException ignored) {
        // not a URL; nothing to mask
      }
    }
  }

  private String describe(Throwable t) {
    String message = String.valueOf(t.getMessage()).lines().findFirst().orElse("");
    Throwable root = t;
    while (root.getCause() != null && root.getCause() != root) {
      root = root.getCause();
    }
    // Wrapper exceptions (for example a RestClientException around a deserializer failure) hide the
    // real
    // cause, so name its class; the message is left out because it can contain response data.
    String cause = root == t ? "" : " (root cause: " + root.getClass().getSimpleName() + ")";
    return redact(t.getClass().getSimpleName() + ": " + message + cause);
  }

  /** Removes anything that must not appear in a report that might be shared. */
  private String redact(String text) {
    String result = text;
    for (String secret : secrets) {
      if (secret != null && !secret.isBlank()) {
        result = result.replace(secret, "***");
      }
    }
    for (String host : hosts) {
      result = result.replace(host, "<org-host>");
    }
    return result.length() > 400 ? result.substring(0, 400) + "..." : result;
  }

  private static void requireValue(String value, String message) {
    if (value == null || "null".equals(value)) {
      throw new Skip(message);
    }
  }

  private JsonNode rawGet(String path) throws Exception {
    return rawGetAbsolute("/services/data/" + api.apiOperations().getVersion() + path);
  }

  private JsonNode rawGetAbsolute(String path) throws Exception {
    return json.readTree(raw.getForObject(instanceUrl + path, String.class));
  }

  private static List<JsonNode> items(JsonNode node, String field) {
    JsonNode array = field == null ? node : node.get(field);
    List<JsonNode> items = new ArrayList<>();
    if (array != null) {
      array.forEach(items::add);
    }
    return items;
  }

  private static String keysOf(JsonNode node) {
    List<String> keys = new ArrayList<>();
    node.fieldNames().forEachRemaining(keys::add);
    return keys.toString();
  }

  /**
   * Deserializes each raw JSON node into the model and checks that every scalar Salesforce sent
   * ended up in the matching property. Scalars without a matching getter are ignored; the model
   * chose not to expose them.
   */
  private <T> String parity(String what, Class<T> type, Iterable<JsonNode> nodes) throws Exception {
    List<String> problems = new ArrayList<>();
    int count = 0;
    for (JsonNode node : nodes) {
      T bean = api.readObject(node, type);
      assertNotNull(what + " did not deserialize", bean);
      scalarProblems(what + "[" + count++ + "]", bean, node, problems);
    }
    assertTrue("no " + what + " records were available to check", count > 0);
    if (!problems.isEmpty()) {
      throw new AssertionError(
          problems.size()
              + " mismatches, e.g. "
              + String.join(" | ", problems.subList(0, Math.min(4, problems.size()))));
    }
    return what + ": " + count + " records match";
  }

  private void scalarProblems(String what, Object bean, JsonNode node, List<String> problems)
      throws Exception {
    for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
      Map.Entry<String, JsonNode> entry = it.next();
      JsonNode expected = entry.getValue();
      if (!expected.isValueNode() || expected.isNull()) {
        continue;
      }
      Method getter = findGetter(bean.getClass(), entry.getKey());
      if (getter == null) {
        continue;
      }
      Object actual = getter.invoke(bean);
      boolean ok;
      if (expected.isBoolean() && actual instanceof Boolean b) {
        ok = b == expected.booleanValue();
      } else if (expected.isNumber() && actual instanceof Number n) {
        ok = n.doubleValue() == expected.doubleValue();
      } else {
        // Also covers a model that holds a JSON boolean or number as text (Field.defaultValue is a
        // String)
        ok = expected.asText().equals(String.valueOf(actual));
      }
      if (!ok) {
        problems.add(
            what
                + "."
                + getter.getName()
                + "() was <"
                + actual
                + "> but Salesforce sent "
                + entry.getKey()
                + "="
                + expected);
      }
    }
  }

  private static Method findGetter(Class<?> type, String key) {
    String cap = Character.toUpperCase(key.charAt(0)) + key.substring(1);
    List<String> candidates = new ArrayList<>(List.of("get" + cap, "is" + cap));
    if (key.length() > 2 && key.startsWith("is") && Character.isUpperCase(key.charAt(2))) {
      candidates.add("is" + key.substring(2));
      candidates.add("get" + key.substring(2));
    }
    for (String name : candidates) {
      try {
        return type.getMethod(name);
      } catch (NoSuchMethodException ignored) {
        // try the next candidate
      }
    }
    return null;
  }
}
