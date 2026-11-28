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
package org.springframework.salesforce.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import org.springframework.salesforce.api.Salesforce;

/**
 * Simple caching wrapper for SalesforceFactory.
 *
 * <p>Keeps one {@link Salesforce} instance per username. If the password or security token supplied
 * for a cached username changes, the cached instance is replaced by a new login rather than reused.
 * The password and token are not retained; only a digest is kept to detect the change.
 *
 * <p>A failed login is never cached, and a failed (or {@code null}) login for a username also
 * discards that username's existing cached instance. Logins run while holding the cache lock, so
 * they are serialized. Salesforce sessions can still expire while cached, so call {@link
 * #evict(String)} or {@link #clear()} to force a new login.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SimpleCachingSalesforceFactory implements SalesforceFactory {

  /**
   * A cached {@link Salesforce} instance together with the digest of the credentials that produced
   * it.
   *
   * @param credentialDigest the digest of the password and security token used to log in
   * @param salesforce the cached instance
   */
  private record CacheEntry(String credentialDigest, Salesforce salesforce) {}

  /** The factory used when no valid cached instance exists. */
  private final SalesforceFactory delegate;

  /** Cached entries keyed by username; guarded by synchronizing on itself. */
  private final Map<String, CacheEntry> cache = new HashMap<>();

  /**
   * Creates a caching factory that logs in through the given delegate.
   *
   * @param delegate the factory used when no valid cached instance exists
   */
  public SimpleCachingSalesforceFactory(SalesforceFactory delegate) {
    this.delegate = delegate;
  }

  /**
   * Returns the cached instance for the username if the credentials are unchanged; otherwise logs
   * in through the delegate and caches the result (unless it is {@code null}).
   *
   * @param username the Salesforce username
   * @param password the user's password
   * @param securityToken the user's security token; may be {@code null}
   * @return the cached or newly created instance, or {@code null} if the delegate returned {@code
   *     null} (which is not cached)
   * @throws RuntimeException whatever the delegate throws; nothing is cached in that case
   */
  @Override
  public Salesforce create(String username, String password, String securityToken) {
    String digest = digest(password, securityToken);
    synchronized (this.cache) {
      CacheEntry entry = this.cache.get(username);
      if (entry != null && entry.credentialDigest().equals(digest)) {
        return entry.salesforce();
      }

      // Drop any stale entry first so a failing or null login is not served from cache later.
      this.cache.remove(username);
      Salesforce salesforce = this.delegate.create(username, password, securityToken);
      if (salesforce != null) {
        this.cache.put(username, new CacheEntry(digest, salesforce));
      }
      return salesforce;
    }
  }

  /**
   * Removes the cached instance for the given username so the next {@link #create} logs in again.
   *
   * @param username the username to evict
   */
  public void evict(String username) {
    synchronized (this.cache) {
      this.cache.remove(username);
    }
  }

  /** Removes all cached instances. */
  public void clear() {
    synchronized (this.cache) {
      this.cache.clear();
    }
  }

  /**
   * Computes a SHA-256 digest of the password and security token so the cache can detect changed
   * credentials without storing them.
   *
   * @param password the user's password; may be {@code null}
   * @param securityToken the user's security token; may be {@code null}
   * @return the digest as a hex string
   * @throws IllegalStateException if SHA-256 is unavailable
   */
  private static String digest(String password, String securityToken) {
    try {
      MessageDigest sha = MessageDigest.getInstance("SHA-256");
      // Length-prefix the password so ("ab","c") and ("a","bc") produce different digests.
      String pw = password == null ? "" : password;
      String token = securityToken == null ? "" : securityToken;
      sha.update((pw.length() + ":" + pw + token).getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(sha.digest());
    } catch (NoSuchAlgorithmException e) {
      // SHA-256 is required to be available on every Java platform
      throw new IllegalStateException(e);
    }
  }
}
