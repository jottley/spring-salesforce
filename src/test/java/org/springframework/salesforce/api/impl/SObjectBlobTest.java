/**
 * Copyright (C) 2017-2026 https://github.com/jottley/spring-social-salesforce
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

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.salesforce.api.InternalServerErrorException;
import org.springframework.salesforce.api.RateLimitExceededException;
import org.springframework.salesforce.api.ResourceNotFoundException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Edge cases for {@code getBlob} and {@code downloadBlob}: binary content, empty content, error
 * responses, a failing output stream, and unusual characters in the path values.
 *
 * @author Jared Ottley
 */
public class SObjectBlobTest extends AbstractSalesforceTest {

	private static final String NOT_FOUND = "[{\"errorCode\":\"NOT_FOUND\",\"message\":\"no blob\"}]";

	private String blobUrl() {
		return "https://na7.salesforce.com/services/data/" + salesforce.apiOperations().getVersion() + "/sobjects/ContentVersion/068xx/VersionData";
	}

	private void expectBlob(byte[] content) {
		mockServer.expect(requestTo(blobUrl())).andExpect(method(GET))
				.andRespond(withSuccess(content, MediaType.APPLICATION_OCTET_STREAM));
	}

	private void expectError(HttpStatus status, String body) {
		mockServer.expect(requestTo(blobUrl()))
				.andRespond(withStatus(status).contentType(MediaType.APPLICATION_JSON).body(body));
	}

	private static byte[] everyByteValue() {
		byte[] bytes = new byte[256];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) i;
		}
		return bytes;
	}

	private InputStream blob() {
		return salesforce.sObjectsOperations().getBlob("ContentVersion", "068xx", "VersionData");
	}

	private long download(OutputStream out) {
		return salesforce.sObjectsOperations().downloadBlob("ContentVersion", "068xx", "VersionData", out);
	}

	// ---- getBlob ----

	@Test
	public void getBlobPreservesBinaryContent() throws IOException {
		mockServer.expect(requestTo(blobUrl())).andRespond(withSuccess(everyByteValue(), MediaType.IMAGE_PNG));

		assertArrayEquals(everyByteValue(), blob().readAllBytes());
	}

	@Test
	public void getBlobReturnsAnEmptyStreamForAnEmptyBody() throws IOException {
		expectBlob(new byte[0]);

		InputStream in = blob();

		assertNotNull(in);
		assertEquals(0, in.readAllBytes().length);
	}

	@Test
	public void getBlobReturnsAnEmptyStreamForNoContent() throws IOException {
		mockServer.expect(requestTo(blobUrl())).andRespond(withNoContent());

		InputStream in = blob();

		assertNotNull(in);
		assertEquals(0, in.readAllBytes().length);
	}

	@Test
	public void getBlobMapsErrorResponsesToExceptions() {
		expectError(HttpStatus.NOT_FOUND, NOT_FOUND);
		assertEquals("no blob", assertThrows(ResourceNotFoundException.class, this::blob).getMessage());
	}

	@Test
	public void getBlobMapsAGatewayErrorWithNoBody() {
		mockServer.expect(requestTo(blobUrl())).andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
		assertThrows(RateLimitExceededException.class, this::blob);
	}

	@Test
	public void getBlobUsesTheConfiguredVersion() throws IOException {
		salesforce.apiOperations().setVersion("v40.0");
		mockServer.expect(requestTo("https://na7.salesforce.com/services/data/v40.0/sobjects/ContentVersion/068xx/VersionData"))
				.andRespond(withSuccess("x".getBytes(), MediaType.APPLICATION_OCTET_STREAM));

		assertEquals(1, blob().readAllBytes().length);
		mockServer.verify();
	}

	@Test
	public void pathValuesAreEncodedSoTheyCannotLeaveTheirSegment() throws IOException {
		AtomicReference<URI> requested = new AtomicReference<>();
		mockServer.expect(requestTo(startsWith("https://na7.salesforce.com/services/data/")))
				.andRespond(request -> {
					requested.set(request.getURI());
					return withSuccess(new byte[0], MediaType.APPLICATION_OCTET_STREAM).createResponse(request);
				});

		salesforce.sObjectsOperations().getBlob("Acc ount", "../../limits?x=1#f", "Body/../Other").readAllBytes();

		String raw = requested.get().getRawPath();
		assertEquals("na7.salesforce.com", requested.get().getHost());
		assertTrue(raw, raw.startsWith("/services/data/" + salesforce.apiOperations().getVersion() + "/sobjects/"));
		// slashes, question marks and hashes inside a value are percent-encoded, not interpreted
		assertFalse(raw, raw.contains("/../"));
		assertEquals(null, requested.get().getRawQuery());
		assertEquals(null, requested.get().getRawFragment());
	}

	// ---- downloadBlob ----

	@Test
	public void downloadBlobPreservesBinaryContentAndCountsBytes() {
		expectBlob(everyByteValue());
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		assertEquals(256L, download(out));
		assertArrayEquals(everyByteValue(), out.toByteArray());
	}

	@Test
	public void downloadBlobStreamsLargeContentIntact() {
		byte[] large = new byte[3 * 1024 * 1024 + 17];
		for (int i = 0; i < large.length; i++) {
			large[i] = (byte) (i * 31 + (i >>> 8));
		}
		expectBlob(large);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		assertEquals((long) large.length, download(out));
		assertArrayEquals(large, out.toByteArray());
	}

	@Test
	public void downloadBlobWritesNothingForEmptyContent() {
		expectBlob(new byte[0]);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		assertEquals(0L, download(out));
		assertEquals(0, out.size());
	}

	@Test
	public void downloadBlobWritesNothingWhenSalesforceRespondsWithAnError() {
		expectError(HttpStatus.NOT_FOUND, NOT_FOUND);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		assertThrows(ResourceNotFoundException.class, () -> download(out));
		assertEquals(0, out.size());

		mockServer.reset();
		mockServer.expect(requestTo(blobUrl())).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
		assertThrows(InternalServerErrorException.class, () -> download(out));
		assertEquals(0, out.size());
	}

	@Test
	public void downloadBlobDoesNotCloseTheCallersStream() {
		expectBlob("abc".getBytes());
		boolean[] closed = { false };
		ByteArrayOutputStream out = new ByteArrayOutputStream() {
			@Override
			public void close() throws IOException {
				closed[0] = true;
				super.close();
			}
		};

		download(out);

		assertFalse("the caller owns and closes the stream", closed[0]);
		assertEquals(3, out.size());
	}

	@Test
	public void downloadBlobReportsAFailingOutputStreamAsAnIoError() {
		expectBlob(new byte[1000]);
		OutputStream failing = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				throw new IOException("disk full");
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				throw new IOException("disk full");
			}
		};

		ResourceAccessException e = assertThrows(ResourceAccessException.class, () -> download(failing));

		assertEquals("disk full", e.getCause().getMessage());
	}
}
