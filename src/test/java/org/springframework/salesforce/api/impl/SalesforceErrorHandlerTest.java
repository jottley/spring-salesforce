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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.salesforce.api.InsufficientPermissionException;
import org.springframework.salesforce.api.InternalServerErrorException;
import org.springframework.salesforce.api.InvalidAuthorizationException;
import org.springframework.salesforce.api.RateLimitExceededException;
import org.springframework.salesforce.api.ResourceNotFoundException;
import org.springframework.salesforce.api.SalesforceRequestException;
import org.springframework.salesforce.api.UncategorizedApiException;

/**
 * Unit tests for {@link SalesforceErrorHandler} status and error code mapping.
 *
 * @author Jared Ottley
 */
public class SalesforceErrorHandlerTest {

	private final SalesforceErrorHandler handler = new SalesforceErrorHandler();

	private static MockClientHttpResponse response(HttpStatus status, String body) {
		return new MockClientHttpResponse(body.getBytes(StandardCharsets.UTF_8), status);
	}

	private static String error(String errorCode, String message) {
		return "[{\"errorCode\":\"" + errorCode + "\",\"message\":\"" + message + "\"}]";
	}

	@Test
	public void notFoundMapsToResourceNotFound() {
		ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
				() -> handler.handleError(response(HttpStatus.NOT_FOUND, error("NOT_FOUND", "no such record"))));
		assertEquals("no such record", e.getMessage());
	}

	@Test
	public void tooManyRequestsMapsToRateLimitExceeded() {
		assertThrows(RateLimitExceededException.class,
				() -> handler.handleError(response(HttpStatus.TOO_MANY_REQUESTS, error("X", "slow down"))));
	}

	@Test
	public void serviceUnavailableMapsToRateLimitExceeded() {
		// Current behavior; see proposals/THROTTLING_PROPOSAL.md (Phase 4) for the planned change.
		assertThrows(RateLimitExceededException.class,
				() -> handler.handleError(response(HttpStatus.SERVICE_UNAVAILABLE, error("X", "unavailable"))));
	}

	@Test
	public void internalServerErrorUsesSalesforceMessage() {
		InternalServerErrorException e = assertThrows(InternalServerErrorException.class,
				() -> handler.handleError(response(HttpStatus.INTERNAL_SERVER_ERROR, error("UNKNOWN_EXCEPTION", "boom"))));
		assertEquals("boom", e.getMessage());
	}

	@Test
	public void badRequestMapsToSalesforceRequestExceptionWithDetails() {
		String body = "[{\"errorCode\":\"REQUIRED_FIELD_MISSING\",\"message\":\"Required fields are missing: [Name]\",\"fields\":[\"Name\"]}]";
		SalesforceRequestException e = assertThrows(SalesforceRequestException.class,
				() -> handler.handleError(response(HttpStatus.BAD_REQUEST, body)));
		assertEquals("REQUIRED_FIELD_MISSING", e.getCode());
		assertEquals(List.of("Name"), e.getFields());
		assertEquals("Required fields are missing: [Name]", e.getMessage());
	}

	@Test
	public void multipleChoicesMapsToSalesforceRequestException() {
		assertThrows(SalesforceRequestException.class,
				() -> handler.handleError(response(HttpStatus.MULTIPLE_CHOICES, error("DUPLICATES_DETECTED", "dup"))));
	}

	@Test
	public void unauthorizedMapsToInvalidAuthorization() {
		InvalidAuthorizationException e = assertThrows(InvalidAuthorizationException.class,
				() -> handler.handleError(response(HttpStatus.UNAUTHORIZED, error("INVALID_SESSION_ID", "Session expired"))));
		assertEquals("Session expired", e.getMessage());
	}

	@Test
	public void forbiddenBadOAuthTokenMapsToInvalidAuthorization() {
		assertThrows(InvalidAuthorizationException.class,
				() -> handler.handleError(response(HttpStatus.FORBIDDEN, error("Bad_OAuth_Token", "bad token"))));
	}

	@Test
	public void forbiddenRequestLimitExceededMapsToRateLimit() {
		RateLimitExceededException e = assertThrows(RateLimitExceededException.class,
				() -> handler.handleError(response(HttpStatus.FORBIDDEN, error("REQUEST_LIMIT_EXCEEDED", "TotalRequests Limit exceeded."))));
		assertEquals("TotalRequests Limit exceeded.", e.getMessage());
	}

	@Test
	public void forbiddenOtherCodeMapsToInsufficientPermission() {
		InsufficientPermissionException e = assertThrows(InsufficientPermissionException.class,
				() -> handler.handleError(response(HttpStatus.FORBIDDEN, error("INSUFFICIENT_ACCESS", "no access"))));
		assertEquals("no access", e.getMessage());
	}

	@Test
	public void unmappedStatusMapsToUncategorized() {
		UncategorizedApiException e = assertThrows(UncategorizedApiException.class,
				() -> handler.handleError(response(HttpStatus.I_AM_A_TEAPOT, error("X", "teapot"))));
		assertEquals("teapot", e.getMessage());
	}

	@Test
	public void nonStandardStatusMapsToUncategorizedWithSalesforceMessage() {
		// 599 has no HttpStatus constant; this used to fail with a NullPointerException
		UncategorizedApiException e = assertThrows(UncategorizedApiException.class, () -> handler
				.handleError(new MockClientHttpResponse(error("X", "gateway trouble").getBytes(StandardCharsets.UTF_8),
						HttpStatusCode.valueOf(599))));
		assertEquals("gateway trouble", e.getMessage());
	}

	@Test
	public void nonStandardStatusWithoutMessageNamesTheStatus() {
		UncategorizedApiException e = assertThrows(UncategorizedApiException.class, () -> handler
				.handleError(new MockClientHttpResponse("[]".getBytes(StandardCharsets.UTF_8), HttpStatusCode.valueOf(599))));
		assertEquals("Unexpected HTTP status 599", e.getMessage());
	}

	@Test
	public void nonJsonBadOAuthTokenBodyMapsToInvalidAuthorization() {
		// Salesforce sometimes returns the bare string Bad_OAuth_Token instead of JSON
		InvalidAuthorizationException e = assertThrows(InvalidAuthorizationException.class,
				() -> handler.handleError(response(HttpStatus.FORBIDDEN, "Bad_OAuth_Token")));
		assertEquals("Invalid access token", e.getMessage());
	}

	@Test
	public void nonJsonUnknownBodyMapsToUncategorized() {
		UncategorizedApiException e = assertThrows(UncategorizedApiException.class,
				() -> handler.handleError(response(HttpStatus.INTERNAL_SERVER_ERROR, "<html>oops</html>")));
		assertEquals("Unable to read salesforce response.", e.getMessage());
	}

	@Test
	public void emptyErrorListOnInternalServerErrorHasNullMessage() {
		InternalServerErrorException e = assertThrows(InternalServerErrorException.class,
				() -> handler.handleError(response(HttpStatus.INTERNAL_SERVER_ERROR, "[]")));
		assertNull(e.getMessage());
	}

	@Test
	public void forbiddenWithEmptyErrorListIsHandled() {
		// An empty error list yields no errorCode; this must not blow up with a NullPointerException.
		assertThrows(InsufficientPermissionException.class,
				() -> handler.handleError(response(HttpStatus.FORBIDDEN, "[]")));
	}

	@Test
	public void handleErrorWithUrlAndMethodDelegates() {
		assertThrows(ResourceNotFoundException.class, () -> handler.handleError(
				URI.create("https://na7.salesforce.com/services/data/v37.0/sobjects/Account/001"), HttpMethod.GET,
				response(HttpStatus.NOT_FOUND, error("NOT_FOUND", "gone"))));
	}

	@Test
	public void unreadableBodyMapsToUncategorized() throws IOException {
		MockClientHttpResponse broken = new MockClientHttpResponse(new java.io.InputStream() {
			@Override
			public int read() throws IOException {
				throw new IOException("stream closed");
			}
		}, HttpStatus.INTERNAL_SERVER_ERROR);
		UncategorizedApiException e = assertThrows(UncategorizedApiException.class, () -> handler.handleError(broken));
		assertEquals("Unable to extract Salesforce response.", e.getMessage());
	}
}
