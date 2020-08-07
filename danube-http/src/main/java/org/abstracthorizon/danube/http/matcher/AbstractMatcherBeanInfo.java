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

import org.abstracthorizon.pasulj.PasuljInfo;

/**
 * Bean info for {@link AbstractMatcher} class
 *
 * @author Daniel Sendula
 */
public class AbstractMatcherBeanInfo extends PasuljInfo {

    /**
     * Constructor
     */
    public AbstractMatcherBeanInfo() {
        this(AbstractMatcher.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected AbstractMatcherBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {

        addProperty("connectionHandler", "Connection handler");
        addProperty("stopOnMatch", "Stop going through other matchers after this is matched. Default is true.");

    }

}
