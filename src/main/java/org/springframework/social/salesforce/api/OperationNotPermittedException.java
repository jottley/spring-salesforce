/**
 * Copyright (C) 2019 https://github.com/jottley/spring-social-salesforce
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
package org.springframework.social.salesforce.api;

/**
 * Exception thrown when an operation is not permitted for the current user or organization.
 */
public class OperationNotPermittedException extends ApiException {
    
    public OperationNotPermittedException(String message) {
        super(message);
    }
    
    public OperationNotPermittedException(String message, Throwable cause) {
        super(message, cause);
    }
}
