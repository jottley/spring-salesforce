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

    public List<Map> getSObjects();

    public SObjectSummary getSObjectSummary(String name);

    public SObjectDetail describeSObject(String name);

    public Map getRow(String name, String id, String... fields);

    /**
     * Reads a blob field (for example ContentVersion.VersionData or Attachment.Body) fully into memory
     * and returns it as a stream. Prefer {@link #downloadBlob} for large content.
     *
     * @return a stream over the content; never null. It is empty if the field has no content.
     */
    public InputStream getBlob(String name, String id, String field);

    /**
     * Streams a blob field (for example ContentVersion.VersionData or Attachment.Body) to the
     * given stream without holding the whole content in memory. The caller owns and closes the stream.
     * Nothing is written if Salesforce responds with an error.
     *
     * @return the number of bytes written
     */
    long downloadBlob(String name, String id, String field, OutputStream out);

    Map<String, Object> create(String name, Map<String, Object> fields);

    Map<String, Object> update(String sObjectName, String sObjectId, Map<String, Object> fields);

    public void delete(String sObjectName, String sObjectId);

}
