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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for {@link Pkce}.
 *
 * @author Jared Ottley
 */
public class PkceTest {

  @Test
  public void challengeMatchesTheRfc7636AppendixBVector() {
    assertEquals(
        "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM",
        Pkce.codeChallenge("dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"));
  }

  @Test
  public void verifierIsAnUnreservedStringOfValidLength() {
    String verifier = Pkce.generateCodeVerifier();

    assertEquals(86, verifier.length());
    assertTrue(verifier, verifier.matches("[A-Za-z0-9\\-._~]{43,128}"));
  }

  @Test
  public void verifiersDiffer() {
    assertNotEquals(Pkce.generateCodeVerifier(), Pkce.generateCodeVerifier());
  }

  @Test
  public void challengeIsUnpaddedBase64UrlOfAFixedLength() {
    String challenge = Pkce.codeChallenge(Pkce.generateCodeVerifier());

    assertEquals(43, challenge.length());
    assertTrue(challenge, challenge.matches("[A-Za-z0-9\\-_]+"));
  }

  @Test
  public void aVerifierThatBreaksTheRulesIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> Pkce.codeChallenge(null));
    assertThrows(IllegalArgumentException.class, () -> Pkce.codeChallenge(""));
    // shorter than 43 characters
    assertThrows(IllegalArgumentException.class, () -> Pkce.codeChallenge("abc"));
    // longer than 128 characters
    assertThrows(IllegalArgumentException.class, () -> Pkce.codeChallenge("a".repeat(129)));
    // a character outside A-Z a-z 0-9 - . _ ~
    assertThrows(IllegalArgumentException.class, () -> Pkce.codeChallenge("a".repeat(42) + "+"));
  }
}
