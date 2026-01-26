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

import java.util.List;
import java.util.Map;

/**
 * Defines operations for getting info on the API.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public interface ApiOperations {

    static final String DEFAULT_API_VERSION = "v37.0";

    List<ApiVersion> getVersions();

    Map<String, String> getServices(String version);

    Map<String, String> getServices();

    void setVersion(String version)
            throws InvalidSalesforceApiVersionException;

    String getVersion();
}
