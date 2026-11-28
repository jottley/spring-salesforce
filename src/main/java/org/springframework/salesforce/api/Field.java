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
package org.springframework.salesforce.api;

import java.util.List;

/**
 * Describes a single field of an sObject, as reported in a Salesforce describe result. Most
 * properties are passed through as reported by Salesforce.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class Field {

  /** The maximum size of the field. */
  private int length;

  /** The name of the field. */
  private final String name;

  /** The Salesforce data type of the field. */
  private final String type;

  /** The default value of the field. */
  private String defaultValue;

  /** The display label of the field. */
  private final String label;

  /** Whether the field can be updated. */
  private boolean updateable;

  /** Whether the field is calculated by a formula. */
  private boolean calculated;

  /** Whether the field can be used in a SOQL filter (WHERE) clause. */
  private boolean filterable;

  /** Whether the field can be used to sort query results. */
  private boolean sortable;

  /** The name of the controlling field for a dependent picklist. */
  private String controllerName;

  /** Whether the field value must be unique. */
  private boolean unique;

  /** Whether the field can be null. */
  private boolean nillable;

  /** The total number of digits for a numeric field. */
  private int precision;

  /** The number of digits to the right of the decimal point for a numeric field. */
  private int scale;

  /** Whether the field value is case sensitive. */
  private boolean caseSensitive;

  /** The maximum length of the field in bytes. */
  private int byteLength;

  /** Whether the field is the name field of the sObject. */
  private boolean nameField;

  /** Whether the field is an external ID. */
  private boolean externalId;

  /** Whether the field can be used to look up a record, such as in upsert. */
  private boolean idLookup;

  /** The inline help text of the field. */
  private String inlineHelpText;

  /** Whether the field can be created. */
  private boolean createable;

  /** The SOAP API type of the field. */
  private String soapType;

  /** Whether the field is an auto-number field. */
  private boolean autoNumber;

  /** Whether the field's value is the name of the parent of this object. */
  private boolean namePointing;

  /** Whether the field is a custom field. */
  private boolean custom;

  /** Whether Salesforce assigns a value when a record is created without one. */
  private boolean defaultedOnCreate;

  /** Reserved for future use by Salesforce. */
  private boolean deprecatedAndHidden;

  /**
   * Whether the field, such as a hyperlink formula field, is formatted as HTML and should be
   * encoded for display in HTML.
   */
  private boolean htmlFormatted;

  /** The formula that calculates the default value of the field. */
  private String defaultValueFormula;

  /** The formula of a calculated field. */
  private String calculatedFormula;

  /** Whether the picklist only accepts its defined values. */
  private boolean restrictedPicklist;

  /** The picklist entries of the field. */
  private List<PickListEntry> picklistValues;

  /**
   * Whether the picklist is a dependent picklist (its valid values depend on a controlling field).
   */
  private boolean dependentPicklist;

  /** The names of the sObject types the field can reference. */
  private String[] referenceTo;

  /** The name of the relationship the field represents. */
  private String relationshipName;

  /**
   * The position of a master-detail relationship field: 0 for the primary relationship and 1 for
   * the secondary one.
   */
  private int relationshipOrder;

  /** Whether writing to the field requires read access to the master record. */
  private boolean writeRequiresMasterRead;

  /**
   * Whether deleting the parent record also deletes the records referencing it through this field.
   */
  private boolean cascadeDelete;

  /**
   * Whether the referenced record cannot be deleted while records reference it through this field.
   */
  private boolean restrictedDelete;

  /** The maximum number of digits for an integer field. */
  private int digits;

  /** Whether the field can be used in a SOQL GROUP BY clause. */
  private boolean groupable;

  /**
   * Creates a field description.
   *
   * @param name the API name of the field
   * @param type the Salesforce data type of the field
   * @param label the display label of the field
   */
  public Field(String name, String type, String label) {
    this.name = name;
    this.type = type;
    this.label = label;
  }

  /**
   * Returns the name of the field.
   *
   * @return the name of the field
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the Salesforce data type of the field.
   *
   * @return the Salesforce data type of the field
   */
  public String getType() {
    return type;
  }

  /**
   * Returns the display label of the field.
   *
   * @return the display label of the field
   */
  public String getLabel() {
    return label;
  }

  /**
   * Returns the maximum size of the field. For string fields this is a number of characters, not
   * bytes; see {@link #getByteLength()} for the size in bytes.
   *
   * @return the maximum size of the field, in characters for string fields
   */
  public int getLength() {
    return length;
  }

  /**
   * Sets the maximum size of the field in characters.
   *
   * @param length the maximum size of the field, in characters for string fields
   */
  public void setLength(int length) {
    this.length = length;
  }

  /**
   * Returns the default value of the field as text; a boolean default arrives as {@code "true"} or
   * {@code "false"}.
   *
   * @return the default value of the field
   */
  public String getDefaultValue() {
    return defaultValue;
  }

  /**
   * Sets the default value of the field.
   *
   * @param defaultValue the default value of the field
   */
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  /**
   * Returns whether the field can be updated.
   *
   * @return {@code true} if the field can be updated
   */
  public boolean isUpdateable() {
    return updateable;
  }

  /**
   * Sets whether the field can be updated.
   *
   * @param updateable {@code true} if the field can be updated
   */
  public void setUpdateable(boolean updateable) {
    this.updateable = updateable;
  }

  /**
   * Returns whether the field is calculated by a formula.
   *
   * @return {@code true} if the field is calculated by a formula
   */
  public boolean isCalculated() {
    return calculated;
  }

  /**
   * Sets whether the field is calculated by a formula.
   *
   * @param calculated {@code true} if the field is calculated by a formula
   */
  public void setCalculated(boolean calculated) {
    this.calculated = calculated;
  }

  /**
   * Returns whether the field can be used in a SOQL filter (WHERE) clause.
   *
   * @return {@code true} if the field can be used in a SOQL filter (WHERE) clause
   */
  public boolean isFilterable() {
    return filterable;
  }

  /**
   * Sets whether the field can be used in a SOQL filter (WHERE) clause.
   *
   * @param filterable {@code true} if the field can be used in a SOQL filter (WHERE) clause
   */
  public void setFilterable(boolean filterable) {
    this.filterable = filterable;
  }

  /**
   * Returns whether the field can be used to sort query results.
   *
   * @return {@code true} if the field can be used to sort query results
   */
  public boolean isSortable() {
    return sortable;
  }

  /**
   * Sets whether the field can be used to sort query results.
   *
   * @param sortable {@code true} if the field can be used to sort query results
   */
  public void setSortable(boolean sortable) {
    this.sortable = sortable;
  }

  /**
   * Returns the name of the controlling field for a dependent picklist.
   *
   * @return the name of the controlling field for a dependent picklist
   */
  public String getControllerName() {
    return controllerName;
  }

  /**
   * Sets the name of the controlling field for a dependent picklist.
   *
   * @param controllerName the name of the controlling field for a dependent picklist
   */
  public void setControllerName(String controllerName) {
    this.controllerName = controllerName;
  }

  /**
   * Returns whether the field value must be unique.
   *
   * @return {@code true} if the field value must be unique
   */
  public boolean isUnique() {
    return unique;
  }

  /**
   * Sets whether the field value must be unique.
   *
   * @param unique {@code true} if the field value must be unique
   */
  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  /**
   * Returns whether the field can be null.
   *
   * @return {@code true} if the field can be null
   */
  public boolean isNillable() {
    return nillable;
  }

  /**
   * Sets whether the field can be null.
   *
   * @param nillable {@code true} if the field can be null
   */
  public void setNillable(boolean nillable) {
    this.nillable = nillable;
  }

  /**
   * Returns the total number of digits for a numeric field.
   *
   * @return the total number of digits for a numeric field
   */
  public int getPrecision() {
    return precision;
  }

  /**
   * Sets the total number of digits for a numeric field.
   *
   * @param precision the total number of digits for a numeric field
   */
  public void setPrecision(int precision) {
    this.precision = precision;
  }

  /**
   * Returns the number of digits to the right of the decimal point for a numeric field.
   *
   * @return the number of digits to the right of the decimal point for a numeric field
   */
  public int getScale() {
    return scale;
  }

  /**
   * Sets the number of digits to the right of the decimal point for a numeric field.
   *
   * @param scale the number of digits to the right of the decimal point for a numeric field
   */
  public void setScale(int scale) {
    this.scale = scale;
  }

  /**
   * Returns whether the field value is case sensitive.
   *
   * @return {@code true} if the field value is case sensitive
   */
  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  /**
   * Sets whether the field value is case sensitive.
   *
   * @param caseSensitive {@code true} if the field value is case sensitive
   */
  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  /**
   * Returns the maximum length of the field in bytes.
   *
   * @return the maximum length of the field in bytes
   */
  public int getByteLength() {
    return byteLength;
  }

  /**
   * Sets the maximum length of the field in bytes.
   *
   * @param byteLength the maximum length of the field in bytes
   */
  public void setByteLength(int byteLength) {
    this.byteLength = byteLength;
  }

  /**
   * Returns whether the field is the name field of the sObject.
   *
   * @return {@code true} if the field is the name field of the sObject
   */
  public boolean isNameField() {
    return nameField;
  }

  /**
   * Sets whether the field is the name field of the sObject.
   *
   * @param nameField {@code true} if the field is the name field of the sObject
   */
  public void setNameField(boolean nameField) {
    this.nameField = nameField;
  }

  /**
   * Returns whether the field is an external ID.
   *
   * @return {@code true} if the field is an external ID
   */
  public boolean isExternalId() {
    return externalId;
  }

  /**
   * Sets whether the field is an external ID.
   *
   * @param externalId {@code true} if the field is an external ID
   */
  public void setExternalId(boolean externalId) {
    this.externalId = externalId;
  }

  /**
   * Returns whether the field can be used to look up a record, such as in upsert.
   *
   * @return {@code true} if the field can be used to look up a record, such as in upsert
   */
  public boolean isIdLookup() {
    return idLookup;
  }

  /**
   * Sets whether the field can be used to look up a record, such as in upsert.
   *
   * @param idLookup {@code true} if the field can be used to look up a record, such as in upsert
   */
  public void setIdLookup(boolean idLookup) {
    this.idLookup = idLookup;
  }

  /**
   * Returns the inline help text of the field.
   *
   * @return the inline help text of the field
   */
  public String getInlineHelpText() {
    return inlineHelpText;
  }

  /**
   * Sets the inline help text of the field.
   *
   * @param inlineHelpText the inline help text of the field
   */
  public void setInlineHelpText(String inlineHelpText) {
    this.inlineHelpText = inlineHelpText;
  }

  /**
   * Returns whether the field can be created.
   *
   * @return {@code true} if the field can be created
   */
  public boolean isCreateable() {
    return createable;
  }

  /**
   * Sets whether the field can be created.
   *
   * @param createable {@code true} if the field can be created
   */
  public void setCreateable(boolean createable) {
    this.createable = createable;
  }

  /**
   * Returns the SOAP API type of the field.
   *
   * @return the SOAP API type of the field
   */
  public String getSoapType() {
    return soapType;
  }

  /**
   * Sets the SOAP API type of the field.
   *
   * @param soapType the SOAP API type of the field
   */
  public void setSoapType(String soapType) {
    this.soapType = soapType;
  }

  /**
   * Returns whether the field is an auto-number field.
   *
   * @return {@code true} if the field is an auto-number field
   */
  public boolean isAutoNumber() {
    return autoNumber;
  }

  /**
   * Sets whether the field is an auto-number field.
   *
   * @param autoNumber {@code true} if the field is an auto-number field
   */
  public void setAutoNumber(boolean autoNumber) {
    this.autoNumber = autoNumber;
  }

  /**
   * Returns whether the field's value is the name of the parent of this object.
   *
   * @return {@code true} if the field's value is the name of the parent of this object
   */
  public boolean isNamePointing() {
    return namePointing;
  }

  /**
   * Sets whether the field's value is the name of the parent of this object.
   *
   * @param namePointing {@code true} if the field's value is the name of the parent of this object
   */
  public void setNamePointing(boolean namePointing) {
    this.namePointing = namePointing;
  }

  /**
   * Returns whether the field is a custom field.
   *
   * @return {@code true} if the field is a custom field
   */
  public boolean isCustom() {
    return custom;
  }

  /**
   * Sets whether the field is a custom field.
   *
   * @param custom {@code true} if the field is a custom field
   */
  public void setCustom(boolean custom) {
    this.custom = custom;
  }

  /**
   * Returns whether Salesforce assigns a value to the field when a record is created, even if the
   * create call does not supply one (for example Owner, which defaults to the current user).
   *
   * @return {@code true} if Salesforce assigns a value when a record is created without one
   */
  public boolean isDefaultedOnCreate() {
    return defaultedOnCreate;
  }

  /**
   * Sets whether Salesforce assigns a value to the field when a record is created.
   *
   * @param defaultedOnCreate {@code true} if Salesforce assigns a value when a record is created
   *     without one
   */
  public void setDefaultedOnCreate(boolean defaultedOnCreate) {
    this.defaultedOnCreate = defaultedOnCreate;
  }

  /**
   * Returns the {@code deprecatedAndHidden} flag, which Salesforce documents as reserved for future
   * use.
   *
   * @return the flag, which Salesforce documents as reserved for future use
   */
  public boolean isDeprecatedAndHidden() {
    return deprecatedAndHidden;
  }

  /**
   * Sets the {@code deprecatedAndHidden} flag (reserved for future use by Salesforce).
   *
   * @param deprecatedAndHidden the flag (reserved for future use by Salesforce)
   */
  public void setDeprecatedAndHidden(boolean deprecatedAndHidden) {
    this.deprecatedAndHidden = deprecatedAndHidden;
  }

  /**
   * Returns whether the field, such as a hyperlink formula field, is formatted as HTML and should
   * be encoded for display in HTML.
   *
   * @return {@code true} if the field is formatted as HTML and should be encoded for display in
   *     HTML
   */
  public boolean isHtmlFormatted() {
    return htmlFormatted;
  }

  /**
   * Sets whether the field is formatted as HTML.
   *
   * @param htmlFormatted {@code true} if the field is formatted as HTML
   */
  public void setHtmlFormatted(boolean htmlFormatted) {
    this.htmlFormatted = htmlFormatted;
  }

  /**
   * Returns the formula that calculates the default value of the field.
   *
   * @return the formula that calculates the default value of the field
   */
  public String getDefaultValueFormula() {
    return defaultValueFormula;
  }

  /**
   * Sets the formula that calculates the default value of the field.
   *
   * @param defaultValueFormula the formula that calculates the default value of the field
   */
  public void setDefaultValueFormula(String defaultValueFormula) {
    this.defaultValueFormula = defaultValueFormula;
  }

  /**
   * Returns the formula of a calculated field.
   *
   * @return the formula of a calculated field
   */
  public String getCalculatedFormula() {
    return calculatedFormula;
  }

  /**
   * Sets the formula of a calculated field.
   *
   * @param calculatedFormula the formula of a calculated field
   */
  public void setCalculatedFormula(String calculatedFormula) {
    this.calculatedFormula = calculatedFormula;
  }

  /**
   * Returns whether the picklist only accepts its defined values.
   *
   * @return {@code true} if the picklist only accepts its defined values
   */
  public boolean isRestrictedPicklist() {
    return restrictedPicklist;
  }

  /**
   * Sets whether the picklist only accepts its defined values.
   *
   * @param restrictedPicklist {@code true} if the picklist only accepts its defined values
   */
  public void setRestrictedPicklist(boolean restrictedPicklist) {
    this.restrictedPicklist = restrictedPicklist;
  }

  /**
   * Returns the picklist entries of the field.
   *
   * @return the picklist entries of the field
   */
  public List<PickListEntry> getPicklistValues() {
    return picklistValues;
  }

  /**
   * Sets the picklist entries of the field.
   *
   * @param picklistValues the picklist entries of the field
   */
  public void setPicklistValues(List<PickListEntry> picklistValues) {
    this.picklistValues = picklistValues;
  }

  /**
   * Returns whether the picklist is a dependent picklist (its valid values depend on a controlling
   * field).
   *
   * @return {@code true} if the picklist is a dependent picklist (its valid values depend on a
   *     controlling field)
   */
  public boolean isDependentPicklist() {
    return dependentPicklist;
  }

  /**
   * Sets whether the picklist is a dependent picklist (its valid values depend on a controlling
   * field).
   *
   * @param dependentPicklist {@code true} if the picklist is a dependent picklist (its valid values
   *     depend on a controlling field)
   */
  public void setDependentPicklist(boolean dependentPicklist) {
    this.dependentPicklist = dependentPicklist;
  }

  /**
   * Returns the names of the sObject types the field can reference.
   *
   * @return the names of the sObject types the field can reference
   */
  public String[] getReferenceTo() {
    return referenceTo;
  }

  /**
   * Sets the names of the sObject types the field can reference.
   *
   * @param referenceTo the names of the sObject types the field can reference
   */
  public void setReferenceTo(String[] referenceTo) {
    this.referenceTo = referenceTo;
  }

  /**
   * Returns the name of the relationship the field represents.
   *
   * @return the name of the relationship the field represents
   */
  public String getRelationshipName() {
    return relationshipName;
  }

  /**
   * Sets the name of the relationship the field represents.
   *
   * @param relationshipName the name of the relationship the field represents
   */
  public void setRelationshipName(String relationshipName) {
    this.relationshipName = relationshipName;
  }

  /**
   * Returns the position of a master-detail relationship field: 0 for the primary relationship and
   * 1 for the secondary one. Salesforce sends null for other fields, which this primitive property
   * reads as 0.
   *
   * @return 0 for a primary master-detail relationship field, 1 for a secondary one, 0 for other
   *     fields
   */
  public int getRelationshipOrder() {
    return relationshipOrder;
  }

  /**
   * Sets the position of a master-detail relationship field (0 primary, 1 secondary).
   *
   * @param relationshipOrder 0 for a primary master-detail relationship field, 1 for a secondary
   *     one
   */
  public void setRelationshipOrder(int relationshipOrder) {
    this.relationshipOrder = relationshipOrder;
  }

  /**
   * Returns whether writing to the field requires read access to the master record.
   *
   * @return {@code true} if writing to the field requires read access to the master record
   */
  public boolean isWriteRequiresMasterRead() {
    return writeRequiresMasterRead;
  }

  /**
   * Sets whether writing to the field requires read access to the master record.
   *
   * @param writeRequiresMasterRead {@code true} if writing to the field requires read access to the
   *     master record
   */
  public void setWriteRequiresMasterRead(boolean writeRequiresMasterRead) {
    this.writeRequiresMasterRead = writeRequiresMasterRead;
  }

  /**
   * Returns whether deleting the parent record also deletes the records referencing it through this
   * field.
   *
   * @return {@code true} if deleting the parent record also deletes the records referencing it
   *     through this field
   */
  public boolean isCascadeDelete() {
    return cascadeDelete;
  }

  /**
   * Sets whether deleting the parent record also deletes the records referencing it through this
   * field.
   *
   * @param cascadeDelete {@code true} if deleting the parent record also deletes the records
   *     referencing it through this field
   */
  public void setCascadeDelete(boolean cascadeDelete) {
    this.cascadeDelete = cascadeDelete;
  }

  /**
   * Returns whether the referenced record cannot be deleted while records reference it through this
   * field.
   *
   * @return {@code true} if the referenced record cannot be deleted while records reference it
   *     through this field
   */
  public boolean isRestrictedDelete() {
    return restrictedDelete;
  }

  /**
   * Sets whether the referenced record cannot be deleted while records reference it through this
   * field.
   *
   * @param restrictedDelete {@code true} if the referenced record cannot be deleted while records
   *     reference it through this field
   */
  public void setRestrictedDelete(boolean restrictedDelete) {
    this.restrictedDelete = restrictedDelete;
  }

  /**
   * Returns the maximum number of digits for an integer field.
   *
   * @return the maximum number of digits for an integer field
   */
  public int getDigits() {
    return digits;
  }

  /**
   * Sets the maximum number of digits for an integer field.
   *
   * @param digits the maximum number of digits for an integer field
   */
  public void setDigits(int digits) {
    this.digits = digits;
  }

  /**
   * Returns whether the field can be used in a SOQL GROUP BY clause.
   *
   * @return {@code true} if the field can be used in a SOQL GROUP BY clause
   */
  public boolean isGroupable() {
    return groupable;
  }

  /**
   * Sets whether the field can be used in a SOQL GROUP BY clause.
   *
   * @param groupable {@code true} if the field can be used in a SOQL GROUP BY clause
   */
  public void setGroupable(boolean groupable) {
    this.groupable = groupable;
  }
}
