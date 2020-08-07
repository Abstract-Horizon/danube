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
package org.abstracthorizon.danube.beanconsole.util;

import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;

/**
 * Property editor for char arrays
 *
 * @author Daniel Sendula
 */
public class CharArrayPropertyEditor extends PropertyEditorSupport {


    public void init() {
        char[] type = new char[1];
        PropertyEditorManager.registerEditor(type.getClass() , getClass());
    }


    /**
     * Converts string to char array
     * @param text string to be converted
     */
    public void setAsText(String text) {
        setValue(text.toCharArray());
    }
}
