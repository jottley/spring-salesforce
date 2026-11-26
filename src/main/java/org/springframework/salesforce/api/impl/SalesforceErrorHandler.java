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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.salesforce.api.InsufficientPermissionException;
import org.springframework.salesforce.api.InternalServerErrorException;
import org.springframework.salesforce.api.InvalidAuthorizationException;
import org.springframework.salesforce.api.RateLimitExceededException;
import org.springframework.salesforce.api.ResourceNotFoundException;
import org.springframework.salesforce.api.SalesforceRequestException;
import org.springframework.salesforce.api.UncategorizedApiException;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SalesforceErrorHandler extends DefaultResponseErrorHandler {
    Logger logger = LoggerFactory.getLogger(SalesforceErrorHandler.class);

    private static final String ERROR_CODE = "errorCode";
    private static final String BAD_OAUTH_TOKEN = "Bad_OAuth_Token";
    private static final String REQUEST_LIMIT_EXCEEDED = "REQUEST_LIMIT_EXCEEDED";
    private static final String MESSAGE = "message";
    private static final String SESSION_EXPIRED_INVALID = "Invalid access token";

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() { };

    public void handleError(@NonNull ClientHttpResponse response) throws IOException {
        int statusValue = response.getStatusCode().value();
        HttpStatus statusCode = HttpStatus.resolve(statusValue);

        // The HTTP status decides which exception is thrown. The body only supplies detail, and is often
        // missing or not JSON (gateway errors, empty 503s, Apex REST custom bodies), so it must never
        // prevent the status from being mapped.
        Map<String, Object> errorDetails = extractErrorDetailsFromResponse(response);
        Object message = errorDetails.get(MESSAGE);
        errorDetails.put(MESSAGE, message != null ? message.toString() : fallbackMessage(statusCode, statusValue));

        if (statusCode == null) {
            // Not a standard HTTP status (for example 599 from a proxy), so it has no HttpStatus constant to switch on
            throw new UncategorizedApiException(extractErrorMessage(errorDetails));
        }
        handleSalesforceError(statusCode, errorDetails);
        // No need for try-catch - handleSalesforceError will always throw an appropriate exception
    }

    @Override
    public void handleError(@NonNull URI url, @NonNull HttpMethod method, @NonNull ClientHttpResponse response) throws IOException {
        handleError(response);
    }

    private static String fallbackMessage(HttpStatus statusCode, int statusValue) {
        return statusCode == null
                ? "Unexpected HTTP status " + statusValue
                : statusValue + " " + statusCode.getReasonPhrase();
    }

    private void handleSalesforceError(HttpStatus statusCode, Map<String, Object> errorDetails) {
        switch (statusCode) {
            case NOT_FOUND -> throw new ResourceNotFoundException(
                    extractErrorMessage(errorDetails)
                );
            case TOO_MANY_REQUESTS, SERVICE_UNAVAILABLE -> throw new RateLimitExceededException(
                    "Rate limit exceeded"
                );
            case INTERNAL_SERVER_ERROR -> throw new InternalServerErrorException(
                    extractErrorMessage(errorDetails)
                );
            case BAD_REQUEST, MULTIPLE_CHOICES -> throw new SalesforceRequestException(
                    errorDetails
                );
            case UNAUTHORIZED -> throw new InvalidAuthorizationException(
                    extractErrorMessage(errorDetails)
                );
            // The called method throws the exceptions
            case FORBIDDEN -> handleForbiddenError(errorDetails);
            default -> throw new UncategorizedApiException(
                    extractErrorMessage(errorDetails)
                );
        }
    }

    private void handleForbiddenError(Map<String, Object> errorDetails) {
        String message = extractErrorMessage(errorDetails);

        // A missing errorCode falls through to the default case rather than throwing a NullPointerException
        switch (Objects.toString(errorDetails.get(ERROR_CODE), "")) {
            case BAD_OAUTH_TOKEN -> throw new InvalidAuthorizationException(message);
            case REQUEST_LIMIT_EXCEEDED -> throw new RateLimitExceededException(message);
            default -> throw new InsufficientPermissionException(message);
        }
    }

    /**
     * Reads whatever error detail the body offers. Salesforce normally sends a JSON array of error objects,
     * but a single JSON object is accepted too. Anything else (empty, HTML, plain text) yields no detail
     * rather than an error. The returned map is always mutable and never null.
     */
    private Map<String, Object> extractErrorDetailsFromResponse(ClientHttpResponse response) {
        //Extract the response body so that it can be reused.  The InputStream provided by the response is closed after single use.
        byte[] body = extractResponseBodyAsByteArray(response);

        String text = new String(body, StandardCharsets.UTF_8);
        try {
            JsonNode root = JSON.readTree(body);
            JsonNode error = root.isArray() && !root.isEmpty() ? root.get(0) : root;
            if (error.isObject()) {
                return new HashMap<>(JSON.convertValue(error, MAP_TYPE));
            }
        } catch (IOException e) {
            // Not JSON (a JsonProcessingException; reading an in-memory array cannot fail otherwise).
            // The bare Bad_OAuth_Token case is handled below, anything else carries no detail.
        }

        //Salesforce is returning Bad_OAuth_Token as a bare (sometimes quoted) string.  We need to handle this case.
        String trimmed = text.trim();
        if (BAD_OAUTH_TOKEN.equals(trimmed) || ("\"" + BAD_OAUTH_TOKEN + "\"").equals(trimmed)) {
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put(ERROR_CODE, BAD_OAUTH_TOKEN);
            errorDetails.put(MESSAGE, SESSION_EXPIRED_INVALID);

            return errorDetails;
        }

        if (logger.isDebugEnabled() && body.length > 0) {
            logger.debug("Salesforce error response has no readable error detail: {}",
                    text.length() > 500 ? text.substring(0, 500) + "..." : text);
        }
        return new HashMap<>();
    }

    private String extractErrorMessage(Map<String, Object> errorDetails) {
        return (String) errorDetails.get(MESSAGE);
    }

    /**
     * The ClientHttpResponse is only valid for a single use before it is closed.  We need to pull it out into a reusable byte array
     * so we can use it in other places.
     *
     * @param response
     * @return byte[]
     */
    private byte[] extractResponseBodyAsByteArray(ClientHttpResponse response)
    {
        try{
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] data = new byte[1024];
            int length;
            while ((length = response.getBody().read(data)) != -1)
            {
                buffer.write(data, 0, length);
            }

            return buffer.toByteArray();
        }
        catch (IOException e)
        {
            throw new UncategorizedApiException("Unable to extract Salesforce response.", e);
        }
    }
}
