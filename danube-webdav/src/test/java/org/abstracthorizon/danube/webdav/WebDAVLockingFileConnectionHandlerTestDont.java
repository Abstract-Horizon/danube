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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import junit.framework.Assert;

/**
 * Test case for Locking
 *
 * @author Daniel Sendula
 */
public class WebDAVLockingFileConnectionHandlerTestDont extends WebDAVFileConnectionHandlerTestBase {

    public void testSimpleLockScenario() throws Exception {
        int size = 987;
        String resourceName = "something.txt";
        String source2Name = "temp.txt";

        File sourceFile = IOUtils.createRandomFile(environment.dir, resourceName, size);
        environment.doMethod("GET", resourceName, 200, sourceFile, null);

//        URL url = environment.getURL(resourceName);

        String lockInfo = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
            + "<D:lockinfo xmlns:D='DAV:'>\n"
            + "<D:lockscope><D:exclusive/></D:lockscope>\n"
            + "<D:locktype><D:write/></D:locktype>\n"
            + "<D:owner><D:href>http://www.ics.uci.edu/~ejw/contact.html</D:href></D:owner>\n"
            + "</D:lockinfo>";
        String expected = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
            + "<D:prop xmlns:D=\"DAV:\">\n"
            + "<D:lockdiscovery>\n"
            + "<D:activelock>\n"
            + "<D:locktype><D:write/></D:locktype>\n"
            + "<D:lockscope><D:exclusive/></D:lockscope>\n"
            + "<D:depth>Infinity</D:depth>\n"
            + "<D:owner>\n"
            + "<D:href>http://www.ics.uci.edu/~ejw/contact.html</D:href>\n"
            + "</D:owner>\n"
            + "<D:locktoken>\n"
            + "<D:href>opaquelocktoken:" + Long.toHexString(sourceFile.lastModified()) + "-1</D:href>\n"
            + "</D:locktoken>\n"
            + "</D:activelock>\n"
            + "</D:lockdiscovery>\n"
            + "</D:prop>\n";
        System.out.println();
        System.out.println(Long.toHexString(sourceFile.lastModified()));
//            + "<D:multistatus xmlns:D=\"DAV:\">\n"
//            + "<D:response>\n"
//            + "<D:href>" + url.getPath() + "</D:href>\n"
//            + "<D:status>HTTP/1.1 200 OK</D:status>\n"
//            + "</D:response>\n"
//            + "</D:multistatus>\n";

        HttpURLConnection connection = environment.doMethod("LOCK", resourceName, lockInfo, 200, expected, null);
        String token = connection.getHeaderField("Lock-Token");
        Assert.assertNotNull("Missing Lock-Token", token);

        File source2File = IOUtils.createRandomFile(environment.dir, source2Name, size);
        environment.doMethod("PUT", resourceName, source2File, 423, (InputStream)null, null);

        HashMap<String, String> ifTokenHeader = new HashMap<String, String>();
        ifTokenHeader.put("IF", "(<" + token + ">)");
        environment.doMethod("PUT", resourceName, source2File, 204, (InputStream)null, ifTokenHeader);
        Environment.compareFiles(source2File, sourceFile);


        HashMap<String, String> lockTokenHeader = new HashMap<String, String>();
        lockTokenHeader.put("Lock-Token", "(<" + token + ">)");
        environment.doMethod("UNLOCK", resourceName, (InputStream)null, 204, (InputStream)null, lockTokenHeader);


        source2File = IOUtils.createRandomFile(environment.dir, source2Name, size);
        environment.doMethod("PUT", resourceName, source2File, 204, (InputStream)null, ifTokenHeader);

        Environment.compareFiles(source2File, sourceFile);
    }

    public void testPUTWithLockedParentDeep() throws Exception {
        int size = 987;
        String source2Name = "temp.txt";
        String path = "dir1/dir2";
        File pathFile = new File(environment.dir, path);
        Assert.assertTrue("Cannot create path " + path, pathFile.mkdirs());
        String resourceName = "dir1/dir2/something.txt";

        File sourceFile = IOUtils.createRandomFile(environment.dir, resourceName, size);
        environment.doMethod("GET", resourceName, 200, sourceFile, null);

        URL pathUrl = environment.getURL(path);
        URL resourceUrl = environment.getURL(resourceName);

        String lockInfo = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
            + "<D:lockinfo xmlns:D='DAV:'>\n"
            + "<D:lockscope><D:exclusive/></D:lockscope>\n"
            + "<D:locktype><D:write/></D:locktype>\n"
            + "<D:owner><D:href>http://www.ics.uci.edu/~ejw/contact.html</D:href></D:owner>\n"
            + "</D:lockinfo>";
        String expected = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
            + "<D:multistatus xmlns:D=\"DAV:\">\n"
            + "<D:response>\n"
            + "<D:href>" + pathUrl.getPath() + "</D:href>\n"
            + "<D:status>HTTP/1.1 200 OK</D:status>\n"
            + "</D:response>\n"
            + "<D:response>\n"
            + "<D:href>" + resourceUrl.getPath() + "</D:href>\n"
            + "<D:status>HTTP/1.1 200 OK</D:status>\n"
            + "</D:response>\n"
            + "</D:multistatus>\n";

        HttpURLConnection connection = environment.doMethod("LOCK", path, lockInfo, 207, expected, null);
        String token = connection.getHeaderField("Lock-Token");
        Assert.assertNotNull("Missing Lock-Token", token);

        File source2File = IOUtils.createRandomFile(environment.dir, source2Name, size);
        environment.doMethod("PUT", resourceName, source2File, 423, (InputStream)null, null);

        HashMap<String, String> ifTokenHeader = new HashMap<String, String>();
        ifTokenHeader.put("IF", "(<" + token + ">)");
        environment.doMethod("PUT", resourceName, source2File, 204, (InputStream)null, ifTokenHeader);
        Environment.compareFiles(source2File, sourceFile);


        HashMap<String, String> lockTokenHeader = new HashMap<String, String>();
        lockTokenHeader.put("Lock-Token", "(<" + token + ">)");
        environment.doMethod("UNLOCK", resourceName, (InputStream)null, 204, (InputStream)null, lockTokenHeader);


        source2File = IOUtils.createRandomFile(environment.dir, source2Name, size);
        environment.doMethod("PUT", resourceName, source2File, 204, (InputStream)null, ifTokenHeader);

        Environment.compareFiles(source2File, sourceFile);
    }

    public void testPUTWithLockedParent() throws Exception {
        int size = 987;
        String source2Name = "temp.txt";
        String path = "dir1/dir2";
        File pathFile = new File(environment.dir, path);
        Assert.assertTrue("Cannot create path " + path, pathFile.mkdirs());
        String resourceName = "dir1/dir2/something.txt";

        File sourceFile = IOUtils.createRandomFile(environment.dir, resourceName, size);
        environment.doMethod("GET", resourceName, 200, sourceFile, null);

        URL pathUrl = environment.getURL(path);



        String lockInfo = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
            + "<D:lockinfo xmlns:D='DAV:'>\n"
            + "<D:lockscope><D:exclusive/></D:lockscope>\n"
            + "<D:locktype><D:write/></D:locktype>\n"
            + "<D:owner><D:href>http://www.ics.uci.edu/~ejw/contact.html</D:href></D:owner>\n"
            + "</D:lockinfo>";
        String expected = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
            + "<D:multistatus xmlns:D=\"DAV:\">\n"
            + "<D:response>\n"
            + "<D:href>" + pathUrl.getPath() + "</D:href>\n"
            + "<D:status>HTTP/1.1 200 OK</D:status>\n"
            + "</D:response>\n"
            + "</D:multistatus>\n";

        HashMap<String, String> depthHeader = new HashMap<String, String>();
        depthHeader.put("Depth","0");
        HttpURLConnection connection = environment.doMethod("LOCK", path, lockInfo, 200, expected, depthHeader);
        String token = connection.getHeaderField("Lock-Token");
        Assert.assertNotNull("Missing Lock-Token", token);

        File source2File = IOUtils.createRandomFile(environment.dir, source2Name, size);
        environment.doMethod("PUT", resourceName, source2File, 423, (InputStream)null, null);

        HashMap<String, String> ifTokenHeader = new HashMap<String, String>();
        ifTokenHeader.put("IF", "(<" + token + ">)");
        environment.doMethod("PUT", resourceName, source2File, 204, (InputStream)null, ifTokenHeader);
        Environment.compareFiles(source2File, sourceFile);


        HashMap<String, String> lockTokenHeader = new HashMap<String, String>();
        lockTokenHeader.put("Lock-Token", "(<" + token + ">)");
        environment.doMethod("UNLOCK", resourceName, (InputStream)null, 204, (InputStream)null, lockTokenHeader);


        source2File = IOUtils.createRandomFile(environment.dir, source2Name, size);
        environment.doMethod("PUT", resourceName, source2File, 204, (InputStream)null, ifTokenHeader);

        Environment.compareFiles(source2File, sourceFile);
    }

}
