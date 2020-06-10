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
package org.abstracthorizon.danube.webdav.java;

import org.abstracthorizon.danube.beanconsole.BeanAccessException;
import org.abstracthorizon.danube.beanconsole.BeanDef;
import org.abstracthorizon.danube.beanconsole.BeanHelper;
import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.http.util.IOUtils;
import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.lock.LockingMechanism;
import org.abstracthorizon.danube.webdav.lock.impl.SimpleInMemoryLockingMechanism;
import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.util.SimpleNamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.RequestProperty;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.DisplayName;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentLength;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentType;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetETag;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.LockDiscovery;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResourceType;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.SupportedLock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Main class in reflection java resource adapter. This resource adapter
 * allows WebDAV to be used in accessing internals of java objects. It
 * exposes collections, maps, array, spring framework application contexts,
 * java bean properties and methods to the WebDAV handler.
 *
 * @author Daniel Sendula
 */
public class JavaWebDAVResourceAdapter implements ResourceAdapter {

    /** Root object */
    protected Object rootObject;

    /** Available property names for files */
    protected static ResponseProperty[] filePropertyNames = new ResponseProperty[]{
        new DisplayName(Status.OK),
        new GetContentLength(Status.OK),
        new GetContentType(Status.OK),
        new GetETag(Status.OK),
        new LockDiscovery(Status.OK),
        new ResourceType(Status.OK),
        new SupportedLock(Status.OK)
    };

    /** Available property names for 'dirs' */
    protected static ResponseProperty[] dirPropertyNames = new ResponseProperty[]{
        new DisplayName(Status.OK),
        new GetETag(Status.OK),
        new LockDiscovery(Status.OK),
        new ResourceType(Status.OK),
        new SupportedLock(Status.OK)
    };

    /** File request propertyes */
    protected static RequestProperty[] fileRequestProperties = new RequestProperty[]{
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.DisplayName(null),
        new org.abstracthorizon.danube.webdav.java.properties.GetContentLength(null),
        new org.abstracthorizon.danube.webdav.java.properties.GetContentType(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetETag(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.LockDiscovery(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.ResourceType(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.SupportedLock(null, null)
    };

    /** Dir request properties */
    protected static RequestProperty[] dirRequestProperties = new RequestProperty[]{
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.DisplayName(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetETag(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.LockDiscovery(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.ResourceType(null),
        new org.abstracthorizon.danube.webdav.xml.dav.request.properties.SupportedLock(null, null)
    };

    /** Namespace provider. It defaults to  {@link SimpleNamespacesProvider}. */
    protected NamespacesProvider namespacesProvider = new SimpleNamespacesProvider();

    /** Locking mechanism. It defaults to {@link SimpleInMemoryLockingMechanism}. */
    protected LockingMechanism lockingMechanism = new SimpleInMemoryLockingMechanism();

    /** DAV namespace. It defaults to {@link DAVNamespace} */
    protected DAVNamespace davNamespace = new DAVNamespace();

    /**
     * Constructor
     */
    public JavaWebDAVResourceAdapter() {
        initHandlers();
    }

    /**
     * Constructor
     * @param rootObject root object
     */
    public JavaWebDAVResourceAdapter(Object rootObject) {
        setRootObject(rootObject);
        initHandlers();
    }

    /**
     * Initialises java dav facotry and sets namespace
     */
    protected void initHandlers() {
        davNamespace.setDAVFactory(new JavaDAVFactory());
        namespacesProvider.addNamespace(davNamespace.getURLString(), davNamespace.getPreferredPrefix(), davNamespace);
    }

    /**
     * Returns root object
     * @return root object
     */
    public Object getRootObject() {
        return rootObject;
    }

    /**
     * Sets root object
     * @param root root object
     */
    public void setRootObject(Object root) {
        this.rootObject = root;
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
     * Finds a resource (object) from the given path. It returns
     * {@link Bean} or extension of that object
     * @return a resource
     */
    public Object findResource(String resourcePath) {
        String name = IOUtils.lastPathComponent(resourcePath);
        int i = name.lastIndexOf('.');
        if (!name.endsWith("]") && (i >= 0)) {
//            String parent = IOUtils.parentPath(resourcePath);
            if (name.equals("object.type")) {
                return new ObjectType(resourcePath);
            } else if (name.equals("object.fields")) {
            } else if (name.equals("object.methods")) {
            } else if (name.equals("object.properties")) {
            } else if (name.equals("object.as.collection")) {
            } else if (name.equals("object.as.map")) {
            } else if (name.endsWith(".field")) {
            } else if (name.endsWith(".property")) {
                return new Property(resourcePath);
            } else if (name.endsWith(".invoke")) {
            } else if (name.equals("index.html")) {
           }
        }
        return new Bean(resourcePath);

    }

    /**
     * This method returns real object from the given bean
     * @param bean bean (defines path)
     * @return an object or <code>null</code> if failed
     */
    protected Object findObjectImpl(Bean bean) {
        String resourcePath = bean.getPath();
//        String name = IOUtils.lastPathComponent(resourcePath);
//        int i = name.indexOf('.');
//        if (!name.endsWith("]") && (i == 0)) {
//            resourcePath = IOUtils.lastPathComponent(resourcePath);
//        }
        return findObjectImpl(resourcePath);
    }

    /**
     * This method returns real object from the given path
     * @param path path
     * @return an object or <code>null</code> if failed
     */
    protected Object findObjectImpl(String resourcePath) {
        Object current = getRootObject();
        if (resourcePath.length() > 0) {
            try {
                current = BeanHelper.navigate(current, resourcePath);
            } catch (BeanAccessException exception) {
                return null;
            }
        }
        return current;
    }

    /**
     * Returns parent resource of the given resource. It takes
     * the path and finds parent of the path and then creates new
     * {@link bean} object (or extension) and returns that. If object
     * is already root objec then it returns <code>null</code>
     *
     * @return parent resource
     */
    public Object findParentResource(Object resource) {
        String path = ((Bean)resource).getPath();
        if ((path == null) || (path.length() == 0)) {
            return null;
        } else {
            int i = path.lastIndexOf('/');
            if (i >= 0) {
                return findResource(path.substring(0, i));
            } else {
                return rootObject;
            }
        }
    }

    /**
     * Returns resource length. It consults {@link Delegate} if resource is of that type.
     * @return resource length
     */
    public long resourceLength(Object resource) {
        if (resource instanceof Delegate) {
            return ((Delegate)resource).resourceLength(this);
        } else {
            return -1;
        }
    }

    /**
     * Returns -1
     * @return -1
     */
    public long resourceLastModified(Object resource) {
        return -1;
    }

    /**
     * Returns -1
     * @return -1
     */
    public long resourceCreated(Object resource) {
        return -1;
    }

    /**
     * Returns resource name from the last portion of the path
     * @return resource name
     */
    public String getResourceName(Object resource) {
        return IOUtils.lastPathComponent(((Bean)resource).getPath());
    }

    /**
     * Returns etag. It consults {@link Delegate} if resource is of that type.
     * @return etag
     */
    public String getResourceETag(Object resource) {
        if (resource instanceof Delegate) {
            return ((Delegate)resource).getResourceETag(this);
        } else {
            Object object = findObjectImpl((Bean)resource);
            if (object != null) {
                String eTag = "W/\"" + Integer.toHexString(System.identityHashCode(object)) + "\"";
                return eTag;
            } else {
                String path = ((Bean)resource).getPath();
                return "W/\"" + path + "-NULL\"";
            }
        }
    }

    /**
     * Returns if resource exists.
     * @return <code>true</code> if resource exists.
     */
    public boolean exists(Object resource) {
        if (resource instanceof Delegate) {
            return true;
        } else {
            return findObjectImpl((Bean)resource) != null;
        }
    }

    /**
     * Returns if resource is collection. It consults {@link Delegate} if resource is of that type.
     * @return <code>true</code> if resource is collection
     */
    public boolean isCollection(Object resource) {
        if (resource instanceof Delegate) {
            return false;
        } else {
            return findObjectImpl((Bean)resource) != null;
        }
    }

    /**
     * Not implemented
     * @param resource resource
     * @throws IOException if there were errors while deleting the resource
     */
    public void delete(Object resource) throws IOException {
    }

    /**
     * Not implemented
     * @param resource resource
     * @throws IOException if there were errors while collection is created
     */
    public void makeCollection(Object resource) throws IOException {
    }

    /**
     * Not implemented
     * @param source source
     * @param destination destination
     * @param recursive deep copy
     * @throws IOException if there were errors while copying resource(s)
     */
    public void copy(Object source, Object destination, boolean recursive) throws IOException {
    }

    /**
     * Not implemented
     * @param source source
     * @param destination destination
     * @throws IOException if there were errors while moving resource(s)
     */
    public void move(Object source, Object destination) throws IOException {
    }

    /**
     * Returns <code>null</code> if resource is delegate. Otherwise
     * returns list of collection, map, arrays, properties, methods, etc..
     * @param resource resource
     * @return object
     */
    @SuppressWarnings("unchecked")
    public Object[] collectionElements(Object resource) {
        if (resource instanceof Delegate) {
            return null;
        }
        Bean bean = (Bean)resource;
        Object object = findObjectImpl(bean);
        if (object == null) {
            return null;
        } else {
            Map<String, Object> result = new HashMap<String, Object>();
            BeanHelper.prepare(object, result);
            ArrayList<Bean> res = new ArrayList<Bean>();
            Set<BeanDef> beans = (Set<BeanDef>)result.get("beans");
            if (beans != null) {
                for (BeanDef beanDef : beans) {
                    if (beanDef.isFollowable()) {
                        Bean b = new Bean(IOUtils.addPaths(bean.getPath(), "[" + beanDef.getName()) + "]");
                        res.add(b);
                    }
                }
            }
            Set<BeanDef> map = (Set<BeanDef>)result.get("map");
            if (map != null) {
                for (BeanDef beanDef : map) {
                    if (beanDef.isFollowable()) {
                        Bean b = new Bean(IOUtils.addPaths(bean.getPath(), "[" + beanDef.getName()) + "]");
                        res.add(b);
                    }
                }
            }
            Set<BeanDef> collection = (Set<BeanDef>)result.get("collection");
            if (collection != null) {
                int i = 0;
                for (BeanDef beanDef : collection) {
                    if (beanDef.isFollowable()) {
                        Bean b = new Bean(IOUtils.addPaths(bean.getPath(), "[" + i + "]"));
                        res.add(b);
                    }
                    i = i + 1;
                }
            }
            Set<BeanDef> props = (Set<BeanDef>)result.get("properties");
            if (props != null) {
                for (BeanDef beanDef : props) {
                    if (beanDef.isFollowable()) {
                        Bean b = new Bean(IOUtils.addPaths(bean.getPath(), beanDef.getName()));
                        res.add(b);
                    }
                }
            }
            res.add(new ObjectType(IOUtils.addPaths(bean.getPath(), "/object.type")));
            if (props != null) {
                for (BeanDef beanDef : props) {
                    Property b = new Property(IOUtils.addPaths(bean.getPath(), beanDef.getName() + ".property"));
                    res.add(b);
                }
            }
            res.add(new Bean(IOUtils.addPaths(bean.getPath(), "index.html")));

            Bean[] r = new Bean[res.size()];
            r = res.toArray(r);
            return r;
        }
    }

    public RequestProperty[] getDefaultRequestProperties(Object resource) {
        if (isCollection(resource)) {
            return dirRequestProperties;
        } else {
            return fileRequestProperties;
        }
    }

    public ResponseProperty[] getDefaultResponseProperties(Object resource) {
        if (isCollection(resource)) {
            return dirPropertyNames;
        } else {
            return filePropertyNames;
        }
    }

    /**
     * Returns input stream. It consults {@link Delegate} if resource is of that type.
     * @return input stream or <code>null</code>
     */
    public InputStream getInputStream(Object resource) {
        if (resource instanceof Delegate) {
            return ((Delegate)resource).getInputStream(this);
        } else {
            return null;
        }
    }

    /**
     * Returns input stream. It consults {@link Delegate} if resource is of that type.
     * @param from offset
     * @param length length
     * @return input stream or <code>null</code>
     */
    public InputStream getInpusStream(Object resource, long from, long length) {
        if (resource instanceof Delegate) {
            return ((Delegate)resource).getInputStream(this, from, length);
        } else {
            return null;
        }
    }

    /**
     * Returns output stream. It consults {@link Delegate} if resource is of that type.
     * @return input stream or <code>null</code>
     */
    public OutputStream getOutputStream(Object resource) {
        if (resource instanceof Delegate) {
            return ((Delegate)resource).getOutputStream(this);
        } else {
            return null;
        }
    }

    /**
     * Returns output stream. It consults {@link Delegate} if resource is of that type.
     * @param from offset
     * @param length length
     * @return output stream or <code>null</code>
     */
    public OutputStream getOutputStream(Object resource, long from, long length) {
        if (resource instanceof Delegate) {
            return ((Delegate)resource).getOutputStream(this, from, length);
        } else {
            return null;
        }
    }

    /**
     * Returns content type. It consults {@link Delegate} if resource is of that type.
     * @return content type or &quot;unknown/unknown&quot;
     */
    public String getContentType(Object resource) {
        if (resource instanceof Delegate) {
            return ((Delegate)resource).getContentType(this);
        } else {
            return "unknown/unknown";
        }
    }

}
