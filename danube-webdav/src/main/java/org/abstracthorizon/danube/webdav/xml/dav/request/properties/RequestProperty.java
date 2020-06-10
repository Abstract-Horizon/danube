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
package org.abstracthorizon.danube.webdav.xml.dav.request.properties;

import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.request.AbstractSimpleXMLHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract request property class. It defines default behaviour of undefined properties.
 *
 * @author Daniel Sendula
 */
public abstract class RequestProperty extends AbstractSimpleXMLHandler {

    /** Logger */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /** Constructor argument classes - {@link Status} */
    protected static final Class<?>[] CONSTRUCTOR_ARGUMENTS_CLASSES = new Class[]{Status.class};

    /** This package name */
    protected static final String packageName = ResponseProperty.class.getPackage().getName();

    /**
     * Constructor
     * @param parent parent parser handler
     */
    public RequestProperty(XMLParserHandler parent) {
        super(parent);
    }

    /**
     * Constructs response property based on supplied status
     * @param status status
     * @return response property from this package
     */
    @SuppressWarnings("unchecked")
    protected ResponseProperty constructResponse(Status status) {
        try {
            String clsName = getClass().getName();
            int i = clsName.lastIndexOf('.');
            clsName = packageName + '.' + clsName.substring(i + 1);
            Class<ResponseProperty> cls = (Class<ResponseProperty>)Class.forName(clsName);
            Constructor<ResponseProperty> constructor = cls.getConstructor(CONSTRUCTOR_ARGUMENTS_CLASSES);
            ResponseProperty responseProperty = constructor.newInstance(new Object[]{status});
            return responseProperty;
        } catch (SecurityException e) {
            logger.debug("", e);
        } catch (NoSuchMethodException e) {
            logger.debug("", e);
        } catch (IllegalArgumentException e) {
            logger.debug("", e);
        } catch (InstantiationException e) {
            logger.debug("", e);
        } catch (IllegalAccessException e) {
            logger.debug("", e);
        } catch (InvocationTargetException e) {
            logger.debug("", e);
        } catch (ClassNotFoundException e) {
            logger.debug("", e);
        }
        return null;
    }

    /**
     * Returns response property with {@link Status#NOT_FOUND} status
     * @param adapter adpater
     * @param resource resource
     * @return response property with {@link Status#NOT_FOUND} status
     */
    public ResponseProperty processResponse(ResourceAdapter adapter, Object resource) {
        return constructResponse(Status.NOT_FOUND);
    }

    /**
     * Returns response property with {@link Status#CONFLICT} status
     * @param adapter adpater
     * @param resource resource
     * @return response property with {@link Status#CONFLICT} status
     */
    public ResponseProperty processSetProperty(ResourceAdapter adapter, Object resource) {
        return constructResponse(Status.CONFLICT);
    }

    /**
     * Returns response property with {@link Status#CONFLICT} status
     * @param adapter adpater
     * @param resource resource
     * @return response property with {@link Status#CONFLICT} status
     */
    public ResponseProperty processRemoveProperty(ResourceAdapter webDAVAdapter, Object resource) {
        return constructResponse(Status.CONFLICT);
    }

}
