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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;

/**
 * @author Jared Ottley
 */
public class HeaderAddingInterceptorTest {

    private final byte[] body = "payload".getBytes();
    private final MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("https://na7.salesforce.com/x"));
    private final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
    private final ClientHttpResponse response = mock(ClientHttpResponse.class);

    @Test
    public void addsEveryConfiguredHeaderAndContinuesTheChain() throws IOException {
        when(execution.execute(request, body)).thenReturn(response);
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("X-PrettyPrint", "1");
        headers.put("Sforce-Query-Options", "batchSize=500");

        ClientHttpResponse result = new HeaderAddingInterceptor(headers).intercept(request, body, execution);

        assertSame(response, result);
        assertEquals(List.of("1"), request.getHeaders().get("X-PrettyPrint"));
        assertEquals(List.of("batchSize=500"), request.getHeaders().get("Sforce-Query-Options"));
        verify(execution).execute(request, body);
    }

    @Test
    public void emptyHeaderMapLeavesRequestUntouched() throws IOException {
        when(execution.execute(request, body)).thenReturn(response);

        new HeaderAddingInterceptor(Map.of()).intercept(request, body, execution);

        assertTrue(request.getHeaders().isEmpty());
        verify(execution).execute(request, body);
    }
}
