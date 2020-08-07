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
package org.abstracthorizon.danube.http.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class UTestUtils {
    
    
    public static File getTempDirectory() throws IOException {
        File path = File.createTempFile("test", "dir");
        path.delete();
        path.mkdir();
        return path;
    }
    
    public static URI addPath(URI uri, String path) throws MalformedURLException {
//        String file = url.getFile();
//        File f = new File(file);
//        f = new File(f, path);
//        file = f.getAbsolutePath();
//        URL resURL = new URL(url.getProtocol(), url.getHost(), url.getPort(), file);
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        uri = uri.resolve(path);
        return uri;
    }
    
    public static URL setPath(URL url, String path) throws MalformedURLException {
        URL resURL = new URL(url.getProtocol(), url.getHost(), url.getPort(), path);
        return resURL;
    }

    public static String compareInAndOut(byte[] inBuffer, byte[] outBuffer) {
        if (inBuffer.length != outBuffer.length) {
            return "Input buffer size does not match output buffer size; inBuffer.length=" + inBuffer.length + " != outBuffer.length=" + outBuffer.length;
        }
        for (int i = 0; i < inBuffer.length; i++) {
            if (inBuffer[i] != outBuffer[i]) {
                return "Byte " + i + " is different in[" + i + "]=" + inBuffer[i] + " != out[" + i + "]=" + outBuffer[i];
            }
        }
        return null;
    }
    


}
