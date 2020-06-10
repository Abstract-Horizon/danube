/*
 * Copyright (c) 2005-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.tapestry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.session.HTTPSessionManager;
import org.abstracthorizon.danube.http.session.SimpleSessionManager;
import org.abstracthorizon.danube.support.RuntimeIOException;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.ModuleDescriptorProvider;
import org.apache.hivemind.Registry;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.impl.StrictErrorHandler;
import org.apache.hivemind.impl.XmlModuleDescriptorProvider;
import org.apache.hivemind.parse.CreateInstanceDescriptor;
import org.apache.hivemind.parse.ImplementationDescriptor;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.util.ClasspathResource;
import org.apache.tapestry.parse.ISpecificationParser;
import org.apache.tapestry.services.ApplicationGlobals;
import org.apache.tapestry.services.Infrastructure;
import org.apache.tapestry.services.WebRequestServicer;
import org.apache.tapestry.spec.IApplicationSpecification;
import org.apache.tapestry.web.WebActivator;
import org.apache.tapestry.web.WebContext;
import org.apache.tapestry.web.WebContextResource;
import org.apache.tapestry.web.WebRequest;
import org.apache.tapestry.web.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tapestry connection handler.
 *
 * During initialisation it reads tapestry application configuration file
 * which defaults to {@link #TAPESTRY_DEFAULT_APPLICATION_NAME} (&quot;tapestry.application&quot;).
 *
 *
 * @author Daniel Sendula
 */
public class TapestryConnectionHandler implements ConnectionHandler {

    /** Suffix of tapestry configuration files */
    public static final String TAPESTRY_SUFFIX = ".application";

    /** Default tapestry application configuration file */
    public static final String TAPESTRY_DEFAULT_APPLICATION_NAME = "tapestry" + TAPESTRY_SUFFIX;

    /** Logger */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Tapestry's class resolver */
    protected ClassResolver classResolver;

    /** Tapestry's registry */
    protected Registry registry;

    /** Tapestry's web request servicer */
    protected WebRequestServicer webRequestServicer;

    /** Web activator implementation reference */
    protected WebActivator webActivator;

    /** Web context implementation reference */
    protected WebContext webContext;

    /** Danube's session manager */
    protected HTTPSessionManager sessionManager;

    /** Application specification resource name. Initial value is &quot;tapestry.application&quot; */
    protected String applicationSpecificationResourceName = TAPESTRY_DEFAULT_APPLICATION_NAME;

    /** Initial parameters */
    protected Map<String, String> initialParameters;

    /**
     * Handles connection by creating {@link DanubeRequest} and {@link DanubeResponse}
     * and invokes servicer's service method.
     * @param connection connection
     */
    public void handleConnection(Connection connection) {
        HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);

        try {
            registry.setupThread();

            WebRequest request = new DanubeRequest(httpConnection, getSessionManager());
            WebResponse response = new DanubeResponse(httpConnection);

            webRequestServicer.service(request, response);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            registry.cleanupThread();
        }
    }

    /**
     * Initialises this connection handler
     * @throws Exception
     */
    public void init() throws Exception {

        classResolver = getClassResolver();

        ErrorHandler errorHandler = constructErrorHandler();

        RegistryBuilder builder = new RegistryBuilder(errorHandler);

        builder.addModuleDescriptorProvider(new XmlModuleDescriptorProvider(classResolver));

        builder.addModuleDescriptorProvider(new ModuleDescriptorProvider() {
            ArrayList<ModuleDescriptor> list;

            public List<ModuleDescriptor> getModuleDescriptors(ErrorHandler errorHandler) {
                if (list == null) {
                    list = new ArrayList<ModuleDescriptor>();
                    ModuleDescriptor moduleDescriptor = new ModuleDescriptor(classResolver, errorHandler);
                    ImplementationDescriptor implementationDescriptor = new ImplementationDescriptor();
                    implementationDescriptor.setServiceId("tapestry.request.CookieSource");
                    CreateInstanceDescriptor instanceBuilder = new CreateInstanceDescriptor();
                    instanceBuilder.setInstanceClassName(DanubeCookieSource.class.getName());
                    implementationDescriptor.setInstanceBuilder(instanceBuilder);
                    moduleDescriptor.addImplementation(implementationDescriptor);
                    list.add(moduleDescriptor);
                }
                return list;
            }

        });

        try {
            // TODO WEB-INF?!
            // ClassResolver classResolver = getClassResolver();
            URL url = classResolver.getResource("WEB-INF/hivemodule.xml");
            InputStream is = url.openStream();
            is.read();
            is.close();

            Resource hivemoduleResource = new ClasspathResource(classResolver, "WEB-INF/hivemodule.xml");
            builder.addModuleDescriptorProvider(new XmlModuleDescriptorProvider(classResolver, hivemoduleResource));
        } catch (Exception ignore) {
        }

        registry = builder.constructRegistry(Locale.getDefault());

        Resource location = obtainApplicationResource();

        // URL url = location.getResourceURL();
        // System.out.println(new File(url.getFile()).exists());
        ISpecificationParser parser = (ISpecificationParser)registry.getService("tapestry.parse.SpecificationParser", ISpecificationParser.class);
        IApplicationSpecification specification = parser.parseApplicationSpecification(location);

        List<?> factoryServices = (List<?>)registry.getConfiguration("tapestry.services.FactoryServices");

        ApplicationGlobals globals = (ApplicationGlobals)registry.getService("tapestry.globals.ApplicationGlobals", ApplicationGlobals.class);
        globals.storeActivator(getWebActivator());
        globals.storeWebContext(getWebContext());
        globals.storeSpecification(specification);
        globals.storeFactoryServices(factoryServices);

        Infrastructure infrastructure = (Infrastructure)registry.getService("tapestry.Infrastructure", Infrastructure.class);
        infrastructure.initialize("danube");

        registry.cleanupThread();

        webRequestServicer = (WebRequestServicer) registry.getService("tapestry.request.WebRequestServicer", WebRequestServicer.class);
    }

    /**
     * Creates {@link WebContext}.
     * @return new {@link DanubeContext}
     */
    protected WebContext obtainWebContext() {
        return new DanubeContext(this);
    }

    /**
     * Constructs error handler. This method returns
     * {@link StrictErrorHandler}
     * @return error handler
     */
    protected ErrorHandler constructErrorHandler() {
        return new StrictErrorHandler();
    }

    /**
     * Obtains application resource.
     * This implementation returns &quot;tapestry.application&qout; file resource
     * @return application resource
     */
    protected Resource obtainApplicationResource() {
        return new WebContextResource(getWebContext(), getApplicationSpecificationResourceName());
    }

    /**
     * Returns session manager. If none is supplied then {@link #createSessionManager()} method is called.
     * @return the sessionManager
     */
    public HTTPSessionManager getSessionManager() {
        if (sessionManager == null) {
            sessionManager = createSessionManager();
        }
        return sessionManager;
    }

    /**
     * Sets session manager
     * @param sessionManager the sessionManager to set
     */
    public void setSessionManager(HTTPSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Creates session manager. This implementation returns
     * {@link SimpleSessionManager}
     * @return creates new session manager
     */
    protected HTTPSessionManager createSessionManager() {
        return new SimpleSessionManager();
    }

    /**
     * Returns class resolver. If none is supplied then {@link #createClassResolver()} method
     * is called
     * @return class resolver
     */
    public ClassResolver getClassResolver() {
        if (classResolver == null) {
            classResolver = createClassResolver();
        }
        return classResolver;
    }

    /**
     * Sets class resolver
     * @param classResolver class resolver
     */
    public void setClassResolver(ClassResolver classResolver) {
        this.classResolver = classResolver;
    }

    /**
     * Creates class resolver. This implementation returns
     * {@link DefaultClassResolver}.
     * @return new class resolver
     */
    protected ClassResolver createClassResolver() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return new DefaultClassResolver(classLoader);
    }

    /**
     * Returns initial parameters. If not already set then it creates new, empty
     * map.
     * @return initial parameters
     */
    public Map<String, String> getInitialParameters() {
        if (initialParameters == null) {
            initialParameters = new HashMap<String, String>();
        }
        return initialParameters;
    }

    /**
     * Sets initial parameters
     * @param parameters initial parameters
     */
    public void setInitialParameters(Map<String, String> parameters) {
        this.initialParameters = parameters;
    }

    /**
     * Returns name of application specification resource
     * @return name of application specification resource
     */
    public String getApplicationSpecificationResourceName() {
        if (applicationSpecificationResourceName == null) {
            applicationSpecificationResourceName = TAPESTRY_DEFAULT_APPLICATION_NAME;
        }
        return applicationSpecificationResourceName;
    }

    /**
     * Sets name of application specification resource
     * @param applicationSpecificationResourceName name of application specification resource
     */
    public void setApplicationSpecificationResourceName(String applicationSpecificationResourceName) {
        this.applicationSpecificationResourceName = applicationSpecificationResourceName;
    }

    /**
     * Returns web activator. If not already set then {@link DanubeActivator} will be
     * instantiated.
     * @return web activator
     */
    public WebActivator getWebActivator() {
        if (webActivator == null) {
            webActivator = new DanubeActivator(this);
        }
        return webActivator;
    }

    /**
     * Sets web activator.
     * @param webActivator web activator
     */
    public void setWebActivator(WebActivator webActivator) {
        this.webActivator = webActivator;
    }

    /**
     * Returns web context assigned to this handler. If not already set then {@link DanubeContext}
     * will be instantiated.
     * @return web context
     */
    public WebContext getWebContext() {
        if (webContext == null) {
            webContext = new DanubeContext(this);
        }
        return webContext;
    }

    /**
     * Sets web context.
     * @param webContext web context
     */
    public void setWebContext(WebContext webContext) {
        this.webContext = webContext;
    }

}
