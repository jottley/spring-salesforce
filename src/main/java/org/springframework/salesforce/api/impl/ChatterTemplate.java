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
 * Default implementation of ChatterOperations.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class ChatterTemplate extends AbstractSalesForceOperations<Salesforce> implements ChatterOperations {

    private final RestTemplate restTemplate;

    public ChatterTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }

    @Override
    public SalesforceProfile getUserProfile() {
        return getUserProfile("me");
    }

    @Override
    public SalesforceProfile getUserProfile(String userId) {
        requireAuthorization();

        return restTemplate.exchange(api.getBaseUrl() + "/{version}/chatter/users/{id}", HttpMethod.GET, new HttpEntity<>("",getChatterHeader()), SalesforceProfile.class, getVersion(), userId).getBody();
    }

    @Override
    public Status getStatus() {
        return getStatus("me");
    }

    @Override
    public Status getStatus(String userId) {
        requireAuthorization();

        JsonNode node = restTemplate.exchange(api.getBaseUrl() + "/{version}/chatter/users/{userId}/status", HttpMethod.GET, new HttpEntity<>("", getChatterHeader()), JsonNode.class, getVersion(), userId).getBody();

        if (node == null || node.isNull()) {
            return null;
        }

        JsonNode body = node.get("body");

        if (body == null || body.isNull()) {
            return null;
        }

        return api.readObject(body, Status.class);
    }

    @Override
    public Status updateStatus(String message) {
        return updateStatus("me", message);
    }

    @Override
    public Status updateStatus(String userId, String message) {
        requireAuthorization();

        MultiValueMap<String, String> post = new LinkedMultiValueMap<>();
        post.add("text", message);

        JsonNode node = restTemplate.exchange(api.getBaseUrl() + "/{version}/chatter/users/{userId}/status", HttpMethod.POST, new HttpEntity<>(post, getChatterHeader()), JsonNode.class, getVersion(), userId).getBody();

        if (node == null || node.isNull()) {
            return null;
        }

        JsonNode body = node.get("body");

        if (body == null || body.isNull()) {
            return null;
        }
        return api.readObject(node.get("body"), Status.class);
    }

    public HttpHeaders getChatterHeader()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Chatter-Entity-Encoding", "false");

        return headers;
    }

}
