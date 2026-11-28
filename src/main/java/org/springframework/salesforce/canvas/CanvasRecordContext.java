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
package org.springframework.salesforce.canvas;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Describes the record the canvas app is shown on.
 *
 * <p>Salesforce sends the record flat: {@code Id}, {@code attributes}, and then one entry per field
 * of the record ({@code Name}, {@code Phone}, and so on). Every property other than {@code Id} and
 * {@code attributes} is collected into {@link #fields()}, whatever its name or type, so custom
 * fields are kept too.
 *
 * @param Id the ID of the record
 * @param attributes the attributes of the record
 * @param fields every other property of the record, in no particular order; never null. Values can
 *     be null, strings, numbers, booleans, lists or maps, as parsed from the JSON.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CanvasRecordContext(
    String Id,
    Attributes attributes,
    @JsonProperty("__fields__") @JsonAnySetter Map<String, Object> fields) {

  /**
   * Copies the fields map, using an empty one when null. {@link Map#copyOf} is not used because it
   * rejects null values, and a record field that is empty in Salesforce arrives as JSON null.
   */
  public CanvasRecordContext {
    fields = (fields == null) ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(fields));
  }

  /**
   * Returns the record's fields. Annotated as the any-getter so the record serializes flat, the way
   * Salesforce sends it, and a read-then-write round trip keeps every field.
   *
   * @return the fields; never null
   */
  @JsonAnyGetter
  public Map<String, Object> fields() {
    return fields;
  }

  /**
   * Describes the attributes of the current record.
   *
   * @param url the URL of the associated object
   * @param type the object the canvas app is associated with
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Attributes(String url, String type) {}
}
