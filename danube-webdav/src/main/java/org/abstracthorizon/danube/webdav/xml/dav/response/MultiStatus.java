/*
 * Copyright (c) 2006-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.webdav.xml.dav.response;

import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.http.util.IOUtils;
import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.dav.HRef;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents multistatus response tag as defined in RFC-2518.
 * It can render multistatus response xml but also can set {@link HTTPConnection} headers
 * in a simple no content response or render partial content as required by some
 * functions.
 *
 * @author Daniel Sendula
 */
public class MultiStatus {

    /** Mutlistatus response status 207 */
    public static final Status MULTISTATUS_RESPONSE = new Status("207", "Multistatus response");

    /** HTTP connection status is going to render response to */
    protected HTTPConnection httpConnection;

    /** Is stauts already sent to the connection */
    protected boolean committed = false;

    /** Is it header response only or proper multistatus xml */
    protected boolean headerResponse = false;

    /** Namespace provider */
    protected NamespacesProvider provider;

    /** List of responses */
    protected List<Response> responses = new ArrayList<Response>();

    /**
     * Constructor
     * @param provider namespace provider
     * @param connection connection to be rendered to
     */
    public MultiStatus(NamespacesProvider provider, HTTPConnection connection) {
        this.provider = provider;
        this.httpConnection = connection;
    }

    /**
     * Sets commit flag to <code>true</code>
     */
    public void commit() {
        committed = true;
    }

    /**
     * Returns commited flag
     * @return commited flag
     */
    public boolean isCommitted() {
        return committed;
    }

    /**
     * Returns is it header only response
     * @return <code>true</code> if it is header only response
     */
    public boolean isHeaderResponse() {
        return headerResponse;
    }

    /**
     * Returns responses
     * @return responses
     */
    public List<Response> getResponses() {
        return responses;
    }

    /**
     * Returns http connection
     * @return http connection
     */
    public HTTPConnection getHTTPConnection() {
        return httpConnection;
    }

    /**
     * Returns http connection's response protocol
     * @return http connection's response protocol
     */
    public String getResponseProtocol() {
        return httpConnection.getResponseProtocol();
    }

    /**
     * Renders with <code>true</code> for force mutlistatus flag
     */
    public void render() {
        render(true);
    }

    /**
     * Renders this object to http connection
     *
     * @param forceMultistatus if <code>true</code> then it is rendered as mutlistatus. Otherwise it can
     * return header only or partial xml response
     */
    public void render(boolean forceMultistatus) {
        if (responses.size() > 0) {
            if (!forceMultistatus && !committed && (responses.size() == 1)/* && !responses.get(0).isDefined()*/) {
                headerResponse = true;
            }
            commit();
            if (headerResponse) {
                Response response = responses.get(0);
                Status status = response.getStatus();
                if (response.isDefined()) {
                    httpConnection.getResponseHeaders().putOnly("Content-Type", "text/xml; charset=\"utf-8\"");
                    PrintWriter writer = (PrintWriter)httpConnection.adapt(PrintWriter.class);

                    // TODO what to do if more then one propstat is available???
                    writer.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
                    Propstat propStat = response.getPropStats().get(0);
                    propStat.getProp().renderWithNamespaces(writer, provider);
                } else if (status != null) {
                    httpConnection.setResponseStatus(status);
                } else {
                    // ERROR
                    throw new RuntimeException("Wrong response nether status nor propstats were defined");
                }
            } else {
                httpConnection.getResponseHeaders().putOnly("Content-Type", "text/xml; charset=\"utf-8\"");
                httpConnection.setResponseStatus(MULTISTATUS_RESPONSE);
                PrintWriter writer = (PrintWriter)httpConnection.adapt(PrintWriter.class);
                writer.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
                writer.append("<D:multistatus ");
                renderNamespaces(writer, provider);
                writer.println(">");
                for (Response response : responses) {
                    response.render(writer, provider);
                }
                writer.println("</D:multistatus>");
            }
        }
    }

    /**
     * Renders namespaces
     *
     * @param writer writer
     * @param provider namespace provider
     */
    public static void renderNamespaces(PrintWriter writer, NamespacesProvider provider) {
        String[] urls = provider.getDefinedURLs();
        for (int i = 0; i < urls.length; i++) {
            if (i > 0) {
                writer.append(' ');
            }
            writer.append("xmlns:").append(provider.getAssignedPrefix(urls[i])).append("=\"");
            writer.append(urls[i]).append('"');
        }
    }

    /**
     * Adds simple response
     *
     * @param status status
     * @return new response
     */
    public Response addSimpleResponse(Status status) {
        Response response = new Response(this, newHRef());
        response.setStatus(status);
        getResponses().add(response);
        return response;
    }

    /**
     * Creates new response with href of request's URL
     * @return new response
     */
    public Response newResponse() {
        HRef href = newHRef();
        Response response = new Response(this, href);
        getResponses().add(response);
        return response;
    }

    /**
     * Creates new response with href of relative path of request's URL
     * @param path relative path from request's URL
     * @return new response
     */
    public Response newResponse(String path) {
        HRef href = newHRef(path);
        Response response = new Response(this, href);
        getResponses().add(response);
        return response;
    }

    /**
     * Creates new href of request's URL
     * @return new href
     */
    public HRef newHRef() {
        HRef href = new HRef(httpConnection.getRequestURI());
//        HRef href = new HRef(connection.getComponentResourcePath());
        return href;
    }

    /**
     * Creates new href of relative path of request's URL
     * @param path path
     * @return new href
     */
    public HRef newHRef(String path) {
        String uri = IOUtils.addPaths(httpConnection.getRequestURI(), path);
//        String uri = IOUtils.addPaths(connection.getComponentResourcePath(), path);
        HRef href = new HRef(uri);
        return href;
    }
}
