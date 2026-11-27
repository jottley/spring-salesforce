/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-social-salesforce
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.salesforce.api.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.List;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.salesforce.api.ResultItem;

/**
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SearchTemplateTest extends AbstractSalesforceTest {

  @Test
  public void search() {
    mockServer
        .expect(
            requestTo(
                "https://na7.salesforce.com/services/data/"
                    + salesforce.apiOperations().getVersion()
                    + "/search?q=FIND%20%7Bxxx*%7D%20IN%20ALL%20FIELDS%20RETURNING%20Contact,%20Account"))
        .andExpect(method(GET))
        .andRespond(
            withStatus(HttpStatus.OK).body(loadResource("search.json")).headers(responseHeaders));
    List<ResultItem> results =
        salesforce
            .searchOperations()
            .search("FIND {xxx*} IN ALL FIELDS RETURNING Contact, Account");
    assertEquals(4, results.size());
    assertEquals("Contact", results.get(0).getType());
    assertEquals("Contact", results.get(1).getType());
    assertEquals("Account", results.get(2).getType());
    assertEquals("Account", results.get(3).getType());
  }

  private static final String SEARCH_URL = "/search?q=FIND%20%7Bxxx*%7D%20IN%20ALL%20FIELDS";
  private static final String SOSL = "FIND {xxx*} IN ALL FIELDS";

  private void expectSearchResponse(
      org.springframework.test.web.client.response.DefaultResponseCreator response) {
    mockServer
        .expect(
            requestTo(
                "https://na7.salesforce.com/services/data/"
                    + salesforce.apiOperations().getVersion()
                    + SEARCH_URL))
        .andExpect(method(GET))
        .andRespond(response);
  }

  @Test
  public void searchReadsTheCurrentApiResponseWhereRecordsAreUnderSearchRecords() {
    // Live check against API v68.0: Salesforce answers with {"searchRecords":[...]}, not a bare
    // array
    expectSearchResponse(
        withStatus(HttpStatus.OK).body(loadResource("search-v68.json")).headers(responseHeaders));

    List<ResultItem> results = salesforce.searchOperations().search(SOSL);

    assertEquals(2, results.size());
    assertEquals("Account", results.get(0).getType());
    assertEquals(
        "/services/data/v68.0/sobjects/Account/001A000000df63xIAA", results.get(0).getUrl());
    assertEquals("001A000000df63xIAA", results.get(0).getAttributes().get("Id"));
    assertEquals("Contact", results.get(1).getType());
  }

  @Test
  public void searchWithNoHitsGivesAnEmptyListInEitherShape() {
    for (String body :
        new String[] {"[]", "{\"searchRecords\":[]}", "{}", "{\"searchRecords\":null}"}) {
      mockServer.reset();
      expectSearchResponse(withStatus(HttpStatus.OK).body(body).headers(responseHeaders));

      assertTrue(body, salesforce.searchOperations().search(SOSL).isEmpty());
    }
  }

  @Test
  public void searchWithAnEmptyResponseGivesAnEmptyListNotNull() {
    expectSearchResponse(withStatus(HttpStatus.NO_CONTENT));

    List<ResultItem> results = salesforce.searchOperations().search(SOSL);

    assertNotNull(results);
    assertTrue(results.isEmpty());
  }
}
