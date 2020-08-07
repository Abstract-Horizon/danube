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
import org.abstracthorizon.danube.webdav.xml.dav.DAVAbstractXMLParser;
import org.abstracthorizon.danube.webdav.xml.dav.DAVFactory;
import org.abstracthorizon.danube.webdav.xml.dav.RequestProp;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class models WebDAV's propertyupdate tag
 *
 * @author Daniel Sendula
 */
public class PropertyUpdate extends DAVAbstractXMLParser {

    /** List of request properties to be updated */
    protected List<RequestProp> list = new ArrayList<RequestProp>();

    /**
     * Constructor
     * @param parent parent parser handler
     * @param factory
     */
    public PropertyUpdate(XMLParserHandler parent, DAVFactory factory) {
        super(parent, factory);
    }

    @Override
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {
        if ("set".equals(tag)) {
            RequestProp set = davFactory.newSet(this);
            list.add(set);
            return set;
        } else if ("remove".equals(tag)) {
            RequestProp remove = davFactory.newRemove(this);
            list.add(remove);
            return remove;
        } else {
            return super.start(current, tag, attributes);
        }
    }

    @Override
    public Object end(Object current, String tag, String value) {
        return this;
    }

    /**
     * Returns a list of request properties to be updated
     * @return a list of properties
     */
    public List<RequestProp> getProperties() {
        return list;
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer("PropertyUpdate[");
        boolean first = true;
        for (Object object: list) {
            if (first) {
                first = false;
            } else {
                res.append(',');
            }
            res.append(object);
        }
        res.append(']');
        return res.toString();
    }


}
