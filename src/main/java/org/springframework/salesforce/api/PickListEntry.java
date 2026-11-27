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
package org.springframework.salesforce.api;

/**
 * A single entry of a picklist field, as reported in a Salesforce describe result.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class PickListEntry {

  /** The API value of the entry. */
  private final String value;

  /** Whether the entry is active. */
  private final boolean active;

  /** The display label of the entry. */
  private final String label;

  /** Whether the entry is the default selection. */
  private final boolean defaultValue;

  /**
   * Creates a picklist entry.
   *
   * @param value the API value of the entry
   * @param label the display label of the entry
   * @param active whether the entry is active
   * @param defaultValue whether the entry is the default selection
   */
  public PickListEntry(String value, String label, boolean active, boolean defaultValue) {
    this.value = value;
    this.active = active;
    this.label = label;
    this.defaultValue = defaultValue;
  }

  /**
   * Returns the API value of the entry.
   *
   * @return the entry value
   */
  public String getValue() {
    return value;
  }

  /**
   * Returns whether the entry is active.
   *
   * @return {@code true} if the entry is active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Returns the display label of the entry.
   *
   * @return the entry label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Returns whether the entry is the default selection.
   *
   * @return {@code true} if the entry is the default value
   */
  public boolean isDefaultValue() {
    return defaultValue;
  }
}
