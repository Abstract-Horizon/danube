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
package org.abstracthorizon.danube.beanconsole;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.mvc.Controller;
import org.abstracthorizon.danube.mvc.ModelAndView;

import java.beans.MethodDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Invokes an arbitrary method on selected bean. The bean is defined with all but last part of the resource path.
 *
 * @author Daniel Sendula
 */
public class InvokeController implements Controller {

    protected DisplayController displayController;

    /**
     * Constructor
     */
    public InvokeController() {
    }

    /**
     * Constructor
     * @param displayController reference to display controller
     */
    public InvokeController(DisplayController displayController) {
        this.displayController = displayController;
    }

    /** Method that handles a request. It returns following entries in model's map:
     * <ul>
     *   <li>connection - httpConnection passed into this controller</li>
     *   <li>methodName - </li>
     *   <li>name - name of the bean (last element of the path or Root Bean)</li>
     *   <li>type - string representation of the type of the selected object</li>
     *   <li>path - full path to the bean. Path elements are separated with &quot;.&quot; and if
     *              element has dot in it then it is enclosed in &quot;&lt;&quot; and &quot;&gt;&quot; simbols</li>
     *   <li>resourcePath - full path to the bean but always starting with &quot;/&quot; and never ending
     *              with it. If root then empty string. It is used for creating URIs.</li>
     *   <li>returnUri - URI this page is called from. It always ends with &quot;/&quot;</li>
     *   <li>topUri and backUri - uris to previous bean and top - root bean. </li>
     *   <li>current - reference to the object referenced with the path</li>
     *   <li>propertiesError - if error occured setting properties</li>
     *   <li>beans - list of {@link BeanDef} objects - if object is of {@link ConfigurableApplicationContext} type.</li>
     *   <li>map - list of {@link BeanDef} objects - if object is of {@link Map} type.</li>
     *   <li>collection - list of {@link BeanDef} objects - if object is of {@link Collection} type.</li>
     *   <li>properties - list of {@link BeanDef} objects - object accessible properties.</li>
     *   <li>methods - list of {@link MethodDescriptor} object - methods that are not used for properties' access.</li>
     * </ul>
     * <p>
     *   For the way navigation is done check {@link BeanHelper#navigate(Object, String)} method documentation.
     * </p>
     */
    public ModelAndView handleRequest(Connection connection) {
        HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);

        Map<String, Object> model = new HashMap<String, Object>();

        model.put("connection", connection);

        String resourcePath = BeanHelper.createResourcePath(httpConnection.getComponentResourcePath());

        Object current = displayController.getRootObject();

        String methodName;
        if (resourcePath.length() == 0) {
            methodName = null;
        } else {
            int i = resourcePath.lastIndexOf('/');
            if (i > 0) {
                methodName = resourcePath.substring(i+1);
                resourcePath = resourcePath.substring(0, i);
            } else {
                methodName = resourcePath.substring(1);
                resourcePath = "";
            }
        }

        BeanHelper.Result result = new BeanHelper.Result();
        result.resultObject = current;
        if (resourcePath.length() > 0) {
            result = BeanHelper.navigate(current, resourcePath);
            current = result.resultObject;
        }
        model.put("name", BeanHelper.createName(resourcePath));
        model.put("path", BeanHelper.createPath(resourcePath));
        model.put("type", BeanHelper.toTypeString(current));
        model.put("resourcePath", resourcePath);
        model.put("current", current);
        model.put("methodName", BeanHelper.createName(resourcePath));

        execute(model, result, httpConnection, methodName);

        model.put("returnUri", httpConnection.getContextPath() + displayController.getComponentPath() + resourcePath);

        String accept = httpConnection.getRequestHeaders().getOnly("Accept");
        String view = null;
        if (accept != null) {
            if (accept.indexOf("text/html") >= 0) {
                view = "html/result";
            } else if (accept.indexOf("text/xml") >= 0) {
                view = "xml/result";
            }
        }
        if (view == null) {
            view = "text/result";
        }

        ModelAndView modelAndView = new ModelAndView(view, model);
        return modelAndView;
    }

    /**
     * Invokes the method
     * @param model model map
     * @param current current bean
     * @param httpConnection http connection (for paramters)
     * @param methodName method name
     */
    protected void execute(Map<String, Object> model, BeanHelper.Result result, HTTPConnection httpConnection, String methodName) {
        Object current = result.resultObject;
        
        if (methodName == null) {
            model.put("error", "Method name not specified.");
            return;
        }

        int paramNumber = 0;
        while (httpConnection.getRequestParameters().getOnly(Integer.toString(paramNumber)) != null) {
            paramNumber = paramNumber + 1;
        }

        Method[] methods = current.getClass().getMethods();
        ArrayList<Method> foundMethods = new ArrayList<Method>();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && (method.getParameterTypes().length == paramNumber)) {
                foundMethods.add(method);
            }
        }
        if (foundMethods.size() == 0) {
            model.put("error", "Couldn't find method '" + methodName +"' that has " + paramNumber +" parameters.");
            return;
        }

        Method method = null;
        if (foundMethods.size() > 1) {
            model.put("error", "Found more then one method '" + methodName +"' that has " + paramNumber +" parameters.\nNot supported at the moment");
            return;
        }

        method = foundMethods.get(0);


        Class<?>[] types = method.getParameterTypes();
        Object[] obj = new Object[types.length];
        for (int j = 0; j < types.length; j++) {
            String value = (String)httpConnection.getRequestParameters().getOnly(Integer.toString(j));
            if (value != null) {
                try {
                    value = URLDecoder.decode(value, "UTF-8");
                } catch (UnsupportedEncodingException ignore) {
                }
            }

            PropertyEditor propertyEditor = PropertyEditorManager.findEditor(types[j]);
            if (propertyEditor != null) {
                propertyEditor.setAsText(value);
                obj[j] = propertyEditor.getValue();
            } else {
                obj[j] = value;
            }
        }
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader contextClassLoader = current.getClass().getClassLoader();
        if (result.lastContext != null) {
            ApplicationContext applicationContext = result.lastContext;
            contextClassLoader = applicationContext.getClassLoader();
        }
        
        Thread.currentThread().setContextClassLoader(contextClassLoader);
        try {
            Object res = method.invoke(current, obj);
            StringBuffer r = new StringBuffer();
            r.append(res);

            model.put("result", r.toString());
        } catch (Throwable t) {
            model.put("error", BeanHelper.stackTrace(t));
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    /**
     * Returns reference to DisplayController instance
     * @return reference to DisplayController instance
     */
    public DisplayController getDisplayController() {
        return displayController;
    }

    /**
     * Sets reference to DisplayController instance
     * @param displayController reference to DisplayController instance
     */
    public void setDisplayController(DisplayController displayController) {
        this.displayController = displayController;
    }

}
