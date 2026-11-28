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
package org.springframework.salesforce.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * Tests for the limits model: the constructors and the additional-properties handling.
 *
 * @author Jared Ottley
 */
public class LimitsResultsTest {

  private final ObjectMapper json = new ObjectMapper();

  /**
   * The LimitResult-typed properties in declaration order, which is also the constructor's
   * parameter order.
   */
  private static List<Field> limitFields() {
    List<Field> fields = new ArrayList<>();
    for (Field f : LimitsResults.class.getDeclaredFields()) {
      if (f.getType() == LimitResult.class) {
        fields.add(f);
      }
    }
    return fields;
  }

  private static Method getterFor(Field field) throws NoSuchMethodException {
    String name = field.getName();
    return LimitsResults.class.getMethod(
        "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
  }

  private static LimitsResults construct(LimitResult[] args) throws Exception {
    Constructor<?> all = null;
    for (Constructor<?> c : LimitsResults.class.getConstructors()) {
      if (c.getParameterCount() == args.length) {
        all = c;
      }
    }
    return (LimitsResults) all.newInstance((Object[]) args);
  }

  @Test
  public void limitResultConstructorSetsMaxAndRemaining() {
    LimitResult limit = new LimitResult(15000, 14961);

    assertEquals(15000, limit.getMax());
    assertEquals(14961, limit.getRemaining());
    assertTrue(limit.getAdditionalProperties().isEmpty());
  }

  @Test
  public void abstractLimitsConstructorSetsMaxAndRemaining() {
    AbstractLimits limits = new AbstractLimits(20, 5);

    assertEquals(20, limits.getMax());
    assertEquals(5, limits.getRemaining());
  }

  @Test
  public void allArgumentsConstructorAssignsEachArgumentToItsOwnProperty() throws Exception {
    List<Field> fields = limitFields();
    assertEquals("expected a parameter for every limit", 32, fields.size());

    // Thirty-two parameters share one type, so a swapped assignment would compile and pass
    // unnoticed.
    LimitResult[] args = new LimitResult[fields.size()];
    for (int i = 0; i < args.length; i++) {
      args[i] = new LimitResult(i + 1, 1000 + i);
    }

    LimitsResults results = construct(args);

    for (int i = 0; i < args.length; i++) {
      assertSame(
          "argument " + i + " should be stored in " + fields.get(i).getName(),
          args[i],
          getterFor(fields.get(i)).invoke(results));
    }
  }

  @Test
  public void serializedLimitsUseSalesforceNamesAndSurviveARoundTrip() throws Exception {
    List<Field> fields = limitFields();
    LimitResult[] args = new LimitResult[fields.size()];
    for (int i = 0; i < args.length; i++) {
      args[i] = new LimitResult(i + 1, 1000 + i);
    }
    LimitsResults original = construct(args);

    JsonNode tree = json.readTree(json.writeValueAsString(original));
    LimitsResults copy = json.readValue(tree.toString(), LimitsResults.class);

    assertEquals(fields.size(), tree.size());
    for (int i = 0; i < args.length; i++) {
      LimitResult restored = (LimitResult) getterFor(fields.get(i)).invoke(copy);
      assertEquals(fields.get(i).getName(), i + 1, restored.getMax());
      assertEquals(fields.get(i).getName(), 1000 + i, restored.getRemaining());
    }
    assertEquals(1, tree.get("AnalyticsExternalDataSizeMB").get("Max").asInt());
    assertEquals(
        15000,
        json.readValue(
                "{\"DailyApiRequests\":{\"Max\":15000,\"Remaining\":14961}}", LimitsResults.class)
            .getDailyApiRequests()
            .getMax());
  }

  @Test
  public void connectedAppLimitsLandInTheLimitResultsAdditionalProperties() throws Exception {
    LimitsResults results =
        json.readValue(
            "{\"DailyApiRequests\":{\"Max\":15000,\"Remaining\":14961,\"Workbench\":{\"Max\":0,\"Remaining\":0}}}",
            LimitsResults.class);

    assertEquals(1, results.getDailyApiRequests().getAdditionalProperties().size());
    assertTrue(results.getDailyApiRequests().getAdditionalProperties().containsKey("Workbench"));
  }

  @Test
  public void additionalPropertiesRoundTripThroughLimitResult() throws Exception {
    LimitResult limit = new LimitResult(10, 4);
    limit.setAdditionalProperty("Workbench", java.util.Map.of("Max", 0, "Remaining", 0));

    JsonNode tree = json.readTree(json.writeValueAsString(limit));

    assertEquals(10, tree.get("Max").asInt());
    assertTrue(
        "additional properties are written flat, next to Max/Remaining", tree.has("Workbench"));
  }

  @Test
  public void limitsSalesforceAddsLaterAreKeptInAdditionalProperties() throws Exception {
    LimitsResults results =
        json.readValue(
            "{\"DailyApiRequests\":{\"Max\":1,\"Remaining\":1},"
                + "\"BrandNewLimit\":{\"Max\":2,\"Remaining\":3}}",
            LimitsResults.class);

    assertEquals(1, results.getDailyApiRequests().getMax());
    assertEquals(1, results.getAdditionalProperties().size());
    Map<?, ?> brandNew = (Map<?, ?>) results.getAdditionalProperties().get("BrandNewLimit");
    assertEquals(2, brandNew.get("Max"));
    assertEquals(3, brandNew.get("Remaining"));
  }

  @Test
  public void modeledLimitsAreNotDuplicatedIntoAdditionalProperties() throws Exception {
    LimitsResults results =
        json.readValue("{\"DailyApiRequests\":{\"Max\":1,\"Remaining\":1}}", LimitsResults.class);

    assertTrue(results.getAdditionalProperties().isEmpty());
  }

  @Test
  public void unexpectedlyShapedLimitDoesNotBreakTheRestOfTheResponse() throws Exception {
    LimitsResults results =
        json.readValue(
            "{\"DailyApiRequests\":{\"Max\":1,\"Remaining\":1},\"Odd\":42,\"AlsoOdd\":\"text\",\"Nothing\":null}",
            LimitsResults.class);

    assertEquals(1, results.getDailyApiRequests().getMax());
    assertEquals(42, results.getAdditionalProperties().get("Odd"));
    assertEquals("text", results.getAdditionalProperties().get("AlsoOdd"));
    assertTrue(results.getAdditionalProperties().containsKey("Nothing"));
  }

  @Test
  public void additionalLimitsAreWrittenBackOutFlat() throws Exception {
    LimitsResults results =
        json.readValue(
            "{\"DailyApiRequests\":{\"Max\":1,\"Remaining\":1},\"BrandNewLimit\":{\"Max\":2,\"Remaining\":3}}",
            LimitsResults.class);

    JsonNode tree = json.readTree(json.writeValueAsString(results));

    assertEquals(2, tree.get("BrandNewLimit").get("Max").asInt());
    assertEquals(1, tree.get("DailyApiRequests").get("Max").asInt());
  }

  @Test
  public void recordedLimitsResponseHasNothingLeftOver() throws Exception {
    // limits.json contains exactly the 32 modeled limits, so nothing should spill into
    // additionalProperties
    try (java.io.InputStream in = getClass().getResourceAsStream("/limits.json")) {
      assertTrue(json.readValue(in, LimitsResults.class).getAdditionalProperties().isEmpty());
    }
  }

  @Test
  public void additionalLimitIsReturnedAsALimitResult() throws Exception {
    LimitsResults results =
        json.readValue("{\"BrandNewLimit\":{\"Max\":2,\"Remaining\":3}}", LimitsResults.class);

    LimitResult limit = results.getAdditionalLimit("BrandNewLimit").orElseThrow();

    assertEquals(2, limit.getMax());
    assertEquals(3, limit.getRemaining());
  }

  @Test
  public void additionalLimitKeepsItsOwnConnectedAppLimits() throws Exception {
    LimitsResults results =
        json.readValue(
            "{\"BrandNewLimit\":{\"Max\":2,\"Remaining\":3,\"Workbench\":{\"Max\":0,\"Remaining\":0}}}",
            LimitsResults.class);

    assertTrue(
        results
            .getAdditionalLimit("BrandNewLimit")
            .orElseThrow()
            .getAdditionalProperties()
            .containsKey("Workbench"));
  }

  @Test
  public void additionalLimitIsEmptyWhenMissingOrNotShapedLikeALimit() throws Exception {
    LimitsResults results =
        json.readValue(
            "{\"Number\":42,\"Text\":\"x\",\"Nothing\":null,\"OnlyMax\":{\"Max\":1},"
                + "\"NotNumbers\":{\"Max\":\"a\",\"Remaining\":\"b\"},\"Unrelated\":{\"foo\":1}}",
            LimitsResults.class);

    assertTrue(results.getAdditionalLimit("Missing").isEmpty());
    assertTrue(results.getAdditionalLimit("Number").isEmpty());
    assertTrue(results.getAdditionalLimit("Text").isEmpty());
    assertTrue(results.getAdditionalLimit("Nothing").isEmpty());
    assertTrue(results.getAdditionalLimit("OnlyMax").isEmpty());
    assertTrue(results.getAdditionalLimit("NotNumbers").isEmpty());
    assertTrue(results.getAdditionalLimit("Unrelated").isEmpty());
  }

  @Test
  public void additionalLimitLookupIsNotSerialized() throws Exception {
    LimitsResults results =
        json.readValue("{\"BrandNewLimit\":{\"Max\":2,\"Remaining\":3}}", LimitsResults.class);

    JsonNode tree = json.readTree(json.writeValueAsString(results));

    assertEquals(1, tree.size());
    assertTrue(tree.has("BrandNewLimit"));
  }
}
