/*
 * Copyright (c) 2005-2007 Creative Sphere Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *   Creative Sphere - initial API and implementation
 *
 */
package org.abstracthorizon.danube.http.cookie;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.util.MultiStringMap;

/**
 * Cookie utilities class
 *
 * @author Daniel Sendula
 */
public class CookieUtilities {

    public static final String RESPONSE_COOKIES_ATTRIBUTE = "org.abstracthorizon.danube.http.cookie.ResponseCookies";

    public static final String RESPONSE_COOKIE_HEADER = "Set-Cookie";

    public static final String REQUEST_COOKIES_ATTRIBUTE = "org.abstracthorizon.danube.http.cookie.RequestCookies";

    public static final String REQUEST_COOKIE_HEADER = "Cookie";

    /**
     * Returns all response cookies as a map
     * @param connection http connection
     * @return response cookies as a map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Cookie> getResponseCookies(HTTPConnection connection) {
        return (Map<String, Cookie>)connection.getAttributes().get(RESPONSE_COOKIES_ATTRIBUTE);
    }

    /**
     * Adds response cookie
     * @param connection http connection
     * @param cookies cookie
     */
    @SuppressWarnings("unchecked")
    public static void addResponseCookies(HTTPConnection connection, Collection<Cookie> cookies) {

        Map<String, Cookie> responseCookies = (Map<String, Cookie>)connection.getAttributes().get(RESPONSE_COOKIES_ATTRIBUTE);

        if (responseCookies == null) {
            responseCookies = new HashMap<String, Cookie>();
            connection.getAttributes().put(RESPONSE_COOKIES_ATTRIBUTE, responseCookies);
        }

        MultiStringMap responseHeaders = connection.getResponseHeaders();

        Map<String, String> cookieHeaders = null;

        for (Cookie cookie : cookies) {
            if (responseCookies.containsKey(cookie.getName())) {
                if (cookieHeaders == null) {
                    cookieHeaders = obtainCookieHeaders(responseHeaders);
                }
            } else {
                responseCookies.put(cookie.getName(), cookie);
                String cookieRepresentation = cookie.toString();
                if (cookieHeaders != null) {
                    cookieHeaders.put(cookie.getName(), cookieRepresentation);
                } else {
                    responseHeaders.add(RESPONSE_COOKIE_HEADER, cookieRepresentation);
                }
            }
        }
        if (cookieHeaders != null) {
            responseHeaders.putAll(RESPONSE_COOKIE_HEADER, cookieHeaders.values());
        }
    }

    /**
     * Obtains cookie headers
     * @param responseHeaders response headers
     * @return map of cookie headers
     */
    protected static Map<String, String> obtainCookieHeaders(MultiStringMap responseHeaders) {
        Map<String, String> headers = new HashMap<String, String>();

        for (String header : responseHeaders.getAsList(RESPONSE_COOKIE_HEADER)) {
            int i = header.indexOf('=');
            if (i < 0) {
                throw new IllegalStateException("Missing '=' in a cookie response header");
            }

            String name = header.substring(0, i);
            headers.put(name, header);
        }

        return headers;
    }

    /**
     * Obtains request cookies as a map
     * @param connection http connection
     * @return request cookies as a map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Cookie> getRequestCookies(HTTPConnection connection) {
        Map<String, Cookie> cookies = (Map<String, Cookie>)connection.getAttributes().get(REQUEST_COOKIES_ATTRIBUTE);
        if (cookies == null) {
            cookies = new HashMap<String, Cookie>();
            connection.getAttributes().put(REQUEST_COOKIES_ATTRIBUTE, cookies);
        }

        for (String header : connection.getRequestHeaders().getAsList(REQUEST_COOKIE_HEADER)) {
            StringTokenizer tokenizer = new StringTokenizer(header, ";");
            while (tokenizer.hasMoreTokens()) {
                try {
                    String token = tokenizer.nextToken();
                    Cookie cookie = new Cookie(token.trim());
                    cookies.put(cookie.getName(), cookie);
                } catch (ParseException ignore) {
                }
            }
        }
        return cookies;
    }


}
