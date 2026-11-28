/**
 * Copyright (C) 2021-2026 https://github.com/jottley/spring-salesforce
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

import java.util.List;

/**
 * Defines operations for interacting with the Salesforce Connect (Communities / Digital Experience)
 * API.
 *
 * @author Jared Ottley
 */
public interface ConnectOperations {

  /**
   * Retrieves the communities available to the current user.
   *
   * @return the communities
   */
  List<Community> getCommunities();

  /**
   * Retrieves the users of a community.
   *
   * @param communityId the ID of the community
   * @return the users of the community
   * @throws IllegalArgumentException if the community id is null, blank, {@code .} or {@code ..}
   */
  List<CommunityUser> getCommunityUsers(String communityId);
}
