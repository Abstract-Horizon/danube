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
package org.abstracthorizon.danube.beanconsole;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.mvc.Controller;
import org.abstracthorizon.danube.mvc.ModelAndView;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Controller that processes request, select the object and prepares data for the view
 *
 * @author Daniel Sendula
 */
public class DisplayController implements Controller, ApplicationContextAware {

    /**
     * Root object
     */
    protected Object root;

    /** Application context this controller is running under */
    protected ApplicationContext rootContext;

    /** Component path this controller is to be working from */
    protected String componentPath;

    /**
     * If path defined as a pattern to a specific page (index.html for instance)
     * then it needs to be stripped of component resource path before it is used
     */
    protected String pageName;

    /**
     * Method that handles a request. It returns following entries in model's map:
     * <ul>
     *   <li>connection - httpConnection passed into this controller</li>
     *   <li>uri - URI this page is called from. It always ends with &quot;/&quot;</li>
     *   <li>name - name of the bean (last element of the path or Root Bean)</li>
     *   <li>type - string representation of the type of the selected object</li>
     *   <li>path - full path to the bean. Path elements are separated with &quot;.&quot; and if
     *              element has dot in it then it is enclosed in &quot;&lt;&quot; and &quot;&gt;&quot; simbols</li>
     *   <li>resourcePath - full path to the bean but always starting with &quot;/&quot; and never ending
     *              with it. If root then empty string. It is used for creating URIs.</li>
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

        if ((pageName != null) && resourcePath.endsWith(pageName)) {
            resourcePath = resourcePath.substring(0, resourcePath.length() - pageName.length() - 1);
        }

        model.put("uri", httpConnection.getContextPath() + componentPath + resourcePath + "/");

        Object current = getRootObject();
        if (resourcePath.length() > 0) {
            int i = resourcePath.lastIndexOf('/');
            if (i > 0) {
                String backUri = httpConnection.getContextPath() + componentPath + resourcePath.substring(0, i);
                model.put("backUri", backUri);
            } else {
                model.put("backUri", httpConnection.getContextPath() + componentPath);
            }
            model.put("topUri", httpConnection.getContextPath() + componentPath);
            BeanHelper.Result result = BeanHelper.navigate(current, resourcePath);
            current = result.resultObject;
        }

        model.put("name", BeanHelper.createName(resourcePath));
        model.put("path", BeanHelper.createPath(resourcePath));
        model.put("type", BeanHelper.toTypeString(current));
        model.put("resourcePath", resourcePath);
        model.put("current", current);

        if ("true".equals(httpConnection.getRequestParameters().getOnly("-update"))) {
            updateProperties(current, httpConnection, model);
        }

        BeanHelper.prepare(current, model);

        String accept = httpConnection.getRequestHeaders().getOnly("Accept");
        String view = null;
        if (accept != null) {
            if (accept.indexOf("text/html") >= 0) {
                view = "html/display";
            } else if (accept.indexOf("text/xml") >= 0) {
                view = "xml/display";
            }
        }
        if (view == null) {
            view = "text/display";
        }

        ModelAndView modelAndView = new ModelAndView(view, model);
        return modelAndView;
    }


    /**
     * Sets properties of the selected bean.
     *
     * @param current selected bean
     * @param httpConnection http connection
     * @param model model
     */
    protected void updateProperties(Object current, HTTPConnection httpConnection, Map<String, Object> model) {
        boolean hasError = false;
        StringBuilder errorString = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(current.getClass());

            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor prop : props) {
                String param = prop.getName();
                Method writeMethod = prop.getWriteMethod();
                if (writeMethod != null) {
                    PropertyEditor writePropertyEditor = PropertyEditorManager.findEditor(writeMethod.getParameterTypes()[0]);
                    if (writePropertyEditor != null) {
                        String value = (String)httpConnection.getRequestParameters().getOnly(param);
                        if (value != null) {
                            String oldValue = BeanHelper.toString(prop);
                            if (oldValue != value) {
                                try {
                                    writePropertyEditor.setAsText(value);
                                    writeMethod.invoke(current, new Object[]{writePropertyEditor.getValue()});
                                } catch (Exception e) {
                                    if (!hasError) {
                                        hasError = true;
                                        errorString = new StringBuilder();
                                    } else {
                                        errorString.append("\n");
                                    }
                                    errorString.append(BeanHelper.stackTrace(e));
                                }
                            } else {
                                 //logger.debug("Skipped storing new value for " + param + " since they are same");
                            }
                        } else {
                            //
                        }
                    }
                }
            }
        } catch (IntrospectionException e) {
            if (errorString == null) {
                errorString = new StringBuilder();
            }
            if (!hasError) {
                hasError = true;
                errorString = new StringBuilder();
            } else {
                errorString.append("\n");
            }
            errorString.append(BeanHelper.stackTrace(e));
        }
        if (hasError) {
            model.put("propertiesError", errorString.toString());
        }

    }

    /**
     * Sets root application context in case root is not set.
     * @param context application context
     */
    public void setApplicationContext(ApplicationContext context) {
        while (context.getParent() != null) {
            context = context.getParent();
        }
        this.rootContext = context;
    }

    /**
     * Returns root object. If not set then rootContext is used instead.
     * @return root object
     */
    public Object getRootObject() {
        if (root == null) {
            root = rootContext;
        }
        return root;
    }

    /**
     * Sets root object.
     * @param root root object.
     */
    public void setRootObject(Object root) {
        this.root = root;
    }

    /**
     * Returns component path
     * @return component path
     */
    public String getComponentPath() {
        return componentPath;
    }

    /**
     * Sets component path
     * @param componentPath component path
     */
    public void setComponentPath(String componentPath) {
        this.componentPath = componentPath;
    }

    /**
     * Returns page name
     * @return page name
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * Sets page name
     * @param pageName page name
     */
    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

}
