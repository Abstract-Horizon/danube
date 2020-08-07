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
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.Assert;

/**
 * Test case for PUT method
 *
 * @author Daniel Sendula
 */
public class WebDAVPUTMethodFileConnectionHandlerTest extends WebDAVFileConnectionHandlerTestBase {

    public void testPUTNew() throws IOException {
        int size = 318;
        String resourceSourceName = "something1.txt";
        String resourceDestName = "something2.txt";
        File sourceFile = IOUtils.createRandomFile(environment.dir, resourceSourceName, size);
        URL url = environment.getURL(resourceDestName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        try {
            FileInputStream inputStream = new FileInputStream(sourceFile);
            try {
                IOUtils.copyStreams(inputStream, outputStream);
            } finally {
                inputStream.close();
            }
        } finally {
            outputStream.close();
        }

        File destFile = new File(environment.dir, resourceDestName);

        Assert.assertEquals(201, urlConnection.getResponseCode());
        FileInputStream sourceInputStream = new FileInputStream(sourceFile);
        FileInputStream destInputStream = new FileInputStream(destFile);

        String res = IOUtils.compareStreams(sourceInputStream, destInputStream);
        if (res != null) {
            Assert.assertTrue(res, false);
        }
    }

    public void testPUTOldError() throws IOException {
        int size = 318;
        String resourceSourceName = "something1.txt";
        String resourceDestName = "something2.txt";
        File sourceFile = IOUtils.createRandomFile(environment.dir, resourceSourceName, size);
        File destFile = new File(environment.dir, resourceDestName);
        destFile.mkdir();
        URL url = environment.getURL(resourceDestName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        try {
            FileInputStream inputStream = new FileInputStream(sourceFile);
            try {
                IOUtils.copyStreams(inputStream, outputStream);
            } finally {
                inputStream.close();
            }
        } finally {
            outputStream.close();
        }


        Assert.assertEquals(409, urlConnection.getResponseCode());
    }


    public void testPUTNewError() throws IOException {
        int size = 318;
        String resourceSourceName = "something1.txt";
        File sourceFile = IOUtils.createRandomFile(environment.dir, resourceSourceName, size);
        String resourceDestName = "unknowndirectory/something2.txt";
        URL url = environment.getURL(resourceDestName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        try {
            FileInputStream inputStream = new FileInputStream(sourceFile);
            try {
                IOUtils.copyStreams(inputStream, outputStream);
            } finally {
                inputStream.close();
            }
        } finally {
            outputStream.close();
        }

        Assert.assertEquals(409, urlConnection.getResponseCode());
    }



    public void testPUTOldBigger() throws IOException {
        int size = 318;
        int oldSize = 514;
        doPUTOld(size, oldSize);
    }

    public void testPUTOldSmaller() throws IOException {
        int size = 318;
        int oldSize = 227;
        doPUTOld(size, oldSize);
    }

    protected void doPUTOld(int size, int oldsize) throws IOException {
        String resourceSourceName = "something1.txt";
        String resourceDestName = "something2.txt";
        File destFile = new File(environment.dir, resourceDestName);
        RandomAccessFile raf = new RandomAccessFile(destFile, "rw");
        raf.setLength(oldsize);
        raf.close();

        File sourceFile = IOUtils.createRandomFile(environment.dir, resourceSourceName, size);
        URL url = environment.getURL(resourceDestName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        try {
            FileInputStream inputStream = new FileInputStream(sourceFile);
            try {
                IOUtils.copyStreams(inputStream, outputStream);
            } finally {
                inputStream.close();
            }
        } finally {
            outputStream.close();
        }


        Assert.assertEquals(204, urlConnection.getResponseCode());
        FileInputStream sourceInputStream = new FileInputStream(sourceFile);
        FileInputStream destInputStream = new FileInputStream(destFile);

        String res = IOUtils.compareStreams(sourceInputStream, destInputStream);
        if (res != null) {
            Assert.assertTrue(res, false);
        }
    }

    public void testPUTRange() throws IOException {
        String destFileName = "destfile.txt";
        File file = new File(environment.dir, destFileName);
        doPUTRange(destFileName, 100, 418, 998);
        Assert.assertTrue("Cannot delete destinatino file", file.delete());
        doPUTRange(destFileName, 0, 765, 766);
        Assert.assertTrue("Cannot delete destinatino file", file.delete());
        doPUTRange(destFileName, 0, 361, 889);
        Assert.assertTrue("Cannot delete destinatino file", file.delete());
    }

    protected void doPUTRange(String destFileName, int from, int to, int size) throws IOException {
        String resourceSourceName = "something1.txt";
        File sourceFile = IOUtils.createRandomFile(environment.dir, resourceSourceName, to - from);
        URL url = environment.getURL(destFileName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.addRequestProperty("Content-Range", "bytes " + from + "-" + to + "/" + size);
        OutputStream outputStream = urlConnection.getOutputStream();
        try {
            FileInputStream inputStream = new FileInputStream(sourceFile);
            try {
                IOUtils.copyStreams(inputStream, outputStream);
            } finally {
                inputStream.close();
            }
        } finally {
            outputStream.close();
        }

        File destFile = new File(environment.dir, destFileName);

        Assert.assertEquals(201, urlConnection.getResponseCode());
        Assert.assertEquals(to, destFile.length());
        FileInputStream sourceInputStream = new FileInputStream(sourceFile);
        try {
            FileInputStream destInputStream = new FileInputStream(destFile);
            try {
                if (from > 0) {
                    byte[] buf = new byte[from];
                    destInputStream.read(buf);
                }
        
                String res = IOUtils.compareStreams(sourceInputStream, destInputStream, 318);
                if (res != null) {
                    Assert.assertTrue(res, false);
                }
            } finally {
                destInputStream.close();
            }
        } finally {
            sourceInputStream.close();
        }
        Assert.assertTrue("Cannot remove test file", sourceFile.delete());
    }


}
