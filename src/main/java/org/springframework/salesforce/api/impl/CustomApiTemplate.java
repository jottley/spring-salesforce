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
package org.springframework.salesforce.api.impl;

import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.salesforce.api.CustomApiOperations;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.web.client.RestTemplate;

/**
 * Default implementation of {@link CustomApiOperations}, for calling Apex based custom REST APIs.
 * Requests go to {@code {instanceUrl}/services/apexrest{uriPath}}, which has no API version
 * segment.
 *
 * @author Sanchit Agarwal
 * @author Jared Ottley
 */
public class CustomApiTemplate extends AbstractSalesForceOperations<Salesforce>
    implements CustomApiOperations {

  /** The shared rest template used for requests. */
  private final RestTemplate restTemplate;

  /**
   * Creates the operations instance.
   *
   * @param api the {@link Salesforce} instance this operations object belongs to
   * @param restTemplate the shared rest template used for requests
   */
  public CustomApiTemplate(Salesforce api, RestTemplate restTemplate) {
    super(api);
    this.restTemplate = restTemplate;
  }

  /**
   * Builds the full Apex REST URL for a path.
   *
   * @param uriPath the path of the Apex REST resource, starting with {@code /}
   * @return the instance URL followed by {@code /services/apexrest} and the path
   * @throws IllegalStateException if the API has no instance URL
   */
  protected @NonNull String createUriPath(@NonNull String uriPath) {
    String instanceUrl = this.api.getInstanceUrl();
    if (instanceUrl == null) {
      throw new IllegalStateException("Instance URL is not available from API");
    }

    String result = instanceUrl + "/services/apexrest" + uriPath;
    return Objects.requireNonNull(result, "Failed to create valid URI path");
  }

  /**
   * Sends a POST to an Apex REST resource.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource
   * @param request the request body
   * @param responseType the type to convert the response to
   * @return the converted response body
   */
  @Override
  public <T> T postForApexObject(
      @NonNull String uriPath, Object request, @NonNull Class<T> responseType) {
    requireAuthorization();
    return this.restTemplate.postForObject(this.createUriPath(uriPath), request, responseType);
  }

  /**
   * Sends a POST to an Apex REST resource, expanding URI variables.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource
   * @param request the request body
   * @param responseType the type to convert the response to
   * @param uriVariables the variables to expand in the path
   * @return the converted response body
   */
  @Override
  public <T> T postForApexObject(
      @NonNull String uriPath,
      Object request,
      @NonNull Class<T> responseType,
      @NonNull Map<String, ?> uriVariables) {
    requireAuthorization();
    return this.restTemplate.postForObject(
        this.createUriPath(uriPath), request, responseType, uriVariables);
  }

  /**
   * Sends a GET to an Apex REST resource.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource
   * @param responseType the type to convert the response to
   * @return the converted response body
   */
  @Override
  public <T> T getForApexObject(@NonNull String uriPath, @NonNull Class<T> responseType) {
    requireAuthorization();
    return this.restTemplate.getForObject(this.createUriPath(uriPath), responseType);
  }

  /**
   * Sends a GET to an Apex REST resource, expanding URI variables.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource
   * @param responseType the type to convert the response to
   * @param uriVariables the variables to expand in the path
   * @return the converted response body
   */
  @Override
  public <T> T getForApexObject(
      @NonNull String uriPath,
      @NonNull Class<T> responseType,
      @NonNull Map<String, ?> uriVariables) {
    requireAuthorization();
    return this.restTemplate.getForObject(this.createUriPath(uriPath), responseType, uriVariables);
  }

  /**
   * Sends a PUT to an Apex REST resource.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource
   * @param request the request body
   * @param responseType the type to convert the response to
   * @return the converted response body
   */
  @Override
  public <T> T putForApexObject(
      @NonNull String uriPath, @NonNull Object request, @NonNull Class<T> responseType) {
    requireAuthorization();
    ResponseEntity<T> entity =
        this.restTemplate.exchange(
            this.createUriPath(uriPath), HttpMethod.PUT, new HttpEntity<>(request), responseType);
    return entity.getBody();
  }

  /**
   * Sends a PUT to an Apex REST resource, expanding URI variables.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource
   * @param request the request body
   * @param responseType the type to convert the response to
   * @param uriVariables the variables to expand in the path
   * @return the converted response body
   */
  @Override
  public <T> T putForApexObject(
      @NonNull String uriPath,
      @NonNull Object request,
      @NonNull Class<T> responseType,
      @NonNull Map<String, ?> uriVariables) {
    requireAuthorization();
    ResponseEntity<T> entity =
        this.restTemplate.exchange(
            this.createUriPath(uriPath),
            HttpMethod.PUT,
            new HttpEntity<>(request),
            responseType,
            uriVariables);
    return entity.getBody();
  }

  /**
   * Sends a PATCH to an Apex REST resource.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource
   * @param request the request body
   * @param responseType the type to convert the response to
   * @return the converted response body
   */
  @Override
  public <T> T patchForApexObject(
      @NonNull String uriPath, Object request, @NonNull Class<T> responseType) {
    requireAuthorization();
    return this.restTemplate.patchForObject(this.createUriPath(uriPath), request, responseType);
  }

  /**
   * Sends a PATCH to an Apex REST resource, expanding URI variables.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource
   * @param request the request body
   * @param responseType the type to convert the response to
   * @param uriVariables the variables to expand in the path
   * @return the converted response body
   */
  @Override
  public <T> T patchForApexObject(
      @NonNull String uriPath,
      Object request,
      @NonNull Class<T> responseType,
      @NonNull Map<String, ?> uriVariables) {
    requireAuthorization();
    return this.restTemplate.patchForObject(
        this.createUriPath(uriPath), request, responseType, uriVariables);
  }

  /**
   * Sends a DELETE to an Apex REST resource.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource
   * @param responseType the type to convert the response to
   * @return the converted response body
   */
  @Override
  public <T> T deleteForApexObject(@NonNull String uriPath, @NonNull Class<T> responseType) {
    requireAuthorization();
    ResponseEntity<T> entity =
        this.restTemplate.exchange(
            this.createUriPath(uriPath), HttpMethod.DELETE, HttpEntity.EMPTY, responseType);
    return entity.getBody();
  }

  /**
   * Sends a DELETE to an Apex REST resource, expanding URI variables.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource
   * @param responseType the type to convert the response to
   * @param uriVariables the variables to expand in the path
   * @return the converted response body
   */
  @Override
  public <T> T deleteForApexObject(
      @NonNull String uriPath,
      @NonNull Class<T> responseType,
      @NonNull Map<String, ?> uriVariables) {
    requireAuthorization();
    ResponseEntity<T> entity =
        this.restTemplate.exchange(
            this.createUriPath(uriPath),
            HttpMethod.DELETE,
            HttpEntity.EMPTY,
            responseType,
            uriVariables);
    return entity.getBody();
  }

  /**
   * Executes a request of any method against an Apex REST resource.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource
   * @param method the HTTP method
   * @param request the request entity (headers and body); may be {@code null}
   * @param responseType the type to convert the response to
   * @return the full response entity
   */
  @Override
  public <T> ResponseEntity<T> executeApexApi(
      @NonNull String uriPath,
      @NonNull HttpMethod method,
      HttpEntity<?> request,
      @NonNull Class<T> responseType) {
    requireAuthorization();
    return this.restTemplate.exchange(this.createUriPath(uriPath), method, request, responseType);
  }

  /**
   * Executes a request of any method against an Apex REST resource, expanding URI variables.
   *
   * @param <T> the response type
   * @param uriPath the path of the Apex REST resource
   * @param method the HTTP method
   * @param request the request entity (headers and body); may be {@code null}
   * @param responseType the type to convert the response to
   * @param uriVariables the variables to expand in the path
   * @return the full response entity
   */
  @Override
  public <T> ResponseEntity<T> executeApexApi(
      @NonNull String uriPath,
      @NonNull HttpMethod method,
      HttpEntity<?> request,
      @NonNull Class<T> responseType,
      @NonNull Map<String, ?> uriVariables) {
    requireAuthorization();
    return this.restTemplate.exchange(
        this.createUriPath(uriPath), method, request, responseType, uriVariables);
  }
}
