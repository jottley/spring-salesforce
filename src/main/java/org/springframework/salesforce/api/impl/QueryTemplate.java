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
package org.springframework.salesforce.api.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.salesforce.api.QueryOperations;
import org.springframework.salesforce.api.QueryResult;
import org.springframework.salesforce.api.ResultItem;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.web.client.RestTemplate;

/**
 * Default implementation of {@link QueryOperations} for SOQL queries.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class QueryTemplate extends AbstractSalesForceOperations<Salesforce> implements QueryOperations {

    /** The shared rest template used for requests. */
    private final RestTemplate restTemplate;

    /**
     * Creates the operations instance.
     *
     * @param api the {@link Salesforce} instance this operations object belongs to
     * @param restTemplate the shared rest template used for requests
     */
    public QueryTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }

    /**
     * Runs a SOQL query, excluding deleted and archived records.
     *
     * @param query the SOQL query
     * @return the first page of results
     */
    @Override
    public QueryResult query(String query) {
        return query(query, false);
    }

    /**
     * Runs a SOQL query, optionally including deleted and archived records.
     *
     * @param query the SOQL query
     * @param includeDeletedItems {@code true} to use the {@code queryAll} resource
     * @return the first page of results
     */
    @Override
    public QueryResult query(String query, boolean includeDeletedItems) {
        String queryType = includeDeletedItems ? "/queryAll" : "/query";
        requireAuthorization();
        URI uri = queryUri(api.getBaseUrl() + "/" + getVersion() + queryType, "q", query);

        return restTemplate.getForObject(uri, QueryResult.class);
    }

    /**
     * Fetches the next page of a query result.
     * <p>
     * A value containing {@code /} is treated as a path (the {@code nextRecordsUrl}) on the same host as the
     * API base URL; anything else is treated as a query locator.
     *
     * @param pathOrToken the {@code nextRecordsUrl} or the query locator
     * @return the next page of results
     * @throws IllegalArgumentException if the value is null, blank, {@code .} or {@code ..}
     */
    @Override
    public QueryResult nextPage(String pathOrToken) {
        requireAuthorization();
        requirePathSegment("query locator", pathOrToken);
        if (pathOrToken.contains("/")) {
            // Take the host from getBaseUrl() so it matches the one the first page was fetched from.
            // getInstanceUrl() can be null (the template then falls back to its default host), which
            // would produce a "null/services/..." URL.
            // The path is appended to the scheme and authority as text rather than resolved as a URI:
            // resolving would let a value such as "//other.host/x" replace the host, and this request
            // carries the bearer token.
            URI base = URI.create(api.getBaseUrl());
            String path = pathOrToken.startsWith("/") ? pathOrToken : "/" + pathOrToken;
            return restTemplate.getForObject(base.getScheme() + "://" + base.getRawAuthority() + path, QueryResult.class);
        } else {
            return restTemplate.getForObject(api.getBaseUrl() + "/" + getVersion() + "/query/{token}", QueryResult.class, pathOrToken);
        }
    }

    /**
     * Pages through the result set and aggregates all sObjects into a single result set, including deleted and
     * archived records (it uses the {@code queryAll} resource).
     * USUAL WARNINGS ABOUT RESULT SET SIZE APPLY.
     *
     * @param query the SOQL query
     * @return a result containing the records from every page
     */
    @Override
    public QueryResult queryAll(String query) {
        return queryAll(query, true);
    }

    /**
     * Pages through the result set and aggregates all sObjects into a single result set, optionally including
     * deleted and archived records.
     * USUAL WARNINGS ABOUT RESULT SET SIZE APPLY.
     * <p>
     * Later pages are fetched from the {@code nextRecordsUrl} that Salesforce returns with each page.
     *
     * @param query the SOQL query
     * @param includeDeletedItems {@code true} to use the {@code queryAll} resource, {@code false} to use
     *        {@code query} and exclude deleted and archived records
     * @return a result containing the records from every page
     */
    @Override
    public QueryResult queryAll(String query, boolean includeDeletedItems) {

        QueryResult page = query(query, includeDeletedItems);
        // set up the final result set. (mostly to get the done flag set properly)
        QueryResult results = new QueryResult(page.getTotalSize(), true);
        // A new list, so appending later pages never changes the first page's own list
        List<ResultItem> records = new ArrayList<>();
        if (page.getRecords() != null) {
            records.addAll(page.getRecords());
        }
        results.setRecords(records);

        while (StringUtils.isNotBlank(page.getNextRecordsUrl())) {
            page = nextPage(page.getNextRecordsUrl());
            if (page.getRecords() != null) {
                records.addAll(page.getRecords());
            }
        }
        return results;
    }

}
