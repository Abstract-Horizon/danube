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
package org.abstracthorizon.danube.http;

import org.abstracthorizon.pasulj.PasuljInfo;

/**
 * Bean info for {@link Selector} class
 *
 * @author Daniel Sendula
 */
public class SelectorBeanInfo extends PasuljInfo {

     /**
      * Constructor
      */
     public SelectorBeanInfo() {
         this(Selector.class);
     }

     /**
      * Constructor
      * @param cls class
      */
     protected SelectorBeanInfo(Class<?> cls) {
         super(cls);
     }

     public void init() {
         addProperty("components", "List of org.abstracthorizon.danube.http.matcher.Matcher objects", false, false);
         addProperty("errorResponse", "Error response connection handler", true, false);
     }
}
