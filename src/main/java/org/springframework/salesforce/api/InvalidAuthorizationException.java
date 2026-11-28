/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-social-salesforce
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
 * Exception thrown when authorization credentials are invalid or expired.
 *
 * <p>{@link org.springframework.salesforce.api.impl.SalesforceErrorHandler} throws it for an HTTP
 * 401 response, and for an HTTP 403 response whose {@code errorCode} is {@code Bad_OAuth_Token}
 * (including the case where Salesforce sends {@code Bad_OAuth_Token} as a bare string rather than
 * JSON). {@link org.springframework.salesforce.client.ErrorHandler} throws it for the OAuth2 token
 * errors {@code invalid_client_id}, {@code invalid_client_credentials}, {@code invalid_scope} and
 * {@code invalid_grant} (except when the description is {@code IP restricted or invalid login
 * hours}).
 *
 * @author Jared Ottley
 */
public class InvalidAuthorizationException extends ApiException {

  /**
   * Creates the exception with a detail message.
   *
   * @param message the detail message
   */
  public InvalidAuthorizationException(String message) {
    super(message);
  }

  /**
   * Creates the exception with a detail message and a cause.
   *
   * @param message the detail message
   * @param cause the underlying cause
   */
  public InvalidAuthorizationException(String message, Throwable cause) {
    super(message, cause);
  }
}
