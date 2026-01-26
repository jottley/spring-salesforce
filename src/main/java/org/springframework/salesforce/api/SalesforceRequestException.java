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
import java.util.Map;
import java.util.Objects;

/**
 * Encapsulates the error response sent by Salesforce.
 * <br>
 *
 * @see <a
 *      href="http://www.salesforce.com/us/developer/docs/api/Content/sforce_api_calls_concepts_core_data_objects.htm">
 *      Salesforce API Core Datatypes</a>
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SalesforceRequestException extends ApiException {

    private static final long serialVersionUID = 7047374539651371668L;

    private final List<String> fields;
    private final String code;

    public SalesforceRequestException(String message) {
        super(message);
        this.code = null;
        this.fields = null;
    }

    @SuppressWarnings("unchecked")
    public SalesforceRequestException(Map<String, Object> errorDetails) {
        super((String)errorDetails.get("message"));

        this.code = Objects.toString(errorDetails.get("errorCode"), "UNKNOWN");
        this.fields = (List<String>) errorDetails.get("fields");
    }

    public List<String> getFields() {
        return fields;
    }

    public String getCode() {
        return code;
    }

}
