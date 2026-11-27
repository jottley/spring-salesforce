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

import java.util.List;

/**
 * Full describe result for an sObject, adding fields, child relationships and record type
 * information to the summary data of {@link SObjectSummary}.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SObjectDetail extends SObjectSummary {

    /** The fields of the sObject. */
    private List<Field> fields;

    /** The child relationships of the sObject. */
    private List<Relationship> childRelationships;

    /** Whether the sObject can be shown in list views, as reported by Salesforce. */
    private boolean listviewable;

    /** Whether the sObject supports lookup layouts, as reported by Salesforce. */
    private boolean lookupLayoutable;

    /** The record type information of the sObject. */
    private List<RecordTypeInfo> recordTypeInfos;

    /** Whether the sObject supports search layouts, as reported by Salesforce. */
    private boolean searchLayoutable;


    /**
     * Creates an empty sObject detail.
     */
    public SObjectDetail() {
        //NOOP
    }


    /**
     * Returns the fields of the sObject.
     *
     * @return the fields
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * Sets the fields of the sObject.
     *
     * @param fields the fields
     */
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    /**
     * Returns the child relationships of the sObject.
     *
     * @return the child relationships
     */
    public List<Relationship> getChildRelationships() {
        return childRelationships;
    }

    /**
     * Sets the child relationships of the sObject.
     *
     * @param childRelationships the child relationships
     */
    public void setChildRelationships(List<Relationship> childRelationships) {
        this.childRelationships = childRelationships;
    }

    /**
     * Returns whether the sObject can be shown in list views, as reported by Salesforce.
     *
     * @return {@code true} if the sObject is list viewable
     */
    public boolean isListviewable() {
        return listviewable;
    }

    /**
     * Sets whether the sObject can be shown in list views.
     *
     * @param listviewable the list-viewable flag
     */
    public void setListviewable(boolean listviewable) {
        this.listviewable = listviewable;
    }

    /**
     * Returns whether the sObject supports lookup layouts, as reported by Salesforce.
     *
     * @return {@code true} if the sObject is lookup layoutable
     */
    public boolean isLookupLayoutable() {
        return lookupLayoutable;
    }

    /**
     * Sets whether the sObject supports lookup layouts.
     *
     * @param lookupLayoutable the lookup-layoutable flag
     */
    public void setLookupLayoutable(boolean lookupLayoutable) {
        this.lookupLayoutable = lookupLayoutable;
    }

    /**
     * Returns the record type information of the sObject.
     *
     * @return the record type infos
     */
    public List<RecordTypeInfo> getRecordTypeInfos() {
        return recordTypeInfos;
    }

    /**
     * Sets the record type information of the sObject.
     *
     * @param recordTypeInfos the record type infos
     */
    public void setRecordTypeInfos(List<RecordTypeInfo> recordTypeInfos) {
        this.recordTypeInfos = recordTypeInfos;
    }

    /**
     * Returns whether the sObject supports search layouts, as reported by Salesforce.
     *
     * @return {@code true} if the sObject is search layoutable
     */
    public boolean isSearchLayoutable() {
        return searchLayoutable;
    }

    /**
     * Sets whether the sObject supports search layouts.
     *
     * @param searchLayoutable the search-layoutable flag
     */
    public void setSearchLayoutable(boolean searchLayoutable) {
        this.searchLayoutable = searchLayoutable;
    }

}
