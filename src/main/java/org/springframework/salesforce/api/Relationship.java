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
 * @author Umut Utkan
 */
public class Relationship {

    private String field;

    private boolean deprecatedAndHidden;

    private String relationshipName;

    private boolean cascadeDelete;

    private boolean restrictedDelete;

    private String childSObject;


    public Relationship(String field, String relationshipName, String childObject) {
        this.field = field;
        this.relationshipName = relationshipName;
        this.childSObject = childObject;
    }


    public String getField() {
        return field;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public String getChildSObject() {
        return childSObject;
    }

    public boolean isDeprecatedAndHidden() {
        return deprecatedAndHidden;
    }

    public void setDeprecatedAndHidden(boolean deprecatedAndHidden) {
        this.deprecatedAndHidden = deprecatedAndHidden;
    }

    public boolean isCascadeDelete() {
        return cascadeDelete;
    }

    public void setCascadeDelete(boolean cascadeDelete) {
        this.cascadeDelete = cascadeDelete;
    }

    public boolean isRestrictedDelete() {
        return restrictedDelete;
    }

    public void setRestrictedDelete(boolean restrictedDelete) {
        this.restrictedDelete = restrictedDelete;
    }

}
