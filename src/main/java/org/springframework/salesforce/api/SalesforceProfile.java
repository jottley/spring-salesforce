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
import java.io.Serializable;

/**
 * A Salesforce user's profile, as returned by the Chatter users resource. Unknown properties in the
 * JSON are ignored.
 *
 * @author Umut Utkan
 * @author Alexandra Leahu
 * @author Jared Ottley
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceProfile implements Serializable {
  /** The user's id. */
  protected String id;

  /** The user's email address. */
  protected String email;

  /** The user's first name. */
  protected String firstName;

  /** The user's last name. */
  protected String lastName;

  /** The user's photo. */
  protected Photo photo;

  /** The user's full name. */
  protected String name;

  /** The user's username. */
  protected String username;

  /** Creates an empty profile. Jackson populates the fields when deserializing. */
  public SalesforceProfile() {}

  /**
   * Returns the user's id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the user's email address.
   *
   * @return the email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Returns the user's first name.
   *
   * @return the first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Returns the user's last name.
   *
   * @return the last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Returns the user's photo.
   *
   * @return the photo
   */
  public Photo getPhoto() {
    return this.photo;
  }

  /**
   * Returns the user's full name.
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the user's username.
   *
   * @return the username
   */
  public String getUsername() {
    return this.username;
  }
}
