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
 *   Criterion Design Concepts  - initial API and implementation
 *   
 */
package org.abstracthorizon.danube.beanconsole.gwt.client;

import org.abstracthorizon.danube.beanconsole.gwt.client.decorator.DBCDecorator;
import org.abstracthorizon.danube.beanconsole.gwt.client.decorator.DanubeGUIDecorator;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * 
 *
 * @author Daniel Sendula
 * @author Mile Lukic
 */
public class Application  {

    public static String basePath;
    
    public Application() {
    }
    
    public void onModuleLoad() {
        
        basePath = GWT.getHostPageBaseURL();
        if (!basePath.endsWith("/")) {
            basePath = basePath + "/";
        }
        
        SC.logWarn("base path variable is \"" + getBasePath() + "\"");
        
        SC.logWarn("Host page base URL: " + GWT.getHostPageBaseURL());
        SC.logWarn("Module base URL: " + GWT.getModuleBaseURL());
        SC.logWarn("Module name: " + GWT.getModuleName());
        SC.logWarn("History token: " + History.getToken());
        
        final DataModel model = new DataModel();
        //Replace the decorator class instantiated here in order to create the console with new graphics
        DBCDecorator decorator = new DanubeGUIDecorator(model);
        VLayout container = decorator.getContainer();
        
        //create the main console and add it to the GUI
        MainConsole console = new MainConsole(model);        
        decorator.addConsoleComponent(console);
        decorator.addStatusBar(console.getStatusBar());
        
        String startURL = History.getToken();
        if (!startURL.startsWith("/")) {
            startURL = "/" + startURL;
        }

        ClientServerTransaction transaction = new ClientServerTransaction(startURL, model);
        
        transaction.sendGetRequest();
        
        container.draw();

        History.addValueChangeHandler(new ValueChangeHandler<String> () {

            public void onValueChange(ValueChangeEvent<String> event) {
                SC.logWarn("History token changed to: " + event.getValue());

                String url = History.getToken();
                if (!url.startsWith("/")) {
                    url = "/" + url;
                }
                
                ClientServerTransaction transaction = new ClientServerTransaction(url, model);
                
                transaction.sendGetRequest();
            }
            
        });
        
    }

    public native String getBasePath()/*-{
        return $wnd.basePathVar;
    }-*/;

}
