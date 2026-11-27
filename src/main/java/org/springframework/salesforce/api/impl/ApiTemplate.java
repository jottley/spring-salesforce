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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.salesforce.api.ApiOperations;
import org.springframework.salesforce.api.ApiVersion;
import org.springframework.salesforce.api.InvalidSalesforceApiVersionException;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Default implementation of {@link ApiOperations}. It holds the API version in use and validates it:
 * a version must be {@code v}, a one to three digit major number, a dot and a single digit (for example
 * {@code v68.0}), and its major number must not be below
 * {@link ApiOperations#MINIMUM_API_VERSION}.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class ApiTemplate extends AbstractSalesForceOperations<Salesforce> implements ApiOperations {

    /** The shared rest template used for requests. */
    private final RestTemplate restTemplate;
    /** The configured API version, or {@code null} to use the default. */
    private String version;


    // Anchored so surrounding text is rejected, and capped at three digits so the major version always fits an int
    /** Matches a version such as {@code v68.0} and captures the major number. */
    private static final Pattern VERSION_PATTERN = Pattern.compile("^v(\\d{1,3})\\.\\d$");


    /**
     * Creates the operations instance.
     *
     * @param api the {@link Salesforce} instance this operations object belongs to
     * @param restTemplate the shared rest template used for requests
     */
    public ApiTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }


    /**
     * Retrieves the API versions the instance supports.
     *
     * @return the available versions
     */
    @Override
    public List<ApiVersion> getVersions() {
        URI uri = UriComponentsBuilder.fromUriString(api.getBaseUrl()).build().toUri();

        JsonNode dataNode = restTemplate.getForObject(uri, JsonNode.class);
        return api.readList(dataNode, ApiVersion.class);
    }

    /**
     * Retrieves the resources available for an API version.
     *
     * @param version the API version, for example {@code v68.0}
     * @return a map of resource name to URL
     * @throws InvalidSalesforceApiVersionException if the version is null, blank, malformed or below the minimum
     * @throws org.springframework.salesforce.api.MissingAuthorizationException if the API instance is {@code null}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getServices(String version) {
        requireAuthorization();
        validateVersion(version);
        return restTemplate.getForObject(api.getBaseUrl() + "/{version}", Map.class, version);
    }

    /**
     * Retrieves the resources available for the current API version.
     *
     * @return a map of resource name to URL
     */
    @Override
    public Map<String, String> getServices() {
        requireAuthorization();
        return this.getServices(getVersion());
    }

    /**
     * Sets the API version to use.
     *
     * @param version the API version, for example {@code v68.0}
     * @throws InvalidSalesforceApiVersionException if the version is null, blank, malformed or below the minimum
     */
    @Override
    public void setVersion(String version)
            throws InvalidSalesforceApiVersionException
    {
        validateVersion(version);
        this.version = version;
    }


    /**
     * Returns the configured API version.
     *
     * @return the configured version, or {@link ApiOperations#DEFAULT_API_VERSION} if none is set
     */
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


    /**
     * Rejects anything that is not a well-formed version such as {@code v68.0}, and anything older than
     * {@link ApiOperations#MINIMUM_API_VERSION}.
     *
     * @param version the version to check
     * @throws InvalidSalesforceApiVersionException if the version is invalid
     */
    private void validateVersion(String version) throws InvalidSalesforceApiVersionException
    {
        Matcher matcher = StringUtils.isBlank(version) ? null : VERSION_PATTERN.matcher(version);
        if (matcher == null || !matcher.matches())
        {
            throw new InvalidSalesforceApiVersionException(version);
        }
        if (Integer.parseInt(matcher.group(1)) < MINIMUM_API_VERSION)
        {
            throw new InvalidSalesforceApiVersionException(version, MINIMUM_API_VERSION);
        }
    }

}
