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

import org.springframework.salesforce.api.Photo;
import org.springframework.salesforce.api.SalesforceProfile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson mixin that maps JSON properties onto {@link SalesforceProfile}.
 *
 * @author Umut Utkan
 * @author Alexandra Leahu
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceProfileMixin {

    /**
     * Mirrors the property names of {@code SalesforceProfile}. {@code SalesforceProfile} has no matching
     * constructor, so Jackson ignores this creator; {@code id}, {@code email}, {@code firstName},
     * {@code lastName}, {@code name} and {@code username} are set directly on the fields that Jackson infers
     * from the getters. Only {@code photo} is bound explicitly, by the field annotation below.
     *
     * @param id the JSON {@code id} property
     * @param firstName the JSON {@code firstName} property
     * @param lastName the JSON {@code lastName} property
     * @param email the JSON {@code email} property
     * @param name the JSON {@code name} property
     */
    @JsonCreator
    SalesforceProfileMixin(
            @JsonProperty("id") String id,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("email") String email,
            @JsonProperty("name") String name) {
    }

    /** Binds the JSON property {@code photo} to {@code SalesforceProfile.photo}. */
    @JsonProperty("photo")
    Photo photo;

}
