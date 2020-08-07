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
package org.abstracthorizon.danube.http;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.http.util.ErrorConnectionHandler;
import org.abstracthorizon.danube.http.util.MultiStringMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This is base http connection handler that splits different HTTP methods
 * (GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS and possibly others) to
 * methods with names starting with &quot;method&quotl and ending with HTTP method name.
 * For instanceof <code>methodGET</code>, <code>methodPOST</code>, etc.
 * </p>
 * <p>
 * This handler also defines default HEAD and TRACE method implementations
 * (<code>methodHEAD</code> and <code>methodTRACE</code>). These can be easily
 * disabled with {@link #setNoDefaultHead(boolean)} and {@link #setNoDefaultTrace(boolean)} properties.
 * That does not affect user defined HEAD and TRACE implementations.
 * </p>
 * <p>
 * HTTP method OPTIONS is handled by {@link #methodOPTIONS(HTTPConnection)} method.
 * It returns all those that are defined in the class.
 * </p>
 * @author Daniel Sendula
 */
public class BaseReflectionHTTPConnectionHandler implements ConnectionHandler {

    protected Map<String, Method> cachedMethods;

    /** Flag that shows should default HEAD handling method be included or not ({@link methodHead(HttpConnection)} */
    protected boolean noDefaultHead = false;

    /** Flag that shows should default TRACE handling method be included or not ({@link methodTrace(HttpConnection)} */
    protected boolean noDefaultTrace = false;

    /** Error response */
    protected ConnectionHandler errorResponse = new ErrorConnectionHandler();

    /**
     * Constructor
     */
    public BaseReflectionHTTPConnectionHandler() {
    }

    /**
     * Returns error response connection handler
     * @return error response connection handler
     */
    public ConnectionHandler getErrorResponse() {
        return errorResponse;
    }

    /**
     * Sets error response connection handler
     * @param errorResponse error response handler
     */
    public void setErrorResponse(ConnectionHandler errorResponse) {
        this.errorResponse = errorResponse;
    }

    /**
     * Returns if default HEAD handling method is to be included or not
     * @return <code>true</code> if default HEAD handling method is to be included
     */
    public boolean getNoDefaultHead() {
        return noDefaultHead;
    }

    /**
     * Sets if default HEAD handling method is to be included or not
     * @param noDefaultHead should default HEAD handling method is to be included or not
     */
    public void setNoDefaultHead(boolean noDefaultHead) {
        this.noDefaultHead = noDefaultHead;
        updateDefaultHeadMethod();
    }

    /**
     * Updates the state of default HEAD method implementation.
     */
    protected void updateDefaultHeadMethod() {
        try {
            Method method = BaseReflectionHTTPConnectionHandler.class.getMethod("methodHEAD", new Class[]{HTTPConnection.class});
            if (noDefaultHead) {
                if (method == cachedMethods.get("HEAD")) {
                    cachedMethods.remove("HEAD");
                }
            } else {
                cachedMethods.put("HEAD", method);
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * Returns if default TRACE handling method is to be included or not
     * @return <code>true</code> if default TRACE handling method is to be included
     */
    public boolean getNoDefaultTrace() {
        return noDefaultTrace;
    }

    /**
     * Sets if default TRACE handling method is to be included or not
     * @param noDefaultTrace should default TRACE handling method is to be included or not
     */
    public void setNoDefaultTrace(boolean noDefaultTrace) {
        this.noDefaultTrace = noDefaultTrace;
        updateDefaultTraceMethod();
    }

    /**
     * Updates the state of default TRACE method implementation.
     */
    protected void updateDefaultTraceMethod() {
        try {
            Method method = BaseReflectionHTTPConnectionHandler.class.getMethod("methodTRACE", new Class[]{HTTPConnection.class});
            if (noDefaultTrace) {
                if (method == cachedMethods.get("TRACE")) {
                    cachedMethods.remove("TRACE");
                }
            } else {
                cachedMethods.put("TRACE", method);
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * Caches HTTP to java methods. Methods that start with &quot;method&quot;, has
     * {@link HTTPConnection} as a single parameter are cached.
     */
    protected void cacheMethods() {
        cachedMethods = new HashMap<String, Method>();
        Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if ((methodName.length() > 6)
                    && methodName.startsWith("method")
                    && (method.getParameterTypes().length == 1)
                    && (HTTPConnection.class.isAssignableFrom(method.getParameterTypes()[0]))) {

                cachedMethods.put(methodName.substring(6), method);
            }
        }
        updateDefaultHeadMethod();
        updateDefaultTraceMethod();
    }

    /**
     * Handles connection
     * @param connection connection
     */
    public void handleConnection(Connection connection) {
        if (cachedMethods == null) {
            cacheMethods();
        }
        HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);
        invokeMethod(httpConnection, httpConnection.getRequestMethod());
    }

    /**
     * Invokes object's method
     * @param httpConnection http connection
     * @param methodName method name
     */
    protected void invokeMethod(HTTPConnection httpConnection, String methodName) {
        Method method = cachedMethods.get(methodName);
        if (method != null) {
            try {
                method.invoke(this, new Object[]{httpConnection});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            httpConnection.setResponseStatus(Status.METHOD_NOT_ALLOWED);
            errorResponse.handleConnection(httpConnection);
        }
    }

    /**
     * Handles OPTIONS HTTP method
     * @param httpConnection HTTP connection
     */
    public void methodOPTIONS(HTTPConnection httpConnection) {
        StringBuffer allowedMethods = new StringBuffer();
        boolean first = true;
        for (String method : cachedMethods.keySet()) {
            if (first) {
                first = false;
            } else {
                allowedMethods.append(", ");
            }
            allowedMethods.append(method);
        }
        httpConnection.getResponseHeaders().putOnly("Allow", allowedMethods.toString());
        httpConnection.getResponseHeaders().removeAll("Content-Type");
    }

    /**
     * Handles OPTIONS HTTP method
     * @param httpConnection HTTP connection
     */
    public void methodTRACE(HTTPConnection httpConnection) {
        MultiStringMap requestHeaders = httpConnection.getRequestHeaders();
        MultiStringMap responseHeaders = httpConnection.getResponseHeaders();
        String contentLength = requestHeaders.getOnly("Content-Length");
        if (contentLength == null) {
            httpConnection.setResponseStatus(Status.LENGTH_REQUIRED);

//            String message = "Content length required for resource " + httpConnection.getComponentResourcePath();

            errorResponse.handleConnection(httpConnection);
        } else {
            long len = Long.parseLong(contentLength);
            responseHeaders.putOnly("Content-Length", contentLength);

            try {
                InputStream inputStream = (InputStream)httpConnection.adapt(InputStream.class);
                OutputStream outputStream = (OutputStream)httpConnection.adapt(OutputStream.class);
                int bufSize = 10240;
                if (len < bufSize) {
                    bufSize = (int)len;
                }

                byte[] buf = new byte[bufSize];

                int r = bufSize;
                if (len < r) {
                    r = (int)len;
                }

                r = inputStream.read(buf, 0, r);
                while ((r > 0) && (len > 0)) {
                    outputStream.write(buf, 0, r);
                    len = len - r;
                    r = bufSize;
                    if (len < r) {
                        r = (int)len;
                    }
                    r = inputStream.read(buf, 0, r);
                }
                outputStream.flush();
            } catch (IOException ignore) {
            }
        }

    }

    /**
     * Handles OPTIONS HTTP method
     * @param httpConnection HTTP connection
     */
    public void methodHEAD(HTTPConnection httpConnection) {
        invokeMethod(httpConnection, "GET");
    }

    public void returnError(HTTPConnection httpConnection, Status status) {
        httpConnection.setResponseStatus(status);
        errorResponse.handleConnection(httpConnection);
    }

    // TODO - do we want Status instead of code/msg?
    public void returnSimpleContent(HTTPConnection httpConnection, Status status, String contentType, String content) {
        httpConnection.setResponseStatus(status);
        if (contentType != null) {
            httpConnection.getResponseHeaders().putOnly("Content-Type", contentType);
        }
        if (content != null) {
            PrintWriter out = (PrintWriter)httpConnection.adapt(PrintWriter.class);
            out.print(content);
        }
    }
}
