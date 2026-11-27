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

import java.util.List;

import org.springframework.salesforce.api.QueryResult;
import org.springframework.salesforce.api.ResultItem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson mixin that maps JSON properties onto {@link QueryResult}.
 *
 * @author Umut Utkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryResultMixin {

    /**
     * Binds the constructor arguments of {@code QueryResult} to JSON properties.
     *
     * @param totalSize the JSON {@code totalSize} property
     * @param done the JSON {@code done} property
     */
    @JsonCreator
    QueryResultMixin(
            @JsonProperty("totalSize") int totalSize,
            @JsonProperty("done") boolean done) {
    }

    /** Binds the JSON property {@code nextRecordsUrl} to {@code QueryResult.nextRecordsUrl}. */
    @JsonProperty("nextRecordsUrl")
    String nextRecordsUrl;

    /** Binds the JSON property {@code records} to {@code QueryResult.records}. */
    @JsonProperty("records")
    List<ResultItem> records;

}
