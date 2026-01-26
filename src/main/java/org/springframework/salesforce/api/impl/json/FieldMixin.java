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

import java.util.List;

import org.springframework.salesforce.api.Field;
import org.springframework.salesforce.api.PickListEntry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link Field}
 *
 * @author Umut Utkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldMixin {

    @JsonCreator
    FieldMixin(
            @JsonProperty("name") String name,
            @JsonProperty("type") String type,
            @JsonProperty("label") String label) {
    }


    @JsonProperty("length")
    int length;

    @JsonProperty("defaultValue")
    String defaultValue;

    @JsonProperty("updateable")
    boolean updateable;

    @JsonProperty("calculated")
    boolean calculated;

    @JsonProperty("filterable")
    boolean filterable;

    @JsonProperty("sortable")
    boolean sortable;

    @JsonProperty("controllerName")
    String controllerName;

    @JsonProperty("unique")
    boolean unique;

    @JsonProperty("nillable")
    boolean nillable;

    @JsonProperty("precision")
    int precision;

    @JsonProperty("scale")
    int scale;

    @JsonProperty("caseSensitive")
    boolean caseSensitive;

    @JsonProperty("byteLength")
    int byteLength;

    @JsonProperty("nameField")
    boolean nameField;

    @JsonProperty("externalId")
    boolean externalId;

    @JsonProperty("idLookup")
    boolean idLookup;

    @JsonProperty("inlineHelpText")
    String inlineHelpText;

    @JsonProperty("createable")
    boolean createable;

    @JsonProperty("soapType")
    String soapType;

    @JsonProperty("autoNumber")
    boolean autoNumber;

    @JsonProperty("namePointing")
    boolean namePointing;

    @JsonProperty("custom")
    boolean custom;

    @JsonProperty("defaultedOnCreate")
    boolean defaultedOnCreate;

    @JsonProperty("deprecatedAndHidden")
    boolean deprecatedAndHidden;

    @JsonProperty("htmlFormatted")
    boolean htmlFormatted;

    @JsonProperty("defaultValueFormula")
    String defaultValueFormula;

    @JsonProperty("calculatedFormula")
    String calculatedFormula;

    @JsonProperty("restrictedPicklist")
    boolean restrictedPicklist;

    @JsonProperty("picklistValues")
    List<PickListEntry> picklistValues;

    @JsonProperty("dependentPicklist")
    boolean dependentPicklist;

    @JsonProperty("referenceTo")
    String[] referenceTo;

    @JsonProperty("relationshipName")
    String relationshipName;

    @JsonProperty("relationshipOrder")
    int relationshipOrder;

    @JsonProperty("writeRequiresMasterRead")
    boolean writeRequiresMasterRead;

    @JsonProperty("cascadeDelete")
    boolean cascadeDelete;

    @JsonProperty("restrictedDelete")
    boolean restrictedDelete;

    @JsonProperty("digits")
    int digits;

    @JsonProperty("groupable")
    boolean groupable;

}
