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
package org.springframework.salesforce.api.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.springframework.salesforce.api.Community;
import org.springframework.salesforce.api.CommunityUser;
import org.springframework.salesforce.api.ConnectOperations;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.web.client.RestTemplate;

/**
 * Default implementation of {@link ConnectOperations}, for Experience Cloud (communities)
 * resources.
 *
 * @author Jared Ottley
 */
public class ConnectTemplate extends AbstractSalesForceOperations<Salesforce>
    implements ConnectOperations {

  /** The shared rest template used for requests. */
  private final RestTemplate restTemplate;

  /** Path of the communities resource, appended to the versioned base URL. */
  private static final String CONNECT_URI = "/connect/communities/";

  /** Path segment of the Chatter users resource within a community. */
  private static final String CHATTER_USERS = "/chatter/users";

  /**
   * Creates the operations instance.
   *
   * @param api the {@link Salesforce} instance this operations object belongs to
   * @param restTemplate the shared rest template used for requests
   */
  public ConnectTemplate(Salesforce api, RestTemplate restTemplate) {
    super(api);
    this.restTemplate = restTemplate;
  }

  /**
   * Retrieves the communities available to the user.
   *
   * @return the communities; an empty list if the response has no body or no {@code communities}
   *     entry
   */
  @Override
  public List<Community> getCommunities() {

    requireAuthorization();

    JsonNode dataNode =
        restTemplate.getForObject(
            api.getBaseUrl() + "/" + getVersion() + CONNECT_URI, JsonNode.class, getVersion());

    if (dataNode == null || dataNode.isNull()) {
      return java.util.Collections.emptyList();
    }

    return emptyIfNull(api.readList(dataNode.get("communities"), Community.class));
  }

  /**
   * Retrieves the first page of users of a community; later pages are not fetched.
   *
   * @param communityId the community id
   * @return the community users; an empty list if the response has no body or no {@code users}
   *     entry
   * @throws IllegalArgumentException if the community id is null, blank, {@code .} or {@code ..}
   */
  @Override
  public List<CommunityUser> getCommunityUsers(String communityId) {

    requireAuthorization();

    JsonNode dataNode =
        restTemplate.getForObject(
            api.getBaseUrl() + "/{version}" + CONNECT_URI + "{communityId}" + CHATTER_USERS,
            JsonNode.class,
            getVersion(),
            requirePathSegment("community id", communityId));

    if (dataNode == null || dataNode.isNull()) {
      return java.util.Collections.emptyList();
    }

    return emptyIfNull(api.readList(dataNode.get("users"), CommunityUser.class));
  }
}
