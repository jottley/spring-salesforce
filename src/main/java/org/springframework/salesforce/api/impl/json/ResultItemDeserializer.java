/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-social-salesforce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.salesforce.api.impl.json;

import java.io.IOException;
import java.util.Map;

import org.joor.Reflect;
import org.springframework.salesforce.api.QueryResult;
import org.springframework.salesforce.api.ResultItem;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Deserializer for {@link ResultItem}
 *
 * @author Umut Utkan
 */
public class ResultItemDeserializer extends JsonDeserializer<ResultItem>
{

    public static final String ATTRIBUTES = "attributes";
    public static final String TYPE = "type";
    public static final String URL = "url";
    public static final String RECORDS = "records";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ResultItem deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        Reflect.on(mapper).set("_deserializationConfig", ctxt.getConfig());
        jp.setCodec(mapper);

        JsonNode jsonNode = jp.readValueAsTree();
        Map<String, Object> map = mapper.convertValue(jsonNode, Map.class);
        ResultItem item = new ResultItem((String) ((Map) map.get(ATTRIBUTES)).get(TYPE),
                (String) ((Map) map.get(ATTRIBUTES)).get(URL));
        map.remove(ATTRIBUTES);

        for(Map.Entry<String, Object> entry : map.entrySet()) {
            if(entry.getValue() instanceof Map<?, ?> inner) {
                if(inner.get(RECORDS) != null) {
                    item.getAttributes().put(entry.getKey(), mapper.convertValue(jsonNode.get(entry.getKey()), QueryResult.class));
                } else {
                    item.getAttributes().put(entry.getKey(), mapper.convertValue(jsonNode.get(entry.getKey()), ResultItem.class));
                }
            } else {
                item.getAttributes().put(entry.getKey(), entry.getValue());
            }
        }

        return item;
    }

}
