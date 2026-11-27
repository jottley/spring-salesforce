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

/**
 * Record type information for an sObject, as reported in a Salesforce describe result.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class RecordTypeInfo {

    /** The record type name. */
    private final String name;

    /** Whether the record type is available to the current user. */
    private final boolean available;

    /** The record type ID. */
    private final String recordTypeId;

    /** Whether this is the default record type mapping for the current user. */
    private final boolean defaultRecordTypeMapping;


    /**
     * Creates record type information.
     *
     * @param name the record type name
     * @param available whether the record type is available to the current user
     * @param recordTypeId the record type ID
     * @param defaultRecordTypeMapping whether this is the default record type mapping for the current user
     */
    public RecordTypeInfo(String name, boolean available, String recordTypeId, boolean defaultRecordTypeMapping) {
        this.name = name;
        this.available = available;
        this.recordTypeId = recordTypeId;
        this.defaultRecordTypeMapping = defaultRecordTypeMapping;
    }


    /**
     * Returns the record type name.
     *
     * @return the record type name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the record type is available to the current user.
     *
     * @return {@code true} if the record type is available
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Returns the record type ID.
     *
     * @return the record type ID
     */
    public String getRecordTypeId() {
        return recordTypeId;
    }

    /**
     * Returns whether this is the default record type mapping for the current user.
     *
     * @return {@code true} if this is the default record type mapping
     */
    public boolean isDefaultRecordTypeMapping() {
        return defaultRecordTypeMapping;
    }

}
