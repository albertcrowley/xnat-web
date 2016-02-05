/*
 * org.nrg.xnat.services.messaging.prearchive.DeleteSessionRequest
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 12/19/13 3:01 PM
 */
package org.nrg.xnat.services.messaging.prearchive;
import org.nrg.xnat.helpers.prearchive.SessionData;
import java.io.File;
import java.io.Serializable;

import org.nrg.xft.security.UserI;

public class DeleteSessionRequest implements Serializable {
    public DeleteSessionRequest(UserI user, SessionData sessionData, File sessionDir) {
        _user = user;
        _sessionData = sessionData;
        _sessionDir = sessionDir;
    }

    public UserI getUser() {
        return _user;
    }

    public SessionData getSessionData() {
        return _sessionData;
    }

    public File getSessionDir() {
        return _sessionDir;
    }

    private static final long serialVersionUID = -6953780271999788326L;

    private final UserI _user;
    private final SessionData _sessionData;
    private final File _sessionDir;
}