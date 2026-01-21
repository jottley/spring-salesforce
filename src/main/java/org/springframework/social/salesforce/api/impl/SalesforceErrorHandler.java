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
package org.springframework.social.salesforce.api.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.social.salesforce.api.*;
import org.springframework.social.salesforce.connect.SalesforceServiceProvider;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SalesforceErrorHandler extends DefaultResponseErrorHandler {
    Logger logger = LoggerFactory.getLogger(SalesforceErrorHandler.class);

    private static final String ERROR_CODE = "errorCode";
    private static final String BAD_OAUTH_TOKEN = "Bad_OAuth_Token";
    private static final String MESSAGE = "message";
    private static final String SESSION_EXPIRED_INVALID = "Invalid access token";

    public void handleError(@NonNull ClientHttpResponse response) throws IOException {
        Map<String, Object> errorDetails = extractErrorDetailsFromResponse(response);

        HttpStatus statusCode = HttpStatus.resolve(response.getStatusCode().value());
        handleSalesforceError(statusCode, errorDetails);
        // No need for try-catch - handleSalesforceError will always throw an appropriate exception
    }
    
    @Override
    public void handleError(@NonNull URI url, @NonNull HttpMethod method, @NonNull ClientHttpResponse response) throws IOException {
        handleError(response);
    }

    private void handleSalesforceError(HttpStatus statusCode, Map<String, Object> errorDetails) {
        switch (statusCode) {
            case NOT_FOUND -> throw new ResourceNotFoundException(
                    extractErrorMessage(errorDetails)
                );
            case SERVICE_UNAVAILABLE -> throw new RateLimitExceededException(
                    "Rate limit exceeded"
                );
            case INTERNAL_SERVER_ERROR -> throw new InternalServerErrorException(
                    errorDetails == null ? "Contact Salesforce administrator." : extractErrorMessage(errorDetails)
                );
            case BAD_REQUEST, MULTIPLE_CHOICES -> throw new SalesforceRequestException(
                    SalesforceServiceProvider.ID,
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
        if (errorDetails == null) {
            throw new InsufficientPermissionException("Access forbidden - no details available");
        }

        String message = extractErrorMessage(errorDetails);

        if (BAD_OAUTH_TOKEN.equals(errorDetails.get(ERROR_CODE))) {
            throw new InvalidAuthorizationException(message);
        }

        throw new InsufficientPermissionException(message);
    }

    private void handleUncategorizedError(@NonNull ClientHttpResponse response, Map<String, Object> errorDetails) {
        String message = (errorDetails == null || errorDetails.isEmpty())
            ? "No error details from Salesforce."
            : extractErrorMessage(errorDetails);
        throw new UncategorizedApiException(message);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractErrorDetailsFromResponse(ClientHttpResponse response) throws IOException {
        //Extract the response body so that it can be reused.  The InputStream provided by the response is closed after single use.
        byte[]  body = extractResponseBodyAsByteArray(response);

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        try {
            CollectionType listType = TypeFactory.defaultInstance().constructCollectionType(List.class, Map.class);
            List<Map<String, Object>> errorList = (List<Map<String, Object>>) mapper.readValue(new ByteArrayInputStream(body), listType);
            if (!errorList.isEmpty()) {
                return errorList.get(0);
            }
        } catch (JsonParseException e) {
            //Salesforce is returning Bad_OAuth_Toekn in poorly formatted JSON.  We need to handle this case.
            if (BAD_OAUTH_TOKEN.equals(new String(body)))
            {
                Map<String, Object> errorDetails = new HashMap<>();
                errorDetails.put(ERROR_CODE, BAD_OAUTH_TOKEN);
                errorDetails.put(MESSAGE, SESSION_EXPIRED_INVALID);

                return errorDetails;
            }

            logger.error("Unable to parse salesforce response: {} ", new String(body));
            throw new UncategorizedApiException("Unable to read salesforce response.");
        }

        return Collections.emptyMap();
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
