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
import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.http.util.MultiStringHashMap;
import org.abstracthorizon.danube.http.util.MultiStringMap;
import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.lock.Lock;
import org.abstracthorizon.danube.webdav.lock.LockingMechanism;
import org.abstracthorizon.danube.webdav.lock.impl.SimpleInMemoryLockingMechanism;
import org.abstracthorizon.danube.webdav.xml.dav.request.properties.RequestProperty;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test case for IF header
 *
 * @author Daniel Sendula
 */
public class IFTest extends TestCase {

    protected Object mainResource;
    protected WebDAVResourceAdapterStub webDAVResourceAdapterStub;
    protected HTTPConnectionStub httpConnectionStub;

    public void setUp() {
        mainResource = new Object();

        webDAVResourceAdapterStub = new WebDAVResourceAdapterStub();

        httpConnectionStub = new HTTPConnectionStub();

        webDAVResourceAdapterStub.getResources().put("/path", mainResource);

        webDAVResourceAdapterStub.getResources().put("/path2", new Object());
    }

    public void testPositive() throws Exception {

        Lock lock = webDAVResourceAdapterStub.getLockingMechanism().createLock(LockingMechanism.TYPE_WRITE, LockingMechanism.SCOPE_EXCLUSIVE, "me", Timeout.INFINITE, Depth.ZERO);
        webDAVResourceAdapterStub.getLockingMechanism().lockResource(lock, mainResource);

        String testHeader = "(<" + lock.getToken() + "> [" + webDAVResourceAdapterStub.getResourceETag(mainResource) + "]) ([\"I am another ETag\"])";
        httpConnectionStub.getRequestHeaders().putOnly("If", testHeader);

        IF lockDetails = new IF();
        lockDetails.parse(httpConnectionStub, webDAVResourceAdapterStub, mainResource, testHeader);
        Assert.assertEquals(lock.getToken(), lockDetails.token);
    }

    public void testPositive2() throws Exception {

        Lock lock = webDAVResourceAdapterStub.getLockingMechanism().createLock(LockingMechanism.TYPE_WRITE, LockingMechanism.SCOPE_EXCLUSIVE, "me", Timeout.INFINITE, Depth.ZERO);
        webDAVResourceAdapterStub.getLockingMechanism().lockResource(lock, mainResource);

        String testHeader = "(<wrongtoken>) (<" + lock.getToken() + "> [" + webDAVResourceAdapterStub.getResourceETag(mainResource) + "]) ([\"I am another ETag\"])";
        httpConnectionStub.getRequestHeaders().putOnly("If", testHeader);

        IF lockDetails = new IF();
        lockDetails.parse(httpConnectionStub, webDAVResourceAdapterStub, mainResource, testHeader);
        Assert.assertEquals(lock.getToken(), lockDetails.token);
    }

    public void testNegative() throws Exception {

        Lock lock = webDAVResourceAdapterStub.getLockingMechanism().createLock(LockingMechanism.TYPE_WRITE, LockingMechanism.SCOPE_EXCLUSIVE, "me", Timeout.INFINITE, Depth.ZERO);
        webDAVResourceAdapterStub.getLockingMechanism().lockResource(lock, mainResource);

        String testHeader = "(<wrongtoken>) (<spoil" + lock.getToken() + "> [" + webDAVResourceAdapterStub.getResourceETag(mainResource) + "]) ([\"I am another ETag\"])";
        httpConnectionStub.getRequestHeaders().putOnly("If", testHeader);

        IF lockDetails = new IF();
        lockDetails.parse(httpConnectionStub, webDAVResourceAdapterStub, mainResource, testHeader);
        Assert.assertNull("Null", lockDetails.token);
    }

    public void testNegative2() throws Exception {

        Lock lock = webDAVResourceAdapterStub.getLockingMechanism().createLock(LockingMechanism.TYPE_WRITE, LockingMechanism.SCOPE_EXCLUSIVE, "me", Timeout.INFINITE, Depth.ZERO);
        webDAVResourceAdapterStub.getLockingMechanism().lockResource(lock, mainResource);

        String testHeader = "(<wrongtoken>) (<" + lock.getToken() + "> [spoil" + webDAVResourceAdapterStub.getResourceETag(mainResource) + "]) ([\"I am another ETag\"])";
        httpConnectionStub.getRequestHeaders().putOnly("If", testHeader);

        IF lockDetails = new IF();
        boolean ok = lockDetails.parse(httpConnectionStub, webDAVResourceAdapterStub, mainResource, testHeader);
        Assert.assertFalse("Expected false", ok);
    }

// TODO check this!
//    public void testExplicitSameResource() throws Exception {
//
//        Object resource = webDAVResourceAdapterStub.findResource("/path2");
//
//        Lock lock = webDAVResourceAdapterStub.getLockingMechanism().createLock(LockingMechanism.TYPE_WRITE, LockingMechanism.SCOPE_EXCLUSIVE, "me", Timeout.INFINITE, Depth.ZERO);
//        webDAVResourceAdapterStub.getLockingMechanism().lockResource(lock, resource);
//
//        String testHeader = "</path2> (<wrongtoken>) (<" + lock.getToken() + "> [" + webDAVResourceAdapterStub.getResourceETag(resource) + "]) ([\"I am another ETag\"])";
//        httpConnectionStub.getRequestHeaders().putOnly("If", testHeader);
//
//        IF lockDetails = new IF();
//        boolean ok = lockDetails.parse(httpConnectionStub, webDAVResourceAdapterStub, mainResource, testHeader);
//        Assert.assertEquals(lock.getToken(), lockDetails.token);
//    }

    public void testExplicitDifferentResource() throws Exception {

        Object resource = webDAVResourceAdapterStub.findResource("/path2");

        Lock lock = webDAVResourceAdapterStub.getLockingMechanism().createLock(LockingMechanism.TYPE_WRITE, LockingMechanism.SCOPE_EXCLUSIVE, "me", Timeout.INFINITE, Depth.ZERO);
        webDAVResourceAdapterStub.getLockingMechanism().lockResource(lock, resource);

        String testHeader = "</path2> (<wrongtoken>) (<" + lock.getToken() + "> [" + webDAVResourceAdapterStub.getResourceETag(resource) + "]) ([\"I am another ETag\"])";
        httpConnectionStub.getRequestHeaders().putOnly("If", testHeader);

        IF lockDetails = new IF();
        lockDetails.parse(httpConnectionStub, webDAVResourceAdapterStub, mainResource, testHeader);
        Assert.assertNull("Expected null", lockDetails.token);
    }

    public void testExplicitBothResource() throws Exception {

        Object resource1 = webDAVResourceAdapterStub.findResource("/path");
        Object resource2 = webDAVResourceAdapterStub.findResource("/path2");

        Lock lock1 = webDAVResourceAdapterStub.getLockingMechanism().createLock(LockingMechanism.TYPE_WRITE, LockingMechanism.SCOPE_EXCLUSIVE, "me", Timeout.INFINITE, Depth.ZERO);
        webDAVResourceAdapterStub.getLockingMechanism().lockResource(lock1, resource1);

        Lock lock2 = webDAVResourceAdapterStub.getLockingMechanism().createLock(LockingMechanism.TYPE_WRITE, LockingMechanism.SCOPE_EXCLUSIVE, "me", Timeout.INFINITE, Depth.ZERO);
        webDAVResourceAdapterStub.getLockingMechanism().lockResource(lock2, resource2);

        String testHeader = "</path2> (<wrongtoken>) (<" + lock2.getToken() + "> [" + webDAVResourceAdapterStub.getResourceETag(resource2) + "])"
            + " ([\"I am another ETag\"])"
            + " </path> (<" + lock1.getToken() + ">)";
        httpConnectionStub.getRequestHeaders().putOnly("If", testHeader);

        IF lockDetails = new IF();
        lockDetails.parse(httpConnectionStub, webDAVResourceAdapterStub, mainResource, testHeader);
        Assert.assertEquals(lock1.getToken(), lockDetails.token);
    }


    public static class HTTPConnectionStub implements HTTPConnection {

        public MultiStringMap requestHeaders = new MultiStringHashMap();

        public HTTPConnectionStub() {

        }

        public void addComponentPathToContextPath() {
            // TODO Auto-generated method stub

        }

        public void forward(String path) {
            // TODO Auto-generated method stub

        }

        public Map<String, Object> getAttributes() {
            // TODO Auto-generated method stub
            return null;
        }

        public int getBufferSize() {
            // TODO Auto-generated method stub
            return 0;
        }

        public String getComponentPath() {
            // TODO Auto-generated method stub
            return null;
        }

        public String getComponentResourcePath() {
            // TODO Auto-generated method stub
            return null;
        }

        public String getContextPath() {
            // TODO Auto-generated method stub
            return null;
        }

        public MultiStringMap getRequestHeaders() {
            return requestHeaders;
        }

        public String getRequestMethod() {
            // TODO Auto-generated method stub
            return null;
        }

        public MultiStringMap getRequestParameters() {
            // TODO Auto-generated method stub
            return null;
        }

        public String getRequestPath() {
            // TODO Auto-generated method stub
            return null;
        }

        public String getRequestProtocol() {
            // TODO Auto-generated method stub
            return null;
        }

        public String getRequestURI() {
            // TODO Auto-generated method stub
            return null;
        }

        public MultiStringMap getResponseHeaders() {
            // TODO Auto-generated method stub
            return null;
        }

        public String getResponseProtocol() {
            // TODO Auto-generated method stub
            return null;
        }

        public Status getResponseStatus() {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isCommited() {
            // TODO Auto-generated method stub
            return false;
        }

        public void reset() {
            // TODO Auto-generated method stub

        }

        public void setBufferSize(int size) {
            // TODO Auto-generated method stub

        }

        public void setComponentPath(String requestURI) {
            // TODO Auto-generated method stub

        }

        public void setComponentResourcePath(String resourcePath) {
            // TODO Auto-generated method stub

        }

        public void setResponseProtocol(String protocol) {
            // TODO Auto-generated method stub

        }

        public void setResponseStatus(Status status) {
            // TODO Auto-generated method stub

        }

        public void close() {
            // TODO Auto-generated method stub

        }

        public boolean isClosed() {
            // TODO Auto-generated method stub
            return false;
        }

        public <T> T adapt(Class<T> arg0) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public static class WebDAVResourceAdapterStub implements ResourceAdapter {

        protected LockingMechanism lockingMechanism = new SimpleInMemoryLockingMechanism();

        protected Map<String, Object> resources = new HashMap<String, Object>();

        public WebDAVResourceAdapterStub() {

        }

        public Map<String, Object> getResources() {
            return resources;
        }

        public Object[] collectionElements(Object resource) {
            // TODO Auto-generated method stub
            return null;
        }

        public void copy(Object source, Object destination, boolean recursive) {
        }

        public void delete(Object resource) {
        }

        public boolean exists(Object resource) {
            // TODO Auto-generated method stub
            return false;
        }

        public Object findResource(String path) {
            return resources.get(path);
        }

        public Object findParentResource(Object resource) {
            return null;
        }

        public RequestProperty[] getDefaultRequestProperties(Object resource) {
            // TODO Auto-generated method stub
            return null;
        }

        public ResponseProperty[] getDefaultResponseProperties(Object resource) {
            // TODO Auto-generated method stub
            return null;
        }

        public InputStream getInpusStream(Object resource, long from, long length) {
            // TODO Auto-generated method stub
            return null;
        }

        public InputStream getInputStream(Object resource) {
            // TODO Auto-generated method stub
            return null;
        }

        public LockingMechanism getLockingMechanism() {
            return lockingMechanism;
        }

        public NamespacesProvider getNamespacesProvider() {
            // TODO Auto-generated method stub
            return null;
        }

        public OutputStream getOutputStream(Object resource) {
            // TODO Auto-generated method stub
            return null;
        }

        public OutputStream getOutputStream(Object resource, long from, long length) {
            // TODO Auto-generated method stub
            return null;
        }

        public String getResourceETag(Object resource) {
            return resource.toString();
        }

        public String getResourceName(Object resource) {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isCollection(Object resource) {
            // TODO Auto-generated method stub
            return false;
        }

        public void makeCollection(Object resource) {
        }

        public void move(Object source, Object destination) {
        }

        public long resourceCreated(Object resource) {
            // TODO Auto-generated method stub
            return 0;
        }

        public long resourceLastModified(Object resource) {
            // TODO Auto-generated method stub
            return 0;
        }

        public long resourceLength(Object resource) {
            // TODO Auto-generated method stub
            return 0;
        }

    }

}
