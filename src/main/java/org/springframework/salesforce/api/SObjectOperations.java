/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-social-salesforce
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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Defines operations for interacting with the sObjects API.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public interface SObjectOperations {

  /**
   * Lists the sObjects available in the org.
   *
   * @return one map of properties per sObject; empty if the response body is empty or has no {@code
   *     sobjects} entry
   */
  public List<Map> getSObjects();

  /**
   * Gets the summary ({@code objectDescribe}) of an sObject.
   *
   * @param name the sObject name, for example {@code Account}
   * @return the summary, or null if Salesforce returns nothing
   * @throws IllegalArgumentException if the name is null, blank, {@code .} or {@code ..}
   */
  public SObjectSummary getSObjectSummary(String name);

  /**
   * Describes an sObject in full, including its fields.
   *
   * @param name the sObject name, for example {@code Account}
   * @return the full description
   * @throws IllegalArgumentException if the name is null, blank, {@code .} or {@code ..}
   */
  public SObjectDetail describeSObject(String name);

  /**
   * Retrieves a single record.
   *
   * @param name the sObject name, for example {@code Account}
   * @param id the record id
   * @param fields the fields to return; if none are given Salesforce returns all fields
   * @return the record's field values keyed by field name
   * @throws IllegalArgumentException if the name or id is null, blank, {@code .} or {@code ..}
   */
  public Map getRow(String name, String id, String... fields);

  /**
   * Reads a blob field (for example ContentVersion.VersionData or Attachment.Body) fully into
   * memory and returns it as a stream. Prefer {@link #downloadBlob} for large content.
   *
   * @param name the sObject name, for example {@code ContentVersion}
   * @param id the record id
   * @param field the blob field name, for example {@code VersionData}
   * @return a stream over the content; never null. It is empty if the field has no content.
   * @throws IllegalArgumentException if the name, id or field is null, blank, {@code .} or {@code
   *     ..}
   */
  public InputStream getBlob(String name, String id, String field);

  /**
   * Streams a blob field (for example ContentVersion.VersionData or Attachment.Body) to the given
   * stream without holding the whole content in memory. The caller owns and closes the stream.
   * Nothing is written if Salesforce responds with an error.
   *
   * @param name the sObject name, for example {@code ContentVersion}
   * @param id the record id
   * @param field the blob field name, for example {@code VersionData}
   * @param out the stream the content is written to
   * @return the number of bytes written
   * @throws IllegalArgumentException if the name, id or field is null, blank, {@code .} or {@code
   *     ..}
   */
  long downloadBlob(String name, String id, String field, OutputStream out);

  /**
   * Creates a record.
   *
   * @param name the sObject name, for example {@code Account}
   * @param fields the field values for the new record, keyed by field name
   * @return Salesforce's response, which includes the new record's {@code id} and {@code success}
   * @throws IllegalArgumentException if the name is null, blank, {@code .} or {@code ..}
   */
  Map<String, Object> create(String name, Map<String, Object> fields);

  /**
   * Updates a record.
   *
   * @param sObjectName the sObject name, for example {@code Account}
   * @param sObjectId the id of the record to update
   * @param fields the field values to change, keyed by field name
   * @return Salesforce's response if it sends one; otherwise, as Salesforce replies with an empty
   *     body on success, a map with the record's {@code id}, {@code success} set to true and an
   *     empty {@code errors} list
   * @throws IllegalArgumentException if the name or id is null, blank, {@code .} or {@code ..}
   */
  Map<String, Object> update(String sObjectName, String sObjectId, Map<String, Object> fields);

  /**
   * Deletes a record.
   *
   * @param sObjectName the sObject name, for example {@code Account}
   * @param sObjectId the id of the record to delete
   * @throws IllegalArgumentException if the name or id is null, blank, {@code .} or {@code ..}
   */
  public void delete(String sObjectName, String sObjectId);
}
