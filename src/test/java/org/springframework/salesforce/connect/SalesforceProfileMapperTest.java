/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-social-salesforce
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.salesforce.api.ChatterOperations;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.api.SalesforceProfile;
import org.springframework.salesforce.api.SalesforceUserDetails;
import org.springframework.salesforce.api.UserOperations;

/**
 * Verifies that {@link SalesforceProfileMapper} routes to the instance-URL and gateway-URL
 * overloads only when those values were configured.
 *
 * @author Jared Ottley
 */
public class SalesforceProfileMapperTest {

  private static final String INSTANCE_URL = "https://na7.salesforce.com";
  private static final String CUSTOM_GATEWAY = "https://custom.gateway";
  private static final String SANDBOX_GATEWAY = SalesforceServiceProvider.SANDBOX_GATEWAY_URL;

  private final SalesforceProfile profile = new SalesforceProfile();
  private final SalesforceUserDetails details =
      new SalesforceUserDetails("id", "f", "l", "e", "n", "o", "nick", "pu");

  private Salesforce salesforce;
  private ChatterOperations defaultChatter;
  private ChatterOperations instanceChatter;
  private UserOperations defaultUser;
  private UserOperations sandboxUser;
  private UserOperations customUser;

  @Before
  public void setUp() {
    salesforce = mock(Salesforce.class);
    defaultChatter = mock(ChatterOperations.class);
    instanceChatter = mock(ChatterOperations.class);
    defaultUser = mock(UserOperations.class);
    sandboxUser = mock(UserOperations.class);
    customUser = mock(UserOperations.class);

    when(salesforce.chatterOperations()).thenReturn(defaultChatter);
    when(salesforce.chatterOperations(INSTANCE_URL)).thenReturn(instanceChatter);
    when(salesforce.userOperations()).thenReturn(defaultUser);
    when(salesforce.userOperations(SANDBOX_GATEWAY)).thenReturn(sandboxUser);
    when(salesforce.userOperations(CUSTOM_GATEWAY)).thenReturn(customUser);

    when(defaultChatter.getUserProfile()).thenReturn(profile);
    when(instanceChatter.getUserProfile()).thenReturn(profile);
    when(defaultUser.getSalesforceUserDetails()).thenReturn(details);
    when(sandboxUser.getSalesforceUserDetails()).thenReturn(details);
    when(customUser.getSalesforceUserDetails()).thenReturn(details);
  }

  @Test
  public void getUserProfileUsesDefaultChatterWhenNoInstanceUrl() {
    assertSame(profile, new SalesforceProfileMapper().getUserProfile(salesforce));
    verify(salesforce, never()).chatterOperations(INSTANCE_URL);
  }

  @Test
  public void getUserProfileUsesInstanceUrlWhenConfigured() {
    assertSame(profile, new SalesforceProfileMapper(INSTANCE_URL).getUserProfile(salesforce));
    verify(salesforce, never()).chatterOperations();
  }

  @Test
  public void emptyInstanceUrlIsTreatedAsNotConfigured() {
    new SalesforceProfileMapper("").getUserProfile(salesforce);
    verify(defaultChatter).getUserProfile();
  }

  @Test
  public void getUserDetailsUsesDefaultUserOperationsForProduction() {
    assertSame(
        details, new SalesforceProfileMapper(INSTANCE_URL, false).getUserDetails(salesforce));
    verify(defaultUser).getSalesforceUserDetails();
  }

  @Test
  public void getUserDetailsUsesSandboxGateway() {
    assertSame(details, new SalesforceProfileMapper(INSTANCE_URL, true).getUserDetails(salesforce));
    assertSame(details, new SalesforceProfileMapper(true).getUserDetails(salesforce));
    verify(sandboxUser, times(2)).getSalesforceUserDetails();
    verify(salesforce, never()).userOperations();
  }

  @Test
  public void sandboxFalseOnlyConstructorUsesDefaultUserOperations() {
    new SalesforceProfileMapper(false).getUserDetails(salesforce);
    verify(defaultUser).getSalesforceUserDetails();
  }

  @SuppressWarnings("deprecation")
  @Test
  public void deprecatedConstructorUsesGivenGateway() {
    assertSame(
        details,
        new SalesforceProfileMapper(INSTANCE_URL, CUSTOM_GATEWAY).getUserDetails(salesforce));
    verify(customUser).getSalesforceUserDetails();
  }

  @Test
  public void updateStatusUsesDefaultChatterWhenNoInstanceUrl() {
    new SalesforceProfileMapper().updateStatus(salesforce, "hello");
    verify(defaultChatter).updateStatus("hello");
  }

  @Test
  public void updateStatusUsesInstanceUrlWhenConfigured() {
    new SalesforceProfileMapper(INSTANCE_URL).updateStatus(salesforce, "hello");
    verify(instanceChatter).updateStatus("hello");
  }

  @Test
  public void testReturnsTrueWhenProfileIsReadable() {
    assertTrue(new SalesforceProfileMapper().test(salesforce));
    assertTrue(new SalesforceProfileMapper(INSTANCE_URL).test(salesforce));
  }

  @Test
  public void testReturnsFalseWhenProfileCallFails() {
    when(defaultChatter.getUserProfile()).thenThrow(new IllegalStateException("denied"));
    when(instanceChatter.getUserProfile()).thenThrow(new IllegalStateException("denied"));
    assertFalse(new SalesforceProfileMapper().test(salesforce));
    assertFalse(new SalesforceProfileMapper(INSTANCE_URL).test(salesforce));
  }
}
