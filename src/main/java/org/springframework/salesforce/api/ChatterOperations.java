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
package org.springframework.salesforce.api;

/**
 * Defines operations for interacting with the Chatter API.
 *
 * @author Umut Utkan
 */
public interface ChatterOperations {

  /**
   * Retrieves the current user's profile
   *
   * @return user profile
   */
  public SalesforceProfile getUserProfile();

  /**
   * Retrieves the given user's profile
   *
   * @param userId the Salesforce Id of the user, or {@code me} for the current user
   * @return user profile
   * @throws IllegalArgumentException if the user id is null, blank, {@code .} or {@code ..}
   */
  public SalesforceProfile getUserProfile(String userId);

  /**
   * Retrieves current user's status
   *
   * @return status, or null if none was returned
   * @deprecated Salesforce retired the Chatter user status resource in API version 25, which is
   *     below this library's minimum of v31.0, so this call is expected to be rejected by
   *     Salesforce (typically as a {@code ResourceNotFoundException}); if a response without a
   *     status body is returned instead, the result is null. There is no replacement; it will be
   *     removed in a future major version.
   */
  @Deprecated
  public Status getStatus();

  /**
   * Retrieves the given user's status
   *
   * @param userId the Salesforce Id of the user, or {@code me} for the current user
   * @return status, or null if none was returned
   * @throws IllegalArgumentException if the user id is null, blank, {@code .} or {@code ..}
   * @deprecated Salesforce retired the Chatter user status resource in API version 25, which is
   *     below this library's minimum of v31.0, so this call is expected to be rejected by
   *     Salesforce (typically as a {@code ResourceNotFoundException}); if a response without a
   *     status body is returned instead, the result is null. There is no replacement; it will be
   *     removed in a future major version.
   */
  @Deprecated
  public Status getStatus(String userId);

  /**
   * Updates current user's status with the given message
   *
   * @param message The message to be posted as the current user's status
   * @return status, or null if none was returned
   * @deprecated Salesforce retired the Chatter user status resource in API version 25, which is
   *     below this library's minimum of v31.0, so this call is expected to be rejected by
   *     Salesforce (typically as a {@code ResourceNotFoundException}); if a response without a
   *     status body is returned instead, the result is null. There is no replacement; it will be
   *     removed in a future major version.
   */
  @Deprecated
  public Status updateStatus(String message);

  /**
   * Updates the given user's status with the given message
   *
   * @param userId the Salesforce Id of the user, or {@code me} for the current user
   * @param message the message to be posted as that user's status
   * @return status, or null if none was returned
   * @throws IllegalArgumentException if the user id is null, blank, {@code .} or {@code ..}
   * @deprecated Salesforce retired the Chatter user status resource in API version 25, which is
   *     below this library's minimum of v31.0, so this call is expected to be rejected by
   *     Salesforce (typically as a {@code ResourceNotFoundException}); if a response without a
   *     status body is returned instead, the result is null. There is no replacement; it will be
   *     removed in a future major version.
   */
  @Deprecated
  public Status updateStatus(String userId, String message);
}
