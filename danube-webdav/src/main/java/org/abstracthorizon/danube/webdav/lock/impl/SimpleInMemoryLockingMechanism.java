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
package org.abstracthorizon.danube.webdav.lock.impl;

import org.abstracthorizon.danube.webdav.lock.Lock;
import org.abstracthorizon.danube.webdav.lock.LockingMechanism;
import org.abstracthorizon.danube.webdav.util.Timeout;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple, in-memory locking mechanism
 *
 * @author Daniel Sendula
 */
public class SimpleInMemoryLockingMechanism implements LockingMechanism {

    /** Map of tokens to locks */
    protected Map<String, Lock> tokenLocks = new HashMap<String, Lock>();

    /** Map of locks to resources */
    protected Map<Lock, Collection<Object>> lockResources = new HashMap<Lock, Collection<Object>>();

    /** Map of resources to locks */
    protected Map<Object, Collection<Lock>> resourcesLocks = new HashMap<Object, Collection<Lock>>();

    /** Minimal interval timed out locks are removed. Defaulted to 10 seconds */
    protected int harvestMinimumInterval = 10000; // 10 seconds

    /** Time locks can be harvesed as the earliest */
    protected long nextHarvested;

    /**
     * Constructor
     */
    public SimpleInMemoryLockingMechanism() {
    }

    /**
     * Creates the lock
     * @param type lock type
     * @param scope lock type
     * @param owner lock owner
     * @param timeout lock timeout
     * @param depth lock depth
     */
    public synchronized Lock createLock(int type, int scope, Object owner, Timeout timeout, int depth) {
        Lock lock = createLockImpl(type, scope, owner, timeout, depth);
        tokenLocks.put(lock.getToken(), lock);
        harvestLocks();
        return lock;
    }

    /**
     * Constructs lock object
     * @param type lock type
     * @param scope lock scope
     * @param owner lock owner
     * @param timeout lock timeout
     * @param depth lock depth
     * @return
     */
    protected Lock createLockImpl(int type, int scope, Object owner, Timeout timeout, int depth) {
        return new Lock(type, scope, owner, timeout, depth);
    }

    /**
     * Retunrs the lock with the given token or <code>null</code>
     * @param token token
     * @return the lock with the given token or <code>null</code>
     */
    public synchronized Lock findLock(String token) {
        return tokenLocks.get(token);
    }

    /**
     * Locks reource
     * @param lock lock
     * @param resource resource
     * @return <code>true</code> if lock succeded
     */
    public synchronized boolean lockResource(Lock lock, Object resource) {
        Collection<Lock> locks = resourcesLocks.get(resource);
        if ((locks != null) && locks.contains(lock)) {
            return false;
        }
        Collection<Object> rs = lockResources.get(lock);
        if (rs == null) {
            rs = new HashSet<Object>();
            lockResources.put(lock, rs);
        }
        rs.add(resource);
        if (locks == null) {
           locks = new HashSet<Lock>();
           resourcesLocks.put(resource, locks);
        }
        locks.add(lock);
        return true;
    }

    /**
     * Unlocks resource
     * @param lock lock
     */
    public synchronized void unlockResources(Lock lock) {
        tokenLocks.remove(lock.getToken());
        removeLock(lock);
    }

    /**
     * Removes locks form the given resource
     * @param resource resource
     */
    public void removeLocks(Object resource) {
        Collection<Lock> locks = resourcesLocks.remove(resource);
        if (locks != null) {
            for (Lock lock : locks) {
                Collection<Object> resources = lockResources.get(lock);
                resources.remove(resource);
                if (resources.size() == 0) {
                    tokenLocks.remove(lock);
                    lockResources.remove(lock);
                }
            }
        }
    }

    /**
     * Returns locks for the given resource
     * @param resource resource
     * @return locks array
     */
    public Lock[] getLocks(Object resource) {
        Collection<Lock> locks = resourcesLocks.get(resource);
        if ((locks != null) && (locks.size() > 0)) {
            Lock[] res = new Lock[locks.size()];
            return locks.toArray(res);
        }
        return null;
    }

    /**
     * Returns an array of resources for the given lock
     * @param lock lock
     * @return an array of resources
     */
    public Object[] getResources(Lock lock) {
        Collection<Object> resources = lockResources.get(lock);
        if ((resources != null) && (resources.size() > 0)) {
            Object[] res = new Object[resources.size()];
            return resources.toArray(res);
        }
        return null;
    }

    /**
     * Returns <code>true</code> if resource is locked
     * @param resource resource
     * @return <code>true</code> if resource is locked
     */
    public synchronized boolean isLocked(Object resource) {
        harvestLocks();
        Collection<Lock> locks = resourcesLocks.get(resource);
        return ((locks != null) && (locks.size() > 0));
    }

    /**
     * Returns <code>true</code> if access to the resource is allowed
     * @param resource resource
     * @param token token
     * @return <code>true</code> if access to the resource is allowed
     */
    public synchronized boolean isAccessAllowed(Object resource, String token) {
        harvestLocks();
        Collection<Lock> locks = resourcesLocks.get(resource);
        if ((locks != null) && (locks.size() > 0)) {
            if (token != null) {
                for (Lock lock : locks) {
                    if (lock.getToken().equals(token)) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns array with {@link LockingMechanism#SCOPE_EXCLUSIVE} and {@link LockingMechanism#SCOPE_SHARED}
     * @return array with {@link LockingMechanism#SCOPE_EXCLUSIVE} and {@link LockingMechanism#SCOPE_SHARED}
     */
    public int[] getSupportedLockScopes(Object resource) {
        return new int[]{LockingMechanism.SCOPE_EXCLUSIVE, LockingMechanism.SCOPE_SHARED};
    }

    /**
     * Removes all locks that are not valid anymore (due to timeout)
     */
    protected synchronized void harvestLocks() {
        long now = System.currentTimeMillis();
        if (nextHarvested  < now) {
            nextHarvested = now + harvestMinimumInterval;

            Iterator<Lock> iterator = tokenLocks.values().iterator();
            while (iterator.hasNext()) {
                Lock lock = iterator.next();
                if (lock.getValidUntil() < now) {
                    iterator.remove();
                    removeLock(lock);
                }
            }

        }
    }

    /**
     * Removes a lock
     * @param lock lock
     */
    protected void removeLock(Lock lock) {
        Collection<Object> resources = lockResources.get(lock);
        if (resources != null) {
            for (Object resource : resources) {
                Collection<Lock> locks = resourcesLocks.get(resource);
                if (locks != null) {
                    locks.remove(lock);
                    if (locks.size() == 0) {
                        resourcesLocks.remove(resource);
                    }
                }
            }
        }
        lockResources.remove(lock);
        lock.freeLock();
    }

    /**
     * Returns minimal harvest interval in miliseconds
     * @return minimal harvest interval in miliseconds
     */
    public int getHarvestMinimumInterval() {
        return harvestMinimumInterval;
    }

    /**
     * Sets minimal harvest interval in miliseconds
     * @return minimal harvest interval in miliseconds
     */
    public void setHarvestMinimumInterval(int harvestMinimumInterval) {
        this.harvestMinimumInterval = harvestMinimumInterval;
    }
}
