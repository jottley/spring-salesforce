/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-social-salesforce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.salesforce.connect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

public class SalesforceOAuth2ClientTest {

    private static final String TOKEN_URL = "https://mydomain.my.salesforce.com/services/oauth2/token";

    private SalesforceOAuth2Client client;
    private MockRestServiceServer server;

    @Before
    public void setUp() {
        client = new SalesforceOAuth2Client("id", "secret",
                "https://mydomain.my.salesforce.com/services/oauth2/authorize", TOKEN_URL);
        server = MockRestServiceServer.createServer(client.getRestTemplate());
    }

    @Test
    public void clientCredentialsAccessPostsGrantAndReadsToken() {
        server.expect(requestTo(TOKEN_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string("grant_type=client_credentials&client_id=id&client_secret=secret"))
                .andRespond(withSuccess(
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
        server.expect(requestTo(TOKEN_URL))
                .andExpect(content().string(
                        "grant_type=client_credentials&client_id=id&client_secret=secret&scope=api"))
                .andRespond(withSuccess("{\"access_token\":\"tok\"}", MediaType.APPLICATION_JSON));

        client.clientCredentialsAccess("api");

        server.verify();
    }

    @Test
    public void buildAuthorizeUrlIncludesOnlyGivenParameters() {
        assertEquals("https://mydomain.my.salesforce.com/services/oauth2/authorize?response_type=code&client_id=id",
                client.buildAuthorizeUrl(null, null, null));
        assertEquals("https://mydomain.my.salesforce.com/services/oauth2/authorize?response_type=code&client_id=id"
                + "&redirect_uri=https://app/cb&scope=api&state=xyz",
                client.buildAuthorizeUrl("https://app/cb", "api", "xyz"));
    }

    @Test
    public void exchangeForAccessPostsAuthorizationCode() {
        server.expect(requestTo(TOKEN_URL))
                .andExpect(content().string("grant_type=authorization_code&code=abc&client_id=id&client_secret=secret"
                        + "&redirect_uri=https%3A%2F%2Fapp%2Fcb"))
                .andRespond(withSuccess("{\"access_token\":\"tok\",\"refresh_token\":\"ref\"}",
                        MediaType.APPLICATION_JSON));

        AccessGrant grant = client.exchangeForAccess("abc", "https://app/cb");

        server.verify();
        assertEquals("ref", grant.getRefreshToken());
        assertNull(client.getInstanceUrl());
    }

    @Test
    public void exchangeForAccessOmitsRedirectUriWhenNull() {
        server.expect(requestTo(TOKEN_URL))
                .andExpect(content().string("grant_type=authorization_code&code=abc&client_id=id&client_secret=secret"))
                .andRespond(withSuccess("{\"access_token\":\"tok\"}", MediaType.APPLICATION_JSON));

        client.exchangeForAccess("abc", null);

        server.verify();
    }

    @Test
    public void refreshAccessPostsRefreshToken() {
        server.expect(requestTo(TOKEN_URL))
                .andExpect(content().string("grant_type=refresh_token&refresh_token=ref&client_id=id&client_secret=secret"))
                .andRespond(withSuccess("{\"access_token\":\"new\"}", MediaType.APPLICATION_JSON));

        assertEquals("new", client.refreshAccess("ref").getAccessToken());
        server.verify();
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
        assertEquals("https://mydomain.my.salesforce.com/services/oauth2/authorize", client.getAuthorizeUrl());
    }
}
