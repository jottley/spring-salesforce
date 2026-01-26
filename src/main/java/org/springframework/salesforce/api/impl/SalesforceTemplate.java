/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-social-salesforce
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


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.salesforce.api.ApiOperations;
import org.springframework.salesforce.api.ChatterOperations;
import org.springframework.salesforce.api.ConnectOperations;
import org.springframework.salesforce.api.CustomApiOperations;
import org.springframework.salesforce.api.LimitsOperations;
import org.springframework.salesforce.api.QueryOperations;
import org.springframework.salesforce.api.RecentOperations;
import org.springframework.salesforce.api.SObjectOperations;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.salesforce.api.SearchOperations;
import org.springframework.salesforce.api.UserOperations;
import org.springframework.salesforce.api.impl.json.SalesforceModule;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Default implementation of Salesforce. This is the main entry point
 * for all the operations that can be performed on Salesforce.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SalesforceTemplate implements Salesforce {

	private static final String INSTANCE_URL = "https://na1.salesforce.com";
	private static final String GATEWAY_URL  = "https://login.salesforce.com";

	private String instanceUrl;
	private String gatewayUrl;
	private final String accessToken;

	private ObjectMapper objectMapper;
	private RestTemplate restTemplate;

	private ApiOperations apiOperations;

	private ChatterOperations chatterOperations;

	private QueryOperations queryOperations;

	private RecentOperations recentOperations;

	private SearchOperations searchOperations;

	private SObjectOperations sObjectsOperations;

	private UserOperations userOperations;

	private LimitsOperations limitsOperations;

	private ConnectOperations connectOperations;

	private CustomApiOperations customApiOperatinos;

	public SalesforceTemplate()
	{
		this.accessToken = null;
		initializeRestTemplate();
		initialize();
	}


	public SalesforceTemplate(String accessToken)
	{
		this.accessToken = accessToken;
		initializeRestTemplate();
		initialize();
	}

	/**
	 * Initialize the RestTemplate with OAuth2 support
	 */
	private void initializeRestTemplate() {
		this.restTemplate = new RestTemplate();

		// Set up message converters
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(new FormHttpMessageConverter());
		messageConverters.add(this.getJsonMessageConverter());
		messageConverters.add(this.getByteArrayMessageConverter());
		restTemplate.setMessageConverters(messageConverters);

		// Set error handler
		restTemplate.setErrorHandler(new SalesforceErrorHandler());

		// Add OAuth2 bearer token interceptor if access token is present
		if (accessToken != null && !accessToken.isEmpty()) {
			List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
			interceptors.add((request, body, execution) -> {
				request.getHeaders().setBearerAuth(accessToken);
				return execution.execute(request, body);
			});
			restTemplate.setInterceptors(interceptors);
		}
	}

	/**
	 * Get the RestTemplate instance
	 */
	protected RestTemplate getRestTemplate() {
		return this.restTemplate;
	}

	@Override
	public ApiOperations apiOperations()
	{
		return apiOperations;
	}


	@Override
	public ApiOperations apiOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;

		return apiOperations;
	}


	@Override
	public ChatterOperations chatterOperations()
	{
		return chatterOperations;
	}


	@Override
	public ChatterOperations chatterOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;
		return chatterOperations;
	}


	@Override
	public QueryOperations queryOperations()
	{
		return queryOperations;
	}


	@Override
	public QueryOperations queryOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;
		return queryOperations;
	}


	@Override
	public RecentOperations recentOperations()
	{
		return recentOperations;
	}


	@Override
	public RecentOperations recentOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;
		return recentOperations;
	}


	@Override
	public SearchOperations searchOperations()
	{
		return searchOperations;
	}


	@Override
	public SearchOperations searchOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;

		return searchOperations;
	}


	@Override
	public SObjectOperations sObjectsOperations()
	{
		return sObjectsOperations;
	}


	@Override
	public SObjectOperations sObjectsOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;
		return sObjectsOperations;
	}


	@Override
	public UserOperations userOperations()
	{
		return userOperations;
	}


	@Override
	public UserOperations userOperations(String gatewayUrl)
	{
		this.gatewayUrl = gatewayUrl;
		return userOperations;
	}


	@Override
	public LimitsOperations limitsOperations()
	{
		return limitsOperations;
	}

	@Override
	public LimitsOperations limitsOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;
		return limitsOperations;
	}

	@Override
	public ConnectOperations connectOperations() { return connectOperations; }

	@Override
	public ConnectOperations connectOperations(String instanceUrl) {
		this.instanceUrl = instanceUrl;
		return connectOperations;
	}

	private void initialize()
	{
		//Add the ApiRequestInterceptor to the rest template used by all of the operations classes
		List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
		interceptors.add(new ApiRequestInterceptor(this));
		restTemplate.setInterceptors(interceptors);

		apiOperations = new ApiTemplate(this, restTemplate);
		chatterOperations = new ChatterTemplate(this, restTemplate);
		queryOperations = new QueryTemplate(this, restTemplate);
		recentOperations = new RecentTemplate(this, restTemplate);
		searchOperations = new SearchTemplate(this, restTemplate);
		sObjectsOperations = new SObjectsTemplate(this, restTemplate);
		userOperations = new UserOperationsTemplate(this, restTemplate);
		limitsOperations = new LimitsOperationsTemplate(this, restTemplate);
		connectOperations = new ConnectTemplate(this, restTemplate);
		customApiOperatinos = new CustomApiTemplate(this, restTemplate);
	}

	protected MappingJackson2HttpMessageConverter getJsonMessageConverter()
	{
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new SalesforceModule());
		converter.setObjectMapper(objectMapper);
		return converter;
	}

	protected ByteArrayHttpMessageConverter getByteArrayMessageConverter()
	{
		ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
		converter.setSupportedMediaTypes(Objects.requireNonNull(List.of(MediaType.ALL)));
		return converter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> readList(JsonNode jsonNode, Class<T> type) {
		CollectionType listType = TypeFactory.defaultInstance().constructCollectionType(List.class, type);
		return (List<T>) objectMapper.convertValue(jsonNode, listType);
	}

	@Override
	public <T> T readObject(JsonNode jsonNode, Class<T> type) {
		return objectMapper.convertValue(jsonNode, type);
	}

	@Override
	public String getBaseUrl() {
		return (this.instanceUrl == null ? INSTANCE_URL : this.instanceUrl) + "/services/data";
	}

	@Override
	public String getInstanceUrl() {
		return this.instanceUrl;
	}

	@Override
    public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}


	@Override
	public String getUserInfoUrl() {
		return (this.gatewayUrl == null ? GATEWAY_URL : this.gatewayUrl) + "/services/oauth2/userinfo";
	}


	@Override
	public String getAuthGatewayUrl() {
		return this.gatewayUrl;
	}


	@Override
	public void setAuthGatewayBaseUrl(String gatewayUrl) {
		this.gatewayUrl = gatewayUrl;
	}


	@Override
	public CustomApiOperations customApiOperations() {
		return this.customApiOperatinos;
	}


	@Override
	public CustomApiOperations customApiOperations(String instanceUrl) {
		this.instanceUrl = instanceUrl;
		return this.customApiOperatinos;
	}
}