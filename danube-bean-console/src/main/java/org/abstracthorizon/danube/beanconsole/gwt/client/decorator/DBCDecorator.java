/*
 * Copyright (c) 2009 Creative Sphere Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *   Criterion Design Concepts  - initial API and implementation
 *   
 */
package org.abstracthorizon.danube.beanconsole.gwt.client.decorator;

import org.abstracthorizon.danube.beanconsole.gwt.client.MainConsole;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * An abstract decorator class which must be extended by any new decorator classes
 *
 * @author Mile Lukic
 */
public abstract class DBCDecorator {
    
    /**
     *  Must be implemented to add the main console component of the Danube Bean Console to the 
     *  Layout component in which the GUI elements have been added 
     */
    public abstract void addConsoleComponent(MainConsole console);
    
    /**
     * Must be implemented to add the status bar component to the footer Layout containing 
     * the footer GUI.
     */
    public abstract void addStatusBar(HLayout status);
    
    /**
     * Must be implemented to get a reference to the layout containing the app components
     */
    public abstract VLayout getContainer();

}
