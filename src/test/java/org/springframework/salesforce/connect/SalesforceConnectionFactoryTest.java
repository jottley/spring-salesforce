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
package org.springframework.salesforce.connect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.api.impl.SalesforceTemplate;

/**
 * @author Jared Ottley
 */
@SuppressWarnings("deprecation")
public class SalesforceConnectionFactoryTest {

  private static final String PROD_TOKEN = "https://login.salesforce.com/services/oauth2/token";
  private static final String SANDBOX_TOKEN = "https://test.salesforce.com/services/oauth2/token";

  private static String tokenUrl(SalesforceConnectionFactory f) {
    return f.getServiceProvider().getOAuth2Client().getAccessTokenUrl();
  }

  @Test
  public void defaultsToProduction() {
    SalesforceConnectionFactory f = new SalesforceConnectionFactory("id", "secret");
    assertEquals(PROD_TOKEN, tokenUrl(f));
    assertNotNull(f.getProfileMapper());
  }

  @Test
  public void sandboxFlagSelectsSandbox() {
    assertEquals(SANDBOX_TOKEN, tokenUrl(new SalesforceConnectionFactory("id", "secret", true)));
    assertEquals(PROD_TOKEN, tokenUrl(new SalesforceConnectionFactory("id", "secret", false)));
  }

  @Test
  public void instanceUrlConstructors() {
    assertEquals(
        PROD_TOKEN,
        tokenUrl(new SalesforceConnectionFactory("id", "secret", "https://na7.salesforce.com")));
    assertEquals(
        SANDBOX_TOKEN,
        tokenUrl(
            new SalesforceConnectionFactory("id", "secret", "https://na7.salesforce.com", true)));
  }

  @Test
  public void deprecatedUrlConstructors() {
    assertEquals(
        "https://t",
        tokenUrl(new SalesforceConnectionFactory("id", "secret", "https://a", "https://t")));
    assertEquals(
        "https://t",
        tokenUrl(
            new SalesforceConnectionFactory(
                "id", "secret", "https://a", "https://t", "https://i")));
    assertEquals(
        "https://t",
        tokenUrl(
            new SalesforceConnectionFactory(
                "id", "secret", "https://a", "https://t", "https://i", "https://g")));
  }

  @Test
  public void createApiBuildsTemplate() {
    SalesforceConnectionFactory f = new SalesforceConnectionFactory("id", "secret");
    Salesforce api = f.createApi("tok");
    assertEquals(SalesforceTemplate.class, api.getClass());
    Salesforce withUrl = f.createApi("tok", "https://na7.salesforce.com");
    assertEquals(
        "https://na7.salesforce.com/services/data", ((SalesforceTemplate) withUrl).getBaseUrl());
  }

  @Test
  public void exposesTheSameProviderInstance() {
    SalesforceConnectionFactory f = new SalesforceConnectionFactory("id", "secret");
    assertSame(f.getServiceProvider(), f.getServiceProvider());
  }
}
