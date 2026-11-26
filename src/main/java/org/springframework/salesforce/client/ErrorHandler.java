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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
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

    public void handleError(@NonNull ClientHttpResponse response) throws IOException {

        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            Map<String, String> error = extractErrorDetailsFromResponse(response);

            // A missing error value (e.g. an unparseable body) falls through to the default case
            switch (Objects.toString(error.get(ERROR), "")) {
                case "unsupported_response_type" -> throw new OperationNotPermittedException(error.get(ERROR_DESCRIPTION));
                case "invalid_client_id" -> throw new InvalidAuthorizationException(error.get(ERROR_DESCRIPTION));
                case "invalid_request" -> throw new OperationNotPermittedException(error.get(ERROR_DESCRIPTION));
                case "invalid_client_credentials" -> throw new InvalidAuthorizationException(error.get(ERROR_DESCRIPTION));
                case "invalid_grant" -> {
                    if ("invalid user credentials".equals(error.get(ERROR_DESCRIPTION))) {
                        throw new InvalidAuthorizationException(error.get(ERROR_DESCRIPTION));
                    } else if ("IP restricted or invalid login hours".equals(error.get(ERROR_DESCRIPTION))) {
                        throw new OperationNotPermittedException(error.get(ERROR_DESCRIPTION));
                    }
                    throw new InvalidAuthorizationException(error.get(ERROR_DESCRIPTION));
                }
                case "inactive_user" -> throw new OperationNotPermittedException(error.get(ERROR_DESCRIPTION));
                case "inactive_org" -> throw new OperationNotPermittedException(error.get(ERROR_DESCRIPTION));
                case "rate_limit_exceeded" -> throw new RateLimitExceededException("Rate limit exceeded");
                case "invalid_scope" -> throw new InvalidAuthorizationException(error.get(ERROR_DESCRIPTION));
                default -> throw new OperationNotPermittedException(error.get(ERROR_DESCRIPTION));
            }
        }
    }

    @Override
    public void handleError(@NonNull URI url, @NonNull HttpMethod method, @NonNull ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            handleError(response);
            // if not handled, fall through to default handling
            super.handleError(url, method, response);
            return;
        }
        super.handleError(url, method, response);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> extractErrorDetailsFromResponse(ClientHttpResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        try {
            return mapper.readValue(response.getBody(), Map.class);
        } catch (JsonParseException e) {
            return Collections.emptyMap();
        }
    }

}
