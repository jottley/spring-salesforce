/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-salesforce
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
package org.springframework.salesforce.canvas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

/**
 * Tests that the canvas model records read the JSON Salesforce actually sends (see {@code
 * canvas-request.json}), and that their defensive copies behave.
 *
 * @author Jared Ottley
 */
public class CanvasModelTest {

  private final ObjectMapper mapper = new ObjectMapper();

  private CanvasRequest read() throws Exception {
    return mapper.readValue(CanvasTestSupport.fixtureJson(), CanvasRequest.class);
  }

  @Test
  public void clientIsReadFromTheWireNames() throws Exception {
    CanvasClient client = read().client();

    assertEquals("00Dx0000000abcd!CANVAS_TOKEN", client.oauthToken());
    assertEquals("https://na1.salesforce.com", client.instanceUrl());
    assertEquals("https://na1.salesforce.com", client.targetOrigin());
    assertEquals("07Hx0000000abcdEAA", client.instanceId());
    assertNull(client.refreshToken());
  }

  @Test
  public void contextSectionsAreReadFromTheWireNames() throws Exception {
    CanvasContext context = read().canvasContext();

    assertNotNull("context", context);
    assertNotNull("context.user", context.userContext());
    assertNotNull("context.organization", context.orgContext());
    assertNotNull("context.links", context.linkContext());
    assertNotNull("context.environment", context.envContext());
  }

  @Test
  public void userContextIsRead() throws Exception {
    CanvasUserContext user = read().canvasContext().userContext();

    assertEquals("005x00000012Q9PAAU", user.userId());
    assertEquals("admin@example.com", user.userName());
    assertEquals("Jane", user.firstName());
    assertEquals("Doe", user.lastName());
    assertEquals("Jane Doe", user.fullName());
    assertEquals("admin@example.com", user.email());
    assertEquals("America/Los_Angeles", user.timeZone());
    assertEquals("00ex0000000abcdAAA", user.profileId());
    assertEquals("STANDARD", user.userType());
    assertEquals("USD", user.currencyISOCode());
    assertNull(user.roleId());
    assertEquals("https://na1.salesforce.com/profilephoto/photo", user.profilePhotoUrl());
    assertEquals("https://na1.salesforce.com/profilephoto/thumb", user.profileThumbnailUrl());
  }

  @Test
  public void localeAndLanguageAreParsedFromUnderscoreForm() throws Exception {
    CanvasUserContext user = read().canvasContext().userContext();

    assertEquals(Locale.US, user.locale());
    assertEquals(Locale.CANADA_FRENCH, user.language());
  }

  @Test
  public void accessibilityModeIsReadFromAccessibilityModeEnabled() throws Exception {
    assertTrue(read().canvasContext().userContext().accessibilityModeEnabled());
  }

  @Test
  public void organizationContextIsRead() throws Exception {
    CanvasOrganizationContext org = read().canvasContext().orgContext();

    assertEquals("00Dx0000000abcdEAA", org.organizationId());
    assertEquals("Acme Corp", org.name());
    assertTrue(org.multicurrencyEnabled());
    assertEquals("EUR", org.currencyIsoCode());
  }

  @Test
  public void linkContextIsRead() throws Exception {
    CanvasLinkContext links = read().canvasContext().linkContext();

    assertEquals("/services/data/v68.0/", links.restUrl());
    assertEquals("/services/data/v68.0/sobjects/", links.sobjectUrl());
    assertEquals("/services/data/v68.0/query/", links.queryUrl());
    assertEquals("/services/data/v68.0/search/", links.searchUrl());
    assertEquals("/services/data/v68.0/recent/", links.recentItemsUrl());
    assertEquals("/services/data/v68.0/chatter/feeds", links.chatterFeedsUrl());
    assertEquals("/services/data/v68.0/chatter/groups", links.chatterGroupsUrl());
    assertEquals("/services/data/v68.0/chatter/users", links.chatterUsersUrl());
    assertEquals("/services/data/v68.0/chatter/feed-items", links.chatterFeedItemsUrl());
    assertEquals("/services/Soap/c/68.0/00Dx0000000abcd", links.enterpriseUrl());
    assertEquals("/services/Soap/m/68.0/00Dx0000000abcd", links.metadataUrl());
    assertEquals("/services/Soap/u/68.0/00Dx0000000abcd", links.partnerUrl());
  }

  @Test
  public void userProfileUrlIsReadFromUserUrl() throws Exception {
    assertEquals("/005x00000012Q9PAAU", read().canvasContext().linkContext().userProfileUrl());
  }

  @Test
  public void environmentContextIsRead() throws Exception {
    CanvasEnvironmentContext env = read().canvasContext().envContext();

    assertEquals("Chatter", env.displayLocation());
    assertEquals("https://na1.salesforce.com/home/home.jsp", env.locationUrl());
    assertEquals("Theme3", env.uiTheme());
    assertEquals("800px", env.dimensions().width());
    assertEquals("900px", env.dimensions().height());
    assertEquals("1000px", env.dimensions().maxWidth());
    assertEquals("2000px", env.dimensions().maxHeight());
    assertEquals("1000px", env.dimensions().clientWidth());
    assertEquals("1000px", env.dimensions().clientHeight());
    assertEquals("v68.0", env.version().api());
    assertEquals("WINTER", env.version().season());
    assertEquals("value1", env.parameters().get("customParam"));
  }

  @Test
  public void unknownPropertiesAreIgnored() throws Exception {
    CanvasRequest request =
        mapper.readValue(
            "{\"userId\":\"005\",\"somethingNew\":{\"a\":1},\"client\":{\"extra\":true}}",
            CanvasRequest.class);

    assertEquals("005", request.userId());
    assertNotNull(request.client());
  }

  @Test
  public void applicationContextIsReadFromTheRequest() throws Exception {
    CanvasApplicationContext application = read().canvasContext().applicationContext();

    assertNotNull("context.application", application);
    assertEquals("My Canvas App", application.name());
    assertEquals("My_Canvas_App", application.developerName());
    assertEquals("06Px0000000abcdEAA", application.applicationId());
    assertEquals("SIGNED_REQUEST", application.authType());
    assertEquals("https://myapp.example.com/canvas", application.canvasUrl());
    assertEquals("09Hx0000000abcdEAA", application.referenceId());
    assertEquals("1.0", application.version());
    assertEquals("", application.namespace());
    assertEquals(List.of("HideShare", "HideHeader"), application.options());
  }

  @Test
  public void aRequestWithoutAnApplicationSectionStillReads() throws Exception {
    CanvasRequest request =
        mapper.readValue("{\"context\":{\"user\":{\"userId\":\"005\"}}}", CanvasRequest.class);

    assertNull(request.canvasContext().applicationContext());
    assertEquals("005", request.canvasContext().userContext().userId());
  }

  @Test
  public void issuedAtHoldsValuesBeyondAnInt() throws Exception {
    // Epoch milliseconds do not fit in an int; epoch seconds do until 2038.
    CanvasRequest millis = mapper.readValue("{\"issuedAt\":1700000000000}", CanvasRequest.class);
    CanvasRequest seconds = mapper.readValue("{\"issuedAt\":1700000000}", CanvasRequest.class);

    assertEquals(Long.valueOf(1700000000000L), millis.issuedAt());
    assertEquals(Long.valueOf(1700000000L), seconds.issuedAt());
  }

  @Test
  public void nullCollectionsBecomeEmptyOnes() {
    assertTrue(
        new CanvasApplicationContext(
                null, null, null, null, null, null, null, null, null, null, false)
            .options()
            .isEmpty());
    assertTrue(
        new CanvasEnvironmentContext(null, null, null, null, null, null, null, null, null)
            .parameters()
            .isEmpty());
    assertTrue(new CanvasRecordContext(null, null, null).fields().isEmpty());
  }

  @Test
  public void collectionsAreCopiedAndImmutable() {
    List<String> options = new ArrayList<>(List.of("HideShare"));
    CanvasApplicationContext application =
        new CanvasApplicationContext(
            null, null, null, null, null, null, options, null, null, null, false);
    options.add("HideHeader");

    assertEquals(List.of("HideShare"), application.options());
    assertThrows(UnsupportedOperationException.class, () -> application.options().add("x"));

    Map<String, Object> parameters = new HashMap<>(Map.of("a", "1"));
    CanvasEnvironmentContext env =
        new CanvasEnvironmentContext(null, null, null, null, null, null, null, null, parameters);
    parameters.put("b", "2");

    assertEquals(Map.of("a", "1"), env.parameters());
    assertThrows(UnsupportedOperationException.class, () -> env.parameters().put("c", "3"));
  }

  private static final String RECORD_JSON =
      """
      {
        "attributes": {"type": "Account", "url": "/services/data/v68.0/sobjects/Account/001x"},
        "Id": "001x",
        "Name": "Acme",
        "Phone": null,
        "NumberOfEmployees": 120,
        "IsActive__c": true,
        "Custom_Field__c": "custom",
        "BillingAddress": {"city": "Seattle", "street": "1 Main St"},
        "Tags__c": ["a", "b"]
      }
      """;

  @Test
  public void recordContextReadsIdAndAttributes() throws Exception {
    CanvasRecordContext record = mapper.readValue(RECORD_JSON, CanvasRecordContext.class);

    assertEquals("001x", record.Id());
    assertEquals("Account", record.attributes().type());
    assertEquals("/services/data/v68.0/sobjects/Account/001x", record.attributes().url());
  }

  @Test
  public void everyOtherPropertyIsCollectedIntoFields() throws Exception {
    CanvasRecordContext record = mapper.readValue(RECORD_JSON, CanvasRecordContext.class);

    assertEquals(
        Set.of(
            "Name",
            "Phone",
            "NumberOfEmployees",
            "IsActive__c",
            "Custom_Field__c",
            "BillingAddress",
            "Tags__c"),
        record.fields().keySet());
    assertEquals("Acme", record.fields().get("Name"));
    assertEquals(120, record.fields().get("NumberOfEmployees"));
    assertEquals(true, record.fields().get("IsActive__c"));
    assertEquals("custom", record.fields().get("Custom_Field__c"));
    assertEquals(List.of("a", "b"), record.fields().get("Tags__c"));
    assertEquals(
        Map.of("city", "Seattle", "street", "1 Main St"), record.fields().get("BillingAddress"));
  }

  @Test
  public void idAndAttributesAreNotRepeatedInFields() throws Exception {
    CanvasRecordContext record = mapper.readValue(RECORD_JSON, CanvasRecordContext.class);

    assertFalse(record.fields().containsKey("Id"));
    assertFalse(record.fields().containsKey("attributes"));
  }

  @Test
  public void aNullFieldValueIsKept() throws Exception {
    CanvasRecordContext record = mapper.readValue(RECORD_JSON, CanvasRecordContext.class);

    assertTrue(record.fields().containsKey("Phone"));
    assertNull(record.fields().get("Phone"));
  }

  @Test
  public void aPropertyNamedFieldsIsJustAnotherField() throws Exception {
    CanvasRecordContext record =
        mapper.readValue(
            "{\"Id\":\"001x\",\"fields\":\"a custom field\"}", CanvasRecordContext.class);

    assertEquals("a custom field", record.fields().get("fields"));
  }

  @Test
  public void aRecordWithOnlyIdHasNoFields() throws Exception {
    CanvasRecordContext record = mapper.readValue("{\"Id\":\"001x\"}", CanvasRecordContext.class);

    assertEquals("001x", record.Id());
    assertTrue(record.fields().isEmpty());
  }

  @Test
  public void theFieldsMapCannotBeChanged() throws Exception {
    CanvasRecordContext record = mapper.readValue(RECORD_JSON, CanvasRecordContext.class);

    assertThrows(UnsupportedOperationException.class, () -> record.fields().put("x", "y"));
    assertThrows(UnsupportedOperationException.class, () -> record.fields().remove("Name"));
  }

  @Test
  public void theFieldsMapIsACopyOfWhatWasPassedIn() {
    Map<String, Object> source = new HashMap<>();
    source.put("Name", "Acme");
    source.put("Phone", null);

    CanvasRecordContext record = new CanvasRecordContext("001x", null, source);
    source.put("Later", "ignored");

    assertEquals(2, record.fields().size());
    assertFalse(record.fields().containsKey("Later"));
  }

  @Test
  public void aRecordSerializesFlatAndReadsBackTheSame() throws Exception {
    CanvasRecordContext record = mapper.readValue(RECORD_JSON, CanvasRecordContext.class);

    String json = mapper.writeValueAsString(record);
    com.fasterxml.jackson.databind.JsonNode tree = mapper.readTree(json);

    assertEquals("Acme", tree.get("Name").asText());
    assertEquals("001x", tree.get("Id").asText());
    assertFalse("fields must not be nested", tree.has("fields"));
    assertFalse("internal name must not leak", tree.has("__fields__"));
    assertEquals(record, mapper.readValue(json, CanvasRecordContext.class));
  }

  @Test
  public void contextToStringToleratesMissingSections() {
    CanvasContext empty = new CanvasContext(null, null, null, null, null);

    String text = empty.toString();

    assertTrue(text.contains("Canvas Context:"));
    assertTrue(text.contains("null"));
    assertFalse(text.isBlank());
  }

  @Test
  public void contextToStringIncludesEachSection() {
    CanvasContext context =
        new CanvasContext(
            new CanvasUserContext(
                "005", "u@x.com", null, null, null, null, null, null, null, null, null, null, null,
                false, null, null, null, null, null, false),
            new CanvasOrganizationContext("00D", "Acme", false, "USD", null),
            new CanvasLinkContext(
                null, null, null, "/rest", null, null, null, null, null, null, null, null, null,
                null),
            new CanvasEnvironmentContext("Chatter", null, null, null, null, null, null, null, null),
            new CanvasApplicationContext(
                null, null, null, null, "My Canvas App", null, null, null, null, null, false));

    String text = context.toString();

    assertTrue(text, text.contains("005"));
    assertTrue(text, text.contains("Acme"));
    assertTrue(text, text.contains("/rest"));
    assertTrue(text, text.contains("Chatter"));
    assertTrue(text, text.contains("My Canvas App"));
  }

  private CanvasRequest readReal(String fixture) throws Exception {
    String json =
        new org.springframework.core.io.ClassPathResource(fixture)
            .getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
    return mapper.readValue(json, CanvasRequest.class);
  }

  @Test
  public void aRealAuraRequestIsRead() throws Exception {
    CanvasRequest request = readReal("canvas-request-aura.json");
    CanvasContext context = request.canvasContext();

    assertEquals(Long.valueOf(595006185L), request.issuedAt());
    assertEquals("[redacted]", request.client().oauthToken());
    assertEquals(Locale.US, context.userContext().locale());
    assertFalse(context.userContext().accessibilityModeEnabled());
    assertTrue(context.userContext().isDefaultNetwork());
    assertEquals("https://example-dev-ed.my.salesforce.com", context.linkContext().loginUrl());
    assertEquals("/005x00000012Q9PAAU", context.linkContext().userProfileUrl());
    assertEquals("None", context.applicationContext().samlInitiationMethod());
    assertFalse(context.applicationContext().isInstalledPersonalApp());
    assertTrue(context.applicationContext().options().isEmpty());
    assertEquals("USD", context.orgContext().currencyIsoCode());
    assertNull(context.orgContext().namespacePrefix());

    CanvasEnvironmentContext env = context.envContext();
    assertEquals("Aura", env.displayLocation());
    assertEquals("WINTER", env.version().season());
    assertEquals("1000px", env.dimensions().width());
    assertEquals(Map.of("recordId", "001x0000000abcdAAA", "type", "Account"), env.parameters());
    assertNull(env.recordContext().Id());
    assertTrue(env.recordContext().fields().isEmpty());
  }

  @Test
  public void aRealVisualforceRequestKeepsEveryRecordField() throws Exception {
    CanvasEnvironmentContext env =
        readReal("canvas-request-visualforce.json").canvasContext().envContext();
    CanvasRecordContext record = env.recordContext();

    assertEquals("Visualforce", env.displayLocation());
    assertTrue(env.parameters().isEmpty());
    assertEquals("001x0000000abcdAAA", record.Id());
    assertEquals("Account", record.attributes().type());
    assertEquals("Example Account", record.fields().get("Name"));
    assertEquals(Boolean.TRUE, record.fields().get("IsCustomerPortal"));
    assertTrue(record.fields().containsKey("CustomerPriority__c"));
    assertNull(record.fields().get("CustomerPriority__c"));
    assertNull(record.fields().get("ShippingAddress"));
    assertEquals("VA", ((Map<?, ?>) record.fields().get("BillingAddress")).get("state"));
    assertFalse(record.fields().containsKey("Id"));
    assertFalse(record.fields().containsKey("attributes"));
  }

  @Test
  public void environmentParametersMayContainNullValues() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("a", null);

    CanvasEnvironmentContext env =
        new CanvasEnvironmentContext(null, null, null, null, null, null, null, null, parameters);

    assertTrue(env.parameters().containsKey("a"));
  }

  @Test
  public void toStringMasksTheTokens() throws Exception {
    CanvasClient client =
        new CanvasClient("SECRET-ACCESS", "SECRET-REFRESH", "id", "inst", "origin", "url");

    assertFalse(client.toString().contains("SECRET-ACCESS"));
    assertFalse(client.toString().contains("SECRET-REFRESH"));
    assertTrue(client.toString().contains("[redacted]"));
    assertTrue(client.toString().contains("instanceUrl=url"));
    // the real values are still available to code
    assertEquals("SECRET-ACCESS", client.oauthToken());
    assertEquals("SECRET-REFRESH", client.refreshToken());

    assertFalse(read().toString().contains("CANVAS_TOKEN"));
  }

  @Test
  public void toStringKeepsAbsentTokensVisible() {
    assertTrue(
        new CanvasClient(null, null, null, null, null, null)
            .toString()
            .contains("oauthToken=null, refreshToken=null"));
  }
}
