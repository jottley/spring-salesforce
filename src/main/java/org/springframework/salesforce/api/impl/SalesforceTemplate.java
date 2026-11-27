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
 * Default implementation of {@link Salesforce}. This is the main entry point
 * for all the operations that can be performed on Salesforce.
 * <p>
 * It builds one {@link RestTemplate} shared by every operations object, with a bearer token
 * interceptor when an access token is given. The instance URL and gateway URL are mutable and
 * the template is not documented as thread safe.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class SalesforceTemplate implements Salesforce {

	/** Instance URL that {@link #getBaseUrl()} uses when none has been set. */
	private static final String INSTANCE_URL = "https://na1.salesforce.com";
	/** Login gateway URL used when none has been set. */
	private static final String GATEWAY_URL  = "https://login.salesforce.com";

	/**
	 * The Salesforce instance URL, or {@code null} to use the default for {@code /services/data} URLs.
	 * Apex REST calls require it to be set.
	 */
	private String instanceUrl;
	/** The authentication gateway URL, or {@code null} to use the default. */
	private String gatewayUrl;
	/** The OAuth2 access token sent as a bearer token, or {@code null} if none. */
	private final String accessToken;

	/** Jackson mapper with the {@code SalesforceModule} registered, shared by the converter and the read methods. */
	private ObjectMapper objectMapper;
	/** The rest template shared by all operations instances. */
	private RestTemplate restTemplate;

	/** The API version operations, created in {@code initialize()}. */
	private ApiOperations apiOperations;

	/** The Chatter operations, created in {@code initialize()}. */
	private ChatterOperations chatterOperations;

	/** The SOQL query operations, created in {@code initialize()}. */
	private QueryOperations queryOperations;

	/** The recent items operations, created in {@code initialize()}. */
	private RecentOperations recentOperations;

	/** The SOSL search operations, created in {@code initialize()}. */
	private SearchOperations searchOperations;

	/** The sObject operations, created in {@code initialize()}. */
	private SObjectOperations sObjectsOperations;

	/** The user info operations, created in {@code initialize()}. */
	private UserOperations userOperations;

	/** The limits operations, created in {@code initialize()}. */
	private LimitsOperations limitsOperations;

	/** The Connect (communities) operations, created in {@code initialize()}. */
	private ConnectOperations connectOperations;

	/** The custom Apex REST operations, created in {@code initialize()}. */
	private CustomApiOperations customApiOperations;

	/** Creates a template without an access token. */
	public SalesforceTemplate()
	{
		this.accessToken = null;
		initializeRestTemplate();
		initialize();
	}


	/**
	 * Creates a template that sends the access token as a bearer token on every request.
	 *
	 * @param accessToken the OAuth2 access token
	 */
	public SalesforceTemplate(String accessToken)
	{
		this.accessToken = accessToken;
		initializeRestTemplate();
		initialize();
	}

	/**
	 * Creates the shared RestTemplate with String, Form, Jackson JSON and byte array converters and the
	 * {@link SalesforceErrorHandler}, plus a bearer token interceptor when an access token is given.
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
	 * Returns the shared {@link RestTemplate}.
	 *
	 * @return the rest template
	 */
	protected RestTemplate getRestTemplate() {
		return this.restTemplate;
	}

	/**
	 * Returns the API operations.
	 *
	 * @return the API operations
	 */
	@Override
	public ApiOperations apiOperations()
	{
		return apiOperations;
	}


	/**
	 * Sets the instance URL and returns the API operations.
	 *
	 * @param instanceUrl the instance URL
	 * @return the API operations
	 */
	@Override
	public ApiOperations apiOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;

		return apiOperations;
	}


	/**
	 * Returns the Chatter operations.
	 *
	 * @return the Chatter operations
	 */
	@Override
	public ChatterOperations chatterOperations()
	{
		return chatterOperations;
	}


	/**
	 * Sets the instance URL and returns the Chatter operations.
	 *
	 * @param instanceUrl the instance URL
	 * @return the Chatter operations
	 */
	@Override
	public ChatterOperations chatterOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;
		return chatterOperations;
	}


	/**
	 * Returns the query operations.
	 *
	 * @return the query operations
	 */
	@Override
	public QueryOperations queryOperations()
	{
		return queryOperations;
	}


	/**
	 * Sets the instance URL and returns the query operations.
	 *
	 * @param instanceUrl the instance URL
	 * @return the query operations
	 */
	@Override
	public QueryOperations queryOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;
		return queryOperations;
	}


	/**
	 * Returns the recent items operations.
	 *
	 * @return the recent items operations
	 */
	@Override
	public RecentOperations recentOperations()
	{
		return recentOperations;
	}


	/**
	 * Sets the instance URL and returns the recent items operations.
	 *
	 * @param instanceUrl the instance URL
	 * @return the recent items operations
	 */
	@Override
	public RecentOperations recentOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;
		return recentOperations;
	}


	/**
	 * Returns the search operations.
	 *
	 * @return the search operations
	 */
	@Override
	public SearchOperations searchOperations()
	{
		return searchOperations;
	}


	/**
	 * Sets the instance URL and returns the search operations.
	 *
	 * @param instanceUrl the instance URL
	 * @return the search operations
	 */
	@Override
	public SearchOperations searchOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;

		return searchOperations;
	}


	/**
	 * Returns the sObject operations.
	 *
	 * @return the sObject operations
	 */
	@Override
	public SObjectOperations sObjectsOperations()
	{
		return sObjectsOperations;
	}


	/**
	 * Sets the instance URL and returns the sObject operations.
	 *
	 * @param instanceUrl the instance URL
	 * @return the sObject operations
	 */
	@Override
	public SObjectOperations sObjectsOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;
		return sObjectsOperations;
	}


	/**
	 * Returns the user operations.
	 *
	 * @return the user operations
	 */
	@Override
	public UserOperations userOperations()
	{
		return userOperations;
	}


	/**
	 * Sets the login gateway URL and returns the user operations.
	 *
	 * @param gatewayUrl the login gateway URL
	 * @return the user operations
	 */
	@Override
	public UserOperations userOperations(String gatewayUrl)
	{
		this.gatewayUrl = gatewayUrl;
		return userOperations;
	}


	/**
	 * Returns the limits operations.
	 *
	 * @return the limits operations
	 */
	@Override
	public LimitsOperations limitsOperations()
	{
		return limitsOperations;
	}

	/**
	 * Sets the instance URL and returns the limits operations.
	 *
	 * @param instanceUrl the instance URL
	 * @return the limits operations
	 */
	@Override
	public LimitsOperations limitsOperations(String instanceUrl)
	{
		this.instanceUrl = instanceUrl;
		return limitsOperations;
	}

	/**
	 * Returns the Connect (communities) operations.
	 *
	 * @return the Connect operations
	 */
	@Override
	public ConnectOperations connectOperations() { return connectOperations; }

	/**
	 * Sets the instance URL and returns the Connect operations.
	 *
	 * @param instanceUrl the instance URL
	 * @return the Connect operations
	 */
	@Override
	public ConnectOperations connectOperations(String instanceUrl) {
		this.instanceUrl = instanceUrl;
		return connectOperations;
	}

	/**
	 * Adds the {@link ApiRequestInterceptor} and creates every operations object, all sharing the same rest template.
	 */
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
		customApiOperations = new CustomApiTemplate(this, restTemplate);
	}

	/**
	 * Creates the JSON message converter, with an {@link ObjectMapper} that has the {@link SalesforceModule} registered.
	 *
	 * @return the converter
	 */
	protected MappingJackson2HttpMessageConverter getJsonMessageConverter()
	{
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new SalesforceModule());
		converter.setObjectMapper(objectMapper);
		return converter;
	}

	/**
	 * Creates a byte array converter that accepts any media type, used for blob downloads.
	 *
	 * @return the converter
	 */
	protected ByteArrayHttpMessageConverter getByteArrayMessageConverter()
	{
		ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
		converter.setSupportedMediaTypes(Objects.requireNonNull(List.of(MediaType.ALL)));
		return converter;
	}

	/**
	 * Converts a JSON array into a list of the given type.
	 *
	 * @param <T> the element type
	 * @param jsonNode the JSON array
	 * @param type the element class
	 * @return the converted list, or {@code null} if {@code jsonNode} is {@code null}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> readList(JsonNode jsonNode, Class<T> type) {
		CollectionType listType = TypeFactory.defaultInstance().constructCollectionType(List.class, type);
		return (List<T>) objectMapper.convertValue(jsonNode, listType);
	}

	/**
	 * Converts a JSON node into an object of the given type.
	 *
	 * @param <T> the target type
	 * @param jsonNode the JSON node
	 * @param type the target class
	 * @return the converted object
	 */
	@Override
	public <T> T readObject(JsonNode jsonNode, Class<T> type) {
		return objectMapper.convertValue(jsonNode, type);
	}

	/**
	 * Returns the base URL for data requests.
	 *
	 * @return the instance URL (or a default host if none is set) followed by {@code /services/data}
	 */
	@Override
	public String getBaseUrl() {
		return (this.instanceUrl == null ? INSTANCE_URL : this.instanceUrl) + "/services/data";
	}

	/**
	 * Returns the instance URL.
	 *
	 * @return the instance URL, or {@code null} if none has been set
	 */
	@Override
	public String getInstanceUrl() {
		return this.instanceUrl;
	}

    /**
     * Sets the instance URL.
     *
     * @param instanceUrl the instance URL
     */
	@Override
    public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}


	/**
	 * Returns the OAuth2 userinfo URL.
	 *
	 * @return the gateway URL (or the production gateway if none is set) followed by {@code /services/oauth2/userinfo}
	 */
	@Override
	public String getUserInfoUrl() {
		return (this.gatewayUrl == null ? GATEWAY_URL : this.gatewayUrl) + "/services/oauth2/userinfo";
	}


	/**
	 * Returns the login gateway URL.
	 *
	 * @return the gateway URL, or {@code null} if none has been set
	 */
	@Override
	public String getAuthGatewayUrl() {
		return this.gatewayUrl;
	}


	/**
	 * Sets the login gateway URL.
	 *
	 * @param gatewayUrl the gateway URL
	 */
	@Override
	public void setAuthGatewayBaseUrl(String gatewayUrl) {
		this.gatewayUrl = gatewayUrl;
	}


	/**
	 * Returns the custom Apex REST API operations.
	 *
	 * @return the custom API operations
	 */
	@Override
	public CustomApiOperations customApiOperations() {
		return this.customApiOperations;
	}


	/**
	 * Sets the instance URL and returns the custom Apex REST API operations.
	 *
	 * @param instanceUrl the instance URL
	 * @return the custom API operations
	 */
	@Override
	public CustomApiOperations customApiOperations(String instanceUrl) {
		this.instanceUrl = instanceUrl;
		return this.customApiOperations;
	}
}