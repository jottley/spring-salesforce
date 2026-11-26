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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.salesforce.api.impl.SalesforceTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Deserializes the recorded Salesforce responses into the model classes and checks that every
 * scalar value Salesforce sends ends up in the matching property. This catches mismatches
 * between the JSON names Salesforce uses and the names the Jackson mixins and annotations expect,
 * which otherwise fail silently because unknown properties are ignored.
 *
 * @author Jared Ottley
 */
public class ModelDeserializationTest {

    private final ObjectMapper json = new ObjectMapper();
    private SalesforceTemplate salesforce;

    @Before
    public void setUp() {
        salesforce = new SalesforceTemplate("token");
    }

    private JsonNode resource(String name) throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/" + name)) {
            assertNotNull("missing test resource " + name, in);
            return json.readTree(in);
        }
    }

    /**
     * Compares each scalar in {@code node} with the bean getter of the same name (also trying
     * the {@code is} prefix and a {@code isXxx} key mapping onto an {@code xxx} property).
     * Keys without a matching getter are ignored; those are properties the model chose not to expose.
     */
    private void assertScalarsMatch(String what, Object bean, JsonNode node, List<String> problems) throws Exception {
        for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
            Map.Entry<String, JsonNode> e = it.next();
            JsonNode expected = e.getValue();
            if (!expected.isValueNode() || expected.isNull()) {
                continue;
            }
            Method getter = findGetter(bean.getClass(), e.getKey());
            if (getter == null) {
                continue;
            }
            Object actual = getter.invoke(bean);
            boolean ok;
            if (expected.isBoolean()) {
                ok = actual instanceof Boolean b && b == expected.booleanValue();
            } else if (expected.isNumber()) {
                ok = actual instanceof Number n && n.doubleValue() == expected.doubleValue();
            } else {
                ok = expected.asText().equals(String.valueOf(actual));
            }
            if (!ok) {
                problems.add(what + "." + getter.getName() + "() was <" + actual + "> but Salesforce sent " + e.getKey() + "=" + expected);
            }
        }
    }

    private static Method findGetter(Class<?> type, String key) {
        String cap = Character.toUpperCase(key.charAt(0)) + key.substring(1);
        List<String> candidates = new ArrayList<>(List.of("get" + cap, "is" + cap));
        if (key.length() > 2 && key.startsWith("is") && Character.isUpperCase(key.charAt(2))) {
            candidates.add("is" + key.substring(2));
            candidates.add("get" + key.substring(2));
        }
        for (String name : candidates) {
            try {
                return type.getMethod(name);
            } catch (NoSuchMethodException ignored) {
                // try the next candidate
            }
        }
        return null;
    }

    private <T> void assertAllMatch(String what, Class<T> type, Iterable<JsonNode> nodes) throws Exception {
        List<String> problems = new ArrayList<>();
        int count = 0;
        for (JsonNode node : nodes) {
            T bean = salesforce.readObject(node, type);
            assertNotNull(bean);
            assertScalarsMatch(what + "[" + count++ + "]", bean, node, problems);
        }
        assertTrue("no " + what + " records were checked", count > 0);
        if (!problems.isEmpty()) {
            // a handful of examples is enough to find the mismatch; the rest are the same property
            fail(problems.size() + " mismatches, e.g.\n" + String.join("\n", problems.subList(0, Math.min(problems.size(), 6))));
        }
    }

    @Test
    public void sObjectSummariesCarryEveryFlagSalesforceSends() throws Exception {
        assertAllMatch("SObjectSummary", SObjectSummary.class, resource("sobjects.json").get("sobjects"));
    }

    @Test
    public void sObjectDetailCarriesEveryFlagSalesforceSends() throws Exception {
        assertAllMatch("SObjectDetail", SObjectDetail.class, List.of(resource("account_desc.json")));
    }

    @Test
    public void layoutableDeprecatedAndRetrieveableFlagsAreRead() throws Exception {
        String body = "{\"name\":\"Thing__c\",\"layoutable\":true,\"deprecatedAndHidden\":true,\"retrieveable\":true}";
        SObjectSummary summary = salesforce.readObject(json.readTree(body), SObjectSummary.class);
        SObjectDetail detail = salesforce.readObject(json.readTree(body), SObjectDetail.class);

        assertTrue(summary.isLayoutable());
        assertTrue(summary.isDeprecatedAndHidden());
        assertTrue(summary.isRetrieveable());
        assertTrue(detail.isLayoutable());
        assertTrue(detail.isDeprecatedAndHidden());
        assertTrue(detail.isRetrieveable());
    }

    @Test
    public void fieldsCarryEveryValueSalesforceSends() throws Exception {
        List<JsonNode> fields = new ArrayList<>();
        resource("account_desc.json").get("fields").forEach(fields::add);
        assertAllMatch("Field", Field.class, fields);
    }

    @Test
    public void picklistValuesAreRead() throws Exception {
        List<JsonNode> entries = new ArrayList<>();
        resource("account_desc.json").get("fields").forEach(f -> f.get("picklistValues").forEach(entries::add));
        assertAllMatch("PickListEntry", PickListEntry.class, entries);
    }

    @Test
    public void childRelationshipsAreRead() throws Exception {
        List<JsonNode> rels = new ArrayList<>();
        resource("account_desc.json").get("childRelationships").forEach(rels::add);
        assertAllMatch("Relationship", Relationship.class, rels);
        Relationship first = salesforce.readObject(rels.get(0), Relationship.class);
        assertEquals(rels.get(0).get("childSObject").asText(), first.getChildSObject());
    }

    @Test
    public void communitiesAreRead() throws Exception {
        assertAllMatch("Community", Community.class, resource("communities.json").get("communities"));
    }

    @Test
    public void communityUsersAreRead() throws Exception {
        assertAllMatch("CommunityUser", CommunityUser.class, resource("community-users.json").get("users"));
    }

    @Test
    public void communityUserReadsIsInThisCommunity() throws Exception {
        CommunityUser user = salesforce.readObject(json.readTree("{\"id\":\"005\",\"isInThisCommunity\":true}"), CommunityUser.class);
        assertTrue(user.isInThisCommunity());
    }

    @Test
    public void profileAndUserDetailsAreRead() throws Exception {
        assertAllMatch("SalesforceProfile", SalesforceProfile.class, List.of(resource("profile.json")));
        SalesforceProfile profile = salesforce.readObject(resource("profile.json"), SalesforceProfile.class);
        assertNotNull(profile.getPhoto());
    }

    @Test
    public void apiVersionsAreRead() throws Exception {
        assertAllMatch("ApiVersion", ApiVersion.class, resource("versions.json"));
    }

    @Test
    public void limitsAreReadIncludingConnectedAppLimits() throws Exception {
        JsonNode limits = resource("limits.json");
        LimitsResults results = salesforce.readObject(limits, LimitsResults.class);
        List<String> problems = new ArrayList<>();
        int checked = 0;
        for (Iterator<Map.Entry<String, JsonNode>> it = limits.fields(); it.hasNext();) {
            Map.Entry<String, JsonNode> e = it.next();
            Method getter = findGetter(LimitsResults.class, e.getKey());
            if (getter == null) {
                problems.add("LimitsResults has no getter for " + e.getKey());
                continue;
            }
            LimitResult actual = (LimitResult) getter.invoke(results);
            assertNotNull(e.getKey() + " was not populated", actual);
            assertScalarsMatch("LimitsResults." + e.getKey(), actual, e.getValue(), problems);
            checked++;
        }
        assertTrue(checked > 0);
        assertTrue(String.join("\n", problems), problems.isEmpty());
    }
}
