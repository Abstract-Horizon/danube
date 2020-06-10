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
package org.abstracthorizon.danube.webdav.xml.dav;

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.RequestProperty;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class parses request properties
 *
 * @author Daniel Sendula
 */
public class RequestProp extends DAVAbstractXMLParser {

    /** Undefined action */
    public static final int UNDEFINED = 0;

    /** Set property */
    public static final int SET = 1;

    /** Remove property */
    public static final int REMOVE = 2;

    /** List of properties */
    protected List<RequestProperty> properties;

    /** Temporary storage for a property type */
    protected int type = UNDEFINED;

    /**
     * Constructor
     */
    protected RequestProp() {
    }

    /**
     * Constructor
     * @param parent parent parser handler
     * @param davFactory factory
     */
    public RequestProp(XMLParserHandler parent, DAVFactory davFactory) {
        super(parent, davFactory);
    }

    /**
     * Constructor
     * @param parent parent parser handler
     * @param davFactory factory
     * @param set type of property - {@link #SET} or {@link #REMOVE}
     */
    public RequestProp(XMLParserHandler parent, DAVFactory davFactory, boolean set) {
        super(parent, davFactory);
        if (set) {
            type = SET;
        } else {
            type = REMOVE;
        }
    }

    @Override
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {
        RequestProperty property = null;
        if ("prop".equals(tag)) {

        } else if ("creationdate".equals(tag)) {
            property =  davFactory.newCreationDate(this);
        } else if ("displayname".equals(tag)) {
            property = davFactory.newDisplayName(this);
        } else if ("getcontentlanguage".equals(tag)) {
            property = davFactory.newGetContentLanguage(this);
        } else if ("getcontentlength".equals(tag)) {
            property = davFactory.newGetContentLength(this);
        } else if ("getcontenttype".equals(tag)) {
            property = davFactory.newGetContentType(this);
        } else if ("getetag".equals(tag)) {
            property = davFactory.newGetETag(this);
        } else if ("getlastmodified".equals(tag)) {
            property = davFactory.newGetLastModified(this);
        } else if ("lockdiscovery".equals(tag)) {
            property = davFactory.newLockDiscovery(this);
        } else if ("resourcetype".equals(tag)) {
            property = davFactory.newResourceType(this);
        } else if ("source".equals(tag)) {
            property = davFactory.newSource(this);
        }
        if (property != null) {
            List<RequestProperty> properties = getProperties();
            properties.add(property);
            return property;
        } else {
            return super.start(current, tag, attributes);
        }
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        if ("prop".equals(tag)) {
            return super.end(current, tag, value);
        } else if ("set".equals(tag) || "remove".equals(tag)) {
            return super.end(current, tag, value);
        } else {
            return current;
        }
    }

    /**
     * Returns type
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * Returns list of request properties
     * @return list of request properties
     */
    public List<RequestProperty> getProperties() {
        if (properties == null) {
            properties = new ArrayList<RequestProperty>();
        }
        return properties;
    }

    @Override
    public String toString() {
        if (type == UNDEFINED) {
            StringBuffer result = new StringBuffer("Prop[");
            if (properties != null) {
                boolean first = true;
                for (RequestProperty property : properties) {
                    if (first) {
                        first = false;
                    } else {
                        result.append(',');
                    }
                    result.append(property.toString());
                }
            }
            result.append(']');
            return result.toString();
        } else {
            StringBuffer result = new StringBuffer();
            if (type == SET) {
                result.append("Set[Prop[");
            } else {
                result.append("Remove[Prop[");
            }
            if (properties != null) {
                boolean first = true;
                for (RequestProperty property : properties) {
                    if (first) {
                        first = false;
                    } else {
                        result.append(',');
                    }
                    result.append(property.toString());
                }
            }
            result.append("]]");
            return result.toString();
        }
    }

}
