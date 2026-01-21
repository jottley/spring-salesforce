/**
 * Copyright (C) 2017 https://github.com/jottley/spring-social-salesforce
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
package org.springframework.social.salesforce.client;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.social.salesforce.api.*;
import org.springframework.social.salesforce.connect.SalesforceServiceProvider;
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

    public void handleError(@NonNull ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            //TODO move to a switch
            Map<String, String> error = extractErrorDetailsFromResponse(response);
            if ("unsupported_response_type".equals(error.get("error"))) {
                throw new OperationNotPermittedException(error.get("error_description"));
            } else if ("invalid_client_id".equals(error.get("error"))) {
                throw new InvalidAuthorizationException(error.get("error_description"));
            } else if ("invalid_request".equals(error.get("error"))) {
                throw new OperationNotPermittedException(error.get("error_description"));
            } else if ("invalid_client_credentials".equals(error.get("error"))) {
                throw new InvalidAuthorizationException(error.get("error_description"));
            } else if ("invalid_grant".equals(error.get("error"))) {
                if ("invalid user credentials".equals(error.get("error_description"))) {
                    throw new InvalidAuthorizationException(error.get("error_description"));
                } else if ("IP restricted or invalid login hours".equals(error.get("error_description"))) {
                    throw new OperationNotPermittedException(error.get("error_description"));
                }
                throw new InvalidAuthorizationException(error.get("error_description"));
            } else if ("inactive_user".equals(error.get("error"))) {
                throw new OperationNotPermittedException(error.get("error_description"));
            } else if ("inactive_org".equals(error.get("error"))) {
                throw new OperationNotPermittedException(error.get("error_description"));
            } else if ("rate_limit_exceeded".equals(error.get("error"))) {
                throw new RateLimitExceededException("Rate limit exceeded");
            } else if ("invalid_scope".equals(error.get("error"))) {
                throw new InvalidAuthorizationException(error.get("error_description"));
            }
        }

        // Call parent handleError with required parameters for Spring 7
        handleError(null, null, response);
    }
    
    @Override
    public void handleError(@NonNull URI url, @NonNull HttpMethod method, @NonNull ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            handleError(response);
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
