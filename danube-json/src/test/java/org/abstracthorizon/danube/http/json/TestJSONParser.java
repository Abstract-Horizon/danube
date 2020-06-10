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

import java.util.HashMap;

import org.abstracthorizon.danube.http.json.TestJSONViewAdapter.StringWriterConnection;
import org.abstracthorizon.danube.mvc.ModelAndView;
import org.junit.Test;

/**
 *
 * @author Daniel Sendula
 */
public class TestJSONParser {

    @Test
    public void testParser() {
        Bean bean = TestJSONViewAdapter.createBean();
        
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("bean", bean);
        
        ModelAndView modelAndView = new ModelAndView("nothing", map);
        
        StringWriterConnection result = new StringWriterConnection();
        
        JSONViewAdapter adapter = new JSONViewAdapter();
        adapter.render(result, modelAndView);


        System.out.println(result.toString());

//        JSONParser parser = new JSONParser();
//        
//        StringReader reader = new StringReader(result.toString());
//        Bean resultBean = new Bean();
//        parser.parse(resultBean, reader);
//        
//        System.out.println(resultBean);
        
    }
    
}
