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
import org.springframework.salesforce.api.RecordTypeInfo;

/**
 * Jackson mixin that maps JSON properties onto {@link RecordTypeInfo}.
 *
 * @author Umut Utkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordTypeInfoMixin {

  /**
   * Binds the constructor arguments of {@code RecordTypeInfo} to JSON properties.
   *
   * @param name the JSON {@code name} property
   * @param available the JSON {@code available} property
   * @param recordTypeId the JSON {@code recordTypeId} property
   * @param defaultRecordTypeMapping the JSON {@code defaultRecordTypeMapping} property
   */
  @JsonCreator
  RecordTypeInfoMixin(
      @JsonProperty("name") String name,
      @JsonProperty("available") boolean available,
      @JsonProperty("recordTypeId") String recordTypeId,
      @JsonProperty("defaultRecordTypeMapping") boolean defaultRecordTypeMapping) {}
}
