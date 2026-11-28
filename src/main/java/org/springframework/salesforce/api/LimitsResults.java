/**
 * Copyright (C) 2019-2026 https://github.com/jottley/spring-salesforce
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
package org.springframework.salesforce.api;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This is the representation of the results of a Limits API call. It models the limits returned by
 * the Limits resource as of the v68.0 test fixtures; limits added or removed in other API versions
 * are available from {@link #getAdditionalProperties()}. If there are new/old key/values needed
 * based on newer or older versions of the API please open an issue.
 *
 * <p>Per-connected-app usage that Salesforce nests inside a limit (for example inside {@code
 * DailyApiRequests}) is found in that {@link LimitResult}'s {@link
 * LimitResult#getAdditionalProperties()}, keyed by app name.
 *
 * @author Jared Ottley
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LimitsResults implements Serializable {

  /** The {@code AnalyticsExternalDataSizeMB} limit. */
  @JsonProperty("AnalyticsExternalDataSizeMB")
  private LimitResult analyticsExternalDataSizeMB;

  /** The {@code ConcurrentAsyncGetReportInstances} limit. */
  @JsonProperty("ConcurrentAsyncGetReportInstances")
  private LimitResult concurrentAsyncGetReportInstances;

  /** The {@code ConcurrentSyncReportRuns} limit. */
  @JsonProperty("ConcurrentSyncReportRuns")
  private LimitResult concurrentSyncReportRuns;

  /** The {@code DailyAnalyticsDataflowJobExecutions} limit. */
  @JsonProperty("DailyAnalyticsDataflowJobExecutions")
  private LimitResult dailyAnalyticsDataflowJobExecutions;

  /** The {@code DailyAnalyticsUploadedFilesSizeMB} limit. */
  @JsonProperty("DailyAnalyticsUploadedFilesSizeMB")
  private LimitResult dailyAnalyticsUploadedFilesSizeMB;

  /** The {@code DailyApiRequests} limit. */
  @JsonProperty("DailyApiRequests")
  private LimitResult dailyApiRequests;

  /** The {@code DailyAsyncApexExecutions} limit. */
  @JsonProperty("DailyAsyncApexExecutions")
  private LimitResult dailyAsyncApexExecutions;

  /** The {@code DailyBulkApiRequests} limit. */
  @JsonProperty("DailyBulkApiRequests")
  private LimitResult dailyBulkApiRequests;

  /** The {@code DailyDurableGenericStreamingApiEvents} limit. */
  @JsonProperty("DailyDurableGenericStreamingApiEvents")
  private LimitResult dailyDurableGenericStreamingApiEvents;

  /** The {@code DailyDurableStreamingApiEvents} limit. */
  @JsonProperty("DailyDurableStreamingApiEvents")
  private LimitResult dailyDurableStreamingApiEvents;

  /** The {@code DailyGenericStreamingApiEvents} limit. */
  @JsonProperty("DailyGenericStreamingApiEvents")
  private LimitResult dailyGenericStreamingApiEvents;

  /** The {@code DailyStandardVolumePlatformEvents} limit. */
  @JsonProperty("DailyStandardVolumePlatformEvents")
  private LimitResult dailyStandardVolumePlatformEvents;

  /** The {@code DailyStreamingApiEvents} limit. */
  @JsonProperty("DailyStreamingApiEvents")
  private LimitResult dailyStreamingApiEvents;

  /** The {@code DailyWorkflowEmails} limit. */
  @JsonProperty("DailyWorkflowEmails")
  private LimitResult dailyWorkflowEmails;

  /** The {@code DataStorageMB} limit. */
  @JsonProperty("DataStorageMB")
  private LimitResult dataStorageMB;

  /** The {@code DurableStreamingApiConcurrentClients} limit. */
  @JsonProperty("DurableStreamingApiConcurrentClients")
  private LimitResult durableStreamingApiConcurrentClients;

  /** The {@code FileStorageMB} limit. */
  @JsonProperty("FileStorageMB")
  private LimitResult fileStorageMB;

  /** The {@code HourlyAsyncReportRuns} limit. */
  @JsonProperty("HourlyAsyncReportRuns")
  private LimitResult hourlyAsyncReportRuns;

  /** The {@code HourlyDashboardRefreshes} limit. */
  @JsonProperty("HourlyDashboardRefreshes")
  private LimitResult hourlyDashboardRefreshes;

  /** The {@code HourlyDashboardResults} limit. */
  @JsonProperty("HourlyDashboardResults")
  private LimitResult hourlyDashboardResults;

  /** The {@code HourlyDashboardStatuses} limit. */
  @JsonProperty("HourlyDashboardStatuses")
  private LimitResult hourlyDashboardStatuses;

  /** The {@code HourlyLongTermIdMapping} limit. */
  @JsonProperty("HourlyLongTermIdMapping")
  private LimitResult hourlyLongTermIdMapping;

  /** The {@code HourlyODataCallout} limit. */
  @JsonProperty("HourlyODataCallout")
  private LimitResult hourlyODataCallout;

  /** The {@code HourlyShortTermIdMapping} limit. */
  @JsonProperty("HourlyShortTermIdMapping")
  private LimitResult hourlyShortTermIdMapping;

  /** The {@code HourlySyncReportRuns} limit. */
  @JsonProperty("HourlySyncReportRuns")
  private LimitResult hourlySyncReportRuns;

  /** The {@code HourlyTimeBasedWorkflow} limit. */
  @JsonProperty("HourlyTimeBasedWorkflow")
  private LimitResult hourlyTimeBasedWorkflow;

  /** The {@code MassEmail} limit. */
  @JsonProperty("MassEmail")
  private LimitResult massEmail;

  /** The {@code MonthlyPlatformEvents} limit. */
  @JsonProperty("MonthlyPlatformEvents")
  private LimitResult monthlyPlatformEvents;

  /** The {@code Package2VersionCreates} limit. */
  @JsonProperty("Package2VersionCreates")
  private LimitResult package2VersionCreates;

  /** The {@code PermissionSets} limit. */
  @JsonProperty("PermissionSets")
  private LimitResult permissionSets;

  /** The {@code SingleEmail} limit. */
  @JsonProperty("SingleEmail")
  private LimitResult singleEmail;

  /** The {@code StreamingApiConcurrentClients} limit. */
  @JsonProperty("StreamingApiConcurrentClients")
  private LimitResult streamingApiConcurrentClients;

  /** Limits that are not modeled by a dedicated field, keyed by name. */
  @JsonIgnore
  private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

  /** Serialization version identifier. */
  private static final long serialVersionUID = 3810468125069690661L;

  /**
   * Mapper used to convert raw limit maps from the additional properties into {@link LimitResult}
   * instances.
   */
  private static final ObjectMapper LIMIT_MAPPER = new ObjectMapper();

  /** No args constructor for use in serialization. */
  public LimitsResults() {}

  /**
   * Creates a result holding every modeled limit. Each argument is the {@code Max} and {@code
   * Remaining} pair for the org limit of the same name.
   *
   * @param analyticsExternalDataSizeMB the analyticsExternalDataSizeMB limit
   * @param concurrentAsyncGetReportInstances the concurrentAsyncGetReportInstances limit
   * @param concurrentSyncReportRuns the concurrentSyncReportRuns limit
   * @param dailyAnalyticsDataflowJobExecutions the dailyAnalyticsDataflowJobExecutions limit
   * @param dailyAnalyticsUploadedFilesSizeMB the dailyAnalyticsUploadedFilesSizeMB limit
   * @param dailyApiRequests the dailyApiRequests limit
   * @param dailyAsyncApexExecutions the dailyAsyncApexExecutions limit
   * @param dailyBulkApiRequests the dailyBulkApiRequests limit
   * @param dailyDurableGenericStreamingApiEvents the dailyDurableGenericStreamingApiEvents limit
   * @param dailyDurableStreamingApiEvents the dailyDurableStreamingApiEvents limit
   * @param dailyGenericStreamingApiEvents the dailyGenericStreamingApiEvents limit
   * @param dailyStandardVolumePlatformEvents the dailyStandardVolumePlatformEvents limit
   * @param dailyStreamingApiEvents the dailyStreamingApiEvents limit
   * @param dailyWorkflowEmails the dailyWorkflowEmails limit
   * @param dataStorageMB the dataStorageMB limit
   * @param durableStreamingApiConcurrentClients the durableStreamingApiConcurrentClients limit
   * @param fileStorageMB the fileStorageMB limit
   * @param hourlyAsyncReportRuns the hourlyAsyncReportRuns limit
   * @param hourlyDashboardRefreshes the hourlyDashboardRefreshes limit
   * @param hourlyDashboardResults the hourlyDashboardResults limit
   * @param hourlyDashboardStatuses the hourlyDashboardStatuses limit
   * @param hourlyLongTermIdMapping the hourlyLongTermIdMapping limit
   * @param hourlyODataCallout the hourlyODataCallout limit
   * @param hourlyShortTermIdMapping the hourlyShortTermIdMapping limit
   * @param hourlySyncReportRuns the hourlySyncReportRuns limit
   * @param hourlyTimeBasedWorkflow the hourlyTimeBasedWorkflow limit
   * @param massEmail the massEmail limit
   * @param monthlyPlatformEvents the monthlyPlatformEvents limit
   * @param package2VersionCreates the package2VersionCreates limit
   * @param permissionSets the permissionSets limit
   * @param singleEmail the singleEmail limit
   * @param streamingApiConcurrentClients the streamingApiConcurrentClients limit
   */
  public LimitsResults(
      LimitResult analyticsExternalDataSizeMB,
      LimitResult concurrentAsyncGetReportInstances,
      LimitResult concurrentSyncReportRuns,
      LimitResult dailyAnalyticsDataflowJobExecutions,
      LimitResult dailyAnalyticsUploadedFilesSizeMB,
      LimitResult dailyApiRequests,
      LimitResult dailyAsyncApexExecutions,
      LimitResult dailyBulkApiRequests,
      LimitResult dailyDurableGenericStreamingApiEvents,
      LimitResult dailyDurableStreamingApiEvents,
      LimitResult dailyGenericStreamingApiEvents,
      LimitResult dailyStandardVolumePlatformEvents,
      LimitResult dailyStreamingApiEvents,
      LimitResult dailyWorkflowEmails,
      LimitResult dataStorageMB,
      LimitResult durableStreamingApiConcurrentClients,
      LimitResult fileStorageMB,
      LimitResult hourlyAsyncReportRuns,
      LimitResult hourlyDashboardRefreshes,
      LimitResult hourlyDashboardResults,
      LimitResult hourlyDashboardStatuses,
      LimitResult hourlyLongTermIdMapping,
      LimitResult hourlyODataCallout,
      LimitResult hourlyShortTermIdMapping,
      LimitResult hourlySyncReportRuns,
      LimitResult hourlyTimeBasedWorkflow,
      LimitResult massEmail,
      LimitResult monthlyPlatformEvents,
      LimitResult package2VersionCreates,
      LimitResult permissionSets,
      LimitResult singleEmail,
      LimitResult streamingApiConcurrentClients) {
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

  /**
   * Returns the {@code AnalyticsExternalDataSizeMB} limit: the daily limit, in MB, on external data
   * uploaded to Analytics through the REST API. {@code Max} is the amount allowed and {@code
   * Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("AnalyticsExternalDataSizeMB")
  public LimitResult getAnalyticsExternalDataSizeMB() {
    return analyticsExternalDataSizeMB;
  }

  /**
   * Returns the {@code ConcurrentAsyncGetReportInstances} limit: Concurrent REST API requests for
   * results of asynchronous report runs. {@code Max} is the amount allowed and {@code Remaining} is
   * the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("ConcurrentAsyncGetReportInstances")
  public LimitResult getConcurrentAsyncGetReportInstances() {
    return concurrentAsyncGetReportInstances;
  }

  /**
   * Returns the {@code ConcurrentSyncReportRuns} limit: Concurrent synchronous report runs via the
   * REST API. {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("ConcurrentSyncReportRuns")
  public LimitResult getConcurrentSyncReportRuns() {
    return concurrentSyncReportRuns;
  }

  /**
   * Returns the {@code DailyAnalyticsDataflowJobExecutions} limit: CRM Analytics dataflow job
   * executions allowed in a rolling 24 hours. {@code Max} is the amount allowed and {@code
   * Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DailyAnalyticsDataflowJobExecutions")
  public LimitResult getDailyAnalyticsDataflowJobExecutions() {
    return dailyAnalyticsDataflowJobExecutions;
  }

  /**
   * Returns the {@code DailyAnalyticsUploadedFilesSizeMB} limit: the daily cumulative size, in MB,
   * of Analytics files that can be uploaded. {@code Max} is the amount allowed and {@code
   * Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DailyAnalyticsUploadedFilesSizeMB")
  public LimitResult getDailyAnalyticsUploadedFilesSizeMB() {
    return dailyAnalyticsUploadedFilesSizeMB;
  }

  /**
   * Returns the {@code DailyApiRequests} limit: API requests allowed in a rolling 24 hours. {@code
   * Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DailyApiRequests")
  public LimitResult getDailyApiRequests() {
    return dailyApiRequests;
  }

  /**
   * Returns the {@code DailyAsyncApexExecutions} limit: Asynchronous Apex executions (batch,
   * future, queueable and scheduled Apex) allowed in a rolling 24 hours. {@code Max} is the amount
   * allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DailyAsyncApexExecutions")
  public LimitResult getDailyAsyncApexExecutions() {
    return dailyAsyncApexExecutions;
  }

  /**
   * Returns the {@code DailyBulkApiRequests} limit: Bulk API requests allowed in a rolling 24
   * hours. {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DailyBulkApiRequests")
  public LimitResult getDailyBulkApiRequests() {
    return dailyBulkApiRequests;
  }

  /**
   * Returns the {@code DailyDurableGenericStreamingApiEvents} limit: Durable generic streaming
   * event notifications delivered to all CometD clients in a rolling 24 hours. {@code Max} is the
   * amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DailyDurableGenericStreamingApiEvents")
  public LimitResult getDailyDurableGenericStreamingApiEvents() {
    return dailyDurableGenericStreamingApiEvents;
  }

  /**
   * Returns the {@code DailyDurableStreamingApiEvents} limit: Durable PushTopic streaming event
   * notifications delivered to all CometD clients in a rolling 24 hours. {@code Max} is the amount
   * allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DailyDurableStreamingApiEvents")
  public LimitResult getDailyDurableStreamingApiEvents() {
    return dailyDurableStreamingApiEvents;
  }

  /**
   * Returns the {@code DailyGenericStreamingApiEvents} limit: Generic streaming event notifications
   * delivered to all CometD clients in a rolling 24 hours. {@code Max} is the amount allowed and
   * {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DailyGenericStreamingApiEvents")
  public LimitResult getDailyGenericStreamingApiEvents() {
    return dailyGenericStreamingApiEvents;
  }

  /**
   * Returns the {@code DailyStandardVolumePlatformEvents} limit: Standard-volume platform event
   * notifications delivered to CometD clients in a rolling 24 hours. {@code Max} is the amount
   * allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DailyStandardVolumePlatformEvents")
  public LimitResult getDailyStandardVolumePlatformEvents() {
    return dailyStandardVolumePlatformEvents;
  }

  /**
   * Returns the {@code DailyStreamingApiEvents} limit: PushTopic event notifications delivered to
   * all CometD clients in a rolling 24 hours. {@code Max} is the amount allowed and {@code
   * Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DailyStreamingApiEvents")
  public LimitResult getDailyStreamingApiEvents() {
    return dailyStreamingApiEvents;
  }

  /**
   * Returns the {@code DailyWorkflowEmails} limit: Workflow emails that can be sent in a rolling 24
   * hours. {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DailyWorkflowEmails")
  public LimitResult getDailyWorkflowEmails() {
    return dailyWorkflowEmails;
  }

  /**
   * Returns the {@code DataStorageMB} limit: Data storage, in MB. {@code Max} is the amount allowed
   * and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DataStorageMB")
  public LimitResult getDataStorageMB() {
    return dataStorageMB;
  }

  /**
   * Returns the {@code DurableStreamingApiConcurrentClients} limit: Concurrent CometD clients
   * (subscribers) of durable streaming across all channels and event types. {@code Max} is the
   * amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("DurableStreamingApiConcurrentClients")
  public LimitResult getDurableStreamingApiConcurrentClients() {
    return durableStreamingApiConcurrentClients;
  }

  /**
   * Returns the {@code FileStorageMB} limit: File storage, in MB. {@code Max} is the amount allowed
   * and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("FileStorageMB")
  public LimitResult getFileStorageMB() {
    return fileStorageMB;
  }

  /**
   * Returns the {@code HourlyAsyncReportRuns} limit: Asynchronous report runs via the REST API
   * allowed per hour. {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("HourlyAsyncReportRuns")
  public LimitResult getHourlyAsyncReportRuns() {
    return hourlyAsyncReportRuns;
  }

  /**
   * Returns the {@code HourlyDashboardRefreshes} limit: Dashboard refreshes via the REST API
   * allowed per hour. {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("HourlyDashboardRefreshes")
  public LimitResult getHourlyDashboardRefreshes() {
    return hourlyDashboardRefreshes;
  }

  /**
   * Returns the {@code HourlyDashboardResults} limit: REST API requests for dashboard results
   * allowed per hour. {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("HourlyDashboardResults")
  public LimitResult getHourlyDashboardResults() {
    return hourlyDashboardResults;
  }

  /**
   * Returns the {@code HourlyDashboardStatuses} limit: Dashboard status requests via the REST API
   * allowed per hour. {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("HourlyDashboardStatuses")
  public LimitResult getHourlyDashboardStatuses() {
    return hourlyDashboardStatuses;
  }

  /**
   * Returns the {@code HourlyLongTermIdMapping} limit: New long-term external record ID mappings
   * allowed per hour. {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("HourlyLongTermIdMapping")
  public LimitResult getHourlyLongTermIdMapping() {
    return hourlyLongTermIdMapping;
  }

  /**
   * Returns the {@code HourlyODataCallout} limit: OData callouts allowed per hour. {@code Max} is
   * the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("HourlyODataCallout")
  public LimitResult getHourlyODataCallout() {
    return hourlyODataCallout;
  }

  /**
   * Returns the {@code HourlyShortTermIdMapping} limit: New short-term external record ID mappings
   * allowed per hour. {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("HourlyShortTermIdMapping")
  public LimitResult getHourlyShortTermIdMapping() {
    return hourlyShortTermIdMapping;
  }

  /**
   * Returns the {@code HourlySyncReportRuns} limit: Synchronous report runs via the REST API
   * allowed per hour. {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("HourlySyncReportRuns")
  public LimitResult getHourlySyncReportRuns() {
    return hourlySyncReportRuns;
  }

  /**
   * Returns the {@code HourlyTimeBasedWorkflow} limit: the hourly limit on workflow time triggers.
   * {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("HourlyTimeBasedWorkflow")
  public LimitResult getHourlyTimeBasedWorkflow() {
    return hourlyTimeBasedWorkflow;
  }

  /**
   * Returns the {@code MassEmail} limit: Mass emails sent to external addresses using Apex or APIs
   * per day. {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("MassEmail")
  public LimitResult getMassEmail() {
    return massEmail;
  }

  /**
   * Returns the {@code MonthlyPlatformEvents} limit: Monthly entitlement and usage of high-volume
   * platform event and Change Data Capture event delivery to CometD clients. {@code Max} is the
   * amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("MonthlyPlatformEvents")
  public LimitResult getMonthlyPlatformEvents() {
    return monthlyPlatformEvents;
  }

  /**
   * Returns the {@code Package2VersionCreates} limit: the daily number of package versions that can
   * be created. {@code Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("Package2VersionCreates")
  public LimitResult getPackage2VersionCreates() {
    return package2VersionCreates;
  }

  /**
   * Returns the {@code PermissionSets} limit: the number of permission sets the org allows
   * (available in API version 41.0 and later). {@code Max} is the amount allowed and {@code
   * Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("PermissionSets")
  public LimitResult getPermissionSets() {
    return permissionSets;
  }

  /**
   * Returns the {@code SingleEmail} limit: Single emails sent to external addresses per day. {@code
   * Max} is the amount allowed and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("SingleEmail")
  public LimitResult getSingleEmail() {
    return singleEmail;
  }

  /**
   * Returns the {@code StreamingApiConcurrentClients} limit: Concurrent CometD clients
   * (subscribers) across all channels and for all event types. {@code Max} is the amount allowed
   * and {@code Remaining} is the amount left.
   *
   * @return the limit, as reported by Salesforce
   */
  @JsonProperty("StreamingApiConcurrentClients")
  public LimitResult getStreamingApiConcurrentClients() {
    return streamingApiConcurrentClients;
  }

  /**
   * Returns the limits that are not modeled by a dedicated getter, keyed by the limit name
   * Salesforce sent. Values are the parsed JSON (usually a map with {@code Max} and {@code
   * Remaining}). The returned map is the live internal map, not a copy.
   *
   * @return the additional, unmodeled limits
   */
  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  /**
   * Keeps any limit that is not one of the modeled ones, such as a limit Salesforce introduced in a
   * newer API version. The value is left as parsed JSON (usually a map with {@code Max} and {@code
   * Remaining}) rather than a {@link LimitResult}, so a limit with an unexpected shape can never
   * break reading the rest of the response.
   *
   * @param name the limit name as Salesforce sends it
   * @param value the parsed JSON value for that limit
   */
  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  /**
   * Returns a limit that is not one of the modeled ones as a {@link LimitResult}.
   *
   * @param name the limit name exactly as Salesforce sends it
   * @return the limit, or empty if there is no such limit or the value is not an object with both
   *     {@code Max} and {@code Remaining} that can be read as integers (for example a non-numeric
   *     or out-of-range value). A {@code null} {@code Max} or {@code Remaining} is read as 0.
   */
  public Optional<LimitResult> getAdditionalLimit(String name) {
    Object value = this.additionalProperties.get(name);
    if (value instanceof Map<?, ?> map && map.containsKey("Max") && map.containsKey("Remaining")) {
      try {
        return Optional.of(LIMIT_MAPPER.convertValue(map, LimitResult.class));
      } catch (IllegalArgumentException e) {
        // Max/Remaining present but not numbers; treat it as not a limit
        return Optional.empty();
      }
    }
    return Optional.empty();
  }
}
