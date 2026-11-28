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
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Describes contextual information about the current canvas app environment.
 *
 * @param displayLocation where in the application the canvas app is shown
 * @param locationUrl the URL of the current location
 * @param sublocation the sublocation the app was called from, on mobile devices
 * @param referer the referer URL of the current request
 * @param uiTheme the UI theme of the environment
 * @param dimensions the dimensions of the environment
 * @param version the system version of the environment
 * @param recordContext the record the app is shown on, if any
 * @param parameters the parameters passed to the app; never null
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CanvasEnvironmentContext(
    String displayLocation,
    String locationUrl,
    String sublocation,
    String referer,
    String uiTheme,
    Dimensions dimensions,
    Version version,
    @JsonProperty("record") CanvasRecordContext recordContext,
    Map<String, Object> parameters) {
  /** Copies the parameters map, using an empty one when null. */
  public CanvasEnvironmentContext {
    parameters =
        (parameters == null)
            ? Map.of()
            : java.util.Collections.unmodifiableMap(new java.util.LinkedHashMap<>(parameters));
  }

  /**
   * Describes the dimensions of the current environment.
   *
   * @param clientHeight the client height
   * @param clientWidth the client width
   * @param height the height
   * @param width the width
   * @param maxHeight the maximum height
   * @param maxWidth the maximum width
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Dimensions(
      String clientHeight,
      String clientWidth,
      String height,
      String width,
      String maxHeight,
      String maxWidth) {}

  /**
   * Describes the version of the current environment.
   *
   * @param api the API version
   * @param season the release season, for example {@code WINTER}
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Version(String api, String season) {}
}
