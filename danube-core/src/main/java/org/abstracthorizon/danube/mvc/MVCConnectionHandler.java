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

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;

import java.io.IOException;

/**
 * Tihs class is driver of MVC. It invokes action that returns view (and modem) and then view
 * renderer is called with that invformation.
 *
 * @author Daniel Sendula
 *
 * @assoc - - - org.abstracthorizon.danube.mvc.ModelAndView
 */
public class MVCConnectionHandler implements ConnectionHandler {

    /** Action manager */
    protected Controller controller;

    /** View renderer */
    protected View view;

    /** Constructor */
    public MVCConnectionHandler() {
    }

    /**
     * This method calls action manager to obtain view and model and then
     * view renderer with that information
     * @param connection connection
     * @throws IOException when there are problems with rendering the view
     */
    public void handleConnection(Connection connection) {
        ModelAndView modelAndView = controller.handleRequest(connection);
        view.render(connection, modelAndView);
    }

    /**
     * Returns action manager
     * @return action manager
     */
    public Controller getController() {
        return controller;
    }

    /**
     * Sets action manager
     * @param controller action manager
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Returns view renderer
     * @return view renderer
     */
    public View getView() {
        return view;
    }

    /**
     * Sets view renderer
     * @param view view renderer
     */
    public void setView(View view) {
        this.view = view;
    }

}
