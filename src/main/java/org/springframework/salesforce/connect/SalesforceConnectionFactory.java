/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-salesforce
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
package org.springframework.salesforce.connect;

import org.springframework.salesforce.api.Salesforce;

/**
 * Factory that bundles a {@link SalesforceServiceProvider} and a {@link SalesforceProfileMapper}
 * and creates {@link Salesforce} API connections from an access token.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SalesforceConnectionFactory {

  /** The provider used to create API connections. */
  private final SalesforceServiceProvider serviceProvider;

  /** The mapper that supplies user details and profile information. */
  private final SalesforceProfileMapper profileMapper;

  /**
   * Creates a factory that uses the production login gateway.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret
   */
  public SalesforceConnectionFactory(String clientId, String clientSecret) {
    this.serviceProvider = new SalesforceServiceProvider(clientId, clientSecret);
    this.profileMapper = new SalesforceProfileMapper();
  }

  /**
   * Creates a factory that uses the production or sandbox login gateway.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret
   * @param sandbox {@code true} to use the sandbox gateway, {@code false} for production
   */
  public SalesforceConnectionFactory(String clientId, String clientSecret, boolean sandbox) {
    this.serviceProvider = new SalesforceServiceProvider(clientId, clientSecret, sandbox);
    this.profileMapper = new SalesforceProfileMapper(sandbox);
  }

  /**
   * Creates a factory that uses the production login gateway and a fixed instance URL for profile
   * lookups.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret
   * @param instanceUrl the instance URL used by the profile mapper
   */
  public SalesforceConnectionFactory(String clientId, String clientSecret, String instanceUrl) {
    this.serviceProvider = new SalesforceServiceProvider(clientId, clientSecret);
    this.profileMapper = new SalesforceProfileMapper(instanceUrl);
  }

  /**
   * Creates a factory with a fixed instance URL and a production or sandbox login gateway.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret
   * @param instanceUrl the instance URL used by the profile mapper
   * @param sandbox {@code true} to use the sandbox gateway, {@code false} for production
   */
  public SalesforceConnectionFactory(
      String clientId, String clientSecret, String instanceUrl, boolean sandbox) {
    this.serviceProvider = new SalesforceServiceProvider(clientId, clientSecret, sandbox);
    this.profileMapper = new SalesforceProfileMapper(instanceUrl, sandbox);
  }

  /**
   * Creates a factory with explicit OAuth2 endpoint URLs.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret
   * @param authorizeUrl the OAuth2 authorization endpoint URL
   * @param tokenUrl the OAuth2 token endpoint URL
   * @deprecated the {@code boolean} overloads only select the production or sandbox gateway and
   *     cannot take custom endpoint URLs. Use {@link #SalesforceConnectionFactory(String, String,
   *     boolean)} when production or sandbox is enough; for custom URLs (for example a My Domain
   *     token URL for the client credentials flow) use the four-argument constructor of {@link
   *     SalesforceServiceProvider}. The URLs apply to OAuth2 requests only; the profile mapper
   *     still uses the default (production) gateway for user details.
   */
  @Deprecated
  public SalesforceConnectionFactory(
      String clientId, String clientSecret, String authorizeUrl, String tokenUrl) {
    this.serviceProvider =
        new SalesforceServiceProvider(clientId, clientSecret, authorizeUrl, tokenUrl);
    this.profileMapper = new SalesforceProfileMapper();
  }

  /**
   * Creates a factory with explicit OAuth2 endpoint URLs and a fixed instance URL.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret
   * @param authorizeUrl the OAuth2 authorization endpoint URL
   * @param tokenUrl the OAuth2 token endpoint URL
   * @param instanceUrl the instance URL used by the profile mapper
   * @deprecated the {@code boolean} overloads only select the production or sandbox gateway and
   *     cannot take custom endpoint URLs. Use {@link #SalesforceConnectionFactory(String, String,
   *     String, boolean)} when production or sandbox is enough; for custom URLs (for example a My
   *     Domain token URL for the client credentials flow) use the four-argument constructor of
   *     {@link SalesforceServiceProvider}. The URLs apply to OAuth2 requests only; the profile
   *     mapper still uses the default (production) gateway for user details.
   */
  @Deprecated
  public SalesforceConnectionFactory(
      String clientId,
      String clientSecret,
      String authorizeUrl,
      String tokenUrl,
      String instanceUrl) {
    this.serviceProvider =
        new SalesforceServiceProvider(clientId, clientSecret, authorizeUrl, tokenUrl);
    this.profileMapper = new SalesforceProfileMapper(instanceUrl);
  }

  /**
   * Creates a factory with explicit OAuth2 endpoint URLs, a fixed instance URL and a gateway URL.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret
   * @param authorizeUrl the OAuth2 authorization endpoint URL
   * @param tokenUrl the OAuth2 token endpoint URL
   * @param instanceUrl the instance URL used by the profile mapper
   * @param gatewayUrl the login gateway URL used by the profile mapper for user details
   * @deprecated the {@code boolean} overloads only select the production or sandbox gateway and
   *     cannot take custom endpoint or gateway URLs. Use {@link
   *     #SalesforceConnectionFactory(String, String, String, boolean)} when production or sandbox
   *     is enough; for custom OAuth2 URLs use the four-argument constructor of {@link
   *     SalesforceServiceProvider}. The authorize and token URLs apply to OAuth2 requests only;
   *     {@code gatewayUrl} applies to user details only.
   */
  @Deprecated
  public SalesforceConnectionFactory(
      String clientId,
      String clientSecret,
      String authorizeUrl,
      String tokenUrl,
      String instanceUrl,
      String gatewayUrl) {
    this.serviceProvider =
        new SalesforceServiceProvider(clientId, clientSecret, authorizeUrl, tokenUrl);
    this.profileMapper = new SalesforceProfileMapper(instanceUrl, gatewayUrl);
  }

  /**
   * Returns the service provider.
   *
   * @return the service provider
   */
  public SalesforceServiceProvider getServiceProvider() {
    return serviceProvider;
  }

  /**
   * Returns the profile mapper.
   *
   * @return the profile mapper
   */
  public SalesforceProfileMapper getProfileMapper() {
    return profileMapper;
  }

  /**
   * Creates an API binding for the given access token, using the instance URL last recorded by the
   * shared OAuth2 client (from any token request it has made), if any.
   *
   * <p>The OAuth2 client holds a single mutable instance URL, so when several users share this
   * factory prefer {@link #createApi(String, String)}.
   *
   * @param accessToken the OAuth2 access token
   * @return a new {@link Salesforce} instance
   */
  public Salesforce createApi(String accessToken) {
    return serviceProvider.getApi(accessToken);
  }

  /**
   * Creates an API binding for the given access token and instance URL.
   *
   * @param accessToken the OAuth2 access token
   * @param instanceUrl the instance URL; if {@code null} no instance URL is set (the OAuth2
   *     client's URL is not consulted) and the template default applies
   * @return a new {@link Salesforce} instance
   */
  public Salesforce createApi(String accessToken, String instanceUrl) {
    return serviceProvider.getApi(accessToken, instanceUrl);
  }
}
