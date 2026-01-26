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
package org.springframework.salesforce.api.impl.json;

import org.springframework.salesforce.api.ApiVersion;
import org.springframework.salesforce.api.Field;
import org.springframework.salesforce.api.Photo;
import org.springframework.salesforce.api.PickListEntry;
import org.springframework.salesforce.api.QueryResult;
import org.springframework.salesforce.api.RecordTypeInfo;
import org.springframework.salesforce.api.Relationship;
import org.springframework.salesforce.api.ResultItem;
import org.springframework.salesforce.api.SObjectDetail;
import org.springframework.salesforce.api.SObjectSummary;
import org.springframework.salesforce.api.SalesforceProfile;
import org.springframework.salesforce.api.SalesforceUserDetails;
import org.springframework.salesforce.api.Status;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;


/**
 * Jackson module for api version v23.0.
 *
 * @author Umut Utkan
 * @author Jared Ottley
 * @author Alexandra Leahu
 */
public class SalesforceModule extends SimpleModule
{

    public SalesforceModule() {
        super("SalesforceModule");
    }

    @Override
    public void setupModule(Module.SetupContext context) {
        super.setupModule(context);

        context.setMixInAnnotations(ApiVersion.class, ApiVersionMixin.class);
        context.setMixInAnnotations(SalesforceProfile.class, SalesforceProfileMixin.class);
        context.setMixInAnnotations(SalesforceUserDetails.class, SalesforceUserDetailsMixin.class);
        context.setMixInAnnotations(Photo.class, PhotoMixin.class);
        context.setMixInAnnotations(Status.class, StatusMixin.class);
        context.setMixInAnnotations(SObjectSummary.class, SObjectSummaryMixin.class);
        context.setMixInAnnotations(RecordTypeInfo.class, RecordTypeInfoMixin.class);
        context.setMixInAnnotations(Relationship.class, RelationshipMixin.class);
        context.setMixInAnnotations(PickListEntry.class, PickListEntryMixin.class);
        context.setMixInAnnotations(Field.class, FieldMixin.class);
        context.setMixInAnnotations(SObjectDetail.class, SObjectDetailMixin.class);
        context.setMixInAnnotations(QueryResult.class, QueryResultMixin.class);
        context.setMixInAnnotations(ResultItem.class, ResultItemMixin.class);
    }

}
