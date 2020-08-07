/*
 * Copyright (c) 2005-2020 Creative Sphere Limited.
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Helper class for manipulation of beans.
 *
 * @author Daniel Sendula
 */
public class BeanHelper {
    /** Empty object array */
    public static final Object[] EMPTY_ARRAY = new Object[0];

    /**
     * This method navigates through objects from given root object to the object specified by path.
     * <p>
     *   Algorithm for navigating through objects is as following (in that order):
     * </p>
     * <ul>
     *   <li>If path element is numeric then it is expected for object to be a list {@link List} or an array.</li>
     *   <li>If current object is of {@link ApplicationContext} type then a bean of path element's name is tried to be retrieved.</li>
     *   <li>If current object is a map ({@link Map}) then an entry of path's entry name is fetched</li>
     *   <li>Bean inspector is used for getter method of given path name attribute to be invoked.</li>
     *   <li></li>
     * </ul>
     * <p>
     *   Each step is performed if previous returns <code>null</code> (or an exception that is in this case ignored).
     * </p>
     * @param from root object
     * @param path path
     * @return resulted object
     *
     * @throws BeanAccessException if there is a problem in dereferencing
     */
    @SuppressWarnings("unchecked")
    public static Result navigate(Object from, String path)
        throws BeanAccessException {
        
        Result result = new Result();

        if (path.length() == 0) {
            result.resultObject = from;
            return result;
        }

        Object current = from;
        if (current instanceof ApplicationContext) {
            result.lastContext = (ApplicationContext)current;
        }
        
        int l = 0;
        if (path.startsWith("/")) {
            l = 1;
        }
        while (l < path.length()) {
            
            int i = path.indexOf('/', l);
            String p;
            if (i >= 0) {
                p = path.substring(l, i);
                l = i + 1;
            } else {
                p = path.substring(l);
                l = path.length();
            }

            Object next = null;
            if ((p.length() > 2) && p.startsWith("[") && p.endsWith("]")) {
                p = p.substring(1, p.length() - 1);
                try {
                    int n = Integer.parseInt(p);
                    if (n < 0) {
                        throw new BeanAccessException(path.substring(1, l), new IndexOutOfBoundsException(p));
                    }
                    if (current instanceof List) {
                        next = ((List<?>)current).get(n);
                    } else if (current.getClass().isArray()) {
                        next = Array.get(current, n);
                    } else if (current instanceof Collection) {
                        int ii = -1;
                        @SuppressWarnings("rawtypes")
                        Iterator<?> iterator = ((Collection)current).iterator();
                        while (iterator.hasNext() && (ii != n)) {
                            Object o = iterator.next();
                            ii++;
                            if (ii == n) {
                                next = o;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    if (current instanceof ApplicationContext) {
                        ApplicationContext context = (ApplicationContext)current;
                        try {
                            Object bean = context.getBean(p);
                            next = bean;
                        } catch (BeansException ignore) {
                        }
                    }
                    if ((next == null) && (current instanceof Map)) {
                        next = ((Map<?, ?>)current).get(p);
                        if (next == null) {
                            @SuppressWarnings("rawtypes")
                            Iterator<Map.Entry<?, ?>> it = ((Map)current).entrySet().iterator();
                            while (it.hasNext() && (next == null)) {
                                Map.Entry<?, ?> entry = it.next();
                                if ((entry.getKey() != null) && p.equals(entry.getKey().toString())) {
                                    next = entry.getValue();
                                }
                            }
                        }
                        
                    }
                }
            } else {
                try {
                    BeanInfo beanInfo = Introspector.getBeanInfo(current.getClass());
                    for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                        if (propertyDescriptor.getName().equals(p)) {
                            Method readMethod = propertyDescriptor.getReadMethod();
                            if (readMethod == null) {
                                return null;
                            }
                            try {
                                next = readMethod.invoke(current, EMPTY_ARRAY);
                                break;
                            } catch (Exception ei) {
                                throw new BeanAccessException(path.substring(1, l), ei);
                            }
                        }
                    }
                } catch (IntrospectionException ex) {
                    throw new BeanAccessException(path.substring(1, l), new RuntimeException(ex));
                }
            }

            if (next == null) {
                throw new BeanAccessException(path.substring(1, l), new IllegalArgumentException("Unknown attribute '" + p + "'"));
            } else {
                current = next;
            }

            if (current instanceof ApplicationContext) {
                result.lastContext = (ApplicationContext)current;
            }
            
            i = i + 1;
        }
        result.resultObject = current;
        return result;
    }

    /**
     * This method set model parameters needed for preseting given object.
     * It sets following model parameters:
     * <ul>
     *   <li>beans - list of {@link BeanDef} objects - if object is of {@link ConfigurableApplicationContext} type.</li>
     *   <li>map - list of {@link BeanDef} objects - if object is of {@link Map} type.</li>
     *   <li>collection - list of {@link BeanDef} objects - if object is of {@link Collection} type.</li>
     *   <li>properties - list of {@link BeanDef} objects - object accessible properties.</li>
     *   <li>methods - list of {@link MethodDescriptor} object - methods that are not used for properties' access.</li>
     * </lu>
     *
     * @param object
     * @param result
     */
    @SuppressWarnings("unchecked")
    public static void prepare(Object object, Map<String, Object> result) {
        Set<BeanDef> properties = new LinkedHashSet<BeanDef>();
        Set<MethodDescriptor> methods = new LinkedHashSet<MethodDescriptor>();

        result.put("properties", properties);
        result.put("methods", methods);

        if (object instanceof ConfigurableApplicationContext) {
            Set<BeanDef> beans = new LinkedHashSet<BeanDef>();
            ConfigurableListableBeanFactory factory = ((ConfigurableApplicationContext)object).getBeanFactory();
            Set<String> names = new HashSet<String>();

            String[] beanNames = factory.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition)factory.getBeanDefinition(beanName);
                String access = null;
                String value = null;
                String type = null;
                boolean followable = true;
                if (beanDefinition.isSingleton() && !beanDefinition.isAbstract()) {
                    access = BeanDef.RO;
                    Object bean = factory.getBean(beanName);
                    value = toString(bean);
                    type = toTypeString(bean);
                    followable = isFollowable(bean);
                } else {
                    access = BeanDef.NO_ACCESS;
                    value = "<no access - not a singleton bean>";
                    type = beanDefinition.getBeanClassName();
                    followable = false;
                    type = "";
                }
                beans.add(new BeanDef(beanName, "", type, access, value, followable));
                names.add(beanName);
            }

            beanNames = factory.getSingletonNames();
            for (String beanName : beanNames) {
                if (!names.contains(beanName)) {
                    String access = BeanDef.RO;
                    Object bean = factory.getBean(beanName);
                    String value = toString(bean);
                    String type = toTypeString(bean);
                    boolean followable = isFollowable(bean);
                    beans.add(new BeanDef(beanName, "", type, access, value, followable));
                }
            }

            result.put("beans", beans);
        } else if (object instanceof Map) {
            Set<BeanDef> elements = new LinkedHashSet<BeanDef>();
            Map<Object, Object> map = (Map<Object, Object>)object;
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                String key = toString(entry.getKey());
                Object val = entry.getValue();
                String value = toString(val);
                String type = toTypeString(val);
                boolean followable = isFollowable(val);
                BeanDef def = new BeanDef(key, "", type, BeanDef.RO, value, followable);
                elements.add(def);
            }
            result.put("map", elements);
        } else if (object instanceof Collection) {
            Set<BeanDef> elements = new LinkedHashSet<BeanDef>();
            for (Object obj : (Collection<?>)object) {
                String value = toString(obj);
                String type = toTypeString(obj);
                boolean followable = isFollowable(obj);
                BeanDef def = new BeanDef(null, "", type, BeanDef.RO, value, followable);
                elements.add(def);
            }
            result.put("collection", elements);
        } else if (object instanceof Object[]) {
            Set<BeanDef> elements = new LinkedHashSet<BeanDef>();
            for (Object obj : (Object[])object) {
                String value = toString(obj);
                String type = toTypeString(obj);
                boolean followable = isFollowable(obj);
                BeanDef def = new BeanDef(null, "", type, BeanDef.RO, value, followable);
                elements.add(def);
            }
            result.put("collection", elements);
        }

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                String name = propertyDescriptor.getDisplayName();
                String access = getAccessType(propertyDescriptor);
                if (!BeanDef.WO.equals(access) && !BeanDef.WO_RAW.equals(access)) {
                    Class<?> cls = propertyDescriptor.getPropertyType();
                    String type = toTypeString(cls);
                    String value = null;
                    boolean followable = false;
                    if (BeanDef.RW.equals(access) || BeanDef.RO.equals(access) || BeanDef.RW_RAW.equals(access) || BeanDef.RO_RAW.equals(access)) {
                        Object val = getPropertyValue(object, propertyDescriptor);
                        value = toString(val);
                        followable = isFollowable(cls);
                    } else {
                        value = "";
                    }
                    BeanDef def = new BeanDef(name, propertyDescriptor.getShortDescription(), type, access, value, followable);
                    properties.add(def);
                }
            }

            for (MethodDescriptor methodDescriptor : getMethodDescriptors(beanInfo)) {
                methods.add(methodDescriptor);
            }
        } catch (IntrospectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Presents given object as a string.
     *
     * @param object object
     * @return string representation of the object
     */
    public static String toString(Object object) {
        if (object == null) {
            return "&lt;null&gt;";
        }
        PropertyEditor propertyEditor = PropertyEditorManager.findEditor(object.getClass());
        if (propertyEditor == null) {
            return object.toString();
        }
        propertyEditor.setValue(object);
        return propertyEditor.getAsText();

    }

    /**
     * Presetns a class as a string
     *
     * @param type the class
     * @return string representation of the class
     */
     public static String toTypeString(Class<?> type) {
        if (type.isArray()) {
            return "[" + type.getComponentType().getName() + "]";
        } else {
            return type.getName();
        }
    }

    /**result.toString()
     * Presetns the object's class as a string
     *
     * @param object  the class
     * @return string representation of the object's class
     */
    public static String toTypeString(Object object) {
        if (object == null) {
            return "&lt;null&gt;";
        } else {
            return toTypeString(object.getClass());
        }
    }

    /**
     * Creates name of the bean from the given path.
     *
     * @param path the path
     * @return name of the bean as last part of the path
     */
    protected static String createName(String path) {
        if (path.length() == 0) {
            return "Root Bean";
        } else {
            int i = path.lastIndexOf('/');
            if (i > 0) {
                return path.substring(path.lastIndexOf("/") + 1);
            } else {
                return path.substring(1);
            }
        }
    }

    /**
     * Creates path separated with &quot;.&quot;. If path element contains dot then it is
     * going to be enclosed in &lt; and &gt; symbols.
     * @param path the path
     * @return string representation of the path
     */
    public static String createPath(String path) {
        if (path.length() == 0) {
            return "/";
        }
        return path;
    }

    /**
     * This method ensures that component path is or empty string or a path that starts and ends with &quot;/&quot;
     * @param path component path
     * @return updated path
     */
    public static String createResourcePath(String path) {
        if ((path == null) || "/".equals(path)) {
            return "";
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        } else {
            return path;
        }
    }

    /**
     * Converts &quot;/&quot; delimited string into the array
     * @param path &quot;/&quot; delimited
     * @return an array of elements
     */
    public static String[] convertPath(String path) {
        if ((path == null) || "".equals(path) || "/".equals(path)) {
            return new String[0];
        }

        int i = 0;
        int l = 0;
        if (path.startsWith("/")) {
            i = path.indexOf('/', 1);
            l = 1;
        } else {
            i = path.indexOf('/');
        }
        if (i >= 0) {
            ArrayList<String> pathList = new ArrayList<String>();
            while (i > 0) {
                pathList.add(path.substring(l, i));
                l = i + 1;
                i = path.indexOf('/', i+1);
            }
            pathList.add(path.substring(l));
            String[] ps = new String[pathList.size()];
            return pathList.toArray(ps);
        } else {
            return new String[]{path};
        }
    }

    /**
     * Returns access type as in {@link BeanDef} for the given property
     *
     * @param property the property
     * @return access type as in {@link BeanDef}
     */
    public static String getAccessType(PropertyDescriptor property) {
        Method readMethod = property.getReadMethod();
        Method writeMethod = property.getWriteMethod();
        if ((readMethod != null) && (writeMethod != null)) {
            if (PropertyEditorManager.findEditor(readMethod.getReturnType()) != null) {
                return BeanDef.RW;
            } else {
                return BeanDef.RW_RAW;
            }
        } else if (readMethod != null) {
            if (PropertyEditorManager.findEditor(readMethod.getReturnType()) != null) {
                return BeanDef.RO;
            } else {
                return BeanDef.RO_RAW;
            }
        } else if (writeMethod != null) {
            if (PropertyEditorManager.findEditor(writeMethod.getParameterTypes()[0]) != null) {
                return BeanDef.WO;
            } else {
                return BeanDef.WO_RAW;
            }
        }
        return BeanDef.NO_ACCESS;
    }

    /**
     * Returns property's value of the given bean
     *
     * @param bean the bean
     * @param property the property
     * @return dereferenced property
     */
    public static Object getPropertyValue(Object bean, PropertyDescriptor property) {
        Method readMethod = property.getReadMethod();
        if (readMethod == null) {
            return null;
        }
        try {
            return readMethod.invoke(bean, EMPTY_ARRAY);
        } catch (Exception e) {
            StringWriter res = new StringWriter();
            res.append("Error on read method: " + readMethod.toString() + "\n");
            e.printStackTrace(new PrintWriter(res));
            return res.toString();
        }
    }

    /**
     * Returns property's value as a string. It uses {@link #toString()} method.
     *
     * @param object the bean
     * @param property the property
     * @return property's value as a string
     */
    public static String getPropertyValueAsString(Object object, String propertyName) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());

            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                String name = propertyDescriptor.getDisplayName();
                if (name.equals(propertyName)) {
                    return getPropertyValueAsString(object, propertyDescriptor);
                }
            }
        } catch (IntrospectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns property's value as a string. It uses {@link #toString()} method.
     *
     * @param bean the bean
     * @param property the property
     * @return property's value as a string
     */
    public static String getPropertyValueAsString(Object bean, PropertyDescriptor property) {
        Method readMethod = property.getReadMethod();
        if (readMethod == null) {
            return "";
        }
        try {
            Object value = readMethod.invoke(bean, EMPTY_ARRAY);
            return toString(value);
        } catch (Exception e) {
            StringWriter res = new StringWriter();
            res.append("Error on read method: " + readMethod.toString() + "\n");
            e.printStackTrace(new PrintWriter(res));
            return res.toString();
        }
    }

    /**
     * This method returns collection of method descriptors for given beanInfo.
     * It filters out properties (getter and setter method) as well as methods
     * whose parameters do not have property editors
     * @param beanInfo
     * @return collection of methods
     */
    public static Collection<MethodDescriptor> getMethodDescriptors(BeanInfo beanInfo) {
        Set<Method> attributes = new HashSet<Method>();
        for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
            Method readMethod = property.getReadMethod();
            if (readMethod != null) {
                attributes.add(readMethod);
            }
            Method writeMethod = property.getWriteMethod();
            if ((writeMethod != null) && (readMethod != null)) {
                attributes.add(writeMethod);
            }
        }

        ArrayList<MethodDescriptor> result = new ArrayList<MethodDescriptor>();

        for (MethodDescriptor methodDescriptor : beanInfo.getMethodDescriptors()) {
            Method method = methodDescriptor.getMethod();
            if (!attributes.contains(method)) {
                boolean ok = true;
                Class<?>[] types = method.getParameterTypes();
                for (int j = 0; j < types.length; j++) {
                    PropertyEditor propertyEditor = PropertyEditorManager.findEditor(types[j]);
                    ok = ok && (propertyEditor != null);
                }
                if (ok) {
                    result.add(methodDescriptor);
                }
            }
        }
        return result;
    }

    /**
     * Returns <code>true</code> if given class is not {@link String} and not a primitive type.
     * @param cls the class
     * @return <code>true</code> if given class is not {@link String} and not a primitive type.
     */
    public static boolean isFollowable(Class<?> cls) {
        return !String.class.equals(cls) && !cls.isPrimitive()/* && !cls.getClass().isArray()*/;
    }

    /**
     * Returns <code>true</code> if given object is not null and its class is not {@link String} and not a primitive type.
     * @param object the object
     * @return <code>true</code> if given object is not null and its class is not {@link String} and not a primitive type.
     */
    public static boolean isFollowable(Object object) {
        return (object != null) && isFollowable(object.getClass());
    }

    /**
     * Returns stack trace of given throwable object as a string.
     * @param t the throwable object
     * @return stack trace of given throwable object as a string
     */
    public static String stackTrace(Throwable t) {
        StringWriter res = new StringWriter();
        PrintWriter out = new PrintWriter(res);
        t.printStackTrace(out);
        out.flush();
        return res.toString();
    }
    
    public static class Result {
        public Object resultObject;
        public ApplicationContext lastContext;
    }
}
