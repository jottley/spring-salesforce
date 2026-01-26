/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-social-salesforce
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * The core limits properties. Any additional limits for connected apps can call
 * be found in the additional properties
 *
 * @author Jared Ottley
 */
public class AbstractLimits implements Serializable {

    private static final long serialVersionUID = 6213553807904726408L;

    @JsonProperty("Max")
    private int max;

    @JsonProperty("Remaining")
    private int remaining;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * NOOP
     */
    public AbstractLimits() {

    }

    /**
     *
     * @param remaining
     * @param max
     */
    public AbstractLimits(int max, int remaining) {
        this.max = max;
        this.remaining = remaining;
    }

    @JsonProperty("Max")
    public int getMax() {
        return max;
    }

    @JsonProperty("Remaining")
    public int getRemaining() {
        return remaining;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}