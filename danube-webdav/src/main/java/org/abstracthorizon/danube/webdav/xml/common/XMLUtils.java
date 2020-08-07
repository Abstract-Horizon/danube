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

/**
 * Sets of utility methods for creating an XML output
 *
 * @author Daniel Sendula
 */
public class XMLUtils {

    /**
     * Creates a start tag with given namespace prefix
     *
     * @param provider namespace provider
     * @param url namespace URL
     * @param tagName tag name
     * @return start tag
     */
    public static String createStartTag(NamespacesProvider provider, String url, String tagName) {
        StringBuffer result = new StringBuffer();
        result.append('<').append(provider.getAssignedPrefix(url)).append(':').append(tagName).append('>');
        return result.toString();
    }

    /**
     * Creates an open start tag with given namespace prefix
     *
     * @param provider namespace provider
     * @param url namespace URL
     * @param tagName tag name
     * @return start tag
     */
    public static String createStartOpenTag(NamespacesProvider provider, String url, String tagName) {
        StringBuffer result = new StringBuffer();
        result.append('<').append(provider.getAssignedPrefix(url)).append(':').append(tagName);
        return result.toString();
    }

    /**
     * Creates an end tag with given namespace prefix
     *
     * @param provider namespace provider
     * @param url namespace URL
     * @param tagName tag name
     * @return end tag
     */
    public static String createEndTag(NamespacesProvider provider, String url, String tagName) {
        StringBuffer result = new StringBuffer();
        result.append('<').append('/').append(provider.getAssignedPrefix(url)).append(':').append(tagName).append('>');
        return result.toString();
    }

    /**
     * Creates a tag with given namespace prefix and tag's content
     *
     * @param provider namespace provider
     * @param url namespace URL
     * @param tagName tag name
     * @param content content. May be <code>null</code> and an empty tag is going to be generated then
     * @return start tag
     */
    public static String createTag(NamespacesProvider provider, String url, String tagName, Object content) {
        if (content == null) {
            return createEmptyTag(provider, url, tagName);
        } else {
            StringBuffer result = new StringBuffer();
            result.append('<').append(provider.getAssignedPrefix(url)).append(':').append(tagName).append('>');
            result.append(content);
            result.append('<').append('/').append(provider.getAssignedPrefix(url)).append(':').append(tagName).append('>');
            return result.toString();
        }
    }

    /**
     * Creates an empty tag with given namespace prefix
     *
     * @param provider namespace provider
     * @param url namespace URL
     * @param tagName tag name
     * @return start tag
     */
    public static String createEmptyTag(NamespacesProvider provider, String url, String tagName) {
        StringBuffer result = new StringBuffer();
        result.append('<').append(provider.getAssignedPrefix(url)).append(':').append(tagName).append('/').append('>');
        return result.toString();
    }


}
