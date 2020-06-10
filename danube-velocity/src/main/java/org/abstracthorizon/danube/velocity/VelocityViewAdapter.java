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
package org.abstracthorizon.danube.velocity;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionException;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.mvc.ModelAndView;
import org.abstracthorizon.danube.mvc.View;
import org.abstracthorizon.danube.support.InitializationException;
import org.abstracthorizon.danube.support.URLUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.servlet.VelocityServlet;
import org.apache.velocity.util.SimplePool;

/**
 * <p>
 *   This is implementation of {@link org.abstracthorizon.danube.mvc.View}
 * that uses Velocity template engine. Model map is used as map of parameters for
 * Velocity.
 * </p>
 * <p>
 *   This implementation is based on {@link VelocityServlet}
 * </p>
 *
 * @author Daniel Sendula
 */
public class VelocityViewAdapter implements View {

    /** The HTTP content type context key. */
    public static final String CONTENT_TYPE = "default.contentType";

    /** The default content type for the response */
    public static final String DEFAULT_CONTENT_TYPE = "text/html";

    /** Default encoding for the output stream */
    public static final String DEFAULT_OUTPUT_ENCODING = "ISO-8859-1";

    /** Cache of writers */
    private static SimplePool writerPool = new SimplePool(40);

    /** Path where templates are stored */
    protected URL templatesURL;

    /** Path where templates are stored */
    protected File templatesPath;

    /** Template suffix */
    protected String suffix;

    /**
     * The default content type.  When necessary, includes the
     * character set to use when encoding textual output.
     */
    protected String contentType;

    /**
     * Constructor.
     * @throws Exception
     */
    public VelocityViewAdapter() throws Exception {
    }

    /**
     * This metod initialises Velocity engine.
     * @throws Exception
     */
    public void init() throws InitializationException {
        contentType = RuntimeSingleton.getString(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        String encoding = RuntimeSingleton.getString(RuntimeConstants.OUTPUT_ENCODING, DEFAULT_OUTPUT_ENCODING);
        // For non Latin-1 encodings, ensure that the charset is
        // included in the Content-Type header.
        if (!DEFAULT_OUTPUT_ENCODING.equalsIgnoreCase(encoding)) {
            int index = contentType.lastIndexOf("charset");
            if (index < 0) {
                // the charset specifier is not yet present in header.
                // append character encoding to default content-type
                contentType += "; charset=" + encoding;
            } else {
                // The user may have configuration issues.
                Velocity.warn("VelocityViewServlet: Charset was already " + "specified in the Content-Type property.  "
                        + "Output encoding property will be ignored.");
            }
        }
        Velocity.info("VelocityViewServlet: Default content-type is: " + contentType);

        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        Velocity.setProperty("file.resource.loader.class", AbsoluteFileResourceLoader.class.getName());
        //Velocity.setProperty("file.resource.loader.path", "/");
        Velocity.setProperty("file.resource.loader.path", "");

        try {
            Velocity.init();
        } catch (Exception e) {
            throw new InitializationException(e);
        }
    }

    /**
     * This method renders output
     * @param connection http connection
     * @param modelAndView view name to be used as template's name and model map to be used as attributes
     * throws ConnectionException
     */
    public void render(Connection connection, ModelAndView modelAndView) throws ConnectionException {
        try {
            Context context = createContext(modelAndView.getModel());

            Template template = findTemplate(context, modelAndView.getView());

            if (template == null) {
                Velocity.warn("VelocityViewServlet: couldn't find template to match request.");
                return;
            }
            // merge the template and context
            HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);
            mergeTemplate(template, context, httpConnection);
        } catch (IOException ioException) {
            throw new ConnectionException(ioException);
        } catch (Exception exception) {
            throw new ConnectionException(exception);
        }
    }

    /**
     * This method finds template based on view name and &quot;.vm&quot; file extension.
     * Template is located in provided {@link #templatesLocation}
     * @param ctx velocity context
     * @param templateFile
     * @return template
     * @throws Exception
     */
    protected Template findTemplate(Context ctx, String view) throws Exception {
        if (templatesPath != null) {
            File file = new File(templatesPath, view + getSuffix());
            return RuntimeSingleton.getTemplate(file.getAbsolutePath());
        }

        if (templatesURL != null) {
            URL newURL = URLUtils.addPath(templatesURL, view + getSuffix());
            return RuntimeSingleton.getTemplate(newURL.toString());
        }

        throw new FileNotFoundException("One of templatesPath or templatesURL must be set.");
    }


    /**
     * Creates context based on provided model's map
     * @param attributes model's map
     * @return velocity context
     */
    protected Context createContext(Map<?, ?> attributes) {
        VelocityContext ctx = new VelocityContext(attributes);
        return ctx;
    }

    /**
     * Generates output from given template
     * @param template template
     * @param context velocity context
     * @param connection http connection result to be rendered to
     * @throws Exception
     */
    protected void mergeTemplate(Template template, Context context, HTTPConnection connection) throws Exception {
        connection.getResponseHeaders().putOnly("Content-Type", contentType);

        VelocityWriter vw = null;
        Writer writer = (Writer)connection.adapt(Writer.class);
        try {
            vw = (VelocityWriter) writerPool.get();
            if (vw == null) {
                vw = new VelocityWriter(writer, 4 * 1024, true);
            } else {
                vw.recycle(writer);
            }
            template.merge(context, vw);
        } finally {
            if (vw != null) {
                try {
                    // flush and put back into the pool
                    // don't close to allow us to play
                    // nicely with others.
                    vw.flush();
                    /* This hack sets the VelocityWriter's internal ref to the
                     * PrintWriter to null to keep memory free while
                     * the writer is pooled. See bug report #18951 */
                    vw.recycle(null);
                    writerPool.put(vw);
                } catch (Exception e) {
                    Velocity.debug("VelocityViewServlet: " + "Trouble releasing VelocityWriter: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Returns path where templates are stored
     * @return Returns the templatePath.
     */
    public File getTemplatesPath() {
        return templatesPath;
    }

    /**
     * Sets path where templates are stored
     * @param templatesPath path where templates are stored.
     */
    public void setTemplatesPath(File templatesPath) {
        this.templatesPath = templatesPath;
    }

    /**
     * Returns URL where templates are stored
     * @return returns the templates URL
     */
    public URL getTemplatesURL() {
        return templatesURL;
    }

    /**
     * Sets URL where templates are stored
     * @param templatesURL URL where templates are stored.
     */
    public void setTemplatesURL(URL templatesURL) {
        this.templatesURL = templatesURL;
        if (templatesURL.getProtocol().equals("file")) {
            templatesPath = new File(templatesURL.getFile());
        }
    }

    /**
     * Returns default content type
     * @return default content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets default content type
     * @param contentType default content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Returns template suffix. If not set it is then initialised to &quot;.vm&quot;
     * @return template suffix
     */
    public String getSuffix() {
        if (suffix == null) {
            suffix = ".vm";
        }
        return suffix;
    }

    /**
     * Sets suffix of templates
     * @param suffix suffix of templates
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
