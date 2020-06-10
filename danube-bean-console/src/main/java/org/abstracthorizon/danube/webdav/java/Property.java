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
package org.abstracthorizon.danube.webdav.java;

import org.abstracthorizon.danube.beanconsole.BeanHelper;
import org.abstracthorizon.danube.http.util.IOUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

/**
 * This class defines property value as a WebDAV's resource (file).
 * Reading the file will return object's property content as a string,
 * while writing to the file will change (if possible) property's value
 * from the string (using property editors defined)
 *
 * @author Daniel Sendula
 */
public class Property extends StringDelegate {

    /** Cached property name */
    protected String propertyName;

    /**
     * Constructor
     * @param path path to the property
     */
    public Property(String path) {
        super(path);
        propertyName = IOUtils.lastPathComponent(path);
        int i = propertyName.lastIndexOf('.');
        if (i >= 0) {
            propertyName = propertyName.substring(0, i);
        }
    }

    /**
     * Reads property as a string value
     * @param adapter
     * @return property value as a string
     */
    protected String getValueAsString(JavaWebDAVResourceAdapter adapter) {
        Object object = adapter.findObjectImpl(objectPath);
        if (object != null) {
            return BeanHelper.getPropertyValueAsString(object, propertyName);
        } else {
            return null;
        }
    }

    /**
     * This method sets property's value from a given string
     * @param adapter adapter
     * @param value value as a string
     */
    protected boolean setValueAsString(JavaWebDAVResourceAdapter adapter, String value) {
        Object object = adapter.findObjectImpl(objectPath);
        if (object != null) {
            boolean hasError = false;
            StringBuilder errorString = null;
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());

                PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor prop : props) {
                    if (prop.getName().equals(propertyName)) {
                        Method writeMethod = prop.getWriteMethod();
                        if (writeMethod != null) {
                            PropertyEditor writePropertyEditor = PropertyEditorManager.findEditor(writeMethod.getParameterTypes()[0]);
                            if (writePropertyEditor != null) {
                                String oldValue = BeanHelper.toString(prop);
                                if (oldValue != value) {
                                    try {
                                        writePropertyEditor.setAsText(value);
                                        writeMethod.invoke(object, new Object[]{writePropertyEditor.getValue()});
                                        return true;
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

        } else {
//            return null;
        }
        return false;
    }

    /**
     * Returns an output stream to write value to.
     * @param adapter adapter
     * @return output stream
     */
    public OutputStream getOutputStream(final JavaWebDAVResourceAdapter adapter) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream() {

            public void close() {
                String value = new String(toByteArray());
                setValueAsString(adapter, value);
            }

        };
        return outputStream;
    }
}
