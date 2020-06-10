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
package org.abstracthorizon.danube.http.util;

import org.junit.Assert;

import junit.framework.TestCase;

public class RangesTest extends TestCase {

    public void testRange() {
        Ranges ranges = Ranges.parseRange("bytes=10-20");
        Assert.assertNotNull(ranges);
        if (ranges != null) {
            Assert.assertEquals(10L, ranges.getSingleRange().getFrom());
            Assert.assertEquals(20L, ranges.getSingleRange().getTo());
            ranges.setSize(30);
            Assert.assertEquals("bytes 10-20/30", ranges.format());
        }
    }

    public void testSuffixRange() {
        Ranges ranges = Ranges.parseRange("bytes=-20");
        Assert.assertNotNull(ranges);
        if (ranges != null) {
            Assert.assertEquals(-1L, ranges.getSingleRange().getFrom());
            Assert.assertEquals(20L, ranges.getSingleRange().getTo());
            ranges.setSize(30);
            Assert.assertEquals("bytes 10-29/30", ranges.format());
        }
    }

    public void testPrefixRange() {
        Ranges ranges = Ranges.parseRange("bytes=10-");
        Assert.assertNotNull(ranges);
        if (ranges != null) {
            Assert.assertEquals(10L, ranges.getSingleRange().getFrom());
            Assert.assertEquals(-1L, ranges.getSingleRange().getTo());
            ranges.setSize(30);
            Assert.assertEquals("bytes 10-29/30", ranges.format());
        }
    }

    public void testBadRanges() {
        Ranges ranges = Ranges.parseRange("bytes=w10-20");
        Assert.assertNull(ranges);

        ranges = Ranges.parseRange("bytes=1w0-20");
        Assert.assertNull(ranges);

        ranges = Ranges.parseRange("bytes=10w-20");
        Assert.assertNull(ranges);


        ranges = Ranges.parseRange("bytes=10-w20");
        Assert.assertNull(ranges);

        ranges = Ranges.parseRange("bytes=,10-20");
        Assert.assertNull(ranges);
    }

    public void testMultipleRanges() {
        Ranges ranges = Ranges.parseRange("bytes=10-20,30-40");
        Assert.assertNotNull(ranges);
        if (ranges != null) {
            Assert.assertEquals(2, ranges.getRanges().size());
            ranges.setSize(50);
            Assert.assertEquals(10L, ranges.getRanges().get(0).getFrom());
            Assert.assertEquals(20L, ranges.getRanges().get(0).getTo());
            Assert.assertEquals(30L, ranges.getRanges().get(1).getFrom());
            Assert.assertEquals(40L, ranges.getRanges().get(1).getTo());
            Assert.assertNull(ranges.format());
        }

        ranges = Ranges.parseRange("bytes=-20,30-40");
        Assert.assertNotNull(ranges);
        if (ranges != null) {
            Assert.assertEquals(2, ranges.getRanges().size());
            Assert.assertEquals(-1L, ranges.getRanges().get(0).getFrom());
            Assert.assertEquals(20L, ranges.getRanges().get(0).getTo());
            Assert.assertEquals(30L, ranges.getRanges().get(1).getFrom());
            Assert.assertEquals(40L, ranges.getRanges().get(1).getTo());
            ranges.setSize(50);
            Assert.assertNull(ranges.format());
        }

        ranges = Ranges.parseRange("bytes=10-,30-40");
        Assert.assertNotNull(ranges);
        if (ranges != null) {
            Assert.assertEquals(2, ranges.getRanges().size());
            Assert.assertEquals(10L, ranges.getRanges().get(0).getFrom());
            Assert.assertEquals(-1L, ranges.getRanges().get(0).getTo());
            Assert.assertEquals(30L, ranges.getRanges().get(1).getFrom());
            Assert.assertEquals(40L, ranges.getRanges().get(1).getTo());
            ranges.setSize(50);
            Assert.assertNull(ranges.format());
        }

        ranges = Ranges.parseRange("bytes=10-20,-40");
        Assert.assertNotNull(ranges);
        if (ranges != null) {
            Assert.assertFalse("Is multirange", ranges.isMultiRange());
            Assert.assertEquals(-1L, ranges.getSingleRange().getFrom());
            Assert.assertEquals(40L, ranges.getSingleRange().getTo());
            ranges.setSize(50);
            Assert.assertEquals("bytes 10-49/50", ranges.format());
        }

        ranges = Ranges.parseRange("bytes=10-20,30-");
        Assert.assertNotNull(ranges);
        if ((ranges != null) && (ranges.getRanges().size() == 2)) {
            Assert.assertEquals(10L, ranges.getRanges().get(0).getFrom());
            Assert.assertEquals(20L, ranges.getRanges().get(0).getTo());
            Assert.assertEquals(30L, ranges.getRanges().get(1).getFrom());
            Assert.assertEquals(-1L, ranges.getRanges().get(1).getTo());
            ranges.setSize(50);
            Assert.assertNull(ranges.format());
        }

        ranges = Ranges.parseRange("bytes=10-,-40");
        Assert.assertNotNull(ranges);
        if ((ranges != null) && (ranges.getRanges().size() == 2)) {
            Assert.assertTrue("Is multirange", ranges.isMultiRange());
            Assert.assertEquals(10L, ranges.getRanges().get(0).getFrom());
            Assert.assertEquals(-1L, ranges.getRanges().get(0).getTo());
            Assert.assertEquals(-1L, ranges.getRanges().get(1).getFrom());
            Assert.assertEquals(40L, ranges.getRanges().get(1).getTo());
            ranges.setSize(50);
            Assert.assertNull(ranges.format());
        }

        ranges = Ranges.parseRange("bytes=10-,-40,50-");
        Assert.assertNotNull(ranges);
        if ((ranges != null) && (ranges.getRanges().size() == 3)) {
            Assert.assertEquals(-1L, ranges.getRanges().get(0).getFrom());
            Assert.assertEquals(40L, ranges.getRanges().get(0).getTo());
            Assert.assertEquals(50L, ranges.getRanges().get(1).getFrom());
            Assert.assertEquals(-1L, ranges.getRanges().get(1).getTo());
            ranges.setSize(50);
            Assert.assertNull(ranges.format());
        }

        ranges = Ranges.parseRange("bytes=10-,30-,-60");
        Assert.assertNotNull(ranges);
        if ((ranges != null) && (ranges.getRanges().size() == 3)) {
            Assert.assertEquals(10L, ranges.getRanges().get(0).getFrom());
            Assert.assertEquals(-1L, ranges.getRanges().get(0).getTo());
            Assert.assertEquals(30L, ranges.getRanges().get(1).getFrom());
            Assert.assertEquals(-1L, ranges.getRanges().get(1).getTo());
            Assert.assertEquals(-1L, ranges.getRanges().get(2).getFrom());
            Assert.assertEquals(60L, ranges.getRanges().get(2).getTo());
            ranges.setSize(50);
            Assert.assertNull(ranges.format());
        }
    }

}
