package com.amazonaws.saas.eks.error;

import com.amazonaws.saas.eks.auth.exception.BadAuthContextException;
import com.amazonaws.saas.eks.exception.EntityExistsException;
import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.services.cognitoidp.model.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     *
     * @param ex the HttpMessageNotReadableException that is thrown when the request JSON is malformed
     * @param headers the HTTP headers
     * @param status the HTTP status code
     * @param request the generic interface for a web request
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
     *
     * @param ex the MethodArgumentNotValidException that is thrown when @Valid validation fails
     * @param headers the HTTP headers
     * @param status the HTTP status code
     * @param request the generic interface for a web request
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Validation error");
        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handleCognitoUserNotFound(UserNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getErrorMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleCognitoResourceNotFound(ResourceNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getErrorMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InvalidParameterException.class)
    protected ResponseEntity<Object> handleCognitoInvalidParameter(InvalidParameterException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getErrorMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    protected ResponseEntity<Object> handleCognitoInvalidPassword(InvalidPasswordException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getErrorMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    protected ResponseEntity<Object> handleCognitoNotAuthorized(NotAuthorizedException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getErrorMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UsernameExistsException.class)
    protected ResponseEntity<Object> handleCognitoUsernameExists(UsernameExistsException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getErrorMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AliasExistsException.class)
    protected ResponseEntity<Object> handleCognitoAliasExists(AliasExistsException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getErrorMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(GroupExistsException.class)
    protected ResponseEntity<Object> handleCognitoGroupExists(GroupExistsException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getErrorMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InternalErrorException.class)
    protected ResponseEntity<Object> handleCognitoInternalError(InternalErrorException ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getErrorMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(BadAuthContextException.class)
    protected ResponseEntity<Object> handleBadAuthContext(BadAuthContextException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleBadAuthContext(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EntityExistsException.class)
    protected ResponseEntity<Object> handleBadAuthContext(EntityExistsException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
