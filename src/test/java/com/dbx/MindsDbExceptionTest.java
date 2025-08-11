package com.dbx;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class MindsDbExceptionTest {

    @Test
    @DisplayName("Should create exception with message only")
    void testCreateExceptionWithMessage() {
        String message = "Test error message";
        MindsDbException exception = new MindsDbException(message);

        assertEquals(message, exception.getMessage());
        assertEquals(-1, exception.getStatusCode());
        assertNull(exception.getResponseBody());
        assertFalse(exception.hasHttpDetails());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void testCreateExceptionWithMessageAndCause() {
        String message = "Test error message";
        RuntimeException cause = new RuntimeException("Root cause");
        MindsDbException exception = new MindsDbException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(-1, exception.getStatusCode());
        assertNull(exception.getResponseBody());
        assertFalse(exception.hasHttpDetails());
    }

    @Test
    @DisplayName("Should create exception with HTTP details")
    void testCreateExceptionWithHttpDetails() {
        String message = "HTTP error occurred";
        int statusCode = 404;
        String responseBody = "{\"error\":\"Not found\"}";
        
        MindsDbException exception = new MindsDbException(message, statusCode, responseBody);

        assertEquals(message, exception.getMessage());
        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(responseBody, exception.getResponseBody());
        assertTrue(exception.hasHttpDetails());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should create exception with HTTP details and cause")
    void testCreateExceptionWithHttpDetailsAndCause() {
        String message = "HTTP error occurred";
        int statusCode = 500;
        String responseBody = "{\"error\":\"Server error\"}";
        RuntimeException cause = new RuntimeException("Network error");
        
        MindsDbException exception = new MindsDbException(message, statusCode, responseBody, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(responseBody, exception.getResponseBody());
        assertTrue(exception.hasHttpDetails());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Should provide meaningful toString with HTTP details")
    void testToStringWithHttpDetails() {
        String message = "API request failed";
        int statusCode = 400;
        String responseBody = "{\"error\":\"Bad request\",\"details\":\"Invalid parameters\"}";
        
        MindsDbException exception = new MindsDbException(message, statusCode, responseBody);
        String toString = exception.toString();

        assertTrue(toString.contains("MindsDbException: " + message));
        assertTrue(toString.contains("[HTTP " + statusCode + "]"));
        assertTrue(toString.contains("Response: " + responseBody));
    }

    @Test
    @DisplayName("Should provide meaningful toString without HTTP details")
    void testToStringWithoutHttpDetails() {
        String message = "General error";
        RuntimeException cause = new RuntimeException("Root cause");
        
        MindsDbException exception = new MindsDbException(message, cause);
        String toString = exception.toString();

        assertTrue(toString.contains("MindsDbException: " + message));
        assertTrue(toString.contains("Caused by: Root cause"));
        assertFalse(toString.contains("[HTTP"));
        assertFalse(toString.contains("Response:"));
    }

    @Test
    @DisplayName("Should truncate long response body in toString")
    void testToStringWithLongResponseBody() {
        String message = "API error";
        int statusCode = 500;
        StringBuilder longResponse = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            longResponse.append("a");
        }
        
        MindsDbException exception = new MindsDbException(message, statusCode, longResponse.toString());
        String toString = exception.toString();

        assertTrue(toString.contains("..."));
        assertTrue(toString.length() < message.length() + longResponse.length() + 100);
    }

    @Test
    @DisplayName("Should handle null response body")
    void testToStringWithNullResponseBody() {
        String message = "API error";
        int statusCode = 404;
        
        MindsDbException exception = new MindsDbException(message, statusCode, null);
        String toString = exception.toString();

        assertTrue(toString.contains("MindsDbException: " + message));
        assertTrue(toString.contains("[HTTP " + statusCode + "]"));
        assertFalse(toString.contains("Response:"));
    }

    @Test
    @DisplayName("Should handle empty response body")
    void testToStringWithEmptyResponseBody() {
        String message = "API error";
        int statusCode = 204;
        String responseBody = "";
        
        MindsDbException exception = new MindsDbException(message, statusCode, responseBody);
        String toString = exception.toString();

        assertTrue(toString.contains("MindsDbException: " + message));
        assertTrue(toString.contains("[HTTP " + statusCode + "]"));
        assertFalse(toString.contains("Response:"));
    }
}