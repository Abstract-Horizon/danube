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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.Assert;

/**
 * Test case for GET method
 *
 * @author Daniel Sendula
 */
public class WebDAVGETMethodFileConnectionHandlerTest extends WebDAVFileConnectionHandlerTestBase {

    public void testGET() throws IOException {
        int size = 318;
        String resource = "something.txt";
        File file = IOUtils.createRandomFile(environment.dir, resource, size);
        URL url = environment.getURL(resource);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);
        InputStream conInputStream = urlConnection.getInputStream();
        FileInputStream fileInputStream = new FileInputStream(file);

        String res = IOUtils.compareStreams(fileInputStream, conInputStream);
        if (res != null) {
            Assert.assertTrue(res, false);
        }
        Assert.assertEquals(200, urlConnection.getResponseCode());
    }

    public void testGETRange() throws IOException {
        int size = 318;
        int from = 101;
        int to = 205;

        String resource = "something.txt";
        File file = IOUtils.createRandomFile(environment.dir, resource, size);
        URL url = environment.getURL(resource);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.addRequestProperty("Range", "bytes=" + from + "-" + to);
        urlConnection.setDoInput(true);
        InputStream conInputStream = urlConnection.getInputStream();
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.skip(from);


        String res = IOUtils.compareStreams(fileInputStream, conInputStream, (to - from));
        if (res != null) {
            Assert.assertTrue(res, false);
        }
        String s = "bytes " + from + "-" + to + "/" + size;
        System.out.println(s);
        Assert.assertEquals(s, urlConnection.getHeaderField("Content-Range"));
        Assert.assertEquals(206, urlConnection.getResponseCode());
    }

    public void testGETPrefixRange() throws IOException {
        int size = 318;
        int from = 101;

        String resource = "something.txt";
        File file = IOUtils.createRandomFile(environment.dir, resource, size);
        URL url = environment.getURL(resource);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.addRequestProperty("Range", "bytes=" + from + "-");
        urlConnection.setDoInput(true);
        InputStream conInputStream = urlConnection.getInputStream();
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.skip(from);


        String res = IOUtils.compareStreams(fileInputStream, conInputStream, (size - from));
        if (res != null) {
            Assert.assertTrue(res, false);
        }
        Assert.assertEquals("bytes " + from + "-" + (size - 1) + "/" + size, urlConnection.getHeaderField("Content-Range"));
        Assert.assertEquals(206, urlConnection.getResponseCode());
    }

    public void testGETSuffixRange() throws IOException {
        int size = 318;
        int last = 118;

        String resource = "something.txt";
        File file = IOUtils.createRandomFile(environment.dir, resource, size);
        URL url = environment.getURL(resource);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.addRequestProperty("Range", "bytes=-" + last);
        urlConnection.setDoInput(true);
        InputStream conInputStream = urlConnection.getInputStream();
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.skip(size - last);

        String res = IOUtils.compareStreams(fileInputStream, conInputStream, last);
        if (res != null) {
            Assert.assertTrue(res, false);
        }
        Assert.assertEquals("bytes " + (size - last) + "-" + (size - 1) + "/" + size, urlConnection.getHeaderField("Content-Range"));
        Assert.assertEquals(206, urlConnection.getResponseCode());
    }

    public void testGETMultiRange() throws IOException {

        String resource = "something.txt";
        IOUtils.createRandomFile(environment.dir, resource, 0);
        URL url = environment.getURL(resource);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.addRequestProperty("Range", "bytes=10-20,30-40");
        urlConnection.setDoInput(true);

        Assert.assertEquals(415, urlConnection.getResponseCode());
    }

    public void testGETUnsatisfiedRange() throws IOException {
        int size = 318;

        String resource = "something.txt";
        IOUtils.createRandomFile(environment.dir, resource, size);
        URL url = environment.getURL(resource);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.addRequestProperty("Range", "bytes=0-" + (size+1));
        urlConnection.setDoInput(true);

        Assert.assertEquals(416, urlConnection.getResponseCode());
    }

    public void testGETUnsatisfiedSuffixRange() throws IOException {
        int size = 318;

        String resource = "something.txt";
        IOUtils.createRandomFile(environment.dir, resource, size);
        URL url = environment.getURL(resource);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.addRequestProperty("Range", "bytes=-" + (size+1));
        urlConnection.setDoInput(true);

        Assert.assertEquals(416, urlConnection.getResponseCode());
    }

    public void testGETUnknownResource() throws IOException {
        String resource = "something.txt";
        URL url = environment.getURL(resource);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);
        Assert.assertEquals(404, urlConnection.getResponseCode());
    }

}
