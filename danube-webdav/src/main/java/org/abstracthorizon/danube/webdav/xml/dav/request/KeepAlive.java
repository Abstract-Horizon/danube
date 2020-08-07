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
package org.abstracthorizon.danube.webdav.xml.dav.request;

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.HRef;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class models WebDAV's keepalive tag
 *
 * @author Daniel Sendula
 */
public class KeepAlive extends AbstractSimpleXMLHandler {

    /** Hrefs */
    protected List<HRef> hrefs;

    /** Value */
    protected String value;

    /**
     * Constructor
     * @param parent parent parser handler
     */
    public KeepAlive(XMLParserHandler parent) {
        super(parent);
    }

    @Override
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {
        if ("href".equals(tag)) {
            if (hrefs == null) {
                hrefs = new ArrayList<HRef>();
            }
            HRef href = new HRef(this);
            hrefs.add(href);
            return href;
        }
        return this;
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        if (hrefs == null) {
            this.value = value;
        }
        return super.end(current, tag, value);
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("KeepAlive[");
        if (hrefs == null) {
            if (value == null) {
                result.append(']');
            } else {
                result.append(value).append(']');
            }
        } else {
            boolean first = true;
            for (HRef href : hrefs) {
                if (first) {
                    first = false;
                } else {
                    result.append(',');
                }
                result.append(href.toString());
            }
            result.append(']');
        }
        return result.toString();
    }
}
