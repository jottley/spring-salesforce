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
package org.springframework.salesforce.api.impl;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.api.SalesforceUserDetails;
import org.springframework.salesforce.api.UserOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Default implementation of {@link UserOperations}.
 *
 * @author Alexandra Leahu
 * @author Jared Ottley
 */
public class UserOperationsTemplate extends AbstractSalesForceOperations<Salesforce>
    implements UserOperations {

  /** The shared rest template used for requests. */
  private final RestTemplate restTemplate;

  /**
   * Creates the operations instance.
   *
   * @param api the {@link Salesforce} instance this operations object belongs to
   * @param restTemplate the shared rest template used for requests
   */
  public UserOperationsTemplate(Salesforce api, RestTemplate restTemplate) {
    super(api);
    this.restTemplate = restTemplate;
  }

  /**
   * Retrieves the current user's details from the OAuth2 userinfo endpoint.
   *
   * @return the user details
   */
  @Override
  public SalesforceUserDetails getSalesforceUserDetails() {
    requireAuthorization();
    return restTemplate
        .exchange(
            api.getUserInfoUrl(),
            HttpMethod.GET,
            new HttpEntity<>(""),
            SalesforceUserDetails.class,
            getVersion())
        .getBody();
  }
}
