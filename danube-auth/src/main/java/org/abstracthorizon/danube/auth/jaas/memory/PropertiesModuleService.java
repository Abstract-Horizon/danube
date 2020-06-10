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
package org.abstracthorizon.danube.auth.jaas.memory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is keystore login module service. This class sets up keystore login module
 *
 * @author Daniel Sendula
 */
public class PropertiesModuleService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /** Login context name */
    private String loginContext;

    /** Control flag defaulted to &quot;requrired&quot;*/
    private String controlFlag = "required";

    /** Configuration */
    private Configuration configuration;

    /** Options */
    private Map<String, Object> options = new HashMap<String, Object>();

    /** Properties URI */
    private String propertiesURI;
    
    /** Properties */
    private Properties properties = new Properties();
    
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Default constructor
     */
    public PropertiesModuleService() {
    }

    /**
     * Starts the service - adding keystore login module to system application configuration entry
     * @throws Exception
     */
    public void start() throws Exception {

        AppConfigurationEntry.LoginModuleControlFlag flag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;

        if (AppConfigurationEntry.LoginModuleControlFlag.REQUIRED.toString().indexOf(controlFlag) > 0 ) {
            flag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
        } else if( AppConfigurationEntry.LoginModuleControlFlag.REQUISITE.toString().indexOf(controlFlag) > 0 ) {
            flag = AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
        } else if( AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT.toString().indexOf(controlFlag) > 0 ) {
            flag = AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
        }else if( AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL.toString().indexOf(controlFlag) > 0 ) {
            flag = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
        }

        
        options.put("properties", properties);
        AppConfigurationEntry entry = new AppConfigurationEntry(PropertiesLoginModule.class.getName(), flag, options);

        AppConfigurationEntry[] list = new AppConfigurationEntry[1];
        list[0] = entry;
        Method method = configuration.getClass().getMethod("setAppConfigurationEntry", new Class[]{String.class, list.getClass()});
        Object[] args = {loginContext, list};
        method.invoke(configuration, args);
        logger.info("Set up login context '" + loginContext + "'");
    }


    public void stop() throws Exception {
        Method method = configuration.getClass().getMethod("removeAppConfigurationEntry", new Class[]{String.class});
        Object[] args = {loginContext};
        method.invoke(configuration, args);
        logger.info("Removed login context '" + loginContext + "'");
    }

    /**
     * Lists certificates (users) from the keystore
     * @return users as a string
     * @throws Exception
     */
    public String listUsersString() throws Exception {
        List<String> u = listUsers();
        String[] users = new String[u.size()];
        users = (String[])u.toArray(users);

        StringBuffer res = new StringBuffer();
        res.append("[");
        for (int i=0; i<users.length; i++) {
            if (i > 0) {
                res.append(", ");
            }
            res.append(users[i]);
        }
        res.append("]");
        return res.toString();
    }

    /**
     * Returns a list of all users in the keystore
     * @return list of all users in the keystore
     * @throws Exception
     */
    public List<String> listUsers() throws Exception {
        ArrayList<String> users = new ArrayList<String>();
        Enumeration<?> en = properties.propertyNames();
        while (en.hasMoreElements()) {
            users.add((String)en.nextElement());
        }
        return users;
    }

    /**
     * Changes the password of the user
     * @param user user
     * @param oldPassword old password
     * @param newPassword new password
     * @throws Exception
     */
    public void changePassword(String user, String oldPassword, String newPassword) throws Exception {
        newPassword = generateMD5Hash(newPassword);
        String line = properties.getProperty(user);
        if (line != null) {
            int i = line.indexOf(',');
            if (i < 0) {
                properties.setProperty(user, newPassword);
            } else {
                properties.setProperty(user, newPassword + line.substring(i));
            }
        }
        storeProperties();
    }

    /**
     * Adds new user to the store
     * @param user user
     * @param passwd password
     * @throws Exception
     */
    public void addUser(String user, String passwd) throws Exception {
        properties.setProperty(user, generateMD5Hash(passwd));
        storeProperties();
    }

    /**
     * Removes certificate  - user
     * @param user username
     * @throws Exception
     */
    public void removeUser(String user) throws Exception {
        properties.remove(user);
        storeProperties();
    }

    public Set<String> getUserRoles(String user) {
        String line = properties.getProperty(user);
        if (line == null) {
            return null;
        }
        Set<String> res = new HashSet<String>();
        String[] roles = line.split(",");
        for (int i = 1; i < roles.length; i++) {
            res.add(roles[i]);
        }
        return res;
    }
    
    public String[] getUserRolesAsArray(String user) {
        String line = properties.getProperty(user);
        if (line == null) {
            return null;
        }
        String[] roles = line.split(",");
        String[] res = new String[roles.length - 1];
        if (roles.length > 1) {
            System.arraycopy(roles, 1, res, 0, roles.length - 1);
        }
        return res;
    }
    
    public void loadProperties() throws CertificateException, IOException, URISyntaxException {
        if (propertiesURI != null) {
            logger.info("Loading properties from " + propertiesURI.toString() + " for login context '" + loginContext + "'");
            InputStream keystoreInputStream = new URI(propertiesURI).toURL().openStream();
            try {
                properties.load(keystoreInputStream);
            } finally {
                keystoreInputStream.close();
            }
        }
    }

    public void storeProperties() throws IOException, URISyntaxException {

        if ((properties != null) && (propertiesURI != null)) {
            
            logger.info("Storing properties to " + propertiesURI.toString() + " for login context '" + loginContext + "'");
            OutputStream keystoreOutputStream = null;
            URLConnection connection = new URI(propertiesURI).toURL().openConnection();
            connection.setDoOutput(true);
            keystoreOutputStream = connection.getOutputStream();
    
            try {
                properties.store(keystoreOutputStream, "For login context " + loginContext);
            } finally {
                keystoreOutputStream.close();
            }
        }
    }


    /**
     * Returns properties uri
     * @return properties uri
     */
    public String getPropertiesURI() {
        return propertiesURI;
    }

    /**
     * Sets properties URI
     * @param uri properties URI
     */
    public void setPropertesURI(String uri) {
        propertiesURI = uri;
    }

    /**
     * Sets login context name
     * @param loginContext login context name
     */
    public void setLoginContext(String loginContext) {
        this.loginContext = loginContext;
    }

    /**
     * Returns login context name
     * @return login context name
     */
    public String getLoginContext() {
        return loginContext;
    }

    /**
     * Sets control flag
     * @param controlFlag control flag
     */
    public void setControlFlag(String controlFlag) {
        this.controlFlag = controlFlag;
    }

    /**
     * Returns control flag
     * @return control flag
     */
    public String getControlFlag() {
        return controlFlag;
    }

    /**
     * Returns configuration
     * @return configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Sets configuration
     * @param configuration configuration
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Properties getProperties() {
        return properties;
    }
    
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public static String generateMD5Hash(String password) throws NoSuchAlgorithmException {
        return generateHash("MD5", password);
    }
    public static String generateHash(String algorithm, String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        StringBuilder res = new StringBuilder();
        res.append("{MD5}");
        byte[] bytes = messageDigest.digest(password.getBytes());
        String h = null;
        int i;
        for (byte b : bytes) {
            i = (int)b;
            if (i < 0) {
                i = - i + 127;
            }
            h = Integer.toString(i, 16);
            if (h.length() < 2) {
                res.append('0').append(h);
            } else {
                res.append(h);
            }
        }
        return res.toString();

    }
}
