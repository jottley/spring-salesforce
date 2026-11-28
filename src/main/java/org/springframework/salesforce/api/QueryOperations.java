/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-social-salesforce
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
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
   * @param includeDeletedItems {@code true} to also return deleted and archived records, by using
   *     the {@code queryAll} resource instead of {@code query}
   * @return QueryResult
   * @see <a
   *     href="https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_queryall.htm">QueryAll</a>
   */
  QueryResult query(String query, boolean includeDeletedItems);

  /**
   * Retrieves the next page of a query result.
   *
   * <p>The argument is usually the {@code nextRecordsUrl} of the previous {@link QueryResult}, a
   * path such as {@code /services/data/v68.0/query/01gxx-2000}, or just the query locator token. A
   * path is always sent to the same host as the first page, never to a host named in the argument.
   *
   * @param urlOrToken the {@code nextRecordsUrl} path or the query locator token
   * @return the next page of results
   * @throws IllegalArgumentException if the value is null, blank, {@code .} or {@code ..}
   */
  QueryResult nextPage(String urlOrToken);

  /**
   * Executes the SOQL query and follows {@code nextRecordsUrl} until every page has been read,
   * returning all records in one result. Deleted and archived records are included: it uses the
   * {@code queryAll} resource. To exclude them, use {@link #queryAll(String, boolean)} with {@code
   * false}. The whole result set is held in memory, so large result sets are costly.
   *
   * @param query The SOQL query to execute
   * @return QueryResult
   */
  QueryResult queryAll(String query);

  /**
   * Executes the SOQL query and follows {@code nextRecordsUrl} until every page has been read,
   * returning all records in one result, optionally including deleted and archived records. {@code
   * queryAll(query, true)} is the same as {@link #queryAll(String)}; {@code queryAll(query, false)}
   * uses the {@code query} resource and excludes deleted and archived records. The whole result set
   * is held in memory, so large result sets are costly.
   *
   * @param query The SOQL query to execute
   * @param includeDeletedItems {@code true} to include deleted and archived records
   * @return QueryResult
   */
  QueryResult queryAll(String query, boolean includeDeletedItems);
}
