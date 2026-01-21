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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.salesforce.api.SObjectDetail;
import org.springframework.social.salesforce.api.SObjectOperations;
import org.springframework.social.salesforce.api.SObjectSummary;
import org.springframework.social.salesforce.api.Salesforce;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Default implementation of SObjectOperations.
 *
 * @author Umut Utkan
 * @author Jared ottley
 */
public class SObjectsTemplate extends AbstractSalesForceOperations<Salesforce> implements SObjectOperations {

    private final RestTemplate restTemplate;

    public SObjectsTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Map> getSObjects() {
        requireAuthorization();
        JsonNode dataNode = restTemplate.getForObject(api.getBaseUrl() + "/{version}/sobjects", JsonNode.class, getVersion());
        return api.readList(dataNode.get("sobjects"), Map.class);
    }

    @Override
    public SObjectSummary getSObjectSummary(String name) {
        requireAuthorization();
        JsonNode node = restTemplate.getForObject(api.getBaseUrl() + "/{version}/sobjects/{name}", JsonNode.class, getVersion(), name);
        return api.readObject(node.get("objectDescribe"), SObjectSummary.class);
    }

    @Override
    public SObjectDetail describeSObject(String name) {
        requireAuthorization();
        return restTemplate.getForObject(api.getBaseUrl() + "/{version}/sobjects/{name}/describe", SObjectDetail.class, getVersion(), name);
    }

    @Override
    public Map getRow(String name, String id, String... fields) {
        requireAuthorization();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(api.getBaseUrl() + "/" + getVersion() + "/sobjects/" + name + "/" + id);
        if (fields.length > 0) {
            builder.queryParam("fields", StringUtils.arrayToCommaDelimitedString(fields));
        }
        return restTemplate.getForObject(builder.build().toUri(), Map.class);
    }

    @Override
    public InputStream getBlob(String name, String id, String field) {
        requireAuthorization();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(
                    api.getBaseUrl() + "/{version}/sobjects/{name}/{id}/{field}", HttpMethod.GET, entity, byte[].class,
                    getVersion(), name, id, field);
        return new ByteArrayInputStream(response.getBody());

    }

    @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Object> create(String name, Map<String, Object> fields) {
        requireAuthorization();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map> entity = new HttpEntity<>(fields, headers);
        return restTemplate.postForObject(api.getBaseUrl() + "/{version}/sobjects/{name}", entity, Map.class, getVersion(), name);
    }

    @Override
    public Map<String, Object> update(String sObjectName, String sObjectId, Map<String, Object> fields) {
        requireAuthorization();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(fields, headers);
        Map<String, Object> result =  restTemplate.postForObject(api.getBaseUrl() + "/{version}/sobjects/{sObjectName}/{sObjectId}?_HttpMethod=PATCH",
                entity, Map.class, getVersion(), sObjectName, sObjectId);
        // SF returns an empty body on success, so mimic the same update you'd get from a create success for consistency
        if (result == null) {
            result = new HashMap<>();
            result.put("id", sObjectId);
            result.put("success", true);
            result.put("errors", new ArrayList<String>());
        }
        return result;
    }


    @Override
    public void delete(String sObjectName, String sObjectId)
    {
        requireAuthorization();
        restTemplate.delete(api.getBaseUrl() + "/{version}/sobjects/{sObjectName}/{sObjectId}", getVersion(), sObjectName, sObjectId);
    }


}
