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
package org.springframework.salesforce.client;

import org.springframework.salesforce.api.Salesforce;

/**
 * Factory for {@link Salesforce} instances created by logging in with a username, password and
 * optional security token.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public interface SalesforceFactory {

  /**
   * Creates a {@link Salesforce} instance by logging in with the given credentials.
   *
   * @param username the Salesforce username
   * @param password the user's password
   * @param securityToken the user's security token; may be {@code null}
   * @return a new {@link Salesforce} instance
   */
  Salesforce create(String username, String password, String securityToken);
}
