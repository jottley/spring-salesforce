/**
 * Copyright (c) 2011, salesforce.com, inc. All rights reserved.
 *
 * <p>Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * <p>Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * <p>Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * <p>Neither the name of salesforce.com, inc. nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.springframework.salesforce.canvas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Verifies and decodes a Salesforce Canvas signed request.
 *
 * <p>A signed request is {@code base64(HMAC-SHA256(encodedEnvelope, consumerSecret))}, a period,
 * and {@code base64(envelopeJson)}. The signature is checked first, over the encoded text exactly
 * as received, and the envelope is only read once it is known to come from Salesforce. The
 * algorithm is always HMAC-SHA256: an envelope that names a different one is rejected rather than
 * trusted to pick its own.
 *
 * <p>There are two entry points: one returns the envelope as a {@link CanvasRequest}, the other as
 * a JSON string.
 */
public final class SignedRequest {

  /** The only algorithm Salesforce signs with, and the only one accepted. */
  private static final String ALGORITHM = "HmacSHA256";

  /** Shared because an {@link ObjectMapper} is thread safe once configured. */
  private static final ObjectMapper MAPPER = new ObjectMapper();

  /** The envelope as a generic map, used to hand back JSON without the typed model. */
  private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

  /** Not instantiable: all methods are static. */
  private SignedRequest() {
    // private constructor to prevent instantiation
  }

  /**
   * Verifies the signature and returns the envelope as a {@link CanvasRequest}.
   *
   * @param input the signed request, {@code signature.envelope}
   * @param secret the canvas app's consumer secret
   * @return the decoded request
   * @throws SecurityException if the input is malformed, not signed with the secret, tampered with,
   *     or names an algorithm other than HMAC-SHA256
   * @throws IllegalArgumentException if the secret is null or blank
   */
  public static CanvasRequest verifyAndDecode(String input, String secret)
      throws SecurityException {
    String json = verifiedEnvelope(input, secret);
    try {
      CanvasRequest request = MAPPER.readValue(json, CanvasRequest.class);
      requireSupportedAlgorithm(request.algorithm());
      return request;
    } catch (IOException e) {
      throw new SecurityException(
          String.format(
              "Error [%s] deserializing JSON to Object [%s]",
              safeMessage(e), CanvasRequest.class.getName()),
          e);
    }
  }

  /**
   * Verifies the signature and returns the envelope as a JSON string.
   *
   * @param input the signed request, {@code signature.envelope}
   * @param secret the canvas app's consumer secret
   * @return the envelope JSON, re-serialized (so key order and spacing may differ from what
   *     Salesforce sent)
   * @throws SecurityException if the input is malformed, not signed with the secret, tampered with,
   *     or names an algorithm other than HMAC-SHA256
   * @throws IllegalArgumentException if the secret is null or blank
   */
  public static String verifyAndDecodeAsJson(String input, String secret) throws SecurityException {
    String json = verifiedEnvelope(input, secret);
    try {
      Map<String, Object> envelope = MAPPER.readValue(json, MAP_TYPE);
      Object algorithm = envelope.get("algorithm");
      requireSupportedAlgorithm(algorithm == null ? null : algorithm.toString());
      return MAPPER.writeValueAsString(envelope);
    } catch (IOException e) {
      throw new SecurityException(
          String.format("Error [%s] deserializing JSON to a Map", safeMessage(e)), e);
    }
  }

  /**
   * Splits the input, checks the signature and returns the decoded envelope text. Nothing in the
   * envelope is interpreted before this has succeeded.
   *
   * @param input the signed request
   * @param secret the consumer secret
   * @return the envelope JSON
   */
  private static String verifiedEnvelope(String input, String secret) {
    if (secret == null || secret.isBlank()) {
      throw new IllegalArgumentException(
          "secret is null, did you set your environment variable CANVAS_CONSUMER_SECRET?");
    }
    if (input == null || input.indexOf('.') <= 0) {
      // The input is not echoed: it carries the OAuth token, only Base64 encoded.
      throw new SecurityException("The input doesn't look like a signed request");
    }

    String[] parts = input.split("[.]", 2);
    String encodedSignature = parts[0];
    String encodedEnvelope = parts[1];

    byte[] signature = decode(encodedSignature, "signature");
    byte[] envelope = decode(encodedEnvelope, "envelope");

    // The signature covers the encoded text as received, not the decoded bytes.
    byte[] expected = hmac(secret, encodedEnvelope);
    // isEqual compares in constant time, so timing reveals nothing about the signature.
    if (!MessageDigest.isEqual(expected, signature)) {
      throw new SecurityException("Warning: Request was tampered with");
    }
    return new String(envelope, StandardCharsets.UTF_8);
  }

  /**
   * Describes a parse failure without quoting the envelope. Jackson's full message can include a
   * snippet of the source text, which here holds the OAuth token.
   *
   * @param e the failure
   * @return the failure's own description, without location or source text
   */
  private static String safeMessage(IOException e) {
    return e instanceof JsonProcessingException jpe
        ? jpe.getOriginalMessage()
        : e.getClass().getSimpleName();
  }

  /**
   * Decodes a standard-alphabet Base64 part, turning a malformed one into a {@link
   * SecurityException} like every other bad input.
   *
   * @param value the encoded text
   * @param what which part this is, for the message
   * @return the decoded bytes
   */
  private static byte[] decode(String value, String what) {
    try {
      return Base64.getDecoder().decode(value);
    } catch (IllegalArgumentException e) {
      throw new SecurityException(
          String.format("The signed request %s is not valid Base64", what), e);
    }
  }

  /**
   * Computes HMAC-SHA256 of the encoded envelope text.
   *
   * @param secret the consumer secret
   * @param encodedEnvelope the envelope text exactly as received
   * @return the raw HMAC
   */
  private static byte[] hmac(String secret, String encodedEnvelope) {
    try {
      Mac mac = Mac.getInstance(ALGORITHM);
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));
      return mac.doFinal(encodedEnvelope.getBytes(StandardCharsets.US_ASCII));
    } catch (GeneralSecurityException e) {
      throw new SecurityException(
          String.format("Problem computing %s: %s", ALGORITHM, e.getMessage()), e);
    }
  }

  /**
   * Accepts a missing algorithm (Salesforce's default) or HMAC-SHA256 in any case, and rejects
   * anything else: the signature has already been checked as HMAC-SHA256, so an envelope that
   * claims otherwise is not what Salesforce sent.
   *
   * @param algorithm the algorithm named in the envelope, or null
   */
  private static void requireSupportedAlgorithm(String algorithm) {
    if (algorithm != null && !ALGORITHM.equalsIgnoreCase(algorithm)) {
      throw new SecurityException(
          String.format("Unsupported algorithm [%s]; only HMACSHA256 is accepted", algorithm));
    }
  }
}
