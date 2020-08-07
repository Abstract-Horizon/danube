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
package org.abstracthorizon.danube.mvc;

import org.abstracthorizon.pasulj.PasuljInfo;

/**
 * Bean info for {@link ModelAndView} class.
 *
 * @author Daniel Sendula
 *
 */
public class ModelAndViewBeanInfo extends PasuljInfo {

    /**
     * Constructor
     */
    public ModelAndViewBeanInfo() {
        this(ModelAndView.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    public ModelAndViewBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        addProperty("view", "Name of the view");
        addProperty("model", "Map that represents the model");
    }

}
