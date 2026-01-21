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
package org.springframework.social.salesforce.connect;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Salesforce OAuth2 client implementation using Spring Security OAuth2.
 * 
 * Handles Salesforce-specific OAuth2 flow, including extracting the instance_url 
 * from the token response.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SalesforceOAuth2Client {

    private String clientId;
    private String clientSecret;
    private String authorizeUrl;
    private String accessTokenUrl;
    private String instanceUrl = null;
    private RestTemplate restTemplate;

    public SalesforceOAuth2Client(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorizeUrl = authorizeUrl;
        this.accessTokenUrl = accessTokenUrl;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Build the authorization URL for the OAuth2 flow
     */
    public String buildAuthorizeUrl(String redirectUri, String scope, String state) {
        org.springframework.web.util.UriComponentsBuilder builder = 
            org.springframework.web.util.UriComponentsBuilder.fromUriString(authorizeUrl);
        builder.queryParam("response_type", "code");
        builder.queryParam("client_id", clientId);
        if (redirectUri != null) {
            builder.queryParam("redirect_uri", redirectUri);
        }
        if (scope != null) {
            builder.queryParam("scope", scope);
        }
        if (state != null) {
            builder.queryParam("state", state);
        }
        return builder.build().toUriString();
    }

    /**
     * Exchange authorization code for access token
     */
    public Map<String, Object> exchangeForAccess(String authorizationCode, String redirectUri) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "authorization_code");
        parameters.add("code", authorizationCode);
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);
        if (redirectUri != null) {
            parameters.add("redirect_uri", redirectUri);
        }

        return postForToken(parameters);
    }

    /**
     * Refresh access token
     */
    public Map<String, Object> refreshAccess(String refreshToken) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "refresh_token");
        parameters.add("refresh_token", refreshToken);
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);

        return postForToken(parameters);
    }

    /**
     * Post request for access token
     * Handles Salesforce's non-standard content type response
     */
    protected Map<String, Object> postForToken(MultiValueMap<String, String> parameters) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);
        
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(accessTokenUrl, request, JsonNode.class);
        JsonNode responseBody = response.getBody();
        
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> result = mapper.convertValue(responseBody, Map.class);
        
        // Extract Salesforce-specific instance_url
        if (result.containsKey("instance_url")) {
            this.instanceUrl = (String) result.get("instance_url");
        }
        
        return result;
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public String getAccessTokenUrl() {
        return accessTokenUrl;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
