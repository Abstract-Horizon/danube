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
package org.abstracthorizon.danube.webdav.xml.dav.response.properties;

import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.webdav.xml.XMLRenderer;

public abstract class ResponseProperty implements XMLRenderer {

    /** Status 200 OK - {@link Status#OK */
    protected Status status = Status.OK;

    /**
     * Constructor
     */
    public ResponseProperty() {
    }

    /**
     * Constructor
     * @param status status
     */
    public ResponseProperty(Status status) {
        this.status = status;
    }

    /**
     * Returns status
     * @return status
     */
    public Status getStatus() {
        return status;
    }

}
