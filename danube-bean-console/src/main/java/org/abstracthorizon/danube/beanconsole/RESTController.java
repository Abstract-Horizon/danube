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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.abstracthorizon.danube.beanconsole.data.Bean;
import org.abstracthorizon.danube.beanconsole.data.Definitions;
import org.abstracthorizon.danube.beanconsole.data.Entry;
import org.abstracthorizon.danube.beanconsole.data.Type;
import org.abstracthorizon.danube.beanconsole.util.MimeMediaType;
import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.session.HTTPSessionManager;
import org.abstracthorizon.danube.http.session.Session;
import org.abstracthorizon.danube.http.session.SimpleSessionManager;
import org.abstracthorizon.danube.http.util.MultiStringMap;
import org.abstracthorizon.danube.mvc.Controller;
import org.abstracthorizon.danube.mvc.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Controller that processes REST requests.
 *
 * @author Daniel Sendula
 */
public class RESTController implements Controller, ApplicationContextAware {

    public static final String NULL = "&lt;null&gt;";
    
    protected static Logger logger = LoggerFactory.getLogger(RESTController.class);
    
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

    /** Session manager */
    protected HTTPSessionManager sessionManager;

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
    @SuppressWarnings("unchecked")
    public ModelAndView handleRequest(Connection connection) {
        HTTPConnection httpConnection = connection.adapt(HTTPConnection.class);

        Map<String, Object> model = new HashMap<String, Object>();

        String path = httpConnection.getComponentResourcePath();
        
        String rootPath = httpConnection.getContextPath();
        if (rootPath.endsWith("/")) {
            rootPath = rootPath.substring(0, rootPath.length() - 1);
        }
        String thisPath = httpConnection.getContextPath() + componentPath;
        if (thisPath.endsWith("/")) {
            thisPath = thisPath.substring(0, thisPath.length() - 1);
        }
        if (path.startsWith("/")) {
            thisPath = thisPath + path;
        } else {
            thisPath = thisPath + "/" + path;
        }
        if (!thisPath.endsWith("/")) {
            thisPath = thisPath + "/";
        }
        
        model.put("uri", thisPath);
        model.put("connection", connection);
        
        SelectedBean selectedBean = new SelectedBean();
        selectedBean.beanDef = new Bean();
        selectedBean.beanDef.setName("&lt;root&gt;");
        selectedBean.beanDef.setPath("");
        selectedBean.beanObject = getRootObject();
        
        Map<String, Object> savedBeans = null;
        boolean createdMap = false;
        
        HTTPSessionManager sessionManager = getSessionManager();
        Session session = (Session)sessionManager.findSession(httpConnection, false);
        if (session != null) {
            savedBeans = (Map<String, Object>)session.getAttributes().get("savedBeans");
        }
        
        if (savedBeans == null) {
            savedBeans = new HashMap<String, Object>();
            createdMap = true;
        }
        
        followPath(selectedBean, path, httpConnection, savedBeans);
        updateBean(selectedBean, savedBeans);
        
        if (!savedBeans.isEmpty() && createdMap) {
            if (session == null) {
                session = (Session)sessionManager.findSession(httpConnection, true);
            }
            session.getAttributes().put("savedBeans", savedBeans);
        }
        
        if (selectedBean.beanDef.getPath().length() > 0) {
            model.put("topUri", rootPath);
            String parentPath = thisPath.substring(0, thisPath.length() - 1);
            int j = parentPath.lastIndexOf('/');
            if (j >= 0) {
                parentPath = parentPath.substring(0, j);
            }
            model.put("backUri", parentPath);
        }
        
        model.put("bean", selectedBean.beanDef);
        
        String accept = httpConnection.getRequestHeaders().getOnly("Accept");
        Set<MimeMediaType> accepts = getAcceptsMediaTypes(accept);
        
        Iterator<MimeMediaType> iterator = accepts.iterator();
        String view = null;
        while (iterator.hasNext() && (view == null)) {
            MimeMediaType m = iterator.next();
            if (m.compareTypeOnly("text", "html")) {
                if ("/".equals(path)) {
                    view = "html/index";
                } else {
                    view = "html/bean";
                    view = "html/redirect";
                }
            } else if (m.compareTypeOnly("text", "xml") || m.compareTypeOnly("application", "xml")) {
                view = "xml/bean";
            } else if (m.compareTypeOnly("text", "json") || m.compareTypeOnly("application", "json")) {
                view = "json/bean";
            } else if (m.compareTypeOnly("text", "plain")) {
                view = "text/bean";
            }
        }

        if (view == null) {
            view = "text/bean";
        }
        
        ModelAndView modelAndView = new ModelAndView(view, model);
        return modelAndView;
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


    /**
     * Sets session manager
     * @param sessionManager http session manager
     */
    public void setSessionManager(HTTPSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Returns session manaager
     * @return http session manager
     */
    public HTTPSessionManager getSessionManager() {
        if (sessionManager == null) {
            sessionManager = new SimpleSessionManager();
        }
        return sessionManager;
    }    
    
    public Set<MimeMediaType> getAcceptsMediaTypes(String accept) {
            
        TreeSet<MimeMediaType> acceptsMediaType = new TreeSet<MimeMediaType>();
            
        if (accept == null) {
            accept = "text/html";
        }
        
        String[] typeStrings = accept.split(",");
        for (String t : typeStrings) {
            MimeMediaType mt = MimeMediaType.mediaType(t);
            acceptsMediaType.add(mt);
        }
        return acceptsMediaType;
    }

    protected static class SelectedBean {
        protected Object beanObject;
        protected Bean beanDef;
    }
    
    @SuppressWarnings("unchecked")
    protected boolean followPath(SelectedBean sb, String path, HTTPConnection connection, Map<String, Object> savedBeans) {
        sb.beanDef.setPath(path);

        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        boolean firstSegment = true;
        while (path.length() > 0) {
            int i = path.indexOf('/');
            String segment = path;
            if (i >= 0) {
                segment = path.substring(0, i);
                path  = path.substring(i + 1);
            } else {
                path = "";
            }
            
            Object res = null;
            Object o = sb.beanObject;
            if (segment.startsWith("b_")) {
                if (o instanceof ApplicationContext) {
                    ApplicationContext applicationContext = (ApplicationContext)o;
                    String name = segment.substring(2);
                    res = applicationContext.getBean(name);
                    if (res != null) {
                        sb.beanDef.setName(name);
                    } else {
                        // TODO describe path better
                        prepareException(sb, new IllegalArgumentException("No bean named "+ name), savedBeans);
                        return true;
                    }
                } else if (o instanceof BeanFactory) {
                    BeanFactory beanFactory = (BeanFactory)o;
                    String name = segment.substring(2);
                    res = beanFactory.getBean(name);
                    if (res != null) {
                        sb.beanDef.setName(name);
                    } else {
                        // TODO describe path better
                        prepareException(sb, new IllegalArgumentException("No bean named "+ name), savedBeans);
                        return true;
                    }
                } else {
                    // TODO describe path better
                    prepareException(sb, new IllegalArgumentException("Not an ApplicationContext or BeanFactory @ "+ segment), savedBeans);
                    return true;
                }
            } else if (segment.startsWith("s_") && firstSegment) {
                String name = segment.substring(2);
                res = savedBeans.get(name);
                if (res != null) {
                    sb.beanDef.setName(name);
                } else {
                    // TODO describe path better
                    prepareException(sb, new IllegalArgumentException("No saved bean with name "+ name), savedBeans);
                    return true;
                }
            } else if (segment.startsWith("i_")) {
                String indexString = segment.substring(2).trim();
                try {
                    int index = Integer.parseInt(indexString);
                    if (o instanceof List) {
                        List<?> list = (List<?>)o;
                        res = list.get(index);
                        if (res != null) {
                            sb.beanDef.setName("");
                        }
                    } else if (o != null) {
                        Class<?> c = o.getClass();
                        if (c.isArray()) {
                            if (!c.getComponentType().isPrimitive()) {
                                Object[] a = (Object[])o;
                                res = a[index];
                                if (res != null) {
                                    sb.beanDef.setName("");
                                }
                            } else {
                                res = java.lang.reflect.Array.get(o, index);
                                if (res != null) {
                                    sb.beanDef.setName("");
                                }
                            }
                        } else if (o instanceof Iterable<?>) {
                            Iterator<?> it = ((Iterable<?>)o).iterator();
                            i = index;
                            while (i >= 0) {
                                if (!it.hasNext()) {
                                    return false;
                                }
                                o = it.next();
                                i--;
                            }
                            res = o;
                            sb.beanDef.setName("");
//                            return true;
                        } else {
                            // TODO describe path better
                            prepareException(sb, new IllegalArgumentException("Wrong class of object for "+ segment), savedBeans);
                            return true;
                        }
                    } else {
                        prepareException(sb, new NullPointerException(segment), savedBeans);
                        return true;
                    }
                } catch (NumberFormatException e) {
                    prepareException(sb, e, savedBeans);
                    return true;
                } catch (IndexOutOfBoundsException e) {
                    prepareException(sb, e, savedBeans);
                    return true;
                }
            } else if (segment.startsWith("k_")) {
                String key = segment.substring(2);
                if (o instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>)o;
                    res = map.get(key);
                    if ((res == null) && (key.length() == 0)) { 
                        res = map.get(null);
                    }
                    if (res == null) {
                        @SuppressWarnings("rawtypes")
                        Iterator<Map.Entry<?, ?>> it = (Iterator)map.entrySet().iterator();
                        boolean found = false;
                        while ((res == null) && it.hasNext() && !found) {
                            Map.Entry<?, ?> entry = it.next();
                            Object k = entry.getKey();
                            if ((k != null) && k.toString().equals(key)){
                                res = entry.getValue();
                                found = true;
                            } else if ((k == null) && (key.length() == 0)) {
                                res = entry.getValue();
                                found = true;
                            }
                        }
                        if (!found) {
                            prepareException(sb, new IllegalArgumentException("No entry for "+ segment), savedBeans);
                            return true;
                        }
                    }
                    sb.beanDef.setName(key);
                }
            } else if (segment.startsWith("p_")) {
                try {
                    BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass());
                    String propertyName = segment.substring(2);
    
                    boolean found = false;
                    PropertyDescriptor[] propertyDescIterators = beanInfo.getPropertyDescriptors();
                    int j = 0;
                    while (!found && (j < propertyDescIterators.length)) {
                        PropertyDescriptor propertyDescriptor = propertyDescIterators[j];
                        String name = propertyDescriptor.getDisplayName();
                        if (propertyName.equals(name)) {
                            if ("GET".equals(connection.getRequestMethod())) {
                                if (propertyDescriptor.getReadMethod() != null) {
                                    res = propertyDescriptor.getReadMethod().invoke(o);
    
                                    sb.beanDef.setName(propertyName);
                                    sb.beanObject = res;
                                    found = true;
                                }
                            } else if ("POST".equals(connection.getRequestMethod())) {
                                if (propertyDescriptor.getWriteMethod() == null) {
                                    throw new RuntimeException("Setter for property " + propertyName + " is missing.");
                                }
                                
                                String type = connection.getRequestHeaders().getOnly("Content-Type");
                                if (type == null) {
                                    type = "";
                                }
                                if (type.startsWith("text/plain")) {
                                    try {
                                        Reader reader = connection.adapt(Reader.class);
                                        StringBuffer content = new StringBuffer();
                                        char[] buf = new char[1024];
                                        
                                        int r = reader.read(buf);
                                        while (r > 0) {
                                            content.append(buf, 0, r);
                                            r = reader.read(buf);
                                        }
                                        
                                        Method writeMethod = propertyDescriptor.getWriteMethod();
                                        
                                        PropertyEditor writePropertyEditor = PropertyEditorManager.findEditor(writeMethod.getParameterTypes()[0]);
                                        if (writePropertyEditor != null) {
                                            writePropertyEditor.setAsText(content.toString());
                                            writeMethod.invoke(o, new Object[]{writePropertyEditor.getValue()});
                                        } else {
                                            throw new RuntimeException("No editor for property " + propertyName + " of type "+ writeMethod.getParameterTypes()[0]);
                                        }
                                        
                                        if (propertyDescriptor.getReadMethod() != null) {
                                            res = propertyDescriptor.getReadMethod().invoke(o);
                                            sb.beanDef.setName(propertyName);
                                            sb.beanObject = res;
                                        } else {
                                            String p = sb.beanDef.getPath(); 
                                            i = p.lastIndexOf('/');
                                            if (i > 0) {
                                                p = p.substring(0, i);
                                                sb.beanDef.setPath(p);
                                            }
                                            
                                            logger.warn("NO WRITE METHOD for " + propertyName);
                                        }
                                        found = true;
                                    } catch (IOException e) {
                                        prepareException(sb, e, savedBeans);
                                        return true;
                                    }
                                } else {
                                    prepareException(sb, new IllegalArgumentException("Content-Type not supported " + connection.getRequestHeaders().getOnly("Content-Type")), savedBeans);
                                    return true;
                                }
                            }
                        }
                        j++;
                    }
                    if (!found) {
                        // TODO describe path better
                        prepareException(sb, new IllegalArgumentException(propertyName), savedBeans);
                        return true;
                    }
                } catch (IntrospectionException e) {
                    prepareException(sb, e, savedBeans);
                    return true;
                } catch (IllegalArgumentException e) {
                    prepareException(sb, e, savedBeans);
                    return true;
                } catch (IllegalAccessException e) {
                    prepareException(sb, e, savedBeans);
                    return true;
                } catch (InvocationTargetException e) {
                    prepareException(sb, e, savedBeans);
                    return true;
                }
            } else if (segment.startsWith("m_")) {
                MultiStringMap parameters = connection.getRequestParameters();
                try {
                    String name = segment.substring(2);
                    
                    ArrayList<Class<?>> paramTypes = new ArrayList<Class<?>>();
                    ArrayList<Object> paramValues = new ArrayList<Object>();
                    int j = 0;
                    boolean more = true;
                    while (more) {
                        String type = parameters.getOnly(j + ".type");
                        if (type == null) {
                            more = false;
                        } else {
                            String value = parameters.getOnly(j + ".value");
                            ClassLoader cl = Thread.currentThread().getContextClassLoader();
                            Class<?> c;
                            c = cl.loadClass(type);
                            paramTypes.add(c);
                            paramValues.add(value);
                        }
                        j++;
                    }
                    
                    Class<?>[] paramTypesArray = new Class<?>[paramTypes.size()];
                    paramTypesArray = paramTypes.toArray(paramTypesArray);
                    Method method = o.getClass().getMethod(name, paramTypesArray);
                    
                    Object[] args = new Object[paramTypesArray.length];
                    args = paramValues.toArray(args);

                    
                    res = method.invoke(o, args);

                    
                    StringBuilder fullName = new StringBuilder();
                    fullName.append(name);
                    fullName.append('(');
                    boolean first = true;
                    for (Class<?> c : paramTypes) {
                        if (first) { first = false; } else { fullName.append(','); }
                        fullName.append(c.getName());
                    }
                    fullName.append(')');

                    sb.beanDef.setPath("/s_lastExecution");
                    sb.beanDef.setName(fullName.toString());
                    sb.beanObject = res;
                    savedBeans.put("lastExecution", res);
                    return true;
                    
                } catch (Exception e) {
                    prepareException(sb, e, savedBeans);
                    return true;
                }

                
            }
            if (res == null) {
                return false;
            }

            sb.beanObject = res;
            o = res;
            
            firstSegment = !firstSegment;
        } // while
        return true;
    }
    
    protected void prepareException(SelectedBean sb, Exception e, Map<String, Object> savedBeans) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        sb.beanDef.setPath("/s_lastExecution");
        sb.beanDef.setName("Exception");
        sb.beanObject = sw.toString();

        savedBeans.put("lastExecution", sb.beanObject);
    }

    protected void updateBean(SelectedBean sb, Map<String, Object> savedBeans) {
        if (sb.beanObject != null) {
            sb.beanDef.setType(sb.beanObject.getClass().getName());
            sb.beanDef.setValue("");
            sb.beanDef.setFollowable(true);
            if ((sb.beanObject instanceof CharSequence) || sb.beanObject.getClass().isPrimitive()) {
                sb.beanDef.setValue(sb.beanObject.toString());
                sb.beanDef.setFollowable(false);
            } else if (sb.beanObject.getClass().isPrimitive() 
                        || (sb.beanObject instanceof Character)
                        || (sb.beanObject instanceof Boolean)
                        || (sb.beanObject instanceof Byte)
                        || (sb.beanObject instanceof Short)
                        || (sb.beanObject instanceof Integer)
                        || (sb.beanObject instanceof Long)
                        || (sb.beanObject instanceof Float)
                        || (sb.beanObject instanceof Double)
                        ) {
                sb.beanDef.setValue(sb.beanObject.toString());
                sb.beanDef.setFollowable(false);
            } else {
                if (sb.beanObject instanceof ApplicationContext) {
                    TreeSet<String> names = new TreeSet<String>();
                    if (sb.beanObject instanceof ConfigurableApplicationContext) {
                        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext)sb.beanObject;
                        if (applicationContext.getBeanFactory() instanceof ConfigurableListableBeanFactory) {
                            ConfigurableListableBeanFactory configurableListableBeanFactory = (ConfigurableListableBeanFactory)applicationContext.getBeanFactory();
                            names.addAll(Arrays.asList(configurableListableBeanFactory.getSingletonNames()));
                        }
                    }
                    ApplicationContext applicationContext = (ApplicationContext)sb.beanObject;
                    names.addAll(Arrays.asList(applicationContext.getBeanDefinitionNames()));
    
                    ArrayList<Entry> beans = new ArrayList<Entry>();
                    for (String name : names) {
                        Object o = applicationContext.getBean(name);
                        if (o != null) {
                            Entry entry = new Entry();
                            entry.setName(name);
                            entry.setFollowable(true);
                            entry.setType(o.getClass().getName());
                            entry.setValue(getValue(o));
                            entry.setAccess(Definitions.RW);
                            entry.setPath(sb.beanDef.getPath() + "/b_" + name);
                            beans.add(entry);
                        } else {
                            logger.warn("Bean with name \"" + name + "\" is missing?!");
                        }
                    }
                    sb.beanDef.setBeans(beans);
                }
                
                if (sb.beanObject instanceof List<?>) {
                    ArrayList<Entry> collection = new ArrayList<Entry>();
    
                    List<?> list = (List<?>)sb.beanObject;
                    for (int i = 0; i < list.size(); i++) {
                        Object o = list.get(i);
                        
                        Entry entry = new Entry();
                        entry.setName(Integer.toString(i));
                        entry.setFollowable(true);
                        entry.setType(o.getClass().getName());
                        entry.setValue(getValue(o));
                        entry.setAccess(Definitions.RW);
                        entry.setPath(sb.beanDef.getPath() + "/i_" + i);
                        collection.add(entry);
                    }
                    sb.beanDef.setCollection(collection);
                } else if (sb.beanObject instanceof Iterable<?>) {
                    ArrayList<Entry> collection = new ArrayList<Entry>();
    
                    Iterable<?> list = (Iterable<?>)sb.beanObject;
                    int i = 0;
                    for (Object o : list) {
                        Entry entry = new Entry();
                        entry.setName(Integer.toString(i));
                        entry.setFollowable(true);
                        entry.setType(o.getClass().getName());
                        entry.setValue(getValue(o));
                        entry.setAccess(Definitions.RW);
                        entry.setPath(sb.beanDef.getPath() + "/i_" + i);
                        collection.add(entry);
                        i++;
                    }
                    sb.beanDef.setCollection(collection);
                }
                
                ArrayList<Entry> saved = new ArrayList<Entry>();
                sb.beanDef.setSavedBeans(saved);
                
                if (!savedBeans.isEmpty()) {
                    for (Map.Entry<String, Object> bs : savedBeans.entrySet()) {
                        String name = bs.getKey();
                        Object o = bs.getValue();
                        Entry entry = new Entry();
                        entry.setName(name);
                        entry.setFollowable(true);
                        entry.setType(o.getClass().getName());
                        entry.setValue(getValue(o));
                        entry.setAccess(Definitions.RW);
                        entry.setPath(sb.beanDef.getPath() + "/s_" + name);
                        saved.add(entry);
                    }
                }
                
                if (sb.beanObject instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>)sb.beanObject;
                    ArrayList<Entry> keys = new ArrayList<Entry>();
                    for (Map.Entry<?, ?> mapEntry : map.entrySet()) {
                        Entry entry = new Entry();
                        if (mapEntry.getKey() != null) {
                            entry.setName(mapEntry.getKey().toString());
                        } else {
                            entry.setName("");
                        }
                        entry.setFollowable(true);
                        if (mapEntry.getValue() != null) {
                            entry.setType(mapEntry.getValue().getClass().getName());
                        } else {
                            entry.setType(NULL);
                        }
                        entry.setValue(getValue(mapEntry.getValue()));
                        entry.setAccess(Definitions.RW);
                        entry.setPath(sb.beanDef.getPath() + "/k_" + entry.getName());
                        keys.add(entry);
                    }
                    sb.beanDef.setMap(keys);
                }
                
                try {
                    BeanInfo beanInfo = Introspector.getBeanInfo(sb.beanObject.getClass());
                    for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                        String name = propertyDescriptor.getDisplayName();
                        String access = getAccessType(propertyDescriptor);
                        // if (!BeanDef.WO.equals(access) && !BeanDef.WO_RAW.equals(access)) {
                            Class<?> cls = propertyDescriptor.getPropertyType();
                            String type = toTypeString(cls);
                            String value = null;
                            boolean followable = false;
                            if (BeanDef.RW.equals(access) || BeanDef.RO.equals(access) || BeanDef.RW_RAW.equals(access) || BeanDef.RO_RAW.equals(access)) {
                                Object val = getPropertyValue(sb.beanObject, propertyDescriptor);
                                value = toString(val);
                                followable = isFollowable(cls);
                            } else {
                                value = "";
                            }
                            Entry entry = new Entry();
                            entry.setName(name);
                            entry.setDesc(propertyDescriptor.getShortDescription());
                            entry.setType(type);
                            entry.setAccess(access);
                            entry.setValue(value);
                            entry.setFollowable(followable);
        
                            sb.beanDef.getProperties().add(entry);
                        // }
                    }
        
                    for (org.abstracthorizon.danube.beanconsole.data.Method method : getMethodDescriptors(beanInfo)) {
                        sb.beanDef.getMethods().add(method);
                    }
                } catch (IntrospectionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            sb.beanDef.setType(NULL);
            sb.beanDef.setValue(NULL);
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
                return Definitions.RW;
            } else {
                return Definitions.RW_RAW;
            }
        } else if (readMethod != null) {
            if (PropertyEditorManager.findEditor(readMethod.getReturnType()) != null) {
                return Definitions.RO;
            } else {
                return Definitions.RO_RAW;
            }
        } else if (writeMethod != null) {
            if (PropertyEditorManager.findEditor(writeMethod.getParameterTypes()[0]) != null) {
                return Definitions.WO;
            } else {
                return Definitions.WO_RAW;
            }
        }
        return BeanDef.NO_ACCESS;
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

     /**
      * Presents given object as a string.
      *
      * @param object object
      * @return string representation of the object
      */
     public static String toString(Object object) {
         if (object == null) {
             return NULL;
         }
         PropertyEditor propertyEditor = PropertyEditorManager.findEditor(object.getClass());
         if (propertyEditor == null) {
             return object.toString();
         }
         propertyEditor.setValue(object);
         return propertyEditor.getAsText();

     }

     /**
      * Returns <code>true</code> if given class is not {@link String} and not a primitive type.
      * @param cls the class
      * @return <code>true</code> if given class is not {@link String} and not a primitive type.
      */
     public static boolean isFollowable(Class<?> cls) {
         return !String.class.equals(cls) && !cls.isPrimitive()/* && !cls.getClass().isArray()*/;
     }

     /** Empty object array */
     public static final Object[] EMPTY_ARRAY = new Object[0];

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
      * This method returns collection of method descriptors for given beanInfo.
      * It filters out properties (getter and setter method) as well as methods
      * whose parameters do not have property editors
      * @param beanInfo
      * @return collection of methods
      */
     public static Collection<org.abstracthorizon.danube.beanconsole.data.Method> getMethodDescriptors(BeanInfo beanInfo) {
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

         ArrayList<org.abstracthorizon.danube.beanconsole.data.Method> result = new ArrayList<org.abstracthorizon.danube.beanconsole.data.Method>();

         for (MethodDescriptor methodDescriptor : beanInfo.getMethodDescriptors()) {
             Method method = methodDescriptor.getMethod();
             boolean exclude = attributes.contains(method);
             if (!exclude 
                     && !method.getName().equals("hashCode")
                     && !method.getName().equals("toString")
                     && method.getDeclaringClass().getName().equals("java.lang.Object")) {
                 
                 exclude = true;
             }
             
             if (!exclude) {
                 boolean ok = true;
                 Class<?>[] types = method.getParameterTypes();
                 for (int j = 0; j < types.length; j++) {
                     PropertyEditor propertyEditor = PropertyEditorManager.findEditor(types[j]);
                     ok = ok && (propertyEditor != null);
                 }
                 if (ok) {
                     org.abstracthorizon.danube.beanconsole.data.Method m = new org.abstracthorizon.danube.beanconsole.data.Method();
                     String methodName = methodDescriptor.getName();
                     if (methodName.equals("")) {
                         methodName = method.getName();
                     }
                     m.setName(methodName);
                     m.setDesc(methodDescriptor.getShortDescription());
                     m.setType(methodDescriptor.getClass().getName());
                     List<Type> paramTypes = new ArrayList<Type>();
                     Class<?>[] pds = methodDescriptor.getMethod().getParameterTypes();
                     if (pds != null) {
                         for (Class<?> c : pds) {
                             Type t = new Type();
                             t.setType(c.getName());
                             paramTypes.add(t);
                         }
                     }
                     m.setParameterTypes(paramTypes);
                     result.add(m);
                 }
             }
         }
         return result;
     }

     /**
      * Returns property's value of the given bean
      *
      * @param o object
      * @return dereferenced property
      */
     public static String getValue(Object o) {
         if (o == null) {
             return NULL;
         }
         PropertyEditor propertyEditor = PropertyEditorManager.findEditor(o.getClass());
         if (propertyEditor == null) {
             if (o.getClass().isArray()) {
                 int size = Array.getLength(o);
                 StringBuilder sb = new StringBuilder();
                 sb.append('[');
                 for (int i = 0; i < size; i++) {
                     if (i > 0) {
                         sb.append(',');
                     }
                     Object oo = Array.get(o, i);
                     sb.append(getValue(oo));
                 }
                 sb.append(']');
                 return sb.toString();
             } else {
                 return o.toString();
             }
         }
         propertyEditor.setValue(o);
         String value = propertyEditor.getAsText();
         return value;
     }
     
     public static boolean isPrimitive(Object o) {
         return (o == null) 
                     || (o instanceof CharSequence) 
                     || o.getClass().isPrimitive()
                     || o.getClass().isArray();         
     }
}
