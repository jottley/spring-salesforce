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
package org.springframework.salesforce.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Alexandra Leahu
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceUserDetails extends SalesforceProfile {
    private String organizationId;

    private String nickname;

    private String preferredUsername;

    public SalesforceUserDetails(String id, String firstName, String lastName,
        String email, String name, String organizationId, String nickname,
        String preferredUsername) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.name = name;
        this.organizationId = organizationId;
        this.nickname = nickname;
        this.preferredUsername = preferredUsername;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }
}
