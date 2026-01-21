/**
 * Copyright (C) 2017 https://github.com/jottley/spring-social-salesforce
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
package org.springframework.social.salesforce.connect;

import org.springframework.social.salesforce.api.Salesforce;
import org.springframework.social.salesforce.api.SalesforceProfile;
import org.springframework.social.salesforce.api.SalesforceUserDetails;
import org.springframework.util.ObjectUtils;


/**
 * Salesforce profile mapper for user profile and connection data.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 * @author Alexandra Leahu
 */
public class SalesforceProfileMapper {

    private String instanceUrl = null;
    private String gatewayUrl  = null;

    public SalesforceProfileMapper()
    {
        //NOOP
    }

    public SalesforceProfileMapper(String instanceUrl)
    {
        this.instanceUrl = instanceUrl;
    }

    public SalesforceProfileMapper(String instanceUrl, boolean sandbox)
    {
        this.instanceUrl = instanceUrl;

        if (sandbox)
        {
            this.gatewayUrl = SalesforceServiceProvider.SANDBOX_GATEWAY_URL;
        }
    }

    @Deprecated
    public SalesforceProfileMapper(String instanceUrl, String gatewayUrl)
    {
        this.instanceUrl = instanceUrl;
        this.gatewayUrl = gatewayUrl;
    }

    public SalesforceProfileMapper(boolean sandbox)
    {
        if (sandbox)
        {
            this.gatewayUrl = SalesforceServiceProvider.SANDBOX_GATEWAY_URL;
        }
    }

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
