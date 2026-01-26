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

import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.springframework.http.HttpMethod.GET;
import org.springframework.http.HttpStatus;
import org.springframework.salesforce.api.ResultItem;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


/**
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class RecentTemplateTest extends AbstractSalesforceTest {

    @Test
    public void search() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/recent"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("recent.json")).headers(responseHeaders));
        List<ResultItem> items = salesforce.recentOperations().recent();
        assertEquals(9, items.size());
        assertEquals("User", items.get(0).getType());
        assertEquals("/services/data/" + salesforce.apiOperations().getVersion() + "/sobjects/User/005A0000001cRuvIAE", items.get(0).getUrl());
        assertEquals("005A0000001cRuvIAE", items.get(0).getAttributes().get("Id"));
        assertEquals("Umut Utkan", items.get(0).getAttributes().get("Name"));
    }

}
