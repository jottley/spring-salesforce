/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-salesforce
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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The core limits properties: the maximum allowed and the amount remaining. Any other properties
 * Salesforce sends for the limit (for example, connected app limits) can be found in the additional
 * properties.
 *
 * @author Jared Ottley
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbstractLimits implements Serializable {

  /** Serialization version identifier. */
  private static final long serialVersionUID = 6213553807904726408L;

  /** The maximum amount allowed. */
  @JsonProperty("Max")
  private int max;

  /** The amount remaining. */
  @JsonProperty("Remaining")
  private int remaining;

  /** Properties other than {@code Max} and {@code Remaining}, keyed by name. */
  @JsonIgnore private final Map<String, Object> additionalProperties = new HashMap<>();

  /** Creates an empty instance; {@code max} and {@code remaining} are zero. */
  public AbstractLimits() {}

  /**
   * Creates a limit with the given values.
   *
   * @param max the maximum amount allowed
   * @param remaining the amount remaining
   */
  public AbstractLimits(int max, int remaining) {
    this.max = max;
    this.remaining = remaining;
  }

  /**
   * Returns the maximum amount allowed.
   *
   * @return the maximum amount allowed
   */
  @JsonProperty("Max")
  public int getMax() {
    return max;
  }

  /**
   * Returns the amount remaining.
   *
   * @return the amount remaining
   */
  @JsonProperty("Remaining")
  public int getRemaining() {
    return remaining;
  }

  /**
   * Returns any properties other than {@code Max} and {@code Remaining} that were received. The
   * returned map is the live internal map, not a copy.
   *
   * @return the additional properties, keyed by name
   */
  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  /**
   * Stores a property other than {@code Max} and {@code Remaining}.
   *
   * @param name the property name
   * @param value the property value
   */
  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
