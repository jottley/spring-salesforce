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
package org.springframework.salesforce.api.impl.json;


import java.util.Map;

import org.springframework.salesforce.api.SObjectSummary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link SObjectSummary}
 *
 * @author Umut Utkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SObjectSummaryMixin {

    @JsonProperty("name")
    String name;

    @JsonProperty("label")
    String label;

    @JsonProperty("updateable")
    boolean updateable;

    @JsonProperty("keyPrefix")
    String keyPrefix;

    @JsonProperty("custom")
    boolean custom;

    @JsonProperty("urls")
    Map<String, String> urls;

    @JsonProperty("searchable")
    boolean searchable;

    @JsonProperty("labelPlural")
    String labelPlural;

    @JsonProperty("layaoutable")
    boolean layoutable;

    @JsonProperty("activateable")
    boolean activateable;

    @JsonProperty("createable")
    boolean createable;

    @JsonProperty("deprecatedAndHidded")
    boolean deprecatedAndHidden;

    @JsonProperty("customSetting")
    boolean customSetting;

    @JsonProperty("deletable")
    boolean deletable;

    @JsonProperty("feedEnabled")
    boolean feedEnabled;

    @JsonProperty("mergeable")
    boolean mergeable;

    @JsonProperty("queryable")
    boolean queryable;

    @JsonProperty("replicateable")
    boolean replicateable;

    @JsonProperty("retriveable")
    boolean retrieveable;

    @JsonProperty("undeletable")
    boolean undeletable;

    @JsonProperty("triggerable")
    boolean triggerable;


}
