/*
 * org.nrg.xnat.turbine.modules.screens.ManageProtocol
 * XNAT http://www.xnat.org
 * Copyright (c) 2013, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Author: Justin Cleveland <clevelandj@wustl.edu>
 * Last modified 3/7/2016 11:52 AM
 */

package org.nrg.xapi.rest.theme;

import io.swagger.annotations.*;
import org.apache.commons.io.FileUtils;
import org.nrg.xapi.rest.NotFoundException;
import org.nrg.xdat.security.XDATUser;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.configuration.ThemeConfig;
import org.nrg.xnat.services.ThemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Api(description = "XNAT Theme Management API")
@RestController
@RequestMapping(value = "/theme")
public class ThemeApi {
    private static final Logger _log = LoggerFactory.getLogger(ThemeApi.class);

    @Autowired
    private ThemeService themeService;

    @ApiOperation(value = "Get the currently selected global theme or a role based theme if specified.", notes = "Use this to get the theme selected by the system administrator on the Theme Management page.", response = ThemeConfig.class, responseContainer = "ThemeConfig")
    @ApiResponses({@ApiResponse(code = 200, message = "Reports the currently selected global theme (if there is one) and whether or not it's enabled."), @ApiResponse(code = 500, message = "Unexpected error")})
    @RequestMapping(value = {"/{role}"}, produces = {"application/json", "application/xml"}, method = RequestMethod.GET)
    public ResponseEntity<ThemeConfig> themeGet(@ApiParam(value = "\"global\" or role name of currently set theme", required = true) @PathVariable("role") String role) {
        if("global".equalsIgnoreCase(role)){
            return new ResponseEntity<>(themeService.getTheme(), HttpStatus.OK);
        }
        return new ResponseEntity<>(themeService.getTheme(role), HttpStatus.OK);
    }

    @ApiOperation(value = "Get list of available themes.", notes = "Use this to get a list of all available themes on the XNAT system.", response = ThemeService.TypeOption.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Reports the currently selected global theme (if there is one), whether or not it's enabled, and a list of available themes on the system in a JSON string."), @ApiResponse(code = 500, message = "Unexpected error")})
    @RequestMapping(produces = {"application/json", "application/xml"}, method = RequestMethod.GET)
    public ResponseEntity<List<ThemeService.TypeOption>> themesGet() {
        return new ResponseEntity<>(themeService.loadExistingThemes(), HttpStatus.OK);
    }

    @ApiOperation(value = "Deletes the theme with the specified name.", notes = "Returns success on deletion. ", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Theme was successfully deleted."), @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."), @ApiResponse(code = 403, message = "Not authorized to delete a theme."), @ApiResponse(code = 404, message = "Theme not found."), @ApiResponse(code = 500, message = "Unexpected error")})
    @RequestMapping(value = {"/{theme}"}, produces = {"application/json", "application/xml", "text/html"}, method = {RequestMethod.DELETE})
    public ResponseEntity<ThemeConfig> themeDelete(@ApiParam(value = "Name of the theme to delete", required = true) @PathVariable("theme") String theme) {
        ThemeConfig themeConfig = null;
        HttpStatus status = isPermitted();
        if (status != null) {
            return new ResponseEntity<>(status);
        }
        if("null".equals(theme)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(!themeService.themeExists(theme)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            File f = new File(themeService.getThemesPath() + File.separator + theme);
            FileUtils.deleteDirectory(f);
            if(!f.exists()) {
                themeConfig = themeService.getTheme();
                String themeName = (themeConfig != null) ? themeConfig.getName() : null;
                if (theme.equals(themeName)) {
                    themeService.setTheme((ThemeConfig) null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return themeConfig != null ? new ResponseEntity<>(themeConfig, HttpStatus.OK) : new ResponseEntity<ThemeConfig>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Sets the current global theme to the one specified.", notes = "Returns the updated serialized theme object.", response = ThemeConfig.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully updated the current global theme."), @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."), @ApiResponse(code = 403, message = "Not authorized to create or update this user."), @ApiResponse(code = 404, message = "Theme not found."), @ApiResponse(code = 500, message = "Unexpected error")})
    @RequestMapping(value = {"/{theme}"}, produces = {"application/json", "application/xml", "text/html"}, method = {RequestMethod.PUT})
    public ResponseEntity<ThemeConfig> themePut(@ApiParam(value = "The name of the theme to select.", required = true) @PathVariable("theme") String theme, @RequestParam(value = "enabled", required=false, defaultValue="true") String enabled) throws NotFoundException {
        HttpStatus status = isPermitted();
        if (status != null) {
            return new ResponseEntity<>(status);
        }
        ThemeConfig themeConfig;
        try {
            boolean themeEnabled = true;
            if("false".equalsIgnoreCase(enabled)){
                themeEnabled = false;
            }
            if("null".equals(theme)){
                theme = null;
            }
            themeConfig = themeService.setTheme(theme, themeEnabled);
        } catch (ThemeService.ThemeNotFoundException e) {
            _log.error(e.getInvalidTheme()+" not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            _log.error("An error occurred setting the theme " + theme, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(themeConfig, HttpStatus.OK);                        // TODO: fix the return on this. It's showing up as [object Object] on the page!
    }

    @ApiOperation(value = "Accepts a multipart form with a zip file upload and extracts its contents in the theme system folder. If successful, the first (root) directory name (or theme name) unzipped is returned in the response. This will overwrite any other directories already existing with the same name without warning.", notes = "The structure of the zipped package must have only directories at it's root.", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Theme package successfully uploaded and extracted."), @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."), @ApiResponse(code = 403, message = "Not authorized to upload a theme package."), @ApiResponse(code = 500, message = "Unexpected error")})
    @RequestMapping(produces = {"application/json"}, method = {RequestMethod.POST})
    public ResponseEntity<List<ThemeService.TypeOption>> themePostUpload(@ApiParam(value = "Multipart file object being uploaded", required = true) @RequestParam(value="themePackage", required=false) MultipartFile themePackage) {
        HttpStatus status = isPermitted();
        if (status != null) {
            return new ResponseEntity<>(status);
        }
        List<ThemeService.TypeOption> themeOptions = new ArrayList<>();
        try {
            if(!themePackage.getContentType().contains("zip")) {
                String error = "No valid files were uploaded. Theme package must be of type: application/x-zip-compressed";
                _log.error(error);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            List<String> dirs = themeService.extractTheme(themePackage.getInputStream());
            for (String dir : dirs) {
                themeOptions.add(new ThemeService.TypeOption(dir, dir));
            }
        } catch (IOException e) {
            e.printStackTrace();
            String error = "An error occurred extracting the theme package";
            _log.error(error, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            String error = "An unknown error occurred accepting the theme package";
            _log.error(error, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(themeOptions, HttpStatus.OK);
    }

    private UserI getSessionUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if ((principal instanceof UserI)) {
            return (UserI) principal;
        }
        return null;
    }

    private HttpStatus isPermitted() {
        UserI sessionUser = getSessionUser();
        if ((sessionUser instanceof XDATUser)) {
            return ((XDATUser) sessionUser).isSiteAdmin() ? null : HttpStatus.FORBIDDEN;
        }
        return null;
    }
}