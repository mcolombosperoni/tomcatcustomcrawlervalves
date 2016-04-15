package com.mcs.tomcat.impl;

import org.apache.catalina.connector.Request;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.regex.Pattern;

import static org.mockito.Mockito.mock;

public class MultiPathHeaderCrawlerSessionManagerTest {

    private MultiPathHeaderCrawlerSessionManager multiPathHeaderCrawlerSessionManager = null;
    private static final Pattern MULTI_PATH = Pattern.compile("^(/.{2}/.{2}/|/.{2}/.{2}_.{2}/)");

    @Before
    public void setUp() throws Exception {
        this.multiPathHeaderCrawlerSessionManager = new MultiPathHeaderCrawlerSessionManager();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void shouldGetRemoteAddress() throws Exception {


        //given
        String servletPath = "/it/en_US/ca/test/sfsfsfs";
        Request request = mock(Request.class);
        Mockito.when(request.getHeader(Mockito.anyString())).thenReturn("127.0.0.1");
        Mockito.when(request.getServletPath()).thenReturn(servletPath);

        //when
        String result = multiPathHeaderCrawlerSessionManager.getRemoteAddress(request);

        //then
        Assert.assertEquals("127.0.0.1_it-en_US", result);

    }

    @Test
    public void shouldGetCountryLangInfoHandleUriPattern() throws Exception {
        //given
        String servletPath = "/it/en_US/ca/test/sfsfsfs";
        String servletPath2 = "/de/de/st/capsule/men/gq-the-performers";

        //when
        String countryLangInfo = multiPathHeaderCrawlerSessionManager.getCountryLangInfo(servletPath, MULTI_PATH);
        String countryLangInfo2 = multiPathHeaderCrawlerSessionManager.getCountryLangInfo(servletPath2, MULTI_PATH);

        //then
        Assert.assertEquals("it-en_US", countryLangInfo);
        Assert.assertEquals("de-de", countryLangInfo2);


    }

}