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
 * A child relationship of an sObject, as reported in a Salesforce describe result.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class Relationship {

    /** The field on the child sObject that references the parent. */
    private final String field;

    /** The {@code deprecatedAndHidden} flag, which Salesforce documents as reserved for future use. */
    private boolean deprecatedAndHidden;

    /** The name of the relationship. */
    private final String relationshipName;

    /** Whether deleting the parent also deletes the child records, as reported by Salesforce. */
    private boolean cascadeDelete;

    /** Whether the parent cannot be deleted while child records exist, as reported by Salesforce. */
    private boolean restrictedDelete;

    /** The name of the child sObject. */
    private final String childSObject;


    /**
     * Creates a child relationship.
     *
     * @param field the field on the child sObject that references the parent
     * @param relationshipName the name of the relationship
     * @param childObject the name of the child sObject
     */
    public Relationship(String field, String relationshipName, String childObject) {
        this.field = field;
        this.relationshipName = relationshipName;
        this.childSObject = childObject;
    }


    /**
     * Returns the field on the child sObject that references the parent.
     *
     * @return the field name
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the name of the relationship.
     *
     * @return the relationship name
     */
    public String getRelationshipName() {
        return relationshipName;
    }

    /**
     * Returns the name of the child sObject.
     *
     * @return the child sObject name
     */
    public String getChildSObject() {
        return childSObject;
    }

    /**
     * Returns the {@code deprecatedAndHidden} flag, which Salesforce documents as reserved for future use.
     *
     * @return the flag, which Salesforce documents as reserved for future use
     */
    public boolean isDeprecatedAndHidden() {
        return deprecatedAndHidden;
    }

    /**
     * Sets the {@code deprecatedAndHidden} flag (reserved for future use by Salesforce).
     *
     * @param deprecatedAndHidden the deprecated-and-hidden flag
     */
    public void setDeprecatedAndHidden(boolean deprecatedAndHidden) {
        this.deprecatedAndHidden = deprecatedAndHidden;
    }

    /**
     * Returns whether deleting the parent also deletes the child records, as reported by Salesforce.
     *
     * @return {@code true} if deletes cascade to the child records
     */
    public boolean isCascadeDelete() {
        return cascadeDelete;
    }

    /**
     * Sets whether deleting the parent also deletes the child records.
     *
     * @param cascadeDelete the cascade-delete flag
     */
    public void setCascadeDelete(boolean cascadeDelete) {
        this.cascadeDelete = cascadeDelete;
    }

    /**
     * Returns whether the parent cannot be deleted while child records exist, as reported by Salesforce.
     *
     * @return {@code true} if deletes are restricted
     */
    public boolean isRestrictedDelete() {
        return restrictedDelete;
    }

    /**
     * Sets whether the parent cannot be deleted while child records exist.
     *
     * @param restrictedDelete the restricted-delete flag
     */
    public void setRestrictedDelete(boolean restrictedDelete) {
        this.restrictedDelete = restrictedDelete;
    }

}
