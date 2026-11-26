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
package org.springframework.salesforce.client;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom error handler for handling Salesforce API specific error responses.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class ErrorHandler extends DefaultResponseErrorHandler {

    private static final String ERROR = "error";
    private static final String ERROR_DESCRIPTION = "error_description";

    private static final ObjectMapper JSON = new ObjectMapper();

    public void handleError(@NonNull ClientHttpResponse response) throws IOException {

        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            Map<String, String> error = extractErrorDetailsFromResponse(response);

            // A missing error value (e.g. an unparseable body) falls through to the default case
            switch (Objects.toString(error.get(ERROR), "")) {
                case "unsupported_response_type" -> throw new OperationNotPermittedException(describe(error));
                case "invalid_client_id" -> throw new InvalidAuthorizationException(describe(error));
                case "invalid_request" -> throw new OperationNotPermittedException(describe(error));
                case "invalid_client_credentials" -> throw new InvalidAuthorizationException(describe(error));
                case "invalid_grant" -> {
                    if ("invalid user credentials".equals(error.get(ERROR_DESCRIPTION))) {
                        throw new InvalidAuthorizationException(describe(error));
                    } else if ("IP restricted or invalid login hours".equals(error.get(ERROR_DESCRIPTION))) {
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

    @Override
    public void handleError(@NonNull URI url, @NonNull HttpMethod method, @NonNull ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            // Always throws for a 400. If that ever changes, fall through to the default handling below.
            handleError(response);
        }
        super.handleError(url, method, response);
    }

    /**
     * Reads the OAuth error object ({@code error} and {@code error_description}). A body that is empty,
     * not JSON, or not an object yields no detail rather than an error, so the 400 is still reported.
     */
    private Map<String, String> extractErrorDetailsFromResponse(ClientHttpResponse response) throws IOException {
        try {
            JsonNode root = JSON.readTree(response.getBody());
            if (root.isObject()) {
                Map<String, String> details = new HashMap<>();
                root.fields().forEachRemaining(e -> {
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

    /** The description Salesforce gave, else the error code, else a note that the response had no detail. */
    private static String describe(Map<String, String> error) {
        String description = error.get(ERROR_DESCRIPTION);
        if (description != null) {
            return description;
        }
        String code = error.get(ERROR);
        return code != null ? code : "Salesforce returned a 400 response with no readable error detail";
    }

}
