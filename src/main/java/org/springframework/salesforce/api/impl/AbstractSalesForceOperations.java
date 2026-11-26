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

import org.springframework.salesforce.api.MissingAuthorizationException;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class AbstractSalesForceOperations<T> {

    protected T api;

    public AbstractSalesForceOperations(T api) {
        this.api = api;
    }

    protected void requireAuthorization() {
        // For now, we assume if we have an API instance, we're authorized
        // In a real implementation, you might want to check token validity
        if (api == null) {
            throw new MissingAuthorizationException("API instance is null - authorization required");
        }
    }

    public String getVersion()
    {
        return ((Salesforce)api).apiOperations().getVersion();
    }

    /**
     * Checks a value that is about to become one segment of a request path (an sObject name, a record id,
     * a field name, a user or community id).
     * <p>
     * The URI template expansion already percent-encodes characters such as {@code /} and {@code ?}, so a
     * value cannot break out of its segment that way. A segment that is exactly {@code .} or {@code ..} is
     * not encoded by anything, and the server would treat it as a dot-segment and resolve it to a different
     * resource, so those, and blank values, are rejected before any request is made.
     *
     * @param what what the value is, for the error message
     * @return the value, unchanged
     * @throws IllegalArgumentException if the value is null, blank, {@code .} or {@code ..}
     */
    protected static String requirePathSegment(String what, String value) {
        if (value == null || value.isBlank() || value.equals(".") || value.equals("..")) {
            throw new IllegalArgumentException("Invalid " + what + ": " + (value == null ? "null" : "\"" + value + "\""));
        }
        return value;
    }

    /**
     * Builds {@code url?name=value} so that the server reads the value back exactly as given.
     * <p>
     * The URI builder leaves {@code +} and {@code &} as they are. A server reads {@code +} in a query string
     * as a space, and {@code &} ends the parameter, so a SOQL string such as {@code 'a+b@x.com'} or
     * {@code 'AT&T'} would be silently changed or cut off. Those two are escaped here; everything the builder
     * already encodes, and the characters it deliberately leaves alone, stay as before.
     */
    protected static URI queryUri(String url, String name, String value) {
        URI built = UriComponentsBuilder.fromUriString(url).queryParam(name, value).build().toUri();
        String query = built.getRawQuery().replace("+", "%2B").replace("&", "%26");
        return URI.create(url + "?" + query);
    }

}
