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
package org.abstracthorizon.danube.http.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class keeping from and to pointers in a file
 *
 * @author Daniel Sendula
 */
public class Ranges {

    /** If this is a single range then it is stored here */
    protected Range singleRange;

    /** List of ranges for multi-range */
    protected List<Range> ranges;

    /** Size of the resource or -1 if unkonwn */
    protected long size = -1;

    /**
     * Constructor
     */
    public Ranges() {
    }

    /**
     * Returns <code>true</code> if this is multi-range
     * @return <code>true</code> if this is multi-range
     */
    public boolean isMultiRange() {
        return ranges != null;
    }

    /**
     * Sets size
     * @param size size
     */
    public void setSize(long size) {
        this.size = size;
        if (isMultiRange()) {
            for (Range range : ranges) {
                updateSize(range);
            }
            makeCanonic();
        } else if (singleRange != null) {
            updateSize(singleRange);
        }
    }

    /**
     * Returns size or -1 if size is unknown
     * @return size or -1 if size is unknown
     */
    public long getSize() {
        return size;
    }

    /**
     * Returns list of ranges
     * @return list of ranges
     */
    public List<Range> getRanges() {
        return ranges;
    }

    /**
     * Returns single range or <code>null</code>
     * @return single range or <code>null</code>
     */
    public Range getSingleRange() {
        return singleRange;
    }

    /**
     * Updates size to prefix and suffix ranges
     * @param range range to be updated
     */
    protected void updateSize(Range range) {
        if (size >= 0) {
            if (range.from == -1) {
                range.from = size - range.to;
                range.to = size - 1;
            }
            if (range.to == -1) {
                range.to = size - 1;
            }
        }
    }

    /**
     * Adds next range
     *
     * @param from from
     * @param to to
     */
    public void addRange(long from, long to) {
        if ((from != -1) || (to != -1)) {
            Range range = new Range(from , to);
            updateSize(range);
            if (singleRange == null) {
                if (ranges == null) {
                    singleRange = range;
                } else {
                    ranges.add(range);
                    makeCanonic();
                }
            } else {
                if (!combine(singleRange, range)) {
                    ranges = new ArrayList<Range>();
                    ranges.add(singleRange);
                    ranges.add(range);
                    singleRange = null;
                }
            }
        }
    }

    /**
     * Tries to combine two ranges. If it succeeds result is in
     * range1 and result is <code>true</code>. Otherwise result is
     * <code>false</code> and ranges are unchanged.
     *
     * @param range1 range one and result in case of success
     * @param range2 range two
     * @return <code>true</code> if two ranges can be combined
     */
    protected boolean combine(Range range1, Range range2) {
        if (range1.from == -1) {
            if (range2.from == -1) {
                if (range1.to < range2.to) {
                    range1.to = range2.to;
                }
                return true;
            } else {
                return false;
            }
        } else if (range1.to == -1) {
            if (range2.to == -1) {
                if (range1.from > range2.from) {
                    range1.from = range2.from;
                }
                return true;
            } else {
                return false;
            }
        } else {
            if (range1.from <= range2.from) {
                if (range1.to <= range2.from) {
                    return false;
                } else {
                    if (range1.to < range2.to) {
                        range1.to = range2.to;
                    }
                    return true;
                }
            } else { // if (range1.from >= range2.from) {
                if (range1.from >= range2.to) {
                    return false;
                } else {
                    if (range1.to < range2.to) {
                        range1.from = range2.from;
                        range1.to = range2.to;
                    } else {
                        range1.from = range2.from;
                    }
                    return true;
                }
            }
        }
    }

    /**
     * Makes this ranges in canonic form (as long as they are sorted
     */
    protected void makeCanonic() {
        if (ranges != null) {
            Collections.sort(ranges, new Comparator<Range>() {
                public int compare(Range r1, Range r2) {
                    if (r1.from < r2.from) {
                        return -1;
                    } else if (r1.from > r2.from) {
                        return 1;
                    } else if (r1.to < r2.to) {
                        return -1;
                    } else if (r1.to > r2.to) {
                        return 1;
                    }
                    return 0;
                }
            });
            int i = 0;
            while (i + 1 < ranges.size()) {
                Range r1 = ranges.get(i);
                Range r2 = ranges.get(i + 1);
                if (combine(r1, r2)) {
                    ranges.remove(i + 1);
                } else {
                    i = i + 1;
                }
            }
        }
    }

    /**
     * Parses input string for ranges format
     * @param ranges input string
     * @return parsed ranges object or <code>null</code> if incorrect format
     */
    public static Ranges parseRange(String ranges) {
        if (ranges.startsWith("bytes=")) {
            Ranges result = new Ranges();
            int j = 6;
            int i = ranges.indexOf(',');
            while (i > 0) {
                if (i > j) {
                    if (!parseOneRange(ranges, j, i, result)) {
                        return null;
                    }
                    j = i + 1;
                    if (j <= ranges.length()) {
                        i = ranges.indexOf(',', j + 1);
                    } else {
                        return result;
                    }
                } else {
                    return null;
                }
            }
            if (!parseOneRange(ranges, j, ranges.length(), result)) {
                return null;
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * Parses input string for ranges format
     * @param ranges input string
     * @return parsed ranges object or <code>null</code> if incorrect format
     */
    public static Ranges parseContentRange(String ranges) {
        if (ranges.startsWith("bytes ")) {
            Ranges result = new Ranges();
            long s = -1;
            int j = 6;
            int i = ranges.indexOf('/');
            if (i >= 0) {
                if (i + 1 >= ranges.length()) {
                    return null;
                }
                if ((i + 2 == ranges.length() && ranges.charAt(i + 1) == '*')) {
                    s = -1;
                } else {
                    s = parseLong(ranges, i+1, ranges.length());
                    if (s == -1) {
                        return null;
                    }
                    result.setSize(s);
                }
            } else {
                i = ranges.length();
            }
            if (!parseOneRange(ranges, j, i, result)) {
                return null;
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * Parses one range in format of &quot;xxx-yyy&quot;, &quot;xxx-&quot; or &quot;-yyy&quot;
     * @param input input string
     * @param from form index
     * @param to to index
     * @param result result object to be populated with newly recognised range
     * @return <code>true</code> if format is recognised, <code>false</code> otherwise
     */
    protected static boolean parseOneRange(String input, int from, int to, Ranges result) {
        long f = -1;
        long t = -1;
        int i = input.indexOf('-', from);
        if ((i < from) || (i > to)) {
            return false;
        }
        if (i > from) {
            f = parseLong(input, from, i);
            if (f < 0) {
                return false;
            }
        }
        if (i + 1 < to) {
            t = parseLong(input, i + 1, to);
            if (t < 0) {
                return false;
            }
        }
        result.addRange(f, t);
        return true;
    }

    /**
     * Parses input string from given index
     * @param input input string
     * @param from index to start parsing from
     * @return <code>true</code> if there is at least one digit
     */
    protected static long parseLong(String input, int from, int to) {
        long res = 0;
        int c = input.charAt(from);
        if (Character.isDigit(c)) {
            res = c - '0';
            from = from + 1;
            while (from < to) {
                c = input.charAt(from);
                if (Character.isDigit(c)) {
                    res = res * 10 + c - '0';
                    from = from + 1;
                } else {
                    return -1;
                }
            }
            return res;
        } else {
            return -1;
        }
    }
    /**
     * Returns string representation
     * @return string represetnation
     */
    public String toString() {
        StringBuffer res = new StringBuffer("Ranges[");
        boolean first = true;
        for (Range range : ranges) {
            if (first) {
                first = false;
            } else {
                res.append(',');
            }
            res.append(range.toString());
        }
        res.append(']');
        return res.toString();
    }

    /**
     * Formats value of this ranges object as specified in &quot;Content-Range&quot;
     * @return value of this ranges object as specified in &quot;Content-Range&quot;
     *         if there is more then one range then return <code>null</code>
     */
    public String format() {
        if (isMultiRange()) {
            return null;
        }
        StringBuffer res = new StringBuffer("bytes ");
        if (singleRange.to == -1) {
            singleRange.to = size -1;
        }
        if (singleRange.from == -1) {
            singleRange.from = 0;
        }
        res.append(singleRange.from).append('-').append(singleRange.to).append('/').append(size);
        return res.toString();
    }

    /**
     * Single range definition
     *
     * @author Daniel Sendula
     */
    public static class Range {

        /** From pointer */
        private long from = -1;

        /** To pointer */
        private long to = -1;

        /**
         * Constructor
         * @param from from
         * @param to to
         */
        public Range(long from, long to) {
            this.from = from;
            this.to = to;
        }

        /**
         * Returns from pointer
         * @return from pointer
         */
        public long getFrom() {
            return from;
        }

        /**
         * Returns to pointer
         * @return to pointer
         */
        public long getTo() {
            return to;
        }

        /**
         * Returns size of the range
         * @return size of the range
         */
        public long getSize() {
            return to - from;
        }

        /**
         * Is this range a suffix range
         * @return is this range a suffix range
         */
        public boolean isSuffix() {
            return from == -1;
        }

        /**
         * Is this range a prefix range
         * @return is this range a prefix range
         */
        public boolean isPrefix() {
            return to == -1;
        }

        /**
         * Returns string representation of this range
         * @return string representation
         */
        public String toString() {
            if (from == -1) {
                return "Range[-" + to + "]";
            } else if (to == -1) {
                return "Range[" + from + "-]";
            } else {
                return "Range[" + from + "-" + to + "]";
            }
        }
    }
}
