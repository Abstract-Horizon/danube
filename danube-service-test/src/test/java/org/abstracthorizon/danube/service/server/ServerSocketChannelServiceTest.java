/*
 * Copyright (c) 2006-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.service.server;

import org.abstracthorizon.danube.test.util.HTTPServiceUtils;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Server socket channel service test case
 *
 * @author Daniel Sendula
 */
public class ServerSocketChannelServiceTest extends ServerSocketServiceTest {

    public void setUp() throws IOException {
        service = HTTPServiceUtils.newMultiThreadSocketChannelService();
        service.create();
        service.start();
    }

    public void testServiceInvoked() throws UnknownHostException, IOException, InterruptedException {
        super.testServiceInvoked();
    }

}
