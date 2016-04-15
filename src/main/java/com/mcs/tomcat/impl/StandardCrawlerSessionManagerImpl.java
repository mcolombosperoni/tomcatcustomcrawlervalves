package com.mcs.tomcat.impl;

import com.mcs.tomcat.CrawlerSessionManagerValve;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class StandardCrawlerSessionManagerImpl extends CrawlerSessionManagerValve {
    private static final Log log =
            LogFactory.getLog(StandardCrawlerSessionManagerImpl.class);

    public StandardCrawlerSessionManagerImpl() {
        super();
    }

    @Override
    protected String getRemoteAddress(Request request) {
        return request.getRemoteAddr();
    }


}
