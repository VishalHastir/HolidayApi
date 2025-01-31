package com.holiday.api.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid ID");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgumentException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "Invalid input: Invalid ID");
    }

    @Test
    void testHandleHttpClientErrorException() {
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleHttpClientErrorException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "API error: 404 Not Found");
    }

    @Test
    void testHandleResourceAccessException() {
        ResourceAccessException ex = new ResourceAccessException("Timeout while connecting");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleResourceAccessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).containsEntry("message", "External service is unreachable: Timeout while connecting");
    }

    @Test
    void testHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleHttpMessageNotReadableException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("message");
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("message", "An unexpected error occurred: Unexpected error");
    }

    @Test
    void testHandleNoResourceFoundException() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleNoResourceFoundException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("message");
    }

    @Test
    void testHandleMissingParams() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("date", "String");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleMissingParams(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "Missing required parameter: date");
    }
}

