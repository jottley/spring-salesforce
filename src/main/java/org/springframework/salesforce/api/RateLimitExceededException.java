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
 * Exception thrown when the API rate limit has been exceeded, or when the service is throttling
 * requests.
 *
 * <p>{@link org.springframework.salesforce.api.impl.SalesforceErrorHandler} throws it for an HTTP
 * 429 or 503 response, and for an HTTP 403 response whose {@code errorCode} is {@code
 * REQUEST_LIMIT_EXCEEDED}. Every 503 is treated as a limit condition even if Salesforce gave
 * another reason (such as maintenance), and for 429 and 503 the message is always {@code Rate limit
 * exceeded}; the 403 case keeps Salesforce's message. {@link
 * org.springframework.salesforce.client.ErrorHandler} throws it for the OAuth2 error {@code
 * rate_limit_exceeded}.
 *
 * @author Jared Ottley
 */
public class RateLimitExceededException extends ApiException {

  /**
   * Creates the exception with a detail message.
   *
   * @param message the detail message
   */
  public RateLimitExceededException(String message) {
    super(message);
  }

  /**
   * Creates the exception with a detail message and a cause.
   *
   * @param message the detail message
   * @param cause the underlying cause
   */
  public RateLimitExceededException(String message, Throwable cause) {
    super(message, cause);
  }
}
