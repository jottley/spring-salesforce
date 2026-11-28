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
import java.util.List;
import java.util.Map;
import org.springframework.salesforce.api.Status;

/**
 * Jackson mixin that maps JSON properties onto {@link Status}.
 *
 * @author Umut Utkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusMixin {

  /**
   * Binds the constructor arguments of {@code Status} to JSON properties.
   *
   * @param text the JSON {@code text} property
   */
  @JsonCreator
  StatusMixin(@JsonProperty("text") String text) {}

  /** Binds the JSON property {@code messageSegments} to {@code Status.segments}. */
  @JsonProperty("messageSegments")
  List<Map<String, String>> segments;
}
