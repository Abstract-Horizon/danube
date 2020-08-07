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
package org.abstracthorizon.danube.http.util;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.Status;

import java.io.PrintWriter;

/**
 * Default error connection handler.
 * It outputs simple html with response status and response code.
 *
 * @author Daniel Sendula
 */
public class ErrorConnectionHandler implements ConnectionHandler {

    /** Constructor */
    public ErrorConnectionHandler() {
    }

    /**
     * Returns simple http with error description
     * @param connection http connection
     */
    public void handleConnection(Connection connection) {
        HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);
        Status status = httpConnection.getResponseStatus();
        if (status == null) {
            status = Status.INTERNAL_SERVER_ERROR;
            httpConnection.setResponseStatus(status);
        }

        PrintWriter out = (PrintWriter)httpConnection.adapt(PrintWriter.class);
        out.println("<http><head><title>Error</title></head>");
        out.println("<body><h2>" + status.getCode() + " " + status.getMessage() + "</h2><br />");
        Throwable t = (Throwable)httpConnection.getAttributes().get("_exception");
        if (t != null) {
            out.print("<pre>");
            t.printStackTrace(out);
            out.print("</pre>");
        }
        out.println("</body></html>");
        out.close();
    }

}
