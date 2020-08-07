/*
 * Copyright (c) 2005-2020 Creative Sphere Limited.
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

import org.abstracthorizon.danube.http.BaseReflectionHTTPConnectionHandler;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.HTTPServerConnectionHandler;
import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.support.RuntimeIOException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.activation.FileTypeMap;

/**
 * Connection handler that handles static files (and directories)
 *
 * @author Daniel Sendula
 */
public class ReadOnlyFileConnectionHandler extends BaseReflectionHTTPConnectionHandler {
    // TODO - Handle connection timeouts
    // TODO - Set connection timeouts
    // TODO - Handle different HTTP version requests
    // TODO - Handle HTTP/1.1 more properly
    // TODO - Check what response headers are needed for HTTP/1.0 and HTTP/1.1

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    public static final long KILOBYTE = 1024L;
    public static final long MEGABYTE = KILOBYTE*1024L;
    public static final long GIGABYTE = MEGABYTE*1024L;
    public static final long TERABYTE = GIGABYTE*1024L;

    public static final int DEFAULT_BUFFER_SIZE = 10240;

    /** Path of directory files are going to be displayed from */
    protected File filePath;

    /** Buffer size */
    protected int bufferSize = DEFAULT_BUFFER_SIZE;

    /** File type map */
    protected FileTypeMap fileTypeMap = FileTypeMapUtil.getDefaultFileTypeMap();

    /** Constructor */
    public ReadOnlyFileConnectionHandler() {
    }


    /**
     * Sets path where files are stored
     * @param filePath file path
     */
    public void setFilePath(File filePath) {
        this.filePath = filePath;
    }

    /**
     * Sets path where files are stored
     * @return file path
     */
    public File getFilePath() {
        return filePath;
    }

    /**
     * Returns buffer size
     * @return buffer size
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * Sets buffer size
     * @param bufferSize buffer size
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * Returns file type map that is used with this object
     * @return file type map that is used with this object
     */
    public FileTypeMap getFileTypeMap() {
        return fileTypeMap;
    }

    /**
     * Sets file type map to be used with this object
     * @param fileTypeMap file type map to be used with this object
     */
    public void setFileTypeMap(FileTypeMap fileTypeMap) {
        this.fileTypeMap = fileTypeMap;
    }

    /**
     * This method retruns list of files from give directory or returns a file,
     * using {@link FileTypeMap} to convert file extension to mime type
     * @param connection http connection
     */
    public void methodGET(HTTPConnection httpConnection) {

        File file = getFileHandle(httpConnection);
        if (file.exists()) {
            updateHeadersForFile(httpConnection, file);
            if (file.isDirectory()) {
                handleDirectory(file, httpConnection);
            } else {
                handleFile(file, httpConnection);
            }

        } else {
            returnError(httpConnection, Status.NOT_FOUND);
        }

    }

    /**
     * This method retruns headers for for GET method but no body
     *
     * @param connection http connection
     */
    public void methodHEAD(HTTPConnection httpConnection) {

        File file = getFileHandle(httpConnection);
        if (file.exists()) {
            updateHeadersForFile(httpConnection, file);
            returnSimpleContent(httpConnection, Status.NO_CONTENT, null, null);
        } else {
            returnError(httpConnection, Status.NOT_FOUND);
        }

    }

    /**
     * Updates http headers for given resource
     *
     * @param httpConnection http connection
     * @param file resource
     */
    protected void updateHeadersForFile(HTTPConnection httpConnection, File resource) {
        long lastModified = resource.lastModified();
        Date lastModifiedDate = new Date(lastModified);
        String lastModifiedHeader = HTTPServerConnectionHandler.DATE_FORMAT.format(lastModifiedDate);
        httpConnection.getRequestHeaders().putOnly("Last-Modified", lastModifiedHeader);

    }

    /**
     * Obtains real file system's file from the given connection
     *
     * @param httpConnection connection
     * @return file handle
     */
    protected File getFileHandle(HTTPConnection httpConnection) {
        String path = httpConnection.getComponentResourcePath();

        if (path.length() == 0) {
            path = "/";
        }

        File file = new File(filePath, path);
        file = new File(file.getAbsolutePath()); // TODO Strange workaround...
        return file;
    }

    /**
     * Handles request of directory. It displays simple list (a table) of
     * file names and sizes. Also it renders link to parent directory if
     * not at the top (given URI).
     *
     * @param dir directory
     * @param httpConnection http connection
     * @throws IOException
     */
    protected void handleDirectory(File dir, HTTPConnection httpConnection) {
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

        PrintWriter out = httpConnection.adapt(PrintWriter.class);
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

        String parent = new File(uri).getParent();
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

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {

            String p = IOUtils.addPaths(prefix, path);
            p = IOUtils.addPaths(p, files[i].getName());

            String link = encode(p);
            String name = encode(files[i].getName());
            String len = getLengthAsString(files[i]);

            String date = dateFormat.format(new Date(files[i].lastModified()));

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
        out.println("</table>");
        out.println("<hr/>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Handles file request. It converts file extension to mime type using
     * {@link FileTypeMap}.
     * @param file requested file
     * @param httpConnection http conneciton
     */
    protected void handleFile(File file, HTTPConnection httpConnection) {
        String mimeType = fileTypeMap.getContentType(file);

        httpConnection.getResponseHeaders().putOnly("Content-Type", mimeType);
        long len = file.length();

        httpConnection.getResponseHeaders().putOnly("Content-Length", Long.toString(len));
        OutputStream outputStream = httpConnection.adapt(OutputStream.class);

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                httpConnection.adapt(OutputStream.class).flush();

                Channel outputChannel = httpConnection.adapt(Channel.class);
                if ((outputChannel != null) && (outputChannel instanceof WritableByteChannel)) {
                    FileChannel fileChannel = fileInputStream.getChannel();
                    transferFromFileChannel(fileChannel, (WritableByteChannel)outputChannel, file.length());
                } else {
                    transferStreams(fileInputStream, outputStream, file.length());
                }
            } finally {
                fileInputStream.close();
            }
            httpConnection.adapt(OutputStream.class).flush();
        } catch (IOException ioException) {
            if (httpConnection.isCommited()) {
                // TODO how to handle this?
                throw new RuntimeIOException(ioException);
            } else {
                returnError(httpConnection, Status.NOT_FOUND);
            }
        }
    }
    /**
     * Returns file length as a string
     * @param f file
     * @return file length as a string
     */
    protected String getLengthAsString(File f) {
        if (f.isDirectory()) {
            return "&lt;dir&gt;";
        }
        long l = f.length();
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
    }

    /**
     * URL encodes given string
     * @param string string to be encoded
     * @return encoded string
     */
    protected String encode(String string) {
        return string;
    }

    /**
     * Transfer file channel to given output channel
     * @param inputChannel file channel
     * @param outputChannel output channel
     * @param length length
     * @throws IOException IO exception
     */
    protected void transferFromFileChannel(FileChannel inputChannel, WritableByteChannel outputChannel, long length) throws IOException {
        MappedByteBuffer buffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, 0, length);
        outputChannel.write(buffer);
//        ByteBuffer buffer = obtainBuffer();
//        int bufferSize = buffer.capacity();
//        int l = 0;
//        while (length > 0) {
//            if (length > bufferSize) {
//                l = bufferSize;
//            } else {
//                l = (int)length;
//            }
//            buffer.limit(l);
//            int r = inputChannel.read(buffer);
//            if (r <= 0) {
//                length = 0;
//            } else {
//                buffer.flip();
//                outputChannel.write(buffer);
//                length = length - r;
//            }
//        }
    }

    /**
     * Obtains new buffer
     * @return new buffer
     */
    protected ByteBuffer obtainBuffer() {
        return ByteBuffer.allocateDirect(getBufferSize());
    }

    /**
     * Releases used direct buffer
     * @parma buffer buffer
     *
     */
    protected void releaseBuffer(ByteBuffer buffer) {

    }

    /**
     * Transfer file input stream to output stream
     * @param inputStream file input stream
     * @param outputStream output stream
     * @param length length
     * @throws IOException IO exception
     */
    protected void transferStreams(InputStream inputStream, OutputStream outputStream, long length) throws IOException {
        byte[] buf = new byte[getBufferSize()];

        int l = buf.length;
        if (l > length) {
            l = (int)length;
        }

        l = inputStream.read(buf, 0 , l);
        while ((l > 0) && (length > 0)) {
            length = length - l;
            outputStream.write(buf, 0, l);

            l = buf.length;
            if (l > length) {
                l = (int)length;
            }
            l = inputStream.read(buf, 0, l);
        }
    }
}
