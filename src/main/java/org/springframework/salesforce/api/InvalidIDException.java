/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-social-salesforce
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
 * Exception for an invalid Salesforce record id.
 * <p>
 * The library does not currently throw it itself; it is available to callers. Blank or unsafe record ids passed
 * to {@link SObjectOperations} are rejected with an {@link IllegalArgumentException} instead.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class InvalidIDException extends ApiException {

    /**
     * Creates the exception with a detail message.
     *
     * @param message the detail message
     */
    public InvalidIDException(String message) {
        super(message);
    }

}
