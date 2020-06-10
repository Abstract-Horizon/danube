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
package org.abstracthorizon.danube.webdav.lock;

import org.abstracthorizon.danube.webdav.util.Timeout;

/**
 * This clas represents a lock
 *
 * @author Daniel Sendula
 */
public class Lock {

    /**
     * Lock type
     * @see LockingMechanism#TYPE_WRITE
     */
    protected int type;

    /**
     * Lock scope
     * @see LockingMechanism#SCOPE_SHARED
     * @see LockingMechanism#SCOPE_EXCLUSIVE
     */
    protected int scope;

    /** Locks owner. May be <code>null</code> */
    protected Object owner;

    /** Locks depth. May be <code>null</code> */
    protected int depth;

    /** Locks timeout */
    protected Timeout timeout;

    /** Lock's token */
    protected String token;

    /** Timestamp until where this lock is valid */
    protected long validUntil = Long.MAX_VALUE;

    /** Static token element counter */
    protected static long counter = 1;

    /**
     * Constructor
     * @param type lock type
     * @param scope lock scope
     * @param owner lock owner
     * @param timeout lock timeout
     * @param depth lock depth
     */
    public Lock(int type, int scope, Object owner, Timeout timeout, int depth) {
        this.type = type;
        this.scope = scope;
        this.owner = owner;
        this.depth = depth;
        this.timeout = timeout;
        createToken();
    }

    /**
     * Creates new lock's token
     */
    protected synchronized void createToken() {
        String t = "opaquelocktoken:" + Long.toHexString(System.currentTimeMillis()) + "-" + Long.toHexString(counter);
        token = t;
        counter = counter + 1;
    }

    /**
     * Returns lock depth
     * @return lock depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets lock depth
     * @param depth lock depth
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Returns lock owner
     * @return lock owner
     */
    public Object getOwner() {
        return owner;
    }

    /**
     * Sets lock owner
     * @param owner lock owner
     */
    public void setOwner(Object owner) {
        this.owner = owner;
    }

    /**
     * Returns lock scope
     * @return lock scope
     */
    public int getScope() {
        return scope;
    }

    /**
     * Sets lock scope
     * @param scope lock scope
     */
    public void setScope(int scope) {
        this.scope = scope;
    }

    /**
     * Returns lock timeout
     * @return lock timeout
     */
    public Timeout getTimeout() {
        return timeout;
    }

    /**
     * Sets lock timeout
     * @param timeout lock timeout
     */
    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
        validUntil = timeout.calculateValidity();
    }

    /**
     * Return's lock token
     * @return lock token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets lock token
     * @param token lock token
     */
    // TODO check if this is needed at all. Remove it!
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Returns lock type
     * @return lock type
     */
    public int getType() {
        return type;
    }

    /**
     * Sets lock type
     * @param type lock type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Returns timestamp until lock is valid
     * @return timestamp until lock is valid
     */
    public long getValidUntil() {
        return validUntil;
    }

    /**
     * Refreshes locks validity by reusing given timeout
     * @param timeout timeout
     * @return <code>true</code>
     */
    public boolean refreshTimeout(Timeout timeout) {
        this.timeout = timeout;
        validUntil = timeout.calculateValidity();
        return true;
    }

    /**
     * Frees lock. This implementation does nothing - it is defined
     * hook point for extensions
     *
     */
    public void freeLock() {
    }

    /**
     * Retunrs <code>true</code> if locks are same (token wise)
     * @return <code>true</code> if locks are same (token wise)
     */
    public boolean equals(Object o) {
        if (o instanceof Lock) {
            Lock l = (Lock)o;
            return token.equals(l.token);
        }
        return false;
    }
}
