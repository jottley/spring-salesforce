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
import java.util.Collections;
import java.util.List;

import org.springframework.salesforce.api.ResultItem;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.api.SearchOperations;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Default implementation of {@link SearchOperations} for SOSL searches.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SearchTemplate extends AbstractSalesForceOperations<Salesforce> implements SearchOperations {

    /** The shared rest template used for requests. */
    private final RestTemplate restTemplate;

    /**
     * Creates the operations instance.
     *
     * @param api the {@link Salesforce} instance this operations object belongs to
     * @param restTemplate the shared rest template used for requests
     */
    public SearchTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }

    /**
     * Runs a SOSL search. Both the bare-array response of API versions 31 to 36 and the
     * {@code searchRecords} object of version 37 and later are accepted.
     *
     * @param soslQuery the SOSL query
     * @return the matching items, or an empty list if there are none
     */
    @Override
    public List<ResultItem> search(String soslQuery) {
        requireAuthorization();
        URI uri = queryUri(api.getBaseUrl() + "/" + getVersion() + "/search", "q", soslQuery);

        JsonNode root = restTemplate.getForObject(uri, JsonNode.class);

        // API versions 31 through 36 return the records as a bare array. Version 37 and later return an object
        // with the records under "searchRecords" (checked against a live org, every version from 31 to 68).
        // Both are accepted, and no records at all gives an empty list.
        JsonNode records = root != null && root.isObject() ? root.get("searchRecords") : root;
        if (records == null || records.isNull()) {
            return Collections.emptyList();
        }
        return api.readList(records, ResultItem.class);
    }

}
