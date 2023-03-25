package com.amazonaws.saas.eks.error;

import com.amazonaws.saas.eks.util.Utils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    @Getter
    private HttpStatus status;

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Utils.dateFormatPattern)
    private LocalDateTime timestamp;

    @Getter
    private String message;

    @Getter
    private String debugMessage;

    @Getter
    private List<ApiSubError> subErrors;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    public ApiError(HttpStatus status, String message) {
        this(status);
        this.message = message;
    }

    public ApiError(HttpStatus status, String message, Throwable ex) {
        this(status, message);
        this.debugMessage = ex.getLocalizedMessage();
    }

    public ApiError(HttpStatus status, Throwable ex) {
        this(status, "Unexpected error", ex);
    }

    public void addValidationErrors(List<FieldError> fieldErrors) {
        fieldErrors.forEach(this::addValidationError);
    }

    public void addValidationError(List<ObjectError> globalErrors) {
        globalErrors.forEach(this::addValidationError);
    }

    private void addValidationError(FieldError fieldError) {
        this.addValidationError(
                fieldError.getObjectName(),
                fieldError.getField(),
                fieldError.getRejectedValue(),
                fieldError.getDefaultMessage());
    }

    private void addValidationError(ObjectError objectError) {
        this.addValidationError(objectError.getObjectName(), objectError.getDefaultMessage());
    }

    private void addValidationError(String object, String field, Object rejectedValue, String message) {
        addSubError(new ApiValidationError(object, field, rejectedValue, message));
    }

    private void addValidationError(String object, String message) {
        addSubError(new ApiValidationError(object, message));
    }

    private void addSubError(ApiSubError subError) {
        if (subErrors == null) {
            subErrors = new ArrayList<>();
        }
        subErrors.add(subError);
    }
}
