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
 * This interface describes locking mechanism used for WebDAV locking
 *
 * @author Daniel Sendula
 */
public interface LockingMechanism {

    /** No scope defined */
    int SCOPE_NONE = 0;

    /** Shared lock */
    int SCOPE_SHARED = 1;

    /** Exclusive lock */
    int SCOPE_EXCLUSIVE = 2;

    /** Write lock */
    int TYPE_WRITE = 0;

    /**
     * Creates lock
     * @param type lcok type
     * @param scope lock scope
     * @param owner lock owner
     * @param timeout lock timeout
     * @param depth lock depth
     * @return newly created lock
     */
    Lock createLock(int type, int scope, Object owner, Timeout timeout, int depth);

    /**
     * Finds a lock or returns <code>null</code>
     * @param token lock token
     * @return a lock or <code>null</code>
     */
    Lock findLock(String token);

    /**
     * Locks a resource
     * @param lock lock
     * @param resource resource
     * @return <code>true</code> if locking succeded
     */
    boolean lockResource(Lock lock, Object resource);

    /**
     * Unlocks all resource defined by this lock
     * @param lock lock
     */
    void unlockResources(Lock lock);

    /**
     * Removes a lock form the resource
     * @param resource resource
     */
    void removeLocks(Object resource);

    /**
     * Returns all locks on the resource or <code>null</code>
     * @param resource resource
     * @return all locks on the resource or <code>null</code>
     */
    Lock[] getLocks(Object resource);

    /**
     * Returns all resources locked by the given lock
     * @param lock lock
     * @return all resources locked by the given lock
     */
    Object[] getResources(Lock lock);

    /**
     * Checks if the resource is locked
     * @param resource resource
     * @return <code>true</code> if the resource is locked
     */
    boolean isLocked(Object resource);

    /**
     * Checks if resource is accessible if token is supplied
     * @param resource resource
     * @param token token, may be <code>null</code>
     * @return <code>true</code> if not locked or locked by the lock with givne token
     */
    boolean isAccessAllowed(Object resource, String token);

    /**
     * Returns an array of supported lock scopes on a resource
     * @param resource resource
     * @return an array of supported lock scopes
     */
    int[] getSupportedLockScopes(Object resource);

}
