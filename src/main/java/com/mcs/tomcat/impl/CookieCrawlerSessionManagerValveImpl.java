package com.mcs.tomcat.impl;

import com.mcs.tomcat.utils.CrawlerSessionManagerValveUtils;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class CookieCrawlerSessionManagerValveImpl extends StandardCrawlerSessionManagerImpl {

    private static final Log log =
            LogFactory.getLog(CookieCrawlerSessionManagerValveImpl.class);
    private static final String COOKIE_NAME = "enduserip";

    public CookieCrawlerSessionManagerValveImpl() {
        super();
    }

    @Override
    protected String getRemoteAddress(Request request) {
        final String realUserIp = CrawlerSessionManagerValveUtils.findCookieByName(request, COOKIE_NAME);
        final boolean cookieFound = realUserIp != null && !realUserIp.isEmpty();
        return cookieFound ? realUserIp : super.getRemoteAddress(request);
    }
}
