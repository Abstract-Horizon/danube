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
package org.abstracthorizon.danube.http.logging;

import org.abstracthorizon.danube.support.logging.AccessLogConnectionHandler;

import java.util.List;

/**
 * <p>
 * Utility class that adds new pattern codes to existing in {@link AccessLogConnectionHandler}
 * </p>
 * <ul>
 * <li><code>%A</code> - local IP address</li>
 * </ul>
 * <p>
 * Those are added through {@link HTTPPatternProcessor}
 * </p>
 * <p>
 * Also it sets default pattern to &quot;%h %l %u %t &quot;%r&quot; %s %b&quot;
 * </p>
 * @author Daniel Sendula
 */
public class HTTPAccessLogConnectionHandler extends AccessLogConnectionHandler {

    /**
     * <p>Adds lists of predefined processors to the lists of provider classes.</p>
     * <p>This method adds following:</p>
     * <ul>
     * <li>{@link HTTPPatternProcessor}</li>
     * </ul>
     * <p>Also it calls super method {@link AccessLogConnectionHandler#addPredefinedProcessors(List)}</p>
     *
     * @param providerClasses list of provider classes
     */
    protected void addPredefinedProcessors(List<String> providerClasses) {
        super.addPredefinedProcessors(providerClasses);
        if (!providerClasses.contains(HTTPPatternProcessor.class.getName())) {
            providerClasses.add(HTTPPatternProcessor.class.getName());
        }
    }


    /**
     * Returns default log pattern
     * @return default log pattern
     */
    protected String getDefaultLogPattern() {
        return "%h %l %u %t \"%r\" %s %b";
    }


}
