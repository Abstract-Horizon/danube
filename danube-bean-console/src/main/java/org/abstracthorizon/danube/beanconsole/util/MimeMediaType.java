/*
 * Copyright (c) 2008 Creative Sphere Limited.
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
package org.abstracthorizon.danube.beanconsole.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Daniel Sendula
 */
public class MimeMediaType implements Comparable<MimeMediaType> {

    public static final MimeMediaType ALL = new MimeMediaType("*", "*");

    public static final MimeMediaType TEXT_ALL = new MimeMediaType("text", "*");

    public static final MimeMediaType TEXT_PLAIN = new MimeMediaType("text", "plain");

    public static final MimeMediaType TEXT_HTML = new MimeMediaType("text", "html");

    public static final MimeMediaType TEXT_HTMLF = new MimeMediaType("text", "htmlf");

    public static final MimeMediaType TEXT_XML = new MimeMediaType("text", "xml");

    public static final MimeMediaType APPLICATION_ALL = new MimeMediaType("application", "*");

    public static final MimeMediaType APPLICATION_XML = new MimeMediaType("application", "xml");

    public static final MimeMediaType APPLICATION_OCTET_STREAM = new MimeMediaType("application", "octet-stream");
    
    public static final MimeMediaType RESOURCE_DOESNT_EXIST = new MimeMediaType("resource", "doesnt-exist");
    
    public static final MimeMediaType RESOURCE_COLLECTION = new MimeMediaType("resource", "collection");
    
    public static final MimeMediaType RESOURCE_POTENTIAL = new MimeMediaType("resource", "potential");
    
    protected static Map<String, MimeMediaType> cachedMediaTypes = new HashMap<String, MimeMediaType>();
    
    static {
        cachedMediaTypes.put(ALL.toString(), ALL);
        cachedMediaTypes.put(TEXT_ALL.toString(), TEXT_ALL);
        cachedMediaTypes.put(TEXT_PLAIN.toString(), TEXT_PLAIN);
        cachedMediaTypes.put(TEXT_HTML.toString(), TEXT_HTML);
        cachedMediaTypes.put(TEXT_XML.toString(), TEXT_XML);
        cachedMediaTypes.put(APPLICATION_ALL.toString(), APPLICATION_ALL);
        cachedMediaTypes.put(APPLICATION_XML.toString(), APPLICATION_XML);
        cachedMediaTypes.put(RESOURCE_DOESNT_EXIST.toString(), RESOURCE_DOESNT_EXIST);
        cachedMediaTypes.put(APPLICATION_OCTET_STREAM.toString(), APPLICATION_OCTET_STREAM);
        cachedMediaTypes.put(RESOURCE_COLLECTION.toString(), RESOURCE_COLLECTION);
    }
    
    
    public static MimeMediaType mediaType(String string) {
        MimeMediaType mediaType = cachedMediaTypes.get(string);
        if (mediaType == null) {
            mediaType = new MimeMediaType(string);
            if (mediaType.getParameters() == null) {
                cachedMediaTypes.put(mediaType.toString(), mediaType);
            }
        }
        return mediaType;
    }
    
    public static MimeMediaType mediaType(String mainType, String subType) {
        return mediaType(mainType + "/" + subType);
    }
    
    protected String stringRepresentation;
    
    protected String mainType;
    
    protected String subType;
    
    protected Map<String, String> parameters;
    
    public MimeMediaType(String mainType, String subType) {
        this(mainType, subType, null);
    }
    
    public MimeMediaType(String mainType, String subType, Map<String, String> parameters) {
        this.mainType = mainType;
        this.subType = subType;
        this.parameters = parameters;
        
        StringBuilder sb = new StringBuilder();
        sb.append(mainType).append('/').append(subType);
        
        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                sb.append(';');
                sb.append(entry.getKey()).append('=').append(entry.getValue());
            }
        }
        
        
        stringRepresentation = sb.toString();
    }
    
    public MimeMediaType(String mediaType) throws MimeTypeException {
        this.stringRepresentation = mediaType;
        parse(mediaType);
    }
    
    public void parse(String mediaType) throws MimeTypeException {
        // TODO Need proper parser as parameter values can be enclosed in quotation marks...
        String parametersString = null;
        int i = mediaType.indexOf(';');
        if (i > 0) {
            parametersString = mediaType.substring(i + 1);
            mediaType = mediaType.substring(0, i);
        }
        i = mediaType.indexOf('/');
        if (i <= 0) {
            throw new MimeTypeException("Bad media mime type, missing '/'", mediaType);
        }
        mainType = mediaType.substring(0, i).trim();
        subType = mediaType.substring(i + 1).trim();
        if (mainType.length() == 0) {
            throw new MimeTypeException("Bad media mime type, empty main type '/'", mediaType);
        }
        if (subType.length() == 0) {
            throw new MimeTypeException("Bad media mime type, empty sub type '/'", mediaType);
        }
        if (parametersString != null) {
            String[] params = parametersString.split(";");
            parameters = new HashMap<String, String>();
            for (String param : params) {
                i = param.indexOf('=');
                if (i < 0) {
                    throw new MimeTypeException("Bad parameter, missing '='", mediaType);
                }
                String name = param.substring(0, i).trim();
                String value = param.substring(i + 1).trim();
                if (name.length() == 0) {
                    throw new MimeTypeException("Bad parameter, empty parameter name", mediaType);
                }
                parameters.put(name, value);
            }
        }
    }

    public String getMainType() {
        return mainType;
    }

    public void setMainType(String mainType) {
        this.mainType = mainType;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
    
    public boolean isCompatibleWith(MimeMediaType mediaType) {
        if ("*".equals(getMainType())) {
            return true;
        } else if (getMainType().equals(mediaType.getMainType())) {
            if ("*".equals(getSubType())) {
                return true;
            } else if (getSubType().equals(mediaType.getSubType())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAll() {
        return "*".equals(getMainType()) && "*".equals(getSubType());
    }

    public boolean isAllSubTypes() {
        return "*".equals(getSubType());
    }
    
    public String toString() {
        return stringRepresentation;
    }

    public String toShortString() {
        return getMainType() + "/" + getSubType();
    }
    
    public boolean compareTypeOnly(MimeMediaType mt) {
        return this.mainType.equals(mt.getMainType()) && this.subType.endsWith(mt.getSubType());
    }
    
    public boolean compareTypeOnly(String mainType, String subType) {
        return this.mainType.equals(mainType) && this.subType.endsWith(subType);
    }
    
    public boolean match(MimeMediaType mt) {
        if (mt.getMainType().equals("*") || getMainType().equals("*") || getMainType().equals(mt.getMainType())) {
            if (mt.getSubType().equals("*") || getSubType().equals("*") || getSubType().equals(mt.getSubType())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean equals(Object o) {
        if (o instanceof MimeMediaType) {
            MimeMediaType other = (MimeMediaType)o;
            if (getMainType().equals(other.getMainType()) 
                    && getSubType().equals(other.getSubType())
                    && ((getParameters() == other.getParameters()) 
                            || ((getParameters() != null)
                                    && getParameters().equals(other.getParameters())
                               )
                       )
                ) {
                return true;
            }
        }

        return super.equals(o);
    }
    
    public int compareTo(MimeMediaType other) {
        double o1 = 1;
        if (parameters != null) {
            String q1 = parameters.get("q");
            if (q1 != null) {
                try {
                    o1 = Double.parseDouble(q1);
                } catch (NumberFormatException ignore) {
                }
            }
        }
        double o2 = 1;
        if (other.parameters != null) {
            String q2 = other.parameters.get("q");
            if (q2 != null) {
                try {
                    o2 = Double.parseDouble(q2);
                } catch (NumberFormatException ignore) {
                }
            }
        }
        if (o1 == o2) {
            return 0;
        } else if (o1 < o2) {
            return 1;
        } else {
            return -1;
        }
    }
    
}
