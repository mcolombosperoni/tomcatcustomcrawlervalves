package com.mcs.tomcat.utils;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public final class CrawlerSessionManagerValveUtils {

    private static final Log log =
            LogFactory.getLog(CrawlerSessionManagerValveUtils.class);

    public static String findCookieByName(final HttpServletRequest request,
                                   final String cookieName) {

        Cookie cookie = getCookieByName(request, cookieName);
        return cookie != null ? cookie.getValue() : null;

    }

    public static Cookie getCookieByName(final HttpServletRequest request,
                                  final String cookieName) {

        if (log.isDebugEnabled()) {
            log.debug("try to find cookie_name: " + cookieName);
        }

        final Cookie[] cookies = request.getCookies();
        if ((cookies == null) || (cookies.length == 0)) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (log.isTraceEnabled()) {
                log.trace("cookie_name: " + cookie.getName() + "cookie_value: " + cookie.getValue());
            }
            if (cookieName.equals(cookie.getName())) {
                if (log.isDebugEnabled()) {
                    log.debug("cookie_name: " + cookie.getName() + "found");
                }
                return cookie;
            }
        }

        return null;
    }
}
