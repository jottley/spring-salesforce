/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-salesforce
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.salesforce.api.impl.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.salesforce.api.Relationship;

/**
 * Jackson mixin that maps JSON properties onto {@link Relationship}.
 *
 * @author Umut Utkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationshipMixin {

  /**
   * Binds the constructor arguments of {@code Relationship} to JSON properties. Salesforce sends
   * the child sObject name as {@code childSObject}.
   *
   * @param field the JSON {@code field} property
   * @param relationshipName the JSON {@code relationshipName} property
   * @param childSObject the JSON {@code childSObject} property
   */
  @JsonCreator
  RelationshipMixin(
      @JsonProperty("field") String field,
      @JsonProperty("relationshipName") String relationshipName,
      @JsonProperty("childSObject") String childSObject) {}

  /**
   * Binds the JSON property {@code deprecatedAndHidden} to {@code
   * Relationship.deprecatedAndHidden}.
   */
  @JsonProperty("deprecatedAndHidden")
  boolean deprecatedAndHidden;

  /** Binds the JSON property {@code cascadeDelete} to {@code Relationship.cascadeDelete}. */
  @JsonProperty("cascadeDelete")
  boolean cascadeDelete;

  /** Binds the JSON property {@code restrictedDelete} to {@code Relationship.restrictedDelete}. */
  @JsonProperty("restrictedDelete")
  boolean restrictedDelete;
}
