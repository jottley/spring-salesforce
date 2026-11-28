/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-salesforce
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
 * Exception thrown when the API server returns an internal server error.
 *
 * <p>{@link org.springframework.salesforce.api.impl.SalesforceErrorHandler} throws it for an HTTP
 * 500 response.
 *
 * @author Jared Ottley
 */
public class InternalServerErrorException extends ApiException {

  /**
   * Creates the exception with a detail message.
   *
   * @param message the detail message from Salesforce
   */
  public InternalServerErrorException(String message) {
    super(message);
  }

  /**
   * Creates the exception with a detail message and a cause.
   *
   * @param message the detail message from Salesforce
   * @param cause the underlying cause
   */
  public InternalServerErrorException(String message, Throwable cause) {
    super(message, cause);
  }
}
