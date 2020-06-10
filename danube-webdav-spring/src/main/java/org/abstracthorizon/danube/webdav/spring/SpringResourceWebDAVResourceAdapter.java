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
package org.abstracthorizon.danube.webdav.spring;

import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.http.util.IOUtils;
import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.lock.LockingMechanism;
import org.abstracthorizon.danube.webdav.lock.impl.SimpleInMemoryLockingMechanism;
import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.util.SimpleNamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.RequestProperty;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.CreationDate;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.DisplayName;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentLength;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentType;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetETag;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetLastModified;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.LockDiscovery;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResourceType;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.SupportedLock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;

/**
 * Simple file system resource adapter
 *
 * @author Daniel Sendula
 */
public class SpringResourceWebDAVResourceAdapter implements ResourceAdapter {

    /** Root path of the files to be served */
    protected Resource path;

    /** Default file property names */
    protected static ResponseProperty[] filePropertyNames = new ResponseProperty[]{
        new CreationDate(Status.OK),
        new DisplayName(Status.OK),
        new GetContentLength(Status.OK),
        new GetContentType(Status.OK),
        new GetETag(Status.OK),
        new GetLastModified(Status.OK),
        new LockDiscovery(Status.OK),
        new ResourceType(Status.OK),
        // new Source(null),
        new SupportedLock(Status.OK)
    };

    /** Default directory property names */
    protected static ResponseProperty[] dirPropertyNames = new ResponseProperty[]{
        new CreationDate(Status.OK),
        new DisplayName(Status.OK),
        new GetETag(Status.OK),
        new GetLastModified(Status.OK),
        new LockDiscovery(Status.OK),
        new ResourceType(Status.OK),
        // new Source(null),
        new SupportedLock(Status.OK)
    };

    /** Default file request properties */
    protected static RequestProperty[] fileRequestProperties = new RequestProperty[]{
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.CreationDate(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.DisplayName(null),
        new org.abstracthorizon.danube.webdav.spring.properties.GetContentLength(null),
        new org.abstracthorizon.danube.webdav.spring.properties.GetContentType(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetETag(null),
        new org.abstracthorizon.danube.webdav.spring.properties.GetLastModified(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.LockDiscovery(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.ResourceType(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.SupportedLock(null, null)
    };

    /** Default dir request properties */
    protected static RequestProperty[] dirRequestProperties = new RequestProperty[]{
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.CreationDate(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.DisplayName(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetETag(null),
        new org.abstracthorizon.danube.webdav.spring.properties.GetLastModified(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.LockDiscovery(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.ResourceType(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.SupportedLock(null, null)
    };

    /** Namespace provider to be used with this adapter */
    protected NamespacesProvider namespacesProvider = new SimpleNamespacesProvider();

    /** Locking mechanism to be used with this adapter */
    protected LockingMechanism lockingMechanism = new SimpleInMemoryLockingMechanism();

    /** Default DAV namespace */
    protected DAVNamespace davNamespace = new DAVNamespace();

    /**
     * Constructor
     */
    public SpringResourceWebDAVResourceAdapter() {
        initHandlers();
    }

    /**
     * Constructor
     * @param path root directory for files to be served from
     */
    public SpringResourceWebDAVResourceAdapter(Resource path) {
        setResourcePath(path);
        initHandlers();
    }

    /**
     * Initialises handlers
     */
    protected void initHandlers() {
        davNamespace.setDAVFactory(new SpringResourceDAVFactory());
        namespacesProvider.addNamespace(davNamespace.getURLString(), davNamespace.getPreferredPrefix(), davNamespace);
    }

    /**
     * Returns root file path
     * @return root file path
     */
    public Resource getResourcePath() {
        return path;
    }

    /**
     * Sets root file path
     * @param path root file path
     */
    public void setResourcePath(Resource path) {
        this.path = path;
        try {
            URL url = path.getURL();
            if (!url.getPath().endsWith("/")) {
                // This is needed since spring resource doesn't recognise resource as a
                // path unless it has a trailing '/'
                String filename = path.getFilename();
                this.path = path.createRelative(filename + "/");
            }
        } catch (IOException e) {
        }
    }

    /**
     * Returns namespace provider
     * @return namespace provider
     */
    public NamespacesProvider getNamespacesProvider() {
        return namespacesProvider;
    }

    /**
     * Sets namespace provider
     * @param namespacesProvider namespace provider
     */
    public void setNamespacesProvider(NamespacesProvider namespacesProvider) {
        this.namespacesProvider = namespacesProvider;
    }

    /**
     * Returns locking mechanism
     * @return locking mechanism
     */
    public LockingMechanism getLockingMechanism() {
        return lockingMechanism;
    }

    /**
     * Sets locking mechanism
     * @param lockingMechanism locking mechanism
     */
    public void setLockingMechanism(LockingMechanism lockingMechanism) {
        this.lockingMechanism = lockingMechanism;
    }

    /**
     * Returns {@link File} objects of supplied resource directory
     * @return {@link File} objects of supplied resource directory
     */
    public Object[] collectionElements(Object resource) {
        Resource res = (Resource)resource;

        try {
            File dir = res.getFile();
            File[] list = dir.listFiles();
            if (list != null) {
                Resource[] result = new Resource[list.length];
                for (int i = 0; i < list.length; i++) {
                    result[i] = res.createRelative(list[i].getName());
                }
                return result;
            } else {
                return null;
            }
        } catch (IOException ignore) {
        }
        return null;
    }

    /**
     * Copies source file to destination file
     * @param source source file
     * @param destination destination file
     * @param recursive is it deep copy
     */
    public void copy(Object source, Object destination, boolean recursive) throws IOException {
        File from = ((Resource)source).getFile();
        File to = ((Resource)destination).getFile();
        // TODO update IOUtils to return IOException
        if (!IOUtils.copy(from, to, recursive)) {
            throw new IOException("Cannot copy");
        }
    }

    /**
     * Deletes a file
     * @param resource file
     * @throws IOException thrown if there is a problem with deletion of the resource
     */
    public void delete(Object resource) throws IOException {
        boolean ok;
        File file = ((Resource)resource).getFile();
        if (file.isDirectory()) {
            // TODO update IOUtils to return IOException
            ok = IOUtils.delete(file);
        } else {
            ok = file.delete();
        }
        if (!ok) {
            throw new IOException("Cannot delete");
        }
    }

    /**
     * Returns <code>true</code> if file exists
     * @return <code>true</code> if file exists
     */
    public boolean exists(Object resource) {
        Resource file = (Resource)resource;
        return file.exists();
    }

    /**
     * Returns {@link File} object of given path startin from {@link #path}
     * @param path path
     * @return a file
     */
    public Object findResource(String path) {
        try {
            Resource file = this.path.createRelative(path);
            return file;
        } catch (IOException e) {
            String basePath = "/";
            try {
                URL url = this.path.getURL();
                basePath = url.getPath();
            } catch (IOException ignore) {
            }
            DescriptiveResource res = new DescriptiveResource(IOUtils.addPaths(basePath, path));
            return res;
        }
    }

    /**
     * Returns file's parent
     * @param resource file
     * @return file's parent
     */
    public Object findParentResource(Object resource) {
        // TODO ??? Check this implementation
        Resource r = (Resource)resource;
        if (r.equals(this.path)) {
            return null;
        }
        if (r instanceof DescriptiveResource) {
            DescriptiveResource res = (DescriptiveResource)r;
            String path = res.getDescription();
            path = IOUtils.parentPath(path);
            try {
                return this.path.createRelative(path);
            } catch (IOException e) {
                return new DescriptiveResource(path);
            }
        }
        try {
            URL url = r.getURL();
            String path = url.getPath();
            path = IOUtils.parentPath(path);

            return this.path.createRelative(path);
        } catch (IOException ignore) {
            return null;
        }
    }

    /**
     * Returns default request properties
     * @param resource a file
     * @return {@link #dirRequestProperties} or {@link #fileRequestProperties}
     */
    public RequestProperty[] getDefaultRequestProperties(Object resource) {
        if (isCollection(resource)) {
            return dirRequestProperties;
        } else {
            return fileRequestProperties;
        }
    }

    /**
     * Returns default response properties
     * @param resource a file
     * @return {@link #dirPropertyNames} or {@link #filePropertyNames}
     */
    public ResponseProperty[] getDefaultResponseProperties(Object resource) {
        if (isCollection(resource)) {
            return dirPropertyNames;
        } else {
            return filePropertyNames;
        }
    }

    /**
     * Returns {@link FileInputStream}
     * @param resource a file
     * @return {@link FileInputStream}
     */
    public InputStream getInputStream(Object resource) {
        try {
            return ((Resource)resource).getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Returns {@link RandomAccessFileRangeInputStream}
     * @param resource a file
     * @param from from offset
     * @param length length in bytes
     * @return {@link RandomAccessFileRangeInputStream}
     */
    public InputStream getInpusStream(Object resource, long from, long length) {
        return null;
    }

    /**
     * Returns {@link FileOutputStream}
     * @param resource a file
     * @return {@link FileOutputStream}
     */
    public OutputStream getOutputStream(Object resource) {
        try {
            File file = ((Resource)resource).getFile();
            return new FileOutputStream(file);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Returns {@link RandomAccessFileRangeOutputStream}
     * @param resource a file
     * @param from from offset
     * @param length length in bytes
     * @return {@link RandomAccessFileRangeOutputStream}
     */
    public OutputStream getOutputStream(Object resource, long from, long length) {
        return null;
    }

    /**
     * Returns file's name
     * @param resource file
     * @return file's name
     */
    public String getResourceName(Object resource) {
        return ((Resource)resource).getFilename();
    }

    /**
     * Returns file's last modified timestamp as hex plus file's absolute path
     * @param resource file
     * @return file's last modified timestamp as hex plus file's absolute path
     */
    public String getResourceETag(Object resource) {
        try {
            URL url = ((Resource)resource).getURL();
            String pathString = url.getPath();
            URL baseURL = ((Resource)resource).getURL();
            String basePath = baseURL.getPath();

            String eTag = pathString;
            eTag = eTag.substring(basePath.length());
            String lastModified = "";
            try {
                File file = ((Resource)resource).getFile();
                lastModified = Long.toHexString(file.lastModified());
            } catch (IOException ignore2) {
            }
            eTag = "W/\"" + lastModified + "-" + eTag + "\"";
            return eTag;
        } catch (IOException e) {
            return "W/\"" + resource.toString() + "\"";
        }
    }

    /**
     * Returns <code>true</code> if file is a directory
     * @param resource a file
     * @return <code>true</code> if file is a directory
     */
    public boolean isCollection(Object resource) {
        try {
            File file = ((Resource)resource).getFile();
            return file.isDirectory();
        } catch (IOException ignore) {
        }
        return false;
    }

    /**
     * Makes a directory
     * @param resource a file
     * @throws IOException thrown in there is a problem in making the file
     */
    public void makeCollection(Object resource) throws IOException {
        File file = ((Resource)resource).getFile();
        if (!file.mkdir()) {
            throw new IOException("Cannot create folder");
        }
    }

    /**
     * Renames the file
     * @param source source
     * @param destination destination
     * @throws IOException thrown if there is a problem with copying the file
     */
    public void move(Object source, Object destination) throws IOException {
        File from = ((Resource)source).getFile();
        File to = ((Resource)destination).getFile();
        if (!from.renameTo(to)) {
            throw new IOException("Cannot move");
        }
    }

    /**
     * Returns file's last modified timestamp
     * @param resource a file
     * @return file's last modified timestamp
     */
    public long resourceCreated(Object resource) {
        try {
            File file = ((Resource)resource).getFile();
            return file.lastModified();
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Returns file's last modified timestamp
     * @param resource a file
     * @return file's last modified timestamp
     */
    public long resourceLastModified(Object resource) {
        try {
            File file = ((Resource)resource).getFile();
            return file.lastModified();
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Returns file's length
     * @param resource a file
     * @return file's length
     */
    public long resourceLength(Object resource) {
        try {
            File file = ((Resource)resource).getFile();
            return file.length();
        } catch (IOException e) {
            return -1;
        }
    }

}
