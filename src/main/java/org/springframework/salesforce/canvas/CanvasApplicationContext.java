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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Describes the canvas application: its identity, URL and options.
 *
 * @param applicationId the ID of the canvas app
 * @param authType how the app is authenticated, for example {@code SIGNED_REQUEST}
 * @param canvasUrl the URL Salesforce loads the canvas app from
 * @param developerName the API name of the canvas app
 * @param name the display name of the canvas app
 * @param namespace the namespace prefix of the canvas app, empty if none
 * @param options the options set on the app, such as {@code HideShare}; never null
 * @param referenceId the ID of the canvas app's reference record
 * @param version the version of the canvas app
 * @param samlInitiationMethod the SAML initiation method for the canvas app
 * @param isInstalledPersonalApp whether the canvas app is installed as a personal app
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CanvasApplicationContext(
    String applicationId,
    String authType,
    String canvasUrl,
    String developerName,
    String name,
    String namespace,
    List<String> options,
    String referenceId,
    String version,
    String samlInitiationMethod,
    boolean isInstalledPersonalApp) {

  /** Copies the options list, using an empty one when null. */
  public CanvasApplicationContext {
    options = (options == null) ? List.of() : List.copyOf(options);
  }
}
