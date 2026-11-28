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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.salesforce.api.Field;
import org.springframework.salesforce.api.PickListEntry;

/**
 * Jackson mixin that maps JSON properties onto {@link Field}.
 *
 * @author Umut Utkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldMixin {

  /**
   * Binds the constructor arguments of {@code Field} to JSON properties.
   *
   * @param name the JSON {@code name} property
   * @param type the JSON {@code type} property
   * @param label the JSON {@code label} property
   */
  @JsonCreator
  FieldMixin(
      @JsonProperty("name") String name,
      @JsonProperty("type") String type,
      @JsonProperty("label") String label) {}

  /** Binds the JSON property {@code length} to {@code Field.length}. */
  @JsonProperty("length")
  int length;

  /** Binds the JSON property {@code defaultValue} to {@code Field.defaultValue}. */
  @JsonProperty("defaultValue")
  String defaultValue;

  /** Binds the JSON property {@code updateable} to {@code Field.updateable}. */
  @JsonProperty("updateable")
  boolean updateable;

  /** Binds the JSON property {@code calculated} to {@code Field.calculated}. */
  @JsonProperty("calculated")
  boolean calculated;

  /** Binds the JSON property {@code filterable} to {@code Field.filterable}. */
  @JsonProperty("filterable")
  boolean filterable;

  /** Binds the JSON property {@code sortable} to {@code Field.sortable}. */
  @JsonProperty("sortable")
  boolean sortable;

  /** Binds the JSON property {@code controllerName} to {@code Field.controllerName}. */
  @JsonProperty("controllerName")
  String controllerName;

  /** Binds the JSON property {@code unique} to {@code Field.unique}. */
  @JsonProperty("unique")
  boolean unique;

  /** Binds the JSON property {@code nillable} to {@code Field.nillable}. */
  @JsonProperty("nillable")
  boolean nillable;

  /** Binds the JSON property {@code precision} to {@code Field.precision}. */
  @JsonProperty("precision")
  int precision;

  /** Binds the JSON property {@code scale} to {@code Field.scale}. */
  @JsonProperty("scale")
  int scale;

  /** Binds the JSON property {@code caseSensitive} to {@code Field.caseSensitive}. */
  @JsonProperty("caseSensitive")
  boolean caseSensitive;

  /** Binds the JSON property {@code byteLength} to {@code Field.byteLength}. */
  @JsonProperty("byteLength")
  int byteLength;

  /** Binds the JSON property {@code nameField} to {@code Field.nameField}. */
  @JsonProperty("nameField")
  boolean nameField;

  /** Binds the JSON property {@code externalId} to {@code Field.externalId}. */
  @JsonProperty("externalId")
  boolean externalId;

  /** Binds the JSON property {@code idLookup} to {@code Field.idLookup}. */
  @JsonProperty("idLookup")
  boolean idLookup;

  /** Binds the JSON property {@code inlineHelpText} to {@code Field.inlineHelpText}. */
  @JsonProperty("inlineHelpText")
  String inlineHelpText;

  /** Binds the JSON property {@code createable} to {@code Field.createable}. */
  @JsonProperty("createable")
  boolean createable;

  /** Binds the JSON property {@code soapType} to {@code Field.soapType}. */
  @JsonProperty("soapType")
  String soapType;

  /** Binds the JSON property {@code autoNumber} to {@code Field.autoNumber}. */
  @JsonProperty("autoNumber")
  boolean autoNumber;

  /** Binds the JSON property {@code namePointing} to {@code Field.namePointing}. */
  @JsonProperty("namePointing")
  boolean namePointing;

  /** Binds the JSON property {@code custom} to {@code Field.custom}. */
  @JsonProperty("custom")
  boolean custom;

  /** Binds the JSON property {@code defaultedOnCreate} to {@code Field.defaultedOnCreate}. */
  @JsonProperty("defaultedOnCreate")
  boolean defaultedOnCreate;

  /** Binds the JSON property {@code deprecatedAndHidden} to {@code Field.deprecatedAndHidden}. */
  @JsonProperty("deprecatedAndHidden")
  boolean deprecatedAndHidden;

  /** Binds the JSON property {@code htmlFormatted} to {@code Field.htmlFormatted}. */
  @JsonProperty("htmlFormatted")
  boolean htmlFormatted;

  /** Binds the JSON property {@code defaultValueFormula} to {@code Field.defaultValueFormula}. */
  @JsonProperty("defaultValueFormula")
  String defaultValueFormula;

  /** Binds the JSON property {@code calculatedFormula} to {@code Field.calculatedFormula}. */
  @JsonProperty("calculatedFormula")
  String calculatedFormula;

  /** Binds the JSON property {@code restrictedPicklist} to {@code Field.restrictedPicklist}. */
  @JsonProperty("restrictedPicklist")
  boolean restrictedPicklist;

  /** Binds the JSON property {@code picklistValues} to {@code Field.picklistValues}. */
  @JsonProperty("picklistValues")
  List<PickListEntry> picklistValues;

  /** Binds the JSON property {@code dependentPicklist} to {@code Field.dependentPicklist}. */
  @JsonProperty("dependentPicklist")
  boolean dependentPicklist;

  /** Binds the JSON property {@code referenceTo} to {@code Field.referenceTo}. */
  @JsonProperty("referenceTo")
  String[] referenceTo;

  /** Binds the JSON property {@code relationshipName} to {@code Field.relationshipName}. */
  @JsonProperty("relationshipName")
  String relationshipName;

  /** Binds the JSON property {@code relationshipOrder} to {@code Field.relationshipOrder}. */
  @JsonProperty("relationshipOrder")
  int relationshipOrder;

  /**
   * Binds the JSON property {@code writeRequiresMasterRead} to {@code
   * Field.writeRequiresMasterRead}.
   */
  @JsonProperty("writeRequiresMasterRead")
  boolean writeRequiresMasterRead;

  /** Binds the JSON property {@code cascadeDelete} to {@code Field.cascadeDelete}. */
  @JsonProperty("cascadeDelete")
  boolean cascadeDelete;

  /** Binds the JSON property {@code restrictedDelete} to {@code Field.restrictedDelete}. */
  @JsonProperty("restrictedDelete")
  boolean restrictedDelete;

  /** Binds the JSON property {@code digits} to {@code Field.digits}. */
  @JsonProperty("digits")
  int digits;

  /** Binds the JSON property {@code groupable} to {@code Field.groupable}. */
  @JsonProperty("groupable")
  boolean groupable;
}
