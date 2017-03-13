/*
 * web: org.nrg.xnat.security.provider.XnatDatabaseAuthenticationProvider
 * XNAT http://www.xnat.org
 * Copyright (c) 2016, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.security.provider;

import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLClassLoader;
import javax.xml.bind.DatatypeConverter;

import org.nrg.xdat.XDAT;
import org.nrg.xdat.services.XdatUserAuthService;
import org.nrg.xft.db.PoolDBUtils;
import org.nrg.xft.security.UserI;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/*
The configuration must be made in the $XNAT_HOME/config/xnat-conf.properties file.  Here is a sample:

nitrc.url=http://test.nitrc.org/xnat/validate_user.php?key=xnat
nitrc.basicauth.user=nitrc
nitrc.basicauth.pass=devwiki
nitrc.loginfailmessage=Incorrect username or password


 */
public abstract class NITRCDatabaseAuthenticationProvider extends DaoAuthenticationProvider implements XnatAuthenticationProvider {

    protected Properties properties;


    @Override
    protected void additionalAuthenticationChecks(final UserDetails userDetails, final UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        System.out.println("in additionaAuthenticationChecks");
        properties = getProps(); // reload the properties file
        String LOGiN_FAIL_MESSAGE = properties.getProperty("nitrc.loginfailmessage");
        if (!UserI.class.isAssignableFrom(userDetails.getClass())) {
            throw new AuthenticationServiceException("User details class is not of a type I know how to handle: " + userDetails.getClass());
        }
        final UserI xdatUserDetails = (UserI) userDetails;
        if ((XDAT.getSiteConfigPreferences().getEmailVerification() && !xdatUserDetails.isVerified() && xdatUserDetails.isEnabled()) || !xdatUserDetails.isAccountNonLocked()) {
            throw new CredentialsExpiredException("Attempted login to unverified or locked account: " + xdatUserDetails.getUsername());
        }

        System.out.println ("Skipping the Spring DAO check.");
        // super.additionalAuthenticationChecks(userDetails, authentication);

        Properties props = getProps();

        if ( ! authenticateWithNITRC(authentication.getPrincipal().toString(), authentication.getCredentials().toString()) ) {
            throw new AuthenticationServiceException(LOGiN_FAIL_MESSAGE);
        }
    }

    protected Properties getProps() {
        Properties prop = new Properties();

        String homeDir = System.getProperty("xnat.home");

        InputStreamReader in = null;
        String fname = homeDir + "/config/" + "xnat-conf.properties";
        try {
            System.out.println("File: " + fname);
            in = new InputStreamReader(new FileInputStream(fname), "UTF-8");
            prop.load(in);
        } catch (IOException ex) {
            System.out.println ("couldn't find xnat-conf.properties in " + fname);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException ex) {}
            }
        }

        return prop;

    }


    protected boolean authenticateWithNITRC(String user, String password) {

        System.out.println("ATC: NITRCDatabaseAuthenticationProvider authenticate called.");

        String BASE_NITRIC_AUTH_URL = "";
        String NITRC_BASIC_AUTH_USER = "";
        String NITRC_BASIC_AUTH_PASS = "";

        BASE_NITRIC_AUTH_URL = properties.getProperty("nitrc.url");
        NITRC_BASIC_AUTH_USER = properties.getProperty("nitrc.basicauth.user");
        NITRC_BASIC_AUTH_PASS = properties.getProperty("nitrc.basicauth.pass");

        // Guest will accept any password
        if (user.equals("guest")) {
            return true;
        }

        if (user.equals("boss") && password.equals(properties.getProperty("nitrc.master.password"))) {
            log ("boss user login successful.");
            return true;
        }

        //TODO: ensure the user and password don't have any dangerous formating
        log("Entered authenticateWithNITRC (NITRCDatabaseAuthenticaitonProvider)");
        String url = BASE_NITRIC_AUTH_URL + "&" + "user=" + user + "&pwd=" + password;
        log(url);
        try {
            String result = null;
            URL fURL = new URL(url);
            URLConnection connection = null;
            connection =  fURL.openConnection();

            // If we need Basic Auth, set it up here
            if (NITRC_BASIC_AUTH_USER != null && NITRC_BASIC_AUTH_PASS != null) {
                log("setting basic auth as " + NITRC_BASIC_AUTH_USER + " : " + NITRC_BASIC_AUTH_PASS);
                String credentials = NITRC_BASIC_AUTH_USER + ":" + NITRC_BASIC_AUTH_PASS;
                String encoding = DatatypeConverter.printBase64Binary(credentials.getBytes("UTF-8"));
                connection.setRequestProperty("Authorization", String.format("Basic %s", encoding));
            }

            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter(END_OF_INPUT);
            result = scanner.next();

            if (result.equals("1"))   {
                log("Successful login");
                return true;
            }
            log("result was " + result);
        }
        catch ( IOException ex ) {
            log("Cannot open connection to " + url);
        }
        catch ( Exception e ) {
            log ("Exception authenticating users " + e.getMessage());
        }
        log("Login failed");
        return false;
    }

    protected void log (String s) {
        System.out.println(s);
    }

    private static final String END_OF_INPUT = "\\Z";
    private static final String NEWLINE = System.getProperty("line.separator");

}
