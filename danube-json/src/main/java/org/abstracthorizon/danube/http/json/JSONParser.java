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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Daniel Sendula
 */
public class JSONParser {

    // private Object root;
    private Reader reader;
    private Object current;
    private StringBuffer buffer = new StringBuffer();
    private Token token;
    private java.lang.reflect.Method setter;
    
    private static enum Token {
        CB_OPEN, CB_CLOSED, SB_OPEN, SB_CLOSED,
        COLON, COMMA, TRUE, FALSE, NULL,
        STRING, NUMBER, EOF
    };
    
    public JSONParser() {
    }

    public void parse(Object root, Reader reader) {
        this.reader = reader;
        if (!(reader instanceof BufferedReader)) {
            reader = new BufferedReader(reader);
        }
        this.current = root;
        nextToken();
        if (!object()) {
            throw new JSONParserException("Expected OBJECT");
        }
    }
    
    protected boolean object() {
        if (token == Token.CB_OPEN) {
            nextToken();
            Object current = this.current;
            if (token == Token.STRING) {
                String propertyName = buffer.toString();
                prepareCurrent(current, propertyName);
                nextToken();
                if (token != Token.COLON) {
                    throw new JSONParserException("Expected ':'");
                }
                nextToken();
                if (!value()) {
                    throw new JSONParserException("Expected VALUE");
                }
                
                process(current, propertyName, this.current);
                    
                nextToken();
                while (token == Token.COMMA) {
                    nextToken();
                    if (token != Token.STRING) {
                        throw new JSONParserException("Expected STRING");
                    }
                    propertyName = buffer.toString();
                    prepareCurrent(current, propertyName);
                    nextToken();
                    
                    current = this.current;
                    if (token != Token.COLON) {
                        throw new JSONParserException("Expected ':'");
                    }
                    if (!value()) {
                        throw new JSONParserException("Expected VALUE");
                    }

                    process(current, propertyName, this.current);

                    nextToken();
                }
                if (token != Token.CB_CLOSED) {
                    throw new JSONParserException("Expected '}' or ','");
                }
            } else if (token != Token.CB_CLOSED) {
                throw new JSONParserException("Expected '}'");
            }
            this.current = current;
            return true;
        } else {
            return false;
        }
    }
    
    protected boolean array() {
        if (token == Token.SB_OPEN) {
            nextToken();
            List<Object> array = new ArrayList<Object>();
            
            if (value()) {
                array.add(current);
                nextToken();
                while (token == Token.COMMA) {
                    if (!value()) {
                        throw new JSONParserException("Expected VALUE");
                    }
                    array.add(current);
                    nextToken();
                }
                if (token != Token.SB_CLOSED) {
                    throw new JSONParserException("Expected ']");
                }
            } else if (token == Token.SB_CLOSED) {
                
            } else {
                throw new JSONParserException("Expected VALUE or ']");
            }
            
            this.current = array;
            return true;
        } else {
            return false;
        }
    }
    
    protected boolean value() {
        if (token == Token.STRING) {
            return true;
        } else if (token == Token.NUMBER) {
            return true;
        } else if (object()) {
            return true;
        } else if (array()) {
            return true;
        } else if (token == Token.TRUE) {
            return true;
        } else if (token == Token.FALSE) {
            return true;
        } else if (token == Token.NULL) {
            return true;
        } else {
            return false;
        }
    }
    
    protected void prepareCurrent(Object current, String propertyName) {
        setter = null;
        String getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        String createName = null;
        String setterName = null;

        Object n = null;
        try {
            java.lang.reflect.Method m = current.getClass().getMethod(getterName, new Class[] {});
            n = m.invoke(current, new Object[]{});
        } catch (SecurityException ignore) {
        } catch (NoSuchMethodException ignore) {
        } catch (IllegalArgumentException ignore) {
        } catch (IllegalAccessException ignore) {
        } catch (InvocationTargetException ignore) {
        }
        if (n == null) {
            createName = "create" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            try {
                java.lang.reflect.Method m = current.getClass().getMethod(createName, new Class[] {});
                n = m.invoke(current, new Object[]{});
            } catch (SecurityException ignore) {
            } catch (NoSuchMethodException ignore) {
            } catch (IllegalArgumentException ignore) {
            } catch (IllegalAccessException ignore) {
            } catch (InvocationTargetException ignore) {
            }
        }
        if (n == null) {
            setterName = findSetterMethod(propertyName);
            
            if (setter != null) {
                Class<?> c = setter.getParameterTypes()[0];
                try {
                    n = c.newInstance();
                } catch (InstantiationException ignore) {
                } catch (IllegalAccessException ignore) {
                }
                if ((n == null) && List.class.isAssignableFrom(c)) {
                    n = new ArrayList<Object>();
                }
                if ((n == null) && Map.class.isAssignableFrom(c)) {
                    n = new HashMap<Object, Object>();
                }
                
            }
        }
        if (n == null) {
            throw new JSONParserException("Cannot create object for attribute " + propertyName + "; tried " + getterName + "(), " + createName + "() and " + setterName + "( with one argument ).");
        }
        this.current = n;
    }

    protected String findSetterMethod(String propertyName) {
        String setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        java.lang.reflect.Method[] methods = current.getClass().getMethods();
        int i = 0;
        while ((i < methods.length) && (setter == null)) {
            if (methods[i].getName().equals(setterName)
                    && (methods[i].getParameterTypes().length == 1)) {
                setter = methods[i]; 
            }
            i++;
        }
        return setterName;
    }
    
    @SuppressWarnings("unchecked")
    protected void process(Object o, String attribute, Object value) {
        if (current instanceof Map) {
            Map<String, Object> map = (Map<String, Object>)current;
            map.put(attribute, value);
        } else {
            if (setter == null) {
                findSetterMethod(attribute);
            }
            if (setter != null) {
                try {
                    setter.invoke(o, new Object[]{value});
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new JSONParserException(e);
                }
            } else {
                throw new JSONParserException("Cannot find setter for attribute " + attribute + " on class " + o.getClass().getName());
            }
        }
    }
    
    protected void nextToken() {
        try {
            char c;
            try {
                c = readChar();
                while (Character.isWhitespace(c)) {
                    c = readChar();
                }
            } catch (EOF eof) {
                token = Token.EOF;
                return;
            }
            
            if (c == '"') {
                token = Token.STRING;
                buffer = new StringBuffer();
                c = readChar();
                while (c != '"') {
                    if (c == '\\') {
                        c = readChar();
                        if (c == '"') {
                            buffer.append(c);
                        } else if (c == '\\') {
                            buffer.append(c);
                        } else if (c == '/') {
                            buffer.append('/'); // TODO heh?
                        } else if (c == 'b') {
                            buffer.append('\b');
                        } else if (c == 'f') {
                            buffer.append('\f');
                        } else if (c == 'n') {
                            buffer.append('\n');
                        } else if (c == 'r') {
                            buffer.append('\r');
                        } else if (c == 't') {
                            buffer.append('\t');
                        } else if (c == 'u') {
                            int hex = 0;
                            for (int i = 0; i < 4; i++) {
                                hex = hex * 256;
                                c = readChar();
                                if ((c >= 'a') && (c <= 'f')) {
                                    hex = hex + c - 'a' + 10;
                                } else if ((c >= 'A') && (c <= 'F')) {
                                    hex = hex + c - 'A' + 10;
                                } else if ((c >= '0') && (c <= '9')) {
                                    hex = hex + c - '0';
                                } else {
                                    throw new JSONParserException("Expected 0-9, A-F or a-f.");
                                }
                            }
                            buffer.append((char)hex);
                        } else {
                            throw new JSONParserException("Expected '\\\\', '\\\"', '\\/', '\\b', '\\f', '\\n', '\\r', '\\t' or '\\uxxxx' ");
                        }
                    } else {
                        buffer.append(c);
                    }
                    c = readChar();
                }
                
            } else if (c == 't') {
                readString("rue");
                current = Boolean.TRUE;
                token = Token.TRUE;
            } else if (c == 'f') {
                readString("alse");
                current = Boolean.FALSE;
                token = Token.FALSE;
            } else if (c == 'n') {
                readString("ull");
                current = null;
                token = Token.NULL;
            } else if ((c == '-') || Character.isDigit(c)) {
                buffer = new StringBuffer();
                boolean integer = true;
                if (c == '-') {
                    buffer.append(c);
                    c = readChar();
               }
                if (!Character.isDigit(c)) {
                    throw new JSONParserException("Expected digit (0-9)");
                }
                if (c != '0') {
                    while (Character.isDigit(c)) {
                        buffer.append(c);
                        c = readChar();
                    }
                } else {
                    buffer.append(c);
                    c = readChar();
                }
                if (c == '.') {
                    integer = false;
                    c = readChar();
                    if (!Character.isDigit(c)) {
                        throw new JSONParserException("Expected digit (0-9)");
                    }
                    while (Character.isDigit(c)) {
                        buffer.append(c);
                        c = readChar();
                    }
                }
                if ((c == 'e') | (c == 'E')) {
                    integer = false;
                    c = readChar();
                    if ((c == '-') | (c == '+')) {
                        buffer.append(c);
                        c = readChar();
                    }
                    if (!Character.isDigit(c)) {
                        throw new JSONParserException("Expected digit (0-9)");
                    }
                    while (Character.isDigit(c)) {
                        buffer.append(c);
                        c = readChar();
                    }
                }
                if (!Character.isWhitespace(c)) {
                    unreadChar();
                }
                
                if (integer) {
                    Long l = Long.parseLong(buffer.toString());
                    long ll = l.longValue();
                    if ((ll >= Integer.MIN_VALUE) && (ll <= Integer.MAX_VALUE)) {
                        if ((ll >= Short.MIN_VALUE) && (ll <= Short.MAX_VALUE)) {
                            if ((ll >= Byte.MIN_VALUE) && (ll <= Byte.MAX_VALUE)) {
                                current = l.byteValue();
                            } else {
                                current = l.shortValue();
                            }
                        } else {
                            current = l.intValue();
                        }
                    } else {
                        current = l;
                    }
                } else {
                    Double d = Double.parseDouble(buffer.toString());
                    current = d;
                }
                
                token = Token.NUMBER;
            } else if (c == '{') {
                token = Token.CB_OPEN;
            } else if (c == '{') {
                token = Token.CB_CLOSED;
            } else if (c == '[') {
                token = Token.SB_OPEN;
            } else if (c == ']') {
                token = Token.SB_CLOSED;
            } else if (c == ',') {
                token = Token.COMMA;
            } else if (c == ':') {
                token = Token.COLON;
            } else {
                throw new JSONParserException("Unexpected character '" + c + "'");
            }
        } catch (IOException e) {
            throw new JSONParserException(e);
        }
    }

    protected void readString(String s) throws IOException {
        int j = 0;
        while (j < s.length()) {
            int i = reader.read();
            if (i == -1) {
                throw new JSONParserException("Premature EOF");
            }
            char c = (char)i;
            if (c != s.charAt(j)) {
                throw new JSONParserException("Unexpected character \"" + c + "\". Expected \"" + s.charAt(j) + "\"");
            }
            j++;
        }
    }

    protected char readChar() throws EOF {
        try {
            reader.mark(1);
            int i = reader.read();
            if (i == -1) {
                throw new EOF();
            }
            return (char)i;
        } catch (IOException e) {
            throw new JSONParserException(e);
        }
    }
    
    protected void unreadChar() {
        try {
            reader.reset();
        } catch (IOException e) {
            throw new JSONParserException(e);
        }
    }
    
    protected class EOF extends JSONParserException {
        
        public EOF() {
            super("Premature EOF");
        }
    };
}
