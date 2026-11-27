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
 * Default implementation of {@link LimitsOperations}. It also tracks the current daily API usage
 * reported by Salesforce in the {@code Sforce-Limit-Info} header of each response.
 *
 * @author Jared Ottley
 */
public class LimitsOperationsTemplate extends AbstractSalesForceOperations<Salesforce> implements LimitsOperations {

    /** The shared rest template used for requests. */
    private final RestTemplate restTemplate;
    /** Logger for unparseable usage data. */
    private static final Logger logger = LoggerFactory.getLogger(LimitsOperationsTemplate.class);

    /**
     * Used and max are replaced together so each getter reads a consistent used/max snapshot; reading the two
     * getters separately is not atomic.
     *
     * @param used the API calls used so far
     * @param max the daily API call limit
     */
    private record Usage(int used, int max) {
    }

    /** The most recent usage, replaced as a whole so used and max stay consistent. */
    private volatile Usage currentUsage = new Usage(0, 0);

    /** Key of the daily API usage entry within the {@code Sforce-Limit-Info} header. */
    private static final String API_USAGE = "api-usage";

    /**
     * Creates the operations instance.
     *
     * @param api the {@link Salesforce} instance this operations object belongs to
     * @param restTemplate the shared rest template used for requests
     */
    public LimitsOperationsTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves the org limits.
     *
     * @return the limits
     */
    @Override
    public LimitsResults getLimits() {
        requireAuthorization();
        return restTemplate.getForObject(api.getBaseUrl() + "/{version}/limits",
                LimitsResults.class, getVersion());
    }

    /**
     * Returns the daily API call limit from the most recent parseable {@code api-usage} entry.
     *
     * @return the limit, or 0 if no usage has been recorded
     */
    @Override
    public int getDailyApiLimit() {
        return currentUsage.max();
    }

    /**
     * Returns the number of daily API calls used, from the most recent parseable {@code api-usage} entry.
     *
     * @return the calls used, or 0 if no usage has been recorded
     */
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
     * previous values are kept. Only the first {@code api-usage} entry is considered; if it is malformed the
     * update is skipped and a later well-formed entry is not tried. This is called for every response that
     * carries the header (only the first header value is passed), so it must never throw.
     *
     * @param limitInfo the header value
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

    /**
     * Parses a {@code used/max} pair and records it, ignoring a value that cannot be parsed.
     *
     * @param usedAndMax the value of the {@code api-usage} entry
     * @param limitInfo the whole header, for logging
     */
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