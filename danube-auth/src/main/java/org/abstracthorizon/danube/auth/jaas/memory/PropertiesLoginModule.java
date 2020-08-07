/*
 * Copyright (c) 2006-2020 Creative Sphere Limited.
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
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.x500.X500PrivateCredential;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is keystore login module. This login module checks keystore's
 * certificates
 *
 * @author Daniel Sendula
 */
public class PropertiesLoginModule implements LoginModule {

    /** Logger */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /** Subject */
    protected Subject subject;

    /** Callback handler */
    protected CallbackHandler callbackHandler;

    protected Map<String, ?> sharedState;

    protected Map<String, ?> options;


    /** User's password */
    private char[] userPassword;

    /** Username */
    protected String username;


    /** Uninitialised state */
    protected static final int UNINITIALIZED = 0;

    /** Initialised state */
    protected static final int INITIALIZED = 1;

    /** User authenticated state */
    protected static final int AUTHENTICATED = 2;

    /** Logged in state */
    protected static final int LOGGED_IN = 3;

    /** Current state defaulted to uninitialised */
    protected int status = UNINITIALIZED;

    /** Principals */
    private Collection<Principal> principals;

    /** Public credentials */
    private java.security.cert.CertPath publicCredentials = null;

    /** Private credential */
    private X500PrivateCredential privateCredential;

    /** Properties */
    private Properties properties;

    /**
     * Default contructor
     */
    public PropertiesLoginModule() {
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    /**
     * Init method
     * @param subject subject
     * @param callbackHandler handler
     * @param sharedState shared state
     * @param options options
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;

        setProperties((Properties)options.get("properties"));

        status = INITIALIZED;
    }

    /**
     * Login method
     * @return <code>true</code> if successful
     * @throws LoginException
     */
    public boolean login() throws LoginException {
        if (status == LOGGED_IN) {
            return true;
        }
        if ((status == INITIALIZED) || (status == AUTHENTICATED)) {
            obtainAuthenticationDetails();
            obtainCertificates();
            status = AUTHENTICATED;
            return true;
        }

        throw new LoginException("The login module is not initialized");
    }

    /**
     * This method obtains username and password from the party that tries to log in
     * @throws LoginException
     */
    private void obtainAuthenticationDetails() throws LoginException {
        TextOutputCallback bannerCallback = new TextOutputCallback(TextOutputCallback.INFORMATION, "Please login to keystore");
        NameCallback aliasCallback = new NameCallback("Keystore alias: ");
        PasswordCallback privateKeyPasswordCallback = new PasswordCallback("Password: ", false);
        ConfirmationCallback confirmationCallback = new ConfirmationCallback(ConfirmationCallback.INFORMATION, ConfirmationCallback.OK_CANCEL_OPTION,
                ConfirmationCallback.OK);
        try {
            callbackHandler.handle(
                    new Callback[]{
                            bannerCallback,
                            aliasCallback,
                            privateKeyPasswordCallback,
                            confirmationCallback}
                );
        } catch (IOException e) {
            throw new LoginException("Exception while getting keystore alias and password: " + e);
        } catch (UnsupportedCallbackException e) {
            throw new LoginException("Error: " + e.getCallback().toString() + " is not available to retrieve authentication "
                    + " information from the user");
        }

        int confirmationResult = confirmationCallback.getSelectedIndex();
        if (confirmationResult == ConfirmationCallback.CANCEL) {
            throw new LoginException("Login cancelled");
        }

        username = aliasCallback.getName();

        char[] tmpPassword = privateKeyPasswordCallback.getPassword();
        userPassword = new char[tmpPassword.length];
        System.arraycopy(tmpPassword, 0, userPassword, 0, tmpPassword.length);
        for (int i = 0; i < tmpPassword.length; i++) {
            tmpPassword[0] = ' ';
        }
        tmpPassword = null;
        privateKeyPasswordCallback.clearPassword();
    }

    /**
     * This method obtains certificates
     * @throws LoginException
     */
    private void obtainCertificates() throws LoginException {


        String line = properties.getProperty(username);
        if (line == null) {
            throw new LoginException("Username doesn't exist");
        }
        String[] roles = line.trim().split(",");
        String pass = roles[0];

        boolean checked = false;
        if (pass.startsWith("{")) {
            int i = pass.indexOf('}');
            if (i > 0) {
                checked = true;
                String algorithm = pass.substring(1, i);
                //String value = pass.substring(i + 1);
                try {
                    String digest = PropertiesModuleService.generateHash(algorithm, new String(userPassword));

                    if (!pass.equals(new String(digest))) {
                        throw new LoginException("Password is wrong");
                    }

                } catch (NoSuchAlgorithmException e) {
                    throw new LoginException(e.getMessage());
                }
            }
        }
        if (!checked) {
            if (!pass.equals(new String(userPassword))) {
                throw new LoginException("Password is wrong");
            }
        }


        principals = new HashSet<Principal>();
        for (int i = 1; i < roles.length; i++) {
            principals.add(new PropertiesPrincipal(roles[i]));
        }
    }

    /**
     * Performs commit
     * @return <code>true</code> if successful
     * @throws LoginException
     */
    public boolean commit() throws LoginException {
        if (status == LOGGED_IN) {
            return true;
        }
        if (status == AUTHENTICATED) {
            if (subject.isReadOnly()) {
                logoutImpl();
                throw new LoginException("Subject is set readonly");
            } else {
                subject.getPrincipals().addAll(principals);
                subject.getPublicCredentials().add(publicCredentials);
                subject.getPrivateCredentials().add(privateCredential);
                status = LOGGED_IN;
                return true;
            }
        }
        if (status == INITIALIZED) {
            logoutImpl();
            throw new LoginException("Authentication failed");
        }

        throw new LoginException("The login module is not initialized");
    }

    /**
     * Aborts login
     * @return <code>true</code> if successful
     */
    public boolean abort() throws LoginException {
        if ((status == AUTHENTICATED) || (status == LOGGED_IN)) {
            logoutImpl();
            return true;
        }

        return false;
    }

    /**
     * Logs out
     * @return <code>true</code> if successful
     */
    public boolean logout() throws LoginException {
        if (status == LOGGED_IN) {
            logoutImpl();
            return true;
        }

        return false;
    }

    /**
     * Internal log out method
     * @throws LoginException
     */
    private void logoutImpl() throws LoginException {
        for (int i = 0; i < userPassword.length; i++) {
            userPassword[i] = '\0';
        }
        userPassword = null;

        if (subject.isReadOnly()) {
            principals = null;
            publicCredentials = null;
            status = INITIALIZED;

            Iterator<Object> it = subject.getPrivateCredentials().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (privateCredential.equals(obj)) {
                    privateCredential = null;
                    try {
                        ((Destroyable) obj).destroy();
                        break;
                    } catch (DestroyFailedException dfe) {
                        throw new LoginException("Unable to destroy private credential, " + obj.getClass().getName() + ": " + dfe.getMessage());
                    }
                }
            }

            throw new LoginException("Unable to remove Principal (X500Principal) and public credential from read-only Subject");
        }


        if (principals != null) {
            for (Principal p : principals) {
                subject.getPrincipals().remove(p);
            }
            principals = null;
        }
        if (publicCredentials != null) {
            subject.getPublicCredentials().remove(publicCredentials);
            publicCredentials = null;
        }
        if (privateCredential != null) {
            subject.getPrivateCredentials().remove(privateCredential);
            privateCredential = null;
        }
        status = INITIALIZED;
    }

    private static class PropertiesPrincipal implements Principal {

        private String name;

        public PropertiesPrincipal(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
