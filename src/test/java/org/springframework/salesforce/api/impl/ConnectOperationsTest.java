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

package org.springframework.salesforce.api.impl;

import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.springframework.http.HttpMethod.GET;
import org.springframework.http.HttpStatus;
import org.springframework.salesforce.api.Community;
import org.springframework.salesforce.api.CommunityUser;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public class ConnectOperationsTest extends AbstractSalesforceTest {

    @Test
    public void getCommunitiesList() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/connect/communities/"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("communities.json")).headers(responseHeaders));

        List<Community> community = salesforce.connectOperations().getCommunities();

        assertEquals("leia-community1", community.get(0).getName());
        assertEquals("leia-community2", community.get(1).getName());
        assertEquals("leia-community3", community.get(2).getName());

        assertEquals("commId1", community.get(0).getId());
        assertEquals("commId2", community.get(1).getId());
        assertEquals("commId3", community.get(2).getId());
    }

    @Test
    public void getCommunityUsersList() {
        mockServer.expect(requestTo("https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/connect/communities/commId1/chatter/users"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.OK).body(loadResource("community-users.json")).headers(responseHeaders));

        List<CommunityUser> communityUsers = salesforce.connectOperations().getCommunityUsers("commId1");

        assertEquals("commUserId1", communityUsers.get(0).getId());
        assertEquals("UserABCDEF", communityUsers.get(0).getCommunityNickname());
        assertEquals("mock-user@test.com", communityUsers.get(0).getUsername());
        assertEquals("MockUser1", communityUsers.get(0).getDisplayName());

    }
}
