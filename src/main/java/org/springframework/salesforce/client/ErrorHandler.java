/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-salesforce
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.salesforce.api.InvalidAuthorizationException;
import org.springframework.salesforce.api.OperationNotPermittedException;
import org.springframework.salesforce.api.RateLimitExceededException;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * Error handler for OAuth2 token requests.
 *
 * <p>Only HTTP 400 responses are translated; the {@code error} field of the body selects the
 * exception (see {@link #handleError(ClientHttpResponse)}). Other statuses use the default
 * handling.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class ErrorHandler extends DefaultResponseErrorHandler {

  /** Creates an error handler for OAuth2 token endpoint responses. */
  public ErrorHandler() {}

  /** Name of the error code field in an OAuth2 error body. */
  private static final String ERROR = "error";

  /** Name of the error description field in an OAuth2 error body. */
  private static final String ERROR_DESCRIPTION = "error_description";

  /** Mapper used to parse the error response body. */
  private static final ObjectMapper JSON = new ObjectMapper();

  /**
   * Translates an HTTP 400 OAuth2 error response into an exception, based on the {@code error}
   * field: authorization problems such as {@code invalid_client_id}, {@code
   * invalid_client_credentials}, {@code invalid_scope} and {@code invalid_grant} (except when the
   * {@code error_description} is exactly {@code IP restricted or invalid login hours}, which
   * becomes {@link OperationNotPermittedException}) become {@link InvalidAuthorizationException};
   * {@code rate_limit_exceeded} becomes {@link RateLimitExceededException} (with a fixed message
   * rather than the Salesforce description); everything else, including a missing or unreadable
   * error, becomes {@link OperationNotPermittedException}. Responses with any other status are
   * ignored.
   *
   * @param response the response to inspect
   * @throws IOException if the response body cannot be read
   * @throws InvalidAuthorizationException if the credentials, client or scope were rejected
   * @throws OperationNotPermittedException if the request was invalid or not permitted
   * @throws RateLimitExceededException if the rate limit was exceeded
   */
  public void handleError(@NonNull ClientHttpResponse response) throws IOException {

    if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
      Map<String, String> error = extractErrorDetailsFromResponse(response);

      // A missing error value (e.g. an unparseable body) falls through to the default case
      switch (Objects.toString(error.get(ERROR), "")) {
        case "unsupported_response_type" ->
            throw new OperationNotPermittedException(describe(error));
        case "invalid_client_id" -> throw new InvalidAuthorizationException(describe(error));
        case "invalid_request" -> throw new OperationNotPermittedException(describe(error));
        case "invalid_client_credentials" ->
            throw new InvalidAuthorizationException(describe(error));
        case "invalid_grant" -> {
          if ("IP restricted or invalid login hours".equals(error.get(ERROR_DESCRIPTION))) {
            throw new OperationNotPermittedException(describe(error));
          }
          throw new InvalidAuthorizationException(describe(error));
        }
        case "inactive_user" -> throw new OperationNotPermittedException(describe(error));
        case "inactive_org" -> throw new OperationNotPermittedException(describe(error));
        case "rate_limit_exceeded" -> throw new RateLimitExceededException("Rate limit exceeded");
        case "invalid_scope" -> throw new InvalidAuthorizationException(describe(error));
        default -> throw new OperationNotPermittedException(describe(error));
      }
    }
  }

  /**
   * Handles an error response; a 400 is translated as in {@link #handleError(ClientHttpResponse)},
   * anything else uses the default handling.
   *
   * @param url the request URL
   * @param method the request method
   * @param response the response to inspect
   * @throws IOException if the response body cannot be read
   * @throws InvalidAuthorizationException for a 400 whose error means the credentials, client or
   *     scope were rejected
   * @throws OperationNotPermittedException for any other 400 except rate limiting
   * @throws RateLimitExceededException for a 400 whose error is {@code rate_limit_exceeded}
   * @throws org.springframework.web.client.RestClientException for other error statuses, from the
   *     default handling
   */
  @Override
  public void handleError(
      @NonNull URI url, @NonNull HttpMethod method, @NonNull ClientHttpResponse response)
      throws IOException {
    if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
      // Always throws for a 400. If that ever changes, fall through to the default handling below.
      handleError(response);
    }
    super.handleError(url, method, response);
  }

  /**
   * Reads the OAuth error object ({@code error} and {@code error_description}). A body that is
   * empty, not JSON, or not an object yields no detail rather than an error, so the 400 is still
   * reported.
   *
   * @param response the response whose body is parsed
   * @return the scalar fields of the error object by name, or an empty map if there is no readable
   *     detail
   * @throws IOException if the response body cannot be read
   */
  private Map<String, String> extractErrorDetailsFromResponse(ClientHttpResponse response)
      throws IOException {
    try {
      JsonNode root = JSON.readTree(response.getBody());
      if (root.isObject()) {
        Map<String, String> details = new HashMap<>();
        root.fields()
            .forEachRemaining(
                e -> {
                  if (e.getValue().isValueNode() && !e.getValue().isNull()) {
                    details.put(e.getKey(), e.getValue().asText());
                  }
                });
        return details;
      }
    } catch (JsonProcessingException e) {
      // not JSON; fall through to no detail
    }
    return Collections.emptyMap();
  }

  /**
   * Builds the exception message: the description Salesforce gave, else the error code, else a note
   * that the response had no detail.
   *
   * @param error the parsed error fields
   * @return the message to use
   */
  private static String describe(Map<String, String> error) {
    String description = error.get(ERROR_DESCRIPTION);
    if (description != null) {
      return description;
    }
    String code = error.get(ERROR);
    return code != null ? code : "Salesforce returned a 400 response with no readable error detail";
  }
}
