package com.example.librarymanager.exception;

import com.example.librarymanager.base.RestData;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.ErrorMessage;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalExceptionHandle {

    MessageSource messageSource;

    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<RestData<?>> handlerInternalServerError(Exception ex) {
        log.error("Internal Server Error: {}", ex.getMessage(), ex);
        String errorMessage = getMessage(ErrorMessage.ERR_EXCEPTION_GENERAL, null);
        return VsResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> result = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach((error) -> {
            String fieldName = ((PathImpl) error.getPropertyPath()).getLeafNode().getName();
            String errorMessage = getMessage(Objects.requireNonNull(error.getMessage()), null);
            result.put(fieldName, errorMessage);
        });
        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, result);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handleValidException(BindException ex) {
        Map<String, String> result = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage;
            try {
                errorMessage = getMessage(Objects.requireNonNull(error.getDefaultMessage()), null);
            } catch (NoSuchMessageException e) {
                errorMessage = error.getDefaultMessage();
            }
            result.put(fieldName, errorMessage);
        });
        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, result);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<RestData<?>> handlerNotFoundException(NotFoundException ex) {
        String errorMessage = getMessage(ex.getMessage(), ex.getParams());
        return VsResponseUtil.error(HttpStatus.NOT_FOUND, errorMessage);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handlerInvalidException(BadRequestException ex) {
        String errorMessage = getMessage(ex.getMessage(), ex.getParams());
        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(BadGatewayException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ResponseEntity<RestData<?>> handleUploadImageException(BadGatewayException ex) {
        String errorMessage = getMessage(ex.getMessage(), ex.getParams());
        return VsResponseUtil.error(HttpStatus.BAD_GATEWAY, errorMessage);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<RestData<?>> handleUnauthorizedException(UnauthorizedException ex) {
        String errorMessage = getMessage(ex.getMessage(), ex.getParams());
        return VsResponseUtil.error(HttpStatus.UNAUTHORIZED, errorMessage);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<RestData<?>> handleForbiddenException(ForbiddenException ex) {
        String errorMessage = getMessage(ex.getMessage(), ex.getParams());
        return VsResponseUtil.error(HttpStatus.FORBIDDEN, errorMessage);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<RestData<?>> handleAccessDeniedException() {
        String errorMessage = getMessage(ErrorMessage.ERR_FORBIDDEN, null);
        return VsResponseUtil.error(HttpStatus.FORBIDDEN, errorMessage);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<RestData<?>> handleDataIntegrityViolationException(ConflictException ex) {
        String errorMessage = getMessage(ex.getMessage(), ex.getParams());
        return VsResponseUtil.error(HttpStatus.CONFLICT, errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handleHttpMessageNotReadableException() {
        String errorMessage = getMessage(ErrorMessage.ERR_REQUEST_BODY, null);
        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<RestData<?>> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String errorMessage = getMessage(ErrorMessage.ERR_METHOD_NOT_SUPPORTED, ex.getSupportedMethods());
        return VsResponseUtil.error(HttpStatus.METHOD_NOT_ALLOWED, errorMessage);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String parameterName = ex.getParameterName();
        String errorMessage = getMessage(ErrorMessage.ERR_REQUIRED_MISSING_PARAMETER, new Object[]{parameterName});
        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String parameterName = ex.getParameter().getParameterName();
        String errorMessage = getMessage(ErrorMessage.ERR_METHOD_ARGUMENT_TYPE_MISMATCH, new Object[]{parameterName});
        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<RestData<?>> handleNoResourceFoundException(NoResourceFoundException ex) {
        String resourcePath = ex.getResourcePath();
        String errorMessage = getMessage(ErrorMessage.ERR_RESOURCE_NOT_FOUND, new Object[]{resourcePath});
        return VsResponseUtil.error(HttpStatus.NOT_FOUND, errorMessage);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseEntity<RestData<?>> handleMaxUploadSizeExceeded() {
        String errorMessage = getMessage(ErrorMessage.INVALID_MAX_UPLOAD_SIZE_EXCEEDED, null);
        return VsResponseUtil.error(HttpStatus.PAYLOAD_TOO_LARGE, errorMessage);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        String partName = ex.getRequestPartName();
        String errorMessage = getMessage(ErrorMessage.ERR_MISSING_SERVLET_REQUEST_PART, new Object[]{partName});
        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<RestData<?>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String supportedContentType = ex.getSupportedMediaTypes().stream()
                .map(MediaType::toString)
                .collect(Collectors.joining());
        String errorMessage = getMessage(ErrorMessage.ERR_UNSUPPORTED_MEDIA_TYPE, new Object[]{supportedContentType});
        return VsResponseUtil.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, errorMessage);
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<RestData<?>> handleMultipartException(MultipartException ex) {
        log.error("Multipart Exception: {}", ex.getMessage(), ex);
        String errorMessage = getMessage(ErrorMessage.ERR_MULTIPART_EXCEPTION, null);
        return VsResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal Argument Exception: {}", ex.getMessage(), ex);
        String errorMessage = getMessage(ErrorMessage.ERR_ILLEGAL_ARGUMENT, null);
        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

}
