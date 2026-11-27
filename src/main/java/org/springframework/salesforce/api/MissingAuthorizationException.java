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
 * Exception thrown when an operations object has no authorization context to work with.
 * <p>
 * The operations implementations throw it when they have no {@link org.springframework.salesforce.api.Salesforce}
 * API instance to work with, before any request is made. It does not check for an access token: a missing or
 * rejected token is reported as an {@link InvalidAuthorizationException}.
 *
 * @author Jared Ottley
 */
public class MissingAuthorizationException extends ApiException {

    /**
     * Creates the exception with a detail message.
     *
     * @param message the detail message
     */
    public MissingAuthorizationException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a detail message and a cause.
     *
     * @param message the detail message
     * @param cause the underlying cause
     */
    public MissingAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
