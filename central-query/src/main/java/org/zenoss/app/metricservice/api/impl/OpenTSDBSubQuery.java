package org.zenoss.app.metricservice.api.impl;

import java.util.ArrayList;
import java.util.List;
import org.zenoss.app.metricservice.api.model.Aggregator;

import java.util.HashMap;
import java.util.Map;

public class OpenTSDBSubQuery {
    public Aggregator aggregator = Aggregator.sum;
    public String metric = null;
    public boolean rate = false;
    public OpenTSDBRateOption rateOptions = null;
    public String downsample = null;
    public Map<String, String> tags = null;
    public List<OpenTSDBFilter> filters = null;

    public void addTag(String key, String value) {
        if (null == tags) {
            tags = new HashMap<>();
        }
        String existingValue = tags.get(key);
        StringBuilder newValue = new StringBuilder();
        if (null != existingValue) {
            newValue.append(existingValue).append('|');
        }
        newValue.append(value);

        tags.put(key, newValue.toString());
    }

    public void setFilters(List<OpenTSDBFilter> filters) {
        this.filters = filters;
    }

    public void addFilter(OpenTSDBFilter filter) {
        if (null == filters) {
            filters = new ArrayList<>();
        }

        if(!filters.contains(filter)) {
            filters.add(filter);
        }
    }
}

