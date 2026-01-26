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
 * Defines operations for executing SOQL queries.
 *
 * @author Umut Utkan
 */
public interface QueryOperations {

    /**
     * Execute SOQL query and return the first page of results
     *
     * @param query The SOQL query to execute
     * @return QueryResult
     */
    QueryResult query(String query);

    /**
     * Execute SOQL query and return the first page of results
     *
     * @param query The SOQL query to execute
     * @param includeDeletedItems if result should include deleted items
     * documentation https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_queryall.htm
     * @return QueryResult
     */
    QueryResult query(String query, boolean includeDeletedItems);

    /*
     * Retrieve next page of results based on argument from url (usually from query result "nextRecordsUrl")
     */
    QueryResult nextPage(String urlOrToken);

    /**
     * Execute SOQL query and return all results.
     *
     * @param query The SOQL query to execute
     * @return QueryResult
     */
    QueryResult queryAll(String query);

}
