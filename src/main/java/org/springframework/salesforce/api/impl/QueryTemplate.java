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

import org.apache.commons.lang3.StringUtils;
import org.springframework.salesforce.api.QueryOperations;
import org.springframework.salesforce.api.QueryResult;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Default implementation of QueryOperations.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class QueryTemplate extends AbstractSalesForceOperations<Salesforce> implements QueryOperations {

    private final RestTemplate restTemplate;

    public QueryTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }

    @Override
    public QueryResult query(String query) {
        return query(query, false);
    }

    @Override
    public QueryResult query(String query, boolean includeDeletedItems) {
        String queryType = includeDeletedItems ? "/queryAll" : "/query";
        requireAuthorization();
        URI uri = UriComponentsBuilder.fromUriString(api.getBaseUrl() + "/" + getVersion() + queryType).queryParam("q", query).build().toUri();

        if (uri == null || StringUtils.isBlank(uri.toString())) {
            throw new IllegalArgumentException("Query URI cannot be null or empty");
        }

        return restTemplate.getForObject(uri, QueryResult.class);
    }

    @Override
    public QueryResult nextPage(String pathOrToken) {
        requireAuthorization();
        if (pathOrToken.contains("/")) {
            return restTemplate.getForObject(api.getInstanceUrl() + pathOrToken, QueryResult.class);
        } else {
            return restTemplate.getForObject(api.getBaseUrl() + "/" + getVersion() + "/query/{token}", QueryResult.class, pathOrToken);
        }
    }

    /**
     * Pages through the result set and aggregates all sObjects into the single result set.<br>
     * USUAL WARNINGS ABOUT RESULT SET SIZE APPLY
     * @see org.springframework.salesforce.api.QueryOperations#queryAll(java.lang.String)
     */
    @Override
    public QueryResult queryAll(String query) {

        QueryResult page = query(query);
        // set up the final result set. (mostly to get the done flag set properly)
        QueryResult results = new QueryResult(page.getTotalSize(), true);
        results.setRecords(page.getRecords());

        while (StringUtils.isNotBlank(page.getNextRecordsUrl())) {
            page = nextPage(page.getNextRecordsUrl());
            results.getRecords().addAll(page.getRecords());
        }
        return results;
    }

}
