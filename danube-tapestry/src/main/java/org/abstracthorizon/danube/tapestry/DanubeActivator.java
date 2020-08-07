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

import org.apache.tapestry.describe.DescriptionReceiver;
import org.apache.tapestry.web.WebActivator;

/**
 * {@link WebActivator} interface implementation
 *
 *
 * @author Daniel Sendula
 */
public class DanubeActivator implements WebActivator {

    /** Handler */
    protected TapestryConnectionHandler handler;

    /**
     * Constructor
     * @param handler handler
     */
    public DanubeActivator(TapestryConnectionHandler handler) {
        this.handler = handler;
    }

    /**
     * Returns &quot;Danube&quot;
     * @return &quot;Danube&quot;
     */
    public String getActivatorName() {
        return "Danube";
    }

    /**
     * Returns an empty list
     * @return an empty list
     */
    public List<String> getInitParameterNames() {
        ArrayList<String> names = new ArrayList<String>(handler.getInitialParameters().keySet());
        return names;
    }

    /**
     * Returns <code>null</code>
     * @param name parameter name
     * @return <code>null</code>
     */
    public String getInitParameterValue(String name) {
        return handler.getInitialParameters().get(name);
    }

    /**
     * Does nothing
     */
    public void describeTo(DescriptionReceiver receiver) {
    }

}
