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

/**
 * Salesforce Connection Factory for creating Salesforce API connections.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SalesforceConnectionFactory {

    private final SalesforceServiceProvider serviceProvider;
    private final SalesforceProfileMapper profileMapper;

    public SalesforceConnectionFactory() {
        this.serviceProvider = new SalesforceServiceProvider(null, null);
        this.profileMapper = new SalesforceProfileMapper();
    }

    public SalesforceConnectionFactory(String clientId, String clientSecret) {
        this.serviceProvider = new SalesforceServiceProvider(clientId, clientSecret);
        this.profileMapper = new SalesforceProfileMapper();
    }

    public SalesforceConnectionFactory(String clientId, String clientSecret, boolean sandbox) {
        this.serviceProvider = new SalesforceServiceProvider(clientId, clientSecret, sandbox);
        this.profileMapper = new SalesforceProfileMapper(sandbox);
    }

    public SalesforceConnectionFactory(String clientId, String clientSecret, String instanceUrl) {
        this.serviceProvider = new SalesforceServiceProvider(clientId, clientSecret);
        this.profileMapper = new SalesforceProfileMapper(instanceUrl);
    }

    public SalesforceConnectionFactory(String clientId, String clientSecret, String instanceUrl, boolean sandbox) {
        this.serviceProvider = new SalesforceServiceProvider(clientId, clientSecret, sandbox);
        this.profileMapper = new SalesforceProfileMapper(instanceUrl, sandbox);
    }

    @Deprecated
    public SalesforceConnectionFactory(String clientId, String clientSecret, String authorizeUrl, String tokenUrl) {
        this.serviceProvider = new SalesforceServiceProvider(clientId, clientSecret, authorizeUrl, tokenUrl);
        this.profileMapper = new SalesforceProfileMapper();
    }

    @Deprecated
    public SalesforceConnectionFactory(String clientId, String clientSecret, String authorizeUrl, String tokenUrl, String instanceUrl) {
        this.serviceProvider = new SalesforceServiceProvider(clientId, clientSecret, authorizeUrl, tokenUrl);
        this.profileMapper = new SalesforceProfileMapper(instanceUrl);
    }

    @Deprecated
    public SalesforceConnectionFactory(String clientId, String clientSecret, String authorizeUrl, String tokenUrl, String instanceUrl, String gatewayUrl) {
        this.serviceProvider = new SalesforceServiceProvider(clientId, clientSecret, authorizeUrl, tokenUrl);
        this.profileMapper = new SalesforceProfileMapper(instanceUrl, gatewayUrl);
    }

    public SalesforceServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public SalesforceProfileMapper getProfileMapper() {
        return profileMapper;
    }

    public Salesforce createApi(String accessToken) {
        return serviceProvider.getApi(accessToken);
    }

    public Salesforce createApi(String accessToken, String instanceUrl) {
        return serviceProvider.getApi(accessToken, instanceUrl);
    }

}
