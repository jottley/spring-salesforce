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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.salesforce.api.PickListEntry;

/**
 * Jackson mixin that maps JSON properties onto {@link PickListEntry}.
 *
 * @author Umut Utkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PickListEntryMixin {

  /**
   * Binds the constructor arguments of {@code PickListEntry} to JSON properties.
   *
   * @param value the JSON {@code value} property
   * @param label the JSON {@code label} property
   * @param active the JSON {@code active} property
   * @param defaultValue the JSON {@code defaultValue} property
   */
  @JsonCreator
  PickListEntryMixin(
      @JsonProperty("value") String value,
      @JsonProperty("label") String label,
      @JsonProperty("active") boolean active,
      @JsonProperty("defaultValue") boolean defaultValue) {}
}
