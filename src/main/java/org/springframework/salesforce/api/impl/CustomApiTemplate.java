/**
 * Copyright (C) 2021-2026 https://github.com/jottley/spring-social-salesforce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.salesforce.api.impl;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.salesforce.api.CustomApiOperations;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.web.client.RestTemplate;

/**
 * Defines operations for calling Apex based custom rest apis.
 *
 * @author Sanchit Agarwal
 *
 */
public class CustomApiTemplate extends AbstractSalesForceOperations<Salesforce> implements CustomApiOperations  {

	private final RestTemplate restTemplate;

	public CustomApiTemplate(Salesforce api, RestTemplate restTemplate) {
		super(api);
		this.restTemplate = restTemplate;
	}

	protected @NonNull String createUriPath(@NonNull String uriPath) {
	    String instanceUrl = this.api.getInstanceUrl();
	    if (instanceUrl == null) {
	        throw new IllegalStateException("Instance URL is not available from API");
	    }

	    String result = instanceUrl + "/services/apexrest" + uriPath;
        return Objects.requireNonNull(result, "Failed to create valid URI path");
	}

	@Override
	public <T> T postForApexObject(@NonNull String uriPath, Object request, @NonNull Class<T> responseType) {
		requireAuthorization();
		return this.restTemplate.postForObject(this.createUriPath(uriPath), request, responseType);
	}

	@Override
	public <T> T postForApexObject(@NonNull String uriPath, Object request, @NonNull Class<T> responseType, @NonNull Map<String, ?> uriVariables) {
		requireAuthorization();
		return this.restTemplate.postForObject(this.createUriPath(uriPath), request, responseType, uriVariables);
	}

	@Override
	public <T> T getForApexObject(@NonNull String uriPath, @NonNull Class<T> responseType) {
		requireAuthorization();
		return this.restTemplate.getForObject(this.createUriPath(uriPath), responseType);
	}

	@Override
	public <T> T getForApexObject(@NonNull String uriPath, @NonNull Class<T> responseType, @NonNull Map<String, ?> uriVariables) {
		requireAuthorization();
		return this.restTemplate.getForObject(this.createUriPath(uriPath), responseType, uriVariables);
	}

	@Override
	public <T> T putForApexObject(@NonNull String uriPath, @NonNull Object request, @NonNull Class<T> responseType) {
		requireAuthorization();
		ResponseEntity<T> entity = this.restTemplate.exchange(this.createUriPath(uriPath), HttpMethod.PUT, new HttpEntity<>(request), responseType);
		return entity.getBody();
	}

	@Override
	public <T> T putForApexObject(@NonNull String uriPath, @NonNull Object request, @NonNull Class<T> responseType, @NonNull Map<String, ?> uriVariables) {
		requireAuthorization();
		ResponseEntity<T> entity = this.restTemplate.exchange(this.createUriPath(uriPath), HttpMethod.PUT, new HttpEntity<>(request), responseType, uriVariables);
		return entity.getBody();
	}

	@Override
	public <T> T patchForApexObject(@NonNull String uriPath, Object request, @NonNull Class<T> responseType) {
		requireAuthorization();
		return this.restTemplate.patchForObject(this.createUriPath(uriPath), request, responseType);
	}

	@Override
	public <T> T patchForApexObject(@NonNull String uriPath, Object request, @NonNull Class<T> responseType, @NonNull Map<String, ?> uriVariables) {
		requireAuthorization();
		return this.restTemplate.patchForObject(this.createUriPath(uriPath), request, responseType, uriVariables);
	}

	@Override
	public <T> T deleteForApexObject(@NonNull String uriPath, @NonNull Class<T> responseType) {
		requireAuthorization();
		ResponseEntity<T> entity =  this.restTemplate.exchange(this.createUriPath(uriPath), HttpMethod.DELETE, HttpEntity.EMPTY, responseType);
		return entity.getBody();
	}

	@Override
	public <T> T deleteForApexObject(@NonNull String uriPath, @NonNull Class<T> responseType, @NonNull Map<String, ?> uriVariables) {
		requireAuthorization();
		ResponseEntity<T> entity = this.restTemplate.exchange(this.createUriPath(uriPath), HttpMethod.DELETE, HttpEntity.EMPTY, responseType, uriVariables);
		return entity.getBody();
	}

	@Override
	public <T> ResponseEntity<T> executeApexApi(@NonNull String uriPath, @NonNull HttpMethod method, HttpEntity<?> request, @NonNull Class<T> responseType) {
		requireAuthorization();
		return this.restTemplate.exchange(this.createUriPath(uriPath), method, request, responseType);
	}

	@Override
	public <T> ResponseEntity<T> executeApexApi(@NonNull String uriPath, @NonNull HttpMethod method, HttpEntity<?> request, @NonNull Class<T> responseType, @NonNull Map<String, ?> uriVariables) {
		requireAuthorization();
		return this.restTemplate.exchange(this.createUriPath(uriPath), method, request, responseType, uriVariables);
	}
}
