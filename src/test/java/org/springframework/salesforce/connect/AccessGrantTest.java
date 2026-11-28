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
package org.springframework.salesforce.connect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

/**
 * @author Jared Ottley
 */
public class AccessGrantTest {

  @Test
  public void deserializesTokenResponseAndIgnoresUnknownFields() throws Exception {
    String json =
        "{\"access_token\":\"a\",\"refresh_token\":\"r\",\"signature\":\"s\",\"scope\":\"api web\","
            + "\"id_token\":\"idt\",\"instance_url\":\"https://na7.salesforce.com\","
            + "\"id\":\"https://login.salesforce.com/id/00D/005\",\"token_type\":\"Bearer\","
            + "\"issued_at\":\"1700000000000\",\"something_new\":true}";

    AccessGrant g = new ObjectMapper().readValue(json, AccessGrant.class);

    assertEquals("a", g.getAccessToken());
    assertEquals("r", g.getRefreshToken());
    assertEquals("api web", g.getScope());
    assertEquals("idt", g.getIdToken());
    assertEquals("https://na7.salesforce.com", g.getInstanceUrl());
    assertEquals("https://login.salesforce.com/id/00D/005", g.getId());
    assertEquals("Bearer", g.getTokenType());
    assertEquals("1700000000000", g.getIssuedAt());
  }

  @Test
  public void settersAndMissingFields() {
    AccessGrant g = new AccessGrant();
    assertNull(g.getAccessToken());
    g.setAccessToken("a");
    g.setRefreshToken("r");
    g.setInstanceUrl("https://i");
    g.setTokenType("Bearer");
    g.setScope("api");
    g.setIdToken("idt");
    assertEquals("a", g.getAccessToken());
    assertEquals("r", g.getRefreshToken());
    assertEquals("https://i", g.getInstanceUrl());
    assertEquals("Bearer", g.getTokenType());
    assertEquals("api", g.getScope());
    assertEquals("idt", g.getIdToken());
  }
}
