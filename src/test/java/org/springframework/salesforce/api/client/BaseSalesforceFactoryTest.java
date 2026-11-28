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
package org.springframework.salesforce.api.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.salesforce.api.InvalidAuthorizationException;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.api.impl.SalesforceTemplate;
import org.springframework.salesforce.client.BaseSalesforceFactory;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

/**
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class BaseSalesforceFactoryTest {

  @Test
  public void testClientLogin() {
    RestTemplate restTemplate = new RestTemplate();
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setContentType(MediaType.APPLICATION_JSON);

    mockServer
        .expect(requestTo("https://login.salesforce.com/services/oauth2/token"))
        .andExpect(method(POST))
        .andExpect(
            content()
                .string(
                    "grant_type=password&client_id=client-id&client_secret=client-secret&username=my-username&password=my-passwordsecurity-token"))
        .andRespond(
            withStatus(HttpStatus.OK)
                .body(loadResource("client-token.json"))
                .headers(responseHeaders));

    Salesforce template =
        new BaseSalesforceFactory("client-id", "client-secret", restTemplate)
            .create("my-username", "my-password", "security-token");
    assertNotNull(template);
  }

  private Salesforce login(String tokenJson, String securityToken, String expectedPassword) {
    RestTemplate restTemplate = new RestTemplate();
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer
        .expect(requestTo("https://login.salesforce.com/services/oauth2/token"))
        .andExpect(
            content()
                .string(
                    "grant_type=password&client_id=id&client_secret=secret&username=user&password="
                        + expectedPassword))
        .andRespond(
            withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(tokenJson));
    Salesforce api =
        new BaseSalesforceFactory("id", "secret", restTemplate).create("user", "pw", securityToken);
    mockServer.verify();
    return api;
  }

  @Test
  public void nullSecurityTokenIsOmittedFromPassword() {
    login("{\"access_token\":\"t\"}", null, "pw");
  }

  @Test
  public void instanceUrlFromTokenResponseIsApplied() {
    Salesforce api =
        login("{\"access_token\":\"t\",\"instance_url\":\"https://na9.salesforce.com\"}", "", "pw");
    assertEquals(
        "https://na9.salesforce.com/services/data", ((SalesforceTemplate) api).getBaseUrl());
  }

  @Test
  public void missingInstanceUrlKeepsTemplateDefault() {
    Salesforce api = login("{\"access_token\":\"t\"}", "", "pw");
    assertEquals(
        "https://na1.salesforce.com/services/data", ((SalesforceTemplate) api).getBaseUrl());
  }

  @Test
  public void customAuthorizeUrlIsUsedForLogin() {
    RestTemplate restTemplate = new RestTemplate();
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer
        .expect(requestTo("https://test.salesforce.com/services/oauth2/token"))
        .andRespond(
            withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"access_token\":\"t\"}"));
    BaseSalesforceFactory factory = new BaseSalesforceFactory("id", "secret", restTemplate);
    factory.setAuthorizeUrl("https://test.salesforce.com/services/oauth2/token");

    factory.create("user", "pw", null);

    mockServer.verify();
    assertEquals("https://test.salesforce.com/services/oauth2/token", factory.getAuthorizeUrl());
  }

  @Test
  public void authorizeUrlDefaultsToProduction() {
    BaseSalesforceFactory factory = new BaseSalesforceFactory("id", "secret");
    assertEquals("https://login.salesforce.com/services/oauth2/token", factory.getAuthorizeUrl());
    factory.setAuthorizeUrl(null);
    assertEquals("https://login.salesforce.com/services/oauth2/token", factory.getAuthorizeUrl());
  }

  @Test
  public void nullAuthorizeUrlFallsBackToTheProductionTokenEndpointForLogin() {
    RestTemplate restTemplate = new RestTemplate();
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer
        .expect(requestTo("https://login.salesforce.com/services/oauth2/token"))
        .andRespond(
            withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"access_token\":\"t\"}"));
    BaseSalesforceFactory factory = new BaseSalesforceFactory("id", "secret", restTemplate);
    factory.setAuthorizeUrl(null);

    assertNotNull(factory.create("user", "pw", null));
    mockServer.verify();
  }

  private InvalidAuthorizationException loginFailure(
      org.springframework.test.web.client.ResponseCreator response) {
    RestTemplate restTemplate = new RestTemplate();
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer
        .expect(requestTo("https://login.salesforce.com/services/oauth2/token"))
        .andRespond(response);
    BaseSalesforceFactory factory = new BaseSalesforceFactory("id", "secret", restTemplate);
    return assertThrows(
        InvalidAuthorizationException.class, () -> factory.create("user", "pw", null));
  }

  @Test
  public void emptyTokenResponseIsAnInvalidAuthorization() {
    InvalidAuthorizationException e = loginFailure(withNoContent());
    assertEquals("The token response contained no access token.", e.getMessage());
  }

  @Test
  public void tokenResponseWithoutAnAccessTokenIsAnInvalidAuthorization() {
    loginFailure(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{}"));
    loginFailure(
        withStatus(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"instance_url\":\"https://na9.salesforce.com\"}"));
  }

  @Test
  public void blankAccessTokenIsAnInvalidAuthorization() {
    loginFailure(
        withStatus(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"access_token\":\"  \"}"));
  }

  protected Resource loadResource(String name) {
    return new ClassPathResource("/" + name, getClass());
  }
}
