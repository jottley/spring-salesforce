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

/**
 * Describes all contextual information related to canvas applications.
 *
 * <p>Some information within the context depends on what oauth scopes are allowed on the canvas
 * application. Some/all items may be null if the oauth scope is not set accordingly.
 *
 * @param userContext the current user
 * @param orgContext the current organization
 * @param linkContext links to resources within Salesforce
 * @param envContext the current environment (page, location, and so on)
 * @param applicationContext the canvas application
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CanvasContext(
    @JsonProperty("user") CanvasUserContext userContext,
    @JsonProperty("organization") CanvasOrganizationContext orgContext,
    @JsonProperty("links") CanvasLinkContext linkContext,
    @JsonProperty("environment") CanvasEnvironmentContext envContext,
    @JsonProperty("application") CanvasApplicationContext applicationContext) {

  // Keeping this toString from the original implementation for debugging purposes.
  @Override
  public String toString() {
    return String.format(
        """
                             Canvas Context:
                             \tUser Context:
                             \t\t%s
                             \tOrg Context:
                             \t\t%s
                             \tEnvironment Context:
                             \t\t%s
                             \tLink Context:
                             \t\t%s
                             \tApplication Context:
                             \t\t%s
                             """,
        null != userContext ? userContext.toString() : "null",
        null != orgContext ? orgContext.toString() : "null",
        null != envContext ? envContext.toString() : "null",
        null != linkContext ? linkContext.toString() : "null",
        null != applicationContext ? applicationContext.toString() : "null");
  }
}
