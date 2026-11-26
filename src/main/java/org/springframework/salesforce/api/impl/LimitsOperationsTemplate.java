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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.salesforce.api.LimitsOperations;
import org.springframework.salesforce.api.LimitsResults;
import org.springframework.salesforce.api.Salesforce;
import org.springframework.web.client.RestTemplate;

/**
 * Default implementation of the Limits API. Also includes tracking of the
 * current API usage as returned from salesforce in each API call in the
 * Sforce-Limit-Info header
 *
 * @author Jared Ottley
 */
public class LimitsOperationsTemplate extends AbstractSalesForceOperations<Salesforce> implements LimitsOperations {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(LimitsOperationsTemplate.class);

    /** Used and max are replaced together so a reader on another thread never sees a mismatched pair. */
    private record Usage(int used, int max) {
    }

    private volatile Usage currentUsage = new Usage(0, 0);

    private static final String API_USAGE = "api-usage";

    public LimitsOperationsTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }

    @Override
    public LimitsResults getLimits() {
        requireAuthorization();
        return restTemplate.getForObject(api.getBaseUrl() + "/{version}/limits",
                LimitsResults.class, getVersion());
    }

    @Override
    public int getDailyApiLimit() {
        return currentUsage.max();
    }

    @Override
    public int getDailyApiUsed() {
        return currentUsage.used();
    }

    /**
     * Updates the daily API usage from a {@code Sforce-Limit-Info} header value, for example
     * {@code api-usage=39/15000}. Salesforce can add further entries separated by semicolons, such as
     * {@code per-app-api-usage=17/250(application=MyApp)}; only {@code api-usage} is read.
     * <p>
     * A header that is missing, has no {@code api-usage} entry, or cannot be parsed is ignored and the
     * previous values are kept. This is called for every response, so it must never throw.
     */
    public void setCurrentDailyApiLimits(String limitInfo) {
        if (StringUtils.isBlank(limitInfo)) {
            return;
        }

        for (String entry : limitInfo.split(";")) {
            String[] keyValue = entry.split("=", 2);
            if (keyValue.length == 2 && API_USAGE.equals(keyValue[0].trim())) {
                parseUsage(keyValue[1].trim(), limitInfo);
                return;
            }
        }
    }

    private void parseUsage(String usedAndMax, String limitInfo) {
        String[] parts = usedAndMax.split("/");
        try {
            if (parts.length != 2) {
                throw new NumberFormatException("expected used/max");
            }
            this.currentUsage = new Usage(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
        } catch (NumberFormatException e) {
            logger.debug("Ignoring unparseable Sforce-Limit-Info header: {}", limitInfo);
        }
    }

}