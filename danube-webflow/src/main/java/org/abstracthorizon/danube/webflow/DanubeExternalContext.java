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
package org.abstracthorizon.danube.webflow;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.session.Session;
import org.springframework.binding.collection.SharedMap;
import org.springframework.binding.collection.SharedMapDecorator;
import org.springframework.binding.collection.StringKeyedMapAdapter;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.LocalParameterMap;
import org.springframework.webflow.core.collection.LocalSharedAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;

//import org.springframework.webflow.AttributeMap;
//import org.springframework.webflow.ExternalContext;
//import org.springframework.webflow.ParameterMap;
//import org.springframework.webflow.SharedAttributeMap;
//import org.springframework.webflow.SharedMap;
//import org.springframework.webflow.context.ExternalContext;
//import org.springframework.webflow.context.SharedMapDecorator;
//import org.springframework.webflow.context.StringKeyedMapAdapter;

/**
 * Danube Spring WebFLow's external context adapter.
 *
 * @author Daniel Sendula
 */
public class DanubeExternalContext implements ExternalContext {

    /** Connection reference */
    protected HTTPConnection connection;

    /** Reference to the controller */
    protected DanubeFlowController controller;

    /**
     * Constructor
     * @param controller reference to the controller
     * @param connection reference to the current connection
     */
    public DanubeExternalContext(DanubeFlowController controller, HTTPConnection connection) {
        this.controller = controller;
        this.connection = connection;
    }

    /**
     * Returns connection's context path
     * @return connection's context path
     */
    public String getContextPath() {
        return connection.getContextPath();
    }

    /**
     * Returns connection's component path
     * @return connection's component path
     */
    public String getDispatcherPath() {
        return connection.getComponentPath();
    }

    /**
     * Returns connection's component resource path
     * @return connection's component resource path
     */
    public String getRequestPathInfo() {
        return connection.getComponentResourcePath();
    }

    /**
     * Returns request parameter map adapter from connection's request paramaters
     * @return request parameter map adapter
     */
    public ParameterMap getRequestParameterMap() {
        return new LocalParameterMap(connection.getRequestParameters().getAsMap());
    }

    /**
     * Returns connection's attributes as an attribute map
     * @return connection's attributes
     */
//    public AttributeMap getRequestMap() {
//        return new LocalAttributeMap(connection.getAttributes());
//    }

    /**
     * Returns new {@link SessionMap} instance
     * @return new {@link SessionMap} instance
     */
//    public SharedAttributeMap getSessionMap() {
//        return new LocalSharedAttributeMap(SharedMap new SessionMap());
//    }

    /**
     * Returns controller's attributes properly decorated and adapted
     * @return controller's attributes
     */
//    public SharedAttributeMap getApplicationMap() {
//        return new SharedAttributeMap(new SharedMapDecorator(controller.getAttributes()));
//    }

    /**
     * Session map adapter
     */
    public class SessionMap extends StringKeyedMapAdapter implements SharedMap {

        /**
         * Constructor
         *
         */
        public SessionMap() {
        }

        /**
         * Returns controller's session manager's session if there is one. <code>null</code> otherwise.
         * @return controller's session manager's session
         */
        protected Session getSession() {
            return (Session)controller.getSessionManager().findSession(connection, false);
        }

        /**
         * Returns session's attribute if session is available
         * @return session's attribute or <code>null</code> if session or attribute is not available
         */
        protected Object getAttribute(String key) {
            Session session = getSession();
            if (session == null) {
                return null;
            } else {
                return session.getAttributes().get(key);
            }
        }

        /**
         * Sets session attribute. If session is not already created it is going to be created now.
         * @param key attribute key
         * @param value attribute value
         */
        protected void setAttribute(String key, Object value) {
            Session session = (Session)controller.getSessionManager().findSession(connection, true);
            session.getAttributes().put(key, value);
        }

        /**
         * Removes attribute. This method won't create new session if there isn't one already there
         * @param key attribute's key
         */
        protected void removeAttribute(String key) {
            Session session = getSession();
            if (session != null) {
                session.getAttributes().remove(key);
            }
        }

        /**
         * Returns enumeration of all attribute names. This method won't create new session if there isn't one already there
         * @return enumeration of all attribute names
         */
        protected Iterator<String> getAttributeNames() {
            Session session = getSession();
            if (session != null) {
                return session.getAttributes().keySet().iterator();
            } else {
                List<String> list = Collections.emptyList();
                return list.iterator();
            }
        }

        /**
         * Returns session. If there wasn't a session created already new one will be created when this
         * method is called.
         * @return session
         */
        public Object getMutex() {
            Session session = (Session)controller.getSessionManager().findSession(connection, true);
            return session;
        }
    }

    /**
     * Enumeration adapter over itereator
     */
    public static class EnumerationIteratorAdapter<T> implements Enumeration<T> {

        /** Iterator to be adapted */
        protected Iterator<T> iterator;

        /**
         * Constructor
         * @param iterator iterator to be adapted
         */
        public EnumerationIteratorAdapter(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        /**
         * Returns {@link Iterator#hasNext()}
         * @return {@link Iterator#hasNext()}
         */
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        /**
         * Returns {@link Iterator#next()}
         * @return {@link Iterator#next()}
         */
        public T nextElement() {
            return iterator.next();
        }
    }

    public SharedAttributeMap getApplicationMap() {
        // TODO Auto-generated method stub
        return new LocalSharedAttributeMap(new SharedMapDecorator(new HashMap<Object, Object>()));
    }

    public SharedAttributeMap getGlobalSessionMap() {
        // TODO Auto-generated method stub
        return new LocalSharedAttributeMap(new SharedMapDecorator(new HashMap<Object, Object>()));
    }

    public MutableAttributeMap getRequestMap() {
        // TODO Auto-generated method stub
        return new LocalSharedAttributeMap(new SharedMapDecorator(new HashMap<Object, Object>()));
    }

    public SharedAttributeMap getSessionMap() {
        // TODO Auto-generated method stub
        return new LocalSharedAttributeMap(new SharedMapDecorator(new HashMap<Object, Object>()));
    }

}
