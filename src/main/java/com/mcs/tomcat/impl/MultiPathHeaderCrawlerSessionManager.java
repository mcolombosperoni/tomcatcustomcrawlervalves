package com.mcs.tomcat.impl;

import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MultiPathHeaderCrawlerSessionManager extends HeaderCrawlerSessionManagerValveImpl {

    private static final Log LOGGER = LogFactory.getLog(MultiPathHeaderCrawlerSessionManager.class);

    private static final Pattern MULTI_PATH = Pattern.compile("^(/.{2}/.{2}/|/.{2}/.{2}_.{2}/)");

    public MultiPathHeaderCrawlerSessionManager() {
        super();
    }

    public MultiPathHeaderCrawlerSessionManager(String ipRequestHeaderKey) {
        super(ipRequestHeaderKey);
    }

    @Override
    protected String getRemoteAddress(Request request) {
        String remoteAddress = super.getRemoteAddress(request);
        String countryLang = getCountryLangInfo(request.getServletPath(), MULTI_PATH);

        String key = remoteAddress + "_" + countryLang;

        LOGGER.info("remoteAddress: " + remoteAddress + " countryLang: " + countryLang + " key: " + key);
        System.out.println("remoteAddress: " + remoteAddress + " countryLang: " + countryLang + " key: " + key);
        return key ;
    }


    protected String getCountryLangInfo(final String uri, final Pattern pattern) {

        String result = "";

        try {

            Matcher matcher = pattern.matcher(uri);
            if (matcher.find()) {

                final String[] split = matcher.group().replaceAll("^.|.$", "").split("/");
                final String country = split[0];
                final String language = split[1];
                result = country + "-" + language;
            }
        } catch (Exception e){
            LOGGER.warn("Request URI doesn't contain a valid site path or a handled site: " + uri);
            System.out.println("Request URI doesn't contain a valid site path or a handled site: " + uri);
        }


        return result;
    }

}


