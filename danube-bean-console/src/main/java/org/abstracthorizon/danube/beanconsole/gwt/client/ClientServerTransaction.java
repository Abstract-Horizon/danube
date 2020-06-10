/*
 * Copyright (c) 2009 Creative Sphere Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *   Criterion Design Concepts  - initial API and implementation
 *   Creative Sphere            - initial API and implementation
 *   
 */
package org.abstracthorizon.danube.beanconsole.gwt.client;

import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModel;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.FormItem;

/**
 * 
 * @author Mile Lukic
 * @author Daniel Sendula
 * 
 * This class handles the asynchronous remote procedure call to the Danube REST interface.
 * A JSON object is returned in response to the URL provided to the request. This class also 
 * updates the status bar in the Danube Bean Console and adds the JSON resource to the 
 * domain model. 
 */
public class ClientServerTransaction implements RequestCallback {

    // This is local debug switch
    private static final boolean DEBUG = false;
    
    private String url;
    private DataModel model;
    private String finalUrl;

    /**
     * Constructor.
     *  
     * @param url
     * @param model
     */
    public ClientServerTransaction(String url, DataModel model) {
         this.url = url;
         this.model = model;
         if (url.startsWith("/")) {
             finalUrl = Application.basePath + url.substring(1);
         } else {
             finalUrl = Application.basePath + url;
         }
    }

    /**
     * Builds the request and sends it. Updates the status bar.
     */
    public void sendGetRequest() {
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, finalUrl);
        if (DEBUG) {
            SC.logWarn("Sending GET request to " + finalUrl + "  for  " + url);
        }
        requestBuilder.setHeader("Accept", "application/json");
        requestBuilder.setCallback(this);
        try {
            StatusBar.setStatus("Sending request at " + url);
            requestBuilder.sendRequest(null, this);
            StatusBar.setStatus("Request sent " + url);
        } catch (RequestException e) {
            StatusBar.setStatus("Status: Got request exception " + e.getMessage());
        }
    }
    
    public void sendPostRequest(FormItem [] methodParams) {
        StringBuffer postData = new StringBuffer();
        if (methodParams.length > 0) {
            for (int i = 0; i < methodParams.length; i++) {
                if (i > 0) {
                    postData.append('&');
                }
                postData.append(URL.encode(i + ".type")).append("=").append(URL.encode(methodParams[i].getName())).append('&');
                postData.append(URL.encode(i + ".value")).append("=").append(URL.encode(methodParams[i].getValue().toString()));
            }
        }

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, finalUrl);
        if (DEBUG) {
            SC.logWarn("Sending POST request to " + finalUrl + "  for  " + url);
        }
        requestBuilder.setHeader("Accept", "application/json");
        requestBuilder.setHeader("Content-type", "application/x-www-form-urlencoded");
        requestBuilder.setCallback(this);
        requestBuilder.setRequestData(postData.toString());
        requestBuilder.setHeader("Content-Length", Integer.toString(postData.length()));
        try {
            StatusBar.setStatus("Sending request at " + url);
            requestBuilder.sendRequest(postData.toString(), this);
            StatusBar.setStatus("Request sent " + url);
        } catch (RequestException e) {
            StatusBar.setStatus("Status: Got request exception " + e.getMessage());
        }
    }
    
    public void sendSimplePostRequest(String value) {

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, finalUrl);
        if (DEBUG) {
            SC.logWarn("Sending POST request to " + finalUrl + "  for  " + url);
        }
        requestBuilder.setHeader("Accept", "application/json");
        requestBuilder.setHeader("Content-type", "text/plain");
        requestBuilder.setCallback(this);
        requestBuilder.setRequestData(value);
        requestBuilder.setHeader("Content-Length", Integer.toString(value.length()));
        try {
            StatusBar.setStatus("Sending request at " + url);
            requestBuilder.sendRequest(value, this);
            StatusBar.setStatus("Request sent " + url);
        } catch (RequestException e) {
            StatusBar.setStatus("Status: Got request exception " + e.getMessage());
        }
    }
    
    /**
     * Updates the status bar with any error messages resulting from a request. 
     */
    public void onError(Request request, Throwable exception) {
        StatusBar.setStatus("Status: Received error");
    }

    /**
     * Handles the response received for a request. The response contains a JSON encoded
     * string which is parsed in order to extract a JSON object. The status bar is updated
     * and the object extracted from the response is added to the data model. 
     */
    public void onResponseReceived(Request request, Response response) {


        StatusBar.setStatus("Received response");
        String t = response.getText();
        try {
            JSONValue beanValue = JSONParser.parse(t);
            StatusBar.setStatus("Received response - parsed");
            JSONObject bean = beanValue.isObject();
            StatusBar.setStatus("Status: Received response - got bean object");
            String returnPath = bean.get("path").isString().stringValue();

            if (DEBUG) {
                SC.logWarn("Received response for " + url + " from " + returnPath);
            }
            model.addResource(bean, returnPath);
            StatusBar.setStatus("Received response " + returnPath);
        } catch (Exception e) {
            StatusBar.setStatus("Got exception " + e.toString());
        }

    }

}
