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

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import static org.springframework.http.HttpMethod.GET;
import org.springframework.http.HttpStatus;
import org.springframework.salesforce.api.QueryResult;
import org.springframework.salesforce.api.ResultItem;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class QueryTemplateTest extends AbstractSalesforceTest {

    @Test
    public void simpleQuery() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/query?q=SELECT%20Id,%20Name,%20BillingCity%20FROM%20Account"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("query-simple.json")).headers(responseHeaders));
        QueryResult result = salesforce.queryOperations().query("SELECT Id, Name, BillingCity FROM Account");

        assertEquals(12, result.getRecords().size());
        assertEquals("/services/data/v37.0/query/01gD0000002HU6KIAW-2000", result.getNextRecordsUrl());
        assertEquals("01gD0000002HU6KIAW-2000", result.getNextRecordsToken());
        for (ResultItem item : result.getRecords()) {
            assertEquals("Account", item.getType());
        }
        Map attrFirst = result.getRecords().get(0).getAttributes();
        assertEquals("001A000000df63xIAA", attrFirst.get("Id"));
        assertEquals("GenePoint", attrFirst.get("Name"));
        assertEquals("Mountain View", attrFirst.get("BillingCity"));
    }

    @Test
    public void whereQuery() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/query?q=SELECT%20Id%20FROM%20Contact%20WHERE%20Name%20LIKE%20'U%25'%20AND%20MailingCity%20=%20'Istanbul'"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("query-where.json")).headers(responseHeaders));
        QueryResult result = salesforce.queryOperations().query("SELECT Id FROM Contact WHERE Name LIKE 'U%' AND MailingCity = 'Istanbul'");

        assertEquals(2, result.getRecords().size());
        assertEquals("Contact", result.getRecords().get(0).getType());
        assertEquals("/services/data/v37.0/sobjects/Contact/003A000000vF6QSIA0", result.getRecords().get(0).getUrl());
        assertEquals("003A000000vF6QSIA0", result.getRecords().get(0).getAttributes().get("Id"));
        assertEquals("Contact", result.getRecords().get(1).getType());
        assertEquals("/services/data/v37.0/sobjects/Contact/003A000000vF6QXIA0", result.getRecords().get(1).getUrl());
        assertEquals("003A000000vF6QXIA0", result.getRecords().get(1).getAttributes().get("Id"));
    }

    @Test
    public void child2parentQuery() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/query?q=SELECT%20Contact.FirstName,%20Contact.Account.Name%20FROM%20Contact"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("query-child2parent.json")).headers(responseHeaders));
        QueryResult result = salesforce.queryOperations().query("SELECT Contact.FirstName, Contact.Account.Name FROM Contact");

        assertEquals(22, result.getRecords().size());
        for (ResultItem item : result.getRecords()) {
            assertEquals("Contact", item.getType());
        }
        assertEquals("Umut", result.getRecords().get(0).getAttributes().get("FirstName"));
        assertNull(result.getRecords().get(0).getAttributes().get("Account"));
        assertEquals("Utkan", result.getRecords().get(1).getAttributes().get("FirstName"));
        assertNull(result.getRecords().get(1).getAttributes().get("Account"));
        assertEquals("Rose", result.getRecords().get(2).getAttributes().get("FirstName"));
        ResultItem roseAccount = (ResultItem) result.getRecords().get(2).getAttributes().get("Account");
        assertEquals("Account", roseAccount.getType());
        assertEquals("/services/data/v37.0/sobjects/Account/001A000000df640IAA", roseAccount.getUrl());
        assertEquals("Edge Communications", roseAccount.getAttributes().get("Name"));
    }

    @Test
    public void countQuery() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/query?q=SELECT%20COUNT()%20FROM%20Contact"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("query-count.json")).headers(responseHeaders));
        QueryResult result = salesforce.queryOperations().query("SELECT COUNT() FROM Contact");

        assertEquals(22, result.getTotalSize());
        assertEquals(0, result.getRecords().size());
    }

    @Test
    public void groupByQuery() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/query?q=SELECT%20LeadSource,%20COUNT(Name)%20FROM%20Lead%20GROUP%20BY%20LeadSource"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("query-groupby.json")).headers(responseHeaders));
        QueryResult result = salesforce.queryOperations().query("SELECT LeadSource, COUNT(Name) FROM Lead GROUP BY LeadSource");

        assertEquals(4, result.getRecords().size());
        for (ResultItem item : result.getRecords()) {
            assertEquals("AggregateResult", item.getType());
        }
        assertEquals("Web", result.getRecords().get(0).getAttributes().get("LeadSource"));
        assertEquals(7, result.getRecords().get(0).getAttributes().get("expr0"));
        assertEquals("Phone Inquiry", result.getRecords().get(1).getAttributes().get("LeadSource"));
        assertEquals(4, result.getRecords().get(1).getAttributes().get("expr0"));
    }

    @Test
    public void parent2childQuery() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/query?q=SELECT%20Name,%20(SELECT%20LastName%20FROM%20Contacts)%20FROM%20Account"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("query-parent2child.json")).headers(responseHeaders));
        QueryResult result = salesforce.queryOperations().query("SELECT Name, (SELECT LastName FROM Contacts) FROM Account");

        assertEquals(12, result.getRecords().size());
        for (ResultItem item : result.getRecords()) {
            assertEquals("Account", item.getType());
        }
        assertEquals("/services/data/v37.0/sobjects/Account/001A000000df63xIAA", result.getRecords().get(0).getUrl());
        assertEquals("GenePoint", result.getRecords().get(0).getAttributes().get("Name"));
        QueryResult genePointContacts = (QueryResult) result.getRecords().get(0).getAttributes().get("Contacts");
        assertEquals(1, genePointContacts.getRecords().size());
        assertEquals("Contact", genePointContacts.getRecords().get(0).getType());
        assertEquals("/services/data/v37.0/sobjects/Contact/003A000000lGE0yIAG", genePointContacts.getRecords().get(0).getUrl());
        assertEquals("Frank", genePointContacts.getRecords().get(0).getAttributes().get("LastName"));
    }

    @Test
    public void validNextPageQuery() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/query?q=SELECT%20Id,%20Name,%20BillingCity%20FROM%20Account"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("query-simple.json")).headers(responseHeaders));
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/query/01gD0000002HU6KIAW-2000"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("query-simple.json")).headers(responseHeaders));

        QueryResult result = salesforce.queryOperations().query("SELECT Id, Name, BillingCity FROM Account");
        String nextRecordsUrl = result.getNextRecordsUrl();
        assertEquals("/services/data/v37.0/query/01gD0000002HU6KIAW-2000", nextRecordsUrl);
        salesforce.queryOperations().nextPage(nextRecordsUrl);
    }

}
