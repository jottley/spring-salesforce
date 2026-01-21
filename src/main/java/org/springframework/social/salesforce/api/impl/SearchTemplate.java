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

import java.net.URI;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.social.salesforce.api.ResultItem;
import org.springframework.social.salesforce.api.Salesforce;
import org.springframework.social.salesforce.api.SearchOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Default implementation of SearchOperations.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SearchTemplate extends AbstractSalesForceOperations<Salesforce> implements SearchOperations {

    private final RestTemplate restTemplate;

    public SearchTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }

    /**
     * @see org.springframework.social.salesforce.api.SearchOperations#search(java.lang.String)
     */
    @Override
    public List<ResultItem> search(String soslQuery) {
        requireAuthorization();
        URI uri = UriComponentsBuilder.fromUriString(api.getBaseUrl() + "/" + getVersion() + "/search").queryParam("q", soslQuery).build().toUri();

        if (uri == null || StringUtils.isBlank(uri.toString())) {
            throw new IllegalArgumentException("Query URI cannot be null or empty");
        }

        JsonNode arr = restTemplate.getForObject(uri, JsonNode.class);
        return api.readList(arr, ResultItem.class);
    }

}
