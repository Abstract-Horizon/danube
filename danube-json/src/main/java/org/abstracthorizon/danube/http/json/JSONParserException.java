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
package org.abstracthorizon.danube.http.json;

/**
 *
 * @author Daniel Sendula
 */
public class JSONParserException extends RuntimeException {

    public JSONParserException() {
    }
    
    public JSONParserException(String msg) {
        super(msg);
    }
    
    public JSONParserException(Throwable cause) {
        super(cause);
    }
    
    public JSONParserException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
}
