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
package org.abstracthorizon.danube.webdav.xml.common;

import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.XMLRenderer;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;

import java.io.PrintWriter;

/**
 * This object represents WebDAV &quot;collection&quot; tag
 *
 * @author Daniel Sendula
 */
public class Collection implements XMLRenderer {

    public static final String TAG_NAME = "collection";

    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.print(XMLUtils.createEmptyTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
    }

}
