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
package org.abstracthorizon.danube.freemarker;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionException;
import org.abstracthorizon.danube.mvc.ModelAndView;
import org.abstracthorizon.danube.mvc.View;
import org.abstracthorizon.danube.support.RuntimeIOException;
import org.abstracthorizon.danube.support.URLUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * <p>This is implementation of {@link org.abstracthorizon.danube.mvc.View}
 * that uses Velocity template engine. Model map is used as map of parameters for
 * Velocity.
 * </p>
 *
 * @author Daniel Sendula
 */
public class FreeMarkerViewAdapter implements View {

    /** Logger */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /** Default pages suffix is &quot;.page&quot; */
    public static final String DEFAULT_SUFFIX = ".page";

    /** Path where templates are stored */
    protected File templatesPath;

    /** URL where templates are stored */
    protected URL templatesURL;

    /** Suffix to be used for templates */
    protected String suffix = DEFAULT_SUFFIX;

    /** Configuration */
    protected Configuration configuration;

    /**
     * Constructor.
     */
    public FreeMarkerViewAdapter() {
    }

    /**
     * This metod initialises Velocity engine.
     * @throws RuntimeIOException
     */
    public void init() throws RuntimeIOException {

        configuration = new Configuration();
        configuration.setTemplateLoader(createTemplateLoader());
        DefaultObjectWrapper defaultObjectWrapper = new DefaultObjectWrapper();
        configuration.setObjectWrapper(defaultObjectWrapper);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
    }

    protected TemplateLoader createTemplateLoader() throws RuntimeIOException {
        return new LocalTemplateLoader();
    }

    /**
     * This method renders output
     * @param connection http connection
     * @param modelAndView view name to be used as template's name and model map to be used as attributes
     * @throws ConnectionException
     */
    public void render(Connection connection, ModelAndView modelAndView) throws ConnectionException {

        try {
            Template template = configuration.getTemplate(makeTemplateName(modelAndView.getView()));
            Writer writer = (Writer)connection.adapt(Writer.class);

            template.process(modelAndView.getModel(), writer);
        } catch (TemplateException templateException) {
            throw new ConnectionException(templateException);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }

        //out.flush();
    }

    /**
     * Returns path where templates are stored
     * @return Returns the templatePath.
     */
    public File getTemplatesPath() {
        return templatesPath;
    }

    /**
     * Sets path where templates are stored. Note: only a file or URL need to be set, not both.
     * @param templatesPath path where templates are stored.
     */
    public void setTemplatesPath(File templatesPath) {
        this.templatesPath = templatesPath;
        if (configuration != null) {
            configuration.setTemplateLoader(createTemplateLoader());
        }
    }

    /**
     * Returns URL where templaes are to be read from
     * @return URL where templaes are to be read from
     */
    public URL getTemplatesURL() {
        return templatesURL;
    }

    /**
     * Sets URL templates are to be read from. Note: only a file or URL need to be set, not both.
     * @param templatesURL URL templates are to be read from
     */
    public void setTemplatesURL(URL templatesURL) {
        this.templatesURL = templatesURL;
        if (templatesURL.getProtocol().equals("file")) {
            templatesPath = new File(templatesURL.getFile());
        }
    }

    /**
     * Creates name of the template
     * @param view name of the view
     * @return returns template's name
     */
    protected String makeTemplateName(String view) {
        if (suffix == null) {
            return view;
        } else {
            return view + suffix;
        }
    }

    /**
     * Returns suffix of the templates
     * @return suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Sets suffix of the templates
     * @param suffix suffix
     */
    public void setSuffix(String suffix) {
        if (suffix.startsWith(".")) {
            this.suffix = suffix;
        } else {
            this.suffix = "." + suffix;
        }
    }

    /**
     * Local template loader that uses templates path or templates URL (which ever is supplied).
     * Templates path has advantage against URL.
     */
    public class LocalTemplateLoader implements TemplateLoader {

        /** Constructor */
        public LocalTemplateLoader() {
        }

        /**
         * This method does nothing
         * @param tmp template resource
         * @throws IOException
         */
        public void closeTemplateSource(Object tmp) throws IOException {
        }

        /**
         * This method returns a file or an URL if file is not available and URL's input stream
         * can be accessed (it tries to read ONE byte of it).
         * @param path path to resource
         * @return file, URL or <code>null</code>
         * @throws IOException
         */
        public Object findTemplateSource(String path) throws IOException {
            if (templatesPath != null) {
                File file = new File(templatesPath, path);
                if (file.exists()) {
                    return file;
                }
            }
            if (templatesURL != null) {
                URL newURL = URLUtils.addPath(templatesURL, path);
                try {
                    InputStream peek = newURL.openStream();
                    peek.read();
                    peek.close();
                    return newURL;
                } catch (IOException ignore) {
                }
            }

            return null;
        }

        /**
         * This method returns file's last modified attribute if file is
         * supplied. If not it tries to invoke last modified on an URL.
         * If that fails too then it returns -1
         * @param tmp resource which has to be a file or URL.
         * @return file's or url's last modified or -1
         */
        public long getLastModified(Object tmp) {
            if (tmp instanceof File) {
                return ((File)tmp).lastModified();
            } else if (tmp instanceof URL) {
                try {
                    URLConnection connection = ((URL)tmp).openConnection();
                    return connection.getLastModified();
                } catch (IOException ignore) {
                }
            }

            return -1;
        }

        /**
         * Returns an reader from file or URL or <code>null</code> if that fails
         * @param tmp file or URL
         * @param encoding encoding
         * @return file reader or input stream reader of URL's input stream or <code>null</code>
         */
        public Reader getReader(Object tmp, String encoding) throws IOException {
            if (tmp instanceof File) {
                File file = (File)tmp;
                return new FileReader(file);
            } else if (tmp instanceof URL) {
                URL url = (URL)tmp;
                return new InputStreamReader(url.openStream());
            } else {
                return null;
            }
        }

    }
}
