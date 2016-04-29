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
package org.zenoss.app.metricservice.health;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.setup.Environment;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.zenoss.app.metricservice.MetricServiceAppConfiguration;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Map;

@org.zenoss.dropwizardspring.annotations.HealthCheck
public class OpenTsdbHealthCheck extends HealthCheck {
    @Autowired
    MetricServiceAppConfiguration config;

    @Autowired
    Environment environment;

    private CloseableHttpClient httpClient;

    @PostConstruct
    public void init() {
        httpClient = new HttpClientBuilder(environment).build("OpenTsdbHealthCheck-client");

    }

    @Override
    protected Result check() throws Exception {

        CloseableHttpResponse response = null;
        try {

            HttpGet httpget = new HttpGet(config.getMetricServiceConfig().getOpenTsdbUrl() + "/api/stats");
            response = httpClient.execute(httpget);

            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();

            int code = response.getStatusLine().getStatusCode();
            if (code != Response.Status.OK.getStatusCode()) {
                return Result.unhealthy("Unexpected result code from OpenTSDB Server: " + code);
            }

            // Exception if unable to parse object from input stream.
            new ObjectMapper().readerFor(Map[].class).readValue(instream).toString();

            return Result.healthy();

        } catch (Exception e) {

            return Result.unhealthy(e);

        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
