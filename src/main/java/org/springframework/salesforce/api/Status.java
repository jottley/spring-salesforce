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
package org.springframework.salesforce.api;

import java.util.List;
import java.util.Map;

/**
 * The {@code body} of a Chatter user status (its text and the segments the text is made of), as
 * returned by the Chatter user status resource, which Salesforce retired in API version 25.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class Status {

  /** The status text. */
  private String text;

  /** The segments of the status text. */
  private List<Map<String, String>> segments;

  /**
   * Creates a status.
   *
   * @param text the status text
   */
  public Status(String text) {
    this.text = text;
  }

  /**
   * Returns the status text.
   *
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the status text.
   *
   * @param text the text
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * Returns the segments of the status text.
   *
   * @return the segments
   */
  public List<Map<String, String>> getSegments() {
    return segments;
  }

  /**
   * Sets the segments of the status text.
   *
   * @param segments the segments
   */
  public void setSegments(List<Map<String, String>> segments) {
    this.segments = segments;
  }

  /**
   * Returns a string showing the text and segments.
   *
   * @return a description of this status
   */
  @Override
  public String toString() {
    return "Status{" + "text='" + text + '\'' + ", segments=" + segments + '}';
  }
}
