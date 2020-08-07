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

import org.abstracthorizon.danube.http.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.Assert;

/**
 * Delete method test case
 *
 * @author Daniel Sendula
 */
public class WebDAVDELETEMethodFileConnectionHandlerTest extends WebDAVFileConnectionHandlerTestBase {

    public void testDELETEExisting() throws IOException {
        int size = 318;
        String resourceSourceName = "something.txt";
        File sourceFile = IOUtils.createRandomFile(environment.dir, resourceSourceName, size);
        URL url = environment.getURL(resourceSourceName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(false);

        Assert.assertEquals(204, urlConnection.getResponseCode());
        Assert.assertFalse("File has not been deleted", sourceFile.exists());
    }

    public void testDELETEUnknown() throws IOException {
        String resourceSourceName = "something.txt";
        URL url = environment.getURL(resourceSourceName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(false);

        Assert.assertEquals(404, urlConnection.getResponseCode());
    }

    public void testDELETEEmptyCollection() throws IOException {
        String resourceSourceName = "testdir";
        File sourceDir = new File(environment.dir, resourceSourceName);
        sourceDir.mkdir();
        URL url = environment.getURL(resourceSourceName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(false);

        Assert.assertEquals(204, urlConnection.getResponseCode());
        Assert.assertFalse("Dir has not been deleted", sourceDir.exists());
    }

    public void testDELETECollection() throws IOException {
        int size = 318;
        String resourceSourceName = "testdir";
        File sourceDir = new File(environment.dir, resourceSourceName);
        sourceDir.mkdir();
        IOUtils.createRandomFile(sourceDir, "file1", size);
        IOUtils.createRandomFile(sourceDir, "file2", size * 2);
        File subdir1 = new File(sourceDir, "subdir1");
        File subdir2 = new File(sourceDir, "subdir2");
        subdir1.mkdir();
        subdir2.mkdir();
        IOUtils.createRandomFile(subdir1, "subfile1", size + size / 2);
        URL url = environment.getURL(resourceSourceName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(false);

        Assert.assertEquals(204, urlConnection.getResponseCode());
        Assert.assertFalse("File has not been deleted", sourceDir.exists());
    }

}
