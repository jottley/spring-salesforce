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
package org.springframework.salesforce.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.salesforce.api.InvalidAuthorizationException;
import org.springframework.salesforce.api.OperationNotPermittedException;
import org.springframework.salesforce.api.RateLimitExceededException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Unit tests for the OAuth2 {@link ErrorHandler}.
 *
 * @author Jared Ottley
 */
public class ErrorHandlerTest {

  private static final URI URL = URI.create("https://login.salesforce.com/services/oauth2/token");

  private final ErrorHandler handler = new ErrorHandler();

  private static MockClientHttpResponse badRequest(String error, String description) {
    String body = "{\"error\":\"" + error + "\",\"error_description\":\"" + description + "\"}";
    return new MockClientHttpResponse(
        body.getBytes(StandardCharsets.UTF_8), HttpStatus.BAD_REQUEST);
  }

  private void assertMaps(
      Class<? extends RuntimeException> type, String error, String description) {
    RuntimeException e =
        assertThrows(
            type, () -> handler.handleError(URL, HttpMethod.POST, badRequest(error, description)));
    assertEquals(description, e.getMessage());
  }

  @Test
  public void operationNotPermittedErrors() {
    assertMaps(
        OperationNotPermittedException.class, "unsupported_response_type", "bad response type");
    assertMaps(OperationNotPermittedException.class, "invalid_request", "bad request");
    assertMaps(OperationNotPermittedException.class, "inactive_user", "user inactive");
    assertMaps(OperationNotPermittedException.class, "inactive_org", "org inactive");
    assertMaps(OperationNotPermittedException.class, "something_new", "unrecognized");
  }

  @Test
  public void invalidAuthorizationErrors() {
    assertMaps(InvalidAuthorizationException.class, "invalid_client_id", "bad client id");
    assertMaps(InvalidAuthorizationException.class, "invalid_client_credentials", "bad creds");
    assertMaps(InvalidAuthorizationException.class, "invalid_scope", "bad scope");
  }

  @Test
  public void invalidGrantVariants() {
    assertMaps(InvalidAuthorizationException.class, "invalid_grant", "invalid user credentials");
    assertMaps(
        OperationNotPermittedException.class,
        "invalid_grant",
        "IP restricted or invalid login hours");
    assertMaps(
        InvalidAuthorizationException.class, "invalid_grant", "expired access/refresh token");
  }

  @Test
  public void rateLimitExceeded() {
    RateLimitExceededException e =
        assertThrows(
            RateLimitExceededException.class,
            () ->
                handler.handleError(
                    URL, HttpMethod.POST, badRequest("rate_limit_exceeded", "too many")));
    assertEquals("Rate limit exceeded", e.getMessage());
  }

  @Test
  public void handleErrorWithoutUrlOnBadRequest() {
    assertThrows(
        InvalidAuthorizationException.class,
        () -> handler.handleError(badRequest("invalid_client_id", "bad client id")));
  }

  @Test
  public void nonBadRequestIsLeftToDefaultHandling() throws Exception {
    MockClientHttpResponse response =
        new MockClientHttpResponse(
            "{}".getBytes(StandardCharsets.UTF_8), HttpStatus.INTERNAL_SERVER_ERROR);
    assertThrows(
        HttpServerErrorException.class, () -> handler.handleError(URL, HttpMethod.POST, response));
    // The single-argument form does nothing for non-400 statuses.
    handler.handleError(response);
  }

  @Test
  public void emptyOrUnreadableBadRequestBodiesAreStillReported() {
    for (String body : new String[] {"", "<html/>", "[]", "[1]", "42", "\"text\"", "null"}) {
      MockClientHttpResponse response =
          new MockClientHttpResponse(body.getBytes(StandardCharsets.UTF_8), HttpStatus.BAD_REQUEST);
      OperationNotPermittedException e =
          assertThrows(
              body,
              OperationNotPermittedException.class,
              () -> handler.handleError(URL, HttpMethod.POST, response));
      assertEquals(
          body, "Salesforce returned a 400 response with no readable error detail", e.getMessage());
    }
  }

  @Test
  public void errorCodeIsUsedWhenThereIsNoDescription() {
    MockClientHttpResponse response =
        new MockClientHttpResponse(
            "{\"error\":\"invalid_client_id\"}".getBytes(StandardCharsets.UTF_8),
            HttpStatus.BAD_REQUEST);
    InvalidAuthorizationException e =
        assertThrows(
            InvalidAuthorizationException.class,
            () -> handler.handleError(URL, HttpMethod.POST, response));
    assertEquals("invalid_client_id", e.getMessage());
  }

  @Test
  public void nonStringValuesDoNotCauseClassCastExceptions() {
    MockClientHttpResponse response =
        new MockClientHttpResponse(
            "{\"error\":\"invalid_request\",\"error_description\":42,\"extra\":{\"a\":1},\"gone\":null}"
                .getBytes(StandardCharsets.UTF_8),
            HttpStatus.BAD_REQUEST);
    OperationNotPermittedException e =
        assertThrows(
            OperationNotPermittedException.class,
            () -> handler.handleError(URL, HttpMethod.POST, response));
    assertEquals("42", e.getMessage());
  }
}
