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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holder for the OAuth2 token response returned by the Salesforce token endpoint.
 * <p>
 * Unknown properties in the response are ignored. Not every flow returns every value, and
 * nothing validates the response, so any property may be {@code null} if it was absent; for
 * example the client credentials flow returns no refresh token. The {@code signature} property is
 * captured but has no accessor; {@code id} and {@code issued_at} are read-only.
 *
 * @author Jared Ottley
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessGrant {

    /**
     * Creates an empty grant; its properties are set afterwards.
     */
    public AccessGrant() {
    }

    /** The access token used to authorize API requests. */
    @JsonProperty("access_token")
    private String access_token;

    /** The refresh token, if the flow issued one. */
    @JsonProperty("refresh_token")
    private String refresh_token;

    /** The signature Salesforce returned with the token response, or {@code null} if absent. It has no getter or setter. */
    @JsonProperty("signature")
    private String signature;

    /** The scopes granted to the token. */
    @JsonProperty("scope")
    private String scope;

    /** The OpenID Connect ID token, if one was issued. */
    @JsonProperty("id_token")
    private String id_token;

    /** The URL of the Salesforce instance that issued the token. */
    @JsonProperty("instance_url")
    private String instance_url;

    /** The identity URL for the authenticated user. */
    @JsonProperty("id")
    private String id;

    /** The token type, typically {@code Bearer}. */
    @JsonProperty("token_type")
    private String token_type;

    /** The time the token was issued, as reported by Salesforce. */
    @JsonProperty("issued_at")
    private String issued_at;

    /**
     * Returns the access token used to authorize API requests.
     *
     * @return the access token, or {@code null} if absent
     */
    public String getAccessToken() {
        return access_token;
    }

    /**
     * Sets the access token.
     *
     * @param accessToken the access token
     */
    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }

    /**
     * Returns the refresh token, if the flow issued one.
     *
     * @return the refresh token, or {@code null}
     */
    public String getRefreshToken() {
        return refresh_token;
    }

    /**
     * Sets the refresh token.
     *
     * @param refreshToken the refresh token
     */
    public void setRefreshToken(String refreshToken) {
        this.refresh_token = refreshToken;
    }

    /**
     * Returns the URL of the Salesforce instance that issued the token.
     *
     * @return the instance URL, or {@code null} if absent
     */
    public String getInstanceUrl() {
        return instance_url;
    }

    /**
     * Sets the instance URL.
     *
     * @param instanceUrl the instance URL
     */
    public void setInstanceUrl(String instanceUrl) {
        this.instance_url = instanceUrl;
    }

    /**
     * Returns the identity URL for the authenticated user.
     *
     * @return the identity URL, or {@code null} if absent
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the token type, typically {@code Bearer}.
     *
     * @return the token type, or {@code null} if absent
     */
    public String getTokenType() {
        return token_type;
    }

    /**
     * Sets the token type.
     *
     * @param tokenType the token type
     */
    public void setTokenType(String tokenType) {
        this.token_type = tokenType;
    }

    /**
     * Returns the time the token was issued, as reported by Salesforce.
     *
     * @return the issued-at value, or {@code null} if absent
     */
    public String getIssuedAt() {
        return issued_at;
    }

    /**
     * Returns the scopes granted to the token.
     *
     * @return the space-separated scopes, or {@code null} if absent
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the granted scopes.
     *
     * @param scope the space-separated scopes
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the OpenID Connect ID token, if one was issued.
     *
     * @return the ID token, or {@code null}
     */
    public String getIdToken() {
        return id_token;
    }

    /**
     * Sets the ID token.
     *
     * @param idToken the ID token
     */
    public void setIdToken(String idToken) {
        this.id_token = idToken;
    }
}
