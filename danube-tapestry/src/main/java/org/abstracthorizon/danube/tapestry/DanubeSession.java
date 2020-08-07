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
package org.abstracthorizon.danube.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.abstracthorizon.danube.http.session.Session;
import org.apache.tapestry.describe.DescriptionReceiver;
import org.apache.tapestry.web.WebSession;

/**
 * {@link WebSession} interface implementation
 *
 * @author Daniel Sendula
 */
public class DanubeSession implements WebSession {

    /** Attribute in session this object is going to be stored */
    public static final String SESSION_ATTRIBUTE = "org.abstracthorizon.danube.tapestry.WebSession";

    /** Danube's session */
    protected Session session;

    /** Flag to denote if the session is new */
    protected boolean isNew = true;

    /**
     * Constructor
     * @param session danube's session
     */
    public DanubeSession(Session session) {
        this.session = session;
        session.getAttributes().put(SESSION_ATTRIBUTE, this);
    }

    /**
     * Returns session id
     * @return session id
     */
    public String getId() {
        return session.getSessionId();
    }

    /**
     * Returns <code>true</code> if session is newly created
     * @return <code>true</code> if session is newly created
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * Clears {@link #isNew} flag.
     */
    protected void clearNew() {
        isNew = false;
    }

    /**
     * Invalidates session. It removes this object from danube's session.
     */
    public void invalidate() {
        session.getAttributes().remove(SESSION_ATTRIBUTE);
    }

    /**
     * Returns attributes names as a list
     * @return attributes names as a list
     */
    public List<String> getAttributeNames() {
        ArrayList<String> names = new ArrayList<String>(session.getAttributes().keySet());
        return names;
    }

    /**
     * Returns attribute
     * @param key attribute name
     * @return attribute's value
     */
    public Object getAttribute(String key) {
        return session.getAttributes().get(key);
    }

    /**
     * Sets attribute
     * @param key attribute's name
     * @param value attribute's value
     */
    public void setAttribute(String key, Object value) {
        session.getAttributes().put(key, value);
    }

    /**
     * Does nothing
     */
    public void describeTo(DescriptionReceiver descriptionReceiver) {
    }

}
