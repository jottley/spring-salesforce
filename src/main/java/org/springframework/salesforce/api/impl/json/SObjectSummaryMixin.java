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
import java.util.Map;
import org.springframework.salesforce.api.SObjectSummary;

/**
 * Jackson mixin that maps JSON properties onto {@link SObjectSummary}.
 *
 * @author Umut Utkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SObjectSummaryMixin {

  /** Creates the mixin. */
  public SObjectSummaryMixin() {}

  /** Binds the JSON property {@code name} to {@code SObjectSummary.name}. */
  @JsonProperty("name")
  String name;

  /** Binds the JSON property {@code label} to {@code SObjectSummary.label}. */
  @JsonProperty("label")
  String label;

  /** Binds the JSON property {@code updateable} to {@code SObjectSummary.updateable}. */
  @JsonProperty("updateable")
  boolean updateable;

  /** Binds the JSON property {@code keyPrefix} to {@code SObjectSummary.keyPrefix}. */
  @JsonProperty("keyPrefix")
  String keyPrefix;

  /** Binds the JSON property {@code custom} to {@code SObjectSummary.custom}. */
  @JsonProperty("custom")
  boolean custom;

  /** Binds the JSON property {@code urls} to {@code SObjectSummary.urls}. */
  @JsonProperty("urls")
  Map<String, String> urls;

  /** Binds the JSON property {@code searchable} to {@code SObjectSummary.searchable}. */
  @JsonProperty("searchable")
  boolean searchable;

  /** Binds the JSON property {@code labelPlural} to {@code SObjectSummary.labelPlural}. */
  @JsonProperty("labelPlural")
  String labelPlural;

  /** Binds the JSON property {@code layoutable} to {@code SObjectSummary.layoutable}. */
  @JsonProperty("layoutable")
  boolean layoutable;

  /** Binds the JSON property {@code activateable} to {@code SObjectSummary.activateable}. */
  @JsonProperty("activateable")
  boolean activateable;

  /** Binds the JSON property {@code createable} to {@code SObjectSummary.createable}. */
  @JsonProperty("createable")
  boolean createable;

  /**
   * Binds the JSON property {@code deprecatedAndHidden} to {@code
   * SObjectSummary.deprecatedAndHidden}.
   */
  @JsonProperty("deprecatedAndHidden")
  boolean deprecatedAndHidden;

  /** Binds the JSON property {@code customSetting} to {@code SObjectSummary.customSetting}. */
  @JsonProperty("customSetting")
  boolean customSetting;

  /** Binds the JSON property {@code deletable} to {@code SObjectSummary.deletable}. */
  @JsonProperty("deletable")
  boolean deletable;

  /** Binds the JSON property {@code feedEnabled} to {@code SObjectSummary.feedEnabled}. */
  @JsonProperty("feedEnabled")
  boolean feedEnabled;

  /** Binds the JSON property {@code mergeable} to {@code SObjectSummary.mergeable}. */
  @JsonProperty("mergeable")
  boolean mergeable;

  /** Binds the JSON property {@code queryable} to {@code SObjectSummary.queryable}. */
  @JsonProperty("queryable")
  boolean queryable;

  /** Binds the JSON property {@code replicateable} to {@code SObjectSummary.replicateable}. */
  @JsonProperty("replicateable")
  boolean replicateable;

  /** Binds the JSON property {@code retrieveable} to {@code SObjectSummary.retrieveable}. */
  @JsonProperty("retrieveable")
  boolean retrieveable;

  /** Binds the JSON property {@code undeletable} to {@code SObjectSummary.undeletable}. */
  @JsonProperty("undeletable")
  boolean undeletable;

  /** Binds the JSON property {@code triggerable} to {@code SObjectSummary.triggerable}. */
  @JsonProperty("triggerable")
  boolean triggerable;
}
