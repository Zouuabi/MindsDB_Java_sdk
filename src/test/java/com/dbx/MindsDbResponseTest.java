package com.dbx;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class MindsDbResponseTest {

    @Test
    @DisplayName("Should create response with valid JSON")
    void testCreateResponseWithValidJson() {
        String jsonResponse = "{\"status\":\"success\",\"count\":42,\"accuracy\":0.95,\"active\":true}";
        MindsDbResponse response = new MindsDbResponse(200, jsonResponse);

        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatusCode());
        assertEquals(jsonResponse, response.getRawResponse());
        assertTrue(response.hasJsonData());
        
        assertEquals("success", response.getStringField("status"));
        assertEquals(42, response.getIntField("count"));
        assertEquals(0.95, response.getDoubleField("accuracy"), 0.001);
        assertTrue(response.getBooleanField("active"));
    }

    @Test
    @DisplayName("Should handle invalid JSON gracefully")
    void testCreateResponseWithInvalidJson() {
        String invalidJson = "invalid json content";
        MindsDbResponse response = new MindsDbResponse(200, invalidJson);

        assertTrue(response.isSuccess());
        assertEquals(invalidJson, response.getRawResponse());
        assertFalse(response.hasJsonData());
        assertNull(response.getJsonData());
        assertNull(response.getStringField("status"));
    }

    @Test
    @DisplayName("Should handle null response body")
    void testCreateResponseWithNullBody() {
        MindsDbResponse response = new MindsDbResponse(200, null);

        assertTrue(response.isSuccess());
        assertNull(response.getRawResponse());
        assertFalse(response.hasJsonData());
        assertNull(response.getJsonData());
    }

    @Test
    @DisplayName("Should handle empty response body")
    void testCreateResponseWithEmptyBody() {
        MindsDbResponse response = new MindsDbResponse(200, "");

        assertTrue(response.isSuccess());
        assertEquals("", response.getRawResponse());
        assertFalse(response.hasJsonData());
    }

    @Test
    @DisplayName("Should identify error status codes")
    void testErrorStatusCodes() {
        MindsDbResponse response404 = new MindsDbResponse(404, "{\"error\":\"Not found\"}");
        MindsDbResponse response500 = new MindsDbResponse(500, "{\"error\":\"Server error\"}");

        assertFalse(response404.isSuccess());
        assertFalse(response500.isSuccess());
        assertEquals(404, response404.getStatusCode());
        assertEquals(500, response500.getStatusCode());
    }

    @Test
    @DisplayName("Should handle custom messages")
    void testCustomMessages() {
        MindsDbResponse response = new MindsDbResponse(200, "{\"status\":\"ok\"}");
        
        assertNull(response.getMessage());
        
        response.setMessage("Custom success message");
        assertEquals("Custom success message", response.getMessage());
    }

    @Test
    @DisplayName("Should return default values for missing fields")
    void testMissingFields() {
        String jsonResponse = "{\"existing\":\"value\"}";
        MindsDbResponse response = new MindsDbResponse(200, jsonResponse);

        assertNull(response.getStringField("missing"));
        assertEquals(0, response.getIntField("missing"));
        assertEquals(0.0, response.getDoubleField("missing"), 0.001);
        assertFalse(response.getBooleanField("missing"));
        assertNull(response.getField("missing"));
    }

    @Test
    @DisplayName("Should handle nested JSON fields")
    void testNestedJsonFields() {
        String jsonResponse = "{\"data\":{\"nested\":\"value\"},\"array\":[1,2,3]}";
        MindsDbResponse response = new MindsDbResponse(200, jsonResponse);

        assertNotNull(response.getField("data"));
        assertNotNull(response.getField("array"));
        assertTrue(response.getField("data").isObject());
        assertTrue(response.getField("array").isArray());
    }

    @Test
    @DisplayName("Should provide meaningful toString representation")
    void testToString() {
        MindsDbResponse response = new MindsDbResponse(200, "{\"status\":\"success\"}");
        response.setMessage("Operation completed");

        String toString = response.toString();
        assertTrue(toString.contains("statusCode=200"));
        assertTrue(toString.contains("message='Operation completed'"));
        assertTrue(toString.contains("rawResponse="));
    }

    @Test
    @DisplayName("Should truncate long response in toString")
    void testToStringWithLongResponse() {
        StringBuilder longResponse = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            longResponse.append("a");
        }
        
        MindsDbResponse response = new MindsDbResponse(200, longResponse.toString());
        String toString = response.toString();
        
        assertTrue(toString.contains("..."));
        assertTrue(toString.length() < longResponse.length() + 100); // Should be truncated
    }
}