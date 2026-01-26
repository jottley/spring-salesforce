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
package org.springframework.salesforce.api.impl;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.springframework.http.HttpMethod.GET;
import org.springframework.http.HttpStatus;
import org.springframework.salesforce.api.SalesforceUserDetails;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * @author Alexandra Leahu
 * @author Jared Ottley
 */
public class UserOperationsTemplateTest extends AbstractSalesforceTest {

    @Test
    public void getUserDetails() {
        mockServer.expect(requestTo("https://login.salesforce.com/services/oauth2/userinfo"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("userDetails.json")).headers(responseHeaders));
        SalesforceUserDetails userDetails = salesforce.userOperations().getSalesforceUserDetails();
        assertEquals("John Doe", userDetails.getName());
        assertEquals("john@doe.com", userDetails.getEmail());
        assertEquals("12345", userDetails.getId());
        assertEquals("johnny", userDetails.getPreferredUsername());
        assertEquals("John", userDetails.getFirstName());
        assertEquals("Doe", userDetails.getLastName());
    }
}
