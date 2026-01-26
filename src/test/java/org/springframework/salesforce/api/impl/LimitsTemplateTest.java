/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-social-salesforce
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
import org.springframework.salesforce.api.LimitsResults;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * @author Jared Ottley
 */
public class LimitsTemplateTest extends AbstractSalesforceTest {

    @Test
    public void getLimits() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/limits"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("limits.json")).headers(responseHeaders));
        LimitsResults results = salesforce.limitsOperations().getLimits();

        assertEquals(9, results.getDailyApiRequests().getAdditionalProperties().size());
        assertEquals(500, results.getHourlySyncReportRuns().getMax());
        assertEquals(500, results.getHourlySyncReportRuns().getRemaining());
    }

    @Test
    public void SforceLimitInfoCheck() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/limits"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("limits.json")).headers(responseHeaders));
        salesforce.limitsOperations().getLimits();

        assertEquals(39, salesforce.limitsOperations().getDailyApiUsed());
        assertEquals(15000, salesforce.limitsOperations().getDailyApiLimit());
    }

}
