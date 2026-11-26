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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.salesforce.api.InvalidSalesforceApiVersionException;
import org.springframework.salesforce.api.MissingAuthorizationException;
import org.springframework.salesforce.api.QueryResult;
import org.springframework.web.client.RestTemplate;

/**
 * Covers the less common paths of the operations templates: instance-URL overloads, empty
 * response bodies, query paging, and argument validation.
 *
 * @author Jared Ottley
 */
public class OperationsTemplateEdgeCaseTest extends AbstractSalesforceTest {

	private static final String OTHER_INSTANCE = "https://na9.salesforce.com";

	private String dataUrl() {
		return "https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion();
	}

	private static String page(int total, boolean done, String nextUrl, String... ids) {
		StringBuilder records = new StringBuilder();
		for (String id : ids) {
			if (!records.isEmpty()) {
				records.append(',');
			}
			records.append("{\"attributes\":{\"type\":\"Account\",\"url\":\"/services/data/v37.0/sobjects/Account/")
					.append(id).append("\"},\"Id\":\"").append(id).append("\"}");
		}
		return "{\"totalSize\":" + total + ",\"done\":" + done
				+ (nextUrl == null ? "" : ",\"nextRecordsUrl\":\"" + nextUrl + "\"")
				+ ",\"records\":[" + records + "]}";
	}

	// ---- SalesforceTemplate: instance URL and gateway overloads ----

	@Test
	public void instanceUrlOverloadsSetInstanceUrlAndReturnTheSameOperations() {
		assertSame(salesforce.apiOperations(), salesforce.apiOperations(OTHER_INSTANCE));
		assertEquals(OTHER_INSTANCE, salesforce.getInstanceUrl());

		salesforce.setInstanceUrl("https://na7.salesforce.com");
		assertSame(salesforce.chatterOperations(), salesforce.chatterOperations(OTHER_INSTANCE));
		assertEquals(OTHER_INSTANCE, salesforce.getInstanceUrl());

		salesforce.setInstanceUrl("https://na7.salesforce.com");
		assertSame(salesforce.queryOperations(), salesforce.queryOperations(OTHER_INSTANCE));
		assertEquals(OTHER_INSTANCE, salesforce.getInstanceUrl());

		salesforce.setInstanceUrl("https://na7.salesforce.com");
		assertSame(salesforce.recentOperations(), salesforce.recentOperations(OTHER_INSTANCE));
		assertEquals(OTHER_INSTANCE, salesforce.getInstanceUrl());

		salesforce.setInstanceUrl("https://na7.salesforce.com");
		assertSame(salesforce.searchOperations(), salesforce.searchOperations(OTHER_INSTANCE));
		assertEquals(OTHER_INSTANCE, salesforce.getInstanceUrl());

		salesforce.setInstanceUrl("https://na7.salesforce.com");
		assertSame(salesforce.sObjectsOperations(), salesforce.sObjectsOperations(OTHER_INSTANCE));
		assertEquals(OTHER_INSTANCE, salesforce.getInstanceUrl());

		salesforce.setInstanceUrl("https://na7.salesforce.com");
		assertSame(salesforce.limitsOperations(), salesforce.limitsOperations(OTHER_INSTANCE));
		assertEquals(OTHER_INSTANCE, salesforce.getInstanceUrl());

		salesforce.setInstanceUrl("https://na7.salesforce.com");
		assertSame(salesforce.connectOperations(), salesforce.connectOperations(OTHER_INSTANCE));
		assertEquals(OTHER_INSTANCE, salesforce.getInstanceUrl());

		salesforce.setInstanceUrl("https://na7.salesforce.com");
		assertSame(salesforce.customApiOperations(), salesforce.customApiOperations(OTHER_INSTANCE));
		assertEquals(OTHER_INSTANCE, salesforce.getInstanceUrl());
	}

	@Test
	public void gatewayUrlOverloadAndUserInfoUrl() {
		assertNull(salesforce.getAuthGatewayUrl());
		assertEquals("https://login.salesforce.com/services/oauth2/userinfo", salesforce.getUserInfoUrl());

		assertSame(salesforce.userOperations(), salesforce.userOperations("https://test.salesforce.com"));
		assertEquals("https://test.salesforce.com", salesforce.getAuthGatewayUrl());
		assertEquals("https://test.salesforce.com/services/oauth2/userinfo", salesforce.getUserInfoUrl());

		salesforce.setAuthGatewayBaseUrl("https://custom.example.com");
		assertEquals("https://custom.example.com/services/oauth2/userinfo", salesforce.getUserInfoUrl());
	}

	// ---- QueryTemplate ----

	@Test
	public void queryIncludingDeletedItemsUsesQueryAllResource() {
		mockServer.expect(requestTo(dataUrl() + "/queryAll?q=SELECT%20Id%20FROM%20Account"))
				.andExpect(method(GET))
				.andRespond(withSuccess(page(1, true, null, "001"), MediaType.APPLICATION_JSON));

		QueryResult result = salesforce.queryOperations().query("SELECT Id FROM Account", true);

		mockServer.verify();
		assertEquals(1, result.getRecords().size());
	}

	@Test
	public void nextPageWithPathUsesInstanceUrl() {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/data/v37.0/query/01g-2000"))
				.andExpect(method(GET))
				.andRespond(withSuccess(page(3, true, null, "002"), MediaType.APPLICATION_JSON));

		QueryResult result = salesforce.queryOperations().nextPage("/services/data/v37.0/query/01g-2000");

		mockServer.verify();
		assertEquals(1, result.getRecords().size());
	}

	@Test
	public void nextPageWithPathUsesTheSameHostAsTheFirstPageWhenNoInstanceUrlIsSet() {
		// With no instance URL the template falls back to its default host for every request,
		// so the continuation page must go to that host rather than a "null/..." URL.
		salesforce.setInstanceUrl(null);
		mockServer.expect(requestTo("https://na1.salesforce.com/services/data/v37.0/query/01g-2000"))
				.andExpect(method(GET))
				.andRespond(withSuccess(page(3, true, null, "002"), MediaType.APPLICATION_JSON));

		QueryResult result = salesforce.queryOperations().nextPage("/services/data/v37.0/query/01g-2000");

		mockServer.verify();
		assertEquals(1, result.getRecords().size());
	}

	@Test
	public void nextPageWithPathWithoutLeadingSlashIsStillAbsolute() {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/data/v37.0/query/01g-2000"))
				.andRespond(withSuccess(page(3, true, null, "002"), MediaType.APPLICATION_JSON));

		salesforce.queryOperations().nextPage("services/data/v37.0/query/01g-2000");

		mockServer.verify();
	}

	@Test
	public void nextPageWithTokenUsesQueryResource() {
		mockServer.expect(requestTo(dataUrl() + "/query/01g-2000"))
				.andExpect(method(GET))
				.andRespond(withSuccess(page(3, true, null, "002"), MediaType.APPLICATION_JSON));

		QueryResult result = salesforce.queryOperations().nextPage("01g-2000");

		mockServer.verify();
		assertEquals(1, result.getRecords().size());
	}

	@Test
	public void queryAllFollowsNextRecordsUrlAndAggregatesRecords() {
		mockServer.expect(requestTo(dataUrl() + "/query?q=SELECT%20Id%20FROM%20Account"))
				.andRespond(withSuccess(
						page(3, false, "/services/data/v37.0/query/01g-2000", "001", "002"), MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo("https://na7.salesforce.com/services/data/v37.0/query/01g-2000"))
				.andRespond(withSuccess(page(3, true, null, "003"), MediaType.APPLICATION_JSON));

		QueryResult result = salesforce.queryOperations().queryAll("SELECT Id FROM Account");

		mockServer.verify();
		assertEquals(3, result.getTotalSize());
		assertTrue(result.isDone());
		assertEquals(3, result.getRecords().size());
	}

	@Test
	public void queryAllWithSinglePageDoesNotRequestMore() {
		mockServer.expect(requestTo(dataUrl() + "/query?q=SELECT%20Id%20FROM%20Account"))
				.andRespond(withSuccess(page(1, true, null, "001"), MediaType.APPLICATION_JSON));

		assertEquals(1, salesforce.queryOperations().queryAll("SELECT Id FROM Account").getRecords().size());
		mockServer.verify();
	}

	// ---- SObjectsTemplate ----

	@Test
	public void getSObjectsReturnsEmptyListForEmptyResponse() {
		mockServer.expect(requestTo(dataUrl() + "/sobjects")).andRespond(withNoContent());

		assertTrue(salesforce.sObjectsOperations().getSObjects().isEmpty());
	}

	@Test
	public void getSObjectSummaryReturnsNullForEmptyResponse() {
		mockServer.expect(requestTo(dataUrl() + "/sobjects/Account")).andRespond(withNoContent());

		assertNull(salesforce.sObjectsOperations().getSObjectSummary("Account"));
	}

	@Test
	public void getRowRequestsOnlyTheGivenFields() {
		mockServer.expect(requestTo(dataUrl() + "/sobjects/Account/001?fields=Id,Name"))
				.andExpect(method(GET))
				.andRespond(withSuccess("{\"Id\":\"001\",\"Name\":\"Acme\"}", MediaType.APPLICATION_JSON));

		Map<?, ?> row = salesforce.sObjectsOperations().getRow("Account", "001", "Id", "Name");

		mockServer.verify();
		assertEquals("Acme", row.get("Name"));
	}

	@Test
	public void getRowWithoutFieldsOmitsTheFieldsParameter() {
		mockServer.expect(requestTo(dataUrl() + "/sobjects/Account/001"))
				.andRespond(withSuccess("{\"Id\":\"001\"}", MediaType.APPLICATION_JSON));

		assertEquals("001", salesforce.sObjectsOperations().getRow("Account", "001").get("Id"));
		mockServer.verify();
	}

	@Test
	public void updateSynthesizesSuccessResultForEmptyResponse() {
		mockServer.expect(requestTo(dataUrl() + "/sobjects/Account/001?_HttpMethod=PATCH"))
				.andExpect(method(POST))
				.andRespond(withNoContent());

		Map<String, Object> result = salesforce.sObjectsOperations().update("Account", "001", Map.of("Name", "New"));

		mockServer.verify();
		assertEquals("001", result.get("id"));
		assertEquals(true, result.get("success"));
		assertEquals(List.of(), result.get("errors"));
	}

	// ---- ConnectTemplate ----

	@Test
	public void communitiesAreEmptyForEmptyResponse() {
		mockServer.expect(requestTo(dataUrl() + "/connect/communities/")).andRespond(withNoContent());

		assertTrue(salesforce.connectOperations().getCommunities().isEmpty());
	}

	@Test
	public void communityUsersAreEmptyForEmptyResponse() {
		mockServer.expect(requestTo(dataUrl() + "/connect/communities/c1/chatter/users")).andRespond(withNoContent());

		assertTrue(salesforce.connectOperations().getCommunityUsers("c1").isEmpty());
	}

	// ---- ChatterTemplate ----

	@Test
	public void getStatusIsNullForEmptyResponse() {
		mockServer.expect(requestTo(dataUrl() + "/chatter/users/me/status")).andRespond(withNoContent());

		assertNull(salesforce.chatterOperations().getStatus());
	}

	@Test
	public void getStatusIsNullWhenBodyFieldIsMissing() {
		mockServer.expect(requestTo(dataUrl() + "/chatter/users/me/status"))
				.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

		assertNull(salesforce.chatterOperations().getStatus());
	}

	@Test
	public void updateStatusIsNullForEmptyResponse() {
		mockServer.expect(requestTo(dataUrl() + "/chatter/users/me/status")).andRespond(withNoContent());

		assertNull(salesforce.chatterOperations().updateStatus("hi"));
	}

	@Test
	public void updateStatusIsNullWhenBodyFieldIsMissing() {
		mockServer.expect(requestTo(dataUrl() + "/chatter/users/me/status"))
				.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

		assertNull(salesforce.chatterOperations().updateStatus("hi"));
	}

	// ---- ApiTemplate version validation ----

	@Test
	public void setVersionRejectsBlankAndMalformedValues() {
		assertThrows(InvalidSalesforceApiVersionException.class, () -> salesforce.apiOperations().setVersion(null));
		assertThrows(InvalidSalesforceApiVersionException.class, () -> salesforce.apiOperations().setVersion(""));
		assertThrows(InvalidSalesforceApiVersionException.class, () -> salesforce.apiOperations().setVersion("  "));
		assertThrows(InvalidSalesforceApiVersionException.class, () -> salesforce.apiOperations().setVersion("latest"));
		assertEquals("v37.0", salesforce.apiOperations().getVersion());
	}

	// ---- Authorization and instance URL guards ----

	@Test
	public void operationsRequireAnApiInstance() {
		QueryTemplate detached = new QueryTemplate(null, new RestTemplate());

		assertThrows(MissingAuthorizationException.class, () -> detached.query("SELECT Id FROM Account"));
	}

	@Test
	public void customApiRequiresAnInstanceUrl() {
		salesforce.setInstanceUrl(null);

		assertThrows(IllegalStateException.class,
				() -> salesforce.customApiOperations().getForApexObject("/thing", String.class));
		assertNotNull(salesforce.customApiOperations());
	}
}
