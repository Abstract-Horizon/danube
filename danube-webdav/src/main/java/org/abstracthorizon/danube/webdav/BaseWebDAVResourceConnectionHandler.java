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

import org.abstracthorizon.danube.adapter.Adaptable;
import org.abstracthorizon.danube.http.BaseReflectionHTTPConnectionHandler;
import org.abstracthorizon.danube.http.HTTPBufferedInputStream;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.http.util.FileTypeMapUtil;
import org.abstracthorizon.danube.http.util.IOUtils;
import org.abstracthorizon.danube.http.util.Ranges;
import org.abstracthorizon.danube.support.RuntimeIOException;
import org.abstracthorizon.danube.support.logging.LoggingConnection;
import org.abstracthorizon.danube.webdav.lock.Lock;
import org.abstracthorizon.danube.webdav.lock.LockingMechanism;
import org.abstracthorizon.danube.webdav.util.CollectionHTMLRenderer;
import org.abstracthorizon.danube.webdav.util.Depth;
import org.abstracthorizon.danube.webdav.util.IF;
import org.abstracthorizon.danube.webdav.util.SimpleHTMLCollectionRenderer;
import org.abstracthorizon.danube.webdav.util.Timeout;
import org.abstracthorizon.danube.webdav.util.URIUtils;
import org.abstracthorizon.danube.webdav.xml.WebDAVXMLHandler;
import org.abstracthorizon.danube.webdav.xml.dav.RequestProp;
import org.abstracthorizon.danube.webdav.xml.dav.request.LockInfo;
import org.abstracthorizon.danube.webdav.xml.dav.request.PropFind;
import org.abstracthorizon.danube.webdav.xml.dav.request.PropertyBehavior;
import org.abstracthorizon.danube.webdav.xml.dav.request.PropertyUpdate;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.RequestProperty;
import org.abstracthorizon.danube.webdav.xml.dav.response.MultiStatus;
import org.abstracthorizon.danube.webdav.xml.dav.response.Propstat;
import org.abstracthorizon.danube.webdav.xml.dav.response.Response;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.LockDiscovery;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.activation.FileTypeMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Base WebDAV resource connection handler. This class uses supplied {@link ResourceAdapter}
 * to read and write resource to/from.
 *
 * @author Daniel Sendula
 */
public class BaseWebDAVResourceConnectionHandler extends BaseReflectionHTTPConnectionHandler {

    /** Status 423 Locked */
    public static final Status STATUS_LOCKED = new Status("423", "Locked");

    /** Status 424 Failed Dependency */
    public static final Status STATUS_FAILED_DEPENDENCY = new Status("424", "Failed Dependency");

    /** Resource adapter to be used */
    protected ResourceAdapter adapter;

    /** Internal buffer size */
    protected int bufferSize = 2048;

    /** Renderer of collection of items if GET method is invoked on a collection resource */
    protected CollectionHTMLRenderer collectionRenderer = new SimpleHTMLCollectionRenderer();

    /** Flag denoting will this class allow resources to be changed or not */
    protected boolean readOnly = false;

    /** File type map */
    protected FileTypeMap fileTypeMap = FileTypeMapUtil.getDefaultFileTypeMap();

    /**
     * Constructor
     */
    public BaseWebDAVResourceConnectionHandler() {
    }

    /**
     * Constructor
     *
     * @param webDAVAdapter web dav adapter
     */
    public BaseWebDAVResourceConnectionHandler(ResourceAdapter webDAVAdapter) {
        setWebDAVResourceAdapter(webDAVAdapter);
    }

    /**
     * Returns WebDAV resource adapter
     *
     * @return WebDAV resource
     */
    public ResourceAdapter getWebDAVResourceAdapter() {
        return adapter;
    }

    /**
     * Sets WebDAV resource adapter
     *
     * @param webDAVAdapter WebDAV resource
     */
    public void setWebDAVResourceAdapter(ResourceAdapter webDAVAdapter) {
        this.adapter = webDAVAdapter;
    }

    /**
     * Returns collection HTML renderer
     * @return collection HTML renderer
     */
    public CollectionHTMLRenderer getCollectionHTMLRenderer() {
        return collectionRenderer;
    }

    /**
     * Sets collection HTML renderer
     * @param collectionHTMLRenderer collection HTML renderer
     */
    public void setCollectionHTMLRenterer(CollectionHTMLRenderer collectionHTMLRenderer) {
        this.collectionRenderer = collectionHTMLRenderer;
    }

    /**
     * Returns read only flag
     * @return read only flag
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets read only flag
     * @param readOnly read only flag
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
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
     * Returns a resource. This implementation obtains resource from the supplied
     * adapter using component resource path as a resource's path.
     *
     * This method can be overriden if adapter is to cache resources or pre-process them
     * in any way.
     *
     * @param httpConnection http connection
     * @return resource
     */
    protected Object findResource(HTTPConnection httpConnection) {
        return adapter.findResource(httpConnection.getComponentResourcePath());
    }

    /**
     * Caches methods for quick invocation. This implementation
     * removes LOCK and UNLOCK method from the cache if adapter doesn't supply
     * locking mechanism (lokcing mechanism is <code>null</code>).
     *
     */
    @Override
    protected void cacheMethods() {
        super.cacheMethods();
        if (adapter.getLockingMechanism() == null) {
            cachedMethods.remove("LOCK");
            cachedMethods.remove("UNLOCK");
        }
    }

    /**
     * This method adds DAV: 1,2 header.
     * @param httpConnection connection
     */
    @Override
    public void methodOPTIONS(HTTPConnection httpConnection) {
        super.methodOPTIONS(httpConnection);
        httpConnection.getResponseHeaders().putOnly("DAV", "1,2");
    }

    /**
     * GET method implementation
     *
     * @param httpConnection connection
     */
    public void methodGET(HTTPConnection httpConnection) {
        Object resource = findResource(httpConnection);
        if (adapter.exists(resource)) {
            Ranges ranges = collectRange(httpConnection);
            if (adapter.isCollection(resource)) {
                if (ranges != null) {
                    httpConnection.setResponseStatus(Status.UNSUPPORTED_MEDIA_TYPE);
                } else {
                    collectionRenderer.render(httpConnection, adapter, resource);
                }
            } else {
                transmitResource(httpConnection, resource, httpConnection, ranges);
            }
        } else {
            httpConnection.setResponseStatus(Status.NOT_FOUND);
        }
    }

    /**
     * Transmits resource.
     *
     * @param httpConnection connection
     * @param resource resource to be used
     * @param output adaptable to be checked for the output stream
     * @param ranges ranges. Can be <code>null</code>.
     */
    protected void transmitResource(HTTPConnection httpConnection, Object resource, Adaptable output, Ranges ranges) {
        String resourceName = adapter.getResourceName(resource);
        String mimeType = fileTypeMap.getContentType(resourceName);

        httpConnection.getResponseHeaders().putOnly("Content-Type", mimeType);

        long size = adapter.resourceLength(resource);
        if ((ranges != null) && (size >= 0)) {
            ranges.setSize(size);
        }

        InputStream in = null;

        if (ranges == null) {
            try {
                in = adapter.getInputStream(resource);
            } catch (IOException ignore) {
                httpConnection.setResponseStatus(Status.FORBIDDEN);
                return;
            }
        } else if (!ranges.isMultiRange()) {
            long from = ranges.getSingleRange().getFrom();
            long to = ranges.getSingleRange().getTo();

            if ((from < 0) || (to >= size)) {
                httpConnection.setResponseStatus(Status.RANGE_NOT_SATISFIABLE);
            } else {
                size = to - from + 1;
                try {
                    in = adapter.getInpusStream(resource, from, size);
                } catch (IOException e) {
                    httpConnection.setResponseStatus(Status.FORBIDDEN);
                    return;
                }
            }
        } else {
            // Multirange is not supported for upload
            httpConnection.setResponseStatus(Status.UNSUPPORTED_MEDIA_TYPE);
            return;
        }

        if (in != null) {
            try {
                boolean oldLoggingState = false;
                LoggingConnection loggingConnection = httpConnection.adapt(LoggingConnection.class);
                if (loggingConnection != null) {
                    oldLoggingState = loggingConnection.isLogging();
                    loggingConnection.setLogging(false);
                }
                try {
                    OutputStream out = output.adapt(OutputStream.class);

                    int bufSize = bufferSize;
                    if ((size > 0) && (bufSize > size)) {
                        bufSize = (int)size;
                    }
                    byte[] buffer = new byte[bufSize];
                    boolean ok = (size != 0);

                    while (ok) {
                        int r = bufSize;
                        if ((size > 0) && (r > size)) {
                            r = (int)size;
                        }
                        r = in.read(buffer, 0, r);
                        if (r <= 0) {
                            ok = false;
                        } else {
                            out.write(buffer, 0, r);
                            if (size >= 0) {
                                size = size - r;
                                if (size <= 0) {
                                    ok = false;
                                }
                            }
                        }
                    }
                } finally {
                    if (loggingConnection != null) {
                        loggingConnection.setLogging(oldLoggingState);
                    }
                    in.close();
                }
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
            if (ranges != null) {
                httpConnection.getResponseHeaders().putOnly("Content-Range", ranges.format());
            }
        }
    }

    /**
     * Implementation of the HEAD method.
     *
     * @param httpConnection connection
     */
    public void methodHEAD(HTTPConnection httpConnection) {
        Object resource = findResource(httpConnection);
        if (adapter.exists(resource)) {
            Ranges ranges = collectRange(httpConnection);
            long size = adapter.resourceLength(resource);
            if (ranges != null) {
                ranges.setSize(size);
                // TODO what now?!
                httpConnection.getResponseHeaders().putOnly("Content-Range", ranges.format());
            } else {
                httpConnection.getResponseHeaders().putOnly("Content-Length", Long.toString(size));
            }
        } else {
            httpConnection.setResponseStatus(Status.NOT_FOUND);
        }
    }

    /**
     * Implements PUT method
     *
     * @param httpConnection connection
     */
    public void methodPUT(HTTPConnection httpConnection) {
        if (readOnly) {
            httpConnection.setResponseStatus(Status.FORBIDDEN);
            return;
        }

        Object resource = findResource(httpConnection);
        Object parentResource = adapter.findParentResource(resource);

        boolean oldFile = adapter.exists(resource);
        if (oldFile && adapter.isCollection(resource)) {
            httpConnection.setResponseStatus(Status.CONFLICT);
            return;
        } else if ((parentResource != null) && !adapter.exists(parentResource)) {
            httpConnection.setResponseStatus(Status.CONFLICT);
            return;
        }

        boolean resourceLocked = false;
        boolean parentLocked = false;
        IF lockDetails = null;
        LockingMechanism lockingMechanism = adapter.getLockingMechanism();
        if (lockingMechanism != null) {
            lockDetails = new IF();
            resourceLocked = lockingMechanism.isLocked(resource);
            if (parentResource != null) {
                parentLocked = lockingMechanism.isLocked(parentResource);
            }

            boolean ok;
            if (parentLocked) {
                ok = lockDetails.parse(httpConnection, adapter, resource, parentResource);
            } else {
                ok = lockDetails.parse(httpConnection, adapter, resource, null);
            }
            if (!ok) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            }

//            if (!lockDetails.parse(httpConnection, adapter, resource, parentResource)) {
//                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
//                return;
//            }

            if ((lockDetails.token == null) && resourceLocked) {
                // TODO
                // This is strange: litmus test expect to allow PUT on locked resource
                // where only parent's tagged if is supplied with appropriate lock token
                // To satisfy it we assumed that if parent token is supplied and is valid
                // on the resource then we will use it.
                if ((lockDetails.parentToken == null) || !lockingMechanism.isAccessAllowed(resource, lockDetails.parentToken)) {
                    httpConnection.setResponseStatus(STATUS_LOCKED);
                    return;
                }
            } else if ((lockDetails.token != null) && !resourceLocked) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            }
        }

        String contentRangeHeader = httpConnection.getRequestHeaders().getOnly("Content-Range");
        Ranges ranges = null;
        if (contentRangeHeader != null) {
            ranges = Ranges.parseContentRange(contentRangeHeader);
        }

        OutputStream out = null;
        if (ranges == null) {
            try {
                out = adapter.getOutputStream(resource);
            } catch (IOException e) {
                httpConnection.setResponseStatus(Status.FORBIDDEN);
                return;
            }
            if (!resourceLocked && parentLocked) {
                Lock lock = lockingMechanism.findLock(lockDetails.parentToken);
                lockingMechanism.lockResource(lock, resource);
            }
        } else if (!ranges.isMultiRange()) {
            if (ranges.getSize() >= 0) {

                long from = ranges.getSingleRange().getFrom();
                long to = ranges.getSingleRange().getTo();
                long size = ranges.getSize();
                if ((from < 0) || (to >= size)) {
                    httpConnection.setResponseStatus(Status.RANGE_NOT_SATISFIABLE);
                } else {
                    try {
                        out = adapter.getOutputStream(resource, from, to - from);
                    } catch (IOException e) {
                        httpConnection.setResponseStatus(Status.FORBIDDEN);
                        return;
                    }
                    if (!resourceLocked && parentLocked) {
                        Lock lock = lockingMechanism.findLock(lockDetails.parentToken);
                        lockingMechanism.lockResource(lock, resource);
                    }
                }
            } else {
                httpConnection.setResponseStatus(Status.NOT_IMPLEMENTED);
                return;
            }
        } else {
            // TODO not sure how this can happen!!!
            httpConnection.setResponseStatus(Status.NOT_IMPLEMENTED);
            return;
        }

        if (out != null) {
            try {
                boolean oldLoggingState = false;
                LoggingConnection loggingConnection = httpConnection.adapt(LoggingConnection.class);
                if (loggingConnection != null) {
                    oldLoggingState = loggingConnection.isLogging();
                    loggingConnection.setLogging(false);
                }
                try {
                    InputStream in = httpConnection.adapt(InputStream.class);

                    int bufSize = bufferSize;
                    // if (bufSize > size) {
                    //    bufSize = (int)size;
                    // }
                    byte[] buffer = new byte[bufSize];
                    int r = 0;
                    while (r >= 0) {
                        r = bufSize;

                        r = in.read(buffer, 0, r);
                        if (r > 0) {
                            out.write(buffer, 0, r);
                        }
                    }
                } finally {
                    if (loggingConnection != null) {
                        loggingConnection.setLogging(oldLoggingState);
                    }
                    out.close();
                }
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
            if (oldFile) {
                httpConnection.setResponseStatus(Status.NO_CONTENT);
            } else {
                httpConnection.setResponseStatus(Status.CREATED);
            }
        }
    }

    /**
     *
     * <p>Following is hard to be maintained and is not followed stictly in this
     * implementation:
     * <p>
     * <p><i>&quot;
     *   If any resource identified by a member URI cannot be deleted then all
     *    of the member's ancestors MUST NOT be deleted, so as to maintain
     *    namespace consistency.
     * &quot;</i></p>
     * <p>Delete will stop at the first problem but deleted resources
     * will remain deleted.
     * </p>
     *
     * @param httpConnection
     */
    public void methodDELETE(HTTPConnection httpConnection) {
        if (readOnly) {
            httpConnection.setResponseStatus(Status.FORBIDDEN);
            return;
        }

        Object resource = findResource(httpConnection);
        Object parentResource = adapter.findParentResource(resource);

        int depth = Depth.collectDepth(httpConnection);
        if ((depth != Depth.INFINITY) && (depth != Depth.NONE)) {
            // depth must be infinity
            httpConnection.setResponseStatus(Status.BAD_REQUEST);
            return;
        }

        if (!adapter.exists(resource)) {
            httpConnection.setResponseStatus(Status.NOT_FOUND);
            return;
        }

        MultiStatus multiStatus = new MultiStatus(adapter.getNamespacesProvider(), httpConnection);

        boolean resourceLocked = false;
        boolean parentLocked = false;
        LockingMechanism lockingMechanism = adapter.getLockingMechanism();
        if (lockingMechanism != null) {
            IF lockDetails = new IF();
            resourceLocked = lockingMechanism.isLocked(resource);
            if (parentResource != null) {
                parentLocked = lockingMechanism.isLocked(parentResource);
            }
            boolean ok;
            if (parentLocked) {
                ok = lockDetails.parse(httpConnection, adapter, resource, parentResource);
            } else {
                ok = lockDetails.parse(httpConnection, adapter, resource, null);
            }
            if (!ok) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            }

            if ((lockDetails.token == null) && resourceLocked) {
                httpConnection.setResponseStatus(STATUS_LOCKED);
                return;
            } else if ((lockDetails.token != null) && !resourceLocked) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            } else if ((lockDetails.token != null) && adapter.isCollection(resource)) {
                if (isLockedRecursive(lockingMechanism, lockDetails, multiStatus, httpConnection, resource, adapter.getResourceName(resource))) {
                    multiStatus.render();
                }
            }
        }

        if (deleteRecursive(multiStatus, "/", resource)) {
            httpConnection.setResponseStatus(Status.NO_CONTENT);
        } else {
            multiStatus.render();
        }
    }

    /**
     * Deletes resouces recursively
     *
     * @param multiStatus multi status response
     * @param path current path
     * @param resource resource
     * @return <code>true</code> if successful
     */
    protected boolean deleteRecursive(MultiStatus multiStatus, String path, Object resource) {
        boolean ok = true;
        if (adapter.isCollection(resource)) {
            String p = IOUtils.addPaths(path, adapter.getResourceName(resource));
            Object[] resources = adapter.collectionElements(resource);
            for (Object r : resources) {
                ok = ok && deleteRecursive(multiStatus, p, r);
            }
        }
        ok = ok && deleteLeafImpl(multiStatus, path, resource);
        return ok;
    }

    /**
     * Deletes a leaf (non-collection resource)
     *
     * @param multiStatus multi status response
     * @param path current path
     * @param resource resource
     * @return <code>true</code> if successful
     */
    protected boolean deleteLeafImpl(MultiStatus multiStatus, String path, Object resource) {
        Response response = multiStatus.newResponse(path);
        try {
            adapter.delete(resource);
            response.setStatus(Status.OK);
            return true;
        } catch (IOException e) {
            response.setResponseDescription(e.getMessage());
            response.setStatus(Status.FORBIDDEN);
            return false;
        }
    }


    /**
     * Implements PROPFIND method
     *
     * @param httpConnection http connection
     */
    public void methodPROPFIND(HTTPConnection httpConnection) {
        Object resource = findResource(httpConnection);
        if (adapter.exists(resource)) {
            int depth = Depth.collectDepth(httpConnection);
            MultiStatus multiStatus = new MultiStatus(adapter.getNamespacesProvider(), httpConnection);
            PropFind propFind;
            try {
                propFind = (PropFind)readRequestXML(httpConnection, PropFind.class);
            } catch (SAXException e) {
                httpConnection.setResponseStatus(Status.BAD_REQUEST);
                return;
            }

            if (propFind == null) {
                // TODO very messy...
                propFind = new PropFind(null, null);
                propFind.setAllprop(true);
            }

            if (adapter.exists(resource)) {
                Response response = multiStatus.newResponse();
                obtainProps(response, resource, propFind);
                if ((depth != Depth.ZERO) && adapter.isCollection(resource)) {
                    Object[] resources = adapter.collectionElements(resource);
                    if (resources != null) {
                        for (int i = 0; i < resources.length; i++) {
                            if (depth == Depth.INFINITY) {
                                String path = adapter.getResourceName(resources[i]);
                                obtainPropsRecursive(multiStatus, resource, propFind, path);
                            } else {
                                response = multiStatus.newResponse(adapter.getResourceName(resources[i]));
                                obtainProps(response, resources[i], propFind);
                            }
                        }
                    }
                }
            } else {
                multiStatus.addSimpleResponse(Status.NOT_FOUND);
            }
            fixUnknownPropsResponse(multiStatus);
            multiStatus.render(true);
        } else {
            httpConnection.setResponseStatus(Status.NOT_FOUND);
        }
    }

    /**
     * Obtain properties recursively
     *
     * @param multiStatus multi status response
     * @param resource resource
     * @param propFind propfind structure
     * @param path current path
     */
    protected void obtainPropsRecursive(MultiStatus multiStatus, Object resource, PropFind propFind, String path) {
        Response response = multiStatus.newResponse(path);
        obtainProps(response, resource, propFind);
        if (adapter.isCollection(resource)) {
            Object[] resources = adapter.collectionElements(resource);
            if (resources != null) {
                for (int i = 0; i < resources.length; i++) {
                    String p = IOUtils.addPaths(path, adapter.getResourceName(resources[i]));
                    obtainPropsRecursive(multiStatus, resource, propFind, p);
                }
            }
        }
    }

    /**
     * Obtain properties of a given resource
     * @param response single response structure
     * @param resource resource
     * @param propFind propfind structure
     */
    protected void obtainProps(Response response, Object resource, PropFind propFind) {

        if (propFind.isPropname()) {
            Propstat propStat = new Propstat(response);
            propStat.setStatus(Status.OK);
            response.getPropStats().add(propStat);
            ResponseProperty[] properties = adapter.getDefaultResponseProperties(resource);
            for (ResponseProperty property : properties) {
                propStat.getProp().getProperties().add(property);
            }
        } else {
            Propstat positive = null;
            Map<String, Propstat> responses = null;

            List<RequestProperty> properties = null;
            if (propFind.isAllprop()) {
                properties = Arrays.asList(adapter.getDefaultRequestProperties(resource));
            } else {
                RequestProp prop = propFind.getProp();
                properties = prop.getProperties();
            }



            for (RequestProperty property : properties) {
                ResponseProperty responseProperty = property.processResponse(adapter, resource);
                Status status = responseProperty.getStatus();
                if (status == Status.OK) {
                    if (positive == null) {
                        positive = new Propstat(response);
                        positive.setStatus(Status.OK);
                    }
                    positive.getProp().getProperties().add(responseProperty);
                } else {
                    if (responses == null) {
                        responses = new HashMap<String, Propstat>();
                    }
                    Propstat propstat = responses.get(status.getCode());
                    if (propstat == null) {
                        propstat = new Propstat(response);
                        propstat.setStatus(responseProperty.getStatus());
                        responses.put(propstat.getStatus().getCode(), propstat);
                    }
                    propstat.getProp().getProperties().add(responseProperty);
                }
            }
            if (responses != null) {
                if (positive != null) {
                    response.getPropStats().add(positive);
                }
                response.getPropStats().addAll(responses.values());
            } else {
                if (positive != null) {
                    response.getPropStats().add(positive);
                }
            }
        }
    }


    /**
     * Implements PROPPATH method
     *
     * @param httpConnection connection
     */
    public void methodPROPPATCH(HTTPConnection httpConnection) {
        if (readOnly) {
            httpConnection.setResponseStatus(Status.FORBIDDEN);
            return;
        }

        Object resource = findResource(httpConnection);
        int depth = Depth.collectDepth(httpConnection);

        PropertyUpdate propertyUpdate;
        try {
            propertyUpdate = (PropertyUpdate)readRequestXML(httpConnection, PropertyUpdate.class);
        } catch (SAXException e) {
            httpConnection.setResponseStatus(Status.BAD_REQUEST);
            return;
        }
        if (propertyUpdate == null) {
            httpConnection.setResponseStatus(Status.BAD_REQUEST);
            return;
        }
        LockingMechanism lockingMechanism = adapter.getLockingMechanism();
        if (lockingMechanism != null) {
            IF lockDetails = new IF();
            boolean resourceLocked = lockingMechanism.isLocked(resource);
            if (!lockDetails.parse(httpConnection, adapter, resource, null)) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            }

            if ((lockDetails.token == null) && resourceLocked) {
                httpConnection.setResponseStatus(STATUS_LOCKED);
                return;
            } else if ((lockDetails.token != null) && !resourceLocked) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            } else if ((lockDetails.token != null) && adapter.isCollection(resource)) {
                MultiStatus multiStatus = new MultiStatus(adapter.getNamespacesProvider(), httpConnection);
                if (isLockedRecursive(lockingMechanism, lockDetails, multiStatus, httpConnection, resource, adapter.getResourceName(resource))) {
                    multiStatus.render(true);
                }
            }
        }

        if (adapter.exists(resource)) {
            MultiStatus multiStatus = new MultiStatus(adapter.getNamespacesProvider(), httpConnection);
            updateProps(multiStatus, resource, depth, propertyUpdate);
            fixUnknownPropsResponse(multiStatus);
            multiStatus.render();
        } else {
            httpConnection.setResponseStatus(Status.NOT_FOUND);
        }
    }

    /**
     * Updates properties
     *
     * @param multiStatus multi status response
     * @param resource resource
     * @param depth depth structure
     * @param propertyUpdate propertyupdate structure
     */
    protected void updateProps(MultiStatus multiStatus, Object resource, int depth, PropertyUpdate propertyUpdate) {
        Response response = multiStatus.newResponse();
        updateProps(response, resource, propertyUpdate);
        if ((depth != Depth.ZERO) && adapter.isCollection(resource)) {
            Object[] resources = adapter.collectionElements(resource);
            if (resources != null) {
                for (int i = 0; i < resources.length; i++) {
                    if (depth == Depth.INFINITY) {
                        String path = adapter.getResourceName(resources[i]);
                        updatePropsRecursive(multiStatus, resource, propertyUpdate, path);
                    } else {
                        response = multiStatus.newResponse(adapter.getResourceName(resources[i]));
                        updateProps(response, resources[i], propertyUpdate);
                    }
                }
            }
        }
    }

    /**
     * Updates properties recursively
     *
     * @param multiStatus multi status response
     * @param resource resource
     * @param propertyUpdate propertyupdate structure
     * @param path current path
     */
    protected void updatePropsRecursive(MultiStatus multiStatus, Object resource, PropertyUpdate propertyUpdate, String path) {
        Response response = multiStatus.newResponse(path);
        // TODO check lock!
        updateProps(response, resource, propertyUpdate);
        if (adapter.isCollection(resource)) {
            Object[] resources = adapter.collectionElements(resource);
            if (resources != null) {
                for (int i = 0; i < resources.length; i++) {
                    String p = IOUtils.addPaths(path, adapter.getResourceName(resources[i]));
                    updatePropsRecursive(multiStatus, resource, propertyUpdate, p);
                }
            }
        }
    }

    /**
     * Updates properties of a given resource (leaf)
     *
     * @param response single response
     * @param resource resource
     * @param propertyUpdate propertyupdate structure
     */
    protected void updateProps(Response response, Object resource, PropertyUpdate propertyUpdate) {
        Propstat positive = null;
        Map<String, Propstat> responses = null;
        for (RequestProp prop : propertyUpdate.getProperties()) {

            for (RequestProperty property : prop.getProperties()) {
                ResponseProperty responseProperty = null;
                if (prop.getType() == RequestProp.SET) {
                    responseProperty = property.processSetProperty(adapter, resource);
                } else {
                    responseProperty = property.processRemoveProperty(adapter, resource);
                }
                Status status = responseProperty.getStatus();
                if (status == Status.OK) {
                    if (positive == null) {
                        positive = new Propstat(response);
                        positive.setStatus(Status.OK);
                    }
                    positive.getProp().getProperties().add(responseProperty);
                } else {
                    if (responses == null) {
                        responses = new HashMap<String, Propstat>();
                    }
                    Propstat propstat = responses.get(status.getCode());
                    if (propstat == null) {
                        propstat = new Propstat(response);
                        propstat.setStatus(responseProperty.getStatus());
                        responses.put(propstat.getStatus().getCode(), propstat);
                    }
                    propstat.getProp().getProperties().add(responseProperty);
                }
            }
        }
        if (responses != null) {
            if (positive != null) {
                response.getPropStats().add(positive);
            }
            response.getPropStats().addAll(responses.values());
        } else {
            if (positive != null) {
                response.getPropStats().add(positive);
            }
        }
    }

    /**
     * Patch to fix response without &quot;known&quotl properties
     * @param multiStatus multi status response
     */
    protected void fixUnknownPropsResponse(MultiStatus multiStatus) {
        if ((multiStatus.getResponses().size() == 1) && (multiStatus.getResponses().get(0).getPropStats().size() == 0)) {
            Propstat propStat = new Propstat(multiStatus.getResponses().get(0));
            propStat.setStatus(Status.NOT_IMPLEMENTED);
            multiStatus.getResponses().get(0).getPropStats().add(propStat);
        }
    }


    /**
     * Implements MKCOL method (making collection).
     *
     * @param httpConnection connection
     */
    public void methodMKCOL(HTTPConnection httpConnection) {
        if (readOnly) {
            httpConnection.setResponseStatus(Status.FORBIDDEN);
            return;
        }

        Object resource = findResource(httpConnection);
        Object parentResource = adapter.findParentResource(resource);
        if ((parentResource != null) && !adapter.exists(parentResource)) {
            httpConnection.setResponseStatus(Status.CONFLICT);
            return;
        }

        if (adapter.exists(resource)) {
            httpConnection.setResponseStatus(Status.METHOD_NOT_ALLOWED);
            return;
        }

        boolean resourceLocked = false;
        boolean parentLocked = false;
        LockingMechanism lockingMechanism = adapter.getLockingMechanism();
        if (lockingMechanism != null) {
            IF lockDetails = new IF();
            resourceLocked = lockingMechanism.isLocked(resource);
            if (parentResource != null) {
                parentLocked = lockingMechanism.isLocked(parentResource);
            }

            boolean ok;
            if (parentLocked) {
                ok = lockDetails.parse(httpConnection, adapter, resource, parentResource);
            } else {
                ok = lockDetails.parse(httpConnection, adapter, resource, null);
            }
            if (!ok) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            }

            if ((lockDetails.token == null) && resourceLocked) {
                httpConnection.setResponseStatus(STATUS_LOCKED);
                return;
            } else if ((lockDetails.token != null) && !resourceLocked) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            }
        }

        InputStream in = httpConnection.adapt(InputStream.class);
        try {
            int r = in.read();
            if (r != -1) {
                httpConnection.setResponseStatus(Status.UNSUPPORTED_MEDIA_TYPE);
                return;
            }
        } catch (IOException ignore) {
        }

        try {
            adapter.makeCollection(resource);
            httpConnection.setResponseStatus(Status.CREATED);
        } catch (IOException e) {
            // TODO add log statement
            httpConnection.setResponseStatus(Status.FORBIDDEN);
        }
    }

    /**
     * Implements copy method
     *
     * @param httpConnection connection
     */
    public void methodCOPY(HTTPConnection httpConnection) {
        if (readOnly) {
            httpConnection.setResponseStatus(Status.FORBIDDEN);
            return;
        }

        int depth = Depth.collectDepth(httpConnection);
        if (depth == Depth.NONE) {
            depth = Depth.INFINITY;
        }
        /*PropertyBehavior propertyBehavior;*/try {
            /*propertyBehavior = (PropertyBehavior)*/readRequestXML(httpConnection, PropertyBehavior.class);
        } catch (SAXException e) {
            httpConnection.setResponseStatus(Status.BAD_REQUEST);
            return;
        }
        // TODO use property behavior!!!
        if (depth == Depth.ONE) {
            // MOVE MUST have infinity as a depth
            httpConnection.setResponseStatus(Status.BAD_REQUEST);
            return;
        }

        Object fromResource = findResource(httpConnection);

        boolean overwrite = !"F".equals(httpConnection.getRequestHeaders().getOnly("Overwrite"));
        String destHeader = httpConnection.getRequestHeaders().getOnly("Destination");
        Object destResource = URIUtils.uriToResource(httpConnection, adapter, destHeader);
        if (destResource == null) {
            httpConnection.setResponseStatus(Status.FORBIDDEN);
            return;
        }

        if (adapter.exists(destResource) && !overwrite) {
            httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
            return;
        }

        Object destParentResource = adapter.findParentResource(destResource);
        if ((destParentResource != null) && !adapter.exists(destParentResource)) {
            httpConnection.setResponseStatus(Status.CONFLICT);
            return;
        }

        LockingMechanism lockingMechanism = adapter.getLockingMechanism();
        boolean destLocked = false;
        boolean destParentLocked = false;
        IF lockDetails = null;

        if (lockingMechanism != null) {
            lockDetails = new IF();
            destLocked = lockingMechanism.isLocked(destResource);
            if (destParentResource != null) {
                destParentLocked = lockingMechanism.isLocked(destParentResource);
            }

            boolean ok;
            if (destParentLocked) {
                ok = lockDetails.parse(httpConnection, adapter, destResource, destParentResource);
            } else {
                ok = lockDetails.parse(httpConnection, adapter, destResource, null);
            }
            if (!ok) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            }

            if ((lockDetails.token == null) && destLocked) {
                httpConnection.setResponseStatus(STATUS_LOCKED);
                return;
            } else if ((lockDetails.token != null) && !destLocked) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            } else if ((lockDetails.token != null) && adapter.isCollection(destResource)) {
                // TODO Deep check if locked?
            }
        }

        boolean newFile = true;
        if (overwrite && adapter.exists(destResource)) {
            newFile = false;
            MultiStatus multiStatus = new MultiStatus(adapter.getNamespacesProvider(), httpConnection);
            if (!deleteRecursive(multiStatus, "/", destResource)) {
                multiStatus.render();
                return;
            }
        }

        try {
            adapter.copy(fromResource, destResource, (depth == Depth.INFINITY));
            if (newFile) {
                if (destParentLocked) {
                    Lock lock = lockingMechanism.findLock(lockDetails.token);
                    lockingMechanism.lockResource(lock, destResource);
                }
                httpConnection.setResponseStatus(Status.CREATED);
            } else {
                httpConnection.setResponseStatus(Status.NO_CONTENT);
            }
        } catch (IOException e) {
            httpConnection.setResponseStatus(Status.FORBIDDEN);
        }

    }

    /**
     * Moves resource
     *
     * <p><i>Note: property behaviour is ignored in this implementation!</i></p>
     * @param httpConnection connection
     */
    public void methodMOVE(HTTPConnection httpConnection) {
        if (readOnly) {
            httpConnection.setResponseStatus(Status.FORBIDDEN);
            return;
        }

        int depth = Depth.collectDepth(httpConnection);
        if (depth == Depth.NONE) {
            depth = Depth.INFINITY;
        }
        /*PropertyBehavior propertyBehavior = (PropertyBehavior)*/try {
            readRequestXML(httpConnection, PropertyBehavior.class);
        } catch (SAXException e) {
            httpConnection.setResponseStatus(Status.BAD_REQUEST);
            return;
        }

        if (depth != Depth.INFINITY) {
            // MOVE MUST have infinity as a depth
            httpConnection.setResponseStatus(Status.BAD_REQUEST);
            return;
        }

        Object fromResource = findResource(httpConnection);

        boolean overwrite = !"F".equals(httpConnection.getRequestHeaders().getOnly("Overwrite"));
        String destHeader = httpConnection.getRequestHeaders().getOnly("Destination");
        Object destResource = URIUtils.uriToResource(httpConnection, adapter, destHeader);

        if (destResource == null) {
            httpConnection.setResponseStatus(Status.FORBIDDEN);
            return;
        }

        if (adapter.exists(destResource) && !overwrite) {
            httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
            return;
        }

        Object destParentResource = adapter.findParentResource(destResource);
        if ((destParentResource != null) && !adapter.exists(destParentResource)) {
            httpConnection.setResponseStatus(Status.CONFLICT);
            return;
        }

        LockingMechanism lockingMechanism = adapter.getLockingMechanism();
        boolean destLocked = false;
        boolean destParentLocked = false;
        IF lockDetails = null;

        if (lockingMechanism != null) {
            lockDetails = new IF();
            Object fromParentResource = adapter.findParentResource(fromResource);
            boolean fromLocked = lockingMechanism.isLocked(fromResource);
            boolean fromParentLocked = false;
            if (fromParentResource != null) {
                fromParentLocked = lockingMechanism.isLocked(fromParentResource);
            }

            boolean ok;
            if (fromParentLocked) {
                ok = lockDetails.parse(httpConnection, adapter, fromResource, fromParentResource);
            } else {
                ok = lockDetails.parse(httpConnection, adapter, fromResource, null);
            }
            if (!ok) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            }

            if ((lockDetails.token == null) && fromLocked) {
                httpConnection.setResponseStatus(STATUS_LOCKED);
                return;
            } else if ((lockDetails.token != null) && !fromLocked) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            } else if ((lockDetails.token != null) && adapter.isCollection(fromResource)) {
                MultiStatus multiStatus = new MultiStatus(adapter.getNamespacesProvider(), httpConnection);
                if (isLockedRecursive(lockingMechanism, lockDetails, multiStatus, httpConnection, fromResource, adapter.getResourceName(fromResource))) {
                    multiStatus.render();
                    return;
                }
            }


            destLocked = lockingMechanism.isLocked(destResource);
            if (destLocked) {
                if ((lockDetails.clearedResources == null) || !lockDetails.clearedResources.contains(destResource)) {
                    if (!lockingMechanism.isAccessAllowed(destResource, lockDetails.token)) {
                        httpConnection.setResponseStatus(STATUS_LOCKED);
                        return;
                    }
                }
            }

            if (destParentResource != null) {
                destParentLocked = lockingMechanism.isLocked(destParentResource);
            }
            if (destParentLocked) {
                if ((lockDetails.clearedResources == null) || !lockDetails.clearedResources.contains(destParentResource)) {
                    if (!lockingMechanism.isAccessAllowed(destParentResource, lockDetails.token)) {
                        httpConnection.setResponseStatus(STATUS_LOCKED);
                        return;
                    }
                }
            }
        }

        boolean newFile = true;
        if (overwrite && !fromResource.equals(destResource) && adapter.exists(destResource)) {
            newFile = false;
            MultiStatus multiStatus = new MultiStatus(adapter.getNamespacesProvider(), httpConnection);
            if (!deleteRecursive(multiStatus, "/", destResource)) {
                multiStatus.render();
                return;
            }
        }

        try {
            adapter.move(fromResource, destResource);
            if (newFile) {
                if (destParentLocked) {
                    Lock lock = lockingMechanism.findLock(lockDetails.token);
                    lockingMechanism.lockResource(lock, destResource);
                }
                httpConnection.setResponseStatus(Status.CREATED);
            } else {
                httpConnection.setResponseStatus(Status.NO_CONTENT);
            }
            if ((lockingMechanism != null) && lockingMechanism.isLocked(fromResource)) {
                lockingMechanism.removeLocks(fromResource);
            }
        } catch (IOException e) {
            httpConnection.setResponseStatus(Status.FORBIDDEN);
        }
    }

    /**
     * Implements LOCK method. This method relies that adapter has non-null lcoking mechanism
     * defined.
     *
     * @param httpConnection connection
     */
    public void methodLOCK(HTTPConnection httpConnection) {
        LockingMechanism lockingMechanism = adapter.getLockingMechanism();
        Object resource = findResource(httpConnection);

        LockInfo lockInfo;
        try {
            lockInfo = (LockInfo)readRequestXML(httpConnection, LockInfo.class);
        } catch (SAXException e) {
            httpConnection.setResponseStatus(Status.BAD_REQUEST);
            return;
        }
        if (lockingMechanism == null) {
            httpConnection.setResponseStatus(Status.METHOD_NOT_ALLOWED);
            return;
        }

        int depth = Depth.collectDepth(httpConnection);
        if (depth == Depth.NONE) {
            depth = Depth.INFINITY;
        }
        if ((depth == Depth.ONE)/* || (lockInfo == null)*/) {
            httpConnection.setResponseStatus(Status.BAD_REQUEST);
        } else {
            Lock[] locks = null;
            IF lockDetails = new IF();

            if (!lockDetails.parse(httpConnection, adapter, resource, null)) {
                httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                return;
            }

            if (lockingMechanism.isLocked(resource)) {
                if (lockInfo != null) {
                    locks = lockingMechanism.getLocks(resource);
                    for (Lock lock : locks) {
                        if (lock.getScope() == LockingMechanism.SCOPE_EXCLUSIVE) {
                            httpConnection.setResponseStatus(STATUS_LOCKED);
                            return;
                        }
                    }
                }
            } else {
                if (lockInfo == null) {
                    httpConnection.setResponseStatus(Status.PRECONDITION_FAILED);
                    return;
                }
            }

            if (adapter.exists(resource)) {
                Timeout[] timeouts = collectTimeouts(httpConnection);
                MultiStatus multiStatus = new MultiStatus(adapter.getNamespacesProvider(), httpConnection);

                Lock lock = null;
                if (lockInfo != null) {
//                    if (lockInfo.getScope() == LockingMechanism.SCOPE_EXCLUSIVE) {
//                        if (locks != null) {
//                            httpConnection.setResponseStatus(STATUS_LOCKED);
//                            return;
//                        }
//                    }

                    Object owner = lockInfo.getOwner();
                    if (timeouts == null) {
                        lock = lockingMechanism.createLock(lockInfo.getType(), lockInfo.getScope(), owner, null, depth);
                    } else if (timeouts.length == 1) {
                        lock = lockingMechanism.createLock(lockInfo.getType(), lockInfo.getScope(), owner, timeouts[0], depth);
                    } else {
                        int i = 0;
                        while ((lock == null) && (i < timeouts.length)) {
                            lock = lockingMechanism.createLock(lockInfo.getType(), lockInfo.getScope(), owner, timeouts[i], depth);
                            i++;
                        }
                    }
                    Response response = multiStatus.newResponse();
                    boolean ok = true;
                    if (lockImpl(response, lock, lockingMechanism, lockInfo, owner, depth == Depth.INFINITY, resource)) {
                        if ((depth != Depth.ZERO) && adapter.isCollection(resource)) {
                            Object[] resources = adapter.collectionElements(resource);
                            if (resources != null) {
                                int i = 0;
                                while (ok && (i < resources.length)) {
                                    String path = adapter.getResourceName(resources[i]);
                                    ok = lockRecursive(multiStatus, lock, lockingMechanism, lockInfo, owner, resources[i], path);
                                    i ++;
                                }
                            }
                        }
                    } else {
                        ok = false;
                    }
                    if (ok) {
                        httpConnection.getResponseHeaders().putOnly("Lock-Token", lock.getToken().toString());
                    } else {
                        lockingMechanism.unlockResources(lock);
                    }
                    multiStatus.render(false);
                } else {
                    // Refresh
                    if (lockDetails.token != null) {
                        lock = lockingMechanism.findLock(lockDetails.token);
                        if ((timeouts == null) || (timeouts.length == 0)) {
                            lock.refreshTimeout(null);
                        } else {
                            boolean ok = false;
                            int i = 0;
                            while (!ok) {
                                ok = lock.refreshTimeout(timeouts[i]);
                                i++;
                            }
                        }
                        httpConnection.getResponseHeaders().putOnly("Timeout", lock.getTimeout().asString());
                        Response response = multiStatus.newResponse();
                        Propstat propstat = new Propstat(response);
                        propstat.setStatus(Status.OK);
                        LockDiscovery lockDiscovery = new LockDiscovery(Status.OK);
                        lockDiscovery.getLocks().add(lock);
                        propstat.getProp().getProperties().add(lockDiscovery);
                        response.getPropStats().add(propstat);
                        multiStatus.render(false);
                    } else {
                        httpConnection.setResponseStatus(Status.CONFLICT);
                    }
                }
            } else {
                httpConnection.setResponseStatus(Status.NOT_FOUND);
            }
        }
    }

    /**
     * Locks resource recursively.
     *
     * @param multiStatus multi status response
     * @param lock lock
     * @param lockingMechanism locking mechanism
     * @param lockInfo lock info structure
     * @param owner owner
     * @param resource resource
     * @param path path
     * @return <code>true</code> if locking succeeded
     */
    protected boolean lockRecursive(MultiStatus multiStatus, Lock lock, LockingMechanism lockingMechanism, LockInfo lockInfo, Object owner, Object resource, String path) {
        Response response = multiStatus.newResponse(path);
        if (lockImpl(response, lock, lockingMechanism, lockInfo, owner, true, resource)) {
            if (adapter.isCollection(resource)) {
                Object[] resources = adapter.collectionElements(resource);
                if (resources != null) {
                    for (int i = 0; i < resources.length; i++) {
                        String p = IOUtils.addPaths(path, adapter.getResourceName(resources[i]));
                        if (!lockRecursive(multiStatus, lock, lockingMechanism, lockInfo, owner, resource, p)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Locks the resource
     *
     * @param response single response
     * @param lock lock
     * @param lockingMechanism locking mechanism
     * @param lockInfo lockinfo structure
     * @param owner owner
     * @param recursive is recursive
     * @param resource resource
     * @return <code>true</code> if succeeded
     */
    protected boolean lockImpl(Response response, Lock lock, LockingMechanism lockingMechanism, LockInfo lockInfo, Object owner, boolean recursive, Object resource) {
        // TODO Check this!!!
        Lock[] locks = lockingMechanism.getLocks(resource);
        if (locks != null) {
            if ((lockInfo.getScope() == LockingMechanism.SCOPE_EXCLUSIVE)) {
                response.setStatus(STATUS_LOCKED);
                return false;
            } else {
                for (Lock l : locks) {
                    if (l.getScope() == LockingMechanism.SCOPE_EXCLUSIVE) {
                        response.setStatus(STATUS_LOCKED);
                        return false;
                    }
                }
            }
        }

//        if (!lockingMechanism.isAccessAllowed(resource, lock.getToken())) {
//            response.setStatus(STATUS_LOCKED);
//            return false;
//        } else
        int[] lockScopes = lockingMechanism.getSupportedLockScopes(resource);
        if (lockScopes.length == 0) {
            response.setStatus(Status.PRECONDITION_FAILED);
            return false;
        } else {
            if (lockingMechanism.lockResource(lock, resource)) {
                Propstat propstat = new Propstat(response);
                LockDiscovery lockDiscovery = new LockDiscovery(Status.OK);
                lockDiscovery.getLocks().add(lock);
                propstat.getProp().getProperties().add(lockDiscovery);
                response.getPropStats().add(propstat);
                propstat.setStatus(Status.OK);
                return true;
            } else {
                response.setStatus(Status.PRECONDITION_FAILED);
                return false;
            }
        }
    }

    /**
     * Implements UNLOCK method. Ir relies on adapter providing locking mechanism.
     *
     * @param httpConnection connection
     */
    public void methodUNLOCK(HTTPConnection httpConnection) {
        Object resource = findResource(httpConnection);
        LockingMechanism lockingMechanism = adapter.getLockingMechanism();
        if (lockingMechanism == null) {
            httpConnection.setResponseStatus(Status.NO_CONTENT);
            return;
        }
        String token = httpConnection.getRequestHeaders().getOnly("Lock-Token");
        if (token != null) {
            if (token.startsWith("<") && token.endsWith(">") && (token.length() > 2)) {
                token = token.substring(1, token.length() - 1);
            } else {
                httpConnection.setResponseStatus(Status.BAD_REQUEST);
                return;
            }
        }

        if (lockingMechanism.isAccessAllowed(resource, token)) {
            httpConnection.setResponseStatus(STATUS_LOCKED);
        }

        if (adapter.exists(resource)) {
            Lock lock = lockingMechanism.findLock(token);
            if (lock != null) {
                lockingMechanism.unlockResources(lock);
                httpConnection.setResponseStatus(Status.NO_CONTENT);
            } else {
                httpConnection.setResponseStatus(STATUS_LOCKED);
            }
        } else {
            httpConnection.setResponseStatus(Status.NOT_FOUND);
        }
    }

    /**
     * Checks recursively if resource is locked and all of it's children.
     * @param lockingMechanism locking mechanism
     * @param lockDetails lock details
     * @param multiStatus multi status response
     * @param httpConnection connection
     * @param resource resource
     * @param path current path
     * @return <code>true</code> if resource is locked
     */
    protected boolean isLockedRecursive(LockingMechanism lockingMechanism, IF lockDetails, MultiStatus multiStatus, HTTPConnection httpConnection, Object resource, String path) {
        boolean locked = false;
        Object[] resources = adapter.collectionElements(resource);
        if ((resources != null) && (resources.length > 0)) {
            for (Object r : resources) {
                String p = IOUtils.addPaths(path, adapter.getResourceName(r));
                if ((lockDetails.clearedResources == null) || !lockDetails.clearedResources.contains(r)) {
                    if (!lockingMechanism.isAccessAllowed(r, lockDetails.token)) {
                        Response response = multiStatus.newResponse(p);
                        response.setStatus(STATUS_FAILED_DEPENDENCY);
                        locked = true;
                    }
                }
                if (adapter.isCollection(r)) {
                    isLockedRecursive(lockingMechanism, lockDetails, multiStatus, httpConnection, r, p);
                }
            }
        }

        return locked;
    }

    /**
     * Collects range from the &quot;Range&quot; header.
     *
     * @param connection http connection
     * @return {@link Ranges} or <code>null</code>
     */
    protected Ranges collectRange(HTTPConnection connection) {
        String rangesHeader = connection.getRequestHeaders().getOnly("Range");
        if (rangesHeader != null) {
            Ranges ranges = Ranges.parseRange(rangesHeader);
            return ranges;
        } else {
            return null;
        }
    }

    /**
     * Collects timeouts from the &quot;Timeout&quot; header
     * @param connection http connection
     * @return {@link Timeout} structure or <code>null</code>
     */
    protected Timeout[] collectTimeouts(HTTPConnection connection) {
        String timeoutHeader = connection.getRequestHeaders().getOnly("Timeout");
        if (timeoutHeader != null) {
            int i = timeoutHeader.indexOf(',');
            if (i >= 0) {
                ArrayList<Timeout> timeouts = new ArrayList<Timeout>();
                StringTokenizer tokenizer = new StringTokenizer(timeoutHeader, ",");
                while (tokenizer.hasMoreTokens()) {
                    Timeout timeout = Timeout.parse(tokenizer.nextToken());
                    timeouts.add(timeout);
                }
                Timeout[] res = new Timeout[timeouts.size()];
                return timeouts.toArray(res);
            } else {
                Timeout timeout = Timeout.parse(timeoutHeader);
                return new Timeout[]{timeout};
            }
        } else {
            return null;
        }
    }


    /**
     * Reads request XML strcture and parses it returning an object based on {@link WebDAVXMLHandler}.
     *
     * @param adaptable adaptable object to be used for obtaining input stream
     * @param expectedClass expected class
     * @return an object of an expected class or <code>null</code>
     * @throws SAXException if there was an expcetion while parsing
     */
    protected Object readRequestXML(Adaptable adaptable, Class<?> expectedClass) throws SAXException {
        InputStream inputStream = adaptable.adapt(InputStream.class);
        if ((inputStream instanceof HTTPBufferedInputStream)
                && (((HTTPBufferedInputStream)inputStream).getContentLength() == 0)) {
            return null;
        } else {
            try {
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                SAXParser parser = parserFactory.newSAXParser();
                XMLReader reader = parser.getXMLReader();
                reader.setFeature("http://xml.org/sax/features/namespaces", true);

                InputSource inputSource = new InputSource(inputStream);
                WebDAVXMLHandler webDAVXMLHandler = new WebDAVXMLHandler(adapter.getNamespacesProvider());
                parser.parse(inputSource, webDAVXMLHandler);
                Object result = webDAVXMLHandler.getResultObject();
                if ((result != null) && (expectedClass.isAssignableFrom(result.getClass()))) {
                    return result;
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
