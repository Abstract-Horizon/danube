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

package org.abstracthorizon.danube.tapestry;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.Status;

import org.apache.tapestry.util.ContentType;
import org.apache.tapestry.web.WebResponse;

/**
 * {@link WebResponse} interface implementation.
 *
 * @author Daniel Sendula
 */
public class DanubeResponse implements WebResponse {

    /** Reference to connection */
    protected HTTPConnection connection;

    /** If true {@link #getPrintWriter(ContentType)} will reset connection. */
    protected boolean needReset = false;

    /**
     * Constructor
     * @param connection connection
     */
    public DanubeResponse(HTTPConnection connection) {
        this.connection = connection;
    }

    /**
     * Returns output stream and sets content type
     * @param contentType
     * @return output stream
     */
    public OutputStream getOutputStream(ContentType contentType) throws IOException {
        connection.getResponseHeaders().putOnly("Content-Type", contentType.getMimeType());
        return (OutputStream)connection.adapt(OutputStream.class);
    }

    /**
     * Returns print writer. It will reset connection ({@link HTTPConnection#reset()}) if this method is called
     * second time.
     */
    public PrintWriter getPrintWriter(ContentType contentType) throws IOException {
        if (needReset) {
            connection.reset();
        }
        needReset = true;
        connection.getRequestHeaders().putOnly("Content-Type", contentType.getMimeType());
        return (PrintWriter)connection.adapt(PrintWriter.class);
    }

    /**
     * Encodes url.
     * @param url to be encoded
     * @return encoded url
     */
    public String encodeURL(String url) {
        return url; // TODO
    }

    /**
     * Resets connection. It calls {@link HTTPConnection#reset()}.
     */
    public void reset() {
        connection.reset();
    }

    /**
     * Sets content length. It sets appropriate header.
     * @param len content length
     */
    public void setContentLength(int len) {
        connection.getResponseHeaders().putOnly("Content-Length", Integer.toString(len));
    }

    /**
     * Returns namespace
     * @return namespace
     */
    public String getNamespace() {
        return ""; // TODO
    }

    /**
     * Sets date header
     * @param name header name
     * @param date date
     */
    public void setDateHeader(String name, long date) {
        // TODO
    }

    /**
     * Sets header
     * @param name header name
     * @param value header value
     */
    public void setHeader(String name, String value) {
        connection.getRequestHeaders().putOnly(name, value);
    }

    /**
     * Sets integer header
     * @param name header name
     * @param i integer value
     */
    public void setIntHeader(String name, int i) {
        connection.getRequestHeaders().putOnly(name, Integer.toString(i));
    }

    /**
     * Sets HTTP status
     * @param code status code
     */
    public void setStatus(int code) {
        // TODO check static fields of Status class for the given code
        Status status = new Status(Integer.toString(code), Integer.toString(code));
        connection.setResponseStatus(status);
    }

    /**
     * Sends error to the client
     * @param code error code
     * @param msg error message
     * @throws IOException
     */
    public void sendError(int code, String msg) throws IOException {
        Status status = new Status(Integer.toString(code), msg);
        connection.setResponseStatus(status);
        // TODO do something!
    }

}
