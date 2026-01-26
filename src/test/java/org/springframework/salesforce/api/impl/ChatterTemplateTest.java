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
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import org.springframework.http.HttpStatus;
import org.springframework.salesforce.api.SalesforceProfile;
import org.springframework.salesforce.api.Status;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


/**
 * @author Umut Utkan
 * @author Jared Ottley
 * @author Alexandru Leahu
 */
public class ChatterTemplateTest extends AbstractSalesforceTest {

    @Test
    public void getProfile() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/chatter/users/me"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("profile.json")).headers(responseHeaders));
        SalesforceProfile profile = salesforce.chatterOperations().getUserProfile();
        assertEquals("Umut Utkan", profile.getName());
        assertEquals("umut.utkan@foo.com", profile.getEmail());
        assertEquals("005A0000001cRuvIAE", profile.getId());
        assertEquals("umut.utkan@foo.com", profile.getUsername());
        assertEquals("Umut Utkan", profile.getName());
        assertEquals("https://c.na7.content.force.com/profilephoto/005/F", profile.getPhoto().getLargePhotoUrl());
        assertEquals("https://c.na7.content.force.com/profilephoto/005/T", profile.getPhoto().getSmallPhotoUrl());
    }

    @Test
    public void getStatus() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/chatter/users/me/status"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("chatter-status.json")).headers(responseHeaders));

        Status status = salesforce.chatterOperations().getStatus();
        assertNotNull(status);
        assertEquals("I am also working on #hede", status.getText());
    }

    @Test
    public void updateStatus() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/chatter/users/me/status"))
                .andExpect(method(POST))
                .andExpect(content().string("text=Updating+status+via+%23spring-social-salesforce%21"))

                .andRespond(withStatus(HttpStatus.OK).body(loadResource("chatter-status.json")).headers(responseHeaders));

        Status status = salesforce.chatterOperations().updateStatus("Updating status via #spring-social-salesforce!");
        assertNotNull(status);
    }

}
