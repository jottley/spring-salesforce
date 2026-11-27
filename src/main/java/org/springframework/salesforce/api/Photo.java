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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Chatter profile photo URLs (small and large) of a user, as reported by Salesforce.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo {

  /** The URL of the small photo. */
  private String smallPhotoUrl;

  /** The URL of the large photo. */
  private String largePhotoUrl;

  /** Creates an empty photo. */
  public Photo() {
    // NOOP
  }

  /**
   * Returns the URL of the small photo.
   *
   * @return the small photo URL
   */
  public String getSmallPhotoUrl() {
    return smallPhotoUrl;
  }

  /**
   * Sets the URL of the small photo.
   *
   * @param smallPhotoUrl the small photo URL
   */
  public void setSmallPhotoUrl(String smallPhotoUrl) {
    this.smallPhotoUrl = smallPhotoUrl;
  }

  /**
   * Returns the URL of the large photo.
   *
   * @return the large photo URL
   */
  public String getLargePhotoUrl() {
    return largePhotoUrl;
  }

  /**
   * Sets the URL of the large photo.
   *
   * @param largePhotoUrl the large photo URL
   */
  public void setLargePhotoUrl(String largePhotoUrl) {
    this.largePhotoUrl = largePhotoUrl;
  }
}
