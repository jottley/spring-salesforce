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

import java.util.List;
import java.util.Map;

/**
 * Defines operations for getting info on the API.
 *
 * <p>It also holds the API version used by all of the other operations.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public interface ApiOperations {

  /** The API version used when none has been set with {@link #setVersion(String)}. */
  static final String DEFAULT_API_VERSION = "v68.0";

  /**
   * The lowest Salesforce API version, as a major version number, that this library will use. Any
   * request for an older version is rejected with an {@link InvalidSalesforceApiVersionException}.
   */
  static final int MINIMUM_API_VERSION = 31;

  /**
   * Lists the API versions the Salesforce instance supports. This call does not require
   * authorization.
   *
   * @return the available versions
   */
  List<ApiVersion> getVersions();

  /**
   * Lists the REST resources available for the given API version.
   *
   * @param version the API version, for example {@code v68.0}
   * @return a map of resource names to their URLs
   * @throws InvalidSalesforceApiVersionException if the version is blank, not in the form {@code
   *     v<major>.<digit>} (one to three digits, a dot, then exactly one digit, for example {@code
   *     v68.0}), or its major version is below {@link #MINIMUM_API_VERSION}
   */
  Map<String, String> getServices(String version);

  /**
   * Lists the REST resources available for the API version currently in use, as returned by {@link
   * #getVersion()}.
   *
   * @return a map of resource names to their URLs
   */
  Map<String, String> getServices();

  /**
   * Sets the API version used by the operations.
   *
   * @param version the API version, for example {@code v68.0}
   * @throws InvalidSalesforceApiVersionException if the version is blank, not in the form {@code
   *     v<major>.<digit>} (one to three digits, a dot, then exactly one digit, for example {@code
   *     v68.0}), or its major version is below {@link #MINIMUM_API_VERSION}
   */
  void setVersion(String version) throws InvalidSalesforceApiVersionException;

  /**
   * Returns the API version in use.
   *
   * @return the version that was set, or {@link #DEFAULT_API_VERSION} if none was set
   */
  String getVersion();
}
