/**
 * Copyright (C) 2016-2026 https://github.com/jottley/spring-salesforce
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.salesforce.api;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * Specifies operations performed on Salesforce.
 *
 * <p>This is the top-level API. It gives access to each group of operations, and to the instance
 * URL and authorization gateway URL they use. Each accessor that takes an {@code instanceUrl} sets
 * it on this API, so it applies to subsequent calls through any of the operations.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public interface Salesforce {

  /**
   * Returns the operations for API version and service discovery.
   *
   * @return the API version and service discovery operations
   */
  public ApiOperations apiOperations();

  /**
   * Sets the instance URL used for subsequent calls and returns the operations for API version and
   * service discovery.
   *
   * @param instanceUrl the Salesforce instance URL, for example {@code https://na1.salesforce.com}
   * @return the API version and service discovery operations
   */
  public ApiOperations apiOperations(String instanceUrl);

  /**
   * Returns the operations for Chatter.
   *
   * @return the Chatter operations
   */
  public ChatterOperations chatterOperations();

  /**
   * Sets the instance URL used for subsequent calls and returns the operations for Chatter.
   *
   * @param instanceUrl the Salesforce instance URL, for example {@code https://na1.salesforce.com}
   * @return the Chatter operations
   */
  public ChatterOperations chatterOperations(String instanceUrl);

  /**
   * Returns the operations for SOQL query.
   *
   * @return the SOQL query operations
   */
  public QueryOperations queryOperations();

  /**
   * Sets the instance URL used for subsequent calls and returns the operations for SOQL query.
   *
   * @param instanceUrl the Salesforce instance URL, for example {@code https://na1.salesforce.com}
   * @return the SOQL query operations
   */
  public QueryOperations queryOperations(String instanceUrl);

  /**
   * Returns the operations for recently viewed items.
   *
   * @return the recently viewed items operations
   */
  public RecentOperations recentOperations();

  /**
   * Sets the instance URL used for subsequent calls and returns the operations for recently viewed
   * items.
   *
   * @param instanceUrl the Salesforce instance URL, for example {@code https://na1.salesforce.com}
   * @return the recently viewed items operations
   */
  public RecentOperations recentOperations(String instanceUrl);

  /**
   * Returns the operations for SOSL search.
   *
   * @return the SOSL search operations
   */
  public SearchOperations searchOperations();

  /**
   * Sets the instance URL used for subsequent calls and returns the operations for SOSL search.
   *
   * @param instanceUrl the Salesforce instance URL, for example {@code https://na1.salesforce.com}
   * @return the SOSL search operations
   */
  public SearchOperations searchOperations(String instanceUrl);

  /**
   * Returns the operations for sObject.
   *
   * @return the sObject operations
   */
  public SObjectOperations sObjectsOperations();

  /**
   * Sets the instance URL used for subsequent calls and returns the operations for sObject.
   *
   * @param instanceUrl the Salesforce instance URL, for example {@code https://na1.salesforce.com}
   * @return the sObject operations
   */
  public SObjectOperations sObjectsOperations(String instanceUrl);

  /**
   * Returns the operations for the current user's information.
   *
   * @return the user operations
   */
  public UserOperations userOperations();

  /**
   * Sets the authorization gateway URL used for subsequent user info calls and returns the user
   * operations. Unlike the other overloads this sets the gateway URL, not the instance URL.
   *
   * @param gatewayUrl the base URL of the Salesforce authorization gateway, for example {@code
   *     https://login.salesforce.com}
   * @return the user operations
   */
  public UserOperations userOperations(String gatewayUrl);

  /**
   * Returns the operations for org limits.
   *
   * @return the org limits operations
   */
  public LimitsOperations limitsOperations();

  /**
   * Sets the instance URL used for subsequent calls and returns the operations for org limits.
   *
   * @param instanceUrl the Salesforce instance URL, for example {@code https://na1.salesforce.com}
   * @return the org limits operations
   */
  public LimitsOperations limitsOperations(String instanceUrl);

  /**
   * Converts a JSON array node into a list of objects using this API's Jackson mapper.
   *
   * @param <T> the element type
   * @param jsonNode the JSON array to convert
   * @param type the element class
   * @return the converted elements
   */
  public <T> List<T> readList(JsonNode jsonNode, Class<T> type);

  /**
   * Converts a JSON node into an object using this API's Jackson mapper.
   *
   * @param <T> the target type
   * @param jsonNode the JSON to convert
   * @param type the target class
   * @return the converted object
   */
  public <T> T readObject(JsonNode jsonNode, Class<T> type);

  /**
   * Returns the base URL of the data API, that is {@code {instanceUrl}/services/data}. A default
   * instance URL is used if none has been set.
   *
   * @return the base URL to which the API version and resource are appended
   */
  public String getBaseUrl();

  /**
   * Returns the instance URL used for calls.
   *
   * @return the instance URL, or null if none has been set
   */
  public String getInstanceUrl();

  /**
   * Sets the instance URL used for subsequent calls.
   *
   * @param instanceUrl the Salesforce instance URL
   */
  public void setInstanceUrl(String instanceUrl);

  /**
   * Returns the URL of the OAuth2 user info endpoint, that is {@code
   * {gatewayUrl}/services/oauth2/userinfo}. A default gateway URL is used if none has been set.
   *
   * @return the user info URL
   */
  public String getUserInfoUrl();

  /**
   * Returns the authorization gateway URL.
   *
   * @return the gateway URL, or null if none has been set
   */
  public String getAuthGatewayUrl();

  /**
   * Sets the authorization gateway URL used for user info calls.
   *
   * @param gatewayUrl the base URL of the Salesforce authorization gateway
   */
  public void setAuthGatewayBaseUrl(String gatewayUrl);

  /**
   * Returns the operations for Connect (Communities).
   *
   * @return the Connect (Communities) operations
   */
  public ConnectOperations connectOperations();

  /**
   * Sets the instance URL used for subsequent calls and returns the operations for Connect
   * (Communities).
   *
   * @param instanceUrl the Salesforce instance URL, for example {@code https://na1.salesforce.com}
   * @return the Connect (Communities) operations
   */
  public ConnectOperations connectOperations(String instanceUrl);

  /**
   * Returns the operations for custom Apex REST API.
   *
   * @return the custom Apex REST API operations
   */
  public CustomApiOperations customApiOperations();

  /**
   * Sets the instance URL used for subsequent calls and returns the operations for custom Apex REST
   * API.
   *
   * @param instanceUrl the Salesforce instance URL, for example {@code https://na1.salesforce.com}
   * @return the custom Apex REST API operations
   */
  public CustomApiOperations customApiOperations(String instanceUrl);
}
