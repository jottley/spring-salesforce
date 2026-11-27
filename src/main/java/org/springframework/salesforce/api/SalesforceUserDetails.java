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
 * User details returned by the OAuth2 userinfo endpoint (OpenID Connect property names). Extends
 * {@link SalesforceProfile}, but {@code photo} and {@code username} are never populated, because userinfo
 * sends {@code photos} and {@code preferred_username} instead; use {@link #getPreferredUsername()}.
 * Unknown properties in the JSON are ignored.
 *
 * @author Alexandra Leahu
 * @author Jared Ottley
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceUserDetails extends SalesforceProfile {
    /** The id of the user's organization. */
    private final String organizationId;

    /** The user's nickname. */
    private final String nickname;

    /** The user's preferred username. */
    private final String preferredUsername;

    /**
     * Creates the user details.
     *
     * @param id the user's id
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email address
     * @param name the user's full name
     * @param organizationId the id of the user's organization
     * @param nickname the user's nickname
     * @param preferredUsername the user's preferred username
     */
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

    /**
     * Returns the id of the user's organization.
     *
     * @return the organization id
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Returns the user's nickname.
     *
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the user's preferred username.
     *
     * @return the preferred username
     */
    public String getPreferredUsername() {
        return preferredUsername;
    }
}
