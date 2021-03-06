/*
 * Copyright (c) 2013, Zenoss and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Zenoss or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.zenoss.app.metricservice.api.model;

import com.google.common.base.Joiner;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.zenoss.app.metricservice.api.impl.Utils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class MetricSpecification {

    public static final Aggregator DEFAULT_AGGREGATOR = Aggregator.avg;

    @JsonProperty
    private String id = null;

    @JsonProperty
    private String metric = null;

    @JsonProperty
    private String name = null;

    @JsonProperty
    private Aggregator aggregator = DEFAULT_AGGREGATOR;

    @JsonProperty
    private InterpolatorType interpolator = InterpolatorType.none;

    @JsonProperty
    private String downsample = null;

    @JsonProperty
    private Boolean rate = Boolean.FALSE;

    @JsonProperty
    private RateOptions rateOptions = null;

    @JsonProperty
    private String expression = null;

    @JsonProperty
    private Map<String, List<String>> tags = new HashMap<>();

    @JsonProperty
    private Map<String, List<String>> filters = new HashMap<>();

    @JsonProperty
    private boolean emit = true;

    /**
     * @return id string
     */
    public String getId() {
        return id;
    }

    /**
     *  @param id - id to set for query
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @param expression
     *            the expression to set
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * @return the metric
     */
    public final String getMetric() {
        return metric;
    }

    /**
     * @param metric
     *            the metric to set
     */
    public final void setMetric(String metric) {
        this.metric = metric;
    }

    /**
     * @return the aggregator
     */
    public final Aggregator getAggregator() {
        return aggregator;
    }

    /**
     * @param aggregator
     *            the aggregator to set
     */
    public final void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    public InterpolatorType getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(InterpolatorType interpolator) {
        this.interpolator = interpolator;
    }

    /**
     * @return the downsample
     */
    public final String getDownsample() {
        return downsample;
    }

    /**
     * @param downsample
     *            the downsample to set
     */
    public final void setDownsample(String downsample) {
        this.downsample = downsample;
    }

    /**
     * @return the rate
     */
    public final Boolean getRate() {
        return rate;
    }

    /**
     * @param rate
     *            the rate to set
     */
    public final void setRate(Boolean rate) {
        this.rate = rate;
    }

    /**
     * @return the rateOptions
     */
    public final RateOptions getRateOptions() {
        return rateOptions;
    }

    /**
     * @param rateOptions
     *            the rateOptions to set
     */
    public final void setRateOptions(RateOptions rateOptions) {
        this.rateOptions = rateOptions;
    }

    /**
     * @return the tags
     */
    public final Map<String, List<String>> getTags() {
        if (null == tags) {
            initializeTags();
        }
        return tags;
    }

    private void initializeTags() {
        tags = new HashMap<>();
    }

    /**
     * @param tags
     *            the tags to set
     */
    public final void setTags(Map<String, List<String>> tags) {
        if (null == tags) {
            initializeTags();
        } else {
            this.tags = tags;
        }
    }

    /**
     * @param newTags
     *            the tags to merge with the existing tags.
     */
    public final void mergeTags(Map<String, List<String>> newTags) {
        if (null == newTags) {
            return;
        }

        for (Map.Entry<String, List<String>> tagEntry : newTags.entrySet()) {
            if (tags.containsKey(tagEntry.getKey())) {
                List<String> tagList = tags.get(tagEntry.getKey());
                tagList.addAll(tagEntry.getValue());
            } else {
                tags.put(tagEntry.getKey(), tagEntry.getValue());
            }
        }
    }

    /**
     * @return the filters
     */
    public final Map<String, List<String>> getFilters() {
        if (null == filters) {
            initializeFilters();
        }
        return filters;
    }

    private void initializeFilters() {
        filters = new HashMap<>();
    }

    /**
     * @param filters
     *            the filters to set
     */
    public final void setFilters(Map<String, List<String>> filters) {
        if (null == filters) {
            initializeFilters();
        } else {
            this.filters = filters;
        }
    }

    /**
     * @param newFilters
     *            the filters to merge with the existing filters.
     */
    public final void mergeFilters(Map<String, List<String>> newFilters) {
        if (null == newFilters) {
            return;
        }

        for (Map.Entry<String, List<String>> filterEntry : newFilters.entrySet()) {
            if (filters.containsKey(filterEntry.getKey())) {
                List<String> filterList = filters.get(filterEntry.getKey());
                filterList.addAll(filterEntry.getValue());
            } else {
                filters.put(filterEntry.getKey(), filterEntry.getValue());
            }
        }
    }

    public String getMetricOrName() {
        if (metric != null) {
            return metric;
        }
        return name;
    }

    public String getNameOrMetric() {
        if (name != null) {
            return name;
        }
        return metric;
    }

    public boolean getEmit() {
        return emit;
    }

    public void setEmit(boolean emit) {
        this.emit = emit;
    }

    /*
     * These methods are mainly to support the testability 
     */

    /**
     * @param value
     * @return
     */
    private static Map<String, List<String>> parseTags(String value) {
        Map<String, List<String>> tags = new HashMap<>();

        if (value == null || (value = value.trim()).length() == 0) {
            return tags;
        }
        String[] pairs = value.substring(1, value.length() - 1).split(",");
        for (String pair : pairs) {
            String[] terms = pair.split("=", 2);
            List<String> vals = new ArrayList<>();
            vals.add(terms[1].trim());
            tags.put(terms[0].trim(), vals);
        }
        return tags;
    }

    private static RateOptions parseRateOptions(String content) {
        RateOptions options = new RateOptions();

        // Format is "counter[,[max][,reset]]"
        String[] parts = content.substring("rate{".length(),
            content.length() - 1).split(",");

        if (parts[0].trim().length() == 0 || parts.length > 3) {
            throw new RateFormatException("invalid number of options");
        }

        if ("dropcounter".equals(parts[0].trim())) {
            options.setDropResets(true);
            options.setCounter(true);
            options.setCounterMax(null);
            options.setResetThreshold(null); 
        } 
        else if ("counter".equals(parts[0].trim())) {
            options.setCounter(true);
            options.setDropResets(false);
        } 
        else {
            throw new RateFormatException(
                "first option must be value \"counter\" or \"dropcounter\"");
        }

        if (parts.length > 1) {
            // We have at least a max value, so this should either be an empty
            // string or a long value. Only set value if length > 0
            String v = parts[1].trim();
            if (v.length() > 0) {
                try {
                    options.setCounterMax(Long.parseLong(v));
                } catch (NumberFormatException nfe) {
                    throw new RateFormatException(
                        String.format(
                            "Unable to parse counter max value '%s' as type long",
                            v), nfe);
                }
            }

            // We have a reset value, again only parse if the length > 0
            if (parts.length > 2) {
                v = parts[2].trim();
                if (v.length() > 0) {
                    try {
                        options.setResetThreshold(Long.parseLong(v));
                    } catch (NumberFormatException nfe) {
                        throw new RateFormatException(
                            String.format(
                                "Unable to parse counter reset value '%s' as type long",
                                v), nfe);
                    }
                }
            }
        }

        return options;
    }

    /**
     * Parse the URL metric parameter format supported by <a
     * href="http://opentsdb.net/http-api.html#/q">OpenTSDB</a> into the metric
     * services {@link org.zenoss.app.metricservice.api.model.MetricSpecification} model object.
     * <p/>
     * <em style="color: red">NOTE: This method supports a format that is
     * proposed to OpenTSDB, but is not yet committed. This format include
     * "rate" options to better support counter base metrics</em>
     *
     * @param content
     *            the metric specification in the OpenTSDB format
     * @return model representation of the URL metric query parameter
     * @see org.zenoss.app.metricservice.api.model.MetricSpecification
     */
    public static MetricSpecification fromString(String content) {

        // Determine if there are tags in this query specification. This will
        // be a simple check, if there is a pattern '{' ... at the end
        // of the value, then strip it off as the tags.
        String[] terms = content.split(":", 4);
        int idx = terms[terms.length - 1].indexOf('{');

        String metric = null;
        Map<String, List<String>> tags = null;
        if (idx >= 0) {
            tags = parseTags(terms[terms.length - 1]
                .substring(idx).trim());
            metric = terms[terms.length - 1].substring(0, idx);
        } else {
            tags = new HashMap<>();
            metric = terms[terms.length - 1];
        }

        Aggregator aggregator = DEFAULT_AGGREGATOR;
        if (terms.length > 1) {
            aggregator = Aggregator.valueOf(terms[0].trim());
        }
        boolean rate = false;
        String downsample = null;
        RateOptions rateOptions = null;
        if (terms.length > 2) {
            if (terms[1].trim().startsWith("rate")) {
                rate = true;
                if (terms[1].indexOf('{') > -1) {
                    try {
                        rateOptions = parseRateOptions(terms[1].trim());
                    } catch (Exception e) {
                        throw new WebApplicationException(
                            Utils.getErrorResponse(null, Response.Status.BAD_REQUEST.getStatusCode(),
                                e.getMessage(), e.getClass().getName()));
                    }
                }
                if (terms.length > 3) {
                    downsample = terms[2].trim();
                }
            } else {
                downsample = terms[1].trim();
                if (terms.length > 3 && terms[2].trim().startsWith("rate")) {
                    rate = true;
                    if (terms[2].indexOf('{') > -1) {
                        try {
                            rateOptions = parseRateOptions(terms[2].trim());
                        } catch (Exception e) {
                            throw new WebApplicationException(
                                Utils.getErrorResponse(null, Response.Status.BAD_REQUEST.getStatusCode(), e
                                    .getMessage(), e.getClass()
                                    .getName()));
                        }
                    }
                } else if (terms.length >= 4) {
                    // They specified enough terms to include "rate", but the
                    // term that should be "rate" is some other random value,
                    // so this is a bad request.
                    throw new WebApplicationException(
                        Utils.getErrorResponse(
                            null,
                            Response.Status.BAD_REQUEST.getStatusCode(),
                            String.format(
                                "unknown value '%s' specified, when only 'rate' value is allowed",
                                terms[2].trim()), "RequestParse"));
                }
            }
        }

        MetricSpecification ms = new MetricSpecification();
        ms.aggregator = aggregator;
        ms.downsample = downsample;
        ms.rate = rate;
        ms.rateOptions = rateOptions;
        ms.metric = metric;
        ms.setTags(tags);
        return ms;
    }

    public void validateWithErrorHandling(List<Object> errors) {
        // Add error if '*' is specified within a tag
        if (null != tags) {
            for (Map.Entry<String, List<String>> entry : tags.entrySet()) {
                for (String tagValue : entry.getValue()) {
                    if (tagValue.contains("*")) {
                        String tagKey = entry.getKey();
                        String errorMessage = String.format("Tag %s has value %s, which contains '*'.", tagKey, tagValue);
                        String tagLocation = String.format("Value %s in tag %s of series %s", tagValue, tagKey, getNameOrMetric());
                        errors.add(Utils.makeError(errorMessage,"Tag values may not contain '*'", tagLocation));
                    }
                }
            }
        }
    }
}
