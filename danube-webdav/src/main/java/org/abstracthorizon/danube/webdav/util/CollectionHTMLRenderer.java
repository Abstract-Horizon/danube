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
package org.abstracthorizon.danube.webdav.util;

import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.webdav.ResourceAdapter;

/**
 * Interface defining a renderer of a collection of resources
 *
 * @author Daniel Sendula
 */
public interface CollectionHTMLRenderer {

    /**
     * Render a resource
     * @param httpConnection connection
     * @param adapter resource adapter
     * @param collection collection resource
     */
    void render(HTTPConnection httpConnection, ResourceAdapter adapter, Object collection);
}
