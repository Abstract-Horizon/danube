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

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Daniel Sendula
 */
public class EntryTest {
    
    @Test
    public void testEntryValueEscape() {
        Entry entry = new Entry();
//        entry.setValue(null);
//        Assert.assertEquals(null, entry.getEscapedValue());
        
        
        entry.setValue("");
        Assert.assertEquals("", entry.getEscapedValue());
        
        entry.setValue("1");
        Assert.assertEquals("1", entry.getEscapedValue());
        
        entry.setValue("1234567890");
        Assert.assertEquals("1234567890", entry.getEscapedValue());
        
        entry.setValue("\"1234567890");
        Assert.assertEquals("\\\"1234567890", entry.getEscapedValue());
        
        entry.setValue("1234567890\"");
        Assert.assertEquals("1234567890\\\"", entry.getEscapedValue());
        
        entry.setValue("1234567890\"1234567890");
        Assert.assertEquals("1234567890\\\"1234567890", entry.getEscapedValue());
        
        entry.setValue("1234567890\"1234567890\"");
        Assert.assertEquals("1234567890\\\"1234567890\\\"", entry.getEscapedValue());
        
        entry.setValue("\"1234567890\"1234567890");
        Assert.assertEquals("\\\"1234567890\\\"1234567890", entry.getEscapedValue());
        
        entry.setValue("\"1234567890\"1234567890\"");
        Assert.assertEquals("\\\"1234567890\\\"1234567890\\\"", entry.getEscapedValue());
        
        entry.setValue("123\"1234567890\"1234567890\"123");
        Assert.assertEquals("123\\\"1234567890\\\"1234567890\\\"123", entry.getEscapedValue());
        
        entry.setValue("\\1234567890");
        Assert.assertEquals("\\\\1234567890", entry.getEscapedValue());
        
        entry.setValue("1234567890\\");
        Assert.assertEquals("1234567890\\\\", entry.getEscapedValue());
        
        entry.setValue("1234567890\\1234567890");
        Assert.assertEquals("1234567890\\\\1234567890", entry.getEscapedValue());
        
        entry.setValue("1234567890\\1234567890\\");
        Assert.assertEquals("1234567890\\\\1234567890\\\\", entry.getEscapedValue());
        
        entry.setValue("\\1234567890\\1234567890");
        Assert.assertEquals("\\\\1234567890\\\\1234567890", entry.getEscapedValue());
        
        entry.setValue("\\1234567890\\1234567890\\");
        Assert.assertEquals("\\\\1234567890\\\\1234567890\\\\", entry.getEscapedValue());
        
        entry.setValue("123\\1234567890\\1234567890\\123");
        Assert.assertEquals("123\\\\1234567890\\\\1234567890\\\\123", entry.getEscapedValue());
        
        entry.setValue("123\"1234567890\\1234567890\"123");
        Assert.assertEquals("123\\\"1234567890\\\\1234567890\\\"123", entry.getEscapedValue());
    }
}
