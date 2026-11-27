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
package org.springframework.salesforce.api.impl.json;


import java.util.Map;

import org.springframework.salesforce.api.SalesforceUserDetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson mixin that maps JSON properties onto {@link SalesforceUserDetails}, using the OpenID Connect userinfo property names.
 *
 * @author Alexandra Leahu
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SalesforceUserDetailsMixin {

    /**
     * Binds the constructor arguments of {@code SalesforceUserDetails} to JSON properties.
     *
     * @param id the JSON {@code user_id} property
     * @param firstName the JSON {@code given_name} property
     * @param lastName the JSON {@code family_name} property
     * @param email the JSON {@code email} property
     * @param name the JSON {@code name} property
     * @param organizationId the JSON {@code organization_id} property
     * @param nickname the JSON {@code nickname} property
     * @param preferredUsername the JSON {@code preferred_username} property
     */
    @JsonCreator
    SalesforceUserDetailsMixin(
            @JsonProperty("user_id") String id,
            @JsonProperty("given_name") String firstName,
            @JsonProperty("family_name") String lastName,
            @JsonProperty("email") String email,
            @JsonProperty("name") String name,
            @JsonProperty("organization_id") String organizationId,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("preferred_username") String preferredUsername) {
    }

    /**
     * Binds the JSON {@code photos} object to {@code SalesforceUserDetails.setPhotos}, which maps
     * {@code picture} and {@code thumbnail} onto the large and small photo URLs.
     *
     * @param photos the JSON {@code photos} object
     */
    @JsonProperty("photos")
    abstract void setPhotos(Map<String, String> photos);
}
