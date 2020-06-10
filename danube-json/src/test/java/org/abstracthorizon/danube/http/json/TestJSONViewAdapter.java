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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.mvc.ModelAndView;
import org.junit.Test;

/**
 * This class expects entry called &quot;bean&quot; in model map. It will
 * be, then, be send as output in json format. 
 * 
 * @author Daniel Sendula
 */
public class TestJSONViewAdapter {

    @Test
    public void testJSON() {
        
        Bean bean = createBean();
        
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("bean", bean);
        
        ModelAndView modelAndView = new ModelAndView("nothing", map);
        
        StringWriterConnection result = new StringWriterConnection();
        
        JSONViewAdapter adapter = new JSONViewAdapter();
        adapter.render(result, modelAndView);

        System.out.println(result.toString());
    }

    public static Bean createBean() {

        Bean bean = new Bean();
        bean.setName("root");
        bean.setType("my.bean.type.Bean");
        bean.setPath("/");
        
        Bean bean1 = new Bean();
        bean1.setName("bean1");
        bean1.setType("java.util.String");
        bean1.setPath("/bean1");
        bean1.setFollowable(true);
        
        Bean bean2 = new Bean();
        bean2.setName("bean2");
        bean2.setType("my.bean.type.Bean");
        bean2.setPath("/bean2");
        bean2.setFollowable(true);
        
        Bean bean3 = new Bean();
        bean3.setName("bean3");
        bean3.setType("java.util.Something");
        bean3.setPath("/bean3");
        bean3.setFollowable(true);
        
        List<Entry> beans = new ArrayList<Entry>();
        bean.setBeans(beans);
        beans.add(bean1);
        beans.add(bean2);
        beans.add(bean3);

        Entry entry1 = new Entry();
        entry1.setName("property1");
        entry1.setType("java.lang.String");
        entry1.setPath("/bean1/property1");
        entry1.setValue("This is value");
        entry1.setAccess("rw");
        entry1.setFollowable(false);
        
        List<Entry> properties = new ArrayList<Entry>();
        bean1.setProperties(properties);
        properties.add(entry1);

        return bean;
    }
    
    public static class StringWriterConnection extends StringWriter implements Connection {

        private PrintWriter printWriter = new PrintWriter(this);
        
        public void close() {
        }

        public boolean isClosed() {
            return false;
        }

        @SuppressWarnings("unchecked")
        public <T> T adapt(Class<T> c) {
            return (T)printWriter;
        }
        
    }
}
