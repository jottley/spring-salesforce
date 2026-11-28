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
import java.util.Locale;

/**
 * Describes contextual information about the current user.
 *
 * @param userId the Salesforce user ID
 * @param userName the Salesforce username
 * @param firstName the first name
 * @param lastName the last name
 * @param email the email address
 * @param fullName the full name
 * @param locale the locale, which controls the formatting of dates and currency symbols
 * @param language the language, which controls the language of labels
 * @param timeZone the configured time zone
 * @param profileId the profile ID
 * @param roleId the ID of the role assigned to the user
 * @param userType the license type, in label form
 * @param currencyISOCode the default currency ISO code, which applies only if multi-currency is
 *     enabled
 * @param accessibilityModeEnabled whether interface modifications for the visually impaired are on
 * @param profilePhotoUrl the URL of the profile photo
 * @param profileThumbnailUrl the URL of the profile thumbnail
 * @param siteUrl the site URL
 * @param siteUrlPrefix the site URL prefix
 * @param networkId the network ID
 * @param isDefaultNetwork whether this is the default network
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CanvasUserContext(
    String userId,
    String userName,
    String firstName,
    String lastName,
    String email,
    String fullName,
    Locale locale,
    Locale language,
    String timeZone,
    String profileId,
    String roleId,
    String userType,
    String currencyISOCode,
    boolean accessibilityModeEnabled,
    String profilePhotoUrl,
    String profileThumbnailUrl,
    String siteUrl,
    String siteUrlPrefix,
    String networkId,
    boolean isDefaultNetwork) {}
