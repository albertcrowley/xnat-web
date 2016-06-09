package org.nrg.xapi.rest.schemas;

import io.swagger.annotations.*;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.nrg.xdat.rest.AbstractXnatRestApi;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Api(description = "XNAT Data Type Schemas Management API")
@XapiRestController
@RequestMapping(value = "/schemas")
public class SchemaApi extends AbstractXnatRestApi {
    @ApiOperation(value = "Get list of all available XNAT data type schemas.", notes = "This searches the XNAT classpath for all of the available XSD files located in a folder that matches the pattern \"schemas/<name>/<name.xsd>\".", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of available XNAT data type schemas."), @ApiResponse(code = 500, message = "Unexpected error")})
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Collection<String>> getDataTypeSchemas() throws IOException {
        return new ResponseEntity<>(getPaths().values(), HttpStatus.OK);
    }

    @ApiOperation(value = "Gets the contents of the XNAT data type schema matching the specified ID.", notes = "The ID is used to compose the path to the schema, e.g. the ID \"xnat\" matches the schema at \"schemas/xnat/xnat.xsd\".", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Requested schema successfully retrieved."), @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."), @ApiResponse(code = 403, message = "Not authorized to view this data type schema."), @ApiResponse(code = 404, message = "Data type schema not found."), @ApiResponse(code = 500, message = "Unexpected error")})
    @RequestMapping(value = "{id}", produces = {MediaType.APPLICATION_XML_VALUE}, method = {RequestMethod.GET})
    public ResponseEntity<String> getDataTypeSchema(@ApiParam(value = "ID of the data type schema to fetch.", required = true) @PathVariable("id") final String id) throws IOException {
        if (!getSchemas().containsKey(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(getSchemas().get(id).getURI().toString(), HttpStatus.OK);
    }

    private Map<String, String> getPaths() throws IOException {
        getSchemas();
        return _paths;
    }

    private Map<String, Resource> getSchemas() throws IOException {
        if (_schemas.size() == 0) {
            synchronized (_schemas) {
                final List<Resource> resources = BasicXnatResourceLocator.getResources("classpath*:schemas/**/*.xsd");
                for (final Resource resource : resources) {
                    final Matcher matcher = SCHEMA_PATTERN.matcher(resource.getURI().toString());
                    if (matcher.find()) {
                        _paths.put(matcher.group("id"), matcher.group(0));
                        _schemas.put(matcher.group("id"), resource);
                    }
                }
            }
        }
        return _schemas;
    }

    private static final Pattern SCHEMA_PATTERN = Pattern.compile("^schemas/(?<id>\\w+)/\\1.xsd$");

    private final Map<String, String> _paths = new HashMap<>();
    private final Map<String, Resource> _schemas = new HashMap<>();
}
