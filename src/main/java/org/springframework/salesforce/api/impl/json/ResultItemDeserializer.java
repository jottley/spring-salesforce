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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.joor.Reflect;
import org.springframework.salesforce.api.QueryResult;
import org.springframework.salesforce.api.ResultItem;

/**
 * Deserializer for {@link ResultItem}. The {@code attributes} object supplies the type and URL;
 * every other property becomes an attribute of the item, with nested objects converted to a {@link
 * QueryResult} (when they contain {@code records}) or to another {@link ResultItem}.
 *
 * @author Umut Utkan
 */
public class ResultItemDeserializer extends JsonDeserializer<ResultItem> {

  /** Creates a deserializer for {@code ResultItem} values. */
  public ResultItemDeserializer() {
    // NOOP
  }

  /** Name of the JSON property that holds the record type and URL. */
  public static final String ATTRIBUTES = "attributes";

  /** Name of the property within {@code attributes} that holds the record type. */
  public static final String TYPE = "type";

  /** Name of the property within {@code attributes} that holds the record URL. */
  public static final String URL = "url";

  /** Name of the property that marks a nested object as a query result. */
  public static final String RECORDS = "records";

  /**
   * Reads a {@link ResultItem} from the JSON tree.
   *
   * <p>Each nested object becomes a {@link org.springframework.salesforce.api.QueryResult} if it
   * has a {@code records} entry (a child relationship), a {@link ResultItem} if it has an {@code
   * attributes} entry (a parent relationship), and otherwise stays a plain map (a compound field
   * such as {@code BillingAddress}).
   *
   * @param jp the parser
   * @param ctxt the deserialization context, whose configuration is reused for nested conversions
   * @return the item
   * @throws IOException if the JSON cannot be read
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public ResultItem deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Reflect.on(mapper).set("_deserializationConfig", ctxt.getConfig());
    jp.setCodec(mapper);

    JsonNode jsonNode = jp.readValueAsTree();
    Map<String, Object> map = mapper.convertValue(jsonNode, Map.class);
    // A record always carries "attributes"; tolerate a value without it rather than failing the
    // whole response
    Map<String, Object> recordAttributes =
        map.get(ATTRIBUTES) instanceof Map<?, ?> found ? (Map<String, Object>) found : Map.of();
    ResultItem item =
        new ResultItem((String) recordAttributes.get(TYPE), (String) recordAttributes.get(URL));
    map.remove(ATTRIBUTES);

    for (Map.Entry<String, Object> entry : map.entrySet()) {
      if (entry.getValue() instanceof Map<?, ?> inner) {
        if (inner.get(RECORDS) != null) {
          item.getAttributes()
              .put(
                  entry.getKey(),
                  mapper.convertValue(jsonNode.get(entry.getKey()), QueryResult.class));
        } else if (inner.containsKey(ATTRIBUTES)) {
          item.getAttributes()
              .put(
                  entry.getKey(),
                  mapper.convertValue(jsonNode.get(entry.getKey()), ResultItem.class));
        } else {
          // A compound field such as BillingAddress or a geolocation: a nested object that is not a
          // record
          item.getAttributes().put(entry.getKey(), entry.getValue());
        }
      } else {
        item.getAttributes().put(entry.getKey(), entry.getValue());
      }
    }

    return item;
  }
}
