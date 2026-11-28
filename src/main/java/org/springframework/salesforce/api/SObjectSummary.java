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
package org.springframework.salesforce.api;

import java.util.Map;

/**
 * Summary describe information for an sObject, as reported by Salesforce. Most properties are
 * passed through as reported by Salesforce.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SObjectSummary {

  /** The name of the sObject. */
  private String name;

  /** The display label of the sObject. */
  private String label;

  /** Whether the sObject can be updated. */
  private boolean updateable;

  /** The three-character ID key prefix of the sObject. */
  private String keyPrefix;

  /** Whether the sObject is a custom sObject. */
  private boolean custom;

  /** The map of related REST resource URLs for the sObject. */
  private Map<String, String> urls;

  /** Whether the sObject can be searched with SOSL. */
  private boolean searchable;

  /** The plural display label of the sObject. */
  private String labelPlural;

  /** Whether the sObject supports page layouts. */
  private boolean layoutable;

  /** The {@code activateable} flag, which Salesforce documents as reserved for future use. */
  private boolean activateable;

  /** Whether the sObject can be created. */
  private boolean createable;

  /**
   * The {@code deprecatedAndHidden} flag, which Salesforce documents as reserved for future use.
   */
  private boolean deprecatedAndHidden;

  /** Whether the sObject is a custom setting. */
  private boolean customSetting;

  /** Whether records of the sObject can be deleted. */
  private boolean deletable;

  /** Whether Chatter feeds are enabled for the sObject. */
  private boolean feedEnabled;

  /** Whether records of the sObject can be merged. */
  private boolean mergeable;

  /** Whether the sObject can be queried with SOQL. */
  private boolean queryable;

  /** Whether the sObject can be replicated (supports the getUpdated and getDeleted calls). */
  private boolean replicateable;

  /** Whether records of the sObject can be retrieved by ID. */
  private boolean retrieveable;

  /** Whether records of the sObject can be undeleted from the Recycle Bin. */
  private boolean undeletable;

  /** Whether Apex triggers are supported for the sObject. */
  private boolean triggerable;

  /** Creates an empty sObject summary. */
  public SObjectSummary() {
    // NOOP
  }

  /**
   * Returns the name of the sObject.
   *
   * @return the name of the sObject
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the sObject.
   *
   * @param name the name of the sObject
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the display label of the sObject.
   *
   * @return the display label of the sObject
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the display label of the sObject.
   *
   * @param label the display label of the sObject
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Returns whether the sObject can be updated.
   *
   * @return {@code true} if the sObject can be updated
   */
  public boolean isUpdateable() {
    return updateable;
  }

  /**
   * Sets whether the sObject can be updated.
   *
   * @param updateable {@code true} if the sObject can be updated
   */
  public void setUpdateable(boolean updateable) {
    this.updateable = updateable;
  }

  /**
   * Returns the three-character ID key prefix of the sObject.
   *
   * @return the three-character ID key prefix of the sObject
   */
  public String getKeyPrefix() {
    return keyPrefix;
  }

  /**
   * Sets the three-character ID key prefix of the sObject.
   *
   * @param keyPrefix the three-character ID key prefix of the sObject
   */
  public void setKeyPrefix(String keyPrefix) {
    this.keyPrefix = keyPrefix;
  }

  /**
   * Returns whether the sObject is a custom sObject.
   *
   * @return {@code true} if the sObject is a custom sObject
   */
  public boolean isCustom() {
    return custom;
  }

  /**
   * Sets whether the sObject is a custom sObject.
   *
   * @param custom {@code true} if the sObject is a custom sObject
   */
  public void setCustom(boolean custom) {
    this.custom = custom;
  }

  /**
   * Returns the map of related REST resource URLs for the sObject.
   *
   * @return the map of related REST resource URLs for the sObject
   */
  public Map<String, String> getUrls() {
    return urls;
  }

  /**
   * Sets the map of related REST resource URLs for the sObject.
   *
   * @param urls the map of related REST resource URLs for the sObject
   */
  public void setUrls(Map<String, String> urls) {
    this.urls = urls;
  }

  /**
   * Returns whether the sObject can be searched with SOSL.
   *
   * @return {@code true} if the sObject can be searched with SOSL
   */
  public boolean isSearchable() {
    return searchable;
  }

  /**
   * Sets whether the sObject can be searched with SOSL.
   *
   * @param searchable {@code true} if the sObject can be searched with SOSL
   */
  public void setSearchable(boolean searchable) {
    this.searchable = searchable;
  }

  /**
   * Returns the plural display label of the sObject.
   *
   * @return the plural display label of the sObject
   */
  public String getLabelPlural() {
    return labelPlural;
  }

  /**
   * Sets the plural display label of the sObject.
   *
   * @param labelPlural the plural display label of the sObject
   */
  public void setLabelPlural(String labelPlural) {
    this.labelPlural = labelPlural;
  }

  /**
   * Returns whether the sObject supports page layouts.
   *
   * @return {@code true} if the sObject supports page layouts
   */
  public boolean isLayoutable() {
    return layoutable;
  }

  /**
   * Sets whether the sObject supports page layouts.
   *
   * @param layoutable {@code true} if the sObject supports page layouts
   */
  public void setLayoutable(boolean layoutable) {
    this.layoutable = layoutable;
  }

  /**
   * Returns the {@code activateable} flag, which Salesforce documents as reserved for future use.
   *
   * @return the flag, which Salesforce documents as reserved for future use
   */
  public boolean isActivateable() {
    return activateable;
  }

  /**
   * Sets the {@code activateable} flag (reserved for future use by Salesforce).
   *
   * @param activateable the flag (reserved for future use by Salesforce)
   */
  public void setActivateable(boolean activateable) {
    this.activateable = activateable;
  }

  /**
   * Returns whether the sObject can be created.
   *
   * @return {@code true} if the sObject can be created
   */
  public boolean isCreateable() {
    return createable;
  }

  /**
   * Sets whether the sObject can be created.
   *
   * @param createable {@code true} if the sObject can be created
   */
  public void setCreateable(boolean createable) {
    this.createable = createable;
  }

  /**
   * Returns the {@code deprecatedAndHidden} flag, which Salesforce documents as reserved for future
   * use.
   *
   * @return the flag, which Salesforce documents as reserved for future use
   */
  public boolean isDeprecatedAndHidden() {
    return deprecatedAndHidden;
  }

  /**
   * Sets the {@code deprecatedAndHidden} flag (reserved for future use by Salesforce).
   *
   * @param deprecatedAndHidden the flag (reserved for future use by Salesforce)
   */
  public void setDeprecatedAndHidden(boolean deprecatedAndHidden) {
    this.deprecatedAndHidden = deprecatedAndHidden;
  }

  /**
   * Returns whether the sObject is a custom setting.
   *
   * @return {@code true} if the sObject is a custom setting
   */
  public boolean isCustomSetting() {
    return customSetting;
  }

  /**
   * Sets whether the sObject is a custom setting.
   *
   * @param customSetting {@code true} if the sObject is a custom setting
   */
  public void setCustomSetting(boolean customSetting) {
    this.customSetting = customSetting;
  }

  /**
   * Returns whether records of the sObject can be deleted.
   *
   * @return {@code true} if records of the sObject can be deleted
   */
  public boolean isDeletable() {
    return deletable;
  }

  /**
   * Sets whether records of the sObject can be deleted.
   *
   * @param deletable {@code true} if records of the sObject can be deleted
   */
  public void setDeletable(boolean deletable) {
    this.deletable = deletable;
  }

  /**
   * Returns whether Chatter feeds are enabled for the sObject.
   *
   * @return {@code true} if Chatter feeds are enabled for the sObject
   */
  public boolean isFeedEnabled() {
    return feedEnabled;
  }

  /**
   * Sets whether Chatter feeds are enabled for the sObject.
   *
   * @param feedEnabled {@code true} if Chatter feeds are enabled for the sObject
   */
  public void setFeedEnabled(boolean feedEnabled) {
    this.feedEnabled = feedEnabled;
  }

  /**
   * Returns whether records of the sObject can be merged.
   *
   * @return {@code true} if records of the sObject can be merged
   */
  public boolean isMergeable() {
    return mergeable;
  }

  /**
   * Sets whether records of the sObject can be merged.
   *
   * @param mergeable {@code true} if records of the sObject can be merged
   */
  public void setMergeable(boolean mergeable) {
    this.mergeable = mergeable;
  }

  /**
   * Returns whether the sObject can be queried with SOQL.
   *
   * @return {@code true} if the sObject can be queried with SOQL
   */
  public boolean isQueryable() {
    return queryable;
  }

  /**
   * Sets whether the sObject can be queried with SOQL.
   *
   * @param queryable {@code true} if the sObject can be queried with SOQL
   */
  public void setQueryable(boolean queryable) {
    this.queryable = queryable;
  }

  /**
   * Returns whether the sObject can be replicated (supports the getUpdated and getDeleted calls).
   *
   * @return {@code true} if the sObject can be replicated (supports the getUpdated and getDeleted
   *     calls)
   */
  public boolean isReplicateable() {
    return replicateable;
  }

  /**
   * Sets whether the sObject can be replicated (supports the getUpdated and getDeleted calls).
   *
   * @param replicateable {@code true} if the sObject can be replicated (supports the getUpdated and
   *     getDeleted calls)
   */
  public void setReplicateable(boolean replicateable) {
    this.replicateable = replicateable;
  }

  /**
   * Returns whether records of the sObject can be retrieved by ID.
   *
   * @return {@code true} if records of the sObject can be retrieved by ID
   */
  public boolean isRetrieveable() {
    return retrieveable;
  }

  /**
   * Sets whether records of the sObject can be retrieved by ID.
   *
   * @param retrieveable {@code true} if records of the sObject can be retrieved by ID
   */
  public void setRetrieveable(boolean retrieveable) {
    this.retrieveable = retrieveable;
  }

  /**
   * Returns whether records of the sObject can be undeleted from the Recycle Bin.
   *
   * @return {@code true} if records of the sObject can be undeleted from the Recycle Bin
   */
  public boolean isUndeletable() {
    return undeletable;
  }

  /**
   * Sets whether records of the sObject can be undeleted from the Recycle Bin.
   *
   * @param undeletable {@code true} if records of the sObject can be undeleted from the Recycle Bin
   */
  public void setUndeletable(boolean undeletable) {
    this.undeletable = undeletable;
  }

  /**
   * Returns whether Apex triggers are supported for the sObject.
   *
   * @return {@code true} if Apex triggers are supported for the sObject
   */
  public boolean isTriggerable() {
    return triggerable;
  }

  /**
   * Sets whether Apex triggers are supported for the sObject.
   *
   * @param triggerable {@code true} if Apex triggers are supported for the sObject
   */
  public void setTriggerable(boolean triggerable) {
    this.triggerable = triggerable;
  }
}
