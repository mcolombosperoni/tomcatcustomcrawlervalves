/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mcs.tomcat;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import java.io.Serializable;

/**
 * Web crawlers can trigger the creation of many thousands of sessions as they
 * crawl a site which may result in significant memory consumption. This Valve
 * ensures that crawlers are associated with a single session - just like normal
 * users - regardless of whether or not they provide a session token with their
 * requests.
 */
public abstract class CrawlerSessionManagerValve extends ValveBase
        implements HttpSessionBindingListener, Serializable {

    private static final Log log =
            LogFactory.getLog(CrawlerSessionManagerValve.class);

    private final Map<String,String> clientIpSessionId =
            new ConcurrentHashMap<>();
    private final Map<String,String> sessionIdClientIp =
            new ConcurrentHashMap<>();

    // use standard regular expression if not specified in server xml valve definition
    private String crawlerUserAgents =
            ".*[bB]ot.*|.*[cC]rawler.*|.*[sS]pider.*|.*Yahoo! Slurp.*|.*Feedfetcher-Google.*";
    private Pattern uaPattern = null;
    private int sessionInactiveInterval = 60;


    /**
     * Specifies a default constructor so async support can be configured.
     */
    public CrawlerSessionManagerValve() {
        super(true);
    }


    /**
     * Specify the regular expression (using {@link Pattern}) that will be used
     * to identify crawlers based in the User-Agent header provided. The default
     * is ".*GoogleBot.*|.*bingbot.*|.*Yahoo! Slurp.*"
     *
     * @param crawlerUserAgents The regular expression using {@link Pattern}
     */
    public void setCrawlerUserAgents(String crawlerUserAgents) {
        this.crawlerUserAgents = crawlerUserAgents;
        if (log.isInfoEnabled()) {
            log.info(new StringBuilder("Setting user agent pattern: [").append(
                    crawlerUserAgents).append("]"));
        }
        if (crawlerUserAgents == null || crawlerUserAgents.length() == 0) {
            uaPattern = null;
        } else {
            uaPattern = Pattern.compile(crawlerUserAgents);
        }
    }

    /**
     * @see #setCrawlerUserAgents(String)
     * @return  The current regular expression being used to match user agents.
     */
    public String getCrawlerUserAgents() {
        return crawlerUserAgents;
    }


    /**
     * Specify the session timeout (in seconds) for a crawler's session. This is
     * typically lower than that for a user session. The default is 60 seconds.
     *
     * @param sessionInactiveInterval   The new timeout for crawler sessions
     */
    public void setSessionInactiveInterval(int sessionInactiveInterval) {
        this.sessionInactiveInterval = sessionInactiveInterval;
    }

    /**
     * @see #setSessionInactiveInterval(int)
     * @return  The current timeout in seconds
     */
    public int getSessionInactiveInterval() {
        return sessionInactiveInterval;
    }


    public Map<String,String> getClientIpSessionId() {
        return clientIpSessionId;
    }


    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();

        uaPattern = Pattern.compile(crawlerUserAgents);
    }


    @Override
    public void invoke(Request request, Response response) throws IOException,
            ServletException {

        boolean isBot = false;
        String sessionId = null;
        String clientIp = null;

        if (log.isDebugEnabled()) {
            log.debug(request.hashCode() + ": ClientIp=" +
                    getRemoteAddress(request) + ", RequestedSessionId=" +
                    request.getRequestedSessionId());
        }

        // If the incoming request has a valid session ID, no action is required
        if (request.getSession(false) == null) {

            // Is this a crawler - check the UA headers
            Enumeration<String> uaHeaders = request.getHeaders("user-agent");
            String uaHeader = null;
            if (uaHeaders.hasMoreElements()) {
                uaHeader = uaHeaders.nextElement();
            }

            // If more than one UA header - assume not a bot
            if (uaHeader != null && !uaHeaders.hasMoreElements()) {

                if (log.isDebugEnabled()) {
                    log.debug(request.hashCode() + ": UserAgent=" + uaHeader);
                }

                if (uaPattern.matcher(uaHeader).matches()) {
                    isBot = true;

                    if (log.isDebugEnabled()) {
                        log.debug(request.hashCode() +
                                ": Bot found. UserAgent=" + uaHeader);
                    }
                }
            }

            // If this is a bot, is the session ID known?
            if (isBot) {
                clientIp = getRemoteAddress(request);
                sessionId = clientIpSessionId.get(clientIp);
                if (sessionId != null) {
                    request.setRequestedSessionId(sessionId);
                    if (log.isDebugEnabled()) {
                        log.debug(request.hashCode() + ": SessionID=" +
                                sessionId);
                    }
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("Bot found. UserAgent=" + uaHeader + "\nwith ip "
                                + clientIp);
                    }
                }
            }
        }

        getNext().invoke(request, response);

        if (isBot) {
            if (sessionId == null) {
                // Has bot just created a session, if so make a note of it
                HttpSession s = request.getSession(false);
                if (s != null) {
                    clientIpSessionId.put(clientIp, s.getId());
                    sessionIdClientIp.put(s.getId(), clientIp);
                    // #valueUnbound() will be called on session expiration
                    s.setAttribute(this.getClass().getName(), this);
                    s.setMaxInactiveInterval(sessionInactiveInterval);

                    if (log.isDebugEnabled()) {
                        log.debug(request.hashCode() +
                                ": New bot session. SessionID=" + s.getId());
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(request.hashCode() +
                            ": Bot session accessed. SessionID=" + sessionId);
                }
            }
        }
    }

    protected abstract String getRemoteAddress(Request request);


    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        // NOOP
    }


    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        String clientIp = sessionIdClientIp.remove(event.getSession().getId());
        if (clientIp != null) {
            clientIpSessionId.remove(clientIp);
        }
    }
}