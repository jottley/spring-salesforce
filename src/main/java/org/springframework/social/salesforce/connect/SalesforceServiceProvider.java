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
import org.springframework.social.salesforce.api.impl.SalesforceTemplate;

/**
 * Salesforce Service Provider implementation using Spring Security OAuth2.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SalesforceServiceProvider {
    
    //Provider ID
    public final static String ID = "salesforce";
    
    public final static String PRODUCTION_GATEWAY_URL = "https://login.salesforce.com";
    public final static String SANDBOX_GATEWAY_URL    = "https://test.salesforce.com";
    
    public final static String TOKEN_PATH      = "/services/oauth2/token";
    public final static String AUTHORIZE_PATH  = "/services/oauth2/authorize";
    
    private SalesforceOAuth2Client oAuth2Client;

    public SalesforceServiceProvider(String clientId, String clientSecret) {
        this(clientId, clientSecret,
                PRODUCTION_GATEWAY_URL + AUTHORIZE_PATH,
                PRODUCTION_GATEWAY_URL + TOKEN_PATH);
    }
    
    public SalesforceServiceProvider(String clientId, String clientSecret, boolean sandbox)
    {
        this(clientId, clientSecret,
                (sandbox ? SANDBOX_GATEWAY_URL : PRODUCTION_GATEWAY_URL) + AUTHORIZE_PATH,
                (sandbox ? SANDBOX_GATEWAY_URL : PRODUCTION_GATEWAY_URL) + TOKEN_PATH);
    }

    public SalesforceServiceProvider(String clientId, String clientSecret, String authorizeUrl, String tokenUrl) {
        this.oAuth2Client = new SalesforceOAuth2Client(clientId, clientSecret, authorizeUrl, tokenUrl);
    }

    public SalesforceOAuth2Client getOAuth2Client() {
        return oAuth2Client;
    }

    public Salesforce getApi(String accessToken) {
        SalesforceTemplate template = new SalesforceTemplate(accessToken);

        // gets the returned instance url and sets to Salesforce template as base url.
        String instanceUrl = oAuth2Client.getInstanceUrl();
        if (instanceUrl != null) {
            template.setInstanceUrl(instanceUrl);
        }

        return template;
    }

}
