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
package org.springframework.salesforce.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A single record in a query, search or recent-items result.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class ResultItem implements Serializable {

    /** The sObject type. */
    private String type;

    /** The URL of the record. */
    private String url;

    /** The record's field values; parent relationships are {@link ResultItem}s and child relationships are {@link QueryResult}s. */
    private Map attributes;


    /**
     * Creates a result item with empty attributes.
     *
     * @param type the sObject type, for example {@code Account}
     * @param url the URL of the record
     */
    public ResultItem(String type, String url) {
        this.type = type;
        this.url = url;
        this.attributes = new HashMap<>();
    }


    /**
     * Returns the sObject type.
     *
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets the sObject type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the URL of the record.
     *
     * @return the URL
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Sets the URL of the record.
     *
     * @param url the URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the record's field values. Parent relationships are {@link ResultItem}s and child
     * relationships are {@link QueryResult}s.
     *
     * @return the attributes keyed by field name
     */
    public Map getAttributes() {
        return attributes;
    }

    /**
     * Sets the record's field values.
     *
     * @param attributes the attributes keyed by field name
     */
    public void setAttributes(Map attributes) {
        this.attributes = attributes;
    }

}
