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
 * Creates {@link Salesforce} bindings from a Canvas signed request, the Canvas counterpart of the
 * OAuth connection factories: Salesforce hands the canvas app a token with the request, so there is
 * no authorization flow to run.
 *
 * <p>Build one per request: it verifies the signed request in its constructor, and {@link
 * #createApi()} returns a {@link Salesforce} that acts as the user who opened the canvas app. See
 * the {@linkplain org.springframework.salesforce.canvas package documentation} for a full example
 * and for what to watch for.
 *
 * <pre>
 * Salesforce salesforce =
 *     new SalesforceCanvasConnectionFactory(signedRequest, consumerSecret).createApi();
 * </pre>
 */
public class SalesforceCanvasConnectionFactory {

  /** Verifies the signed request and builds the API bindings. */
  private final SalesforceCanvasServiceProvider serviceProvider;

  /**
   * Creates a SalesforceCanvasConnectionFactory with the given signed request and client secret.
   *
   * @param signedRequest the signed request
   * @param clientSecret the client secret
   * @throws SecurityException if the signed request is malformed or fails verification
   */
  public SalesforceCanvasConnectionFactory(String signedRequest, String clientSecret) {
    this.serviceProvider = new SalesforceCanvasServiceProvider(signedRequest, clientSecret);
  }

  /**
   * Returns the SalesforceCanvasServiceProvider instance.
   *
   * @return the service provider
   */
  public SalesforceCanvasServiceProvider getServiceProvider() {
    return serviceProvider;
  }

  /**
   * Creates and returns the Salesforce API instance.
   *
   * @return the Salesforce API instance
   */
  public Salesforce createApi() {
    return serviceProvider.getApi();
  }
}
