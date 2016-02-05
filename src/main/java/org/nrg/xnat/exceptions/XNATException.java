/*
 * org.nrg.xnat.exceptions.XNATException
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/29/13 3:21 PM
 */
package org.nrg.xnat.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.nrg.xft.security.UserI;

public class XNATException extends Exception {

    public XNATException() {
        super();
    }

    public XNATException(final UserI user) {
        super();
        _user = user;
    }

    public XNATException(final String message, final UserI user) {
        super(message);
        _user = user;
    }

    @Override
    public String getMessage() {
        StringBuilder buffer = new StringBuilder();
        for (Map.Entry<String, Object> parameter : _parameters.entrySet()) {
            buffer.append(parameter.getKey()).append(": ").append(parameter.getValue().toString()).append("\n");
        }
        buffer.append("\n");
        buffer.append(super.getMessage());
        buffer.append("\n");
        return buffer.toString();
    }

    public UserI getUser() {
        return _user;
    }

    public void setUser(final UserI user) {
        _user = user;
    }

    protected Object getParameter(final String name) {
        return _parameters.get(name);
    }

    protected void setParameter(final String name, final Object value) {
        _parameters.put(name, value);
    }

    private Map<String, Object> _parameters = new HashMap<String, Object>();
    private UserI _user;
}