/*
 * Copyright (c) 2005-2007 Creative Sphere Limited.
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

import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.Status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import javax.activation.FileTypeMap;

/**
 * Connection handler that handles static files (and directories)
 *
 * @author Daniel Sendula
 */
public class ReadWriteFileConnectionHandler extends ReadOnlyFileConnectionHandler {
    // TODO - Handle connection timeouts
    // TODO - Set connection timeouts
    // TODO - Handle different HTTP version requests
    // TODO - Handle HTTP/1.1 more properly
    // TODO - Check what response headers are needed for HTTP/1.0 and HTTP/1.1

    /** Constructor */
    public ReadWriteFileConnectionHandler() {
    }

    public void methodDELETE(HTTPConnection httpConnection) {

        File file = getFileHandle(httpConnection);
        if (file.exists()) {
            if (!file.delete()) {
                returnError(httpConnection, Status.METHOD_NOT_ALLOWED);
            } else {
                returnSimpleContent(httpConnection, Status.NO_CONTENT, null, null);
            }
        } else {
            returnError(httpConnection, Status.NOT_FOUND);
        }
    }

    /**
     * This method retruns list of files from give directory or returns a file,
     * using {@link FileTypeMap} to convert file extension to mime type
     * @param httpConnection http connection
     */
    public void methodPUT(HTTPConnection httpConnection) {

        File file = getFileHandle(httpConnection);
        file = new File(file.getAbsolutePath()); // TODO Strange workaround...
        if (file.isDirectory()) {
            returnError(httpConnection, Status.METHOD_NOT_ALLOWED);
        } else {
            if (file.getParentFile().exists()) {
                if (file.isDirectory()) {
                    returnError(httpConnection, Status.CONFLICT);
                } else {
                    boolean oldFile = file.exists();
                    uploadFile(file, httpConnection);
                    if (oldFile) {
                        returnSimpleContent(httpConnection, Status.NO_CONTENT, null, null);
                    } else {
                        returnSimpleContent(httpConnection, Status.CREATED, null, null);
                    }
                }
            } else {
                returnError(httpConnection, Status.NOT_FOUND);
            }
        }
    }

    /**
     * Uploads the file
     * @param file file
     * @param httpConnection http connection
     */
    protected void uploadFile(File file, HTTPConnection httpConnection) {

        long length = 0;
        String contentLength = httpConnection.getRequestHeaders().getOnly("Content-Length");
        if (contentLength == null) {
            returnError(httpConnection, Status.LENGTH_REQUIRED);
        } else {
            length = Long.parseLong(contentLength);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                try {
                    Channel inputChannel = (Channel)httpConnection.adapt(Channel.class);
                    if ((inputChannel != null) && (inputChannel instanceof ReadableByteChannel)) {
                        FileChannel fileChannel = fileOutputStream.getChannel();
                        transferToFileChannel(fileChannel, (ReadableByteChannel)inputChannel, file.length());
                    } else {
                        InputStream inputStream = (InputStream)httpConnection.adapt(InputStream.class);
                        transferStreams(inputStream, fileOutputStream, length);
                    }
                } finally {
                    fileOutputStream.close();
                }
            } catch (IOException ioException) {
                returnError(httpConnection, Status.GONE);
            }
        }

    }

    /**
     * Transfer given channel to the file channel
     * @param inputChannel file channel
     * @param fileChannel output channel
     * @param length length
     * @throws IOException IO exception
     */
    protected void transferToFileChannel(FileChannel fileChannel, ReadableByteChannel inputChannel, long length) throws IOException {
        // TODO this IS wrong. Encoding is needed to be catered for here!!!
        fileChannel.transferFrom(inputChannel, 0, length);
    }
}
