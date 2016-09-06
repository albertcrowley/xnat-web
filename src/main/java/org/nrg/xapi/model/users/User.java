package org.nrg.xapi.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.nrg.xdat.entities.UserAuthI;
import org.nrg.xdat.om.XdatUser;
import org.nrg.xdat.security.XDATUser;
import org.nrg.xft.security.UserI;

import java.util.Date;

@ApiModel(description = "Contains the properties that define a user on the system.")
public class User {
    public User() {
        // Nothing to see here...
    }

    public User(final String username) {
        this(XDATUser.getXdatUsersByLogin(username, null, false));
    }

    public User(final UserI user) {
        _id = user.getID();
        _username = user.getUsername();
        _firstName = user.getFirstname();
        _lastName = user.getLastname();
        _email = user.getEmail();
        _isAdmin = (user instanceof XDATUser && ((XDATUser) user).isSiteAdmin());
        _dbName = user.getDBName();
        _password = user.getPassword();
        _salt = user.getSalt();
        _lastModified = user.getLastModified();
        _authorization = user.getAuthorization();
        _isEnabled = user.isEnabled();
        _isVerified = user.isVerified();
    }

    public User(final XdatUser user) {
        this((UserI) user);
    }

    /**
     * The user's unique key.
     **/
    @ApiModelProperty(value = "The user's unique key.")
    public Integer getId() {
        return _id;
    }

    public void setId(Integer id) {
        _id = id;
    }

    /**
     * The user's login name.
     **/
    @ApiModelProperty(value = "The user's login name.")
    public String getUsername() {
        return _username;
    }

    public void setUsername(String username) {
        _username = username;
    }

    /**
     * The user's first name.
     **/
    @ApiModelProperty(value = "The user's first name.")
    public String getFirstName() {
        return _firstName;
    }

    @SuppressWarnings("unused")
    public void setFirstName(String firstName) {
        _firstName = firstName;
    }

    /**
     * The user's last name.
     **/
    @ApiModelProperty(value = "The user's last name.")
    public String getLastName() {
        return _lastName;
    }

    @SuppressWarnings("unused")
    public void setLastName(String lastName) {
        _lastName = lastName;
    }

    /**
     * The user's _email address.
     **/
    @ApiModelProperty(value = "The user's email address.")
    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        _email = email;
    }

    /**
     * Whether the user is a site administrator.
     **/
    @ApiModelProperty(value = "Whether the user is a site administrator.")
    public boolean isAdmin() {
        return _isAdmin;
    }

    public void setAdmin(final boolean isAdmin) {
        _isAdmin = isAdmin;
    }

    /**
     * Whether the user is enabled.
     **/
    @ApiModelProperty(value = "Whether the user is enabled.")
    public boolean isEnabled() {
        return _isEnabled;
    }

    public void setEnabled(final boolean isEnabled) {
        _isEnabled = isEnabled;
    }

    /**
     * Whether the user is verified.
     **/
    @ApiModelProperty(value = "Whether the user is verified.")
    public boolean isVerified() {
        return _isVerified;
    }

    public void setVerified(final boolean isVerified) {
        _isVerified = isVerified;
    }

    /**
     * The user's encrypted password.
     **/
    @ApiModelProperty(value = "The user's encrypted password.")
    @JsonIgnore
    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    /**
     * The _salt used to encrypt the user's _password.
     **/
    @ApiModelProperty(value = "The salt used to encrypt the user's password.")
    @JsonIgnore
    public String getSalt() {
        return _salt;
    }

    @SuppressWarnings("unused")
    public void setSalt(String salt) {
        _salt = salt;
    }

    /**
     * The date and time the user record was last modified.
     **/
    @ApiModelProperty(value = "The date and time the user record was last modified.")
    public Date getLastModified() {
        return _lastModified;
    }

    public void setLastModified(Date lastModified) {
        _lastModified = lastModified;
    }

    /**
     * The user's authorization record used when logging in.
     **/
    @ApiModelProperty(value = "The user's authorization record used when logging in.")
    public UserAuthI getAuthorization() {
        return _authorization;
    }

    @SuppressWarnings("unused")
    public void setAuthorization(UserAuthI authorization) {
        _authorization = authorization;
    }

    @ApiModelProperty(value = "The user's full name.")
    @JsonIgnore
    public String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }

    @Override
    public String toString() {
        return "class User {\n" +
               "  id: " + _id + "\n" +
               "  username: " + _username + "\n" +
               "  firstName: " + _firstName + "\n" +
               "  lastName: " + _lastName + "\n" +
               "  email: " + _email + "\n" +
               "  dbName: " + _dbName + "\n" +
               "  password: " + _password + "\n" +
               "  salt: " + _salt + "\n" +
               "  lastModified: " + _lastModified + "\n" +
               "  authorization: " + _authorization + "\n" +
               "}\n";
    }

    private Integer   _id;
    private String    _username;
    private String    _firstName;
    private String    _lastName;
    private String    _email;
    private String    _dbName;
    private String    _password;
    private String    _salt;
    private Date      _lastModified;
    private UserAuthI _authorization;
    private boolean   _isAdmin;
    private boolean   _isEnabled;
    private boolean   _isVerified;
}
