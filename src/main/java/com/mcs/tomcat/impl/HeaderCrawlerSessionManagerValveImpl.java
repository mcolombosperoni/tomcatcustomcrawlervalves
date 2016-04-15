package com.mcs.tomcat.impl;

import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.servlet.http.HttpSessionBindingListener;

public class HeaderCrawlerSessionManagerValveImpl extends StandardCrawlerSessionManagerImpl implements
        HttpSessionBindingListener {

    private static final Log log = LogFactory
            .getLog(HeaderCrawlerSessionManagerValveImpl.class);

    private String ipRequestHeaderKey = "x-forwarded-for";

    public HeaderCrawlerSessionManagerValveImpl() {
        super();
    }

    public HeaderCrawlerSessionManagerValveImpl(String ipRequestHeaderKey) {
        super();
        this.ipRequestHeaderKey = ipRequestHeaderKey;
    }

    @Override
    protected String getRemoteAddress(Request request) {
        String ipAddressFromHeader = request
                .getHeader(this.ipRequestHeaderKey);
        return ipAddressFromHeader != null ? ipAddressFromHeader
                : request.getRemoteAddr();
    }

    public String getIpRequestHeaderKey() {
        return ipRequestHeaderKey;
    }

    public void setIpRequestHeaderKey(String ipRequestHeaderKey) {
        this.ipRequestHeaderKey = ipRequestHeaderKey;
    }
}
