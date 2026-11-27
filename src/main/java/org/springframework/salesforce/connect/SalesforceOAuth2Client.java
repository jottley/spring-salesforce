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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Salesforce OAuth2 client built on Spring's {@link RestTemplate}.
 *
 * <p>Handles the Salesforce-specific parts of the OAuth2 flows, including capturing the {@code
 * instance_url} from the token response.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SalesforceOAuth2Client {

  /** The connected app consumer key. */
  private final String clientId;

  /** The connected app consumer secret. */
  private final String clientSecret;

  /** The OAuth2 authorization endpoint URL. */
  private final String authorizeUrl;

  /** The OAuth2 token endpoint URL. */
  private final String accessTokenUrl;

  /**
   * The {@code instance_url} from the most recent token response that contained one; {@code null}
   * until then. Never reset.
   */
  private String instanceUrl = null;

  /**
   * The rest template used for token requests; it uses Spring's default error handling, not {@link
   * org.springframework.salesforce.client.ErrorHandler}.
   */
  private final RestTemplate restTemplate;

  /**
   * Creates a client for the given endpoints.
   *
   * @param clientId the connected app consumer key
   * @param clientSecret the connected app consumer secret; {@code null} or blank for a public
   *     client, in which case no secret is sent with the authorization-code and refresh requests
   * @param authorizeUrl the OAuth2 authorization endpoint URL
   * @param accessTokenUrl the OAuth2 token endpoint URL
   */
  public SalesforceOAuth2Client(
      String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.authorizeUrl = authorizeUrl;
    this.accessTokenUrl = accessTokenUrl;
    this.restTemplate = new RestTemplate();
  }

  /**
   * Builds the authorization URL for the authorization code flow, without PKCE. Apps that require
   * PKCE (the default for new External Client Apps) need {@link #buildAuthorizeUrl(String, String,
   * String, String)}.
   *
   * @param redirectUri the redirect URI; omitted if {@code null}
   * @param scope the space-separated scopes; omitted if {@code null}
   * @param state an opaque state value; omitted if {@code null}
   * @return the URL to send the user to. Characters that would break the URL (spaces, {@code &},
   *     {@code #}, quotes, non-ASCII characters and a {@code %} that does not start an escape) are
   *     percent-encoded. Values you have already encoded are left alone, so {@code %20} and {@code
   *     +} are passed through unchanged; this also means a literal {@code %} or {@code +} cannot be
   *     sent through this method
   */
  public String buildAuthorizeUrl(String redirectUri, String scope, String state) {
    return buildAuthorizeUrl(redirectUri, scope, state, null);
  }

  /**
   * Builds the authorization URL for the authorization code flow, with a PKCE code challenge.
   *
   * <p>Create the challenge from a verifier with {@link Pkce#codeChallenge(String)}, keep the
   * verifier, and pass it to {@link #exchangeForAccess(String, String, String)}.
   *
   * @param redirectUri the redirect URI; omitted if {@code null}
   * @param scope the space-separated scopes; omitted if {@code null}
   * @param state an opaque state value; omitted if {@code null}
   * @param codeChallenge the PKCE code challenge (method {@code S256}); if {@code null} or blank no
   *     PKCE parameters are added
   * @return the URL to send the user to; the encoding rules of {@link #buildAuthorizeUrl(String,
   *     String, String)} apply
   */
  public String buildAuthorizeUrl(
      String redirectUri, String scope, String state, String codeChallenge) {
    org.springframework.web.util.UriComponentsBuilder builder =
        org.springframework.web.util.UriComponentsBuilder.fromUriString(authorizeUrl);
    builder.queryParam("response_type", "code");
    builder.queryParam("client_id", clientId);
    if (redirectUri != null) {
      builder.queryParam("redirect_uri", encodeValue(redirectUri));
    }
    if (scope != null) {
      builder.queryParam("scope", encodeValue(scope));
    }
    if (state != null) {
      builder.queryParam("state", encodeValue(state));
    }
    if (codeChallenge != null && !codeChallenge.isBlank()) {
      builder.queryParam("code_challenge", encodeValue(codeChallenge));
      builder.queryParam("code_challenge_method", "S256");
    }
    return builder.build().toUriString();
  }

  /** Characters, besides letters and digits, that are left as they are in a query value. */
  private static final String UNENCODED_QUERY_CHARACTERS = "-._~!$'()*+,;=:@/?";

  /**
   * Percent-encodes the characters of a query value that would break the URL, without touching
   * characters that are already valid. In particular an existing {@code %XX} escape and a {@code +}
   * pass through unchanged, so callers that encoded their own values (as earlier versions required)
   * are not encoded twice.
   *
   * @param value the value to encode
   * @return the value with unsafe characters percent-encoded as UTF-8
   */
  private static String encodeValue(String value) {
    StringBuilder out = new StringBuilder(value.length());
    byte[] bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    for (int i = 0; i < bytes.length; i++) {
      int c = bytes[i] & 0xFF;
      boolean escape =
          c == '%' && i + 2 < bytes.length && isHex(bytes[i + 1]) && isHex(bytes[i + 2]);
      boolean plain =
          c < 0x80 && (Character.isLetterOrDigit(c) || UNENCODED_QUERY_CHARACTERS.indexOf(c) >= 0);
      if (plain || escape) {
        out.append((char) c);
      } else {
        out.append('%')
            .append(Character.toUpperCase(Character.forDigit(c >> 4, 16)))
            .append(Character.toUpperCase(Character.forDigit(c & 0xF, 16)));
      }
    }
    return out.toString();
  }

  /**
   * Tells whether a byte is an ASCII hexadecimal digit.
   *
   * @param b the byte
   * @return {@code true} for {@code 0-9}, {@code a-f} and {@code A-F}
   */
  private static boolean isHex(byte b) {
    return (b >= '0' && b <= '9') || (b >= 'a' && b <= 'f') || (b >= 'A' && b <= 'F');
  }

  /**
   * Exchanges an authorization code for an access token, without a PKCE verifier. Apps that require
   * PKCE (the default for new External Client Apps) need {@link #exchangeForAccess(String, String,
   * String)}.
   *
   * @param authorizationCode the code returned to the redirect URI
   * @param redirectUri the redirect URI used when authorizing; omitted if {@code null}
   * @return the token response
   * @throws org.springframework.web.client.RestClientException if the token endpoint returns an
   *     error status or the request fails (the default error handler is used, not {@link
   *     org.springframework.salesforce.client.ErrorHandler})
   * @throws IllegalStateException if the response has no body
   */
  public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri) {
    return exchangeForAccess(authorizationCode, redirectUri, null);
  }

  /**
   * Exchanges an authorization code for an access token, proving possession of the PKCE verifier.
   *
   * <p>The client secret is sent only if one was configured, so a public client (no secret) can use
   * this method.
   *
   * @param authorizationCode the code returned to the redirect URI
   * @param redirectUri the redirect URI used when authorizing; omitted if {@code null}
   * @param codeVerifier the verifier whose challenge was sent in {@link #buildAuthorizeUrl(String,
   *     String, String, String)}; omitted if {@code null} or blank
   * @return the token response
   * @throws org.springframework.web.client.RestClientException if the token endpoint returns an
   *     error status or the request fails (the default error handler is used, not {@link
   *     org.springframework.salesforce.client.ErrorHandler})
   * @throws IllegalStateException if the response has no body
   */
  public AccessGrant exchangeForAccess(
      String authorizationCode, String redirectUri, String codeVerifier) {
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
    parameters.add("grant_type", "authorization_code");
    parameters.add("code", authorizationCode);
    parameters.add("client_id", clientId);
    addClientSecret(parameters);
    if (redirectUri != null) {
      parameters.add("redirect_uri", redirectUri);
    }
    if (codeVerifier != null && !codeVerifier.isBlank()) {
      parameters.add("code_verifier", codeVerifier);
    }

    return postForToken(parameters);
  }

  /**
   * Adds the client secret to a token request, unless none is configured (a public client).
   *
   * @param parameters the form parameters for the token request
   */
  private void addClientSecret(MultiValueMap<String, String> parameters) {
    if (clientSecret != null && !clientSecret.isBlank()) {
      parameters.add("client_secret", clientSecret);
    }
  }

  /**
   * Obtains a new access token using a refresh token. The client secret is sent only if one was
   * configured.
   *
   * @param refreshToken the refresh token
   * @return the token response
   * @throws org.springframework.web.client.RestClientException if the token endpoint returns an
   *     error status or the request fails (the default error handler is used, not {@link
   *     org.springframework.salesforce.client.ErrorHandler})
   * @throws IllegalStateException if the response has no body
   */
  public AccessGrant refreshAccess(String refreshToken) {
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
    parameters.add("grant_type", "refresh_token");
    parameters.add("refresh_token", refreshToken);
    parameters.add("client_id", clientId);
    addClientSecret(parameters);

    return postForToken(parameters);
  }

  /**
   * Obtain an access token using the OAuth 2.0 client credentials flow.
   *
   * <p>The token URL must be the org's My Domain URL (for example {@code
   * https://mydomain.my.salesforce.com/services/oauth2/token}); the flow is not supported on
   * login.salesforce.com or test.salesforce.com. The response does not include a refresh token, so
   * request a new token when the current one expires.
   *
   * @param scope optional space-separated scopes; null uses the scopes configured on the app
   * @return the token response
   * @throws org.springframework.web.client.RestClientException if the token endpoint returns an
   *     error status or the request fails (the default error handler is used, not {@link
   *     org.springframework.salesforce.client.ErrorHandler})
   * @throws IllegalStateException if the response has no body
   */
  public AccessGrant clientCredentialsAccess(String scope) {
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
    parameters.add("grant_type", "client_credentials");
    parameters.add("client_id", clientId);
    parameters.add("client_secret", clientSecret);
    if (scope != null) {
      parameters.add("scope", scope);
    }

    return postForToken(parameters);
  }

  /**
   * Posts the form parameters to the token endpoint and records the returned instance URL, if any.
   *
   * @param parameters the form parameters for the token request
   * @return the token response
   * @throws org.springframework.web.client.RestClientException if the token endpoint returns an
   *     error status or the request fails
   * @throws IllegalStateException if the response has no body
   */
  protected AccessGrant postForToken(MultiValueMap<String, String> parameters) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

    ResponseEntity<AccessGrant> response =
        restTemplate.exchange(accessTokenUrl, HttpMethod.POST, request, AccessGrant.class);

    AccessGrant accessGrant = response.getBody();
    if (accessGrant == null) {
      throw new IllegalStateException("Token response body is null");
    }
    // Extract Salesforce-specific instance_url
    if (accessGrant.getInstanceUrl() != null) {
      this.instanceUrl = accessGrant.getInstanceUrl();
    }

    return accessGrant;
  }

  /**
   * Returns the instance URL from the most recent token response that contained one.
   *
   * @return the instance URL, or {@code null} if no token response has contained an {@code
   *     instance_url} yet
   */
  public String getInstanceUrl() {
    return instanceUrl;
  }

  /**
   * Returns the consumer key.
   *
   * @return the client id
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Returns the consumer secret.
   *
   * @return the client secret
   */
  public String getClientSecret() {
    return clientSecret;
  }

  /**
   * Returns the authorization endpoint URL.
   *
   * @return the authorization URL
   */
  public String getAuthorizeUrl() {
    return authorizeUrl;
  }

  /**
   * Returns the token endpoint URL.
   *
   * @return the token URL
   */
  public String getAccessTokenUrl() {
    return accessTokenUrl;
  }

  /**
   * Returns the {@link RestTemplate} used for token requests.
   *
   * @return the rest template
   */
  public RestTemplate getRestTemplate() {
    return restTemplate;
  }
}
