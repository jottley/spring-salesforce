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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Helpers for Proof Key for Code Exchange (PKCE, <a
 * href="https://www.rfc-editor.org/rfc/rfc7636">RFC 7636</a>) using the {@code S256} method, which
 * is the one Salesforce supports.
 *
 * <p>Generate a verifier, keep it (for example in the user's session), send its challenge when you
 * build the authorize URL, and send the verifier when you exchange the code:
 *
 * <pre>{@code
 * String verifier = Pkce.generateCodeVerifier();
 * String url = oauth.buildAuthorizeUrl(redirectUri, scope, state, Pkce.codeChallenge(verifier));
 * // ... the user signs in and is redirected back with a code ...
 * AccessGrant grant = oauth.exchangeForAccess(code, redirectUri, verifier);
 * }</pre>
 *
 * @author Jared Ottley
 */
public final class Pkce {

  /** The number of random bytes in a verifier; their base64url form is 86 characters. */
  private static final int VERIFIER_BYTES = 64;

  /** The shortest verifier RFC 7636 allows. */
  private static final int MIN_VERIFIER_LENGTH = 43;

  /** The longest verifier RFC 7636 allows. */
  private static final int MAX_VERIFIER_LENGTH = 128;

  /** The source of randomness for verifiers. */
  private static final SecureRandom RANDOM = new SecureRandom();

  /** Not instantiable. */
  private Pkce() {
    // NOOP
  }

  /**
   * Generates a new code verifier: 64 random bytes from a {@link SecureRandom}, base64url-encoded
   * without padding (86 characters, within the 43 to 128 that RFC 7636 allows).
   *
   * @return a new, random code verifier
   */
  public static String generateCodeVerifier() {
    byte[] bytes = new byte[VERIFIER_BYTES];
    RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  /**
   * Computes the {@code S256} code challenge for a verifier: the base64url encoding, without
   * padding, of the SHA-256 hash of the verifier.
   *
   * @param codeVerifier the code verifier, 43 to 128 characters from {@code A-Z a-z 0-9 - . _ ~}
   * @return the code challenge
   * @throws IllegalArgumentException if the verifier is null, has an invalid length or contains a
   *     character that RFC 7636 does not allow
   */
  public static String codeChallenge(String codeVerifier) {
    if (codeVerifier == null
        || codeVerifier.length() < MIN_VERIFIER_LENGTH
        || codeVerifier.length() > MAX_VERIFIER_LENGTH
        || !codeVerifier.matches("[A-Za-z0-9\\-._~]+")) {
      throw new IllegalArgumentException(
          "A code verifier must be 43 to 128 characters from A-Z a-z 0-9 - . _ ~");
    }
    try {
      byte[] hash =
          MessageDigest.getInstance("SHA-256")
              .digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      // Every Java platform is required to provide SHA-256
      throw new IllegalStateException("SHA-256 is not available", e);
    }
  }
}
