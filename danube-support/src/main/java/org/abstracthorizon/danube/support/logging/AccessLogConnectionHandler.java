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
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.connection.ConnectionWrapper;
import org.abstracthorizon.danube.support.logging.patternsupport.CurrentDateTimeProcessor;
import org.abstracthorizon.danube.support.logging.patternsupport.HandlingTimeProcessor;
import org.abstracthorizon.danube.support.logging.patternsupport.PatternProcessor;
import org.abstracthorizon.danube.support.logging.patternsupport.SocketDetailsProcessor;
import org.abstracthorizon.danube.support.logging.util.LogFileRotator;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This handler implements simple access log.
 * </p>
 *
 * <p>
 * If internally defines following pattern codes (through {@link CurrentDateTimeProcessor},
 * {@link HandlingTimeProcessor} and {@link SocketDetailsProcessor}):
 * </p>
 * <ul>
 * <li><code>%A</code> - local IP address</li>
 * <li><code>%a</code> - remote IP address</li>
 * <li><code>%p</code> - local port number</li>
 * <li><code>%h</code> - remote host name</li>
 * <li><code>%T</code> - connection handling elapsed time in seconds</li>
 * <li><code>%D</code> - connection handling elapsed time in milliseconds</li>
 * <li><code>%t</code> - current time in Common Log Format</li>
 * </ul>
 * <p>
 * Main aim was to keep as much compatibility with already known codes from Apache HTTP server
 * </p>
 *
 * @author Daniel Sendula
 */
public class AccessLogConnectionHandler implements ConnectionHandler {

    /** Logger */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Implementation of log rotator */
    private LogFileRotator logFileRotator;

    /** Connection handler */
    private ConnectionHandler connectionHandler;

    /** Log pattern */
    private String logPattern;

    /** Custom processors' class names */
    private List<String> customProcessors = new ArrayList<String>();

    /** Internal log pattern string */
    protected String logPatternString;

    /** Defined providers */
    protected PatternProcessor[] selectedProcessors;

    /** Number of arguments */
    protected int argumentNumber;

    /**
     * Constructor. It sets log pattern to &quot;%a %h %A %p %t %D %T&quot
     */
    public AccessLogConnectionHandler() {
        String defaultLogPattern = getDefaultLogPattern();
        if (defaultLogPattern != null) {
            setLogPattern(defaultLogPattern);
        }
    }

    /**
     * Returns default log pattern
     * @return default log pattern
     */
    protected String getDefaultLogPattern() {
        return "%a %h %A %p %t %D %T";
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
     * @param connectionHandler conneciton handler
     */
    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    /**
     * Returns a list of custom processors' file names
     * @return a list of custom processors' file names
     */
    public List<String> getCustomProcessors() {
        return customProcessors;
    }

    /**
     * Sets a list of custom processors' file names
     * @param customProcessors a list of custom processors' file names
     */
    public void setCustomProcessors(List<String> customProcessors) {
        this.customProcessors = customProcessors;
    }

    /**
     * Returns log file rotator implementation
     * @return log file rotator implementation
     */
    public LogFileRotator getLogFileRotator() {
        return logFileRotator;
    }

    /**
     * Sets log file rotator implementation
     * @param logFileRotator log file rotator implementation
     */
    public void setLogFileRotator(LogFileRotator logFileRotator) {
        this.logFileRotator = logFileRotator;
    }

    /**
     * Returns log pattern
     * @return log pattern
     */
    public String getLogPattern() {
        return logPatternString;
    }

    /**
     * Sets log pattern
     * @param logPattern log pattern
     */
    public void setLogPattern(String logPattern) {
        this.logPatternString = logPattern;
        StringBuffer message = new StringBuffer(logPattern);

        int index = 0;
        ArrayList<String> providerClasses = new ArrayList<String>(getCustomProcessors());
        addPredefinedProcessors(providerClasses);

        ArrayList<PatternProcessor> providers = new ArrayList<PatternProcessor>();
        for (String className : providerClasses) {
            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Class<?> cls = classLoader.loadClass(className);
                PatternProcessor processor = (PatternProcessor)cls.newInstance();
                int newIndex = processor.init(index, message);
                if (newIndex > index) {
                    providers.add(processor);
                }
                index = newIndex;
            } catch (Exception e) {
                logger.error("Problem with processing provider " + className, e);
            }
        }

        this.argumentNumber = index;
        this.selectedProcessors = new PatternProcessor[providers.size()];
        this.selectedProcessors = providers.toArray(this.selectedProcessors);

        this.logPattern = message.toString();
    }

    /**
     * <p>Adds lists of predefined processors to the lists of provider classes.</p>
     * <p>This method adds following:</p>
     * <ul>
     * <li>{@link HandlingTimeProcessor}</li>
     * <li>{@link CurrentDateTimeProcessor}</li>
     * <li>{@link SocketDetailsProcessor}</li>
     * </ul>
     * @param providerClasses list of provider classes
     */
    protected void addPredefinedProcessors(List<String> providerClasses) {
        if (!providerClasses.contains(HandlingTimeProcessor.class.getName())) {
            providerClasses.add(HandlingTimeProcessor.class.getName());
        }
        if (!providerClasses.contains(CurrentDateTimeProcessor.class.getName())) {
            providerClasses.add(CurrentDateTimeProcessor.class.getName());
        }
        if (!providerClasses.contains(SocketDetailsProcessor.class.getName())) {
            providerClasses.add(SocketDetailsProcessor.class.getName());
        }
    }

    /**
     * Invokes supplied {@link #connectionHandler} measuring time and then
     * writes the log line
     *
     * @param connection connection
     */
    public void handleConnection(Connection connection) {
        long start = System.currentTimeMillis();
        try {
            connectionHandler.handleConnection(connection);
        } finally {
            String logLine = createLogLine(connection, start);
            if (logLine != null) {
                outputLogLine(logLine);
            }
            
        }
    }

    /**
     * This method output log line to the log.
     * @param logLine log line
     */
    protected void outputLogLine(String logLine) {
        try {
            OutputStream out = logFileRotator.logFile();
            synchronized (out) {
                out.write(logLine.getBytes());
                out.write('\r');
                out.write('\n');
            }
        } catch (IOException e) {
            logger.error("Problem writing to access log", e);
            try {
                logFileRotator.rotate();
            } catch (IOException e1) {
                logger.error("Problem rotating access log", e);
            }
        }
    }
    
    /**
     * Creates log line. This method can be overriden to prevent log from happening by returning
     * <code>null</code>.
     * 
     * @param connection connection
     * @param start start time
     * @return log line or <code>null</code> for nothing to be logged
     */
    protected String createLogLine(Connection connection, long start) {
        String logLine;

        if (selectedProcessors.length > 0) {
            DateWrapper dateWrappedConnection = new DateWrapper(connection, start);

            Object[] arguments = new Object[argumentNumber];
            for (PatternProcessor provider : selectedProcessors) {
                provider.process(dateWrappedConnection, arguments);
            }
            logLine = MessageFormat.format(logPattern, arguments);
        } else {
            logLine = logPattern;
        }

        return logLine;
    }
    
    /**
     * Simple connection wrapper that adds handling started time in milliseconds
     *
     * @author Daniel Sendula
     */
    public static class DateWrapper extends ConnectionWrapper {
        /** Handling started time */
        long handlingStarted;

        /**
         * Constructor
         * @param connection connection to be wrapped
         * @param handlingStarted handling started time
         */
        public DateWrapper(Connection connection, long handlingStarted) {
            super(connection);
            this.handlingStarted = handlingStarted;
        }

        /**
         * Returns handling started time
         * @return handling started time
         */
        public long getHandlingStarted() {
            return handlingStarted;
        }

        /**
         * If this class is request than returns this object otherwise calls super method
         * @param cls class to be adapted to
         * @return adapted object or nothing
         */
        @SuppressWarnings("unchecked")
        public <T> T adapt(Class<T> cls) {
            if (cls == DateWrapper.class) {
                return (T)this;
            } else {
                return super.adapt(cls);
            }
        }

    }

}
