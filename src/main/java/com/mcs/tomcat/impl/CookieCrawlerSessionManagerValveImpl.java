package com.mcs.tomcat.impl;

import com.mcs.tomcat.utils.CrawlerSessionManagerValveUtils;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class CookieCrawlerSessionManagerValveImpl extends StandardCrawlerSessionManagerImpl {

    private static final Log log =
            LogFactory.getLog(CookieCrawlerSessionManagerValveImpl.class);

    private String ipRequestCookieKey = "enduserip";

    public CookieCrawlerSessionManagerValveImpl() {
        super();
    }

    public CookieCrawlerSessionManagerValveImpl(String ipRequestCookieKey) {
        super();
        this.ipRequestCookieKey = ipRequestCookieKey;
    }

    @Override
    protected String getRemoteAddress(Request request) {
        final String realUserIp = CrawlerSessionManagerValveUtils.findCookieByName(request, this.ipRequestCookieKey);
        final boolean cookieFound = realUserIp != null && !realUserIp.isEmpty();
        return cookieFound ? realUserIp : super.getRemoteAddress(request);
    }

    public String getIpRequestCookieKey() {
        return ipRequestCookieKey;
    }

    public void setIpRequestCookieKey(String ipRequestCookieKey) {
        this.ipRequestCookieKey = ipRequestCookieKey;
    }
}
