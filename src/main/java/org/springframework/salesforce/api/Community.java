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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Salesforce community (Experience Cloud site) as returned by the Connect
 * communities API. Properties are as reported by Salesforce.
 *
 * @author Jared Ottley
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Community {

    /** The description of the community. */
    private String description;

    /** The ID of the community. */
    private String id;

    /** The name of the community. */
    private String name;

    /** The relative REST resource path of the community, for example {@code /services/data/v68.0/connect/communities/{id}}. */
    private String url;

    /** Whether invitations are enabled. */
    private boolean invitationsEnabled;

    /** Whether a welcome email is sent to new members. */
    private boolean sendWelcomeEmail;

    /** Whether Chatter can be accessed without logging in. */
    private boolean allowChatterAccessWithoutLogin;

    /** Whether members may flag content. */
    private boolean allowMembersToFlag;

    /** Whether guest members are visible. */
    private boolean guestMemberVisibilityEnabled;

    /** Whether the knowledgeable people feature is enabled. */
    private boolean knowledgeableEnabled;

    /** The login URL of the community. */
    private String loginUrl;

    /** Whether members are visible. */
    private boolean memberVisibilityEnabled;

    /** Whether nicknames are displayed instead of names. */
    private boolean nicknameDisplayEnabled;

    /** Whether private messages are enabled. */
    private boolean privateMessagesEnabled;

    /** Whether reputation is enabled. */
    private boolean reputationEnabled;

    /** Whether the site is used as the container, as reported by Salesforce. */
    private boolean siteAsContainerEnabled;

    /** The URL of the community site. */
    private String siteUrl;

    /** The status of the community, as reported by Salesforce. */
    private String status;

    /** The name of the template the community uses. */
    private String templateName;

    /** The URL path prefix of the community. */
    private String urlPathPrefix;

    /**
     * Creates an empty instance.
     */
    public Community() {
    }

    /**
     * Returns the description of the community.
     *
     * @return the description of the community
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the community.
     *
     * @param description the description of the community
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the ID of the community.
     *
     * @return the ID of the community
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the community.
     *
     * @param id the ID of the community
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the name of the community.
     *
     * @return the name of the community
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the community.
     *
     * @param name the name of the community
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the relative REST resource path of the community, for example
     * {@code /services/data/v68.0/connect/communities/{id}}; see {@link #getSiteUrl()} for the site URL.
     *
     * @return the relative REST resource path of the community
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the relative REST resource path of the community.
     *
     * @param url the relative REST resource path of the community
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns whether invitations are enabled.
     *
     * @return whether invitations are enabled
     */
    public boolean isInvitationsEnabled() {
        return invitationsEnabled;
    }

    /**
     * Sets whether invitations are enabled.
     *
     * @param invitationsEnabled whether invitations are enabled
     */
    public void setInvitationsEnabled(boolean invitationsEnabled) {
        this.invitationsEnabled = invitationsEnabled;
    }

    /**
     * Returns whether a welcome email is sent to new members.
     *
     * @return whether a welcome email is sent to new members
     */
    public boolean isSendWelcomeEmail() {
        return sendWelcomeEmail;
    }

    /**
     * Sets whether a welcome email is sent to new members.
     *
     * @param sendWelcomeEmail whether a welcome email is sent to new members
     */
    public void setSendWelcomeEmail(boolean sendWelcomeEmail) {
        this.sendWelcomeEmail = sendWelcomeEmail;
    }

    /**
     * Returns whether Chatter can be accessed without logging in.
     *
     * @return whether Chatter can be accessed without logging in
     */
    public boolean isAllowChatterAccessWithoutLogin() {
        return allowChatterAccessWithoutLogin;
    }

    /**
     * Sets whether Chatter can be accessed without logging in.
     *
     * @param allowChatterAccessWithoutLogin whether Chatter can be accessed without logging in
     */
    public void setAllowChatterAccessWithoutLogin(boolean allowChatterAccessWithoutLogin) {
        this.allowChatterAccessWithoutLogin = allowChatterAccessWithoutLogin;
    }

    /**
     * Returns whether members may flag content.
     *
     * @return whether members may flag content
     */
    public boolean isAllowMembersToFlag() {
        return allowMembersToFlag;
    }

    /**
     * Sets whether members may flag content.
     *
     * @param allowMembersToFlag whether members may flag content
     */
    public void setAllowMembersToFlag(boolean allowMembersToFlag) {
        this.allowMembersToFlag = allowMembersToFlag;
    }

    /**
     * Returns whether guest members are visible.
     *
     * @return whether guest members are visible
     */
    public boolean isGuestMemberVisibilityEnabled() {
        return guestMemberVisibilityEnabled;
    }

    /**
     * Sets whether guest members are visible.
     *
     * @param guestMemberVisibilityEnabled whether guest members are visible
     */
    public void setGuestMemberVisibilityEnabled(boolean guestMemberVisibilityEnabled) {
        this.guestMemberVisibilityEnabled = guestMemberVisibilityEnabled;
    }

    /**
     * Returns whether the knowledgeable people feature is enabled.
     *
     * @return whether the knowledgeable people feature is enabled
     */
    public boolean isKnowledgeableEnabled() {
        return knowledgeableEnabled;
    }

    /**
     * Sets whether the knowledgeable people feature is enabled.
     *
     * @param knowledgeableEnabled whether the knowledgeable people feature is enabled
     */
    public void setKnowledgeableEnabled(boolean knowledgeableEnabled) {
        this.knowledgeableEnabled = knowledgeableEnabled;
    }

    /**
     * Returns the login URL of the community.
     *
     * @return the login URL of the community
     */
    public String getLoginUrl() {
        return loginUrl;
    }

    /**
     * Sets the login URL of the community.
     *
     * @param loginUrl the login URL of the community
     */
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    /**
     * Returns whether members are visible.
     *
     * @return whether members are visible
     */
    public boolean isMemberVisibilityEnabled() {
        return memberVisibilityEnabled;
    }

    /**
     * Sets whether members are visible.
     *
     * @param memberVisibilityEnabled whether members are visible
     */
    public void setMemberVisibilityEnabled(boolean memberVisibilityEnabled) {
        this.memberVisibilityEnabled = memberVisibilityEnabled;
    }

    /**
     * Returns whether nicknames are displayed instead of names.
     *
     * @return whether nicknames are displayed instead of names
     */
    public boolean isNicknameDisplayEnabled() {
        return nicknameDisplayEnabled;
    }

    /**
     * Sets whether nicknames are displayed instead of names.
     *
     * @param nicknameDisplayEnabled whether nicknames are displayed instead of names
     */
    public void setNicknameDisplayEnabled(boolean nicknameDisplayEnabled) {
        this.nicknameDisplayEnabled = nicknameDisplayEnabled;
    }

    /**
     * Returns whether private messages are enabled.
     *
     * @return whether private messages are enabled
     */
    public boolean isPrivateMessagesEnabled() {
        return privateMessagesEnabled;
    }

    /**
     * Sets whether private messages are enabled.
     *
     * @param privateMessagesEnabled whether private messages are enabled
     */
    public void setPrivateMessagesEnabled(boolean privateMessagesEnabled) {
        this.privateMessagesEnabled = privateMessagesEnabled;
    }

    /**
     * Returns whether reputation is enabled.
     *
     * @return whether reputation is enabled
     */
    public boolean isReputationEnabled() {
        return reputationEnabled;
    }

    /**
     * Sets whether reputation is enabled.
     *
     * @param reputationEnabled whether reputation is enabled
     */
    public void setReputationEnabled(boolean reputationEnabled) {
        this.reputationEnabled = reputationEnabled;
    }

    /**
     * Returns whether the site is used as the container, as reported by Salesforce.
     *
     * @return whether the site is used as the container, as reported by Salesforce
     */
    public boolean isSiteAsContainerEnabled() {
        return siteAsContainerEnabled;
    }

    /**
     * Sets whether the site is used as the container, as reported by Salesforce.
     *
     * @param siteAsContainerEnabled whether the site is used as the container, as reported by Salesforce
     */
    public void setSiteAsContainerEnabled(boolean siteAsContainerEnabled) {
        this.siteAsContainerEnabled = siteAsContainerEnabled;
    }

    /**
     * Returns the URL of the community site.
     *
     * @return the URL of the community site
     */
    public String getSiteUrl() {
        return siteUrl;
    }

    /**
     * Sets the URL of the community site.
     *
     * @param siteUrl the URL of the community site
     */
    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    /**
     * Returns the status of the community, as reported by Salesforce.
     *
     * @return the status of the community, as reported by Salesforce
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the community, as reported by Salesforce.
     *
     * @param status the status of the community, as reported by Salesforce
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the name of the template the community uses.
     *
     * @return the name of the template the community uses
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Sets the name of the template the community uses.
     *
     * @param templateName the name of the template the community uses
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * Returns the URL path prefix of the community.
     *
     * @return the URL path prefix of the community
     */
    public String getUrlPathPrefix() {
        return urlPathPrefix;
    }

    /**
     * Sets the URL path prefix of the community.
     *
     * @param urlPathPrefix the URL path prefix of the community
     */
    public void setUrlPathPrefix(String urlPathPrefix) {
        this.urlPathPrefix = urlPathPrefix;
    }
}
