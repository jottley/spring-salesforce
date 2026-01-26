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
package org.springframework.salesforce.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * This is the representation of the the results of a Limits API call. It is
 * built using the v45.0 version of the Salesforce API. If there are new/old
 * key/valules needed based on newer or older versions of the API please open an
 * issue.
 *
 * Connected App limits can be found in the additionalProperties of the
 * individual LimitResult classes.
 *
 * @author Jared Ottley
 */
public class LimitsResults implements Serializable {

    @JsonProperty("AnalyticsExternalDataSizeMB")
    private LimitResult analyticsExternalDataSizeMB;
    @JsonProperty("ConcurrentAsyncGetReportInstances")
    private LimitResult concurrentAsyncGetReportInstances;
    @JsonProperty("ConcurrentSyncReportRuns")
    private LimitResult concurrentSyncReportRuns;
    @JsonProperty("DailyAnalyticsDataflowJobExecutions")
    private LimitResult dailyAnalyticsDataflowJobExecutions;
    @JsonProperty("DailyAnalyticsUploadedFilesSizeMB")
    private LimitResult dailyAnalyticsUploadedFilesSizeMB;
    @JsonProperty("DailyApiRequests")
    private LimitResult dailyApiRequests;
    @JsonProperty("DailyAsyncApexExecutions")
    private LimitResult dailyAsyncApexExecutions;
    @JsonProperty("DailyBulkApiRequests")
    private LimitResult dailyBulkApiRequests;
    @JsonProperty("DailyDurableGenericStreamingApiEvents")
    private LimitResult dailyDurableGenericStreamingApiEvents;
    @JsonProperty("DailyDurableStreamingApiEvents")
    private LimitResult dailyDurableStreamingApiEvents;
    @JsonProperty("DailyGenericStreamingApiEvents")
    private LimitResult dailyGenericStreamingApiEvents;
    @JsonProperty("DailyStandardVolumePlatformEvents")
    private LimitResult dailyStandardVolumePlatformEvents;
    @JsonProperty("DailyStreamingApiEvents")
    private LimitResult dailyStreamingApiEvents;
    @JsonProperty("DailyWorkflowEmails")
    private LimitResult dailyWorkflowEmails;
    @JsonProperty("DataStorageMB")
    private LimitResult dataStorageMB;
    @JsonProperty("DurableStreamingApiConcurrentClients")
    private LimitResult durableStreamingApiConcurrentClients;
    @JsonProperty("FileStorageMB")
    private LimitResult fileStorageMB;
    @JsonProperty("HourlyAsyncReportRuns")
    private LimitResult hourlyAsyncReportRuns;
    @JsonProperty("HourlyDashboardRefreshes")
    private LimitResult hourlyDashboardRefreshes;
    @JsonProperty("HourlyDashboardResults")
    private LimitResult hourlyDashboardResults;
    @JsonProperty("HourlyDashboardStatuses")
    private LimitResult hourlyDashboardStatuses;
    @JsonProperty("HourlyLongTermIdMapping")
    private LimitResult hourlyLongTermIdMapping;
    @JsonProperty("HourlyODataCallout")
    private LimitResult hourlyODataCallout;
    @JsonProperty("HourlyShortTermIdMapping")
    private LimitResult hourlyShortTermIdMapping;
    @JsonProperty("HourlySyncReportRuns")
    private LimitResult hourlySyncReportRuns;
    @JsonProperty("HourlyTimeBasedWorkflow")
    private LimitResult hourlyTimeBasedWorkflow;
    @JsonProperty("MassEmail")
    private LimitResult massEmail;
    @JsonProperty("MonthlyPlatformEvents")
    private LimitResult monthlyPlatformEvents;
    @JsonProperty("Package2VersionCreates")
    private LimitResult package2VersionCreates;
    @JsonProperty("PermissionSets")
    private LimitResult permissionSets;
    @JsonProperty("SingleEmail")
    private LimitResult singleEmail;
    @JsonProperty("StreamingApiConcurrentClients")
    private LimitResult streamingApiConcurrentClients;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 3810468125069690661L;

    /**
     * No args constructor for use in serialization
     *
     */
    public LimitsResults() {
    }

    /**
     *
     * @param dailyApiRequests Daily API calls
     * @param dailyBulkApiRequests Daily Bulk API calls
     * @param concurrentAsyncGetReportInstances Concurrent REST API requests for results of asynchronous report runs
     * @param hourlyLongTermIdMapping Hourly new long-term external record ID mappings
     * @param hourlyODataCallout Hourly OData callouts
     * @param dailyStreamingApiEvents PushTopic event notifications delivered in the past 24 hours to all CometD clients
     * @param fileStorageMB File storage in MB. (The API user must have the Manage Users permission.)
     * @param massEmail Daily number of mass emails that are sent to external email addresses by using Apex or APIs
     * @param dailyAnalyticsUploadedFilesSizeMB
     * @param analyticsExternalDataSizeMB
     * @param hourlyAsyncReportRuns Hourly asynchronous report runs via REST API
     * @param hourlyTimeBasedWorkflow
     * @param hourlyDashboardStatuses Hourly dashboard status requests via REST API
     * @param permissionSets
     * @param dailyDurableStreamingApiEvents PushTopic event notifications delivered in the past 24 hours to all CometD clients
     * @param singleEmail Daily number of single emails that are sent to external email addresses
     * @param dailyAnalyticsDataflowJobExecutions
     * @param hourlyShortTermIdMapping Hourly new short-term external record ID mappings
     * @param streamingApiConcurrentClients Concurrent CometD clients (subscribers) across all channels and for all event types
     * @param durableStreamingApiConcurrentClients Concurrent CometD clients (subscribers) across all channels and for all event types
     * @param monthlyPlatformEvents Monthly entitlement and usage of high-volume platform event and Change Data Capture event delivery to CometD clients
     * @param dailyWorkflowEmails Daily workflow emails
     * @param package2VersionCreates
     * @param dailyDurableGenericStreamingApiEvents Generic events notifications delivered in the past 24 hours to all CometD clients
     * @param dataStorageMB Data storage in MB. (The API user must have the Manage Users permission.)
     * @param concurrentSyncReportRuns Concurrent synchronous report runs via REST API
     * @param hourlyDashboardRefreshes Hourly dashboard refreshes via REST API
     * @param dailyGenericStreamingApiEvents Generic events notifications delivered in the past 24 hours to all CometD clients
     * @param hourlySyncReportRuns Hourly synchronous report runs via REST API
     * @param hourlyDashboardResults Hourly REST API requests for dashboard results
     * @param dailyStandardVolumePlatformEvents Daily standard-volume platform event notifications delivered to CometD clients
     * @param dailyAsyncApexExecutions
     */
    public LimitsResults(LimitResult analyticsExternalDataSizeMB, LimitResult concurrentAsyncGetReportInstances,
            LimitResult concurrentSyncReportRuns, LimitResult dailyAnalyticsDataflowJobExecutions,
            LimitResult dailyAnalyticsUploadedFilesSizeMB, LimitResult dailyApiRequests,
            LimitResult dailyAsyncApexExecutions, LimitResult dailyBulkApiRequests,
            LimitResult dailyDurableGenericStreamingApiEvents, LimitResult dailyDurableStreamingApiEvents,
            LimitResult dailyGenericStreamingApiEvents, LimitResult dailyStandardVolumePlatformEvents,
            LimitResult dailyStreamingApiEvents, LimitResult dailyWorkflowEmails, LimitResult dataStorageMB,
            LimitResult durableStreamingApiConcurrentClients, LimitResult fileStorageMB,
            LimitResult hourlyAsyncReportRuns, LimitResult hourlyDashboardRefreshes, LimitResult hourlyDashboardResults,
            LimitResult hourlyDashboardStatuses, LimitResult hourlyLongTermIdMapping, LimitResult hourlyODataCallout,
            LimitResult hourlyShortTermIdMapping, LimitResult hourlySyncReportRuns, LimitResult hourlyTimeBasedWorkflow,
            LimitResult massEmail, LimitResult monthlyPlatformEvents, LimitResult package2VersionCreates,
            LimitResult permissionSets, LimitResult singleEmail, LimitResult streamingApiConcurrentClients) {
        super();
        this.analyticsExternalDataSizeMB = analyticsExternalDataSizeMB;
        this.concurrentAsyncGetReportInstances = concurrentAsyncGetReportInstances;
        this.concurrentSyncReportRuns = concurrentSyncReportRuns;
        this.dailyAnalyticsDataflowJobExecutions = dailyAnalyticsDataflowJobExecutions;
        this.dailyAnalyticsUploadedFilesSizeMB = dailyAnalyticsUploadedFilesSizeMB;
        this.dailyApiRequests = dailyApiRequests;
        this.dailyAsyncApexExecutions = dailyAsyncApexExecutions;
        this.dailyBulkApiRequests = dailyBulkApiRequests;
        this.dailyDurableGenericStreamingApiEvents = dailyDurableGenericStreamingApiEvents;
        this.dailyDurableStreamingApiEvents = dailyDurableStreamingApiEvents;
        this.dailyGenericStreamingApiEvents = dailyGenericStreamingApiEvents;
        this.dailyStandardVolumePlatformEvents = dailyStandardVolumePlatformEvents;
        this.dailyStreamingApiEvents = dailyStreamingApiEvents;
        this.dailyWorkflowEmails = dailyWorkflowEmails;
        this.dataStorageMB = dataStorageMB;
        this.durableStreamingApiConcurrentClients = durableStreamingApiConcurrentClients;
        this.fileStorageMB = fileStorageMB;
        this.hourlyAsyncReportRuns = hourlyAsyncReportRuns;
        this.hourlyDashboardRefreshes = hourlyDashboardRefreshes;
        this.hourlyDashboardResults = hourlyDashboardResults;
        this.hourlyDashboardStatuses = hourlyDashboardStatuses;
        this.hourlyLongTermIdMapping = hourlyLongTermIdMapping;
        this.hourlyODataCallout = hourlyODataCallout;
        this.hourlyShortTermIdMapping = hourlyShortTermIdMapping;
        this.hourlySyncReportRuns = hourlySyncReportRuns;
        this.hourlyTimeBasedWorkflow = hourlyTimeBasedWorkflow;
        this.massEmail = massEmail;
        this.monthlyPlatformEvents = monthlyPlatformEvents;
        this.package2VersionCreates = package2VersionCreates;
        this.permissionSets = permissionSets;
        this.singleEmail = singleEmail;
        this.streamingApiConcurrentClients = streamingApiConcurrentClients;
    }

    @JsonProperty("AnalyticsExternalDataSizeMB")
    public LimitResult getAnalyticsExternalDataSizeMB() {
        return analyticsExternalDataSizeMB;
    }

    @JsonProperty("ConcurrentAsyncGetReportInstances")
    public LimitResult getConcurrentAsyncGetReportInstances() {
        return concurrentAsyncGetReportInstances;
    }

    @JsonProperty("ConcurrentSyncReportRuns")
    public LimitResult getConcurrentSyncReportRuns() {
        return concurrentSyncReportRuns;
    }

    @JsonProperty("DailyAnalyticsDataflowJobExecutions")
    public LimitResult getDailyAnalyticsDataflowJobExecutions() {
        return dailyAnalyticsDataflowJobExecutions;
    }

    @JsonProperty("DailyAnalyticsUploadedFilesSizeMB")
    public LimitResult getDailyAnalyticsUploadedFilesSizeMB() {
        return dailyAnalyticsUploadedFilesSizeMB;
    }

    @JsonProperty("DailyApiRequests")
    public LimitResult getDailyApiRequests() {
        return dailyApiRequests;
    }

    @JsonProperty("DailyAsyncApexExecutions")
    public LimitResult getDailyAsyncApexExecutions() {
        return dailyAsyncApexExecutions;
    }

    @JsonProperty("DailyBulkApiRequests")
    public LimitResult getDailyBulkApiRequests() {
        return dailyBulkApiRequests;
    }

    @JsonProperty("DailyDurableGenericStreamingApiEvents")
    public LimitResult getDailyDurableGenericStreamingApiEvents() {
        return dailyDurableGenericStreamingApiEvents;
    }

    @JsonProperty("DailyDurableStreamingApiEvents")
    public LimitResult getDailyDurableStreamingApiEvents() {
        return dailyDurableStreamingApiEvents;
    }

    @JsonProperty("DailyGenericStreamingApiEvents")
    public LimitResult getDailyGenericStreamingApiEvents() {
        return dailyGenericStreamingApiEvents;
    }

    @JsonProperty("DailyStandardVolumePlatformEvents")
    public LimitResult getDailyStandardVolumePlatformEvents() {
        return dailyStandardVolumePlatformEvents;
    }

    @JsonProperty("DailyStreamingApiEvents")
    public LimitResult getDailyStreamingApiEvents() {
        return dailyStreamingApiEvents;
    }

    @JsonProperty("DailyWorkflowEmails")
    public LimitResult getDailyWorkflowEmails() {
        return dailyWorkflowEmails;
    }

    @JsonProperty("DataStorageMB")
    public LimitResult getDataStorageMB() {
        return dataStorageMB;
    }

    @JsonProperty("DurableStreamingApiConcurrentClients")
    public LimitResult getDurableStreamingApiConcurrentClients() {
        return durableStreamingApiConcurrentClients;
    }

    @JsonProperty("FileStorageMB")
    public LimitResult getFileStorageMB() {
        return fileStorageMB;
    }

    @JsonProperty("HourlyAsyncReportRuns")
    public LimitResult getHourlyAsyncReportRuns() {
        return hourlyAsyncReportRuns;
    }

    @JsonProperty("HourlyDashboardRefreshes")
    public LimitResult getHourlyDashboardRefreshes() {
        return hourlyDashboardRefreshes;
    }

    @JsonProperty("HourlyDashboardResults")
    public LimitResult getHourlyDashboardResults() {
        return hourlyDashboardResults;
    }

    @JsonProperty("HourlyDashboardStatuses")
    public LimitResult getHourlyDashboardStatuses() {
        return hourlyDashboardStatuses;
    }

    @JsonProperty("HourlyLongTermIdMapping")
    public LimitResult getHourlyLongTermIdMapping() {
        return hourlyLongTermIdMapping;
    }

    @JsonProperty("HourlyODataCallout")
    public LimitResult getHourlyODataCallout() {
        return hourlyODataCallout;
    }

    @JsonProperty("HourlyShortTermIdMapping")
    public LimitResult getHourlyShortTermIdMapping() {
        return hourlyShortTermIdMapping;
    }

    @JsonProperty("HourlySyncReportRuns")
    public LimitResult getHourlySyncReportRuns() {
        return hourlySyncReportRuns;
    }

    @JsonProperty("HourlyTimeBasedWorkflow")
    public LimitResult getHourlyTimeBasedWorkflow() {
        return hourlyTimeBasedWorkflow;
    }

    @JsonProperty("MassEmail")
    public LimitResult getMassEmail() {
        return massEmail;
    }

    @JsonProperty("MonthlyPlatformEvents")
    public LimitResult getMonthlyPlatformEvents() {
        return monthlyPlatformEvents;
    }

    @JsonProperty("Package2VersionCreates")
    public LimitResult getPackage2VersionCreates() {
        return package2VersionCreates;
    }

    @JsonProperty("PermissionSets")
    public LimitResult getPermissionSets() {
        return permissionSets;
    }

    @JsonProperty("SingleEmail")
    public LimitResult getSingleEmail() {
        return singleEmail;
    }

    @JsonProperty("StreamingApiConcurrentClients")
    public LimitResult getStreamingApiConcurrentClients() {
        return streamingApiConcurrentClients;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

}