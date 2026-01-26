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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.springframework.salesforce.api.ApiVersion;
import org.springframework.salesforce.api.Community;
import org.springframework.salesforce.api.CommunityUser;
import org.springframework.salesforce.api.QueryResult;
import org.springframework.salesforce.api.ResultItem;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.client.BaseSalesforceFactory;

/**
 * This is a test that fully test the API over a real account.
 *
 * @author Umut Utkan
 */
@Ignore
public class ApiTest {

    public static final String PRODUCTION_SALESFORCE_URL = "https://login.salesforce.com/services/oauth2/token";
    public static final String SANDBOX_SALESFORCE_URL = "https://test.salesforce.com/services/oauth2/token";

    public static void main(String args[]) throws IOException {
        BufferedReader br = null;
        if (args.length > 0) {
            br = new BufferedReader(new FileReader(args[0]));
        } else {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        System.out.println("Enter your auth url (1 or 2)\n" +
                "\t1: https://login.salesforce.com/services/oauth2/token \n" +
                "\t2: https://test.salesforce.com/services/oauth2/token");
        final String urlSelection = br.readLine();
        String authURL = resolveAuthURL(urlSelection);
        System.out.println("Entered: " + authURL);

        System.out.println("\nEnter your SF client id: ");
        final String clientid = br.readLine();
        System.out.println("Entered: " + clientid);

        System.out.println("\nEnter your SF client secret: ");
        final String clientSecret = br.readLine();
        System.out.println("Entered: " + clientSecret);

        System.out.println("\nEnter your SF username: ");
        final String username = br.readLine();
        System.out.println("Entered: " + username);

        System.out.println("\nEnter your SF password: ");
        final String password = br.readLine();
        System.out.println("Entered: " + password);

        System.out.println("\nEnter your secret token (none if trusted host set up): ");
        final String secretToken = br.readLine();
        System.out.println("Entered: " + secretToken);

        BaseSalesforceFactory factory = new BaseSalesforceFactory(clientid, clientSecret);
        factory.setAuthorizeUrl(authURL);

        Salesforce template = factory.create(username, password, secretToken);

        testMetaApiOperations(template);

        System.out.println("\n\n");

        testChatterOperations(template);

        System.out.println("\n\n");

        testSObjectsOperations(template);

        System.out.println("\n\n");

        testLeadCreateUpdate(template);

        System.out.println("\n\n");

        testCommunityOperations(template);

        System.out.println("\n\n");

    }

    private static String resolveAuthURL(String numberSelection) {
        if (Integer.valueOf(numberSelection) == 2) {
            return SANDBOX_SALESFORCE_URL;
        }
        return PRODUCTION_SALESFORCE_URL;
    }

    public static void testMetaApiOperations(Salesforce api) {
        System.out.println("Supported API Versions:");

        for (ApiVersion apiVersion : api.apiOperations().getVersions()) {

            System.out.println(apiVersion);
            System.out.println("Services supported by this version:");
            System.out.println(api.apiOperations().getServices(
                    apiVersion.getVersion()));
        }
    }

    @SuppressWarnings("rawtypes")
    public static void testSObjectsOperations(Salesforce api) {
        System.out.println("SObjects:");

        for (Map sobject : api.sObjectsOperations().getSObjects()) {

            System.out.println(sobject);

            String sobjectName = sobject.get("name").toString();

            System.out.println(sobjectName + " : Calling describe");
            System.out.println(sobjectName + " : " + api.sObjectsOperations().describeSObject(sobjectName));

            if ((Boolean) sobject.get("queryable") && (Boolean) sobject.get("replicateable")) {
                try {
                    QueryResult queryResult = api.queryOperations().query("SELECT Id from " + sobjectName);
                    if (queryResult.getTotalSize() > 0) {
                        ResultItem first = queryResult.getRecords().get(0);

                        System.out.println(sobjectName + " : The id the of the first row is " + first.getAttributes().get("Id"));

                        Map row = api.sObjectsOperations().getRow(first.getType(), (String) first.getAttributes().get("Id"));

                        System.out.println(sobjectName + " : Printing the first row.");
                        System.out.println(sobjectName + " : " + row);
                    }

                } catch (Exception e) {
                    System.out.println("WARNING: Possibly " + sobjectName + " does not allow queries without filter.");
                }
            }
        }
    }

    public static void testChatterOperations(Salesforce template) {
        System.out.println("Chatter API:");
        System.out.println("Current user's latest status:");
        System.out.println(template.chatterOperations().getStatus());

        System.out.println("Updated current user's status, new status:");
        System.out.println(template.chatterOperations().updateStatus("Updated status via #spring-social-salesforce!"));
    }

    public static void testCommunityOperations(Salesforce api) {

        System.out.println("Communities test:");

        for(Community community: api.connectOperations(api.getInstanceUrl()).getCommunities()) {
            System.out.println("Community ID: " + community.getId());
            System.out.println("Community Name: " + community.getName());
            System.out.println("Community URL: " + community.getUrl());

            for(CommunityUser communityUser: api.connectOperations(api.getInstanceUrl()).getCommunityUsers(community.getId())) {
                System.out.println("User name: " + communityUser.getName());
                System.out.println("User email:  " + communityUser.getEmail());
            }
        }

    }

    public static void testLeadCreateUpdate(Salesforce template) {
        System.out.println("Object Creation (Lead):");

        Map<String, Object> lead = new HashMap<String, Object>();
        lead.put("Company", "Spring-social-salesforce test");
        // lead.put("FirstName", "John");
        lead.put("LastName", "Smith");
        lead.put("Phone", "555-555-1212");

        Map<String, ?> createdLead = template.sObjectsOperations().create("Lead", lead);
        System.out.println("Created lead: " + createdLead);

        String createdId = createdLead.get("id").toString();
        Map<String, Object> updateValues = new HashMap<String, Object>();
        updateValues.put("LastName", "Johnson");
        updateValues.put("FirstName", null);
        updateValues.put("Title", "Updatee");
        Map<String, ?> updated = template.sObjectsOperations().update("Lead", createdId, updateValues);
        System.out.println("Updated Lead: " + updated);

    }

}
