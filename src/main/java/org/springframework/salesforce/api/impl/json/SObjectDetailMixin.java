/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-social-salesforce
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.salesforce.api.Field;
import org.springframework.salesforce.api.RecordTypeInfo;
import org.springframework.salesforce.api.Relationship;
import org.springframework.salesforce.api.SObjectDetail;

/**
 * Jackson mixin that maps JSON properties onto {@link SObjectDetail} (the sObject describe result),
 * adding the detail properties to those of {@link SObjectSummaryMixin}.
 *
 * @author Umut Utkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SObjectDetailMixin extends SObjectSummaryMixin {

  /** Creates the mixin. */
  public SObjectDetailMixin() {}

  /** Binds the JSON property {@code fields} to {@code SObjectDetail.fields}. */
  @JsonProperty("fields")
  List<Field> fields;

  /**
   * Binds the JSON property {@code childRelationships} to {@code SObjectDetail.childRelationships}.
   */
  @JsonProperty("childRelationships")
  List<Relationship> childRelationships;

  /** Binds the JSON property {@code listviewable} to {@code SObjectDetail.listviewable}. */
  @JsonProperty("listviewable")
  boolean listviewable;

  /** Binds the JSON property {@code lookupLayoutable} to {@code SObjectDetail.lookupLayoutable}. */
  @JsonProperty("lookupLayoutable")
  boolean lookupLayoutable;

  /** Binds the JSON property {@code recordTypeInfos} to {@code SObjectDetail.recordTypeInfos}. */
  @JsonProperty("recordTypeInfos")
  List<RecordTypeInfo> recordTypeInfos;

  /** Binds the JSON property {@code searchLayoutable} to {@code SObjectDetail.searchLayoutable}. */
  @JsonProperty("searchLayoutable")
  boolean searchLayoutable;
}
