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
import org.abstracthorizon.danube.webdav.lock.LockingMechanism;

import java.util.HashSet;
import java.util.Set;

/**
 * This object represents &quot;If&quot; header of WebDAV specification.
 *
 * @author Daniel Sendula
 */
public class IF {

    /** Collected token of a main resource. May be <code>null</code> */
    public String token;

    /** Collected token of a parent resource. May be <code>null</code> */
    public String parentToken;

    /** Collection of resource that was cleared. May be <code>null</code> */
    public Set<Object> clearedResources;

    /** <code>true<code> if header parsing wasn't successful */
    public boolean error;

    /**
     * Parses &quot;if&quot; header and checks if values correspond
     * to given resource.
     *
     * It returns lock token of supplied main resource. If it failed
     * <code>null</code> will be returned.
     *
     * @param httpConnection http connection
     * @param adapter resource adapter
     * @param mainResource main resource
     * @param parentResource parent resource. May be null.
     * @param header if header
     * @return lock token of supplied main resource or <code>null</code>
     */
    public boolean parse(HTTPConnection httpConnection, ResourceAdapter adapter, Object mainResource, Object parentResource) {
        String[] ifs = httpConnection.getRequestHeaders().getAsArray("If");
        if ((ifs == null) || (ifs.length == 0)) {
            return true;
//            if (adapter.getLockingMechanism().isLocked(mainResource)) {
//                error = true;
//                return false;
//            } else {
//                return true;
//            }
        }
        int len = 0;
        for (String s : ifs) {
            len = len + s.length() + 1;
        }
        char[] header = new char[len];
        len = 0;
        for (String s : ifs) {
            s.getChars(0, s.length(), header, len);
            len = len + s.length();
            header[len] = ' ';
            len = len + 1;
        }
        return parse(httpConnection, adapter, mainResource, parentResource, header);
    }

    /**
     * Parses &quot;if&quot; header and checks if values correspond
     * to given resource.
     *
     * It returns lock token of supplied main resource. If it failed
     * <code>null</code> will be returned.
     *
     * @param httpConnection http connection
     * @param adapter resource adapter
     * @param mainResource main resource
     * @param parentResource parent resource. May be null.
     * @param header if header
     * @return lock token of supplied main resource or <code>null</code>
     */
    public boolean parse(HTTPConnection httpConnection, ResourceAdapter adapter, Object mainResource, Object parentResource, char[] buf) {
        boolean orResult = false;
        boolean andResult = true;
        boolean parentCleared = (parentResource == null);

        char[] temp = new char[buf.length];
        int tp = 0;
        int p = 0;
        int state = 0;
        Object currentResource = null;
        boolean not = false;

        while (p < buf.length) {
            char c = buf[p];
            if (state == 0) {
                if (c == ' ') {
                    // ignore spaces
                } else if (c == '<') {
                    if ((currentResource != null) && !orResult) {
                        // it is not first resource and result is still false.
                        error = true;
                        return false;
                    }
                    tp = 0;
                    state = 1;
                } else if (c == '(') {
                    andResult = true;
                    if (currentResource == null) {
                        currentResource = mainResource;
                    }
                    not = false;
                    state = 4;
                } else {
                    p = buf.length;
                }
            } else if (state == 1) {
                if (c == '>') {
                    if (tp == 0) {
                        error = true;
                        return false;
                    }
                    String uriString = new String(temp, 0, tp);
                    tp = 0;
                    currentResource = URIUtils.uriToResource(httpConnection, adapter, uriString);
                    if (currentResource == null) {
                        error = true;
                        return false;
                    }
                    state = 3;
                } else {
                    temp[tp] = c;
                    tp++;
                }
            } else if (state == 3) {
                if (c == ' ') {
                } else if (c == '(') {
                    andResult = true;
                    not = false;
                    state = 4;
                } else {
                    error = true;
                    return false;
                }
            } else if (state == 4) {
                if (c == ' ') {
                    // ignore
                } else if (c == 'N') {
                    state = 5;
                } else if (c == '<') {
                    not = false;
                    state = 8;
                    tp = 0;
                } else if (c == '[') {
                    not = false;
                    tp = 0;
                    state = 9;
                } else if (c == ')') {
                    orResult = orResult || andResult;
                    state = 0;
                } else {
                    error = true;
                    return false;
                }
            } else if (state == 5) {
                if (c == 'o') {
                    state = 6;
                } else {
                    return false;
                }
            } else if (state == 6) {
                if (c == 't') {
                    not = true;
                    state = 7;
                } else {
                    return false;
                }
            } else if (state == 7) {
                if (c == ' ') {
                    // ignore
                } else if (c == '<') {
                    tp = 0;
                    state = 8;
                } else if (c == '[') {
                    tp = 0;
                    state = 9;
                }
            } else if (state == 8) {
                if (c == '>') {
                    if (tp == 0) {
                        return false;
                    }
                    String tokenString = new String(temp, 0, tp);
                    tp = 0;

                    LockingMechanism lockingMechanism = adapter.getLockingMechanism();
                    if (lockingMechanism.isAccessAllowed(currentResource, tokenString)) {
                        if (not) {
                            andResult = false;
                        } else {
                            if (currentResource == mainResource) {
                                token = tokenString;
                            } else if (currentResource == parentResource) {
                                parentCleared = true;
                                parentToken = tokenString;
                            } else if (currentResource.equals(mainResource)) {
                                token = tokenString;
                            } else if ((parentResource != null) && parentResource.equals(currentResource)) {
                                parentCleared = true;
                                parentToken = tokenString;
                            } else {
                                if (clearedResources == null) {
                                    clearedResources = new HashSet<Object>();
                                }
                                clearedResources.add(currentResource);
                            }
                        }
                    } else {
                        if (!not) {
                            andResult = false;
                        }
                    }

                    not = false;
                    state = 4;
                } else {
                    temp[tp] = c;
                    tp++;
                }
            } else if (state == 9) {
                if (c == ']') {
                    if (tp == 0) {
                        error = true;
                        return false;
                    }
                    String etagToken = new String(temp, 0, tp);
                    tp = 0;

                    String etag = adapter.getResourceETag(currentResource);
                    if ((etag != null) && etag.equals(etagToken)) {
                        if (not) {
                            andResult = false;
                        }
                    } else {
                        if (!not) {
                            andResult = false;
                        }
                    }

                    not = false;
                    state = 4;
                } else {
                    temp[tp] = c;
                    tp++;
                }
            }

            p = p + 1;
        }

        return ((state == 0) && orResult && parentCleared);
    }
}
