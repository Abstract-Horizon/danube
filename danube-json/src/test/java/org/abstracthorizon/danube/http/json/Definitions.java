
/*
 * Copyright (c) 2005-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.http.json;

/**
 * This bean represents a definition of an object or a property
 *
 * @author Daniel Sendula
 */
public class Definitions {

    /** Constant describing no access */
    public static final String NO_ACCESS = "-";

    /** Constant describing read only access through bean info */
    public static final String RO = "RO";

    /** Constant describing read/write access through bean info */
    public static final String RW = "RW";

    /** Constant describing write only access through bean info */
    public static final String WO = "WO";

    /** Constant describing read/write access through reflection (no property editors for the type) */
    public static final String RW_RAW = "rw";

    /** Constant describing read only access through reflection (no property editors for the type) */
    public static final String RO_RAW = "ro";

    /** Constant describing write only access through reflection (no property editors for the type) */
    public static final String WO_RAW = "wo";

    /** Max value size */
    public static final int MAX_VALUE_SIZE = 70;

    /** Max type size */
    public static final int MAX_TYPE_SIZE = 16;
}
