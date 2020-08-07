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

import org.abstracthorizon.danube.webdav.lock.LockingMechanism;
import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.RequestProperty;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This is resource adapter interface that simplifies
 * access to resource from WebDAV handler
 *
 * @author Daniel Sendula
 */
public interface ResourceAdapter {

    /**
     * <p>
     * Obtains resource object. It is supposed to be resource
     * itself or an proxy as {@link File} is.
     * </p>
     * <p>
     * If this method returns <code>null</code> then rest of the method
     * relying on the resource must be able to deal with it since
     * this class users might not care what the value is.
     * </p>
     *
     * @param path path to the resource
     * @return resource
     */
    Object findResource(String path);

    /**
     * Returns parent resource for given resource.
     *
     *
     * @param resource existing resource
     * @return parent resource or <code>null</code> if resource is already root of the hierarchy
     */
    Object findParentResource(Object resource);

    /**
     * Returns resource's length or -1 if unknown
     * @param resource resource
     * @return resource's length
     */
    long resourceLength(Object resource);

    /**
     * Returns when resource was last modified or -1 if unknown
     * @param resource resource
     * @return resource's last modified timestamp
     */
    long resourceLastModified(Object resource);

    /**
     * Returns when resource was created or -1 if unknown
     * @param resource resource
     * @return resource's created timestamp
     */
    long resourceCreated(Object resource);

    /**
     * Returns resource name
     * @param resource resource name
     * @return resource name
     */
    String getResourceName(Object resource);

    /**
     * Returns resource ETag (as specified in RFC-2616)
     * @param resource resource
     * @return etag
     */
    String getResourceETag(Object resource);

    /**
     * Returns <code>true</code> if resource exists
     * @param resource resource
     * @return <code>true</code> if resource exists
     */
    boolean exists(Object resource);

    /**
     * Returns <code>true</code> if resource is a collection
     * @param resource resource
     * @return <code>true</code> if resource is a collection
     */
    boolean isCollection(Object resource);

    /**
     * Deletes resource
     *
     * @param resource resource
     *
     * @throws IOException thrown if there was a problem deleting the resource
     */
    void delete(Object resource) throws IOException;

    /**
     * Makes a collection
     *
     * @param resource resource that identifies collection
     * @throws IOException thrown if there was a problem while creating the collection
     */
    void makeCollection(Object resource) throws IOException;

    /**
     * Copies the resource to given destination.
     *
     * @param source source resource
     * @param destination destination resource
     * @param recursive will it perform deep copy or not
     * @throws IOException thrown if there is a problem with copying.
     */
    void copy(Object source, Object destination, boolean recursive) throws IOException;

    /**
     * Noves the resource to given destination
     * @param source source
     * @param destination destination
     * @throws IOException thrown if moving failed
     */
    void move(Object source, Object destination) throws IOException;

    /**
     * Returns collection elements for given resource
     *
     * @param resource resource
     * @return collection elements or <code>null</code> if there is no elemetns or resource is not a colleciton
     */
    Object[] collectionElements(Object resource);

    // TODO Refactor this
    ResponseProperty[] getDefaultResponseProperties(Object resource);

    // TODO Refactor this
    RequestProperty[] getDefaultRequestProperties(Object resource);

    /**
     * Returns input stream of a resource
     *
     * @param resource resource
     * @return input stream or <code>null</code> if not supported
     *
     * @throws IOException thrown if there is a problem returning the input stream
     */
    InputStream getInputStream(Object resource) throws IOException;

    /**
     * Returns input stream of a resource's range
     *
     * @param resource resource
     * @param from from offset
     * @param length amount of bytes to be trasmitted
     * @return input stream or <code>null</code> if not supported
     *
     * @throws IOException thrown if there is a problem returning the input stream
     */
    InputStream getInpusStream(Object resource, long from, long length) throws IOException;

    /**
     * Returns output stream of a resource. It creates new resource or overwrites
     * existing.
     *
     * @param resource resource
     * @return output stream or <code>null</code> if not supported
     *
     * @throws IOException thrown if there is a problem creating new resource or replaying existing
     */
    OutputStream getOutputStream(Object resource) throws IOException;

    /**
     * Returns output stream of a resource. It creates new resource or overwrites
     * existing. Given range specifies that only part of the resource is going to be supplied.
     *
     * @param resource resource
     * @param from from offset
     * @param length number of bytes to be trasmitted
     * @return output stream or <code>null</code> if not supported
     *
     * @throws IOException thrown if there is a problem creating new resource or replaying existing
     */
    OutputStream getOutputStream(Object resource, long from, long length) throws IOException;

    // TODO - is this right place for the provider?
    /**
     * Returns namespace privider
     * @return namespace provider
     */
    NamespacesProvider getNamespacesProvider();

    // TODO - re-design obtaining locking mechanism so it doesn't have to be supplied by the adapter
    /**
     * Returns locking mechanism
     * @return locking mechanism
     */
    LockingMechanism getLockingMechanism();
}
