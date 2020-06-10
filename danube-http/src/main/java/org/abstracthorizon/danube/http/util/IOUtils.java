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
package org.abstracthorizon.danube.http.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * IO util methods
 *
 * @author Daniel Sendula
 */
public class IOUtils {

    /**
     * Transfer given channel to the file channel
     * @param inputChannel file channel
     * @param fileChannel output channel
     * @param length length
     * @throws IOException IO exception
     */
    public static void transferToFileChannel(FileChannel fileChannel, ReadableByteChannel inputChannel, long length) throws IOException {
        // TODO this IS wrong. Encoding is needed to be catered for here!!!
        fileChannel.transferFrom(inputChannel, 0, length);
    }

    /**
     * Adds two paths together always ensuring that it starts with a &quot;/&quot; and if path 2 is &quot;/&quot; to end with &quot;/&quot;
     * @param path1 path one
     * @param path2 path two
     * @return new path
     */
    public static String addPaths(String path1, String path2) {
        StringBuffer result = new StringBuffer();
        boolean p1s = false;
        boolean p1e = false;
        boolean p1 = false;
        boolean p2s = false;
        boolean p2 = false;
        if (path1.equals("/")) {
            p1s = true;
            p1e = true;
            p1 = true;
        } else {
            p1s = path1.startsWith("/");
            p1e = path1.endsWith("/");
        }
        if (path2.equals("/")) {
            p2s = true;
            p2 = true;
        } else {
            p2s = path2.startsWith("/");
        }

        if (p1) {
            if (p2) {
                return path1;
            } else if (!p2s) {
                result.append(path1).append(path2);
                return result.toString();
            }
        }
        if (!p1s) {
            result.append('/');
        }
        result.append(path1);
        if (p2) {
            if (p1e) {
                return result.toString();
            } else {
                result.append(path2);
                return result.toString();
            }
        }
        if (p1e && p2s) {
            result.append(path2.substring(1));
            return result.toString();
        } else if (!p1e && !p2s) {
            result.append('/');
        }
        result.append(path2);

        return result.toString();
    }

    public static String lastPathComponent(String path) {
        int i = path.lastIndexOf('/');
        if (i >= 0) {
            return path.substring(i + 1);
        } else {
            return path;
        }
    }

    public static String parentPath(String path) {
        if ((path == null) || (path.length() == 0)) {
            return null;
        }
        int i = path.lastIndexOf('/');
        if (i >= 0) {
            return path.substring(0, i);
        } else {
            return path;
        }
    }

    public static String compareStreams(InputStream s1, InputStream s2) throws IOException {
        long size = 0;
        byte[] buf1 = new byte[1000];
        byte[] buf2 = new byte[1000];
        int r1 = 0;
        while (r1 >= 0) {
            r1 = s1.read(buf1);
            int r2 = s2.read(buf2);
            if (r1 < 0) {
                if (r2 >= 0) {
                    return "Different sizes; s1=" + size + ", s2=" + (r2 + size);
                }
                return null;
            }
            if (r2 < 0) {
                if (r1 >= 0) {
                    return "Different sizes; s1=" + (r1 + size) + ", s2=" + size;
                }
                return null;
            }
            if (r1 != r2) {
                return "Different sizes; s1=" + (r1 + size) + ", s2=" + (r2 + size);
            }
            if (r1 > 0) {
                String r = compareArrays(buf1, buf2, size, r1);
                size = size + r1;
                if (r != null) {
                    return r;
                }
            }
        }
        return null;
    }

    public static String compareStreams(InputStream s1, InputStream s2, long size) throws IOException {
        long loaded = 0;
        byte[] buf1 = new byte[1000];
        byte[] buf2 = new byte[1000];
        int r1 = 0;
        while ((r1 >= 0) && (size > 0)) {
            int s = buf1.length;
            if (s > size) {
                s = (int)size;
            }
            r1 = s1.read(buf1, 0, s);
            int r2 = s2.read(buf2, 0, s);
            if (r1 < 0) {
                if (r2 >= 0) {
                    return "Different sizes; s1=" + loaded + ", s2=" + (r2 + loaded);
                }
                return null;
            }
            if (r2 < 0) {
                if (r1 >= 0) {
                    return "Different sizes; s1=" + (r1 + loaded) + ", s2=" + loaded;
                }
                return null;
            }
            if (r1 != r2) {
                return "Different sizes; s1=" + (r1 + loaded) + ", s2=" + (r2 + loaded);
            }
            if (r1 > 0) {
                String r = compareArrays(buf1, buf2, loaded, r1);
                loaded = loaded + r1;
                if (r != null) {
                    return r;
                }
            }
            size = size - r1;
        }
        return null;
    }

    public static String compareArrays(byte[] buf1, byte[] buf2, long previousSize, int size) {
        for (int i = 0; i < size; i++) {
            if (buf1[i] != buf2[i]) {
                return "Different content at position " + (previousSize + i)
                    + "; buf1[" + (previousSize + i) + "]=" + buf1[i]
                    + " != buf2[" + (previousSize + i) + "]=" + buf2[i];
            }
        }
        return null;
    }

    public static boolean delete(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    boolean b = delete(file);
                    if (!b) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else {
            return dir.delete();
        }
    }

    public static boolean copy(File from, File to, boolean recursive) {
        if (from.isFile()) {
            return copyFile(from, to);
        } else {
            to.mkdir();
            if (recursive) {
                File[] files = from.listFiles();
                if (files != null) {
                    for (File file : files) {
                        File f = new File(to, file.getName());
                        if (!copy(file, f, true)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }

    public static boolean copyFile(File from, File to) {
        try {
            FileInputStream fis = new FileInputStream(from);
            try {
                FileChannel fromChannel = fis.getChannel();
                FileOutputStream fos = new FileOutputStream(to);
                try {
                    FileChannel toChannel = fos.getChannel();
                    toChannel.transferFrom(fromChannel, 0, from.length());
                } finally {
                    fos.close();
                }
            } finally {
                fis.close();
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static File createRandomFile(File path, String name, int size) throws IOException {
        File file = new File(path, name);
        int bufsize = 1000;
        if (bufsize > size) {
            bufsize = size;
        }
        int c = 'A';
        byte[] buf = new byte[bufsize];
        FileOutputStream fos = new FileOutputStream(file);
        try {
            while (size > 0) {
                int s = bufsize;
                if (s > size) {
                    s = size;
                }
                for (int i = 0; i < s; i++) {
                    buf[i] = (byte)c;
                    c = c + 1;
                    if (c > 'Z') {
                        c = 'A';
                    }
                }
                fos.write(buf, 0, size);
                size = size - s;
            }
        } finally {
            fos.close();
        }
        return file;
    }

    public static void copyStreams(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[10240];
        int r = inputStream.read(buffer);
        while (r > 0) {
            outputStream.write(buffer, 0, r);
            r = inputStream.read(buffer);
        }
    }
}
