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

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.List;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.mvc.ModelAndView;
import org.abstracthorizon.danube.mvc.View;

/**
 * This class expects entry called &quot;bean&quot; in model map. It will
 * be, then, be send as output in json format. 
 * 
 * @author Daniel Sendula
 */
public class JSONViewAdapter implements View {

    public void render(Connection connection, ModelAndView modelAndView) {
        PrintWriter out = (PrintWriter)connection.adapt(PrintWriter.class);
        output(modelAndView.getModel().get("bean"), out, "");
    }
    
    public void output(Object o, PrintWriter out, String ident) {
        
        if (o == null) {
            out.print("null");
        } else {
            Class<?> cls = o.getClass();
            if (o instanceof CharSequence) {
                CharSequence charSequence = (CharSequence)o;
                StringBuilder res = new StringBuilder();
                for (int i = 0; i < charSequence.length(); i++) {
                    char c = charSequence.charAt(i);
                    if ((c == '\\') || (c == '"')) {
                        res.append('\\');
                        res.append(c);
                    } else if (c == '\n') {
                        res.append("\\n");
                    } else if (c == '\r') {
                        res.append("\\r");
                    } else if (c == '\t') {
                        res.append("\\t");
                    } else if (c == '\b') {
                        res.append("\\b");
                    } else if (c == '\f') {
                        res.append("\\f");
                    } else if (c < ' ') {
                        res.append("\\u");
                        int h = c;
                        String hs = Integer.toHexString(h);
                        int j = hs.length();
                        while (j < 4) {
                            res.append('0');
                            j++;
                        }
                        res.append(hs);
                    } else {
                        res.append(c);
                    }
                }
                out.print('"');
                out.print(res.toString());
                out.print('"');
            } else if (o instanceof Boolean) {
                Boolean b = (Boolean)o;
                out.print(b.toString());
            } else if (o instanceof Number) {
                Number n = (Number)o;
                out.print(n.toString());
            } else if (cls.isArray()) {
                out.println("[");
                boolean first = true;
                int len = Array.getLength(o);
                for (int i = 0; i < len; i++) {
                    if (first) { first = false; } else {
                        out.println(",");
                    }
                    out.print(ident);
                    out.print("  ");
                    output(Array.get(o, i), out, ident + "  ");
                }
                out.println();
                out.print(ident);
                out.println("]");
            } else if (o instanceof List) {
                List<?> list = (List<?>)o;
                out.println("[");
                boolean first = true;
                int len = list.size();
                for (int i = 0; i < len; i++) {
                    if (first) { first = false; } else {
                        out.println(",");
                    }
                    out.print(ident);
                    out.print("  ");
                    output(list.get(i), out, ident + "  ");
                }
                out.println();
                out.print(ident);
                out.println("]");
            } else {
                out.println("{");
                boolean first = true;
                for (java.lang.reflect.Method method : o.getClass().getMethods()) {
                    String name = method.getName();
                    if (((name.startsWith("get") && (name.length() > 3) && Character.isUpperCase(name.charAt(3)))
                           || (name.startsWith("is") && (name.length() > 2) && Character.isUpperCase(name.charAt(2))))
                            && !name.equals("getClass")
                            && (method.getParameterTypes().length == 0)) {
                        
                        if (name.startsWith("get")) {
                            name = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                        } else {
                            name = Character.toLowerCase(name.charAt(2)) + name.substring(3);
                        }
                        
                        try {
                            Object res = method.invoke(o, new Object[]{});
                            
                            boolean proceed = res != null;
                            if (proceed && res.getClass().isArray() && (Array.getLength(res) == 0)) {
                                proceed = false;
                            }
                            if (proceed && (res instanceof List) && (((List<?>)res).size() == 0)) {
                                proceed = false;
                            }
                            if (proceed) {
                                if (first) { first = false; } else {
                                    out.println(",");
                                }
                                out.print(ident);
                                out.print("  ");
                                out.print('"');
                                out.print(name);
                                out.print("\": ");
                                output(res, out, ident + "  ");
                            }
                        } catch (Exception e) {
                            if (e instanceof RuntimeException) {
                                throw (RuntimeException)e;
                            } else {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                out.println();
                
                out.print(ident);
                out.println("}");
            }
        }
    }

}
