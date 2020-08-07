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
package org.abstracthorizon.danube.webdav.util;

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;

/**
 * Simple name space provider implementation
 *
 * @author Daniel Sendula
 */
public class SimpleNamespacesProvider implements NamespacesProvider {

    /** Array of namespaces */
    protected NamespaceSlot[] namespaces = new NamespaceSlot[5];

    /** Number of namespaces in the array */
    protected int numberOfNamespaces = 0;

    /**
     * Returns an array of defined URLS
     * @return an array of defined URLS
     */
    public String[] getDefinedURLs() {
        String[] result = new String[numberOfNamespaces];
        for (int i = 0; i < numberOfNamespaces; i++) {
            result[i] = namespaces[i].url;
        }
        return result;
    }

    /**
     * Returns assigned prefix for given URL
     * @param url URL
     * @return assigned prefix for given URL
     */
    public String getAssignedPrefix(String url) {
        for (int i = 0; i < numberOfNamespaces; i++) {
            if (namespaces[i].url == url) {
                return namespaces[i].prefix;
            }
        }
        for (int i = 0; i < numberOfNamespaces; i++) {
            if (namespaces[i].url.equals(url)) {
                return namespaces[i].prefix;
            }
        }
        return null;
    }

    /**
     * Returns {@link XMLParserHandler} for given URL
     * @param url URL
     * @return {@link XMLParserHandler} for given URL
     */
    public XMLParserHandler getParserHandler(String url) {
        for (int i = 0; i < numberOfNamespaces; i++) {
            if (namespaces[i].url == url) {
                return namespaces[i].parserHandler;
            }
        }
        for (int i = 0; i < numberOfNamespaces; i++) {
            if (namespaces[i].url.equals(url)) {
                return namespaces[i].parserHandler;
            }
        }

        return null;
    }

    /**
     * Class that defines namespace
     *
     * @author Daniel Sendula
     */
    protected static class NamespaceSlot {
        /** Prefix */
        String prefix;

        /** URL */
        String url;

        /** Parser handler */
        XMLParserHandler parserHandler;
    }

    /**
     * Adds new namespace to the provider
     * @param url namespace's URL
     * @param preferredPrefix preferred prefix
     * @param parserHandler parser handler
     */
    public synchronized void addNamespace(String url, String preferredPrefix, XMLParserHandler parserHandler) {
        if (namespaces.length == numberOfNamespaces) {
            NamespaceSlot[] newNamespaces = new NamespaceSlot[namespaces.length + (namespaces.length / 2)];
            System.arraycopy(namespaces, 0, newNamespaces, 0, numberOfNamespaces);
            namespaces = newNamespaces;
        }
        NamespaceSlot newSlot = new NamespaceSlot();
        newSlot.parserHandler = parserHandler;
        newSlot.url = url;

        if (getAssignedPrefix(preferredPrefix) != null) {
            newSlot.prefix = new String(new char[]{(char)('a' + numberOfNamespaces)});
        } else {
            newSlot.prefix = preferredPrefix;
        }
        namespaces[numberOfNamespaces] = newSlot;
        numberOfNamespaces = numberOfNamespaces + 1;
    }

}
