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
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.api.impl.SalesforceTemplate;
import org.springframework.test.web.client.MockRestServiceServer;

/**
 * @author Jared Ottley
 */
public class SalesforceServiceProviderTest {

    @Test
    public void productionUrls() {
        SalesforceOAuth2Client client = new SalesforceServiceProvider("id", "secret").getOAuth2Client();
        assertEquals("https://login.salesforce.com/services/oauth2/authorize", client.getAuthorizeUrl());
        assertEquals("https://login.salesforce.com/services/oauth2/token", client.getAccessTokenUrl());
        assertEquals("id", client.getClientId());
        assertEquals("secret", client.getClientSecret());
    }

    @Test
    public void sandboxUrls() {
        SalesforceOAuth2Client client = new SalesforceServiceProvider("id", "secret", true).getOAuth2Client();
        assertEquals("https://test.salesforce.com/services/oauth2/authorize", client.getAuthorizeUrl());
        assertEquals("https://test.salesforce.com/services/oauth2/token", client.getAccessTokenUrl());
    }

    @Test
    public void sandboxFalseUsesProductionUrls() {
        SalesforceOAuth2Client client = new SalesforceServiceProvider("id", "secret", false).getOAuth2Client();
        assertEquals("https://login.salesforce.com/services/oauth2/token", client.getAccessTokenUrl());
    }

    @Test
    public void customUrls() {
        SalesforceOAuth2Client client = new SalesforceServiceProvider("id", "secret", "https://a/authorize",
                "https://a/token").getOAuth2Client();
        assertEquals("https://a/authorize", client.getAuthorizeUrl());
        assertEquals("https://a/token", client.getAccessTokenUrl());
    }

    @Test
    public void getApiWithoutKnownInstanceUrlKeepsTemplateDefault() {
        Salesforce api = new SalesforceServiceProvider("id", "secret").getApi("token");
        assertTrue(api instanceof SalesforceTemplate);
        assertEquals("https://na1.salesforce.com/services/data", ((SalesforceTemplate) api).getBaseUrl());
    }

    @Test
    public void getApiUsesInstanceUrlFromTokenResponse() {
        SalesforceServiceProvider provider = new SalesforceServiceProvider("id", "secret",
                "https://a/authorize", "https://a/token");
        MockRestServiceServer server = MockRestServiceServer.createServer(provider.getOAuth2Client().getRestTemplate());
        server.expect(requestTo("https://a/token")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"access_token\":\"tok\",\"instance_url\":\"https://na7.salesforce.com\"}",
                        MediaType.APPLICATION_JSON));
        provider.getOAuth2Client().exchangeForAccess("code", null);

        Salesforce api = provider.getApi("tok");
        assertEquals("https://na7.salesforce.com/services/data", ((SalesforceTemplate) api).getBaseUrl());
    }

    @Test
    public void getApiWithExplicitInstanceUrl() {
        Salesforce api = new SalesforceServiceProvider("id", "secret").getApi("tok", "https://na9.salesforce.com");
        assertEquals("https://na9.salesforce.com/services/data", ((SalesforceTemplate) api).getBaseUrl());
    }

    @Test
    public void getApiWithNullInstanceUrlKeepsTemplateDefault() {
        Salesforce api = new SalesforceServiceProvider("id", "secret").getApi("tok", null);
        assertEquals("https://na1.salesforce.com/services/data", ((SalesforceTemplate) api).getBaseUrl());
    }
}
