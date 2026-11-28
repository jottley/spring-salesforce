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
package org.springframework.salesforce.client;

import java.util.Map;
import org.springframework.salesforce.api.InvalidAuthorizationException;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.api.impl.SalesforceTemplate;
import org.springframework.salesforce.connect.SalesforceServiceProvider;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Default {@link SalesforceFactory} that logs in with the OAuth2 username-password flow.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class BaseSalesforceFactory implements SalesforceFactory {

  /**
   * The production token endpoint URL; the initial value of the token URL field and the fallback of
   * {@link #getAuthorizeUrl()}.
   */
  private static final String DEFAULT_AUTH_URL =
      SalesforceServiceProvider.PRODUCTION_GATEWAY_URL + SalesforceServiceProvider.TOKEN_PATH;

  /** The connected app consumer key. */
  private final String clientId;

  /** The connected app consumer secret. */
  private final String clientSecret;

  /**
   * The token endpoint URL used for logins (despite the name); read through {@link
   * #getAuthorizeUrl()}, which falls back to the production URL if this is {@code null}.
   */
  private String authorizeUrl = DEFAULT_AUTH_URL;

  /** The rest template used for the token request. */
  private final RestTemplate restTemplate;

  /**
   * Creates a factory with a default {@link RestTemplate} that uses {@link ErrorHandler}.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret
   */
  public BaseSalesforceFactory(String clientId, String clientSecret) {
    this(clientId, clientSecret, createRestTemplate());
  }

  /**
   * Creates a factory with the given {@link RestTemplate}.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret
   * @param restTemplate the rest template used for the token request
   */
  public BaseSalesforceFactory(String clientId, String clientSecret, RestTemplate restTemplate) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.restTemplate = restTemplate;
  }

  /**
   * Sets the token endpoint URL used for logins (despite the method name, this is the token
   * endpoint).
   *
   * @param authorizeUrl the token endpoint URL; {@code null} restores the production token URL
   */
  public void setAuthorizeUrl(String authorizeUrl) {
    this.authorizeUrl = authorizeUrl;
  }

  /**
   * Returns the token endpoint URL used for logins.
   *
   * @return the configured URL, or the production token URL if none is set; this is the URL {@link
   *     #create} posts to
   */
  public String getAuthorizeUrl() {
    return (this.authorizeUrl == null) ? DEFAULT_AUTH_URL : this.authorizeUrl;
  }

  /**
   * Logs in with the username-password flow and returns an API binding for the resulting token. The
   * security token, if any, is appended to the password. The binding's instance URL is set from the
   * response's {@code instance_url} when present.
   *
   * @param username the Salesforce username
   * @param password the user's password
   * @param securityToken the user's security token; may be {@code null}
   * @return a new {@link Salesforce} instance
   * @throws org.springframework.salesforce.api.InvalidAuthorizationException for a 400 response
   *     whose error means the credentials or client were rejected (default rest template only), or
   *     if a successful response contains no {@code access_token} (an empty body, or a body without
   *     the entry)
   * @throws org.springframework.salesforce.api.OperationNotPermittedException for other 400
   *     responses (default rest template only)
   * @throws org.springframework.salesforce.api.RateLimitExceededException for a {@code
   *     rate_limit_exceeded} 400 response (default rest template only)
   * @throws org.springframework.web.client.RestClientException for other failures; with a
   *     caller-supplied rest template none of the {@link ErrorHandler} mapping applies unless that
   *     template installs it
   */
  @Override
  @SuppressWarnings("unchecked")
  public Salesforce create(String username, String password, String securityToken) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "password");
    map.add("client_id", this.clientId);
    map.add("client_secret", this.clientSecret);
    map.add("username", username);
    map.add("password", password + (securityToken == null ? "" : securityToken));

    Map<String, String> token = restTemplate.postForObject(getAuthorizeUrl(), map, Map.class);
    String accessToken = token == null ? null : token.get("access_token");
    if (accessToken == null || accessToken.isBlank()) {
      // Without this a missing token would build an unauthenticated binding that only fails on
      // first use
      throw new InvalidAuthorizationException("The token response contained no access token.");
    }
    SalesforceTemplate template = new SalesforceTemplate(accessToken);
    String instanceUrl = token.get("instance_url");
    if (instanceUrl != null) {
      template.setInstanceUrl(instanceUrl);
    }
    return template;
  }

  /**
   * Creates the default rest template, which reports OAuth2 errors through {@link ErrorHandler}.
   *
   * @return a new rest template
   */
  private static RestTemplate createRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    // Using default request factory - Spring will auto-detect the best available
    restTemplate.setErrorHandler(new ErrorHandler());
    return restTemplate;
  }
}
