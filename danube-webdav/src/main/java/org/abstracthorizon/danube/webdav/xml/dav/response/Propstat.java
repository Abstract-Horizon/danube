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

import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.XMLRenderer;
import org.abstracthorizon.danube.webdav.xml.common.XMLUtils;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;

import java.io.PrintWriter;

/**
 * This class models WebDAV's propstat response tag
 *
 * @author Daniel Sendula
 */
public class Propstat implements XMLRenderer {

    /** Tag name */
    public static final String TAG_NAME = "propstat";

    /** Response this propstat belongs to */
    protected Response response;

    /** Request property */
    protected ResponseProp prop = new ResponseProp();

    /** Status */
    protected Status status;

    /** Response description */
    protected ResponseDescription responsedescription;

    /**
     * Constructor
     * @param response response
     */
    public Propstat(Response response) {
        this.response = response;
    }

    /**
     * Returns response prop
     * @return response prop
     */
    public ResponseProp getProp() {
        return prop;
    }

    /**
     * Returns response description
     * @return response description
     */
    public ResponseDescription getResponsedescription() {
        return responsedescription;
    }

    /**
     * Sets response description
     * @param responsedescription response description
     */
    public void setResponsedescription(ResponseDescription responsedescription) {
        this.responsedescription = responsedescription;
    }

    /**
     * Returns status
     * @return status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets status
     * @param status status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.println(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        prop.render(writer, provider);
        response.renderStatus(writer, provider, status);
        if (responsedescription != null) {
            responsedescription.render(writer, provider);
        }
        writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
    }
}
