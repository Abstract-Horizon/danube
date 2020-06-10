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
package org.abstracthorizon.danube.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.http.matcher.Matcher;
import org.abstracthorizon.danube.http.util.ErrorConnectionHandler;

/**
 * This class selects appropriate {@link org.abstracthorizon.danube.connection.ConnectionHandler}
 * based on matching based on the URI from the http connection.
 *
 * @author Daniel Sendula
 *
 * @has 1 - n org.abstracthorizon.danube.http.matcher.Matcher
 */
public class Selector implements ConnectionHandler {

    /** List of {@link org.abstracthorizon.danube.http.matcher.Matcher} objects */
    protected List<Matcher> components = new ArrayList<Matcher>();

    /** Error response if uri hasn't been matched */
    protected ConnectionHandler errorResponse = new ErrorConnectionHandler();

    /** Constructor */
    public Selector() {
    }

    /**
     * <p>Selects {@link ConnectionHandler} checking all {@link Matcher} from {@link #components} list.
     * If {@link Matcher#matches(HTTPConnection)} returns true then {@link ConnectionHandler} stored in
     * {@link Matcher} is called. Depending on {@link Matcher#isStopOnMatch()} selector will
     * proceed with rest of the list or not.
     * </p>
     * <p>If no matches are found {@link #errorResponse} is used to generate error. Default
     * value is {@link ErrorConnectionHandler}.
     *
     * @param connection http connection
     */
    public void handleConnection(Connection connection) {
        HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);

        boolean matched = false;
        Iterator<Matcher> it = components.iterator();
        while (it.hasNext()) {
            Matcher httpMatcher = it.next();
            if (httpMatcher.matches(httpConnection)) {
                matched = true;
                httpMatcher.adjustForInvocation(httpConnection);
                httpMatcher.getConnectionHandler().handleConnection(httpConnection);
                if (httpMatcher.isStopOnMatch()) {
                    return;
                }
            }
        }

        if (!matched) {
            httpConnection.setResponseStatus(Status.NOT_FOUND);
            errorResponse.handleConnection(httpConnection);
        }
    }

    /**
     * Sets list of connection handlers
     * @param components list of connection handlers
     */
    public void setComponents(List<Matcher> components) {
        this.components = components;
    }

    /**
     * Returns list of connection handlers
     * @return list of connection handlers
     */
    public List<Matcher> getComponents() {
        return components;
    }

    /**
     * Returns error response
     * @return error response
     */
    public ConnectionHandler getErrorResponse() {
        return errorResponse;
    }

    /**
     * Sets error response
     * @param errorResponse error response
     */
    public void setErrorResponse(ConnectionHandler errorResponse) {
        this.errorResponse = errorResponse;
    }

}
