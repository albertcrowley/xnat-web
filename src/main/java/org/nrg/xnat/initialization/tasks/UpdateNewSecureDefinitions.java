package org.nrg.xnat.initialization.tasks;

import org.nrg.xdat.security.ElementSecurity;
import org.nrg.xdat.security.services.FeatureRepositoryServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UpdateNewSecureDefinitions extends AbstractInitializingTask {
    @Override
    public String getTaskName() {
        return "Update new secure definitions";
    }

    @Override
    public void run() {
        try {
            if (ElementSecurity.GetElementSecurities() != null) {
                _log.debug("Found element securities, running update new secure definitions.");
                _featureRepositoryService.updateNewSecureDefinitions();
            }
            complete();
        } catch (Exception ignore) {
            // No worries...
        }
    }

    private static final Logger _log = LoggerFactory.getLogger(UpdateNewSecureDefinitions.class);

    @Autowired
    @Lazy
    private FeatureRepositoryServiceI _featureRepositoryService;
}