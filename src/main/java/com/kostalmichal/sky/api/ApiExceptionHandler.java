package com.kostalmichal.sky.api;

import com.kostalmichal.sky.exception.ExternalProjectNotFoundException;
import com.kostalmichal.sky.exception.InvalidEmailValueException;
import com.kostalmichal.sky.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ProblemDetail handleUserNotFoundException(UserNotFoundException e) {
        ProblemDetail problemDetail
                = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setInstance(URI.create("users/" + e.getId()));
        return problemDetail;

    }

    @ExceptionHandler
    public ProblemDetail handleExternalProjectNotFoundException(ExternalProjectNotFoundException e) {
        ProblemDetail problemDetail
                = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setInstance(URI.create("users/" + e.getUserId() + "/external-projects/" + e.getExternalProjectId()));
        return problemDetail;

    }

    @ExceptionHandler
    public ProblemDetail handleInvalidEmailException(InvalidEmailValueException e) {
        ProblemDetail problemDetail
                = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        return problemDetail;

    }


}
