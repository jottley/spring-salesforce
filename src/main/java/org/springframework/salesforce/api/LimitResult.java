/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-social-salesforce
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * @author Jared Ottley
 */
public class LimitResult extends AbstractLimits {

    private static final long serialVersionUID = 1L;

    /**
     * No args constructor for use in serialization
     *
     */
    public LimitResult() {
        super();
    }

    /**
     *
     * @param remaining
     * @param max
     */
    public LimitResult(Integer max, Integer remaining) {
        super(max, remaining);
    }
}