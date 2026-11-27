/**
 * Copyright (C) 2021-2026 https://github.com/jottley/spring-social-salesforce
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

import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Defines operations for calling custom apex exposed rest apis.
 *
 * <p>Custom Apex REST resources live at {@code {instanceUrl}/services/apexrest{uriPath}}; unlike
 * the standard endpoints no API version is included in the path.
 *
 * @author Sanchit Agarwal
 */
public interface CustomApiOperations {

  /**
   * Calls a custom Apex REST API with POST and converts the response body to the given type. The
   * request goes to {@code {instanceUrl}/services/apexrest{uriPath}}.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource, beginning with {@code /}, as defined by the
   *     Apex class's {@code urlMapping}
   * @param request the object to send as the request body; may be null
   * @param responseType the type to convert the response body to
   * @return the converted response body
   * @throws IllegalStateException if no instance URL is available from the API
   */
  <T> T postForApexObject(@NonNull String uriPath, Object request, @NonNull Class<T> responseType);

  /**
   * Calls a custom Apex REST API with POST and converts the response body to the given type. The
   * request goes to {@code {instanceUrl}/services/apexrest{uriPath}}.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource, beginning with {@code /}, as defined by the
   *     Apex class's {@code urlMapping}
   * @param request the object to send as the request body; may be null
   * @param responseType the type to convert the response body to
   * @param uriVariables values for any URI template variables in {@code uriPath}
   * @return the converted response body
   * @throws IllegalStateException if no instance URL is available from the API
   */
  <T> T postForApexObject(
      @NonNull String uriPath,
      Object request,
      @NonNull Class<T> responseType,
      @NonNull Map<String, ?> uriVariables);

  /**
   * Calls a custom Apex REST API with GET and converts the response body to the given type. The
   * request goes to {@code {instanceUrl}/services/apexrest{uriPath}}.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource, beginning with {@code /}, as defined by the
   *     Apex class's {@code urlMapping}
   * @param responseType the type to convert the response body to
   * @return the converted response body
   * @throws IllegalStateException if no instance URL is available from the API
   */
  <T> T getForApexObject(@NonNull String uriPath, @NonNull Class<T> responseType);

  /**
   * Calls a custom Apex REST API with GET and converts the response body to the given type. The
   * request goes to {@code {instanceUrl}/services/apexrest{uriPath}}.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource, beginning with {@code /}, as defined by the
   *     Apex class's {@code urlMapping}
   * @param responseType the type to convert the response body to
   * @param uriVariables values for any URI template variables in {@code uriPath}
   * @return the converted response body
   * @throws IllegalStateException if no instance URL is available from the API
   */
  <T> T getForApexObject(
      @NonNull String uriPath,
      @NonNull Class<T> responseType,
      @NonNull Map<String, ?> uriVariables);

  /**
   * Calls a custom Apex REST API with PUT and converts the response body to the given type. The
   * request goes to {@code {instanceUrl}/services/apexrest{uriPath}}.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource, beginning with {@code /}, as defined by the
   *     Apex class's {@code urlMapping}
   * @param request the object to send as the request body
   * @param responseType the type to convert the response body to
   * @return the converted response body
   * @throws IllegalStateException if no instance URL is available from the API
   */
  <T> T putForApexObject(
      @NonNull String uriPath, @NonNull Object request, @NonNull Class<T> responseType);

  /**
   * Calls a custom Apex REST API with PUT and converts the response body to the given type. The
   * request goes to {@code {instanceUrl}/services/apexrest{uriPath}}.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource, beginning with {@code /}, as defined by the
   *     Apex class's {@code urlMapping}
   * @param request the object to send as the request body
   * @param responseType the type to convert the response body to
   * @param uriVariables values for any URI template variables in {@code uriPath}
   * @return the converted response body
   * @throws IllegalStateException if no instance URL is available from the API
   */
  <T> T putForApexObject(
      @NonNull String uriPath,
      @NonNull Object request,
      @NonNull Class<T> responseType,
      @NonNull Map<String, ?> uriVariables);

  /**
   * Calls a custom Apex REST API with PATCH and converts the response body to the given type. The
   * request goes to {@code {instanceUrl}/services/apexrest{uriPath}}.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource, beginning with {@code /}, as defined by the
   *     Apex class's {@code urlMapping}
   * @param request the object to send as the request body; may be null
   * @param responseType the type to convert the response body to
   * @return the converted response body
   * @throws IllegalStateException if no instance URL is available from the API
   */
  <T> T patchForApexObject(@NonNull String uriPath, Object request, @NonNull Class<T> responseType);

  /**
   * Calls a custom Apex REST API with PATCH and converts the response body to the given type. The
   * request goes to {@code {instanceUrl}/services/apexrest{uriPath}}.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource, beginning with {@code /}, as defined by the
   *     Apex class's {@code urlMapping}
   * @param request the object to send as the request body; may be null
   * @param responseType the type to convert the response body to
   * @param uriVariables values for any URI template variables in {@code uriPath}
   * @return the converted response body
   * @throws IllegalStateException if no instance URL is available from the API
   */
  <T> T patchForApexObject(
      @NonNull String uriPath,
      Object request,
      @NonNull Class<T> responseType,
      @NonNull Map<String, ?> uriVariables);

  /**
   * Calls a custom Apex REST API with DELETE and converts the response body to the given type. The
   * request goes to {@code {instanceUrl}/services/apexrest{uriPath}}.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource, beginning with {@code /}, as defined by the
   *     Apex class's {@code urlMapping}
   * @param responseType the type to convert the response body to
   * @return the converted response body
   * @throws IllegalStateException if no instance URL is available from the API
   */
  <T> T deleteForApexObject(@NonNull String uriPath, @NonNull Class<T> responseType);

  /**
   * Calls a custom Apex REST API with DELETE and converts the response body to the given type. The
   * request goes to {@code {instanceUrl}/services/apexrest{uriPath}}.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource, beginning with {@code /}, as defined by the
   *     Apex class's {@code urlMapping}
   * @param responseType the type to convert the response body to
   * @param uriVariablesMap values for any URI template variables in {@code uriPath}
   * @return the converted response body
   * @throws IllegalStateException if no instance URL is available from the API
   */
  <T> T deleteForApexObject(
      @NonNull String uriPath,
      @NonNull Class<T> responseType,
      @NonNull Map<String, ?> uriVariablesMap);

  /**
   * Calls a custom Apex REST API with the given HTTP method and returns the full response, so that
   * the headers and status as well as the body are available. The request goes to {@code
   * {instanceUrl}/services/apexrest{uriPath}}.
   *
   * @param <T> the response body type
   * @param uriPath the path of the Apex REST resource, beginning with {@code /}, as defined by the
   *     Apex class's {@code urlMapping}
   * @param method the HTTP method to use
   * @param request the request headers and body; may be null
   * @param responseType the type to convert the response body to
   * @param uriVariables values for any URI template variables in {@code uriPath}
   * @return the response entity
   * @throws IllegalStateException if no instance URL is available from the API
   */
  <T> ResponseEntity<T> executeApexApi(
      @NonNull String uriPath,
      @NonNull HttpMethod method,
      HttpEntity<?> request,
      @NonNull Class<T> responseType,
      @NonNull Map<String, ?> uriVariables);

  /**
   * Calls a custom Apex REST API with the given HTTP method and returns the full response, so that
   * the headers and status as well as the body are available. The request goes to {@code
   * {instanceUrl}/services/apexrest{uriPath}}.
   *
   * @param <T> the response body type
   * @param uriPath the path of the Apex REST resource, beginning with {@code /}, as defined by the
   *     Apex class's {@code urlMapping}
   * @param method the HTTP method to use
   * @param request the request headers and body; may be null
   * @param responseType the type to convert the response body to
   * @return the response entity
   * @throws IllegalStateException if no instance URL is available from the API
   */
  <T> ResponseEntity<T> executeApexApi(
      @NonNull String uriPath,
      @NonNull HttpMethod method,
      HttpEntity<?> request,
      @NonNull Class<T> responseType);
}
