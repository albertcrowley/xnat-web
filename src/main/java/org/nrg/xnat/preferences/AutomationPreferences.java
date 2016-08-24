package org.nrg.xnat.preferences;

import org.nrg.framework.services.NrgEventService;
import org.nrg.prefs.annotations.NrgPreference;
import org.nrg.prefs.annotations.NrgPreferenceBean;
import org.nrg.prefs.exceptions.InvalidPreferenceName;
import org.nrg.xdat.preferences.EventTriggeringAbstractPreferenceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@NrgPreferenceBean(toolId = AutomationPreferences.AUTOMATION_TOOL_ID,
                   toolName = "XNAT Automation Preferences",
                   description = "Manages preferences and settings for the XNAT automation services.",
                   properties = "META-INF/xnat/preferences/automation.properties",
                   strict = false)
public class AutomationPreferences extends EventTriggeringAbstractPreferenceBean {
    public static final String AUTOMATION_TOOL_ID = "automation";

    @Autowired
    public AutomationPreferences(final NrgEventService eventService) {
        super(eventService);
    }

    @NrgPreference(defaultValue = "true")
    public boolean isInternalScriptingEnabled() {
        return getBooleanValue("internalScriptingEnabled");
    }

    public void setInternalScriptingEnabled(final boolean internalScriptingEnabled) {
        try {
            setBooleanValue(internalScriptingEnabled, "internalScriptingEnabled");
        } catch (InvalidPreferenceName e) {
            _log.error("Invalid preference name internalScriptingEnabled: something is very wrong here.", e);
        }
    }

    private static final Logger _log = LoggerFactory.getLogger(AutomationPreferences.class);
}
