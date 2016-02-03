/*
 * org.nrg.xnat.security.XnatProviderManager
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 12/13/13 2:19 PM
 */
package org.nrg.xnat.security;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.hibernate.exception.DataException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.entities.AliasToken;
import org.nrg.xdat.entities.UserAuthI;
import org.nrg.xdat.entities.XdatUserAuth;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.services.XdatUserAuthService;
import org.nrg.xdat.turbine.utils.AdminUtils;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.AuthUtils;
import org.nrg.xnat.security.alias.AliasTokenAuthenticationProvider;
import org.nrg.xnat.security.alias.AliasTokenAuthenticationToken;
import org.nrg.xnat.security.provider.XnatAuthenticationProvider;
import org.nrg.xnat.security.provider.XnatDatabaseAuthenticationProvider;
import org.nrg.xnat.security.provider.XnatLdapAuthenticationProvider;
import org.nrg.xnat.security.tokens.XnatDatabaseUsernamePasswordAuthenticationToken;
import org.nrg.xnat.security.tokens.XnatLdapUsernamePasswordAuthenticationToken;
import org.nrg.xnat.security.userdetailsservices.XnatDatabaseUserDetailsService;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class XnatProviderManager extends ProviderManager {

    public XnatProviderManager(final List<AuthenticationProvider> providers, final Properties properties) {

        super(providers);

        if (properties == null) {
            throw new IllegalArgumentException("The list of authentication providers cannot be set to null.");
        }

        _properties = properties;

        if(!StringUtils.isBlank(properties.getProperty(SECURITY_MAX_FAILED_LOGINS_PROPERTY))) {
            AuthUtils.MAX_FAILED_LOGIN_ATTEMPTS=Integer.valueOf(properties.getProperty(SECURITY_MAX_FAILED_LOGINS_PROPERTY));
        }

        if(!StringUtils.isBlank(properties.getProperty(SECURITY_MAX_FAILED_LOGINS_LOCKOUT_DURATION_PROPERTY))) {
            AuthUtils.LOCKOUT_DURATION=Integer.valueOf(properties.getProperty(SECURITY_MAX_FAILED_LOGINS_LOCKOUT_DURATION_PROPERTY));
            if(AuthUtils.LOCKOUT_DURATION>0)AuthUtils.LOCKOUT_DURATION=-(AuthUtils.LOCKOUT_DURATION); //LOCKOUT must be negative for date comparison to work
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Class<? extends Authentication> toTest = authentication.getClass();
        AuthenticationException lastException = null;
        Authentication result = null;
        List<AuthenticationProvider> providers = new ArrayList<>();

        // HACK: This is a hack to work around open XNAT auth issue. If this is a bare un/pw auth token, use anon auth.
        if (authentication.getClass() == UsernamePasswordAuthenticationToken.class && authentication.getName().equalsIgnoreCase("guest")) {

            providers.add(_anonymousAuthenticationProvider);
            authentication = new AnonymousAuthenticationToken(_anonymousAuthenticationProvider.getKey(), authentication.getPrincipal(), Collections.<GrantedAuthority>singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        } else {
            for (AuthenticationProvider candidate : getProviders()) {
                if (!candidate.supports(toTest)) {
                    continue;
                }
                if(authentication instanceof XnatLdapUsernamePasswordAuthenticationToken){
                    if (!(candidate instanceof XnatLdapAuthenticationProvider)) {
                        continue;
                    }
                    XnatLdapAuthenticationProvider ldapCandidate = (XnatLdapAuthenticationProvider) candidate;
                    if (!((XnatLdapUsernamePasswordAuthenticationToken) authentication).getProviderId().equalsIgnoreCase(ldapCandidate.getProviderId())) {
                        //This is a different LDAP provider than the one that was selected.
                        continue;
                    }
                }
                try {
                    if (((XnatDatabaseAuthenticationProvider)candidate).isPlainText()) {
                        String username = authentication.getPrincipal().toString();
                        Boolean encrypted = new JdbcTemplate(_dataSource).query("SELECT primary_password_encrypt<>0 OR (primary_password_encrypt IS NULL AND CHAR_LENGTH(primary_password)=64) FROM xdat_user WHERE login=? LIMIT 1", new String[] {username}, new RowMapper<Boolean>() {
                            public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                                return rs.getBoolean(1);
                            }
                        }).get(0);

                        if (encrypted) continue;
                    }
                } catch (Exception e) {/*casting exceptions can be safely ignored*/}
                providers.add(candidate);
            }
        }

        assert providers.size() > 0: "No provider found for authentication of type " + authentication.getClass().getSimpleName();

        for (AuthenticationProvider provider : providers) {
            _log.debug("Authentication attempt using " + provider.getClass().getName());

            try {
                result = provider.authenticate(authentication);
                if (result != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("Found a provider that worked for " + authentication.getName() + ": " + provider.getClass().getSimpleName());
                    }

                    copyDetails(authentication, result);
                    break;
                }
            } catch (AccountStatusException exception) {
                _log.warn("Error occurred authenticating login request", exception);
                lastException = exception;
            } catch(NewLdapAccountNotAutoEnabledException e) {
                try {
                    AdminUtils.sendNewUserNotification(e.getUserDetails(), "", "", "", new VelocityContext());
                } catch (Exception exception) {
                    _log.error("Error occurred sending new user request email", exception);
                }
                lastException = e;

            } catch (AuthenticationException e) {
                lastException = e;
            }
        }

        if (result != null) {
            boolean eraseCredentialsAfterAuthentication = false;
            if (eraseCredentialsAfterAuthentication && (result instanceof CredentialsContainer)) {
                // Authentication is complete. Remove credentials and other secret data from authentication
                ((CredentialsContainer)result).eraseCredentials();
            }

            eventPublisher.publishAuthenticationSuccess(authentication);

            return result;
            
        } else {
            // Parent was null, or didn't authenticate (or throw an exception).
            if (lastException == null) {
                lastException = new ProviderNotFoundException(messages.getMessage("ProviderManager.providerNotFound",
                        new Object[] {toTest.getName()}, "No AuthenticationProvider found for {0}"));
            }

            eventPublisher.publishAuthenticationFailure(lastException, authentication);
            throw lastException;
        }
    }

    public void setProperties(List<String> fileNames) {
        _properties = new Properties();
        for (String filename : fileNames) {
            String path = "../../../../../../" + filename;
            URL url = getClass().getResource(path);
            if (url != null) {
                try {
                    _properties.load(url.openStream());
                } catch (IOException e) {
                    _log.error(e);
                }
            }
        }
    }

    public static XdatUserAuth getUserByAuth(Authentication authentication) {
        if(authentication==null){
            return null;
        }

        final String u;
        if(authentication.getPrincipal() instanceof String){
            u=(String)authentication.getPrincipal();
        }else{
            u=((UserI)authentication.getPrincipal()).getLogin();
        }
        final String method;
        final String provider;
        if(authentication instanceof XnatLdapUsernamePasswordAuthenticationToken){
            provider=((XnatLdapUsernamePasswordAuthenticationToken)authentication).getProviderId();
            method=XdatUserAuthService.LDAP;
        }else{
            provider=XnatDatabaseUserDetailsService.DB_PROVIDER;
            method=XdatUserAuthService.LOCALDB;
        }

        try {
            return XDAT.getXdatUserAuthService().getUserByNameAndAuth(u, method, provider);
        } catch (DataException exception) {
            _log.error("An error occurred trying to retrieve the auth method", exception);
            throw new RuntimeException("An error occurred trying to validate the given information. Please check your username and password. If this problem persists, please contact your system administrator.");
        }
    }

    public UsernamePasswordAuthenticationToken buildUPTokenForAuthMethod(String authMethod, String username, String password){
        XnatAuthenticationProvider chosenProvider = findAuthenticationProviderByAuthMethod(authMethod);
        return buildUPToken(chosenProvider, username, password);
    }

    public UsernamePasswordAuthenticationToken buildUPTokenForProviderName(String providerName, String username, String password){
        XnatAuthenticationProvider chosenProvider = findAuthenticationProviderByProviderName(providerName);
        return buildUPToken(chosenProvider, username, password);
    }

    public String retrieveAuthMethod(final String username) {
        String auth = cached_methods.get(username);
        if (auth == null) {
            try {
                List<XdatUserAuth> userAuths = _service.getUsersByName(username);
                if (userAuths.size() == 1) {
                    auth = userAuths.get(0).getAuthMethod();
                    cached_methods.put(username.intern(), auth.intern());
                    // The list may contain localdb auth method even when password is empty and LDAP authentication is used (MRH)
                } else if (userAuths.size() > 1) {
                    for (UserAuthI userAuth : userAuths) {
                        auth = userAuth.getAuthMethod();
                        cached_methods.put(username.intern(), auth.intern());
                        if (!userAuth.getAuthMethod().equalsIgnoreCase(XdatUserAuthService.LOCALDB)) {
                            break;
                        }
                    }
                } else if (AliasToken.isAliasFormat(username)) {
                    auth = XdatUserAuthService.TOKEN;
                    cached_methods.put(username.intern(), auth.intern());
                }
            } catch (DataException exception) {
                _log.error("An error occurred trying to retrieve the auth method", exception);
                throw new RuntimeException("An error occurred trying to validate the given information. Please check your username and password. If this problem persists, please contact your system administrator.");
            }
        }
        return auth;
    }

    private static UsernamePasswordAuthenticationToken buildUPToken(XnatAuthenticationProvider provider, String username, String password){
        if (provider instanceof XnatLdapAuthenticationProvider) {
            return new XnatLdapUsernamePasswordAuthenticationToken(username, password, provider.getProviderId());
        } else  if (provider instanceof AliasTokenAuthenticationProvider) {
            return new AliasTokenAuthenticationToken(username, Long.parseLong(password));
        } else {
            return new XnatDatabaseUsernamePasswordAuthenticationToken(username, password);
        }
    }

    private XnatAuthenticationProvider findAuthenticationProviderByAuthMethod(final String authMethod){
        return findAuthenticationProvider(new XnatAuthenticationProviderMatcher() {
            @Override
            public boolean matches(XnatAuthenticationProvider provider) {
                return provider.getAuthMethod().equalsIgnoreCase(authMethod);
            }
        });
    }

    private XnatAuthenticationProvider findAuthenticationProviderByProviderName(final String providerName){
        return findAuthenticationProvider(new XnatAuthenticationProviderMatcher() {
            @Override
            public boolean matches(XnatAuthenticationProvider provider) {
                return provider.getName().equalsIgnoreCase(providerName);
            }
        });
    }

    private XnatAuthenticationProvider findAuthenticationProvider(XnatAuthenticationProviderMatcher matcher){
        List<AuthenticationProvider> prov = getProviders();
        for(AuthenticationProvider ap : prov){
            XnatAuthenticationProvider xap = (XnatAuthenticationProvider) ap;
            if(matcher.matches(xap)){
                return xap;
            }
        }
        return null;
    }

    private void copyDetails(Authentication source, Authentication destination) {
        if ((destination instanceof AbstractAuthenticationToken) && (destination.getDetails() == null)) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) destination;

            token.setDetails(source.getDetails());
}
    }

    private static final class AuthenticationAttemptEventPublisher implements AuthenticationEventPublisher {

        private final FailedAttemptsManager failedAttemptsManager = new FailedAttemptsManager();
        private final LastSuccessfulLoginManager lastSuccessfulLoginManager = new LastSuccessfulLoginManager();

        public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
            //increment failed login attempt
            failedAttemptsManager.addFailedLoginAttempt(authentication);
        }

        public void publishAuthenticationSuccess(Authentication authentication) {
            failedAttemptsManager.clearCount(authentication);
            lastSuccessfulLoginManager.updateLastSuccessfulLogin(authentication);
        }
    }

    private static final class LastSuccessfulLoginManager {
        private void updateLastSuccessfulLogin(Authentication auth) {
            XdatUserAuth ua = getUserByAuth(auth);
            if (ua != null) {
                Date now = java.util.Calendar.getInstance(TimeZone.getDefault()).getTime();
                ua.setLastSuccessfulLogin(now);
                XDAT.getXdatUserAuthService().update(ua);
            }
        }
    }

    private static final class FailedAttemptsManager {
        /**
         * Increments failed Login count
         *
         * @param auth The authentication that failed.
         */
        private synchronized void addFailedLoginAttempt(final Authentication auth) {
            XdatUserAuth ua = getUserByAuth(auth);
            if (ua != null) {
                if (AuthUtils.MAX_FAILED_LOGIN_ATTEMPTS > 0) {
                    ua.setFailedLoginAttempts(ua.getFailedLoginAttempts() + 1);
                    XDAT.getXdatUserAuthService().update(ua);
                }

                if (StringUtils.isNotEmpty(ua.getXdatUsername())) {
                    Integer uid = Users.getUserid(ua.getXdatUsername());
                    if (uid != null) {
                        try {
                            if (ua.getFailedLoginAttempts().equals(AuthUtils.MAX_FAILED_LOGIN_ATTEMPTS)) {
                                String expiration = TurbineUtils.getDateTimeFormatter().format(DateUtils.addMilliseconds(GregorianCalendar.getInstance().getTime(), -(AuthUtils.LOCKOUT_DURATION)));
                                System.out.println("Locked out " + ua.getXdatUsername() + " user account until " + expiration);
                                AdminUtils.sendAdminEmail(ua.getXdatUsername() + " account temporarily disabled.", "User " + ua.getXdatUsername() + " has been temporarily disabled due to excessive failed login attempts. The user's account will be automatically enabled at " + expiration + ".");
                            }
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                }
            }
        }

        public void clearCount(final Authentication auth) {
            if (AuthUtils.MAX_FAILED_LOGIN_ATTEMPTS > 0) {
                XdatUserAuth ua = getUserByAuth(auth);
                if (ua != null) {
                    ua.setFailedLoginAttempts(0);
                    XDAT.getXdatUserAuthService().update(ua);
                }
            }
        }
    }

    private interface XnatAuthenticationProviderMatcher  {
        boolean matches(XnatAuthenticationProvider provider);
    }

    private static final String SECURITY_MAX_FAILED_LOGINS_LOCKOUT_DURATION_PROPERTY = "security.max_failed_logins_lockout_duration";
    private static final String SECURITY_MAX_FAILED_LOGINS_PROPERTY = "security.max_failed_logins";

    private static final Log _log = LogFactory.getLog(XnatProviderManager.class);

    private static Map<String,String> cached_methods= Maps.newConcurrentMap();//this will prevent 20,000 curl scripts from hitting the db everytime

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Inject
    private XdatUserAuthService _service;

    @Inject
    private AnonymousAuthenticationProvider _anonymousAuthenticationProvider;

    @Inject
    private DataSource _dataSource;

    private final AuthenticationEventPublisher eventPublisher = new AuthenticationAttemptEventPublisher();
    private Properties _properties;
}
