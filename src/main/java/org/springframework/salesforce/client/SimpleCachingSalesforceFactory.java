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
package org.springframework.salesforce.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.salesforce.api.Salesforce;

/**
 * Simple caching wrapper for SalesforceFactory.
 *
 * @author Umut Utkan
 */
public class SimpleCachingSalesforceFactory implements SalesforceFactory {

    private SalesforceFactory delegate;

    private final Map<String, Salesforce> cache = new HashMap<String, Salesforce>();


    public SimpleCachingSalesforceFactory(SalesforceFactory delegate) {
        this.delegate = delegate;
    }


    @Override
    public Salesforce create(String username, String password, String securityToken) {
        synchronized (this.cache) {
            Salesforce template = cache.get(username);
            if (template == null) {
                this.cache.put(username, this.delegate.create(username, password, securityToken));
            }
            return this.cache.get(username);
        }
    }

}
