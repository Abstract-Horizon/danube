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
package org.abstracthorizon.danube.http;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.nio.charset.IllegalCharsetNameException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionException;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.connection.ConnectionWrapper;
import org.abstracthorizon.danube.http.util.EncodingPrintWrtier;
import org.abstracthorizon.danube.http.util.IOUtils;
import org.abstracthorizon.danube.http.util.MultiStringHashMap;
import org.abstracthorizon.danube.http.util.MultiStringMap;
import org.abstracthorizon.danube.http.util.StringPrintWriter;
import org.abstracthorizon.danube.support.RuntimeIOException;

/**
 * <p>
 * This connection represents one HTTP request and response.
 * It can be reused over the same underlaying connection
 * (multiple requests over the same socket).
 * </p>
 * <p>
 * This implementation handles HTTP request string, headers and parameters.
 * </p>
 *
 * @author Daniel Sendula
 */
public class HTTPConnectionImpl extends ConnectionWrapper implements HTTPConnection {

    /** Helper array of CR and LF characters */
    protected static final String CRLF = "\r\n";

    /** Cached underlaying connection's input stream */
    protected InputStream cachedInputStream;

    /** Cached underlaying connection's output stream */
    protected OutputStream cachedOutputStream;

    /** Request headers */
    protected MultiStringHashMap requestHeaders = new MultiStringHashMap();

    /**
     * Request parameters. If more then one parameter with the same name
     * is supplied then instead of a {@link String} as a type,
     * a {@link List} is going to be used.
     */
    protected MultiStringHashMap requestParameters;

    /** Request method (GET, POST, etc) */
    protected String requestMethod;

    /** Requested protocol (HTTP/1.0, HTTP/1.1 or null) */
    protected String requestProtocol;

    /** Raw URI request. It contains everything, even get parameters... */
    protected String requestURI;

    /** Similar as raw URI but without parameters */
    protected String requestPath;

    /** Pointer to end of context part of the request path */
    protected int contextPointer = 0;

    /** Pointer to end of component part of the request path */
    protected int componentPointer = 0;

    /** Current path up to the current component */
    protected String contextPath = "/";

    /** Current (processed) requestURI. This URI is only upto parameters */
    protected String componentPath = "/";

    /** Current (processed) requestURI. This URI is only upto parameters */
    protected String componentResourcePath;

    /** Response headers */
    protected MultiStringHashMap responseHeaders = new MultiStringHashMap();

    /** Response status */
    protected Status responseStatus = Status.OK;

    /** Response protocol */
    protected String responseProtocol;

    /** Have headers been commited already. */
    protected boolean headersCommitted;

    /** Writer */
    protected StringPrintWriter writer;

    /** Map of attributes */
    protected Map<String, Object> attributes;

    /** Reference to creator of this handler so forward can work from that point */
    protected ConnectionHandler parent;

    /** Has writer been already returned */
    protected boolean writerReturned = false;

    /** Shell content output be suppressed or not. This is needed for HEAD method. */
    protected boolean suppressOutput = false;

    /** Buffered output */
    protected HTTPBufferedOutputStream bufferedOutput;

    /** Buffered input */
    protected HTTPBufferedInputStream bufferedInput;

    /** Cached print writer */
    protected EncodingPrintWrtier cachedPrintWriter;

    /** Cached buffered reader */
    protected BufferedReader cachedBufferedReader;

    /** Cached readers encoding */
    protected String cachedReadersEncoding;

    /** Default buffer size */
    protected int defaultBufferSize;

    /** Is expectation header handled */
    protected boolean expectationIsHandled;

    /**
     * Constructor.
     *
     * @param connection original connection
     * @param parent parent connection handler needed for forwarding
     * @param defaultBufferSize default buffer size
     */
    public HTTPConnectionImpl(Connection connection, ConnectionHandler parent, int defaultBufferSize) {
        super(connection);
        this.parent = parent;
        cachedInputStream = (InputStream)connection.adapt(InputStream.class);
        cachedOutputStream = (OutputStream)connection.adapt(OutputStream.class);
        bufferedOutput = new HTTPBufferedOutputStream(this, cachedOutputStream, defaultBufferSize);
        this.defaultBufferSize = defaultBufferSize;
    }

    /**
     * Constructor.
     *
     * @param connection original connection
     * @param parent parent connection handler needed for forwarding
     * @param inputStream input stream to be read from
     * @param outputStrema output stream to be written to
     * @param defaultBufferSize default buffer size
     */
    public HTTPConnectionImpl(Connection connection, ConnectionHandler parent, InputStream inputStream, OutputStream outputStream, int defaultBufferSize) {
        super(connection);
        this.parent = parent;
        cachedInputStream = inputStream;
        cachedOutputStream = outputStream;
        bufferedOutput = new HTTPBufferedOutputStream(this, cachedOutputStream, defaultBufferSize);
        this.defaultBufferSize = defaultBufferSize;
    }

    /**
     * This method processes request.
     * It extracts method, uri, parameters (GET or POST),
     * protocol version and headers.
     *
     * @throws IOException
     */
    public void reset() {
        suppressOutput = false;
        headersCommitted = false;
        requestProtocol = null;
        requestMethod = null;
        contextPath = "/";
        componentPath = "/";
        componentResourcePath = null;
        requestURI = null;
        requestPath = null;
        getRequestHeaders().clear();

        bufferedOutput.resetInternals();
        bufferedOutput.setBufferSize(defaultBufferSize);
        if (bufferedInput != null) {
            bufferedInput.resetInternals();
        }
        if (cachedPrintWriter != null) {
            cachedPrintWriter.resetInternals();
        }

        // if (requestParameters != null) {
        //    requestParameters.clear();
        //}
        requestParameters = null;

        responseStatus = Status.OK;
        getResponseHeaders().clear();

        writerReturned = false;

        if (writer != null) {
            writer.reset();
        }

        if (attributes != null) {
            attributes.clear();
        }
        expectationIsHandled = false;
    }

    /**
     * Should content output be suppressed or not.
     *
     * @return is content output suppressed or not
     */
    public boolean isSuppressOutput() {
        return suppressOutput;
    }

    /**
     * Should output be suppressed or not.
     *
     * @param suppressOutput should the output be suppressed or not.
     */
    public void setSuppressOutput(boolean suppressOutput) {
        this.suppressOutput = suppressOutput;
        bufferedOutput.setSupporessOutput(suppressOutput);
    }

    /**
     * Returns buffer size
     *
     * @return buffer size
     */
    public int getBufferSize() {
        return bufferedOutput.getBufferSize();
    }

    /**
     * Sets buffer size
     *
     * @param size buffer size
     */
    public void setBufferSize(int size) {
        if (bufferedInput != null) {
            bufferedInput.setBufferSize(size);
        }
        bufferedOutput.setBufferSize(size);
    }

    /**
     * This method processes request.
     * It extracts method, uri, parameters (GET or POST),
     * protocol version and headers.
     *
     * @throws IOException
     */
    public void processRequest() throws IOException {
        reset();

        parseHttpRequestLine();
        if ("HEAD".equals(requestMethod)) {
            setSuppressOutput(true);
        }
        retrieveHeaders();
        setupRequestPaths();
        // parseGetParameters();
        // if ("POST".equals(getRequestMethod())) {
        //     parsePostParameters();
        // }
        responseProtocol = requestProtocol;
    }

    protected void parseHttpRequestLine() throws IOException {
        String request = readLine();
        if (request == null) {
            throw new EOFException();
        }
        int i = request.indexOf(' ');
        if (i >= 0) {
            requestMethod = request.substring(0, i);
            int j = request.indexOf(' ', i+1);
            if (i >= 0) {
                requestURI = URLDecoder.decode(request.substring(i+1, j), "UTF-8");
                requestProtocol = request.substring(j+1);
            } else {
                requestURI = request.substring(i+1);
            }
        }
    }

    /**
     * Retrieves get parameters
     * @throws IOException
     */
    protected void parseGetParameters() {
        if (requestURI != null) {
            int i = requestURI.indexOf('?');
            if (i > 0) {
                String params = requestURI.substring(i+1);
                try {
                    retrieveParams(requestParameters, new StringReader(params), params.length());
                } catch (IOException ignore) {
                }
            }
        }
    }
    /**
     * Retrieves get parameters
     * @throws IOException
     */
    protected void setupRequestPaths() {
        contextPointer = 0;
        componentPointer = 0;
        contextPath = "/";
        if (requestURI != null) {
            int i = requestURI.indexOf('?');
            if (i > 0) {
                componentResourcePath = requestURI.substring(0, i);
            } else {
                componentResourcePath = requestURI;
            }
            requestPath = componentResourcePath;
        }
    }

    /**
     * Retrieves post parameters
     * @throws IOException
     */
    protected void parsePostParameters() throws IOException {
        MultiStringMap requestHeaders = getRequestHeaders();
        String contentLenString = requestHeaders.getOnly("Content-Length");
        if (contentLenString != null) {
            int len = Integer.parseInt(contentLenString);
            String header = requestHeaders.getOnly("Content-Type");
            if ((header != null) && header.startsWith("application/x-www-form-urlencoded")) {
                String encoding = null;
                try {
                    MimeType mimeType = new MimeType(header);
                    encoding = mimeType.getParameter("charset");
                } catch (MimeTypeParseException ignore) {
                }

                if (encoding == null) {
                    retrieveParams(requestParameters, new InputStreamReader(getContentInputStream()), len);
                } else {
                    retrieveParams(requestParameters, new InputStreamReader(getContentInputStream(), encoding), len);
                }
            }
        }
    }

    /**
     * Retrieves headers
     * @throws IOException
     */
    protected void retrieveHeaders() throws IOException {
        MultiStringMap requestHeaders = getRequestHeaders();
        String line = readLine();
        while ((line != null) && !"".equals(line)) {
            int i = line.indexOf(':');
            if (i >= 0) {
                String header = line.substring(0, i);
                String value = line.substring(i+2);
                requestHeaders.add(header, value);
            }

            line = readLine();
        }
        if (bufferedInput != null) {
            updateInputStreamLen();
        }
    }

    /**
     * This method extracts parameters from givem reader.
     * @param params parameters map
     * @param r reader
     * @param len number of chars to be read from reader
     * @throws IOException
     */
    protected static void retrieveParams(MultiStringMap params, Reader r, int len) throws IOException {
        // TODO optimise this for speed and resources...
        // TODO URLDecode!
        StringBuffer name = new StringBuffer();
        StringBuffer value = new StringBuffer();
        boolean retrieveName = true;
        int i = 0;
        while ((len > 0) && (i >= 0)) {
            i = r.read();
            len = len - 1;
            if (i != -1) {
                char c = (char)i;
                if (c == '&') {
                    if (!retrieveName) {
                        addParam(params, name.toString(), value.toString());
                        name = new StringBuffer();
                        value = new StringBuffer();
                        retrieveName = true;
                    } else {
                        // ERROR
                    }
                } else if (retrieveName && (c == '=')) {
                    retrieveName = false;
                } else if (retrieveName) {
                    name.append(c);
                } else {
                    value.append(c);
                }
            }
            if ((i == -1) || (len == 0)) {
                if (!retrieveName) {
                    addParam(params, name.toString(), value.toString());
                } else {
                    // ERROR
                }
            }
        }
    }

    /**
     * Adds parameter to the map. If parameter already exists and is
     * of {@link String} type then it is replaced with a {@link List}
     * and then old and new parameter stored under it.
     * @param params parameter map
     * @param name name of parameter
     * @param value parameter's value
     */
    public static void addParam(MultiStringMap params, String name, String value) {
        try {
            name = URLDecoder.decode(name, "UTF-8");
            value = URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }

        params.add(name, value);
    }

    /**
     * Utility method that reads a line from input stream
     * @return line string or <code>null</code> if <code>EOF</code> is reached
     * @throws IOException
     */
    public String readLine() throws IOException {
        // TODO - this blocks socket and thread. It needs to have a timeout!!!
        InputStream in = cachedInputStream;
        StringBuffer result = new StringBuffer();
        int r = in.read();
        if (r < 0) {
            return null;
        }
        while ((r >= 0) && (r != '\n')) {
            if (r >= ' ') {
                result.append((char)r);
            }
            r = in.read();
        }
        return result.toString();
    }


    /**
     * Returns request headers map
     * @return request headers map
     */
    public MultiStringMap getRequestHeaders() {
        if (requestHeaders == null) {
            createRequestHeaders();
        }
        return requestHeaders;
    }

    protected void createRequestHeaders() {
        requestHeaders = new MultiStringHashMap();
    }

    /**
     * Returns request parameters map. If more
     * then one parameter is supplied with the same name
     * then {@link List} returned with all parameter
     * values in it.
     * @return request parameters map
     */
    public MultiStringMap getRequestParameters() {
        if (requestParameters == null) {
            requestParameters = new MultiStringHashMap();
            parseGetParameters();
            if ("POST".equalsIgnoreCase(getRequestMethod())) {
                try {
                    parsePostParameters();
                } catch (IOException e) {
                    throw new ConnectionException(e);
                }
            }
        }
        return requestParameters;
    }

    /**
     * Returns request method
     * @return request method
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * Returns request protocol
     * @return request protocol
     */
    public String getRequestProtocol() {
        return requestProtocol;
    }

    /**
     * Returns portion of request path up to component path
     * @return portion of request path up to component path
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * Updates context path adding new path element to it
     */
    public void addComponentPathToContextPath() {
//        contextPath = requestPath.substring(0, componentPointer);
//        contextPointer = componentPointer;
//        componentPath = "/";

        contextPath = IOUtils.addPaths(contextPath, componentPath);

//        if (contextPath.endsWith("/")) {
//            if (subpath.startsWith("/")) {
//                contextPath = contextPath + subpath.substring(1);
//            } else {
//                contextPath = contextPath + subpath;
//            }
//        } else {
//            if (subpath.startsWith("/")) {
//                contextPath = contextPath + subpath;
//            } else {
//                contextPath = contextPath + "/" + subpath;
//            }
//
//        }
    }

    /**
     * Returns request uri
     * @return request uri
     */
    public String getComponentPath() {
        return componentPath;
    }

    /**
     * Sets request uri.
     * This is to be called from selectors not applicaiton code.
     *
     * @param requestURI
     */
    public void setComponentPath(String requestURI) {
        this.componentPath = requestURI;
    }

    /**
     * Returns remainder of path after context path and component path is removed
     * @return remainder of path after context path and component path is removed
     */
    public String getComponentResourcePath() {
        return componentResourcePath;
    }


    /**
     * Sets component resource path
     * @param resourcePath component resource path
     */
    public void setComponentResourcePath(String resourcePath) {
        this.componentResourcePath = resourcePath;
    }

    /**
     * This is similar to {@link #getRequestURI()} but without parameters part
     * @return full (unchanged) uri
     */
    public String getRequestPath() {
        return requestPath;
    }

    /**
     * Returns raw requested uri along with all parameters if supplied
     * (GET method)
     * @return raw requested uri
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * Returns response headers map
     * @return response headers map
     */
    public MultiStringMap getResponseHeaders() {
        if (responseHeaders == null) {
            createResponseHeaders();
        }
        return responseHeaders;
    }

    protected void createResponseHeaders() {
        responseHeaders = new MultiStringHashMap();
    }

    /**
     * Returns response status
     * @return response status
     */
    public Status getResponseStatus() {
        return responseStatus;
    }

    /**
     * Sets response status
     * @param status response status
     */
    public void setResponseStatus(Status status) {
        this.responseStatus = status;
    }

    /**
     * Returns response protocol
     * @return response protocol
     */
    public String getResponseProtocol() {
        return responseProtocol;
    }

    /**
     * Sets response protocol
     * @param protocol response protocol
     */
    public void setResponseProtocol(String protocol) {
        this.responseProtocol = protocol;
    }

    /**
     * Returns <code>true</code> if headers are already send back to the client
     * @return <code>true</code> if headers are already send back to the client
     */
    public boolean isCommited() {
        return headersCommitted;
    }

    /**
     * This method output response string and headers
     * @throws IOException
     */
    public void commitHeaders() {
        if (!expectationIsHandled) {
            handleExpectationHeader();
        }
        if (!headersCommitted) {
            try {
                if (requestProtocol.equals("HTTP/1.0")) {
                    if (!responseProtocol.equals(requestMethod)) {
                        responseProtocol = requestMethod;
                    }
                }

                MultiStringMap responseHeaders = getResponseHeaders();

                String contentLength = responseHeaders.getOnly("Content-Length");
                String responseStatusCode = responseStatus.getCode();

                if (responseStatusCode.startsWith("1") || responseStatusCode.equals("204") || responseStatusCode.equals("304")) {
                    responseHeaders.putOnly("Content-Length", "0");
                    responseHeaders.removeAll("Transfer-Encoding");
                    bufferedOutput.setLimitedContentLength(0);
                } else if (responseStatusCode.equals("200") && responseHeaders.containsKey("Content-Range")) {
                    responseStatus = Status.PARTIAL_CONTENT;
                } else if ("HEAD".equals(requestMethod)) {
                    // TODO check this!!!
                    bufferedOutput.setLimitedContentLength(0);
                } else {

                    int len = -1;
                    if (contentLength != null) {
                        try {
                            len = Integer.parseInt(contentLength);
                        } catch (NumberFormatException e) {
                            responseHeaders.removeAll("Content-Length");
                            len = -1;
                        }
                    }
                    bufferedOutput.setLimitedContentLength(len);
                    boolean chunkedEncoding = (len < 0) && "HTTP/1.1".equals(responseProtocol);
                    bufferedOutput.setChunkEncoding(chunkedEncoding);
                    if (chunkedEncoding) {
                        responseHeaders.removeAll("Content-Length");
                        responseHeaders.putOnly("Transfer-Encoding", "chunked");
                    }
                }

                StringBuffer headersBuffer = new StringBuffer(1024);
                headersBuffer.append(responseProtocol).append(' ').append(responseStatus.getFullStatus()).append(CRLF);

                Iterator<String> it = responseHeaders.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    String[] headers = responseHeaders.getAsArray(key);
                    for (String header : headers) {
                        headersBuffer.append(key).append(": ").append(header).append(CRLF);
                    }
                }
                headersBuffer.append(CRLF);
                cachedOutputStream.write(headersBuffer.toString().getBytes());
                cachedOutputStream.flush();
                headersCommitted = true;
            } catch (IOException ioException) {
                throw new RuntimeIOException(ioException);
            }
        }
    }

    /**
     * Returns output stream but creates and commits headers before.
     * @return output stream obtained from {@link Connection#getOutputStream}
     */
    public HTTPBufferedOutputStream getContentOutputStream() {
        return bufferedOutput;
    }

    /**
     * Returns content input stream
     * @return content input stream
     */
    public HTTPBufferedInputStream getContentInputStream() {
        if (!expectationIsHandled) {
            handleExpectationHeader();
        }
        if (bufferedInput == null) {
            bufferedInput = new HTTPBufferedInputStream(cachedInputStream, defaultBufferSize);
            updateInputStreamLen();
        }

        return bufferedInput;
    }

    protected void handleExpectationHeader() {
        if (!expectationIsHandled) {
            MultiStringMap requestHeaders = getRequestHeaders();
            String expect = requestHeaders.getOnly("Expect");
            if ((expect != null) && ("100-continue".equals(expect))) {
                String code = responseStatus.getCode();
                StringBuffer headersBuffer = new StringBuffer(1024);
                if (code.startsWith("2")) {
                    headersBuffer.append(responseProtocol).append(' ').append(Status.CONTINUE.getFullStatus()).append(CRLF);
                    // send continue
                } else {
                    headersBuffer.append(responseProtocol).append(' ').append(Status.EXPECTATION_FAILED.getFullStatus()).append(CRLF);
                    headersCommitted = true;
                }
                // TODO check this CRLF
                headersBuffer.append(CRLF);
                try {
                    cachedOutputStream.write(headersBuffer.toString().getBytes());
                    cachedOutputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeIOException(e);
                }
            }
            expectationIsHandled = true;
        }
    }

    /**
     * Updates content input stream's length or chunked encoding
     *
     */
    protected void updateInputStreamLen() {
        MultiStringMap requestHeaders = getRequestHeaders();

        boolean chunkedEncoding = "chunked".equals(requestHeaders.getOnly("Transfer-Encoding"));
        bufferedInput.setChunkEncoding(chunkedEncoding);
        if (!chunkedEncoding) {
            long len = -1;
            String contentLength = requestHeaders.getOnly("Content-Length");
            if (contentLength != null) {
                try {
                    len = Long.parseLong(contentLength);
                } catch (NumberFormatException ignore) {
                }
            }
            if (len >= 0) {
                bufferedInput.setContentLength(len);
            }
        }
    }

    /**
     * Returns writer. If writer is not created yet it will be created on the fly.
     * @return writer
     * @throws RuntimeIOException
     */
    public PrintWriter getContentWriter() throws RuntimeIOException {
        String encoding = getOutputEncoding();
        if (cachedPrintWriter == null) {
            try {
                cachedPrintWriter = new EncodingPrintWrtier(getContentOutputStream(), encoding);
            } catch (IllegalCharsetNameException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeIOException(e);
            }
        } else {
            try {
                cachedPrintWriter.setEncoding(encoding);
            } catch (IllegalCharsetNameException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeIOException(e);
            }
        }
        return cachedPrintWriter;
    }

    /**
     * Retuns response encoding
     * @return response encoding
     */
    protected String getOutputEncoding() {
        return null;
    }

    /**
     * Returns content reader
     * @return content reader
     */
    public BufferedReader getContentReader() {
        String encoding = getInputEncoding();
        if ((encoding != cachedReadersEncoding)
                && ((encoding == null) || !encoding.equals(cachedReadersEncoding))) {
            cachedBufferedReader = null;
        }
        if (cachedBufferedReader == null) {
            if (encoding != null) {
                try {
                    cachedBufferedReader = new BufferedReader(new InputStreamReader(getContentInputStream(), encoding));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeIOException(e);
                }
            } else {
                cachedBufferedReader = new BufferedReader(new InputStreamReader(getContentInputStream()));
            }
            cachedReadersEncoding = encoding;
        }
        return cachedBufferedReader;
    }

    /**
     * Returns request encoding
     * @return request encoding
     */
    protected String getInputEncoding() {
        return null;
    }

    /**
     * Returns map of attributes. This method performs lazy instantition of the map.
     *
     * @return map of attribtues
     */
    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }
        return attributes;
    }

    /**
     * Redirects request
     * @param uri uri to be redirected to
     */
    public void forward(String uri) {
        // TODO check if url is local and if is not then perform client redirection
        // TODO check if all connection parameters are set correctly (paths, uri, etc..)

        requestURI = uri;

        componentResourcePath = null;
        contextPath = "/";
        componentPath = "/";
        parseGetParameters();

        parent.handleConnection(this);
    }

    /**
     * Adapts this class to {@link HTTPConnection}
     * @param cls class
     * @return adapter
     */
    @SuppressWarnings("unchecked")
    public <T> T adapt(Class<T> cls) {
        if (cls == HTTPConnection.class) {
            return (T)this;
        } else if ((cls == OutputStream.class) || (cls == HTTPBufferedOutputStream.class)) {
            return (T)getContentOutputStream();
        } else if ((cls == PrintWriter.class) || (cls == Writer.class) || (cls == EncodingPrintWrtier.class)) {
            return (T)getContentWriter();
        } else if ((cls == InputStream.class) || (cls == HTTPBufferedInputStream.class)) {
            return (T)getContentInputStream();
        } else if ((cls == Reader.class) || (cls == BufferedReader.class)) {
            return (T)getContentReader();
        } else {
            return super.adapt(cls);
        }
    }

}
