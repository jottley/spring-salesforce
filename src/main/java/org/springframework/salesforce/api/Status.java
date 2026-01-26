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

/**
 * @author Umut Utkan
 */
public class Status {

    private String text;

    private List<Map<String, String>> segments;


    public Status(String text) {
        this.text = text;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Map<String, String>> getSegments() {
        return segments;
    }

    public void setSegments(List<Map<String, String>> segments) {
        this.segments = segments;
    }

    @Override
    public String toString() {
        return "Status{" +
                "text='" + text + '\'' +
                ", segments=" + segments +
                '}';
    }

}
