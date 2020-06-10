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
package org.abstracthorizon.danube.webdav.xml;

import org.abstracthorizon.danube.webdav.util.NamespacesProvider;

import java.io.PrintWriter;

/**
 * XML Renderer
 *
 * @author Daniel Sendula
 */
public interface XMLRenderer {

    /**
     * Renders the xml
     * @param writer writer to render to
     * @param provider namespace provider to obtain tag prefix, etc...
     */
    void render(PrintWriter writer, NamespacesProvider provider);

}
