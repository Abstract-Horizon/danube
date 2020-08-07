/*
 * Copyright (c) 2005-2020 Creative Sphere Limited.
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.http.util.MultiStringMap;

/**
 * <p>
 * This connection represents one HTTP request and response.
 * It can be reused over the same underlaying connection
 * (multiple requests over the same socket).
 * </p>
 * <p>
 * This implementation handles HTTP request string, headers and parameters.
 * </p>
 *
 * @author Daniel Sendula
 */
public interface HTTPConnection extends Connection {

    // Request methods
    SimpleDateFormat HEADER_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    /**
     * Returns request protocol
     * @return request protocol
     */
    public String getRequestProtocol();

    /**
     * Returns request method
     * @return request method
     */
    public String getRequestMethod();

    /**
     * Returns request headers map
     * @return request headers map
     */
    public MultiStringMap getRequestHeaders();

    /**
     * Returns request parameters map. If more
     * then one parameter is supplied with the same name
     * then {@link List} returned with all parameter
     * values in it.
     * @return request parameters map
     */
    public MultiStringMap getRequestParameters();

    /**
     * Returns raw requested uri along with path info (if supplied
     * in GET method)
     * @return raw requested uri
     */
    public String getRequestURI();

    /**
     * This is similar to {@link #getRequestURI()} but without path info (parameters) part
     * @return full (unchanged) uri
     */
    public String getRequestPath();

    /**
     * Returns portion of request path up to component path
     * @return portion of request path up to component path
     */
    public String getContextPath();

    /**
     * Updates context path adding new path element to it
     */
    public void addComponentPathToContextPath();

    /**
     * Returns portion of request URI from context path till end or '?' whichever comes first
     * @return request path
     */
    public String getComponentPath();

    /**
     * Sets request uri.
     * This is to be called from selectors not applicaiton code.
     *
     * @param requestURI
     */
    public void setComponentPath(String requestURI);

    /**
     * Returns remainder of path after context path and component path is removed
     * @return remainder of path after context path and component path is removed
     */
    public String getComponentResourcePath();

    /**
     * Sets component resource path
     * @param resourcePath component resource path
     */
    public void setComponentResourcePath(String resourcePath);

    // Response methods

    /**
     * Returns response headers map
     * @return response headers map
     */
    public MultiStringMap getResponseHeaders();

    /**
     * Returns response status
     * @return response status
     */
    public Status getResponseStatus();

    /**
     * Sets response status
     * @param status response status
     */
    public void setResponseStatus(Status status);

    /**
     * Returns response protocol
     * @return response protocol
     */
    public String getResponseProtocol();

    /**
     * Sets response protocol
     * @param protocol response protocol
     */
    public void setResponseProtocol(String protocol);

    /**
     * Returns <code>true</code> if headers are already send back to the client
     * TODO - maybe this method is not needed!
     * @return <code>true</code> if headers are already send back to the client
     */
    public boolean isCommited();

    /**
     * This method processes request.
     * It extracts method, uri, parameters (GET or POST),
     * protocol version and headers.
     */
    public void reset();

    /**
     * This method returns map of attributes.
     *
     * @return map of attributes
     */
    public Map<String, Object> getAttributes();

    /**
     * Forwards request.
     * @param path uri for request to be forwarded to.
     * @throws IOException if underlaying handler had IOException
     */
    public void forward(String path);

    /**
     * Sets buffer size
     *
     * @param size buffer size
     */
    public void setBufferSize(int size);

    /**
     * Returns buffer size
     *
     * @return buffer size
     */
    public int getBufferSize();
}
