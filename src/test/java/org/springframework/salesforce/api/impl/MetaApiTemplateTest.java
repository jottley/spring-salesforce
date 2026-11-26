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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import org.springframework.salesforce.api.ApiOperations;
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
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/v40.0"))
                  .andExpect(method(GET))
                  .andRespond(withStatus(HttpStatus.OK).body(loadResource("services.json")).headers(responseHeaders));
        Map<String, String> services = salesforce.apiOperations().getServices("v40.0");
        assertEquals(6, services.size());
        assertEquals("/services/data/v68.0/sobjects", services.get("sobjects"));
        assertEquals("/services/data/v68.0/chatter", services.get("chatter"));
    }

    @Test
    public void getServices2()
    {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/v68.0"))
                  .andExpect(method(GET))
                  .andRespond(withStatus(HttpStatus.OK).body(loadResource("services2.json")).headers(responseHeaders));
        Map<String, String> services = salesforce.apiOperations().getServices();
        assertEquals(6, services.size());
        assertEquals("/services/data/v68.0/sobjects", services.get("sobjects"));
        assertEquals("/services/data/v68.0/chatter", services.get("chatter"));
    }

    @Test
    public void getVersion()
    {
        String version = salesforce.apiOperations().getVersion();
        assertEquals("v68.0", version);
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

    // ---- default version and the minimum-version floor ----

    @Test
    public void defaultVersionIsV68AndNotBelowTheFloor()
    {
        assertEquals("v68.0", ApiOperations.DEFAULT_API_VERSION);
        assertEquals("v68.0", salesforce.apiOperations().getVersion());
        assertEquals(31, ApiOperations.MINIMUM_API_VERSION);
        int defaultMajor = Integer.parseInt(ApiOperations.DEFAULT_API_VERSION.substring(1, ApiOperations.DEFAULT_API_VERSION.indexOf('.')));
        assertTrue("the default must never be below the floor", defaultMajor >= ApiOperations.MINIMUM_API_VERSION);
    }

    @Test
    public void setVersionAcceptsTheFloorAndAnythingAbove()
    {
        for (String ok : new String[] { "v31.0", "v32.0", "v41.0", "v68.0", "v99.0", "v100.0", "v999.0" })
        {
            salesforce.apiOperations().setVersion(ok);
            assertEquals(ok, salesforce.apiOperations().getVersion());
        }
    }

    @Test
    public void setVersionRejectsAnythingBelowTheFloor()
    {
        for (String old : new String[] { "v30.0", "v29.0", "v21.0", "v20.0", "v10.0", "v9.0", "v1.0", "v0.0", "v030.0" })
        {
            InvalidSalesforceApiVersionException e = assertThrows(old, InvalidSalesforceApiVersionException.class,
                    () -> salesforce.apiOperations().setVersion(old));
            assertEquals(old + " is below the minimum supported Salesforce Api version (v31.0).", e.getMessage());
        }
    }

    @Test
    public void aRejectedVersionLeavesTheCurrentVersionUntouched()
    {
        salesforce.apiOperations().setVersion("v38.0");

        assertThrows(InvalidSalesforceApiVersionException.class, () -> salesforce.apiOperations().setVersion("v30.0"));
        assertThrows(InvalidSalesforceApiVersionException.class, () -> salesforce.apiOperations().setVersion("nonsense"));

        assertEquals("v38.0", salesforce.apiOperations().getVersion());
    }

    @Test
    public void malformedVersionsKeepTheOriginalMessage()
    {
        for (String bad : new String[] { null, "", "  ", "38.0", "latest", "v68", "vv68.0", " v68.0", "v68.0 ",
                "xv68.0y", "v68.00", "v68.x", "V68.0", "v1000.0", "v-1.0", "v68.0/../x" })
        {
            InvalidSalesforceApiVersionException e = assertThrows(String.valueOf(bad), InvalidSalesforceApiVersionException.class,
                    () -> salesforce.apiOperations().setVersion(bad));
            assertEquals(bad + " is not a valid Salesforce Api version.", e.getMessage());
        }
    }

    @Test
    public void getServicesRejectsVersionsBelowTheFloorWithoutMakingARequest()
    {
        // no expectations are registered, so any HTTP request would fail the test
        assertThrows(InvalidSalesforceApiVersionException.class, () -> salesforce.apiOperations().getServices("v30.0"));
        assertThrows(InvalidSalesforceApiVersionException.class, () -> salesforce.apiOperations().getServices("v23.0"));
        mockServer.verify();
    }

    @Test
    public void getServicesRejectsMalformedVersionsWithoutMakingARequest()
    {
        for (String bad : new String[] { null, "", "bad", "68.0", "v68.0/../../x", "v68.0?x=1" })
        {
            assertThrows(String.valueOf(bad), InvalidSalesforceApiVersionException.class,
                    () -> salesforce.apiOperations().getServices(bad));
        }
        mockServer.verify();
    }

    @Test
    public void getServicesAcceptsTheFloorVersion()
    {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/v31.0"))
                  .andExpect(method(GET))
                  .andRespond(withStatus(HttpStatus.OK).body(loadResource("services.json")).headers(responseHeaders));

        assertEquals(6, salesforce.apiOperations().getServices("v31.0").size());
        mockServer.verify();
    }

    @Test
    public void requestsUseTheNewDefaultVersionInTheirUrls()
    {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/v68.0/sobjects"))
                  .andExpect(method(GET))
                  .andRespond(withStatus(HttpStatus.OK).body(loadResource("sobjects.json")).headers(responseHeaders));

        salesforce.sObjectsOperations().getSObjects();
        mockServer.verify();
    }
}
