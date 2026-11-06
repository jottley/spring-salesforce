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
package org.springframework.salesforce.connect;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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

    private final String clientId;
    private final String clientSecret;
    private final String authorizeUrl;
    private final String accessTokenUrl;
    private String instanceUrl = null;
    private final RestTemplate restTemplate;

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
    public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri) {
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
    public AccessGrant refreshAccess(String refreshToken) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "refresh_token");
        parameters.add("refresh_token", refreshToken);
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);

        return postForToken(parameters);
    }

    /**
     * Obtain an access token using the OAuth 2.0 client credentials flow.
     * <p>
     * The token URL must be the org's My Domain URL (for example
     * {@code https://mydomain.my.salesforce.com/services/oauth2/token}); the flow is not
     * supported on login.salesforce.com or test.salesforce.com. The response does not
     * include a refresh token, so request a new token when the current one expires.
     *
     * @param scope optional space-separated scopes; null uses the scopes configured on the app
     */
    public AccessGrant clientCredentialsAccess(String scope) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "client_credentials");
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);
        if (scope != null) {
            parameters.add("scope", scope);
        }

        return postForToken(parameters);
    }

    /**
     * Post request for access token
     * Handles Salesforce's non-standard content type response
     */
    protected AccessGrant postForToken(MultiValueMap<String, String> parameters) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        ResponseEntity<AccessGrant> response = restTemplate.exchange(
            accessTokenUrl,
            HttpMethod.POST,
            request,
            AccessGrant.class
        );

        AccessGrant accessGrant = response.getBody();
        if (accessGrant == null) {
            throw new IllegalStateException("Token response body is null");
        }
        // Extract Salesforce-specific instance_url
        if (accessGrant.getInstanceUrl() != null) {
            this.instanceUrl = accessGrant.getInstanceUrl();
        }

        return accessGrant;
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
