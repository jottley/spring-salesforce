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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;

public class SalesforceOAuth2ClientTest {

  private static final String TOKEN_URL =
      "https://mydomain.my.salesforce.com/services/oauth2/token";

  private SalesforceOAuth2Client client;
  private MockRestServiceServer server;

  @Before
  public void setUp() {
    client =
        new SalesforceOAuth2Client(
            "id",
            "secret",
            "https://mydomain.my.salesforce.com/services/oauth2/authorize",
            TOKEN_URL);
    server = MockRestServiceServer.createServer(client.getRestTemplate());
  }

  @Test
  public void clientCredentialsAccessPostsGrantAndReadsToken() {
    server
        .expect(requestTo(TOKEN_URL))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(
            content().string("grant_type=client_credentials&client_id=id&client_secret=secret"))
        .andRespond(
            withSuccess(
                "{\"access_token\":\"tok\",\"instance_url\":\"https://mydomain.my.salesforce.com\","
                    + "\"token_type\":\"Bearer\",\"scope\":\"api\"}",
                MediaType.APPLICATION_JSON));

    AccessGrant grant = client.clientCredentialsAccess(null);

    server.verify();
    assertEquals("tok", grant.getAccessToken());
    assertNull(grant.getRefreshToken());
    assertEquals("https://mydomain.my.salesforce.com", client.getInstanceUrl());
  }

  @Test
  public void clientCredentialsAccessSendsScopeWhenGiven() {
    server
        .expect(requestTo(TOKEN_URL))
        .andExpect(
            content()
                .string(
                    "grant_type=client_credentials&client_id=id&client_secret=secret&scope=api"))
        .andRespond(withSuccess("{\"access_token\":\"tok\"}", MediaType.APPLICATION_JSON));

    client.clientCredentialsAccess("api");

    server.verify();
  }

  @Test
  public void buildAuthorizeUrlIncludesOnlyGivenParameters() {
    assertEquals(
        "https://mydomain.my.salesforce.com/services/oauth2/authorize?response_type=code&client_id=id",
        client.buildAuthorizeUrl(null, null, null));
    assertEquals(
        "https://mydomain.my.salesforce.com/services/oauth2/authorize?response_type=code&client_id=id"
            + "&redirect_uri=https://app/cb&scope=api&state=xyz",
        client.buildAuthorizeUrl("https://app/cb", "api", "xyz"));
  }

  private static final String AUTHORIZE =
      "https://mydomain.my.salesforce.com/services/oauth2/authorize?response_type=code&client_id=id";

  @Test
  public void buildAuthorizeUrlEncodesCharactersThatWouldBreakTheUrl() {
    assertEquals(
        AUTHORIZE + "&scope=api%20refresh_token",
        client.buildAuthorizeUrl(null, "api refresh_token", null));
    assertEquals(
        AUTHORIZE + "&state=a%26b%23c%22d", client.buildAuthorizeUrl(null, null, "a&b#c\"d"));
    assertEquals(AUTHORIZE + "&state=caf%C3%A9", client.buildAuthorizeUrl(null, null, "caf\u00e9"));
  }

  @Test
  public void buildAuthorizeUrlLeavesAlreadyEncodedValuesAlone() {
    // Callers that followed the old Javadoc and encoded their own values must not be encoded twice
    assertEquals(
        AUTHORIZE + "&scope=api%20refresh_token",
        client.buildAuthorizeUrl(null, "api%20refresh_token", null));
    assertEquals(
        AUTHORIZE + "&scope=api+refresh_token",
        client.buildAuthorizeUrl(null, "api+refresh_token", null));
    assertEquals(
        AUTHORIZE + "&redirect_uri=https%3A%2F%2Fapp%2Fcb",
        client.buildAuthorizeUrl("https%3A%2F%2Fapp%2Fcb", null, null));
  }

  @Test
  public void buildAuthorizeUrlEncodesAPercentSignThatIsNotAnEscape() {
    assertEquals(AUTHORIZE + "&state=100%25", client.buildAuthorizeUrl(null, null, "100%"));
    assertEquals(AUTHORIZE + "&state=%25zz", client.buildAuthorizeUrl(null, null, "%zz"));
  }

  @Test
  public void buildAuthorizeUrlKeepsPlainUrlsReadable() {
    assertEquals(
        AUTHORIZE + "&redirect_uri=https://app/cb?x=1",
        client.buildAuthorizeUrl("https://app/cb?x=1", null, null));
  }

  @Test
  public void exchangeForAccessPostsAuthorizationCode() {
    server
        .expect(requestTo(TOKEN_URL))
        .andExpect(
            content()
                .string(
                    "grant_type=authorization_code&code=abc&client_id=id&client_secret=secret"
                        + "&redirect_uri=https%3A%2F%2Fapp%2Fcb"))
        .andRespond(
            withSuccess(
                "{\"access_token\":\"tok\",\"refresh_token\":\"ref\"}",
                MediaType.APPLICATION_JSON));

    AccessGrant grant = client.exchangeForAccess("abc", "https://app/cb");

    server.verify();
    assertEquals("ref", grant.getRefreshToken());
    assertNull(client.getInstanceUrl());
  }

  @Test
  public void exchangeForAccessOmitsRedirectUriWhenNull() {
    server
        .expect(requestTo(TOKEN_URL))
        .andExpect(
            content()
                .string("grant_type=authorization_code&code=abc&client_id=id&client_secret=secret"))
        .andRespond(withSuccess("{\"access_token\":\"tok\"}", MediaType.APPLICATION_JSON));

    client.exchangeForAccess("abc", null);

    server.verify();
  }

  @Test
  public void refreshAccessPostsRefreshToken() {
    server
        .expect(requestTo(TOKEN_URL))
        .andExpect(
            content()
                .string(
                    "grant_type=refresh_token&refresh_token=ref&client_id=id&client_secret=secret"))
        .andRespond(withSuccess("{\"access_token\":\"new\"}", MediaType.APPLICATION_JSON));

    assertEquals("new", client.refreshAccess("ref").getAccessToken());
    server.verify();
  }

  private static final String VERIFIER = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";
  private static final String CHALLENGE = "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM";

  @Test
  public void buildAuthorizeUrlAddsThePkceChallenge() {
    assertEquals(
        AUTHORIZE
            + "&redirect_uri=https://app/cb&scope=api&state=xyz"
            + "&code_challenge="
            + CHALLENGE
            + "&code_challenge_method=S256",
        client.buildAuthorizeUrl("https://app/cb", "api", "xyz", CHALLENGE));
  }

  @Test
  public void buildAuthorizeUrlWithANullChallengeHasNoPkceParameters() {
    assertEquals(
        client.buildAuthorizeUrl("https://app/cb", "api", "xyz"),
        client.buildAuthorizeUrl("https://app/cb", "api", "xyz", null));
  }

  @Test
  public void aBlankChallengeOrVerifierIsTreatedAsNone() {
    assertEquals(
        client.buildAuthorizeUrl("https://app/cb", "api", "xyz"),
        client.buildAuthorizeUrl("https://app/cb", "api", "xyz", " "));
    server
        .expect(requestTo(TOKEN_URL))
        .andExpect(
            content()
                .string("grant_type=authorization_code&code=abc&client_id=id&client_secret=secret"))
        .andRespond(withSuccess("{\"access_token\":\"tok\"}", MediaType.APPLICATION_JSON));

    client.exchangeForAccess("abc", null, "");
    server.verify();
  }

  @Test
  public void exchangeForAccessSendsTheCodeVerifier() {
    server
        .expect(requestTo(TOKEN_URL))
        .andExpect(
            content()
                .string(
                    "grant_type=authorization_code&code=abc&client_id=id&client_secret=secret"
                        + "&redirect_uri=https%3A%2F%2Fapp%2Fcb&code_verifier="
                        + VERIFIER))
        .andRespond(withSuccess("{\"access_token\":\"tok\"}", MediaType.APPLICATION_JSON));

    assertEquals(
        "tok", client.exchangeForAccess("abc", "https://app/cb", VERIFIER).getAccessToken());
    server.verify();
  }

  @Test
  public void exchangeForAccessWithoutAVerifierIsUnchanged() {
    server
        .expect(requestTo(TOKEN_URL))
        .andExpect(
            content()
                .string("grant_type=authorization_code&code=abc&client_id=id&client_secret=secret"))
        .andRespond(withSuccess("{\"access_token\":\"tok\"}", MediaType.APPLICATION_JSON));

    client.exchangeForAccess("abc", null);
    server.verify();
  }

  @Test
  public void aPublicClientSendsNoSecret() {
    SalesforceOAuth2Client publicClient =
        new SalesforceOAuth2Client("id", null, "https://login/authorize", TOKEN_URL);
    MockRestServiceServer publicServer =
        MockRestServiceServer.createServer(publicClient.getRestTemplate());
    publicServer
        .expect(requestTo(TOKEN_URL))
        .andExpect(
            content()
                .string(
                    "grant_type=authorization_code&code=abc&client_id=id&code_verifier="
                        + VERIFIER))
        .andRespond(withSuccess("{\"access_token\":\"tok\"}", MediaType.APPLICATION_JSON));
    publicServer
        .expect(requestTo(TOKEN_URL))
        .andExpect(content().string("grant_type=refresh_token&refresh_token=ref&client_id=id"))
        .andRespond(withSuccess("{\"access_token\":\"new\"}", MediaType.APPLICATION_JSON));

    publicClient.exchangeForAccess("abc", null, VERIFIER);
    publicClient.refreshAccess("ref");
    publicServer.verify();
  }

  @Test
  public void aBlankSecretIsTreatedAsNoSecret() {
    SalesforceOAuth2Client blank =
        new SalesforceOAuth2Client("id", "  ", "https://login/authorize", TOKEN_URL);
    MockRestServiceServer blankServer = MockRestServiceServer.createServer(blank.getRestTemplate());
    blankServer
        .expect(requestTo(TOKEN_URL))
        .andExpect(content().string("grant_type=refresh_token&refresh_token=ref&client_id=id"))
        .andRespond(withSuccess("{\"access_token\":\"new\"}", MediaType.APPLICATION_JSON));

    blank.refreshAccess("ref");
    blankServer.verify();
  }

  private static final String REVOKE_URL =
      "https://mydomain.my.salesforce.com/services/oauth2/revoke";

  @Test
  public void revokeUrlIsDerivedFromTheTokenUrl() {
    assertEquals(REVOKE_URL, client.getRevokeUrl());
    assertEquals(
        "https://login.salesforce.com/services/oauth2/revoke",
        new SalesforceServiceProvider("id", "secret").getOAuth2Client().getRevokeUrl());
    assertEquals(
        "https://test.salesforce.com/services/oauth2/revoke",
        new SalesforceServiceProvider("id", "secret", true).getOAuth2Client().getRevokeUrl());
  }

  @Test
  public void revokeTokenPostsOnlyTheEncodedToken() {
    server
        .expect(requestTo(REVOKE_URL))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(content().string("token=00Dxx%21AQ.a%2Fb%2Bc"))
        .andRespond(withSuccess());

    client.revokeToken("00Dxx!AQ.a/b+c");

    server.verify();
  }

  @Test
  public void revokeTokenDoesNotChangeTheInstanceUrl() {
    server.expect(requestTo(REVOKE_URL)).andRespond(withSuccess());

    client.revokeToken("tok");

    assertNull(client.getInstanceUrl());
  }

  @Test
  public void revokeTokenLetsAnErrorResponseThrough() {
    server
        .expect(requestTo(REVOKE_URL))
        .andRespond(
            // The body Salesforce sent for an already-revoked token: form-encoded, no content type
            withBadRequest()
                .body(
                    "error=unsupported_token_type&error_description=this%20token%20type%20is%20not%20supported"));

    HttpClientErrorException e =
        assertThrows(HttpClientErrorException.class, () -> client.revokeToken("tok"));

    assertTrue(
        e.getResponseBodyAsString(),
        e.getResponseBodyAsString().contains("error=unsupported_token_type"));
  }

  @Test
  public void revokeTokenRejectsABlankToken() {
    assertThrows(IllegalArgumentException.class, () -> client.revokeToken(null));
    assertThrows(IllegalArgumentException.class, () -> client.revokeToken(" "));
  }

  @Test
  public void revokeTokenNeedsATokenUrlItCanDeriveFrom() {
    SalesforceOAuth2Client custom =
        new SalesforceOAuth2Client("id", "secret", "https://x/authorize", "https://x/oauth");

    assertNull(custom.getRevokeUrl());
    assertThrows(IllegalStateException.class, () -> custom.revokeToken("tok"));
  }

  @Test
  public void emptyTokenResponseBodyIsRejected() {
    server.expect(requestTo(TOKEN_URL)).andRespond(withNoContent());

    assertThrows(IllegalStateException.class, () -> client.refreshAccess("ref"));
  }

  @Test
  public void gettersExposeConfiguration() {
    assertEquals("id", client.getClientId());
    assertEquals("secret", client.getClientSecret());
    assertEquals(TOKEN_URL, client.getAccessTokenUrl());
    assertEquals(
        "https://mydomain.my.salesforce.com/services/oauth2/authorize", client.getAuthorizeUrl());
  }
}
