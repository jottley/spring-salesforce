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
 * Defines operations for interacting with the Limits API.
 *
 * @author Jared Ottley
 */
public interface LimitsOperations {


    /**
     * Retrieve the organization limits
     *
     * @return LimitsResults
     */
    public LimitsResults getLimits();

    /**
     * Get the current Daily API limit.  This value is returned in a header from Salesforce.  The value is persisted here after each API call.
     * @return
     */
    public int getDailyApiLimit();


    /**
     * Get the current Daily API usage.  This value is returned in a header from Salesforce.  The value is persisted here after each API call.
     * @return
     */
    public int getDailyApiUsed();

}