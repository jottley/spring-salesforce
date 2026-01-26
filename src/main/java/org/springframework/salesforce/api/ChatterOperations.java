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
 * Defines operations for interacting with the Chatter API.
 *
 * @author Umut Utkan
 */
public interface ChatterOperations {

    /**
     * Retrieves current users's profile
     *
     * @return user profile
     */
    public SalesforceProfile getUserProfile();

    /**
     * Retrieves the given user's profile
     *
     * @param userId The salesforce Id of the user
     * @return user profile
     */
    public SalesforceProfile getUserProfile(String userId);

    /**
     * Retrieves current user's status
     *
     * @return status
     */
    public Status getStatus();

    /**
     * Retrieves the given user's status
     *
     * @param userId The salesforce Id of the user
     * @return status
     */
    public Status getStatus(String userId);

    /**
     * Updates current user's status with the given message
     *
     * @param message The message to be posted as the current user's status
     * @return status
     */
    public Status updateStatus(String message);

    /**
     * Updates the given user's status with the given message
     *
     * @param userId The salesforce Id of the user
     * @param message The message to be posted as the current user's status
     * @return status
     */
    public Status updateStatus(String userId, String message);

}
