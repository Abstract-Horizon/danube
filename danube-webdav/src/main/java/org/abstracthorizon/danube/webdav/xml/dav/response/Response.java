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
import org.abstracthorizon.danube.webdav.xml.dav.HRef;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class models WebDAV response tag. Response tag exist only as
 * a part of multistatus tag but values from it can be used to create
 * partial response or header response only.
 *
 * @author Daniel Sendula
 */
public class Response implements XMLRenderer {

    /** Tag name */
    public static final String TAG_NAME = "response";

    /** Status tag's name */
    public static final String STATUS_TAG_NAME = "status";

    /** Multistatus response belongs to */
    protected MultiStatus multiStatus;

    /** Status of the response. May be <code>null</code>. */
    protected Status status;

    /** Response's href */
    protected HRef href;

    /** List of additional hrefs. May be <code>null</code>. */
    protected List<HRef> additionalHRefs;

    /** List of propstats. May be <code>null</code>. */
    protected List<Propstat> propstats;

    /** Response description */
    protected String responseDescription;

    /**
     * Constructor
     * @param multiStatus parent multistatus
     * @param href href
     */
    public Response(MultiStatus multiStatus, HRef href) {
        this.multiStatus = multiStatus;
        this.href = href;
    }

    /**
     * Returns defined only if there are propstats in this response
     * @return defined only if there are propstats in this response
     */
    public boolean isDefined() {
        return (propstats != null);
    }

    /**
     * Returns response's status. May be <code>null</code>.
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
     * Returns response's href
     * @return response's href
     */
    public HRef getHRef() {
        return href;
    }

    /**
     * Returns additional hrefs. May be <code>null</code>.
     * @return additional hrefs
     */
    public List<HRef> getAdditionalHRefs() {
        return additionalHRefs;
    }

    /**
     * Sets list of additional hrefs
     * @param additionalHRefs list of additional hrefs
     */
    public void setAdditionalHRefs(List<HRef> additionalHRefs) {
        this.additionalHRefs = additionalHRefs;
    }

    /**
     * Adds additional href.
     * @param href additional href
     */
    public void addHRef(HRef href) {
        if (additionalHRefs == null) {
            additionalHRefs = new ArrayList<HRef>();
        }
        additionalHRefs.add(href);
    }

    /**
     * Returns response description
     * @return response description
     */
    public String getResponseDescription() {
        return responseDescription;
    }

    /**
     * Sets resoinse description.
     * @param responseDescription response description
     */
    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    /**
     * Returns list of propstats. It will always return the list - won't be <code>null</code>.
     * @return list of propstats.
     */
    public List<Propstat> getPropStats() {
        if (propstats == null) {
            propstats = new ArrayList<Propstat>();
        }
        return propstats;
    }

    @Override
    public String toString() {
        if (status != null) {
            StringBuffer response = new StringBuffer("Response[");
            response.append(href);
            if (additionalHRefs != null) {
                for (HRef href : additionalHRefs) {
                    response.append(',');
                    response.append(href);
                }
            }
            response.append(',');
            response.append(status);
            response.append(']');
            return response.toString();
        } else {
            StringBuffer response = new StringBuffer("Response[");
            response.append(href);
            response.append(",{");
            if (propstats != null) {
                boolean first = true;
                for (Propstat propStat : propstats) {
                    if (first) {
                        first = false;
                    } else {
                        response.append(',');
                    }
                    response.append(propStat);
                }
            }
            response.append('}');
            response.append(']');
            return response.toString();
        }
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.println(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        href.render(writer, provider);
        if (status != null) {
            if (additionalHRefs != null) {
                for (HRef href: additionalHRefs) {
                    href.render(writer, provider);
                }
            }
            renderStatus(writer, provider, status);
        } else {
            for (Propstat propstat : propstats) {
                propstat.render(writer, provider);
            }
        }
        if (responseDescription != null) {
            writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, "responsedescription", responseDescription));
        }
        writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
    }

    /**
     * Renders status
     * @param writer writer
     * @param provider provider
     * @param status status
     */
    public void renderStatus(PrintWriter writer, NamespacesProvider provider, Status status) {
        writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, STATUS_TAG_NAME, multiStatus.getResponseProtocol() + " " + status.getFullStatus()));
    }
}
