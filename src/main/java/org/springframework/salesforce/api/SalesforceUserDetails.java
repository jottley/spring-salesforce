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
package org.springframework.salesforce.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 * User details returned by the OAuth2 userinfo endpoint (OpenID Connect property names). Extends
 * {@link SalesforceProfile}. Userinfo names two properties differently from the Chatter profile, so
 * they are mapped: {@code photos} ({@code picture} and {@code thumbnail}) becomes {@link
 * #getPhoto()} (large and small URL), and {@code preferred_username} is returned by both {@link
 * #getUsername()} and {@link #getPreferredUsername()}. Unknown properties in the JSON are ignored.
 *
 * @author Alexandra Leahu
 * @author Jared Ottley
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceUserDetails extends SalesforceProfile {
  /** The id of the user's organization. */
  private final String organizationId;

  /** The user's nickname. */
  private final String nickname;

  /** The user's preferred username. */
  private final String preferredUsername;

  /**
   * Creates the user details.
   *
   * @param id the user's id
   * @param firstName the user's first name
   * @param lastName the user's last name
   * @param email the user's email address
   * @param name the user's full name
   * @param organizationId the id of the user's organization
   * @param nickname the user's nickname
   * @param preferredUsername the user's preferred username
   */
  public SalesforceUserDetails(
      String id,
      String firstName,
      String lastName,
      String email,
      String name,
      String organizationId,
      String nickname,
      String preferredUsername) {
    super();
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.name = name;
    this.organizationId = organizationId;
    this.nickname = nickname;
    this.preferredUsername = preferredUsername;
    // Userinfo has no "username" property; the inherited getter returns the preferred username
    this.username = preferredUsername;
  }

  /**
   * Sets the photo from the userinfo {@code photos} object, where {@code picture} is the large
   * photo and {@code thumbnail} the small one. Called by Jackson (through the mixin); a missing or
   * {@code null} {@code photos} leaves {@link #getPhoto()} {@code null}.
   *
   * @param photos the userinfo {@code photos} object, or {@code null}
   */
  private void setPhotos(Map<String, String> photos) {
    if (photos == null) {
      return;
    }
    Photo mapped = new Photo();
    mapped.setLargePhotoUrl(photos.get("picture"));
    mapped.setSmallPhotoUrl(photos.get("thumbnail"));
    this.photo = mapped;
  }

  /**
   * Returns the id of the user's organization.
   *
   * @return the organization id
   */
  public String getOrganizationId() {
    return organizationId;
  }

  /**
   * Returns the user's nickname.
   *
   * @return the nickname
   */
  public String getNickname() {
    return nickname;
  }

  /**
   * Returns the user's preferred username.
   *
   * @return the preferred username
   */
  public String getPreferredUsername() {
    return preferredUsername;
  }
}
