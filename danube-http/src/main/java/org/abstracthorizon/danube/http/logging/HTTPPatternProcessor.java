/*
 * Copyright (c) 2006-2020 Creative Sphere Limited.
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
package org.abstracthorizon.danube.http.logging;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.support.logging.patternsupport.PatternProcessor;
import org.abstracthorizon.danube.support.logging.util.StringUtil;

import java.util.ArrayList;

/**
 * This processor adds following pattern codes:
 *
 * <ul>
 * <li><code>%b</code> - bytes sent excluding headers or &quot;-&quot; if nothing</li>
 * <li><code>%B</code> - bytes sent excluding headers or 0 if nothing</li>
 * <li><code>%H</code> - request protocol</li>
 * <li><code>%l</code> - always &quot;-&quot;</li>
 * <li><code>%m</code> - request method</li>
 * <li><code>%q</code> - query string</li>
 * <li><code>%r</code> - first line of the request</li>
 * <li><code>%s</code> - status code of the response</li>
 * <li><code>%>s</code> - status code of the response (same as %s)</li>
 * <li><code>%S</code> - user session ID (not implemented yet)</li>
 * <li><code>%u</code> - remote authenticated user (not implemented yet)</li>
 * <li><code>%U</code> - requested url path</li>
 * <li><code>%v</code> - server local name (not implemented yet)</li>
 * </ul>
 *
 * @author Daniel Sendula
 */
public class HTTPPatternProcessor implements PatternProcessor {

    /** Cached index of bytes sent */
    protected int bytesSentIndex = -1;

    /** Cached index of bytes sent with zero */
    protected int bytesSent0Index = -1;


    protected int requestProtocolIndex = -1;


    protected int lIndex = -1;

    protected int requestMethodIndex = -1;

    protected int queryStringiIndex = -1;

    protected int firstLineOfRequestIndex = -1;

    protected int responseStatusIndex = -1;

    protected int requestedURLIndex = -1;

    protected int remoteUserIndex = -1;

    protected Pair[] requestHeaders;
    protected Pair[] cookies;

    /**
     * Constructor
     */
    public HTTPPatternProcessor() {
    }

    /**
     * Checks if parameters are present and if so replaces it and caches their indexes
     * @param index next index to be used
     * @param message message to be altered
     */
    public int init(int index, StringBuffer message) {
        bytesSentIndex = -1;
        bytesSent0Index = -1;
        requestProtocolIndex = -1;
        lIndex = -1;
        requestMethodIndex = -1;
        queryStringiIndex = -1;
        firstLineOfRequestIndex = -1;
        responseStatusIndex = -1;
        requestedURLIndex = -1;
        remoteUserIndex = -1;
        requestHeaders = null;
        cookies = null;

        if (message.indexOf("%b") >= 0) {
            StringUtil.replaceAll(message, "%b", "{" + index + "}");
            bytesSentIndex = index;
            index = index + 1;
        }

        if (message.indexOf("%B") >= 0) {
            StringUtil.replaceAll(message, "%B", "{" + index + "}");
            bytesSent0Index = index;
            index = index + 1;
        }

        if (message.indexOf("%H") >= 0) {
            StringUtil.replaceAll(message, "%H", "{" + index + "}");
            requestProtocolIndex = index;
            index = index + 1;
        }

        if (message.indexOf("%l") >= 0) {
            StringUtil.replaceAll(message, "%l", "{" + index + "}");
            lIndex = index;
            index = index + 1;
        }

        if (message.indexOf("%m") >= 0) {
            StringUtil.replaceAll(message, "%m", "{" + index + "}");
            requestMethodIndex = index;
            index = index + 1;
        }

        if (message.indexOf("%q") >= 0) {
            StringUtil.replaceAll(message, "%q", "{" + index + "}");
            queryStringiIndex = index;
            index = index + 1;
        }

        if (message.indexOf("%r") >= 0) {
            StringUtil.replaceAll(message, "%r", "{" + index + "}");
            firstLineOfRequestIndex = index;
            index = index + 1;
        }

        if ((message.indexOf("%s") >= 0) || (message.indexOf("%>s") >= 0)) {
            StringUtil.replaceAll(message, "%s", "{" + index + "}");
            StringUtil.replaceAll(message, "%>s", "{" + index + "}");
            responseStatusIndex = index;
            index = index + 1;
        }

        if (message.indexOf("%U") >= 0) {
            StringUtil.replaceAll(message, "%U", "{" + index + "}");
            requestedURLIndex = index;
            index = index + 1;
        }

        if (message.indexOf("%u") >= 0) {
            StringUtil.replaceAll(message, "%u", "{" + index + "}");
            remoteUserIndex = index;
            index = index + 1;
        }

        int i = message.indexOf("%{");
        if (i >= 0) {
            ArrayList<Pair> requestHeaders = new ArrayList<Pair>();
            ArrayList<Pair> cookies = new ArrayList<Pair>();
            while (i >= 0) {
                int j = message.indexOf("}", i);
                if ((j > i) && (message.length() > j + 1)) {
                    char c = message.charAt(j + 1);
                    String name = message.substring(i + 2, j);
                    Pair pair = new Pair();
                    pair.name = name;
                    pair.fieldIndex = index;
                    boolean ok = true;
                    if (c == 'i') {
                        requestHeaders.add(pair);
                    } else if (c == 'c') {
                        cookies.add(pair);
                    } else {
                        ok = false;
                    }
                    if (ok) {
                        message.replace(i, j+2, "{" + index + "}");
                        index = index + 1;
                    }
                    i = message.indexOf("%{", i + 1);
                } else {
                    i = -1;
                }
            }
            if (requestHeaders.size() > 0) {
                this.requestHeaders = new Pair[requestHeaders.size()];
                this.requestHeaders = requestHeaders.toArray(this.requestHeaders);
            }
            if (cookies.size() > 0) {
                this.cookies = new Pair[cookies.size()];
                this.cookies = cookies.toArray(this.cookies);
            }
        }

        return index;
    }

    /**
     * Adds parameter values to cached index positions
     * @param connection connection
     * @param array array
     */
    public void process(Connection connection, Object[] array) {
        HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);

        if (bytesSentIndex >= 0) {
            array[bytesSentIndex] = "-";
        }
        if (bytesSent0Index >= 0) {
            array[bytesSent0Index] = 0;
        }
        if (requestProtocolIndex >= 0) {
            array[requestProtocolIndex] = httpConnection.getRequestProtocol();
        }
        if (lIndex >= 0) {
            array[lIndex] = "-";
        }
        if (requestMethodIndex >= 0) {
            array[requestMethodIndex] = httpConnection.getRequestMethod();
        }
        if (queryStringiIndex >= 0) {
            String q = httpConnection.getRequestURI();
            int i = q.indexOf('?');
            if (i >= 0) {
                q = q.substring(i);
            } else {
                q = "-";
            }
            array[queryStringiIndex] = q;
        }
        if (firstLineOfRequestIndex >= 0) {
            array[firstLineOfRequestIndex] = httpConnection.getRequestMethod() + " " + httpConnection.getRequestURI() + " " + httpConnection.getRequestProtocol();
        }
        if (responseStatusIndex >= 0) {
            array[responseStatusIndex] = httpConnection.getResponseStatus().getCode();
        }
        if (requestedURLIndex >= 0) {
            String q = httpConnection.getRequestURI();
            int i = q.indexOf('?');
            if (i >= 0) {
                q = q.substring(0, i);
            }
            array[requestedURLIndex] = q;
        }
        if (remoteUserIndex >= 0) {
            array[remoteUserIndex] = "-";
        }
        if (requestHeaders.length > 0) {
            for (Pair pair : requestHeaders) {
                String value = httpConnection.getRequestHeaders().getFirst(pair.name);
                if ((value == null) || (value.length() == 0)) {
                    value = "";
                }
                array[pair.fieldIndex] = value;
            }
        }
    }

    protected static class Pair {
        public String name;
        public int fieldIndex;
    }

}
