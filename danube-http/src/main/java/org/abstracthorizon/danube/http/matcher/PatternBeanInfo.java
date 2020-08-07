/*
 * Copyright (c) 2007-2020 Creative Sphere Limited.
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
package org.abstracthorizon.danube.http.matcher;

/**
 * Bean info for {@link Pattern} class
 *
 * @author Daniel Sendula
 */
public class PatternBeanInfo extends AbstractMatcherBeanInfo {

    /**
     * Constructor
     */
    public PatternBeanInfo() {
        this(Pattern.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected PatternBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        super.init();

        addProperty("pattern", "Pattern as a string");

        addProperty("componentPath", "Component path to be used when matched");

        addProperty("matchAsComponentPath", "Match as component path");

        addProperty("compiledPattern", "Compiled Pattern", true, false);

    }

}
