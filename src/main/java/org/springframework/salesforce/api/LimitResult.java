/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-salesforce
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A single Salesforce org limit as returned by the Limits resource: the maximum allowed ({@code
 * Max}) and the amount left ({@code Remaining}).
 *
 * @author Jared Ottley
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LimitResult extends AbstractLimits {

  /** Serialization version identifier. */
  private static final long serialVersionUID = 1L;

  /** No args constructor for use in serialization. */
  public LimitResult() {
    super();
  }

  /**
   * Creates a limit with the given values.
   *
   * @param max the maximum amount allowed; must not be {@code null}
   * @param remaining the amount remaining; must not be {@code null}
   * @throws NullPointerException if {@code max} or {@code remaining} is {@code null}
   */
  public LimitResult(Integer max, Integer remaining) {
    super(max, remaining);
  }
}
