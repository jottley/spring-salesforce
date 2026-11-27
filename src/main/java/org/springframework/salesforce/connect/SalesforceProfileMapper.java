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
package org.springframework.salesforce.connect;

import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.api.SalesforceProfile;
import org.springframework.salesforce.api.SalesforceUserDetails;
import org.springframework.util.ObjectUtils;


/**
 * Maps a {@link Salesforce} connection to the user's details and profile, using a fixed instance URL
 * or login gateway URL when one is configured.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 * @author Alexandra Leahu
 */
public class SalesforceProfileMapper {

    /** The fixed instance URL used for the Chatter calls (test, profile, status), or {@code null} to use the connection's own. */
    private String instanceUrl = null;
    /** The login gateway URL used for user details, or {@code null} to leave the connection's current gateway (production unless set earlier) in use. */
    private String gatewayUrl  = null;

    /** Creates a mapper that uses the connection's own instance URL and current gateway (production unless one was set earlier). */
    public SalesforceProfileMapper()
    {
        //NOOP
    }

    /**
     * Creates a mapper that uses a fixed instance URL.
     *
     * @param instanceUrl the instance URL for the Chatter calls ({@link #test}, {@link #getUserProfile}, {@link #updateStatus}); user details are not affected
     */
    public SalesforceProfileMapper(String instanceUrl)
    {
        this.instanceUrl = instanceUrl;
    }

    /**
     * Creates a mapper with a fixed instance URL, optionally using the sandbox gateway for user details.
     *
     * @param instanceUrl the instance URL for the Chatter calls ({@link #test}, {@link #getUserProfile}, {@link #updateStatus}); user details are not affected
     * @param sandbox {@code true} to use the sandbox gateway for user details
     */
    public SalesforceProfileMapper(String instanceUrl, boolean sandbox)
    {
        this.instanceUrl = instanceUrl;

        if (sandbox)
        {
            this.gatewayUrl = SalesforceServiceProvider.SANDBOX_GATEWAY_URL;
        }
    }

    /**
     * Creates a mapper with a fixed instance URL and gateway URL.
     *
     * @param instanceUrl the instance URL for the Chatter calls ({@link #test}, {@link #getUserProfile}, {@link #updateStatus}); user details are not affected
     * @param gatewayUrl the login gateway URL for user details
     * @deprecated use {@link #SalesforceProfileMapper(String, boolean)} instead
     */
    @Deprecated
    public SalesforceProfileMapper(String instanceUrl, String gatewayUrl)
    {
        this.instanceUrl = instanceUrl;
        this.gatewayUrl = gatewayUrl;
    }

    /**
     * Creates a mapper that optionally uses the sandbox gateway for user details.
     *
     * @param sandbox {@code true} to use the sandbox gateway
     */
    public SalesforceProfileMapper(boolean sandbox)
    {
        if (sandbox)
        {
            this.gatewayUrl = SalesforceServiceProvider.SANDBOX_GATEWAY_URL;
        }
    }

    /**
     * Tests whether the connection is usable by fetching the Chatter user profile.
     * <p>
     * Any exception is swallowed. If a fixed instance URL is configured, it is set on the connection as a side effect.
     *
     * @param salesforce the connection to test
     * @return {@code true} if the profile request succeeded, {@code false} if it failed for any reason
     */
    public boolean test(Salesforce salesforce)
    {
        try
        {
            if (ObjectUtils.isEmpty(instanceUrl))
            {
                salesforce.chatterOperations().getUserProfile();
            }
            else
            {
                salesforce.chatterOperations(instanceUrl).getUserProfile();
            }
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Retrieves the user's details from the configured gateway, or from the connection's current gateway
     * (production unless one was set earlier) if none is configured.
     *
     * @param salesforce the connection to use
     * @return the user details
     */
    public SalesforceUserDetails getUserDetails(Salesforce salesforce) {
        SalesforceUserDetails userDetails;
        if (ObjectUtils.isEmpty(gatewayUrl))
        {
            userDetails = salesforce.userOperations().getSalesforceUserDetails();
        }
        else
        {
            userDetails = salesforce.userOperations(gatewayUrl).getSalesforceUserDetails();
        }
        return userDetails;
    }

    /**
     * Retrieves the user's Chatter profile, using the configured instance URL if there is one.
     *
     * @param salesforce the connection to use
     * @return the user profile
     */
    public SalesforceProfile getUserProfile(Salesforce salesforce) {
        SalesforceProfile profile;

        if (ObjectUtils.isEmpty(instanceUrl))
        {
            profile = salesforce.chatterOperations().getUserProfile();
        }
        else
        {
            profile = salesforce.chatterOperations(instanceUrl).getUserProfile();
        }
        return profile;
    }

    /**
     * Updates the user's Chatter status, using the configured instance URL if there is one.
     *
     * @param salesforce the connection to use
     * @param message the status text
     * @deprecated Salesforce retired the Chatter user status resource in API version 25, so this always fails.
     *             There is no replacement; it will be removed in a future major version.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public void updateStatus(Salesforce salesforce, String message) {
        if (ObjectUtils.isEmpty(instanceUrl))
        {
            salesforce.chatterOperations().updateStatus(message);
        }
        else
        {
            salesforce.chatterOperations(instanceUrl).updateStatus(message);
        }
    }
}
