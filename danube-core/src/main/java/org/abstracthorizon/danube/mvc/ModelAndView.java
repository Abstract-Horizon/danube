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
package org.abstracthorizon.danube.mvc;

import java.util.Map;

/**
 * Simple class that combines model and view.
 * Based on {@link org.springframework.web.servlet.ModelAndView}
 *
 * @author Daniel Sendula
 *
 * @assoc - - - org.abstracthorizon.danube.mvc.Controller
 * @assoc - - - org.abstracthorizon.danube.mvc.View
 */
public class ModelAndView {

    /** View name */
    protected String view;

    /** Map that represents the model */
    protected Map<? extends Object, ? extends Object> model;

    /**
     * Constructor
     * @param view view name
     * @param model map that represents the model
     */
    public ModelAndView(String view, Map<? extends Object, ? extends Object> model) {
        this.view = view;
        this.model = model;
    }

    /**
     * Returns view name
     * @return view name
     */
    public String getView() {
        return view;
    }

    /**
     * Returns model as an map
     * @return model as an map
     */
    public Map<? extends Object, ? extends Object> getModel() {
        return model;
    }
}
