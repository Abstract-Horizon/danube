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
package org.abstracthorizon.danube.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * This class is insipired by the similar from eclipse project
 *
 * @author Daniel Sendula
 */
public class AdapterManager implements AdapterFactory {

    /** Parent adapter manager */
    private AdapterManager parentAdapterManager;

    /** List of factories */
    private Collection<AdapterFactory> adapterFactories = new ArrayList<AdapterFactory>();

    /**
     * Constructor
     */
    public AdapterManager() {
    }

    /**
     * Constructor
     * @param parentAdapterManager parent adapter manager
     */
    public AdapterManager(AdapterManager parentAdapterManager) {
        this.parentAdapterManager = parentAdapterManager;
    }

    /**
     * Returns parent adapter manager
     * @return parent adapter manager
     */
    public AdapterManager getParentAdapterManager() {
        return parentAdapterManager;
    }

    /**
     * Sets parent adapter manager
     * @param parentAdapterManager parent adapter manager
     */
    public void setParentAdapterManager(AdapterManager parentAdapterManager) {
        this.parentAdapterManager = parentAdapterManager;
    }

    /**
     * Returns collection of adapter factories
     * @return collection of adapter factories
     */
    public Collection<AdapterFactory> getAdapterFactories() {
        return adapterFactories;
    }

    /**
     * Sets the collection of adapter factories
     * @param adapterFactories collection of adapter factories
     */
    public void setAdapterFactories(Collection<AdapterFactory> adapterFactories) {
        this.adapterFactories = adapterFactories;
    }

    /**
     * Adopts given object to the instance of the asked class
     * @param object object to he adopted
     * @param cls asked class
     * @return adopted given object to the instance of the asked class
     */
    public <T> T adapt(T object, Class<T> cls) {
        if (adapterFactories != null) {
            // TODO this can be optimised by caching what classes are available by
            // what adapters...
            for (AdapterFactory adapterFactory : adapterFactories) {
                T res = adapterFactory.adapt(object, cls);
                if (res != null) {
                    return res;
                }
            }
        }
        if (parentAdapterManager != null) {
            return parentAdapterManager.adapt(object, cls);
        } else {
            return null;
        }
    }

    /**
     * Returns list of classes to which given object can be adopted to by this adopter factory
     * @return list of classes to which given object can be adopted to
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T>[] getAdaptingClasses(T object) {
        HashSet<Class<T>> classes = new HashSet<Class<T>>();
        if (adapterFactories != null) {
            for (AdapterFactory adapterFactory : adapterFactories) {
                Class<T>[] clss = adapterFactory.getAdaptingClasses(object);
                for (Class<T> c : clss) {
                    classes.add(c);
                }
            }
        }
        if (parentAdapterManager != null) {
            Class<T>[] clss = parentAdapterManager.getAdaptingClasses(object);
            for (Class<T> c : clss) {
                classes.add(c);
            }
        }
        Class<T>[] res = new Class[classes.size()];
        res = classes.toArray(res);
        return res;
    }

}
