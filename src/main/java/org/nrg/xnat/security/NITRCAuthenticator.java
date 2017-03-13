// Copyright 2010 Washington University School of Medicine All Rights Reserved
package org.nrg.xnat.security;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import org.nrg.xdat.security.Authenticator.Credentials;
import org.nrg.xdat.security.Authenticator;
import org.nrg.xft.security.UserI;
import org.nrg.xft.XFT;
import org.nrg.xdat.security.helpers.Users;

public class NITRCAuthenticator extends java.net.Authenticator {

    protected static String MASTER_PASSWORD;
    protected static String BASE_NITRIC_AUTH_URL;
    protected static String LOGIN_FAIL_MESSAGE;

    public NITRCAuthenticator() {
        try {
            System.out.println("NITRC: new NITRCAuthenticator instance");

		/* get the master password */
            File AUTH_PROPS = new File(XFT.GetConfDir(), "authentication.properties");
            if (AUTH_PROPS.exists()) {
                InputStream inputs = new FileInputStream(AUTH_PROPS);
                Properties properties = new Properties();
                properties.load(inputs);

                if (properties.containsKey("MASTER_PASSWORD"))
                    MASTER_PASSWORD = properties.getProperty("MASTER_PASSWORD");
                else
                    MASTER_PASSWORD = null;

                if (properties.containsKey("LOGIN_FAIL_MESSAGE"))
                    LOGIN_FAIL_MESSAGE = properties.getProperty("LOGIN_FAIL_MESSAGE");
                else
                    LOGIN_FAIL_MESSAGE = "Invalid username or password.";

                if (properties.containsKey("BASE_NITRIC_AUTH_URL"))
                    BASE_NITRIC_AUTH_URL = properties.getProperty("BASE_NITRIC_AUTH_URL");
                else
                    System.out.println("ERROR: No NITRC authentication URL supplied in authentication.properties");
            }

        } catch (Exception e) {
            System.out.println("Problem during init" + e.getMessage());
        }
    }


    public UserI authenticate(Credentials cred)
            throws Exception {

        System.out.println("ATC: NITRCAuthenticator authenticate called.");

        System.out.println("in 1 arg authenticat.  user is " + cred.getUsername() + " and the password is XXXXX " + cred.getPassword());
        if (authenticateWithNITRC(cred.getUsername(), cred.getPassword()) || (MASTER_PASSWORD != null && cred.getPassword().equals(MASTER_PASSWORD))) {
            System.out.println("authed successfully");
            try {
                UserI xdu = Users.getUser(cred.getUsername());
                System.out.println("Got a user for " + cred.getUsername());
                System.out.println("it is " + xdu.getID());
                return xdu;
            } catch (Exception e) {
                System.out.println("exception " + e.getMessage());
            }
        }
        System.out.println("auth failed");
        throw new Exception(LOGIN_FAIL_MESSAGE);
    }

    public boolean authenticate(UserI u, Credentials cred)
            throws  Exception {

        System.out.println("ATC: NITRCAuthenticator authenticate called.");
        log("ATC: in 2 arg authenticat.  user is " + cred.getUsername());
        if (authenticateWithNITRC(cred.getUsername(), cred.getPassword()) || (MASTER_PASSWORD != null && cred.getPassword().equals(MASTER_PASSWORD))) {
            if (cred.getPassword().equals(MASTER_PASSWORD))
                System.out.println("master local password used.");
            System.out.println("User authenticated.");
            return true;
        } else {
            return false;
        }
    }

    protected boolean authenticateWithNITRC(String user, String password) {

        //TODO: ensure the user and password don't have any dangerous formating
        log("Entered authenticateWithNITRC (NITRCAuthenticator)");
        String url = BASE_NITRIC_AUTH_URL + "&" + "user=" + user + "&pwd=" + password;
        try {
            String result = null;
            URL fURL = new URL(url);
            log ("using URL " + url);
            URLConnection connection = null;
            connection = fURL.openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter(END_OF_INPUT);
            result = scanner.next();

            if (result.equals("1")) {
                log("Successful login");
                return true;
            }
            log("result was " + result);
        } catch (IOException ex) {
            log("Cannot open connection to " + url);
        } catch (Exception e) {
            log("Exception authenticating users " + e.getMessage());
        }
        log("Login failed");
        return false;
    }

    protected void log(String s) {
        System.out.println(s);
    }

    // PRIVATE //

    private static final String END_OF_INPUT = "\\Z";
    private static final String NEWLINE = System.getProperty("line.separator");




}
