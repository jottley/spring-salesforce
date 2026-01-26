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
 *
 * Salesforce Access Grant representation.
 * @author Jared Ottley
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessGrant {

    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("refresh_token")
    private String refresh_token;

    @JsonProperty("signature")
    private String signature;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("id_token")
    private String id_token;

    @JsonProperty("instance_url")
    private String instance_url;

    @JsonProperty("id")
    private String id;

    @JsonProperty("token_type")
    private String token_type;

    @JsonProperty("issued_at")
    private String issued_at;

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refresh_token = refreshToken;
    }

    public String getInstanceUrl() {
        return instance_url;
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instance_url = instanceUrl;
    }

    public String getId() {
        return id;
    }

    public String getTokenType() {
        return token_type;
    }

    public void setTokenType(String tokenType) {
        this.token_type = tokenType;
    }

    public String getIssuedAt() {
        return issued_at;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getIdToken() {
        return id_token;
    }

    public void setIdToken(String idToken) {
        this.id_token = idToken;
    }
}
