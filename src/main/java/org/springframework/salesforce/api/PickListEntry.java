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
public class PickListEntry {

    private final String value;

    private final boolean active;

    private final String label;

    private final boolean defaultValue;

    public PickListEntry(String value, String label, boolean active, boolean defaultValue) {
        this.value = value;
        this.active = active;
        this.label = label;
        this.defaultValue = defaultValue;
    }


    public String getValue() {
        return value;
    }

    public boolean isActive() {
        return active;
    }

    public String getLabel() {
        return label;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

}
