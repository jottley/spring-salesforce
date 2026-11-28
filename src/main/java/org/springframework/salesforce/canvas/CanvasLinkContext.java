/**
 * Copyright (c) 2011, salesforce.com, inc. All rights reserved.
 *
 * <p>Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * <p>Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * <p>Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * <p>Neither the name of salesforce.com, inc. nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.springframework.salesforce.canvas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes all contextual information around external references, or links to external resources.
 *
 * @param enterpriseUrl the URL for enterprise-wide external clients
 * @param metadataUrl the base URL of the Metadata API
 * @param partnerUrl the URL of the Partner API
 * @param restUrl the base URL for REST services
 * @param sobjectUrl the URL for custom sObject definitions
 * @param searchUrl the URL of the search API
 * @param queryUrl the URL of the SOQL query API
 * @param recentItemsUrl the URL for recent items
 * @param userProfileUrl the URL for user profiles
 * @param chatterFeedsUrl the URL for Chatter feeds
 * @param chatterGroupsUrl the URL for Chatter groups
 * @param chatterUsersUrl the URL for Chatter users
 * @param chatterFeedItemsUrl the URL for Chatter feed items
 * @param loginUrl the URL for login
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CanvasLinkContext(
    String enterpriseUrl,
    String metadataUrl,
    String partnerUrl,
    String restUrl,
    String sobjectUrl,
    String searchUrl,
    String queryUrl,
    String recentItemsUrl,
    @JsonProperty("userUrl") String userProfileUrl,
    String chatterFeedsUrl,
    String chatterGroupsUrl,
    String chatterUsersUrl,
    String chatterFeedItemsUrl,
    String loginUrl) {}
