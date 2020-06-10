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
package org.abstracthorizon.danube.support.logging;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.support.RuntimeIOException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connection handler that logs connection to a file.
 *
 * @author Daniel Sendula
 */
public class LoggingConnectionHandler implements ConnectionHandler {

    /** Logger */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Connection handler connection to be passed to */
    private ConnectionHandler connectionHandler;

    /** Is logging switched on or off */
    private boolean logging = true;

    /** Is it direction/readable text logging or not */
    private boolean directional = true;

    /** Should all input be logged on the temporary basis */
    private boolean tempLogging = false;

    /** Client socket address pattern string */
    private String addressPatternString;

    /** Client socket address pattern */
    protected Pattern addressPattern;

    /** Path of log files */
    private File logsPath;

    /** Log file name pattern string */
    private String logFileNamePatternString;

    /** Log file name pattern */
    protected String logFileNamePattern;

    /** Should remote host names be resolved for address pattern */
    private boolean resolveRemoteHostNames = false;

    /**
     * Constructor
     */
    public LoggingConnectionHandler() {
        setLogsPath(new File(System.getProperty("java.io.tmpdir")));
        setAddressPattern(".*");
        setLogFileNamePattern("log-%D-%T-%a:%p.log");
    }

    /**
     * Returns address pattern.
     *
     * @return returns address pattern.
     */
    public String getAddressPattern() {
        return addressPatternString;
    }

    /**
     * Sets socket address pattern. Only socket host addresses (or names, see {@link #setResolveRemoteHostNames(boolean))
     * that match this pattern will create log files or temporary log files.
     *
     *
     * @param addressPatternString
     */
    public void setAddressPattern(String addressPatternString) {
        this.addressPatternString = addressPatternString;
        this.addressPattern = Pattern.compile(addressPatternString);
    }

    /**
     * Returns connection handler
     * @return connection handler
     */
    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    /**
     * Sets connection handler
     * @param connectionHandler connection handler
     */
    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    /**
     * Is logging directional or not
     * @return if logging is directional
     */
    public boolean isDirectional() {
        return directional;
    }

    /**
     * Sets for logging to be directional or not
     * @param directional is logging directional or not
     */
    public void setDirectional(boolean directional) {
        this.directional = directional;
    }

    /**
     * Returns log file name pattern
     *
     * @return log file name pattern
     */
    public String getLogFileNamePattern() {
        return logFileNamePatternString;
    }

    /**
     * Sets the log file name pattern. The following
     * pattern codes are supported:
     * <ul>
     * <li><code>%c</code> - current time milliseconds as a long string</li>
     * <li><code>%D</code> - current date</li>
     * <li><code>%T</code> - current time</li>
     * <li><code>%A</code> - local address</li>
     * <li><code>%P</code> - local port</li>
     * <li><code>%a</code> - remote address</li>
     * <li><code>%p</code> - remote port</li>
     * </ul>
     *
     * @return
     */
    public void setLogFileNamePattern(String logFileNamePatternString) {
        this.logFileNamePatternString = logFileNamePatternString;
        this.logFileNamePattern = logFileNamePatternString
                                .replaceAll("%c", "{1,number,#}")
                                .replaceAll("%D", "{0,date,yyyyMMdd}")
                                .replaceAll("%T", "{0,time,HHmmssSSSS}")
                                .replaceAll("%A", "{4}")
                                .replaceAll("%a", "{2}")
                                .replaceAll("%P", "{5,number,#}")
                                .replaceAll("%p", "{3,number,#}")
                                ;
    }

    /**
     * Returns if logging is switched on or off. If it is switched off
     * no logging will occur for current connection
     *
     * @return if logging is switched on or off
     */
    public boolean isLogging() {
        return logging;
    }

    /**
     * Switches logging on or off
     * @param logging <code>true</code> if logging is to be switched on
     */
    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    /**
     * Returns log files path
     * @return log files path
     */
    public File getLogsPath() {
        return logsPath;
    }

    /**
     * Sets log files path
     * @param logsPath log files path
     */
    public void setLogsPath(File logsPath) {
        this.logsPath = logsPath;
    }

    /**
     * Returns if temporary logs be created or not.
     *
     * @return is temporary logging switched on
     */
    public boolean isTempLogging() {
        return tempLogging;
    }

    /**
     * Sets temporary logging. If address is matched then log is going to be
     * permanent. If address is not matched and temporary logging is on then log is going to be
     * created as temporary log. Temporary log is removed at the end of the connection handling unless
     * its state is changed within {@link LoggingConnection} itself.
     *
     *
     * @param tempLogging
     */
    public void setTempLogging(boolean tempLogging) {
        this.tempLogging = tempLogging;
    }

    /**
     * Returns if host names should be resolved or not. It is used in
     * matching remote socket address ({@link #setAddressPattern(String)})
     *
     * @return if host names should be resolved or not.
     */
    public boolean isResolveRemoteHostNames() {
        return resolveRemoteHostNames;
    }

    /**
     * Sets if remote host names are to be resolved or not. It is used in
     * matching remote socket address ({@link #setAddressPattern(String)})
     *
     * @param resolveRemoteHostNames
     */
    public void setResolveRemoteHostNames(boolean resolveRemoteHostNames) {
        this.resolveRemoteHostNames = resolveRemoteHostNames;
    }

    /**
     * This method wrapps connection to logging connection and passes it further.
     * Will connection be wrapped or not depetns on
     * {@link #isLogging()}, {@link #getAddressPattern()} and {@link #isTempLogging()}.
     *
     * @param connection original connection
     */
    public void handleConnection(Connection connection) {
        boolean log = isLogging();
        boolean temporary = false;
        if (log) {
            boolean socketMatched = false;
            Socket socket = (Socket)connection.adapt(Socket.class);
            if (socket != null) {
                String remoteHost = null;
                InetAddress remoteAddress = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress();
                if (isResolveRemoteHostNames()) {
                    remoteHost = remoteAddress.getHostName();
                } else {
                    remoteHost = remoteAddress.getHostAddress();
                }
                socketMatched = addressPattern.matcher(remoteHost).matches();
            }
            if (!socketMatched) {
                if (isTempLogging()) {
                    temporary = true;
                } else {
                    log = false;
                }
            } else {
                temporary = false;
            }
        }

        if (log) {
            OutputStream logOutputStream = createLogOutputStream(connection, temporary);
            LoggingConnection loggingConnection = null;
            try {
                loggingConnection = new LoggingConnection(connection, logOutputStream, directional, temporary);
                loggingConnection.setTemporaryLog(temporary);
                connectionHandler.handleConnection(loggingConnection);
            } finally {
                // Ensure logOutputStream is closed
                closeOutputStream(loggingConnection, logOutputStream);
            }
        } else {
            connectionHandler.handleConnection(connection);
        }
    }

    /**
     * This method creates log output stream.
     * This implementation returns {@link InternalFileOutputStream} but it can be overriden
     * with any other output stream.
     *
     * @param connection original connection
     * @param temporary is file supposed to be temporary or not
     * @return log output stream
     */
    protected OutputStream createLogOutputStream(Connection connection, boolean temporary) {
        String fileName;
        Socket socket = (Socket)connection.adapt(Socket.class);
        Date now = new Date();
        if (socket != null) {
            InetSocketAddress remote = (InetSocketAddress)socket.getRemoteSocketAddress();
            InetSocketAddress local = (InetSocketAddress)socket.getLocalSocketAddress();
            fileName = MessageFormat.format(logFileNamePattern, new Object[]{
                        now,
                        now.getTime(),
                        remote.getHostName(),
                        remote.getPort(),
                        local.getHostName(),
                        local.getPort()
                    }
                );
        } else {
            fileName = MessageFormat.format(logFileNamePattern, new Object[]{
                        System.currentTimeMillis(),
                        null,
                        null,
                        null,
                        null
                    }
                );
        }

        try {
            File file = new File(logsPath, fileName);
            if (logger.isDebugEnabled()) {
                if (temporary) {
                    logger.debug("Creating temporary log file " + file.getAbsolutePath());
                } else {
                    logger.debug("Creating log file " + file.getAbsolutePath());
                }
            }
            FileOutputStream fileOutputStream = new InternalFileOutputStream(file);
            return fileOutputStream;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * This method closes output stream and then checks if it needs to be removed or not.
     *
     * @param loggingConnection logging connection
     * @param logOutputStream log output stream
     */
    protected void closeOutputStream(LoggingConnection loggingConnection, OutputStream logOutputStream) {
        try {
            logOutputStream.close();
        } catch (IOException ignore) {
        }
        if ((loggingConnection == null) || loggingConnection.isTermporaryLog()) {
            if (logOutputStream instanceof InternalFileOutputStream) {
                InternalFileOutputStream fileOutputStream = (InternalFileOutputStream)logOutputStream;
                File file = fileOutputStream.getFile();
                if (logger.isDebugEnabled()) {
                    logger.debug("Removing temporary log file " + file.getAbsolutePath());
                }
                file.delete();
            }
        }
    }

    /**
     * This is helper class that adds reference to original {@link File} class
     * passed in {@link FileOutputStream}.
     *
     * @author Daniel Sendula
     */
    public static class InternalFileOutputStream extends FileOutputStream {

        /** File */
        protected File file;

        /**
         * Constructor
         * @param file file
         * @throws FileNotFoundException file not found exception
         */
        public InternalFileOutputStream(File file) throws FileNotFoundException {
            super(file);
            this.file = file;
        }

        /**
         * Returns reference to original file object this stream is created with.
         *
         * @return reference to original file object this stream is created with
         */
        public File getFile() {
            return file;
        }

    }
}
