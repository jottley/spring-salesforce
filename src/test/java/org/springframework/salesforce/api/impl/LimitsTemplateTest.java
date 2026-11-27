/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-social-salesforce
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
package org.springframework.salesforce.api.impl;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.salesforce.api.LimitsResults;

/**
 * @author Jared Ottley
 */
public class LimitsTemplateTest extends AbstractSalesforceTest {

  @Test
  public void getLimits() {
    mockServer
        .expect(
            requestTo(
                "https://na7.salesforce.com/services/data/"
                    + salesforce.apiOperations().getVersion()
                    + "/limits"))
        .andExpect(method(GET))
        .andRespond(
            withStatus(HttpStatus.OK).body(loadResource("limits.json")).headers(responseHeaders));
    LimitsResults results = salesforce.limitsOperations().getLimits();

    assertEquals(9, results.getDailyApiRequests().getAdditionalProperties().size());
    assertEquals(500, results.getHourlySyncReportRuns().getMax());
    assertEquals(500, results.getHourlySyncReportRuns().getRemaining());
  }

  @Test
  public void SforceLimitInfoCheck() {
    mockServer
        .expect(
            requestTo(
                "https://na7.salesforce.com/services/data/"
                    + salesforce.apiOperations().getVersion()
                    + "/limits"))
        .andExpect(method(GET))
        .andRespond(
            withStatus(HttpStatus.OK).body(loadResource("limits.json")).headers(responseHeaders));
    salesforce.limitsOperations().getLimits();

    assertEquals(39, salesforce.limitsOperations().getDailyApiUsed());
    assertEquals(15000, salesforce.limitsOperations().getDailyApiLimit());
  }

  private LimitsOperationsTemplate limits() {
    return (LimitsOperationsTemplate) salesforce.limitsOperations();
  }

  private void assertUsage(int used, int max) {
    assertEquals(used, limits().getDailyApiUsed());
    assertEquals(max, limits().getDailyApiLimit());
  }

  @Test
  public void plainApiUsageHeaderIsParsed() {
    limits().setCurrentDailyApiLimits("api-usage=39/15000");

    assertUsage(39, 15000);
  }

  @Test
  public void perAppApiUsageAfterApiUsageDoesNotBreakParsing() {
    limits()
        .setCurrentDailyApiLimits("api-usage=17/5000; per-app-api-usage=17/250(application=MyApp)");

    assertUsage(17, 5000);
  }

  @Test
  public void perAppApiUsageBeforeApiUsageIsSkipped() {
    limits()
        .setCurrentDailyApiLimits("per-app-api-usage=1/2(application=MyApp); api-usage=17/5000");

    assertUsage(17, 5000);
  }

  @Test
  public void whitespaceAroundTheEntriesIsTolerated() {
    limits().setCurrentDailyApiLimits("  api-usage = 5 / 10  ;per-app-api-usage=1/2");

    assertUsage(5, 10);
  }

  @Test
  public void headerWithoutApiUsageKeepsPreviousValues() {
    limits().setCurrentDailyApiLimits("api-usage=39/15000");

    limits().setCurrentDailyApiLimits("per-app-api-usage=17/250(application=MyApp)");

    assertUsage(39, 15000);
  }

  @Test
  public void malformedHeadersAreIgnoredAndNeverThrow() {
    limits().setCurrentDailyApiLimits("api-usage=39/15000");

    for (String bad :
        new String[] {
          null,
          "",
          "   ",
          "api-usage",
          "api-usage=",
          "api-usage=5",
          "api-usage=a/b",
          "api-usage=1/2/3",
          "api-usage=/",
          "api-usage=1/",
          "api-usage=99999999999/1",
          ";;;",
          "=",
          "garbage"
        }) {
      limits().setCurrentDailyApiLimits(bad);
      assertUsage(39, 15000);
    }
  }

  @Test
  public void aSuccessfulCallIsNotFailedByAnUnexpectedLimitInfoHeader() {
    responseHeaders.set(
        "Sforce-Limit-Info", "api-usage=17/5000; per-app-api-usage=17/250(application=MyApp)");
    mockServer
        .expect(
            requestTo(
                "https://na7.salesforce.com/services/data/"
                    + salesforce.apiOperations().getVersion()
                    + "/limits"))
        .andExpect(method(GET))
        .andRespond(
            withStatus(HttpStatus.OK).body(loadResource("limits.json")).headers(responseHeaders));

    LimitsResults results = salesforce.limitsOperations().getLimits();

    assertEquals(500, results.getHourlySyncReportRuns().getMax());
    assertUsage(17, 5000);
  }

  @Test
  public void aSuccessfulCallIsNotFailedByAMalformedLimitInfoHeader() {
    responseHeaders.set("Sforce-Limit-Info", "api-usage=oops");
    mockServer
        .expect(
            requestTo(
                "https://na7.salesforce.com/services/data/"
                    + salesforce.apiOperations().getVersion()
                    + "/limits"))
        .andExpect(method(GET))
        .andRespond(
            withStatus(HttpStatus.OK).body(loadResource("limits.json")).headers(responseHeaders));

    assertEquals(500, salesforce.limitsOperations().getLimits().getHourlySyncReportRuns().getMax());
    assertUsage(0, 0);
  }
}
