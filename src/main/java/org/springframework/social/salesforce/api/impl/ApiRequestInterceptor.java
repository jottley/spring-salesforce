/**
 * Copyright (C) 2019 https://github.com/jottley/spring-social-salesforce
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

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

/**
 * This class can be used to inspect and use the any request or response to the
 * Salesforce API.
 *
 * This class currently inspects the Sforce-Limit-Info header from Salesforce to
 * capture the current Daily API call usage after each API call.
 *
 * @author Jared Ottley
 *
 */
public class ApiRequestInterceptor implements ClientHttpRequestInterceptor {

    // The header will look like -> Sforce-Limit-Info: api-usage=39/15000
    private static final String SFORCE_LIMIT_INFO = "Sforce-Limit-Info";

    private final SalesforceTemplate salesforceTemplate;

    public ApiRequestInterceptor(SalesforceTemplate salesforceTemplate) {
        this.salesforceTemplate = salesforceTemplate;
    }

    @Override
    @SuppressWarnings("null") // Suppress null-related warnings
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution)
            throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        processSForceLimitInfoHeader(response);
        return response;
    }

    /**
     * Check response for the Sforce-Limit-Info header. Update the Limits Operations
     * with the currently API usage
     *
     * @param response
     */
    private void processSForceLimitInfoHeader(ClientHttpResponse response) {
        if (response.getHeaders().get(SFORCE_LIMIT_INFO) != null) {
            ((LimitsOperationsTemplate) salesforceTemplate.limitsOperations())
                    .setCurrentDailyApiLimits(response.getHeaders().getFirst(SFORCE_LIMIT_INFO));
        }
    }

}