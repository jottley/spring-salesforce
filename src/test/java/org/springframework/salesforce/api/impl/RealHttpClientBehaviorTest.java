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

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

/**
 * Checks behavior that the mock server cannot show because it replaces the HTTP client: how the real client stack
 * that {@link SalesforceTemplate} builds treats a 300 response.
 *
 * @author Jared Ottley
 */
public class RealHttpClientBehaviorTest {

    /** A local server standing in for Salesforce. */
    private HttpServer server;

    /**
     * Starts a server that answers every request with a 300 and a JSON list of candidate record URLs,
     * with no {@code Location} header, as Salesforce does for an external id that matches several records.
     *
     * @throws Exception if the server cannot start
     */
    @Before
    public void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            byte[] body = "[\"/services/data/v68.0/sobjects/Account/001A\"]".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(300, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
    }

    /** Stops the server. */
    @After
    public void stopServer() {
        server.stop(0);
    }

    /** A 300 with no Location header reaches the caller as a normal response, through the real HTTP client. */
    @Test
    public void aMultipleChoicesResponseReachesTheCallerThroughTheRealClient() {
        SalesforceTemplate template = new SalesforceTemplate("token");
        template.setInstanceUrl("http://127.0.0.1:" + server.getAddress().getPort());

        String body = template.customApiOperations().getForApexObject("/Multi", String.class);

        assertEquals("[\"/services/data/v68.0/sobjects/Account/001A\"]", body);
    }
}
