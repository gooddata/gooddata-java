/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.gooddata.report.ReportExportFormat;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Attachment to {@link ScheduledMail} represents report-related information for the schedule.
 */
public class ReportAttachment extends Attachment {

    private final Collection<String> formats;
    private final Map<String, String> exportOptions;

    @JsonCreator
    protected ReportAttachment(
            @JsonProperty("uri") String uri,
            @JsonProperty("exportOptions") Map<String, String> exportOptions,
            @JsonProperty("formats") String... formats
            ) {
        super(uri);
        this.exportOptions = exportOptions;
        this.formats = Arrays.asList(formats);
    }

    protected ReportAttachment(String uri, Map<String, String> exportOptions, ReportExportFormat... formats) {
        this(uri, exportOptions, ReportExportFormat.arrayToStringArray(formats));
    }

    /**
     *  Options which modify default export behavior. Due to variety of
     *  export formats options only work for explicitly listed
     *  format types.
     *
     * <ul>
     *   <li>pageOrientation
     *     <ul>
     *       <li>set page orientation</li>
     *       <li>default value: 'portrait'</li>
     *       <li>supported in: tabular PDF</li>
     *     </ul>
     *   </li>
     *
     *   <li>optimalColumnWidth
     *     <ul>
     *       <li>set 'yes' to automatically resize all columns to fit cell's content</li>
     *       <li>default is: 'no' (do not auto resize)</li>
     *       <li>supported in: tabular PDF</li>
     *     </ul>
     *   </li>
     *
     *   <li>pageScalePercentage
     *     <ul>
     *       <li>down-scaling factor (in percent), 100 means no scale-down</li>
     *       <li>may not be combined with scaleToPages, scaleToPagesX and scaleToPagesY</li>
     *       <li>default is: 100</li>
     *       <li>supported in: tabular PDF</li>
     *     </ul>
     *   </li>
     *
     *   <li>scaleToPages
     *     <ul>
     *       <li>total number of pages of target PDF file</li>
     *       <li>may not be combined with pageScalePercentage, scaleToPagesX and scaleToPagesY</li>
     *       <li>default is: unlimited</li>
     *       <li>supported in: tabular PDF</li>
     *     </ul>
     *   </li>
     *
     *   <li>scaleToPagesX
     *     <ul>
     *       <li>number of horizontal pages of target PDF file</li>
     *       <li>may not be combined with pageScalePercentage and scaleToPages, but may be combined with scaleToPagesY</li>
     *       <li>default is: unlimited</li>
     *       <li>supported in: tabular PDF</li>
     *     </ul>
     *   </li>
     *
     *   <li>scaleToPagesY
     *     <ul>
     *       <li>number of vertical pages of target PDF file</li>
     *       <li>may not be combined with pageScalePercentage and scaleToPages, but may be combined with scaleToPagesX</li>
     *       <li>default is: unlimited</li>
     *       <li>supported in: tabular PDF</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @return map of export options
     */
    public Map<String, String> getExportOptions() { return exportOptions; }

    public Collection<String> getFormats() { return formats; }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportAttachment that = (ReportAttachment) o;

        if (formats != null ? !formats.equals(that.formats) : that.formats != null) return false;
        return !(exportOptions != null ? !exportOptions.equals(that.exportOptions) : that.exportOptions != null);

    }

    @Override
    public int hashCode() {
        int result = formats != null ? formats.hashCode() : 0;
        result = 31 * result + (exportOptions != null ? exportOptions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new GoodDataToStringBuilder(this).append("uri", getUri()).toString();
    }
}
