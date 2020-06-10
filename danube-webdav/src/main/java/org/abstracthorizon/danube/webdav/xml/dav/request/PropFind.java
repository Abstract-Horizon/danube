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
package org.abstracthorizon.danube.webdav.xml.dav.request;

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.DAVAbstractXMLParser;
import org.abstracthorizon.danube.webdav.xml.dav.DAVFactory;
import org.abstracthorizon.danube.webdav.xml.dav.RequestProp;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class models WebDAV's propfind tag
 *
 * @author Daniel Sendula
 */
public class PropFind extends DAVAbstractXMLParser {

    /** All props flag */
    protected boolean allprop = false;

    /** Properties names only tag */
    protected boolean propname = false;

    /** Request property */
    protected RequestProp prop;

    /**
     * Constructor
     * @param parent parent parser handler
     * @param davFactory factory
     */
    public PropFind(XMLParserHandler parent, DAVFactory davFactory) {
        super(parent, davFactory);
    }

    @Override
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {
        if ("allprop".equals(tag)) {
            allprop = true;
            return this;
        } else if ("propname".equals(tag)) {
            propname = true;
            return this;
        } else if ("prop".equals(tag)) {
            prop = davFactory.newProp(this);
            return prop;
        } else {
            return super.start(current, tag, attributes);
        }
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        return current;
    }

    /**
     * Sets allprop flag
     * @param allprop allprop flag
     */
    public void setAllprop(boolean allprop) {
        this.allprop = allprop;
    }

    /**
     * Returns allprop flag
     * @return allprop flag
     */
    public boolean isAllprop() {
        return allprop || (!allprop && !propname && (prop == null));
    }

    /**
     * Sets propname flag
     * @param propname propname flag
     */
    public void setPropname(boolean propname) {
        this.propname = propname;
    }

    /**
     * Returns propname flag
     * @return propname flag
     */
    public boolean isPropname() {
        return propname;
    }

    /**
     * Returns request property
     * @return request property
     */
    public RequestProp getProp() {
        return prop;
    }

    @Override
    public String toString() {
        if (isAllprop()) {
            return "PropFind[allprop]";
        } else if (isPropname()) {
            return "PropFind[propname]";
        } else {
            return "PropFind[" + prop + "]";
        }
    }
}
