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
 * @author Umut Utkan
 */
public class QueryResult {

    private int totalSize;

    private boolean done;

    private String nextRecordsUrl;

    private List<ResultItem> records;


    public QueryResult(int totalSize, boolean done) {
        this.totalSize = totalSize;
        this.done = done;
    }


    public int getTotalSize() {
        return totalSize;
    }

    public boolean isDone() {
        return done;
    }

    public List<ResultItem> getRecords() {
        return records;
    }

    public void setRecords(List<ResultItem> records) {
        this.records = records;
    }

    public String getNextRecordsUrl() {
        return nextRecordsUrl;
    }

    public void setNextRecordsUrl(String nextRecordsUrl) {
        this.nextRecordsUrl = nextRecordsUrl;
    }

    public String getNextRecordsToken() {
        if (this.nextRecordsUrl != null) {
            return this.nextRecordsUrl.substring(this.nextRecordsUrl.lastIndexOf('/') + 1, this.nextRecordsUrl.length());
        }
        return null;
    }

}
