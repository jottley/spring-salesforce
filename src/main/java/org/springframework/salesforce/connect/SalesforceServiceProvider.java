/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-social-salesforce
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
import org.springframework.salesforce.api.impl.SalesforceTemplate;

/**
 * Service provider that holds the {@link SalesforceOAuth2Client} for a Salesforce org and creates
 * {@link Salesforce} API bindings.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SalesforceServiceProvider {

  // Provider ID
  /** The provider id. */
  public static final String ID = "salesforce";

  /** Login gateway URL for production orgs. */
  public static final String PRODUCTION_GATEWAY_URL = "https://login.salesforce.com";

  /** Login gateway URL for sandbox orgs. */
  public static final String SANDBOX_GATEWAY_URL = "https://test.salesforce.com";

  /** Path of the OAuth2 token endpoint, relative to the gateway. */
  public static final String TOKEN_PATH = "/services/oauth2/token";

  /** Path of the OAuth2 authorization endpoint, relative to the gateway. */
  public static final String AUTHORIZE_PATH = "/services/oauth2/authorize";

  /** The OAuth2 client that handles the authorization and token requests. */
  private final SalesforceOAuth2Client oAuth2Client;

  /**
   * Creates a provider that uses the production gateway.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret
   */
  public SalesforceServiceProvider(String clientId, String clientSecret) {
    this(
        clientId,
        clientSecret,
        PRODUCTION_GATEWAY_URL + AUTHORIZE_PATH,
        PRODUCTION_GATEWAY_URL + TOKEN_PATH);
  }

  /**
   * Creates a provider that uses the production or sandbox gateway.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret
   * @param sandbox {@code true} to use the sandbox gateway
   */
  public SalesforceServiceProvider(String clientId, String clientSecret, boolean sandbox) {
    this(
        clientId,
        clientSecret,
        (sandbox ? SANDBOX_GATEWAY_URL : PRODUCTION_GATEWAY_URL) + AUTHORIZE_PATH,
        (sandbox ? SANDBOX_GATEWAY_URL : PRODUCTION_GATEWAY_URL) + TOKEN_PATH);
  }

  /**
   * Creates a provider with explicit OAuth2 endpoint URLs.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret
   * @param authorizeUrl the OAuth2 authorization endpoint URL
   * @param tokenUrl the OAuth2 token endpoint URL
   */
  public SalesforceServiceProvider(
      String clientId, String clientSecret, String authorizeUrl, String tokenUrl) {
    this.oAuth2Client = new SalesforceOAuth2Client(clientId, clientSecret, authorizeUrl, tokenUrl);
  }

  /**
   * Returns the OAuth2 client.
   *
   * @return the OAuth2 client
   */
  public SalesforceOAuth2Client getOAuth2Client() {
    return oAuth2Client;
  }

  /**
   * Creates an API binding for the access token, using the instance URL last recorded by the OAuth2
   * client (from any token request it has made), if any.
   *
   * <p>The OAuth2 client holds a single mutable instance URL, so when several users share this
   * provider prefer {@link #getApi(String, String)}.
   *
   * @param accessToken the OAuth2 access token
   * @return a new {@link Salesforce} instance
   */
  public Salesforce getApi(String accessToken) {
    SalesforceTemplate template = new SalesforceTemplate(accessToken);

    // gets the returned instance url and sets to Salesforce template as base url.
    String instanceUrl = oAuth2Client.getInstanceUrl();
    if (instanceUrl != null) {
      template.setInstanceUrl(instanceUrl);
    }

    return template;
  }

  /**
   * Creates an API binding for the access token and instance URL.
   *
   * @param accessToken the OAuth2 access token
   * @param instanceUrl the instance URL; if {@code null} no instance URL is set (the OAuth2
   *     client's URL is not consulted) and the template default applies
   * @return a new {@link Salesforce} instance
   */
  public Salesforce getApi(String accessToken, String instanceUrl) {
    SalesforceTemplate template = new SalesforceTemplate(accessToken);

    if (instanceUrl != null) {
      template.setInstanceUrl(instanceUrl);
    }

    return template;
  }
}
