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
 * Exception thrown when an API error occurs that doesn't fit into a specific category.
 * <p>
 * {@link org.springframework.salesforce.api.impl.SalesforceErrorHandler} throws it for any HTTP status that is
 * not otherwise mapped, including non-standard statuses, and when the error response body cannot be read.
 *
 * @author Jared Ottley
 */
public class UncategorizedApiException extends ApiException {

    /**
     * Creates the exception with a detail message.
     *
     * @param message the detail message
     */
    public UncategorizedApiException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a detail message and a cause.
     *
     * @param message the detail message
     * @param cause the underlying cause
     */
    public UncategorizedApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
