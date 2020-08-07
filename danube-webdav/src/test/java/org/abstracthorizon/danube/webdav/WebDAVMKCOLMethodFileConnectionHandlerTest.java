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

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.Assert;

/**
 * Test case for MKCOL method
 *
 * @author Daniel Sendula
 */
public class WebDAVMKCOLMethodFileConnectionHandlerTest extends WebDAVFileConnectionHandlerTestBase {

    public void testMKCOL() throws IOException {
        String resourceSourceName = "newdir";
        File sourceDir = new File(environment.dir, resourceSourceName);
        URL url = environment.getURL(resourceSourceName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("MKCOL");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(false);

        Assert.assertEquals(201, urlConnection.getResponseCode());
        Assert.assertTrue("Dir has not been created", sourceDir.exists());
    }

    public void testMKCOLExisting() throws IOException {
        String resourceSourceName = "newdir";
        File sourceDir = new File(environment.dir, resourceSourceName);
        sourceDir.mkdir();
        URL url = environment.getURL(resourceSourceName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("MKCOL");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(false);

        Assert.assertEquals(405, urlConnection.getResponseCode());
    }

    public void testMKCOLUnknownParent() throws IOException {
        String resourceSourceName = "unknown/newdir";
        URL url = environment.getURL(resourceSourceName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("MKCOL");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(false);

        Assert.assertEquals(409, urlConnection.getResponseCode());
    }

}
