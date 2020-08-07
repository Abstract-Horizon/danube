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
package org.abstracthorizon.danube.support.logging;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionWrapper;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Logging connection
 *
 * @author Daniel Sendula
 */
public class LoggingConnection extends ConnectionWrapper {

    /** Wrapped input stream */
    protected LoggingInputStream inputStream;

    /** Wrapped output stream */
    protected LoggingOutputStream outputStream;

    /** Log output stream */
    protected OutputStream logOutputStream;

    /** Is logggin switched on or not */
    private boolean logging = true;

    /** Is log demporary or not */
    private boolean temporaryLog = true;

    /**
     * Constructor.
     *
     * @param connection wrapped connection
     * @param logOutputStream log output stream
     * @param directional is logging directional
     * @param temporaryLog is log temporary log
     */
    public LoggingConnection(Connection connection, OutputStream logOutputStream, boolean directional, boolean temporaryLog) {
        super(connection);
        this.logOutputStream = logOutputStream;
        this.temporaryLog = temporaryLog;
        InputStream originalInputStream = (InputStream)connection.adapt(InputStream.class);
        OutputStream originalOutputStream = (OutputStream)connection.adapt(OutputStream.class);
        if (directional) {
            inputStream = new DirectionalLoggingInputStream(originalInputStream, logOutputStream);
            outputStream = new DirectionalLoggingOutputStream(originalOutputStream, logOutputStream);
        } else {
            inputStream = new LoggingInputStream(originalInputStream, logOutputStream);
            outputStream = new LoggingOutputStream(originalOutputStream, logOutputStream);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T adapt(Class<T> cls) {
        if (cls == InputStream.class) {
            return (T)getInputStream();
        } else if (cls == OutputStream.class) {
            return (T)getOutputStream();
        } else if (cls == LoggingInputStream.class) {
            return (T)this;
        }
        return super.adapt(cls);
    }

    /**
     * Retruns logged input stream
     * @return logged input stream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Returns logged output stream
     * @return logged output stream
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Returns logger output stream. It is useful for
     * making other log statements directly to it.
     * @return logger output stream
     */
    public OutputStream getDebugOutputStream() {
        return logOutputStream;
    }

    /**
     * Turns logging on or off
     * @param logging set to <code>true</code> if logging is switched on
     */
    public void setLogging(boolean logging) {
        this.logging = logging;
        inputStream.setLogging(logging);
        outputStream.setLogging(logging);
    }

    /**
     * Returns if logging is switched on or off
     * @return <code>true<code> logging is switched on or off
     */
    public boolean isLogging() {
        return logging;
    }

    /**
     * Sets flag if log is temporary or not
     * @param temporaryLog is log temporary or not
     */
    public void setTemporaryLog(boolean temporaryLog) {
        this.temporaryLog = temporaryLog;
    }

    /**
     * Returns is logging temporary or not
     * @return is logging temporary or not
     */
    public boolean isTermporaryLog() {
        return temporaryLog;
    }

}
