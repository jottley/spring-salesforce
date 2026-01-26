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

import java.io.IOException;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Interceptor for adding headers
 *
 * @author Umut Utkan
 */
public class HeaderAddingInterceptor implements ClientHttpRequestInterceptor {

    private final Map<String, String> headers;

    public HeaderAddingInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public @NonNull ClientHttpResponse intercept(@NonNull HttpRequest httpRequest, @NonNull byte[] bytes,
                                    @NonNull ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            httpRequest.getHeaders().add(header.getKey(), header.getValue());
        }

        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }

}
