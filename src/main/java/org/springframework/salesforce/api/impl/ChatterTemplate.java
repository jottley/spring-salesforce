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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.salesforce.api.ChatterOperations;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.api.SalesforceProfile;
import org.springframework.salesforce.api.Status;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * Default implementation of {@link ChatterOperations}.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class ChatterTemplate extends AbstractSalesForceOperations<Salesforce> implements ChatterOperations {

    /** The shared rest template used for requests. */
    private final RestTemplate restTemplate;

    /**
     * Creates the operations instance.
     *
     * @param api the {@link Salesforce} instance this operations object belongs to
     * @param restTemplate the shared rest template used for requests
     */
    public ChatterTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves the Chatter profile of the current user.
     *
     * @return the user profile
     */
    @Override
    public SalesforceProfile getUserProfile() {
        return getUserProfile("me");
    }

    /**
     * Retrieves a user's Chatter profile.
     *
     * @param userId the user id, or {@code me} for the current user
     * @return the user profile
     * @throws IllegalArgumentException if the user id is null, blank, {@code .} or {@code ..}
     */
    @Override
    public SalesforceProfile getUserProfile(String userId) {
        requireAuthorization();

        return restTemplate.exchange(api.getBaseUrl() + "/{version}/chatter/users/{id}", HttpMethod.GET, new HttpEntity<>("",getChatterHeader()), SalesforceProfile.class, getVersion(), requirePathSegment("user id", userId)).getBody();
    }

    /**
     * Retrieves the current user's Chatter status.
     *
     * @return the status, or {@code null} if there is none
     * @deprecated Salesforce retired the Chatter user status resource in API version 25.
     */
    @Override
    @Deprecated
    public Status getStatus() {
        return getStatus("me");
    }

    /**
     * Retrieves a user's Chatter status.
     *
     * @param userId the user id, or {@code me} for the current user
     * @return the status, or {@code null} if there is none
     * @throws IllegalArgumentException if the user id is null, blank, {@code .} or {@code ..}
     * @deprecated Salesforce retired the Chatter user status resource in API version 25.
     */
    @Override
    @Deprecated
    public Status getStatus(String userId) {
        requireAuthorization();

        JsonNode node = restTemplate.exchange(api.getBaseUrl() + "/{version}/chatter/users/{userId}/status", HttpMethod.GET, new HttpEntity<>("", getChatterHeader()), JsonNode.class, getVersion(), requirePathSegment("user id", userId)).getBody();

        if (node == null || node.isNull()) {
            return null;
        }

        JsonNode body = node.get("body");

        if (body == null || body.isNull()) {
            return null;
        }

        return api.readObject(body, Status.class);
    }

    /**
     * Updates the current user's Chatter status.
     *
     * @param message the status text
     * @return the new status, or {@code null} if none was returned
     * @deprecated Salesforce retired the Chatter user status resource in API version 25.
     */
    @Override
    @Deprecated
    public Status updateStatus(String message) {
        return updateStatus("me", message);
    }

    /**
     * Updates a user's Chatter status.
     *
     * @param userId the user id, or {@code me} for the current user
     * @param message the status text
     * @return the new status, or {@code null} if none was returned
     * @throws IllegalArgumentException if the user id is null, blank, {@code .} or {@code ..}
     * @deprecated Salesforce retired the Chatter user status resource in API version 25.
     */
    @Override
    @Deprecated
    public Status updateStatus(String userId, String message) {
        requireAuthorization();

        MultiValueMap<String, String> post = new LinkedMultiValueMap<>();
        post.add("text", message);

        JsonNode node = restTemplate.exchange(api.getBaseUrl() + "/{version}/chatter/users/{userId}/status", HttpMethod.POST, new HttpEntity<>(post, getChatterHeader()), JsonNode.class, getVersion(), requirePathSegment("user id", userId)).getBody();

        if (node == null || node.isNull()) {
            return null;
        }

        JsonNode body = node.get("body");

        if (body == null || body.isNull()) {
            return null;
        }
        return api.readObject(node.get("body"), Status.class);
    }

    /**
     * Builds the headers for Chatter requests, which turn off entity encoding.
     *
     * @return the headers
     */
    public HttpHeaders getChatterHeader()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Chatter-Entity-Encoding", "false");

        return headers;
    }

}
