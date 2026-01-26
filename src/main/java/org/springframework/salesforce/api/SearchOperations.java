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

/**
 * Defines operations to execute SOSL search queries.
 *
 * @author Umut Utkan
 */
public interface SearchOperations {

    /**
     * Execute SOSL Query and retrieve results
     *
     * @param soslQuery The SOSL Query to execute
     * @return List of ResultItem
     */
    List<ResultItem> search(String soslQuery);


}
