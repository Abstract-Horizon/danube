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
package org.abstracthorizon.danube.mvc;

import org.abstracthorizon.danube.connection.Connection;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 *
 * @author Daniel Sendula
 */
public class MVCConnectionHandlerTest {

    private boolean viewRendered = false;

    @Test
    public void testHandler() throws IOException {
        final Connection connection = new Connection() {

            public <T> T adapt(Class<T> cls) {
                // TODO Auto-generated method stub
                return null;
            }

            public void close() {
            }

            public boolean isClosed() {
                return false;
            }

        };

        final ModelAndView modelAndView = new ModelAndView("aview", new HashMap<Object, Object>());

        Controller controller = new Controller() {

            public ModelAndView handleRequest(Connection connection) {
                return modelAndView;
            }

        };

        View view = new View() {
            public void render(Connection inConnection, ModelAndView inModelAndView) {
                Assert.assertTrue(connection == inConnection);
                Assert.assertTrue(modelAndView == inModelAndView);
                viewRendered = true;
            }
        };

        MVCConnectionHandler handler = new MVCConnectionHandler();
        handler.setController(controller);
        handler.setView(view);
        handler.handleConnection(connection);

        Assert.assertTrue(viewRendered);

    }
}
