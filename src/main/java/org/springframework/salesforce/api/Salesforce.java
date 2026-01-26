/**
 * Copyright (C) 2016-2026 https://github.com/jottley/spring-social-salesforce
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

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Specifies operations performed on Salesforce.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public interface Salesforce {

    public ApiOperations apiOperations();

    public ApiOperations apiOperations(String instanceUrl);

    public ChatterOperations chatterOperations();

    public ChatterOperations chatterOperations(String instanceUrl);

    public QueryOperations queryOperations();

    public QueryOperations queryOperations(String instanceUrl);

    public RecentOperations recentOperations();

    public RecentOperations recentOperations(String instanceUrl);

    public SearchOperations searchOperations();

    public SearchOperations searchOperations(String instanceUrl);

    public SObjectOperations sObjectsOperations();

    public SObjectOperations sObjectsOperations(String instanceUrl);

    public UserOperations userOperations();

    public UserOperations userOperations(String gatewayUrl);

    public LimitsOperations limitsOperations();

    public LimitsOperations limitsOperations(String instanceUrl);

    public <T> List<T> readList(JsonNode jsonNode, Class<T> type);

    public <T> T readObject(JsonNode jsonNode, Class<T> type);

    public String getBaseUrl();

    public String getInstanceUrl();

    public void setInstanceUrl(String instanceUrl);

    public String getUserInfoUrl();

    public String getAuthGatewayUrl();

    public void setAuthGatewayBaseUrl(String gatewayUrl);

    public ConnectOperations connectOperations();

    public ConnectOperations connectOperations(String instanceUrl);

    public CustomApiOperations customApiOperations();

    public CustomApiOperations customApiOperations(String instanceUrl);
}