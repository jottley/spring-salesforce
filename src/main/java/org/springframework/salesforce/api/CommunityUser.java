/**
 * Copyright (C) 2021-2026 https://github.com/jottley/spring-social-salesforce
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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A user of a Salesforce community as returned by the Connect communities API.
 * Properties are as reported by Salesforce.
 *
 * @author Jared Ottley
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunityUser {

    /** The user's nickname in the community. */
    private String communityNickname;

    /** The user's company name. */
    private String companyName;

    /** The user's display name. */
    private String displayName;

    /** The user's email address. */
    private String email;

    /** The user's first name. */
    private String firstName;

    /** Whether the user has Chatter access. */
    private boolean hasChatter;

    /** The user's ID. */
    private String id;

    /** Whether the user is a member of this community. */
    private boolean isInThisCommunity;

    /** The user's last name. */
    private String lastName;

    /** The user's full name. */
    private String name;

    /** The user's username. */
    private String username;

    /** The user's about me text. */
    private String aboutMe;

    /** The name of the contact associated with the user, as reported by Salesforce. */
    private String contactName;

    /**
     * Creates an empty instance.
     */
    public CommunityUser() {
    }

    /**
     * Returns the user's nickname in the community.
     *
     * @return the user's nickname in the community
     */
    public String getCommunityNickname() {
        return communityNickname;
    }

    /**
     * Sets the user's nickname in the community.
     *
     * @param communityNickname the user's nickname in the community
     */
    public void setCommunityNickname(String communityNickname) {
        this.communityNickname = communityNickname;
    }

    /**
     * Returns the user's company name.
     *
     * @return the user's company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the user's company name.
     *
     * @param companyName the user's company name
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Returns the user's display name.
     *
     * @return the user's display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the user's display name.
     *
     * @param displayName the user's display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user's email address.
     *
     * @return the user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the user's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user's first name.
     *
     * @return the user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName the user's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns whether the user has Chatter access.
     *
     * @return whether the user has Chatter access
     */
    public boolean isHasChatter() {
        return hasChatter;
    }

    /**
     * Sets whether the user has Chatter access.
     *
     * @param hasChatter whether the user has Chatter access
     */
    public void setHasChatter(boolean hasChatter) {
        this.hasChatter = hasChatter;
    }

    /**
     * Returns the user's ID.
     *
     * @return the user's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the user's ID.
     *
     * @param id the user's ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns whether the user is a member of this community.
     *
     * @return whether the user is a member of this community
     */
    public boolean isInThisCommunity() {
        return isInThisCommunity;
    }

    /**
     * Sets whether the user is a member of this community.
     *
     * @param inThisCommunity whether the user is a member of this community
     */
    // Salesforce sends this as "isInThisCommunity"; the bean property name is "inThisCommunity"
    @JsonAlias("isInThisCommunity")
    public void setInThisCommunity(boolean inThisCommunity) {
        isInThisCommunity = inThisCommunity;
    }

    /**
     * Returns the user's last name.
     *
     * @return the user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName the user's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the user's full name.
     *
     * @return the user's full name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's full name.
     *
     * @param name the user's full name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the user's username.
     *
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username.
     *
     * @param username the user's username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the user's about me text.
     *
     * @return the user's about me text
     */
    public String getAboutMe() {
        return aboutMe;
    }

    /**
     * Sets the user's about me text.
     *
     * @param aboutMe the user's about me text
     */
    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    /**
     * Returns the name of the contact associated with the user, as reported by Salesforce.
     *
     * @return the name of the contact associated with the user, as reported by Salesforce
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Sets the name of the contact associated with the user, as reported by Salesforce.
     *
     * @param contactName the name of the contact associated with the user, as reported by Salesforce
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
