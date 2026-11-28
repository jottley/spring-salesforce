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
/**
 * Support for Salesforce Canvas apps: verifying the signed request Salesforce posts to the app and
 * reading what it carries.
 *
 * <p>When a canvas component loads, Salesforce POSTs a form field named {@code signed_request} to
 * the app's Canvas URL, from an iframe. The value is {@code signature.envelope}: the envelope is
 * Base64 JSON holding the user, organization, links, application, environment and a {@link
 * org.springframework.salesforce.canvas.CanvasClient client} section with an OAuth token and the
 * instance URL, and the signature is an HMAC-SHA256 of it made with the canvas app's consumer
 * secret. The signature <em>is</em> the authentication: there is no login.
 *
 * <h2>Using it</h2>
 *
 * <pre>
 * &#64;PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
 * public String canvas(&#64;RequestParam("signed_request") String signedRequest) {
 *   try {
 *     SalesforceCanvasServiceProvider provider =
 *         new SalesforceCanvasServiceProvider(signedRequest, consumerSecret);
 *
 *     CanvasRequest request = provider.getCanvasRequest();
 *     String who = request.canvasContext().userContext().fullName();
 *
 *     // A Salesforce binding that sends the request's token to its instance URL
 *     Salesforce salesforce = provider.getApi();
 *     QueryResult accounts = salesforce.queryOperations().query("SELECT Name FROM Account");
 *     ...
 *   } catch (SecurityException e) {
 *     // not signed with this secret, tampered with, or malformed: answer 403
 *   }
 * }
 * </pre>
 *
 * <h2>The record</h2>
 *
 * <p>Where the component is placed decides what the request holds. On a Visualforce page Salesforce
 * sends the record inside the request, and {@link
 * org.springframework.salesforce.canvas.CanvasRecordContext#fields()} holds every field of it (null
 * values included, custom fields too). On a Lightning record page ({@code displayLocation} {@code
 * Aura}) the record is empty and only its id arrives in the environment's {@code parameters}
 * ({@code recordId}, and {@code type} when the wrapper passes it); read the record with {@link
 * org.springframework.salesforce.api.SObjectOperations#getRow} using {@code provider.getApi()}.
 *
 * <h2>Things to know</h2>
 *
 * <ul>
 *   <li>The client section holds the OAuth token, and {@code refreshToken} when the app has the
 *       {@code refresh_token} scope. {@code CanvasClient.toString()} masks both, but {@code
 *       SalesforceCanvasServiceProvider#getCanvasRequestJson()} returns the full envelope, so
 *       redact it before logging.
 *   <li>Salesforce sends no CSRF token and shows the page in an iframe. In Spring Security, turn
 *       CSRF off for the Canvas URL only and replace {@code X-Frame-Options: DENY} on that path
 *       with a {@code frame-ancestors} policy naming Salesforce's domains.
 *   <li>The model records ignore properties they do not declare, and {@code environment.record}
 *       collects everything it does not declare in {@code fields()}, so new properties Salesforce
 *       adds do not break reading.
 *   <li>The request carries no expiry check: {@code issuedAt} is available, but this library does
 *       not reject old requests.
 * </ul>
 *
 * @see org.springframework.salesforce.connect.SalesforceCanvasServiceProvider
 * @see org.springframework.salesforce.connect.SalesforceCanvasConnectionFactory
 */
package org.springframework.salesforce.canvas;
