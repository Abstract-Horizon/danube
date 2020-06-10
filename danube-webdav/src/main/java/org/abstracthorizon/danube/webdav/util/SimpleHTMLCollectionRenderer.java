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
import org.abstracthorizon.danube.http.util.IOUtils;
import org.abstracthorizon.danube.webdav.ResourceAdapter;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple HTML collection renderer. It is similar to other popular products'
 * dir rendering
 *
 * @author Daniel Sendula
 */
public class SimpleHTMLCollectionRenderer implements CollectionHTMLRenderer {

    /** Date format */
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    /** Number of bytes in a kilobyte */
    public static final long KILOBYTE = 1024L;

    /** Number of bytes in a megabyte */
    public static final long MEGABYTE = KILOBYTE*1024L;

    /** Number of bytes in a gigabyte */
    public static final long GIGABYTE = MEGABYTE*1024L;

    /** Number of bytes in a terabyte */
    public static final long TERABYTE = GIGABYTE*1024L;

    /**
     * Renders the html
     * @param httpConnection connection
     * @param adapter resource adapter
     * @param dir resource
     */
    public void render(HTTPConnection httpConnection, ResourceAdapter adapter, Object dir) {
        String uri = httpConnection.getComponentPath();
        if (!uri.endsWith("/")) {
            uri = uri + "/";
        }

        String path = httpConnection.getComponentResourcePath();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        String requestPath = httpConnection.getRequestPath();

        String prefix = requestPath.substring(0, requestPath.length() - path.length() + 1);

        PrintWriter out = (PrintWriter)httpConnection.adapt(PrintWriter.class);
        out.println("<!--");
        out.println("uri = " + uri);
        out.println("path = " + path);
        out.println("requestPath = " + requestPath);
        out.println("prefix = " + prefix);
        out.println("-->");
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        out.println("<head>");
        out.println("  <title>Index of " + path + "</title>");
        out.println("  <style type=\"text/css\">");
        out.println("    img { border: 0; padding: 0 2px; vertical-align: text-bottom; }");
        out.println("    td  { font-family: monospace; padding: 2px 3px; text-align: right; vertical-align: bottom; white-space: pre; }");
        out.println("    td:first-child { text-align: left; padding: 2px 10px 2px 3px; }");
        out.println("    table { border: 0; }");
        out.println("    a.symlink { font-style: italic; }");
        out.println("  </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Index of " + path + "</h1>");
        out.println("<hr/>");
        out.println("<table>");

        String parent = new File(path).getParent();
        if (parent == null) {
            parent = "";
        }
        if (!parent.endsWith("/")) {
            parent = parent + "/";
        }
        if (!path.equals("/")) {
            out.print("<tr><td><a href=\"");
            out.print(IOUtils.addPaths(prefix, parent));
            out.print("\">Up to higher level directory</a></td><td>&nbsp;</td><td colspan=\"3\">&nbsp;</td></tr>");
            out.println();
        }

        Object[] files = adapter.collectionElements(dir);
        if (files != null) {
            for (int i = 0; i < files.length; i++) {

                String p = IOUtils.addPaths(prefix, path);
                p = IOUtils.addPaths(p, adapter.getResourceName(files[i]));

                String link = encode(p);
                String name = encode(adapter.getResourceName(files[i]));
                String len = getLengthAsString(adapter, files[i]);

                String date = "";
                long lastModified = adapter.resourceLastModified(files[i]);
                if (lastModified > 0) {
                    dateFormat.format(new Date(lastModified));
                }

                out.print("<tr><td><a href=\"");
                out.print(link);
                out.print("\">");
                out.print(name);
                out.print("</a></td><td>");
                out.print(len);
                out.print("</td><td>");
                out.print(date);
                out.print("</td></tr>");
                out.println();
            }
        }
        out.println("</table>");
        out.println("<hr/>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Returns file length as a string
     * @param f file
     * @return file length as a string
     */
    protected String getLengthAsString(ResourceAdapter adapter, Object f) {
        if (adapter.isCollection(f)) {
            return "&lt;dir&gt;";
        }
        long l = adapter.resourceLength(f);
        if (l >= 0) {
            if (l < KILOBYTE) {
                return Long.toString(l);
            }
            if (l < MEGABYTE) {
                return Long.toString(l / KILOBYTE) + " KB";
            }
            if (l < GIGABYTE) {
                return Long.toString(l / MEGABYTE) + " MB";
            }
            if (l < TERABYTE) {
                return Long.toString(l / GIGABYTE) + " MB";
            }
            return Long.toString(l / TERABYTE) + " TB";
        } else {
            return "unknown";
        }
    }

    /**
     * URL encodes given string
     * @param string string to be encoded
     * @return encoded string
     */
    protected String encode(String string) {
        return string;
    }
}
