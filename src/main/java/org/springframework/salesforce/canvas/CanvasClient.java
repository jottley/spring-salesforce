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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Describes contextual information about the current canvas client (third party app). This Object,
 * in JS literal notation, needs to ba passed on all Sfdc.canvas.client requests.
 *
 * <p>The tokens are credentials: {@link #toString()} masks them, so logging this record or the
 * {@link CanvasRequest} that holds it does not write them to the log.
 *
 * @param oauthToken the scoped OAuth token for subsequent REST calls
 * @param refreshToken the token used to refresh the session; returned only if the app has the
 *     refresh_token scope, otherwise null
 * @param clientId the client ID of the canvas app (not confirmed to be sent by Salesforce)
 * @param instanceId the unique identifier of this canvas app instance, different for each placement
 *     of the same app
 * @param targetOrigin the origin of the page that hosts the canvas app, used by the in-browser
 *     proxy
 * @param instanceUrl the base URL for REST calls, on the instance this org is pinned to
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CanvasClient(
    String oauthToken,
    String refreshToken,
    String clientId,
    String instanceId,
    String targetOrigin,
    String instanceUrl) {

  /** What {@link #toString()} prints in place of a token. */
  private static final String REDACTED = "[redacted]";

  /**
   * Describes the client without its credentials. The generated record {@code toString} would print
   * the OAuth and refresh tokens, and a request is easily logged by accident, so both are masked.
   * The accessors still return the real values.
   *
   * @return the client, with the tokens replaced by {@code [redacted]}
   */
  @Override
  public String toString() {
    return "CanvasClient[oauthToken="
        + mask(oauthToken)
        + ", refreshToken="
        + mask(refreshToken)
        + ", clientId="
        + clientId
        + ", instanceId="
        + instanceId
        + ", targetOrigin="
        + targetOrigin
        + ", instanceUrl="
        + instanceUrl
        + "]";
  }

  /**
   * Masks a token, keeping the difference between absent and present visible.
   *
   * @param token the token, or null
   * @return {@code null} for a null token, otherwise {@code [redacted]}
   */
  private static String mask(String token) {
    return token == null ? null : REDACTED;
  }
}
