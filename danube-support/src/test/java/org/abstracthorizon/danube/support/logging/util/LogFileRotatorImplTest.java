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
package org.abstracthorizon.danube.support.logging.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Log file rotator implementation test case
 *
 * @author Daniel Sendula
 */
public class LogFileRotatorImplTest extends TestCase {

    public void testRotating() throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        for (int i = 1; i < 10; i++) {
            File n1 = new File(tempDir, "test." + i + ".log");
            n1.createNewFile();
        }
        new File(tempDir, "test.10.log").delete();
        new File(tempDir, "test.11.log").delete();

        File logFile = new File(tempDir, "test.log");
        logFile.createNewFile();
        try {
            LogFileRotatorImpl instance = new LogFileRotatorImpl();
            instance.setLogFile(logFile);
            instance.setNumberOfGenerations(10);

            OutputStream os = instance.logFile();
            os.close();

            File n = new File(tempDir, "test.log");
            Assert.assertTrue("File doesn't exist", n.exists());

            for (int i = 1; i < 10; i++) {
                File n1 = new File(tempDir, "test." + i + ".log");
                Assert.assertTrue("File " + n1.getName() + " Doesn't exist", n1.exists());
            }

            File n10 = new File(tempDir, "test.10.log");
            Assert.assertFalse("File " + n10.getName() + " doesn't exist", !n10.exists());

            File n11 = new File(tempDir, "test.11.log");
            Assert.assertFalse(n11.exists());

        } finally {
            File n = new File(tempDir, "test.log");
            n.delete();

            for (int i = 1; i < 10; i++) {
                File n1 = new File(tempDir, "test." + i + ".log");
                n1.delete();
            }

            File n10 = new File(tempDir, "test.10.log");
            File n11 = new File(tempDir, "test.11.log");
            n10.delete();
            n11.delete();
        }

    }
}
