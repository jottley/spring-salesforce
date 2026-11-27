/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-social-salesforce
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

/**
 * Defines operations for interacting with the Limits API.
 *
 * @author Jared Ottley
 */
public interface LimitsOperations {

  /**
   * Retrieves the organization limits from the Salesforce Limits resource.
   *
   * @return the organization limits
   */
  public LimitsResults getLimits();

  /**
   * Gets the current Daily API limit. This value is read from the {@code Sforce-Limit-Info} header
   * of the most recent response that carried an {@code api-usage} entry, so it is not updated by
   * responses without one.
   *
   * @return the daily API request limit, or 0 if no such response has been seen yet
   */
  public int getDailyApiLimit();

  /**
   * Gets the current Daily API usage. This value is read from the {@code Sforce-Limit-Info} header
   * of the most recent response that carried an {@code api-usage} entry, so it is not updated by
   * responses without one.
   *
   * @return the number of daily API requests used, or 0 if no such response has been seen yet
   */
  public int getDailyApiUsed();
}
