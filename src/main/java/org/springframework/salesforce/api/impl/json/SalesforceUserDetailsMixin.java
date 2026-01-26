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


import org.springframework.salesforce.api.SalesforceUserDetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link SalesforceUserDetails}
 * @author Alexandra Leahu
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceUserDetailsMixin {

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
}
