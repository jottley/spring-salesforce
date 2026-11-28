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
package org.springframework.salesforce.api.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.springframework.salesforce.api.RecentOperations;
import org.springframework.salesforce.api.ResultItem;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.web.client.RestTemplate;

/**
 * Default implementation of {@link RecentOperations}.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class RecentTemplate extends AbstractSalesForceOperations<Salesforce>
    implements RecentOperations {

  /** The shared rest template used for requests. */
  private final RestTemplate restTemplate;

  /**
   * Creates the operations instance.
   *
   * @param api the {@link Salesforce} instance this operations object belongs to
   * @param restTemplate the shared rest template used for requests
   */
  public RecentTemplate(Salesforce api, RestTemplate restTemplate) {
    super(api);
    this.restTemplate = restTemplate;
  }

  /**
   * Retrieves the items the user recently viewed or modified.
   *
   * @return the recent items
   */
  @Override
  public List<ResultItem> recent() {
    return api.readList(
        restTemplate.getForObject(
            api.getBaseUrl() + "/" + getVersion() + "/recent", JsonNode.class),
        ResultItem.class);
  }
}
