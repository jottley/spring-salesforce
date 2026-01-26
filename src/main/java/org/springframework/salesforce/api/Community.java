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

@JsonIgnoreProperties(ignoreUnknown = true)
public class Community {

    private String description;

    private String id;

    private String name;

    private String url;

    private boolean invitationsEnabled;

    private boolean sendWelcomeEmail;

    private boolean allowChatterAccessWithoutLogin;

    private boolean allowMembersToFlag;

    private boolean guestMemberVisibilityEnabled;

    private boolean knowledgeableEnabled;

    private String loginUrl;

    private boolean memberVisibilityEnabled;

    private boolean nicknameDisplayEnabled;

    private boolean privateMessagesEnabled;

    private boolean reputationEnabled;

    private boolean siteAsContainerEnabled;

    private String siteUrl;

    private String status;

    private String templateName;

    private String urlPathPrefix;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isInvitationsEnabled() {
        return invitationsEnabled;
    }

    public void setInvitationsEnabled(boolean invitationsEnabled) {
        this.invitationsEnabled = invitationsEnabled;
    }

    public boolean isSendWelcomeEmail() {
        return sendWelcomeEmail;
    }

    public void setSendWelcomeEmail(boolean sendWelcomeEmail) {
        this.sendWelcomeEmail = sendWelcomeEmail;
    }

    public boolean isAllowChatterAccessWithoutLogin() {
        return allowChatterAccessWithoutLogin;
    }

    public void setAllowChatterAccessWithoutLogin(boolean allowChatterAccessWithoutLogin) {
        this.allowChatterAccessWithoutLogin = allowChatterAccessWithoutLogin;
    }

    public boolean isAllowMembersToFlag() {
        return allowMembersToFlag;
    }

    public void setAllowMembersToFlag(boolean allowMembersToFlag) {
        this.allowMembersToFlag = allowMembersToFlag;
    }

    public boolean isGuestMemberVisibilityEnabled() {
        return guestMemberVisibilityEnabled;
    }

    public void setGuestMemberVisibilityEnabled(boolean guestMemberVisibilityEnabled) {
        this.guestMemberVisibilityEnabled = guestMemberVisibilityEnabled;
    }

    public boolean isKnowledgeableEnabled() {
        return knowledgeableEnabled;
    }

    public void setKnowledgeableEnabled(boolean knowledgeableEnabled) {
        this.knowledgeableEnabled = knowledgeableEnabled;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public boolean isMemberVisibilityEnabled() {
        return memberVisibilityEnabled;
    }

    public void setMemberVisibilityEnabled(boolean memberVisibilityEnabled) {
        this.memberVisibilityEnabled = memberVisibilityEnabled;
    }

    public boolean isNicknameDisplayEnabled() {
        return nicknameDisplayEnabled;
    }

    public void setNicknameDisplayEnabled(boolean nicknameDisplayEnabled) {
        this.nicknameDisplayEnabled = nicknameDisplayEnabled;
    }

    public boolean isPrivateMessagesEnabled() {
        return privateMessagesEnabled;
    }

    public void setPrivateMessagesEnabled(boolean privateMessagesEnabled) {
        this.privateMessagesEnabled = privateMessagesEnabled;
    }

    public boolean isReputationEnabled() {
        return reputationEnabled;
    }

    public void setReputationEnabled(boolean reputationEnabled) {
        this.reputationEnabled = reputationEnabled;
    }

    public boolean isSiteAsContainerEnabled() {
        return siteAsContainerEnabled;
    }

    public void setSiteAsContainerEnabled(boolean siteAsContainerEnabled) {
        this.siteAsContainerEnabled = siteAsContainerEnabled;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getUrlPathPrefix() {
        return urlPathPrefix;
    }

    public void setUrlPathPrefix(String urlPathPrefix) {
        this.urlPathPrefix = urlPathPrefix;
    }
}
