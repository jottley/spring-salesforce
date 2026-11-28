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
 * Exception thrown when a Salesforce API version is malformed or older than the minimum this
 * library supports.
 *
 * <p>{@link ApiOperations#setVersion(String)} and {@link ApiOperations#getServices(String)} throw
 * it for a blank version, a version not in the form {@code v68.0}, or a major version below {@link
 * ApiOperations#MINIMUM_API_VERSION}.
 *
 * @author Jared Ottley
 */
public class InvalidSalesforceApiVersionException extends ApiException {
  /**
   * For a blank or malformed version.
   *
   * @param version the rejected version, which may be null or blank
   */
  public InvalidSalesforceApiVersionException(String version) {
    super(version + " is not a valid Salesforce Api version.");
  }

  /**
   * For a well-formed version that is older than the minimum this library supports.
   *
   * @param version the rejected version, for example {@code v30.0}
   * @param minimum the lowest major version accepted, for example {@code 31}
   */
  public InvalidSalesforceApiVersionException(String version, int minimum) {
    super(version + " is below the minimum supported Salesforce Api version (v" + minimum + ".0).");
  }
}
