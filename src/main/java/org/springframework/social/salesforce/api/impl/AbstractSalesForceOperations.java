/**
 * Copyright (C) 2017 https://github.com/jottley/spring-social-salesforce
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
package org.springframework.social.salesforce.api.impl;

import org.springframework.social.salesforce.api.MissingAuthorizationException;
import org.springframework.social.salesforce.api.Salesforce;

/**
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class AbstractSalesForceOperations<T> {

    protected T api;


    public AbstractSalesForceOperations(T api) {
        this.api = api;
    }

    protected void requireAuthorization() {
        // For now, we assume if we have an API instance, we're authorized
        // In a real implementation, you might want to check token validity
        if (api == null) {
            throw new MissingAuthorizationException("API instance is null - authorization required");
        }
    }

    protected static String BASE_URL = "https://na7.salesforce.com/services/data";
    
    
    public String getVersion()
    {
        return ((Salesforce)api).apiOperations().getVersion();
    }

}
