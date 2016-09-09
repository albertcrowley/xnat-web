package org.nrg.xapi.rest;

import org.nrg.dcm.exceptions.EnabledDICOMReceiverWithDuplicatePortException;
import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceException;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class XapiRestControllerAdvice {
    @ExceptionHandler(EnabledDICOMReceiverWithDuplicatePortException.class)
    public ModelAndView handleEnabledDICOMReceiverWithDuplicatePort(final HttpServletRequest request, final EnabledDICOMReceiverWithDuplicatePortException exception) {
        return handleException(request, exception.getMessage());
    }

    @ExceptionHandler(DataFormatException.class)
    public ModelAndView handleDataFormatException(final HttpServletRequest request, final DataFormatException exception) {
        return handleException(request, exception.getMessage(), exception);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ModelAndView handleDataFormatException(final HttpServletRequest request, final ResourceAlreadyExistsException exception) {
        return handleException(request, exception.getMessage(), exception);
    }

    @ExceptionHandler(NrgServiceException.class)
    public ModelAndView handleNrgServiceException(final HttpServletRequest request, final NrgServiceException exception) {
        return handleException(HttpStatus.CONFLICT, request, "An NRG service error occurred.", exception);
    }

    @ExceptionHandler(URISyntaxException.class)
    public ModelAndView handleUriSyntaxException(final HttpServletRequest request, final URISyntaxException exception) {
        final String message = "An error occurred at index " + exception.getIndex() + " when processing the URI " + exception.getInput() + ": " + exception.getMessage();
        return handleException(HttpStatus.BAD_REQUEST, request, message);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ModelAndView handleFileNotFoundException(final HttpServletRequest request, final FileNotFoundException exception) {
        return handleException(HttpStatus.BAD_REQUEST, request, "Unable to find requested file or resource: " + exception.getMessage(), exception);
    }

    private ModelAndView handleException(final HttpServletRequest request, final String message) {
        return handleException(request, message, null);
    }

    private ModelAndView handleException(final HttpServletRequest request, final String message, final Exception exception) {
        final ResponseStatus status = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
        if (status == null) {
            throw new NrgServiceRuntimeException(NrgServiceError.ConfigurationError, "Only exceptions with @ResponseStatus annotation can be handled through this method.", exception);
        }
        return handleException(status.value(), request, message, exception);
    }

    private ModelAndView handleException(final HttpStatus status, final HttpServletRequest request, final String message) {
        return handleException(status, request, message, null);
    }

    private ModelAndView handleException(final HttpStatus status, final HttpServletRequest request, final String message, final Exception exception) {
        @SuppressWarnings("SpringMVCViewInspection")
        final ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", status);
        modelAndView.addObject("url", request.getRequestURL().toString());
        modelAndView.addObject("message", message);
        if (exception != null) {
            modelAndView.addObject("exception", exception);
            final StackTraceElement[] stackTrace = exception.getStackTrace();
            if (stackTrace != null && stackTrace.length > 1) {
                final List<String> elements = new ArrayList<>();
                for (final StackTraceElement element : stackTrace) {
                    elements.add(element.toString());
                    if (element.toString().startsWith("javax.servlet.http.HttpServlet.service")) {
                        elements.add("(stack trace truncated for readability, see server logs for full ");
                        break;
                    }
                }
                modelAndView.addObject("stacktrace", elements);
            }
        }
        _log.error("An exception was encountered", exception);
        return modelAndView;
    }

    private static final Logger _log = LoggerFactory.getLogger(XapiRestControllerAdvice.class);
}
