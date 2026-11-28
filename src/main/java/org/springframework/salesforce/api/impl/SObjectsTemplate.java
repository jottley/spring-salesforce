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
package org.springframework.salesforce.api.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.salesforce.api.SObjectDetail;
import org.springframework.salesforce.api.SObjectOperations;
import org.springframework.salesforce.api.SObjectSummary;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

/**
 * Default implementation of {@link SObjectOperations}. Names, ids and field names are checked
 * before use so that they cannot alter the request path.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SObjectsTemplate extends AbstractSalesForceOperations<Salesforce>
    implements SObjectOperations {

  /** The shared rest template used for requests. */
  private final RestTemplate restTemplate;

  /**
   * Creates the operations instance.
   *
   * @param api the {@link Salesforce} instance this operations object belongs to
   * @param restTemplate the shared rest template used for requests
   */
  public SObjectsTemplate(Salesforce api, RestTemplate restTemplate) {
    super(api);
    this.restTemplate = restTemplate;
  }

  /**
   * Lists the sObjects available in the org.
   *
   * @return one map of properties per sObject; an empty list if the response had no body or no
   *     {@code sobjects} entry
   */
  @Override
  public List<Map> getSObjects() {
    requireAuthorization();
    JsonNode dataNode =
        restTemplate.getForObject(
            api.getBaseUrl() + "/{version}/sobjects", JsonNode.class, getVersion());

    if (dataNode != null) {
      return emptyIfNull(api.readList(dataNode.get("sobjects"), Map.class));
    }

    return Collections.emptyList();
  }

  /**
   * Retrieves the basic information of an sObject type.
   *
   * @param name the sObject name
   * @return the summary, or {@code null} if nothing was returned
   * @throws IllegalArgumentException if the name is null, blank, {@code .} or {@code ..}
   */
  @Override
  public SObjectSummary getSObjectSummary(String name) {
    requireAuthorization();
    JsonNode node =
        restTemplate.getForObject(
            api.getBaseUrl() + "/{version}/sobjects/{name}",
            JsonNode.class,
            getVersion(),
            requirePathSegment("sObject name", name));

    if (node != null) {
      return api.readObject(node.get("objectDescribe"), SObjectSummary.class);
    }
    return null;
  }

  /**
   * Retrieves the full metadata of an sObject type.
   *
   * @param name the sObject name
   * @return the sObject description
   * @throws IllegalArgumentException if the name is null, blank, {@code .} or {@code ..}
   */
  @Override
  public SObjectDetail describeSObject(String name) {
    requireAuthorization();
    return restTemplate.getForObject(
        api.getBaseUrl() + "/{version}/sobjects/{name}/describe",
        SObjectDetail.class,
        getVersion(),
        requirePathSegment("sObject name", name));
  }

  /**
   * Retrieves a record.
   *
   * @param name the sObject name
   * @param id the record id
   * @param fields the fields to return; all fields if none are given
   * @return the record as a map of field values
   * @throws IllegalArgumentException if the name or id is null, blank, {@code .} or {@code ..}
   */
  @Override
  public Map getRow(String name, String id, String... fields) {
    requireAuthorization();
    // Every caller-supplied piece is strictly percent-encoded, so none of it can add path segments
    // or
    // query parameters. Only the commas between field names stay literal.
    StringBuilder url =
        new StringBuilder(api.getBaseUrl())
            .append('/')
            .append(getVersion())
            .append("/sobjects/")
            .append(
                UriUtils.encode(requirePathSegment("sObject name", name), StandardCharsets.UTF_8))
            .append('/')
            .append(UriUtils.encode(requirePathSegment("record id", id), StandardCharsets.UTF_8));
    if (fields != null && fields.length > 0) {
      url.append("?fields=")
          .append(
              Arrays.stream(fields)
                  .map(field -> UriUtils.encode(field, StandardCharsets.UTF_8))
                  .collect(Collectors.joining(",")));
    }
    return restTemplate.getForObject(URI.create(url.toString()), Map.class);
  }

  /**
   * Retrieves the content of a blob field into memory.
   *
   * @param name the sObject name
   * @param id the record id
   * @param field the blob field name
   * @return a stream over the content; empty if there is none
   * @throws IllegalArgumentException if the name, id or field is null, blank, {@code .} or {@code
   *     ..}
   */
  @Override
  public InputStream getBlob(String name, String id, String field) {
    requireAuthorization();
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<byte[]> response =
        restTemplate.exchange(
            api.getBaseUrl() + "/{version}/sobjects/{name}/{id}/{field}",
            HttpMethod.GET,
            entity,
            byte[].class,
            getVersion(),
            requirePathSegment("sObject name", name),
            requirePathSegment("record id", id),
            requirePathSegment("field name", field));
    // A blob with no content comes back as an empty (or 204) response, which RestTemplate reports
    // as a null body
    byte[] body = response.getBody();
    return new ByteArrayInputStream(body != null ? body : new byte[0]);
  }

  /**
   * Streams the content of a blob field to an output stream without buffering it in memory.
   *
   * @param name the sObject name
   * @param id the record id
   * @param field the blob field name
   * @param out the stream to write to; not closed by this method
   * @return the number of bytes written
   * @throws IllegalArgumentException if the name, id or field is null, blank, {@code .} or {@code
   *     ..}
   */
  @Override
  public long downloadBlob(String name, String id, String field, OutputStream out) {
    requireAuthorization();
    Long written =
        restTemplate.execute(
            api.getBaseUrl() + "/{version}/sobjects/{name}/{id}/{field}",
            HttpMethod.GET,
            null,
            response -> response.getBody().transferTo(out),
            getVersion(),
            requirePathSegment("sObject name", name),
            requirePathSegment("record id", id),
            requirePathSegment("field name", field));
    // RestTemplate.execute is declared nullable, although this extractor always returns a value
    return written == null ? 0L : written;
  }

  /**
   * Creates a record.
   *
   * @param name the sObject name
   * @param fields the field values
   * @return the create result (id, success flag and errors)
   * @throws IllegalArgumentException if the name is null, blank, {@code .} or {@code ..}
   */
  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public Map<String, Object> create(String name, Map<String, Object> fields) {
    requireAuthorization();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map> entity = new HttpEntity<>(fields, headers);
    return restTemplate.postForObject(
        api.getBaseUrl() + "/{version}/sobjects/{name}",
        entity,
        Map.class,
        getVersion(),
        requirePathSegment("sObject name", name));
  }

  /**
   * Updates a record.
   *
   * <p>The request is sent as a POST with {@code _HttpMethod=PATCH}. If Salesforce returns an empty
   * body (the normal success case), a result with the record id, {@code success} true and no errors
   * is returned in the same shape as a create result; any body that is returned is passed through
   * unchanged.
   *
   * @param sObjectName the sObject name
   * @param sObjectId the record id
   * @param fields the field values to change
   * @return the update result
   * @throws IllegalArgumentException if the name or id is null, blank, {@code .} or {@code ..}
   */
  @Override
  public Map<String, Object> update(
      String sObjectName, String sObjectId, Map<String, Object> fields) {
    requireAuthorization();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(fields, headers);
    @SuppressWarnings("unchecked")
    Map<String, Object> result =
        restTemplate.postForObject(
            api.getBaseUrl() + "/{version}/sobjects/{sObjectName}/{sObjectId}?_HttpMethod=PATCH",
            entity,
            Map.class,
            getVersion(),
            requirePathSegment("sObject name", sObjectName),
            requirePathSegment("record id", sObjectId));
    // SF returns an empty body on success, so mimic the same update you'd get from a create success
    // for consistency
    if (result == null) {
      result = new HashMap<>();
      result.put("id", sObjectId);
      result.put("success", true);
      result.put("errors", new ArrayList<String>());
    }
    return result;
  }

  /**
   * Deletes a record.
   *
   * @param sObjectName the sObject name
   * @param sObjectId the record id
   * @throws IllegalArgumentException if the name or id is null, blank, {@code .} or {@code ..}
   */
  @Override
  public void delete(String sObjectName, String sObjectId) {
    requireAuthorization();
    restTemplate.delete(
        api.getBaseUrl() + "/{version}/sobjects/{sObjectName}/{sObjectId}",
        getVersion(),
        requirePathSegment("sObject name", sObjectName),
        requirePathSegment("record id", sObjectId));
  }
}
