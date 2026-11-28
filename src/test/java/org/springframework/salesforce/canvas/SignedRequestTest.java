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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.salesforce.canvas.CanvasTestSupport.SECRET;
import static org.springframework.salesforce.canvas.CanvasTestSupport.fixtureJson;
import static org.springframework.salesforce.canvas.CanvasTestSupport.sign;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.Test;

/**
 * Tests for {@link SignedRequest}: signature verification and decoding.
 *
 * @author Jared Ottley
 */
public class SignedRequestTest {

  @Test
  public void verifyAndDecodeReturnsTheEnvelope() {
    CanvasRequest request = SignedRequest.verifyAndDecode(sign(fixtureJson()), SECRET);

    assertNotNull(request);
    assertEquals("HMACSHA256", request.algorithm());
    assertEquals(Long.valueOf(1700000000L), request.issuedAt());
    assertEquals("005x00000012Q9PAAU", request.userId());
  }

  @Test
  public void verifyAndDecodeAsJsonReturnsTheEnvelopeAsJson() throws Exception {
    String json = SignedRequest.verifyAndDecodeAsJson(sign(fixtureJson()), SECRET);

    JsonNode tree = new ObjectMapper().readTree(json);
    assertEquals("005x00000012Q9PAAU", tree.get("userId").asText());
    assertEquals("Acme Corp", tree.get("context").get("organization").get("name").asText());
  }

  @Test
  public void aWrongSecretIsRejectedAsTampered() {
    String signed = sign(fixtureJson(), "some-other-secret");

    assertRejected(() -> SignedRequest.verifyAndDecode(signed, SECRET), "tampered");
    assertRejected(() -> SignedRequest.verifyAndDecodeAsJson(signed, SECRET), "tampered");
  }

  @Test
  public void aChangedEnvelopeIsRejectedAsTampered() {
    String[] parts = sign(fixtureJson()).split("\\.", 2);
    String forgedJson = fixtureJson().replace("005x00000012Q9PAAU", "005x00000099ZZZAAA");
    String forgedEnvelope =
        Base64.getEncoder().encodeToString(forgedJson.getBytes(StandardCharsets.UTF_8));
    String forged = parts[0] + "." + forgedEnvelope;

    assertRejected(() -> SignedRequest.verifyAndDecode(forged, SECRET), "tampered");
    assertRejected(() -> SignedRequest.verifyAndDecodeAsJson(forged, SECRET), "tampered");
  }

  @Test
  public void aSignatureOfTheDecodedBytesInsteadOfTheEncodedTextIsRejected() {
    // Salesforce signs the encoded envelope text; a signature over the raw JSON must not verify.
    String envelope =
        Base64.getEncoder().encodeToString(fixtureJson().getBytes(StandardCharsets.UTF_8));
    String wrongSignature = CanvasTestSupport.hmac(fixtureJson(), SECRET);

    assertRejected(
        () -> SignedRequest.verifyAndDecode(wrongSignature + "." + envelope, SECRET), "tampered");
  }

  @Test
  public void inputWithoutADotIsRejected() {
    assertRejected(() -> SignedRequest.verifyAndDecode("nodots", SECRET), "signed request");
    assertRejected(() -> SignedRequest.verifyAndDecodeAsJson("nodots", SECRET), "signed request");
  }

  @Test
  public void inputThatStartsWithADotIsRejected() {
    assertRejected(() -> SignedRequest.verifyAndDecode(".abc", SECRET), "signed request");
  }

  @Test
  public void nullInputIsRejected() {
    assertRejected(() -> SignedRequest.verifyAndDecode(null, SECRET), "signed request");
    assertRejected(() -> SignedRequest.verifyAndDecodeAsJson(null, SECRET), "signed request");
  }

  @Test
  public void anEmptyEnvelopeIsRejected() {
    String signed = CanvasTestSupport.hmac("", SECRET) + ".";

    try {
      SignedRequest.verifyAndDecode(signed, SECRET);
      fail("expected SecurityException");
    } catch (SecurityException expected) {
      // an envelope with no JSON cannot be read
    }
  }

  @Test
  public void anEnvelopeThatIsNotJsonIsRejected() {
    String signed = sign("this is not json");

    assertRejected(() -> SignedRequest.verifyAndDecode(signed, SECRET), "deserializing");
    assertRejected(() -> SignedRequest.verifyAndDecodeAsJson(signed, SECRET), "deserializing");
  }

  @Test
  public void aNullOrBlankSecretIsRejected() {
    String signed = sign(fixtureJson());

    for (String secret : new String[] {null, "", "   "}) {
      try {
        SignedRequest.verifyAndDecode(signed, secret);
        fail("expected IllegalArgumentException for secret [" + secret + "]");
      } catch (IllegalArgumentException expected) {
        assertTrue(expected.getMessage().contains("secret"));
      }
    }
  }

  @Test
  public void aNonPinnedAlgorithmInTheEnvelopeIsRejectedEvenWhenSignedCorrectly() {
    // The signature is always checked as HMAC-SHA256; an envelope may not pick another algorithm.
    String signed = sign(fixtureJson().replace("HMACSHA256", "HMACMD5"));

    assertRejected(() -> SignedRequest.verifyAndDecode(signed, SECRET), "HMACMD5");
    assertRejected(() -> SignedRequest.verifyAndDecodeAsJson(signed, SECRET), "HMACMD5");
  }

  @Test
  public void theAlgorithmNameIsMatchedWithoutRegardToCase() {
    String signed = sign(fixtureJson().replace("HMACSHA256", "HmacSHA256"));

    assertEquals("005x00000012Q9PAAU", SignedRequest.verifyAndDecode(signed, SECRET).userId());
  }

  @Test
  public void aMissingAlgorithmIsAcceptedByBothMethods() throws Exception {
    String signed = sign(fixtureJson().replace("\"algorithm\": \"HMACSHA256\",", ""));

    assertEquals("005x00000012Q9PAAU", SignedRequest.verifyAndDecode(signed, SECRET).userId());
    assertEquals(
        "005x00000012Q9PAAU",
        new ObjectMapper()
            .readTree(SignedRequest.verifyAndDecodeAsJson(signed, SECRET))
            .get("userId")
            .asText());
  }

  @Test
  public void anEnvelopeIsNotReadUntilTheSignatureIsVerified() {
    // Unparseable JSON with a bad signature must fail on the signature, not on the JSON.
    String envelope =
        Base64.getEncoder().encodeToString("not json".getBytes(StandardCharsets.UTF_8));
    String signed = CanvasTestSupport.hmac(envelope, "someone-else") + "." + envelope;

    assertRejected(() -> SignedRequest.verifyAndDecode(signed, SECRET), "tampered");
  }

  @Test
  public void aMalformedBase64EnvelopeIsASecurityException() {
    assertRejected(
        () -> SignedRequest.verifyAndDecode("c2ln.not*base64!", SECRET), "not valid Base64");
    assertRejected(
        () -> SignedRequest.verifyAndDecodeAsJson("c2ln.not*base64!", SECRET), "not valid Base64");
  }

  @Test
  public void aMalformedBase64SignatureIsASecurityException() {
    String envelope = sign(fixtureJson()).split("\\.", 2)[1];

    assertRejected(
        () -> SignedRequest.verifyAndDecode("not*base64!." + envelope, SECRET), "not valid Base64");
  }

  @Test
  public void anUnknownAlgorithmIsRejected() {
    String signed = sign(fixtureJson().replace("HMACSHA256", "HMACNOSUCH"));

    assertRejected(() -> SignedRequest.verifyAndDecode(signed, SECRET), "algorithm");
  }

  @Test
  public void aMissingAlgorithmDefaultsToHmacSha256() {
    String json = fixtureJson().replace("\"algorithm\": \"HMACSHA256\",", "");
    assertTrue("fixture still has an algorithm", !json.contains("algorithm"));

    CanvasRequest request = SignedRequest.verifyAndDecode(sign(json), SECRET);

    assertEquals("005x00000012Q9PAAU", request.userId());
  }

  @Test
  public void anEnvelopeWithNonAsciiTextSurvivesTheRoundTrip() {
    String json = fixtureJson().replace("Jane", "Jörg");

    CanvasRequest request = SignedRequest.verifyAndDecode(sign(json), SECRET);

    assertEquals("Jörg", request.canvasContext().userContext().firstName());
  }

  @Test
  public void errorMessagesNeverQuoteTheInputOrTheEnvelope() {
    // The envelope holds the OAuth token, only Base64 encoded, so it must not reach a log through
    // an exception message.
    String notASignedRequest = "TOKEN-IN-THE-INPUT";
    SecurityException noDot =
        assertThrows(
            SecurityException.class,
            () -> SignedRequest.verifyAndDecode(notASignedRequest, SECRET));
    assertFalse(noDot.getMessage().contains(notASignedRequest));

    String signed = sign("{\"client\": {\"oauthToken\": \"TOKEN-IN-THE-JSON\"} oops");
    SecurityException badJson =
        assertThrows(SecurityException.class, () -> SignedRequest.verifyAndDecode(signed, SECRET));
    assertFalse(badJson.getMessage(), badJson.getMessage().contains("TOKEN-IN-THE-JSON"));
    SecurityException badJsonAsMap =
        assertThrows(
            SecurityException.class, () -> SignedRequest.verifyAndDecodeAsJson(signed, SECRET));
    assertFalse(badJsonAsMap.getMessage().contains("TOKEN-IN-THE-JSON"));
  }

  private static void assertRejected(Runnable call, String messageFragment) {
    try {
      call.run();
      fail("expected SecurityException containing [" + messageFragment + "]");
    } catch (SecurityException expected) {
      assertTrue(
          "message [" + expected.getMessage() + "] should contain [" + messageFragment + "]",
          expected.getMessage().toLowerCase().contains(messageFragment.toLowerCase()));
    }
  }
}
