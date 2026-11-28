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
package org.springframework.salesforce.connect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.springframework.salesforce.canvas.CanvasTestSupport.SECRET;
import static org.springframework.salesforce.canvas.CanvasTestSupport.fixtureJson;
import static org.springframework.salesforce.canvas.CanvasTestSupport.sign;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.api.impl.SalesforceTemplate;

/**
 * Tests for {@link SalesforceCanvasServiceProvider} and {@link SalesforceCanvasConnectionFactory}.
 *
 * @author Jared Ottley
 */
public class SalesforceCanvasServiceProviderTest {

  @Test
  public void decodesAndExposesTheRequest() throws Exception {
    SalesforceCanvasServiceProvider provider =
        new SalesforceCanvasServiceProvider(sign(fixtureJson()), SECRET);

    assertEquals("005x00000012Q9PAAU", provider.getCanvasRequest().userId());
    assertEquals(
        "Acme Corp",
        new ObjectMapper()
            .readTree(provider.getCanvasRequestJson())
            .get("context")
            .get("organization")
            .get("name")
            .asText());
  }

  @Test
  public void aBadSignatureIsRejectedByTheConstructor() {
    assertThrows(
        SecurityException.class,
        () -> new SalesforceCanvasServiceProvider(sign(fixtureJson(), "wrong"), SECRET));
  }

  @Test
  public void getApiUsesTheInstanceUrlFromTheRequest() {
    Salesforce api = new SalesforceCanvasServiceProvider(sign(fixtureJson()), SECRET).getApi();

    assertTrue(api instanceof SalesforceTemplate);
    assertEquals("https://na1.salesforce.com", ((SalesforceTemplate) api).getInstanceUrl());
  }

  @Test
  public void factoryExposesTheProviderAndCreatesTheApi() {
    SalesforceCanvasConnectionFactory factory =
        new SalesforceCanvasConnectionFactory(sign(fixtureJson()), SECRET);

    SalesforceCanvasServiceProvider provider = factory.getServiceProvider();

    assertNotNull(provider);
    assertSame(provider, factory.getServiceProvider());
    assertNotNull(factory.createApi());
    assertEquals("005x00000012Q9PAAU", provider.getCanvasRequest().userId());
  }

  @Test
  public void factoryRejectsABadSignature() {
    assertThrows(
        SecurityException.class,
        () -> new SalesforceCanvasConnectionFactory(sign(fixtureJson(), "wrong"), SECRET));
  }
}
