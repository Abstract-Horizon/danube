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
package org.abstracthorizon.danube.webdav;


import java.io.File;

import junit.framework.TestCase;

/**
 * Base class for test cases
 *
 * @author Daniel Sendula
 */
public class WebDAVFileConnectionHandlerTestBase extends TestCase {

    public Environment environment = new Environment();

    public void setUp() throws Exception {
        environment.debug = true;
        environment.setUp();
    }

    public void tearDown() throws Exception {
        environment.tearDown();
    }

    public static void main(String[] args) throws Exception {
        Environment environment = new Environment();
        environment.debug = true;
        environment.port = 8080;
        environment.setUp();
        environment.fileSystemWebDAVAdapter.setFilePath(new File(args[0]));
    }
}
