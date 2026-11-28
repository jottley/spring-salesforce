/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-salesforce
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
package org.springframework.salesforce.api.impl;

import java.io.IOException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Request interceptor that inspects each response from the Salesforce API.
 *
 * <p>It reads the {@code Sforce-Limit-Info} header and passes it to the {@link
 * LimitsOperationsTemplate} to record the current daily API usage after each call.
 *
 * @author Jared Ottley
 */
public class ApiRequestInterceptor implements ClientHttpRequestInterceptor {

  // The header will look like -> Sforce-Limit-Info: api-usage=39/15000
  /** Name of the response header that carries the daily API usage. */
  private static final String SFORCE_LIMIT_INFO = "Sforce-Limit-Info";

  /** The template whose limits operations record the usage. */
  private final SalesforceTemplate salesforceTemplate;

  /**
   * Creates the interceptor.
   *
   * @param salesforceTemplate the template whose limits operations receive the usage data
   */
  public ApiRequestInterceptor(SalesforceTemplate salesforceTemplate) {
    this.salesforceTemplate = salesforceTemplate;
  }

  /**
   * Executes the request, then records the API usage reported in the response headers.
   *
   * @param request the request
   * @param body the request body
   * @param execution the execution chain
   * @return the response
   * @throws IOException if the request fails
   */
  @Override
  public ClientHttpResponse intercept(
      @NonNull HttpRequest request,
      @NonNull byte[] body,
      @NonNull ClientHttpRequestExecution execution)
      throws IOException {
    ClientHttpResponse response = execution.execute(request, body);
    processSForceLimitInfoHeader(response);
    return response;
  }

  /**
   * Checks the response for the {@code Sforce-Limit-Info} header and, if present, updates the
   * limits operations with the current API usage.
   *
   * @param response the response to inspect
   */
  private void processSForceLimitInfoHeader(ClientHttpResponse response) {
    if (response.getHeaders().get(SFORCE_LIMIT_INFO) != null) {
      ((LimitsOperationsTemplate) salesforceTemplate.limitsOperations())
          .setCurrentDailyApiLimits(response.getHeaders().getFirst(SFORCE_LIMIT_INFO));
    }
  }
}
