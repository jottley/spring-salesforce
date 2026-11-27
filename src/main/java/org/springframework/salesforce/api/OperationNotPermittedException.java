/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-social-salesforce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.salesforce.api;

/**
 * Exception thrown when an operation is not permitted for the current user or organization.
 * <p>
 * {@link org.springframework.salesforce.client.ErrorHandler} throws it for an OAuth2 token error (HTTP 400) of
 * {@code unsupported_response_type}, {@code invalid_request}, {@code inactive_user} or {@code inactive_org}, for
 * an {@code invalid_grant} with the description {@code IP restricted or invalid login hours}, and for any
 * unrecognized OAuth2 error.
 *
 * @author Jared Ottley
 */
public class OperationNotPermittedException extends ApiException {

    /**
     * Creates the exception with a detail message.
     *
     * @param message the detail message
     */
    public OperationNotPermittedException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a detail message and a cause.
     *
     * @param message the detail message
     * @param cause the underlying cause
     */
    public OperationNotPermittedException(String message, Throwable cause) {
        super(message, cause);
    }
}
