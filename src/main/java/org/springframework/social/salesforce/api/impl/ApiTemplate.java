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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.social.salesforce.api.ApiOperations;
import org.springframework.social.salesforce.api.ApiVersion;
import org.springframework.social.salesforce.api.InvalidSalesforceApiVersionException;
import org.springframework.social.salesforce.api.Salesforce;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Default implementation of ApiOperations.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class ApiTemplate extends AbstractSalesForceOperations<Salesforce> implements ApiOperations {

    private final RestTemplate restTemplate;
    private String version;


    String versionRegEx = "v[0-9][0-9]\\.[0-9]";


    public ApiTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }


    @Override
    public List<ApiVersion> getVersions() {
        URI uri = UriComponentsBuilder.fromUriString(api.getBaseUrl()).build().toUri();

        //Ensure URI is not null before making the call
        if (uri == null) {
            throw new IllegalStateException("Salesforce instance URL is null. Ensure that the connection is properly authorized.");
        }

        JsonNode dataNode = restTemplate.getForObject(uri, JsonNode.class);
        return api.readList(dataNode, ApiVersion.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getServices(String version) {
        requireAuthorization();
        return restTemplate.getForObject(api.getBaseUrl() + "/{version}", Map.class, version);
    }

    @Override
    public Map<String, String> getServices() {
        requireAuthorization();
        return this.getServices(getVersion());
    }

    @Override
    public void setVersion(String version)
            throws InvalidSalesforceApiVersionException
    {
        if (StringUtils.isNotBlank(version) && validateVersionString(version))
        {
            this.version = version;
        }
        else
        {
            throw new InvalidSalesforceApiVersionException(version);
        }
    }


    @Override
    public String getVersion()
    {
        if (StringUtils.isNotBlank(version))
        {
            return version;
        }
        else
        {
            return DEFAULT_API_VERSION;
        }
    }


    private boolean validateVersionString(String version)
    {
        if (StringUtils.isNotBlank(version))
        {
            Pattern pattern = Pattern.compile(versionRegEx);
            Matcher matcher = pattern.matcher(version);

            return matcher.find();
        }

        return false;
    }

}
