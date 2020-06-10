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
package org.abstracthorizon.danube.support.logging.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Simple implementation of {@link LogFileRotator} interface. This implementation
 * knows how to keep certain number of generations of log files,
 * to rotate logs when size exceeds or age of file (age is calculated since file
 * is created by an instance of this class) or at one predefined moment during the day.
 * </p>
 * <p>
 * Each time this class is instantiated log rotation is enforced. That is done
 * in the lazy manner - first call to {@link #logFile()} method will do so.
 * </p>
 * @author Daniel Sendula
 */
public class LogFileRotatorImpl implements LogFileRotator {

    /** Logger */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Cached output stream */
    protected OutputStream cachedOutputStream;

    /** Log file */
    protected File logFile;

    /** Log file path */
    protected File logPath;

    /** Number of generations. -1 if unlimited or 0 if no generations of the file to be kept */
    private int numberOfGenerations = -1;

    /** Maximum size of the file in bytes or -1 if no maximum is defined */
    private long maxSize = -1;

    /** Maximum age of the fime in milliseconds or -1 if no maximum age is defined */
    private long maxAge = -1;

    /** Time of the day in the form of a calendar. May be null */
    private Calendar timeOfDay = null;

    /**
     * Amount of time before check for log rotation is made. This is performance optimisation.
     * Default is 100
     */
    private int checkDelayMillis = 100;

    /** When is this object lass accessed through {@link #logFile()} method. */
    protected long lastAccessed;

    /** When is scheduled next rotation based on the time of the day or 0 if none */
    protected long nextRotate;

    /** When is log file created */
    protected long logFileCreated;

    /** Buffer len for output file or -1 if none. */
    private int bufferLen = -1;

    /**
     * Constructor
     */
    public LogFileRotatorImpl() {
    }

    /**
     * Constructor
     *
     * @param logFile log file handle
     */
    public LogFileRotatorImpl(File logFile) {
        this.setLogFile(logFile);
    }

    /**
     * This method first checks if we have accessed it too quickly since
     * last time according to the {@link #checkDelayMillis} field. If so
     * check should log be rotated or not wont be done.
     *
     * @returns output stream of the log file
     * @throws IOException io exception
     */
    public synchronized OutputStream logFile() throws IOException {
        if ((System.currentTimeMillis() - lastAccessed) > checkDelayMillis) {
            lastAccessed = System.currentTimeMillis();
            check();
        }
        return cachedOutputStream;
    }

    /**
     * Returns file handle of the log file.
     * @return file handle of the log file
     */
    public File getLogFile() {
        return logFile;
    }

    /**
     * Sets file handle of the log file
     * @param logFile file handle of the log file
     */
    public void setLogFile(File logFile) {
        this.logFile = logFile;
        this.logPath = logFile.getParentFile();
    }

    /**
     * Returns the direcotry of log file.
     * .
     * @return the direcotry of log file
     * @see #setLogDirectory(File)
     */
    public File getLogDirectory() {
        return logPath;
    }

    /**
     * Sets log directory. This is only convenience setter to be used
     * in conjuction with {@link #setLogFileName(String)} instead
     * of {@link #setLogFile(File)}. There is no
     * need using these two (this and {@link #setLogFileName(String)} in
     * log file is set through {@link #setLogFile(File)}.
     * Settin only log directory will assume log file name
     * as &quot;access.log&quot. If you want
     * to set log directory and use different name use {@link #setLogFileName(String)} to
     * set the name after you have
     * set the directory using this method
     *
     * @param logPath log file directory
     */
    public void setLogDirectory(File logPath) {
        this.logPath = logPath;
        this.logFile = new File(logPath, "access.log");
    }

    /**
     * Returns last part of log file's path
     * @return last part of log file's path
     */
    public String getLogFileName() {
        return logFile.getName();
    }

    /**
     * Sets last part of a path of log file. Seet {@link #setLogDirectory(File)} for more explanations.
     * @param name
     */
    public void setLogFileName(String name) {
        this.logFile = new File(logPath, name);
    }

    /**
     * Returns maximum age of the file.
     *
     * @see #maxAge
     * @return maximum age of the file
     */
    public long getMaxAge() {
        return maxAge;
    }

    /**
     * Sets maximum age of the file
     *
     * @see #maxAge
     * @param maxAge maximum age of the file
     */
    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }


    /**
     * Returns maximum size of the file.
     *
     * @see #maxSize
     * @return maximum size of the file
     */
    public long getMaxSize() {
        return maxSize;
    }

    /**
     * Sets maximum size of the file
     *
     * @see #maxSize
     * @param maxAge maximum size of the file
     */
    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Returns defined number of generations
     *
     * @see #numberOfGenerations
     * @return defined number of generations
     */
    public int getNumberOfGenerations() {
        return numberOfGenerations;
    }

    /**
     * Sets defined number of generations
     *
     * @see #numberOfGenerations
     * @param numberOfGeneration defined number of generations
     */
    public void setNumberOfGenerations(int numberOfGeneration) {
        this.numberOfGenerations = numberOfGeneration;
    }

    /**
     * Returns time of day when rotation is going to happen.
     *
     * @see #timeOfDay
     * @return time of day when rotation is going to happen
     */
    public Calendar getTimeOfDay() {
        return timeOfDay;
    }

    /**
     * Sets time of day when rotation is going to happen
     *
     * @see #timeOfDay
     * @param timeOfDay time of day when rotation is going to happen
     */
    public void setTimeOfDay(Calendar timeOfDay) {
        this.timeOfDay = timeOfDay;
        if (timeOfDay == null) {
            nextRotate = 0;
        } else {
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(Calendar.HOUR, timeOfDay.get(Calendar.HOUR));
            cal.set(Calendar.MINUTE, timeOfDay.get(Calendar.MINUTE));
            cal.set(Calendar.SECOND, timeOfDay.get(Calendar.SECOND));
            cal.set(Calendar.MILLISECOND, timeOfDay.get(Calendar.MILLISECOND));
            cal.add(Calendar.DAY_OF_YEAR, 1);
            nextRotate = cal.getTimeInMillis();
        }
    }

    /**
     * Returns check delay in milliseconds
     *
     * @see #checkDelayMillis
     * @return check delay in milliseconds
     */
    public int getCheckDelayMillis() {
        return checkDelayMillis;
    }

    /**
     * Sets check delay in milliseconds
     *
     * @see #checkDelayMillis
     * @param checkDelayMillis check delay in milliseconds
     */
    public void setCheckDelayMillis(int checkDelayMillis) {
        this.checkDelayMillis = checkDelayMillis;
    }

    /**
     * Returns file buffer len
     *
     * @see #bufferLen
     * @return file buffer len
     */
    public int getBufferLen() {
        return bufferLen;
    }

    /**
     * Sets file buffer len
     *
     * @see #bufferLen
     * @param bufferLen file buffer len
     */
    public void setBufferLen(int bufferLen) {
        this.bufferLen = bufferLen;
    }

    /**
     * Checks if file needs to be rotated
     *
     * @throws IOException io exception
     */
    protected void check() throws IOException {
        boolean doRotate = false;
        if (cachedOutputStream == null) {
            doRotate = true;
        }
        if (!doRotate) {
            long maxAge = getMaxAge();
            if ((maxAge > 0) && ((lastAccessed - logFileCreated) > maxAge)) {
                doRotate = true;
            }
        }
        if (!doRotate) {
            long maxSize = getMaxSize();
            if ((maxSize > 0) && (getLogFile().length()) > maxSize) {
                doRotate = true;
            }
        }
        if (!doRotate) {
            if ((nextRotate > 0) && (lastAccessed > nextRotate)) {
                doRotate = true;
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTimeInMillis(nextRotate);
                cal.add(Calendar.DAY_OF_YEAR, 1);
                nextRotate = cal.getTimeInMillis();
            }
        }
        if (doRotate) {
            rotate();
        }
    }

    /**
     * Rotates the log files
     *
     * @throws IOException io exception
     */
    public void rotate() throws IOException {
        if (cachedOutputStream != null) {
            try {
                cachedOutputStream.close();
            } catch (IOException e) {
                logger.error("Failed to close previous file", e);
            }
        }
        ArrayList<File> files = new ArrayList<File>();
        int i = 1;
        File f = createFileName(i);
        while (f.exists()) {
            if ((numberOfGenerations >= 0) && (i >= numberOfGenerations)) {
                f.delete();
            }
            if ((numberOfGenerations < 0) || (i <= numberOfGenerations)) {
                files.add(f);
            }
            i++;
            f = createFileName(i);
        }
        if ((numberOfGenerations < 0) || (i <= numberOfGenerations)) {
            files.add(createFileName(i));
            i++;
        }
        i = i - 1;
        if (i > 1) {
            for (int j = i-1; j >= 1; j--) {
                File last = files.get(j);
                File second = files.get(j-1);
                second.renameTo(last);
            }
        }
        if (numberOfGenerations != 0) {
            getLogFile().renameTo(createFileName(1));
        } else {
            getLogFile().delete();
        }
        createLogFile();
    }

    /**
     * Creates a file name from given generation number.
     * If 0 is supplied for generation number, then {@link #logFile} i sreturned
     *
     * @param g generation number
     * @return a file name from given generation number
     */
    protected File createFileName(int g) {
        if (g == 0) {
            return getLogFile();
        } else {
            File p = getLogFile().getParentFile();
            if (p == null) {
                p = new File(".");
            }
            String name = getLogFile().getName();
            int i = name.lastIndexOf('.');
            if (i >= 0) {
                name = name.substring(0, i) + "." + g + name.substring(i);
            } else {
                name = name + "." + g;
            }
            return new File(p, name);
        }
    }

    /**
     * Creates log file
     *
     * @throws IOException io exception
     */
    protected void createLogFile() throws IOException {
        File file = getLogFile();
        FileOutputStream fos = new FileOutputStream(file);
        if (getBufferLen() > 0) {
            BufferedOutputStream out = new BufferedOutputStream(fos, getBufferLen());
            cachedOutputStream = out;
        } else {
            cachedOutputStream = fos;
        }
        logFileCreated = file.lastModified();
    }
}
