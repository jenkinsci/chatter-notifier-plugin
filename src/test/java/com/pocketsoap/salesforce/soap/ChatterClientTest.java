package com.pocketsoap.salesforce.soap;

import jenkins.model.Jenkins;
import org.apache.commons.httpclient.HttpClient;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ChatterClientTest {

    private Jenkins jenkins;

    @Before
    public void setUp() {
        jenkins = mock(Jenkins.class);
    }

    @Test
    public void testNullProxyHostIfNullProxyConfiguration() {
        HttpClient httpClient = ChatterClient.getHttpClient(jenkins);

        assertEquals(null, httpClient.getHostConfiguration().getProxyHost());
    }

    @Test
    public void testNullProxyPortIfNullProxyConfiguration() {
        HttpClient httpClient = ChatterClient.getHttpClient(jenkins);

        assertEquals(-1, httpClient.getHostConfiguration().getProxyPort());
    }

    @Test
    public void testNullProxyHostIfNullJenkinsInstance() {
        HttpClient httpClient = ChatterClient.getHttpClient(null);

        assertEquals(null, httpClient.getHostConfiguration().getProxyHost());
    }

    @Test
    public void testNullProxyPortIfNullJenkinsInstance() {
        HttpClient httpClient = ChatterClient.getHttpClient(null);

        assertEquals(-1, httpClient.getHostConfiguration().getProxyPort());
    }
}