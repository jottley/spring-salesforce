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
import java.util.Map;
import java.util.Objects;

/**
 * Encapsulates the error response sent by Salesforce.
 *
 * <p>{@link org.springframework.salesforce.api.impl.SalesforceErrorHandler} throws it for an HTTP
 * 400 response, carrying the {@code message}, {@code errorCode} and {@code fields} from the error
 * body.
 *
 * @see <a
 *     href="http://www.salesforce.com/us/developer/docs/api/Content/sforce_api_calls_concepts_core_data_objects.htm">
 *     Salesforce API Core Datatypes</a>
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SalesforceRequestException extends ApiException {

  /** Serialization version identifier. */
  private static final long serialVersionUID = 7047374539651371668L;

  /** The names of the fields the error relates to. */
  private final List<String> fields;

  /** The Salesforce error code. */
  private final String code;

  /**
   * Creates the exception with a detail message only. {@link #getCode()} and {@link #getFields()}
   * return null.
   *
   * @param message the detail message
   */
  public SalesforceRequestException(String message) {
    super(message);
    this.code = null;
    this.fields = null;
  }

  /**
   * Creates the exception from the error object in a Salesforce error response.
   *
   * @param errorDetails the parsed error object; {@code message} is used as the detail message,
   *     {@code errorCode} as the code ({@code UNKNOWN} if absent) and {@code fields} as the fields
   */
  @SuppressWarnings("unchecked")
  public SalesforceRequestException(Map<String, Object> errorDetails) {
    super((String) errorDetails.get("message"));

    this.code = Objects.toString(errorDetails.get("errorCode"), "UNKNOWN");
    this.fields = (List<String>) errorDetails.get("fields");
  }

  /**
   * Returns the names of the fields the error relates to.
   *
   * @return the field names, or null if Salesforce reported none or the exception was created from
   *     a message only
   */
  public List<String> getFields() {
    return fields;
  }

  /**
   * Returns the Salesforce error code.
   *
   * @return the {@code errorCode} from the response, {@code UNKNOWN} if it was absent, or null if
   *     the exception was created from a message only
   */
  public String getCode() {
    return code;
  }
}
