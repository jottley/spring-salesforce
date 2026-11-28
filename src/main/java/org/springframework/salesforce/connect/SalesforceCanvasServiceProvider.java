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
import org.springframework.salesforce.api.impl.SalesforceTemplate;
import org.springframework.salesforce.canvas.CanvasRequest;
import org.springframework.salesforce.canvas.SignedRequest;

/**
 * Verifies a Canvas signed request and provides the decoded request and a {@link Salesforce}
 * binding that uses the OAuth token and instance URL it carries.
 *
 * <p>The constructor does the verifying: a request that is malformed, not signed with the consumer
 * secret, or tampered with throws a {@link SecurityException} and no instance exists. Use this
 * class when you also need the decoded request (user, organization, environment, record); use
 * {@link SalesforceCanvasConnectionFactory} when you only need the API. The request's tokens are
 * credentials: {@code CanvasClient.toString()} masks them, {@link #getCanvasRequestJson()} does
 * not. See the {@linkplain org.springframework.salesforce.canvas package documentation} for an
 * example.
 */
public class SalesforceCanvasServiceProvider {

  /** The verified, decoded request. */
  private final CanvasRequest canvasRequest;

  /** The verified envelope as JSON. */
  private final String canvasRequestJson;

  /**
   * Verifies and decodes the signed request.
   *
   * @param signedRequest the signed request Salesforce posted to the canvas app
   * @param clientSecret the canvas app's consumer secret
   * @throws SecurityException if the signed request is malformed or fails verification
   * @throws IllegalArgumentException if the client secret is null or blank
   */
  public SalesforceCanvasServiceProvider(String signedRequest, String clientSecret) {
    this.canvasRequest = SignedRequest.verifyAndDecode(signedRequest, clientSecret);
    this.canvasRequestJson = SignedRequest.verifyAndDecodeAsJson(signedRequest, clientSecret);
  }

  /**
   * Returns the decoded request.
   *
   * @return the canvas request
   */
  public CanvasRequest getCanvasRequest() {
    return canvasRequest;
  }

  /**
   * Returns the decoded request as JSON. This is the full envelope, <strong>including the OAuth and
   * refresh tokens</strong>: redact them before writing it to a log.
   *
   * @return the envelope JSON
   */
  public String getCanvasRequestJson() {
    return canvasRequestJson;
  }

  /**
   * Creates a {@link Salesforce} binding that sends the request's OAuth token to its instance URL.
   *
   * @return a new Salesforce binding
   */
  public Salesforce getApi() {
    return new SalesforceTemplate(
        canvasRequest.client().oauthToken(), canvasRequest.client().instanceUrl());
  }
}
