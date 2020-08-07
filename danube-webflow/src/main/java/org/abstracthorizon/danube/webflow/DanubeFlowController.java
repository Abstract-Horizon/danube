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
package org.abstracthorizon.danube.webflow;

import java.util.HashMap;
import java.util.Map;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionException;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.session.HTTPSessionManager;
import org.abstracthorizon.danube.http.session.SimpleSessionManager;
import org.abstracthorizon.danube.mvc.Controller;
import org.abstracthorizon.danube.mvc.ModelAndView;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.ResponseInstruction;
import org.springframework.webflow.executor.mvc.FlowController;
import org.springframework.webflow.executor.support.FlowExecutorArgumentExtractor;
import org.springframework.webflow.executor.support.FlowRequestHandler;

/**
 * <p>
 *   Flow execution manager for Danube MVC. This is a glue between Spring Webflow and Danube.
 * </p>
 * <p>
 *   Note: Code is based on the code from {@link FlowController} by Erwin Vervaet and Keith Donald
 * </p>
 *
 * @author Daniel Sendula
 */
public class DanubeFlowController implements Controller {

    /** Flow executor */
    private FlowExecutor flowExecutor;

    /** Argument extractor */
    private FlowExecutorArgumentExtractor argumentExtractor = null; // TODO !!! new FlowExecutorArgumentExtractor();

    /** Session manager */
    private HTTPSessionManager sessionManager;

    /** Context attributes */
    private Map<String, Object> attributes;

    /**
     * Constructor.
     */
    public DanubeFlowController() {
    }

    /**
     * Sets the flow locator. It automatically creates and sets {@link FlowExecutionImpl}.

     * @param flowLocator flow locator
     */
//  TODO !!!
//    public void setFlowLocator(FlowLocator flowLocator) {
//        this.flowExecutor = new FlowExecutorImpl(flowLocator);
//    }

    /**
     * Returns flow executor.
     *
     * @return flow executor
     */
    public FlowExecutor getFlowExecutor() {
        return flowExecutor;
    }

    /**
     * Sets flow executor.
     *
     * @param flowExecutor flow executor
     */
    public void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor;
    }

    /**
     * Returns argument extractor used by this controller.
     * @return argument extractor
     */
    public FlowExecutorArgumentExtractor getArgumentExtractor() {
        if (argumentExtractor == null) {
//          TODO !!! argumentExtractor = new FlowExecutorArgumentExtractor();
        }
        return argumentExtractor;
    }

    /**
     * Sets argument extractor to be used.
     *
     * @param parameterExtractor argument extractor
     */
    public void setArgumentExtractor(FlowExecutorArgumentExtractor parameterExtractor) {
        this.argumentExtractor = parameterExtractor;
    }

    /**
     * Sets default flow id if none is specified with the parameters.
     *
     * @param defaultFlowId default flow id
     */
    public void setDefaultFlowId(String defaultFlowId) {
        // TODO !!! this.argumentExtractor.setDefaultFlowId(defaultFlowId);
    }

    /**
     * Handles request
     * @param connection connection
     * @return model and view combination
     * @throws ConnectionException connection exception
     */
    public ModelAndView handleRequest(Connection connection) throws ConnectionException {
        try {
            HTTPConnection httpConnection = (HTTPConnection) connection;

            DanubeExternalContext context = new DanubeExternalContext(this, httpConnection);

            FlowRequestHandler flowRequestHandler = new FlowRequestHandler(getFlowExecutor(), getArgumentExtractor());
            ResponseInstruction responseInstruction = flowRequestHandler.handleFlowRequest(context);

            return toModelAndView(responseInstruction, context);
        } catch (ConnectionException e) {
            throw e;
        } catch (Exception e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * Returns model and a view combination
     * @param response response instruction
     * @param context external context
     * @return model and view
     */
    @SuppressWarnings("unchecked")
    protected ModelAndView toModelAndView(ResponseInstruction response, ExternalContext context) {
        if (response.isApplicationView()) {
            ApplicationView view = (ApplicationView)response.getViewSelection();
            Map<String, Object> model = new HashMap<String, Object>(view.getModel());
//          TODO
//            argumentExtractor.put(response.getFlowExecutionKey(), model);
//            argumentExtractor.put(response.getFlowExecutionContext(), model);
            return new ModelAndView(view.getViewName(), model);
// TODO
//        } else if (response.isConversationRedirect()) {
//            // redirect to active conversation URL
//            Serializable conversationId = response.getFlowExecutionKey().getConversationId();
//            String conversationUrl = argumentExtractor.createConversationUrl(conversationId, context);
//            return new org.springframework.web.servlet.ModelAndView(new RedirectView(conversationUrl, true));
//        } else if (response.isExternalRedirect()) {
//            // redirect to external URL
//            ExternalRedirect redirect = (ExternalRedirect)response.getViewSelection();
//            String externalUrl = argumentExtractor.createExternalUrl(redirect, response.getFlowExecutionKey(), context);
//            return new org.springframework.web.servlet.ModelAndView(new RedirectView(externalUrl, redirect.isContextRelative()));
//        } else if (response.isFlowRedirect()) {
//            // restart the flow by redirecting to flow launch URL
//            String flowUrl = argumentExtractor.createFlowUrl((FlowRedirect)response.getViewSelection(), context);
//            return new org.springframework.web.servlet.ModelAndView(new RedirectView(flowUrl, true));
        } else if (response.isNull()) {
            return null;
        } else {
            throw new IllegalArgumentException("Don't know how to handle response instruction " + response);
        }
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

    /**
     * Sets session manager
     * @param sessionManager http session manager
     */
    public void setSessionManager(HTTPSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Returns attributes as a map
     * @return attributes
     */
    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }
        return attributes;
    }

    /**
     * Sets attributes map
     * @param attributes attributes
     */
    public void setAttributes(Map<String, Object> attributes) {
        if (attributes == null) {
            getAttributes();
        }
        this.attributes = attributes;
    }
}
