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
package org.springframework.salesforce.api.client;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.HttpMethod.POST;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.client.BaseSalesforceFactory;
import org.springframework.test.web.client.MockRestServiceServer;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
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

        mockServer.expect(requestTo("https://login.salesforce.com/services/oauth2/token"))
                .andExpect(method(POST))
                .andExpect(content().string("grant_type=password&client_id=client-id&client_secret=client-secret&username=my-username&password=my-passwordsecurity-token"))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("client-token.json")).headers(responseHeaders));

        Salesforce template = new BaseSalesforceFactory("client-id", "client-secret", restTemplate)
                .create("my-username", "my-password", "security-token");
        assertNotNull(template);
    }

    protected Resource loadResource(String name) {
        return new ClassPathResource("/" + name, getClass());
    }

}
