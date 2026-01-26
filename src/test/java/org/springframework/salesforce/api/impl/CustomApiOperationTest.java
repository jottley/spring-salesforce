/**
 * Copyright (C) 2021-2026 https://github.com/jottley/spring-social-salesforce
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
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Sanchit Agarwal
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomApiOperationTest extends AbstractSalesforceTest {

	@Test
	public void getOperation() {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/apexrest/xxx/test"))
		.andExpect(method(GET))
		.andRespond(withStatus(HttpStatus.OK).body(loadResource("customApi1.json")).headers(responseHeaders));

		Map<String, Object> result = salesforce.customApiOperations().getForApexObject("/xxx/test", Map.class);

		assertEquals(false, result.get("allowChatterAccessWithoutLogin"));
		assertEquals(false, result.get("allowMembersToFlag"));
		assertEquals(null, result.get("description"));
		assertEquals(false, result.get("guestMemberVisibilityEnabled"));
		assertEquals("commId3", result.get("id"));
		assertEquals(false, result.get("invitationsEnabled"));
	}

	@Test
	public void getOperationWithParameterizedUrl() {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/apexrest/xxx/test"))
		.andExpect(method(GET))
		.andRespond(withStatus(HttpStatus.OK).body(loadResource("customApi1.json")).headers(responseHeaders));

		Map<String, Object> result = salesforce.customApiOperations().getForApexObject("/xxx/{uri}", Map.class, new HashMap<String, Object>() {{ put("uri", "test"); }});

		assertEquals(false, result.get("allowChatterAccessWithoutLogin"));
		assertEquals(false, result.get("allowMembersToFlag"));
		assertEquals(null, result.get("description"));
		assertEquals(false, result.get("guestMemberVisibilityEnabled"));
		assertEquals("commId3", result.get("id"));
		assertEquals(false, result.get("invitationsEnabled"));
	}

	@Test
	public void deleteOperation() throws IOException {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/apexrest/xxx/account/1"))
		.andExpect(method(HttpMethod.DELETE))
		.andRespond(withStatus(HttpStatus.OK).body("Deleted".getBytes("UTF-8")).headers(responseHeaders));

		String result = salesforce.customApiOperations().deleteForApexObject("/xxx/account/1", String.class);

		assertEquals("Deleted", result);
	}

	@Test
	public void deleteOperationForParameterizedUrl() throws IOException {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/apexrest/xxx/account/1"))
		.andExpect(method(HttpMethod.DELETE))
		.andRespond(withStatus(HttpStatus.OK).body("Deleted".getBytes("UTF-8")).headers(responseHeaders));

		String result = salesforce.customApiOperations().deleteForApexObject("/xxx/{objectFriendlyName}/{pk}", String.class, new HashMap<String, Object>() {{ put("objectFriendlyName", "account"); put("pk", "1");  }});

		assertEquals("Deleted", result);
	}

	@Test
	public void postOperation() throws IOException {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/apexrest/xxx/contact"))
		.andExpect(method(HttpMethod.POST))
		.andRespond(withStatus(HttpStatus.OK).body(new ByteArrayResource("{\"Id\" : \"1\"}".getBytes("UTF-8"))).headers(responseHeaders));

		Map<String, Object> result = salesforce.customApiOperations().postForApexObject("/xxx/contact", new HashMap<String, Object>() {{ put("LastName", "Agarwal"); }}, Map.class);

		assertEquals("1", result.get("Id"));
	}


	@Test
	public void postOperationForParameterizedUrl() throws IOException {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/apexrest/xxx/contact"))
		.andExpect(method(HttpMethod.POST))
		.andRespond(withStatus(HttpStatus.OK).body(new ByteArrayResource("{\"Id\" : \"1\"}".getBytes("UTF-8"))).headers(responseHeaders));

		Map<String, Object> objectMap = new HashMap<String, Object>() {{ put("LastName", "Agarwal"); }};
		Map<String, Object> result = salesforce.customApiOperations().postForApexObject("/xxx/{objectFriendlyName}", objectMap, Map.class, new HashMap<String, Object>() {{ put("objectFriendlyName", "contact"); }});

		assertEquals("1", result.get("Id"));
	}

	@Test
	public void patchOperation() throws IOException {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/apexrest/xxx/contact"))
		.andExpect(method(HttpMethod.PATCH))
		.andRespond(withStatus(HttpStatus.OK).body(new ByteArrayResource("{\"LastName\" : \"Aggarwal\"}".getBytes("UTF-8"))).headers(responseHeaders));

		Map<String, Object> result = salesforce.customApiOperations().patchForApexObject("/xxx/contact", new HashMap<String, Object>() {{ put("LastName", "Agarwal"); }}, Map.class);

		assertEquals("Aggarwal", result.get("LastName"));
	}


	@Test
	public void patchOperationForParameterizedUrl() throws IOException {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/apexrest/xxx/contact"))
		.andExpect(method(HttpMethod.PATCH))
		.andRespond(withStatus(HttpStatus.OK).body(new ByteArrayResource("{\"LastName\" : \"Aggarwal\"}".getBytes("UTF-8"))).headers(responseHeaders));

		Map<String, Object> objectMap = new HashMap<String, Object>() {{ put("LastName", "Agarwal"); }};
		Map<String, Object> result = salesforce.customApiOperations().patchForApexObject("/xxx/{objectFriendlyName}", objectMap, Map.class, new HashMap<String, Object>() {{ put("objectFriendlyName", "contact"); }});

		assertEquals("Aggarwal", result.get("LastName"));
	}

	@Test
	public void putOperation() throws IOException {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/apexrest/xxx/contact"))
		.andExpect(method(HttpMethod.PUT))
		.andRespond(withStatus(HttpStatus.OK).body(new ByteArrayResource("{\"Id\" : \"1\"}".getBytes("UTF-8"))).headers(responseHeaders));

		Map<String, Object> result = salesforce.customApiOperations().putForApexObject("/xxx/contact", new HashMap<String, Object>() {{ put("LastName", "Agarwal"); }}, Map.class);

		assertEquals("1", result.get("Id"));
	}


	@Test
	public void putOperationForParameterizedUrl() throws IOException {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/apexrest/xxx/contact"))
		.andExpect(method(HttpMethod.PUT))
		.andRespond(withStatus(HttpStatus.OK).body(new ByteArrayResource("{\"Id\" : \"1\"}".getBytes("UTF-8"))).headers(responseHeaders));

		Map<String, Object> objectMap = new HashMap<String, Object>() {{ put("Id", "1"); }};
		Map<String, Object> result = salesforce.customApiOperations().putForApexObject("/xxx/{objectFriendlyName}", objectMap, Map.class, new HashMap<String, Object>() {{ put("objectFriendlyName", "contact"); }});

		assertEquals("1", result.get("Id"));
	}


	@Test
	public void executeApexApi() throws IOException {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/apexrest/xxx/contact"))
		.andExpect(method(HttpMethod.PUT))
		.andRespond(withStatus(HttpStatus.OK).body(new ByteArrayResource("{\"Id\" : \"1\"}".getBytes("UTF-8"))).headers(responseHeaders));

		ResponseEntity<Map> result = salesforce.customApiOperations().executeApexApi("/xxx/contact", HttpMethod.PUT, new HttpEntity<>(new HashMap<String, Object>() {{ put("LastName", "Agarwal"); }}), Map.class);

		assertEquals("1", result.getBody().get("Id"));
	}

	@Test
	public void executeApexApiWithParameterizedUri() throws IOException {
		mockServer.expect(requestTo("https://na7.salesforce.com/services/apexrest/xxx/contact"))
		.andExpect(method(HttpMethod.POST))
		.andRespond(withStatus(HttpStatus.OK).body(new ByteArrayResource("{\"Id\" : \"1\"}".getBytes("UTF-8"))).headers(responseHeaders));

		ResponseEntity<Map> result = salesforce.customApiOperations().executeApexApi("/xxx/{objectFriendlyName}", HttpMethod.POST, new HttpEntity<>(new HashMap<String, Object>() {{ put("LastName", "Agarwal"); }}), Map.class, new HashMap<String, Object>() {{ put("objectFriendlyName", "contact"); }});

		assertEquals("1", result.getBody().get("Id"));
	}
}