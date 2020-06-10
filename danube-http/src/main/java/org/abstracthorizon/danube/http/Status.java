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
package org.abstracthorizon.danube.http;

/**
 * This class descibes HTTP response status.
 *
 * @author Daniel Sendula
 */
public class Status {

    public static final Status CONTINUE = new Status("100", "Continue");

    public static final Status OK = new Status("200", "OK");

    public static final Status CREATED = new Status("201", "Created");

    public static final Status NO_CONTENT = new Status("204", "No Content");

    public static final Status PARTIAL_CONTENT = new Status("206", "Partial Content");

    public static final Status MOVED_PERMANENTLY = new Status("301", "Moved Permanently");

    public static final Status FOUND = new Status("302", "Found");

    public static final Status SEE_OTHER = new Status("303", "See Other");

    public static final Status BAD_REQUEST = new Status("400", "Bad Request");

    public static final Status UNAUTHORIZED = new Status("401", "Unauthorized");

    public static final Status FORBIDDEN = new Status("403", "Forbidden");

    public static final Status NOT_FOUND = new Status("404", "Not Found");

    public static final Status METHOD_NOT_ALLOWED = new Status("405", "Method Not Allowed");

    public static final Status CONFLICT = new Status("409", "Conflict");

    public static final Status GONE = new Status("410", "Gone");

    public static final Status LENGTH_REQUIRED = new Status("411", "Length Required");

    public static final Status PRECONDITION_FAILED = new Status("412", "Precondition Failed");

    public static final Status UNSUPPORTED_MEDIA_TYPE = new Status("415", "Unsupported Media Type");

    public static final Status RANGE_NOT_SATISFIABLE = new Status("416", "Requested Range Not Satisfiable");

    public static final Status EXPECTATION_FAILED = new Status("417", "Expectation failed");

    public static final Status INTERNAL_SERVER_ERROR = new Status("500", "Internal Server Error");

    public static final Status NOT_IMPLEMENTED = new Status("501", "Not Implemented");

    /** Response code */
    protected String code;

    /** Response message */
    protected String message;

    /**
     * Constructor
     */
    public Status() {
    }

    /**
     * Constructor
     * @param statusCode status code
     * @param statusMessage status message
     */
    public Status(String statusCode, String statusMessage) {
        this.code = statusCode;
        this.message = statusMessage;
    }

    /**
     * Returns status code
     * @return status code
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns status message
     * @return status message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns full status (code, space, message)
     * @return full status (code, space, message)
     */
    public String getFullStatus() {
        return code + " " + message;
    }

    /**
     * Returns status as a string
     * @return status as a string
     */
    public String toString() {
        return "Status[" + getFullStatus() + "]";
    }
}
