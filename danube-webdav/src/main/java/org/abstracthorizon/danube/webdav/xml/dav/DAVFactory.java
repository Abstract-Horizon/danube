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
package org.abstracthorizon.danube.webdav.xml.dav;

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.request.KeepAlive;
import org.abstracthorizon.danube.webdav.xml.dav.request.LockEntry;
import org.abstracthorizon.danube.webdav.xml.dav.request.LockInfo;
import org.abstracthorizon.danube.webdav.xml.dav.request.LockScope;
import org.abstracthorizon.danube.webdav.xml.dav.request.LockType;
import org.abstracthorizon.danube.webdav.xml.dav.request.PropFind;
import org.abstracthorizon.danube.webdav.xml.dav.request.PropertyBehavior;
import org.abstracthorizon.danube.webdav.xml.dav.request.PropertyUpdate;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.CreationDate;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.DisplayName;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetContentLanguage;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetContentLength;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetContentType;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetETag;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetLastModified;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.LockDiscovery;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.ResourceType;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.Source;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.SupportedLock;

/**
 * Factory of DAV related objects. (See RFC-2518)
 *
 * @author Daniel Sendula
 */
public class DAVFactory {

    public LockInfo newLockInfo(XMLParserHandler parent) {
        return new LockInfo(parent, this);
    }

    public LockEntry newLockEntry(XMLParserHandler parent) {
        return new LockEntry(parent, this);
    }

    public LockScope newLockScope(XMLParserHandler parent) {
        return new LockScope(parent);
    }

    public LockType newLockType(XMLParserHandler parent) {
        return new LockType(parent);
    }

    public PropertyBehavior newPropertyBehavior(XMLParserHandler parent) {
        return new PropertyBehavior(parent, this);
    }

    public KeepAlive newKeepAlive(XMLParserHandler parent) {
        return new KeepAlive(parent);
    }

    public PropertyUpdate newPropertyUpdate(XMLParserHandler parent) {
        return new PropertyUpdate(parent, this);
    }

    public PropFind newPropFind(XMLParserHandler parent) {
        return new PropFind(parent, this);
    }

    public RequestProp newProp(XMLParserHandler parent) {
        return new RequestProp(parent, this);
    }

    public RequestProp newSet(XMLParserHandler parent) {
        return new RequestProp(parent, this, true);
    }

    public RequestProp newRemove(XMLParserHandler parent) {
        return new RequestProp(parent, this, false);
    }

    public CreationDate newCreationDate(XMLParserHandler parent) {
        return new CreationDate(parent);
    }

    public DisplayName newDisplayName(XMLParserHandler parent) {
        return new DisplayName(parent);
    }

    public GetContentLanguage newGetContentLanguage(XMLParserHandler parent) {
        return new GetContentLanguage(parent);
    }

    public GetContentLength newGetContentLength(XMLParserHandler parent) {
        return new GetContentLength(parent);
    }

    public GetContentType newGetContentType(XMLParserHandler parent) {
        return new GetContentType(parent);
    }

    public GetETag newGetETag(XMLParserHandler parent) {
        return new GetETag(parent);
    }

    public GetLastModified newGetLastModified(XMLParserHandler parent) {
        return new GetLastModified(parent);
    }

    public LockDiscovery newLockDiscovery(XMLParserHandler parent) {
        return new LockDiscovery(parent);
    }

    public ResourceType newResourceType(XMLParserHandler parent) {
        return new ResourceType(parent);
    }

    public Source newSource(XMLParserHandler parent) {
        return new Source(parent);
    }

    public SupportedLock newSupportedLock(XMLParserHandler parent) {
        return new SupportedLock(parent, this);
    }
}
