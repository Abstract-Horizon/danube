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
package org.abstracthorizon.danube.webdav.fs;

import org.abstracthorizon.danube.webdav.lock.Lock;
import org.abstracthorizon.danube.webdav.lock.impl.SimpleInMemoryLockingMechanism;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Locking mechanism that locks files on the file system by using
 * {@link RandomAccessFile} to open the file with &quot;rw&quot;
 * attributes. Unlocking is done by closing that file.
 * </p>
 *
 * <p>
 * Note: So far it is confirmed that it works only on Windows machines
 * </p>
 *
 * @author Daniel Sendula
 */
public class FSLockingMechanism extends SimpleInMemoryLockingMechanism {

    /** Map of locks */
    protected Map<Object, RandomAccessFile> fsLocks = new HashMap<Object, RandomAccessFile>();

    /**
     * Constructor
     */
    public FSLockingMechanism() {
    }

    /**
     * Locks the file
     * @param lock lock
     * @param resource a file
     * @return <code>true</code> if file is locked
     */
    @Override
    public synchronized boolean lockResource(Lock lock, Object resource) {
        if (super.lockResource(lock, resource)) {
            try {
                File file = (File)resource;
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                fsLocks.put(resource, raf);
            } catch (IOException ignore) {
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Unlocks the file
     * @param lock lock
     */
    public synchronized void unlockResources(Lock lock) {
        Collection<Object> resources = lockResources.get(lock);
        if ((resources != null) && (resources.size() > 0)) {
            for (Object resource : resources) {
                RandomAccessFile raf = fsLocks.remove(resource);
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
        super.unlockResources(lock);
    }
}
