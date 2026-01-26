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
 * @author Umut Utkan
 */
public class Field {

    private int length;

    private String name;

    private String type;

    private String defaultValue;

    private String label;

    private boolean updateable;

    private boolean calculated;

    private boolean filterable;

    private boolean sortable;

    private String controllerName;

    private boolean unique;

    private boolean nillable;

    private int precision;

    private int scale;

    private boolean caseSensitive;

    private int byteLength;

    private boolean nameField;

    private boolean externalId;

    private boolean idLookup;

    private String inlineHelpText;

    private boolean createable;

    private String soapType;

    private boolean autoNumber;

    private boolean namePointing;

    private boolean custom;

    private boolean defaultedOnCreate;

    private boolean deprecatedAndHidden;

    private boolean htmlFormatted;

    private String defaultValueFormula;

    private String calculatedFormula;

    private boolean restrictedPicklist;

    private List<PickListEntry> picklistValues;

    private boolean dependentPicklist;

    private String[] referenceTo;

    private String relationshipName;

    private int relationshipOrder;

    private boolean writeRequiresMasterRead;

    private boolean cascadeDelete;

    private boolean restrictedDelete;

    private int digits;

    private boolean groupable;


    public Field(String name, String type, String label) {
        this.name = name;
        this.type = type;
        this.label = label;
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isUpdateable() {
        return updateable;
    }

    public void setUpdateable(boolean updateable) {
        this.updateable = updateable;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    public boolean isFilterable() {
        return filterable;
    }

    public void setFilterable(boolean filterable) {
        this.filterable = filterable;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public int getByteLength() {
        return byteLength;
    }

    public void setByteLength(int byteLength) {
        this.byteLength = byteLength;
    }

    public boolean isNameField() {
        return nameField;
    }

    public void setNameField(boolean nameField) {
        this.nameField = nameField;
    }

    public boolean isExternalId() {
        return externalId;
    }

    public void setExternalId(boolean externalId) {
        this.externalId = externalId;
    }

    public boolean isIdLookup() {
        return idLookup;
    }

    public void setIdLookup(boolean idLookup) {
        this.idLookup = idLookup;
    }

    public String getInlineHelpText() {
        return inlineHelpText;
    }

    public void setInlineHelpText(String inlineHelpText) {
        this.inlineHelpText = inlineHelpText;
    }

    public boolean isCreateable() {
        return createable;
    }

    public void setCreateable(boolean createable) {
        this.createable = createable;
    }

    public String getSoapType() {
        return soapType;
    }

    public void setSoapType(String soapType) {
        this.soapType = soapType;
    }

    public boolean isAutoNumber() {
        return autoNumber;
    }

    public void setAutoNumber(boolean autoNumber) {
        this.autoNumber = autoNumber;
    }

    public boolean isNamePointing() {
        return namePointing;
    }

    public void setNamePointing(boolean namePointing) {
        this.namePointing = namePointing;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public boolean isDefaultedOnCreate() {
        return defaultedOnCreate;
    }

    public void setDefaultedOnCreate(boolean defaultedOnCreate) {
        this.defaultedOnCreate = defaultedOnCreate;
    }

    public boolean isDeprecatedAndHidden() {
        return deprecatedAndHidden;
    }

    public void setDeprecatedAndHidden(boolean deprecatedAndHidden) {
        this.deprecatedAndHidden = deprecatedAndHidden;
    }

    public boolean isHtmlFormatted() {
        return htmlFormatted;
    }

    public void setHtmlFormatted(boolean htmlFormatted) {
        this.htmlFormatted = htmlFormatted;
    }

    public String getDefaultValueFormula() {
        return defaultValueFormula;
    }

    public void setDefaultValueFormula(String defaultValueFormula) {
        this.defaultValueFormula = defaultValueFormula;
    }

    public String getCalculatedFormula() {
        return calculatedFormula;
    }

    public void setCalculatedFormula(String calculatedFormula) {
        this.calculatedFormula = calculatedFormula;
    }

    public boolean isRestrictedPicklist() {
        return restrictedPicklist;
    }

    public void setRestrictedPicklist(boolean restrictedPicklist) {
        this.restrictedPicklist = restrictedPicklist;
    }

    public List<PickListEntry> getPicklistValues() {
        return picklistValues;
    }

    public void setPicklistValues(List<PickListEntry> picklistValues) {
        this.picklistValues = picklistValues;
    }

    public boolean isDependentPicklist() {
        return dependentPicklist;
    }

    public void setDependentPicklist(boolean dependentPicklist) {
        this.dependentPicklist = dependentPicklist;
    }

    public String[] getReferenceTo() {
        return referenceTo;
    }

    public void setReferenceTo(String[] referenceTo) {
        this.referenceTo = referenceTo;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    public int getRelationshipOrder() {
        return relationshipOrder;
    }

    public void setRelationshipOrder(int relationshipOrder) {
        this.relationshipOrder = relationshipOrder;
    }

    public boolean isWriteRequiresMasterRead() {
        return writeRequiresMasterRead;
    }

    public void setWriteRequiresMasterRead(boolean writeRequiresMasterRead) {
        this.writeRequiresMasterRead = writeRequiresMasterRead;
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

    public int getDigits() {
        return digits;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    public boolean isGroupable() {
        return groupable;
    }

    public void setGroupable(boolean groupable) {
        this.groupable = groupable;
    }
}
