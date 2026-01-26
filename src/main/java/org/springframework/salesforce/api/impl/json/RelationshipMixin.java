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


import org.springframework.salesforce.api.Relationship;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * {@link Relationship}
 *
 * @author Umut Utkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationshipMixin {

    @JsonCreator
    RelationshipMixin(
            @JsonProperty("field") String field,
            @JsonProperty("relationshipName") String relationshipName,
            @JsonProperty("childObject") String childObject) {
    }

    @JsonProperty("deprecatedAndHidden")
    boolean deprecatedAndHidden;

    @JsonProperty("cascadeDelete")
    boolean cascadeDelete;

    @JsonProperty("restrictedDelete")
    boolean restrictedDelete;

}
