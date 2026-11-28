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

import java.io.Serializable;

/**
 * A Salesforce API version, as listed by the instance's {@code /services/data} resource.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class ApiVersion implements Serializable {

  /** The release label. */
  private final String label;

  /** The version number. */
  private final String version;

  /** The root path of this version, relative to the instance URL. */
  private final String url;

  /**
   * Creates an API version.
   *
   * @param version the version number, for example {@code 68.0}
   * @param label the release label, for example {@code Winter '27}
   * @param url the root path of this version, relative to the instance URL, for example {@code
   *     /services/data/v68.0}
   */
  public ApiVersion(String version, String label, String url) {
    this.version = version;
    this.label = label;
    this.url = url;
  }

  /**
   * Returns the release label.
   *
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Returns the version number.
   *
   * @return the version number
   */
  public String getVersion() {
    return version;
  }

  /**
   * Returns the root path of this version, relative to the instance URL.
   *
   * @return the URL
   */
  public String getUrl() {
    return url;
  }

  /**
   * Returns a string showing the label, version and URL.
   *
   * @return a description of this version
   */
  @Override
  public String toString() {
    return "ApiVersion{"
        + "label='"
        + label
        + '\''
        + ", version='"
        + version
        + '\''
        + ", url='"
        + url
        + '\''
        + '}';
  }
}
