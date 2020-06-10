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

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;

/**
 * An interface that defined namespace provider
 *
 * @author Daniel Sendula
 */
public interface NamespacesProvider {

    /**
     * Returns an array of defined URLS
     * @return an array of defined URLS
     */
    String[] getDefinedURLs();

    /**
     * Returns assigned prefix for given URL
     * @param url URL
     * @return assigned prefix for given URL
     */
    String getAssignedPrefix(String url);

    /**
     * Returns {@link XMLParserHandler} for given URL
     * @param url URL
     * @return {@link XMLParserHandler} for given URL
     */
    XMLParserHandler getParserHandler(String url);

    /**
     * Adds new namespace to the provider
     * @param url namespace's URL
     * @param preferredPrefix preferred prefix
     * @param parserHandler parser handler
     */
    void addNamespace(String url, String preferredPrefix, XMLParserHandler parserHandler);

}
