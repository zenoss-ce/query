package org.zenoss.app.metricservice.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.zenoss.app.metricservice.MetricServiceAppConfiguration;
import org.zenoss.app.metricservice.api.model.MetricSpecification;

public interface MetricStorageAPI {
    public BufferedReader getReader(MetricServiceAppConfiguration config,
            String id, String startTime, String endTime,
            Boolean exactTimeWindow, Boolean series, String downsample,
            Map<String, String> tags, List<MetricSpecification> queries)
            throws IOException;

    public String getSourceId();

    public TimeZone getServerTimeZone();
}