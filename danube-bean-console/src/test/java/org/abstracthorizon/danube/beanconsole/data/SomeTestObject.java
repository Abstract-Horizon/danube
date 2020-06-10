/*
 * Copyright (c) 2009 Creative Sphere Limited.
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
package org.abstracthorizon.danube.beanconsole.data;

/**
 *
 * @author Daniel Sendula
 */
public class SomeTestObject {

    private String name = "";
    private String readOnly = "";
    
    private boolean enabled = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled2() {
        return false;
    }

    public void setEnabled2(boolean enabled) {
    }
    
    public String getReadOnly() {
        return readOnly;
    }
    
    public void setWriteOnly(String value) {
        this.readOnly = value;
    }
    
    
}
