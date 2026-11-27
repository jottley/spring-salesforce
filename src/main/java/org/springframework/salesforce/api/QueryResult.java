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
package org.springframework.salesforce.api;

import java.util.List;

/**
 * The result of a SOQL query: one page of records, and how to fetch the next page if there is one.
 * For a {@code COUNT()} query {@code totalSize} is the count and {@code records} is empty; for a nested
 * subquery it counts that relationship's child records.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 */
public class QueryResult {

    /** The total number of records the query matches, across all pages. */
    private final int totalSize;

    /** Whether this is the last page of results. */
    private final boolean done;

    /** The URL of the next page of results. */
    private String nextRecordsUrl;

    /** The records in this page. */
    private List<ResultItem> records;


    /**
     * Creates a query result.
     *
     * @param totalSize the total number of records the query matches, across all pages
     * @param done whether this is the last page
     */
    public QueryResult(int totalSize, boolean done) {
        this.totalSize = totalSize;
        this.done = done;
    }


    /**
     * Returns the total number of records the query matches, across all pages.
     *
     * @return the total size
     */
    public int getTotalSize() {
        return totalSize;
    }

    /**
     * Returns whether this is the last page of results.
     *
     * @return true if there are no more pages
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Returns the records in this page.
     *
     * @return the records
     */
    public List<ResultItem> getRecords() {
        return records;
    }

    /**
     * Sets the records in this page.
     *
     * @param records the records
     */
    public void setRecords(List<ResultItem> records) {
        this.records = records;
    }

    /**
     * Returns the URL of the next page of results.
     *
     * @return the URL, or null if this is the last page
     */
    public String getNextRecordsUrl() {
        return nextRecordsUrl;
    }

    /**
     * Sets the URL of the next page of results.
     *
     * @param nextRecordsUrl the URL
     */
    public void setNextRecordsUrl(String nextRecordsUrl) {
        this.nextRecordsUrl = nextRecordsUrl;
    }

    /**
     * Returns the last path segment of the next records URL, which identifies the next page of results.
     *
     * @return the token, or null if there is no next records URL
     */
    public String getNextRecordsToken() {
        if (this.nextRecordsUrl != null) {
            return this.nextRecordsUrl.substring(this.nextRecordsUrl.lastIndexOf('/') + 1, this.nextRecordsUrl.length());
        }
        return null;
    }

}
