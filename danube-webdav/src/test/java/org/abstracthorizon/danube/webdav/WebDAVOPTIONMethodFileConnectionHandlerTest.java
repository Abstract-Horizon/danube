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
package org.abstracthorizon.danube.webdav;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.Assert;

/**
 * Test case for OPTION method
 *
 * @author Daniel Sendula
 */
public class WebDAVOPTIONMethodFileConnectionHandlerTest extends WebDAVFileConnectionHandlerTestBase {

    public void testOPTIONWithLock() throws IOException {
        URL url = environment.getURL();
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("OPTIONS");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(false);

        Assert.assertEquals(200, urlConnection.getResponseCode());
        // "PROPPATCH, DELETE, COPY, GET, PROPFIND, PUT, MOVE, UNLOCK, TRACE, OPTIONS, HEAD, LOCK, MKCOL"
        String allow = urlConnection.getHeaderField("Allow");
        assertMethodTrue(allow, "GET");
        assertMethodTrue(allow, "PUT");
        assertMethodTrue(allow, "DELETE");
        assertMethodTrue(allow, "HEAD");
        assertMethodTrue(allow, "TRACE");
        assertMethodTrue(allow, "OPTIONS");
        assertMethodTrue(allow, "PROPFIND");
        assertMethodTrue(allow, "PROPPATCH");
        assertMethodTrue(allow, "COPY");
        assertMethodTrue(allow, "MOVE");
        assertMethodTrue(allow, "MKCOL");
        assertMethodTrue(allow, "LOCK");
        assertMethodTrue(allow, "UNLOCK");
    }

    public void testOPTIONWithoutLock() throws IOException {
        environment.fileSystemWebDAVAdapter.setLockingMechanism(null);
        URL url = environment.getURL();
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("OPTIONS");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(false);

        Assert.assertEquals(200, urlConnection.getResponseCode());
        // "PROPPATCH, DELETE, COPY, GET, PROPFIND, PUT, MOVE, UNLOCK, TRACE, OPTIONS, HEAD, LOCK, MKCOL"
        String allow = urlConnection.getHeaderField("Allow");
        assertMethodTrue(allow, "GET");
        assertMethodTrue(allow, "PUT");
        assertMethodTrue(allow, "DELETE");
        assertMethodTrue(allow, "HEAD");
        assertMethodTrue(allow, "TRACE");
        assertMethodTrue(allow, "OPTIONS");
        assertMethodTrue(allow, "PROPFIND");
        assertMethodTrue(allow, "PROPPATCH");
        assertMethodTrue(allow, "COPY");
        assertMethodTrue(allow, "MOVE");
        assertMethodTrue(allow, "MKCOL");
        assertMethodFalse(allow, "LOCK");
        assertMethodFalse(allow, "UNLOCK");
    }

    protected void assertMethodTrue(String allow, String method) {
        Assert.assertTrue("Missing " + method + " method", allow.indexOf(method) >= 0);

    }

    protected void assertMethodFalse(String allow, String method) {
        Assert.assertFalse("Method " + method + " is present", allow.indexOf(method) >= 0);

    }

}
