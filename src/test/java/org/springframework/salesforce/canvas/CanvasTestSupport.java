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
package org.springframework.salesforce.canvas;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.core.io.ClassPathResource;

/**
 * Builds signed requests the way Salesforce does, so the tests can exercise {@link SignedRequest}
 * without a real org: {@code base64(HMAC-SHA256(encodedEnvelope, secret)) + "." +
 * base64(envelopeJson)}, both in the standard Base64 alphabet with padding.
 *
 * @author Jared Ottley
 */
public final class CanvasTestSupport {

  /** The consumer secret used to sign every test request. */
  public static final String SECRET = "test-consumer-secret";

  private CanvasTestSupport() {
    // static helpers only
  }

  /**
   * Reads the canvas request fixture, which uses the wire format Salesforce sends.
   *
   * @return the JSON text of {@code canvas-request.json}
   */
  public static String fixtureJson() {
    try {
      return new ClassPathResource("canvas-request.json")
          .getContentAsString(StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot read the canvas-request.json fixture", e);
    }
  }

  /**
   * Signs a JSON envelope with {@link #SECRET}.
   *
   * @param json the envelope JSON
   * @return the signed request string
   */
  public static String sign(String json) {
    return sign(json, SECRET);
  }

  /**
   * Signs a JSON envelope.
   *
   * @param json the envelope JSON
   * @param secret the consumer secret to sign with
   * @return the signed request string
   */
  public static String sign(String json, String secret) {
    String encodedEnvelope =
        Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    return hmac(encodedEnvelope, secret) + "." + encodedEnvelope;
  }

  /**
   * Computes the signature part for an already encoded envelope.
   *
   * @param encodedEnvelope the encoded envelope text, exactly as it appears in the request
   * @param secret the consumer secret
   * @return the Base64 signature
   */
  public static String hmac(String encodedEnvelope, String secret) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      return Base64.getEncoder()
          .encodeToString(mac.doFinal(encodedEnvelope.getBytes(StandardCharsets.UTF_8)));
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException(e);
    }
  }
}
