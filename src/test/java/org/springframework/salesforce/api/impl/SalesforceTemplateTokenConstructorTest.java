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
package org.springframework.salesforce.api.impl;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;

/**
 * Tests the {@code SalesforceTemplate(accessToken, instanceUrl)} constructor that the canvas
 * support uses.
 *
 * @author Jared Ottley
 */
public class SalesforceTemplateTokenConstructorTest extends AbstractSalesforceTest {

  @Test
  public void storesTheInstanceUrl() {
    SalesforceTemplate template =
        new SalesforceTemplate("TOKEN", "https://canvas.my.salesforce.com");

    assertEquals("https://canvas.my.salesforce.com", template.getInstanceUrl());
  }

  @Test
  public void sendsTheTokenAsABearerTokenToTheInstanceUrl() {
    SalesforceTemplate template =
        new SalesforceTemplate("CANVAS_TOKEN", "https://canvas.my.salesforce.com");
    MockRestServiceServer server = MockRestServiceServer.createServer(template.getRestTemplate());
    server
        .expect(
            requestTo(
                "https://canvas.my.salesforce.com/services/data/"
                    + template.apiOperations().getVersion()
                    + "/limits"))
        .andExpect(method(GET))
        .andExpect(header("Authorization", "Bearer CANVAS_TOKEN"))
        .andRespond(
            withStatus(HttpStatus.OK).body(loadResource("limits.json")).headers(responseHeaders));

    template.limitsOperations().getLimits();

    server.verify();
  }

  @Test
  public void aNullInstanceUrlFallsBackToTheDefaultHost() {
    SalesforceTemplate template = new SalesforceTemplate("CANVAS_TOKEN", null);

    assertEquals(null, template.getInstanceUrl());
    assertEquals("https://na1.salesforce.com/services/data", template.getBaseUrl());
  }
}
