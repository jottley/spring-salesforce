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
package org.springframework.salesforce.client;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.salesforce.api.Salesforce;

/**
 * @author Jared Ottley
 */
public class SimpleCachingSalesforceFactoryTest {

  private SalesforceFactory delegate;
  private SimpleCachingSalesforceFactory factory;
  private Salesforce alice;
  private Salesforce bob;

  @Before
  public void setUp() {
    delegate = mock(SalesforceFactory.class);
    alice = mock(Salesforce.class);
    bob = mock(Salesforce.class);
    when(delegate.create("alice", "pw", "tok")).thenReturn(alice);
    when(delegate.create("bob", "pw", "tok")).thenReturn(bob);
    factory = new SimpleCachingSalesforceFactory(delegate);
  }

  @Test
  public void createsOncePerUsernameAndReusesTheInstance() {
    assertSame(alice, factory.create("alice", "pw", "tok"));
    assertSame(alice, factory.create("alice", "pw", "tok"));

    verify(delegate, times(1)).create("alice", "pw", "tok");
  }

  @Test
  public void keepsSeparateInstancesPerUsername() {
    assertSame(alice, factory.create("alice", "pw", "tok"));
    assertSame(bob, factory.create("bob", "pw", "tok"));
    assertSame(alice, factory.create("alice", "pw", "tok"));

    verify(delegate, times(1)).create("alice", "pw", "tok");
    verify(delegate, times(1)).create("bob", "pw", "tok");
  }

  @Test
  public void changedPasswordTriggersNewLogin() {
    Salesforce rotated = mock(Salesforce.class);
    when(delegate.create("alice", "new-pw", "tok")).thenReturn(rotated);

    assertSame(alice, factory.create("alice", "pw", "tok"));
    assertSame(rotated, factory.create("alice", "new-pw", "tok"));
    // the replacement is now the cached instance
    assertSame(rotated, factory.create("alice", "new-pw", "tok"));

    verify(delegate, times(1)).create("alice", "new-pw", "tok");
  }

  @Test
  public void changedSecurityTokenTriggersNewLogin() {
    Salesforce rotated = mock(Salesforce.class);
    when(delegate.create("alice", "pw", "new-tok")).thenReturn(rotated);

    factory.create("alice", "pw", "tok");

    assertSame(rotated, factory.create("alice", "pw", "new-tok"));
  }

  @Test
  public void passwordAndTokenBoundaryDoesNotCollide() {
    Salesforce other = mock(Salesforce.class);
    when(delegate.create("dave", "ab", "c")).thenReturn(alice);
    when(delegate.create("dave", "a", "bc")).thenReturn(other);

    assertSame(alice, factory.create("dave", "ab", "c"));
    assertSame(other, factory.create("dave", "a", "bc"));
  }

  @Test
  public void nullPasswordAndTokenAreSupported() {
    when(delegate.create("erin", null, null)).thenReturn(alice);

    assertSame(alice, factory.create("erin", null, null));
    assertSame(alice, factory.create("erin", null, null));

    verify(delegate, times(1)).create("erin", null, null);
  }

  @Test
  public void failedLoginIsNotCachedAndDropsStaleEntry() {
    when(delegate.create("alice", "bad", "tok")).thenThrow(new IllegalStateException("denied"));

    factory.create("alice", "pw", "tok");
    assertThrows(IllegalStateException.class, () -> factory.create("alice", "bad", "tok"));

    // the old entry was dropped, so the original credentials log in again
    factory.create("alice", "pw", "tok");
    verify(delegate, times(2)).create("alice", "pw", "tok");
  }

  @Test
  public void evictForcesNewLoginForThatUserOnly() {
    factory.create("alice", "pw", "tok");
    factory.create("bob", "pw", "tok");

    factory.evict("alice");
    factory.create("alice", "pw", "tok");
    factory.create("bob", "pw", "tok");

    verify(delegate, times(2)).create("alice", "pw", "tok");
    verify(delegate, times(1)).create("bob", "pw", "tok");
  }

  @Test
  public void clearForcesNewLoginForEveryone() {
    factory.create("alice", "pw", "tok");
    factory.create("bob", "pw", "tok");

    factory.clear();
    factory.create("alice", "pw", "tok");
    factory.create("bob", "pw", "tok");

    verify(delegate, times(2)).create("alice", "pw", "tok");
    verify(delegate, times(2)).create("bob", "pw", "tok");
  }

  @Test
  public void nullFromDelegateIsRetriedOnTheNextCall() {
    when(delegate.create("carol", "pw", "tok")).thenReturn(null);

    assertNull(factory.create("carol", "pw", "tok"));
    assertNull(factory.create("carol", "pw", "tok"));

    verify(delegate, times(2)).create("carol", "pw", "tok");
  }
}
