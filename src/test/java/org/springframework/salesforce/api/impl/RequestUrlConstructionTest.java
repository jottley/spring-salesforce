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

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.springframework.http.MediaType;

/**
 * Checks how caller-supplied values end up in request URLs: they must not be able to add path
 * segments, query parameters or dot-segments, and a query must reach the server exactly as written.
 *
 * @author Jared Ottley
 */
public class RequestUrlConstructionTest extends AbstractSalesforceTest {

	private static final String DATA = "/services/data/v68.0";

	/** Runs the call against a server that records the URI of the one request it receives. */
	private URI requested(String responseBody, Runnable call) {
		AtomicReference<URI> uri = new AtomicReference<>();
		mockServer.expect(requestTo(startsWith("https://na7.salesforce.com/"))).andRespond(request -> {
			uri.set(request.getURI());
			return withSuccess(responseBody, MediaType.APPLICATION_JSON).createResponse(request);
		});
		call.run();
		mockServer.verify();
		return uri.get();
	}

	// ---- getRow ----

	@Test
	public void getRowValuesCannotLeaveTheirPathSegments() {
		URI uri = requested("{}", () -> salesforce.sObjectsOperations().getRow("Acc ount", "../../limits?x=1#f"));

		assertEquals("na7.salesforce.com", uri.getHost());
		assertTrue(uri.getRawPath(), uri.getRawPath().startsWith(DATA + "/sobjects/Acc%20ount/"));
		assertFalse(uri.getRawPath(), uri.getRawPath().contains("/../"));
		assertNull("a '?' in the id must not start a query", uri.getRawQuery());
		assertNull("a '#' in the id must not start a fragment", uri.getRawFragment());
	}

	@Test
	public void getRowFieldNamesCannotAddQueryParameters() {
		URI uri = requested("{}", () -> salesforce.sObjectsOperations().getRow("Account", "001", "Id", "Name&x=1", "a#b"));

		assertEquals("fields=Id,Name%26x%3D1,a%23b", uri.getRawQuery());
		assertNull(uri.getRawFragment());
	}

	@Test
	public void getRowLeavesOrdinaryValuesAsTheyWere() {
		URI uri = requested("{}", () -> salesforce.sObjectsOperations().getRow("My_Obj__c", "001xx000003DGb2AAG", "Id", "Name"));

		assertEquals(DATA + "/sobjects/My_Obj__c/001xx000003DGb2AAG", uri.getRawPath());
		assertEquals("fields=Id,Name", uri.getRawQuery());
	}

	@Test
	public void getRowToleratesAnExplicitNullFieldList() {
		URI uri = requested("{}", () -> salesforce.sObjectsOperations().getRow("Account", "001", (String[]) null));

		assertNull(uri.getRawQuery());
	}

	// ---- community id ----

	@Test
	public void communityIdCannotLeaveItsPathSegment() {
		URI uri = requested("{\"users\":[]}", () -> salesforce.connectOperations().getCommunityUsers("a/../b?x#y"));

		assertEquals(DATA + "/connect/communities/a%2F..%2Fb%3Fx%23y/chatter/users", uri.getRawPath());
		assertNull(uri.getRawQuery());
		assertNull(uri.getRawFragment());
	}

	// ---- dot-segments and blanks are rejected before any request ----

	@Test
	public void dotSegmentsAndBlankValuesAreRejectedEverywhereWithoutARequest() {
		Map<String, java.util.function.Consumer<String>> operations = new LinkedHashMap<>();
		var sobjects = salesforce.sObjectsOperations();
		var chatter = salesforce.chatterOperations();
		operations.put("getSObjectSummary", v -> sobjects.getSObjectSummary(v));
		operations.put("describeSObject", v -> sobjects.describeSObject(v));
		operations.put("getRow name", v -> sobjects.getRow(v, "001"));
		operations.put("getRow id", v -> sobjects.getRow("Account", v));
		operations.put("getBlob name", v -> sobjects.getBlob(v, "1", "Body"));
		operations.put("getBlob id", v -> sobjects.getBlob("Account", v, "Body"));
		operations.put("getBlob field", v -> sobjects.getBlob("Account", "1", v));
		operations.put("downloadBlob name", v -> sobjects.downloadBlob(v, "1", "Body", new ByteArrayOutputStream()));
		operations.put("downloadBlob id", v -> sobjects.downloadBlob("Account", v, "Body", new ByteArrayOutputStream()));
		operations.put("downloadBlob field", v -> sobjects.downloadBlob("Account", "1", v, new ByteArrayOutputStream()));
		operations.put("create", v -> sobjects.create(v, Map.of()));
		operations.put("update name", v -> sobjects.update(v, "1", Map.of()));
		operations.put("update id", v -> sobjects.update("Account", v, Map.of()));
		operations.put("delete name", v -> sobjects.delete(v, "1"));
		operations.put("delete id", v -> sobjects.delete("Account", v));
		operations.put("getUserProfile", v -> chatter.getUserProfile(v));
		operations.put("getStatus", v -> chatter.getStatus(v));
		operations.put("updateStatus", v -> chatter.updateStatus(v, "hi"));
		operations.put("getCommunityUsers", v -> salesforce.connectOperations().getCommunityUsers(v));
		operations.put("nextPage", v -> salesforce.queryOperations().nextPage(v));

		for (Map.Entry<String, java.util.function.Consumer<String>> operation : operations.entrySet()) {
			for (String bad : new String[] { "..", ".", "", "  ", null }) {
				assertThrows(operation.getKey() + " with " + bad, IllegalArgumentException.class,
						() -> operation.getValue().accept(bad));
			}
		}
		// no expectations were registered, so any request that slipped through would already have failed
		mockServer.verify();
	}

	@Test
	public void valuesThatOnlyLookLikeDotSegmentsAreStillAccepted() {
		for (String ok : new String[] { "...", "a..b", ".hidden", "x." }) {
			mockServer.reset();
			URI uri = requested("{}", () -> salesforce.sObjectsOperations().getRow("Account", ok));
			assertTrue(uri.getRawPath(), uri.getRawPath().endsWith("/" + ok));
		}
	}

	// ---- query and search text reaches the server exactly as written ----

	private static final String[] AWKWARD_TEXT = {
			"SELECT Id FROM Contact WHERE Email = 'a+b@x.com'",
			"SELECT Id FROM Account WHERE Name = 'AT&T'",
			"SELECT Id FROM Account WHERE Name = 'a=b&c=d'",
			"SELECT Id FROM Account WHERE Name LIKE '100%'",
			"SELECT Id FROM Account WHERE Name = 'A#1'",
			"SELECT Id FROM Account WHERE Name = 'café'",
			"SELECT Id FROM Account WHERE Name = '+&+&'" };

	private void assertReadBackExactly(String original, URI uri) {
		String raw = uri.getRawQuery();
		assertTrue(raw, raw.startsWith("q="));
		assertFalse("a raw '&' would end the parameter: " + raw, raw.contains("&"));
		assertFalse("a raw '+' would be read as a space: " + raw, raw.contains("+"));
		assertEquals(original, URLDecoder.decode(raw.substring(2), StandardCharsets.UTF_8));
	}

	@Test
	public void queryTextIsReadBackExactlyByTheServer() {
		for (String soql : AWKWARD_TEXT) {
			mockServer.reset();
			URI uri = requested("{\"totalSize\":0,\"done\":true,\"records\":[]}",
					() -> salesforce.queryOperations().query(soql));
			assertReadBackExactly(soql, uri);
		}
	}

	@Test
	public void queryAllEndpointAlsoEncodesTheQuery() {
		URI uri = requested("{\"totalSize\":0,\"done\":true,\"records\":[]}",
				() -> salesforce.queryOperations().query("SELECT Id FROM Account WHERE Name = 'AT&T'", true));

		assertTrue(uri.getRawPath(), uri.getRawPath().endsWith("/queryAll"));
		assertReadBackExactly("SELECT Id FROM Account WHERE Name = 'AT&T'", uri);
	}

	@Test
	public void searchTextIsReadBackExactlyByTheServer() {
		for (String sosl : new String[] { "FIND {a+b&c} IN ALL FIELDS", "FIND {AT&T} IN NAME FIELDS", "FIND {100%} RETURNING Account(Id)" }) {
			mockServer.reset();
			URI uri = requested("[]", () -> salesforce.searchOperations().search(sosl));
			assertReadBackExactly(sosl, uri);
		}
	}

	@Test
	public void ordinaryQueriesAreSentAsBefore() {
		URI uri = requested("{\"totalSize\":0,\"done\":true,\"records\":[]}",
				() -> salesforce.queryOperations().query("SELECT Id, Name FROM Account WHERE Name = 'Acme'"));

		assertEquals("q=SELECT%20Id,%20Name%20FROM%20Account%20WHERE%20Name%20=%20'Acme'", uri.getRawQuery());
	}
}
