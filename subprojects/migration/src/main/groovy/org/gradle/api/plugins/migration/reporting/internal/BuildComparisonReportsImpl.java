/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.plugins.migration.reporting.internal;

import org.gradle.api.Task;
import org.gradle.api.plugins.migration.model.render.internal.BuildComparisonResultRenderer;
import org.gradle.api.plugins.migration.reporting.BuildComparisonReports;
import org.gradle.api.reporting.SingleFileReport;
import org.gradle.api.reporting.internal.TaskReportContainer;

import java.io.Writer;

public class BuildComparisonReportsImpl extends TaskReportContainer<SingleFileReport> implements BuildComparisonReports {

    public BuildComparisonReportsImpl(Task task, BuildComparisonResultRenderer<Writer> htmlRenderer) {
        super(SingleFileReport.class, task);
        add(BuildComparisonResultRendererReport.class, "html", task, htmlRenderer).setEnabled(true);
    }

    public BuildComparisonResultRendererReport getHtml() {
        return (BuildComparisonResultRendererReport) super.getByName("html");
    }

}
