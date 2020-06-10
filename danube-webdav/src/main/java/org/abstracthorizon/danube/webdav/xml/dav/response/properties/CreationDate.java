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
package org.abstracthorizon.danube.webdav.xml.dav.response.properties;

import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.common.XMLUtils;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class models WebDAV's creationdate response tag.
 *
 * @author Daniel Sendula
 */
public class CreationDate extends ResponseProperty {

    /** Date format or &quot;yyy-MM-dd'T'HH:mm:ssZ"&quot; */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ssZ");

    /** Modified timestamp */
    protected long modified = -1;

    /** Cached formatted date */
    protected String cachedDate;

    /** Tag name */
    public static final String TAG_NAME = "creationdate";

    /** Date string */
    protected String dateString;

    /**
     * Constructor
     * @param status status
     */
    public CreationDate(Status status) {
        super(status);
    }

    /**
     * Constructor
     * @param modified timestamp
     */
    public CreationDate(long modified) {
        this.modified = modified;
    }

    /**
     * Returns timestamp
     * @return timestamp
     */
    public long getLastModified() {
        return modified;
    }

    /**
     * Returns formatted timestamp
     * @return formatted timestamp
     */
    public String asString() {
        if (cachedDate == null) {
            if (modified != -1) {
                cachedDate = DATE_FORMAT.format(new Date(modified));
            }
        }
        return cachedDate;
    }

    @Override
    public String toString() {
        return "CreationDate[" + asString() + "]";
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        if ((dateString == null) || (dateString.length() == 0)) {
            writer.println(XMLUtils.createEmptyTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        } else {
            writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, asString()));
        }
    }
}
