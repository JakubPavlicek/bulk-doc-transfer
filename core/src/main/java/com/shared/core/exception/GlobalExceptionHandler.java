package com.shared.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private String getRequestURI(WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        return servletWebRequest.getRequest().getRequestURI();
    }

    @ExceptionHandler(SubmissionNotFoundException.class)
    public ProblemDetail handleSubmissionNotFoundException(SubmissionNotFoundException ex) {
        log.warn(ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Submission Not Found");

        return problemDetail;
    }

    @ExceptionHandler(SubmissionFileNotFoundException.class)
    public ProblemDetail handleSubmissionNotFoundException(SubmissionFileNotFoundException ex) {
        log.warn(ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Submission File Not Found");

        return problemDetail;
    }

    @ExceptionHandler(SubmissionFileReadException.class)
    public ProblemDetail handleSubmissionFileReadException(SubmissionFileReadException ex) {
        log.warn(ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Submission File Read Error");

        return problemDetail;
    }

    @ExceptionHandler(FileProcessingEexception.class)
    public ProblemDetail handleFileProcessingException(FileProcessingEexception ex) {
        log.warn(ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("File Processing Error");

        return problemDetail;
    }

    @Override
    protected ResponseEntity<@NonNull Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request
    ) {
        log.error("Request {} raised", getRequestURI(request), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Invalid Value");
        problemDetail.setProperty("errors", getErrors(ex));

        return ResponseEntity.of(problemDetail)
                             .build();

    }

    private Map<String, List<String>> getErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                 .getFieldErrors()
                 .stream()
                 .collect(Collectors.groupingBy(
                     FieldError::getField,
                     Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                 ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.error("Request {} raised", request.getRequestURI(), e);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid Value");
        problemDetail.setProperty("violations", getConstraintViolations(e));

        return problemDetail;
    }

    private Map<String, List<String>> getConstraintViolations(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                 .stream()
                 .collect(Collectors.groupingBy(
                     violation -> ((PathImpl) violation.getPropertyPath()).getLeafNode().toString(),
                     Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                 ));
    }

    @Override
    protected ResponseEntity<@NonNull Object> handleHttpMessageNotReadable(
        @NonNull HttpMessageNotReadableException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request
    ) {
        log.error("Request {} raised", getRequestURI(request), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMostSpecificCause().getMessage());
        problemDetail.setTitle("Invalid Value");

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex, HttpServletRequest request) {
        log.error("Request {} raised", request.getRequestURI(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
        problemDetail.setTitle("Internal Server Error");

        return problemDetail;
    }

}
