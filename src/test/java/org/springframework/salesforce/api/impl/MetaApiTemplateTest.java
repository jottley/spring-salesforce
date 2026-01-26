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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import static org.springframework.http.HttpMethod.GET;
import org.springframework.http.HttpStatus;
import org.springframework.salesforce.api.ApiVersion;
import org.springframework.salesforce.api.InvalidSalesforceApiVersionException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class MetaApiTemplateTest extends AbstractSalesforceTest
{

    @Test
    public void getApiVersions()
    {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data"))
                  .andExpect(method(GET))
                  .andRespond(withStatus(HttpStatus.OK).body(loadResource("versions.json")).headers(responseHeaders));
        List<ApiVersion> versions = salesforce.apiOperations().getVersions();
        assertEquals(4, versions.size());
        assertEquals("Winter '12", versions.get(3).getLabel());
        assertEquals("23.0", versions.get(3).getVersion());
        assertEquals("/services/data/v23.0", versions.get(3).getUrl());
    }

    @Test
    public void getServices()
    {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/v23.0"))
                  .andExpect(method(GET))
                  .andRespond(withStatus(HttpStatus.OK).body(loadResource("services.json")).headers(responseHeaders));
        Map<String, String> services = salesforce.apiOperations().getServices("v23.0");
        assertEquals(6, services.size());
        assertEquals("/services/data/v37.0/sobjects", services.get("sobjects"));
        assertEquals("/services/data/v37.0/chatter", services.get("chatter"));
    }

    @Test
    public void getServices2()
    {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/v37.0"))
                  .andExpect(method(GET))
                  .andRespond(withStatus(HttpStatus.OK).body(loadResource("services2.json")).headers(responseHeaders));
        Map<String, String> services = salesforce.apiOperations().getServices();
        assertEquals(6, services.size());
        assertEquals("/services/data/v37.0/sobjects", services.get("sobjects"));
        assertEquals("/services/data/v37.0/chatter", services.get("chatter"));
    }

    @Test
    public void getVersion()
    {
        String version = salesforce.apiOperations().getVersion();
        assertEquals("v37.0", version);
    }

    @Test
    public void setVersion()
    {
        try
        {
            salesforce.apiOperations().setVersion("v38.0");
            String version = salesforce.apiOperations().getVersion();
            assertEquals("v38.0", version);
        }
        catch (InvalidSalesforceApiVersionException e)
        {
            fail("InvalidSalesforceApiVersionException thrown");
        }
    }

    @Test
    public void setVersion2()
    {
        try
        {
            salesforce.apiOperations().setVersion("38.0");
            fail("InvalidSalesforceApiVersionException not thrown");
        }
        catch (InvalidSalesforceApiVersionException e)
        {
            assertEquals("38.0 is not a valid Salesforce Api version.", e.getMessage());
        }
    }
}
