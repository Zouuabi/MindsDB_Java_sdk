package com.dbx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents a response from the MindsDB API.
 * 
 * This class encapsulates the HTTP response including status code, raw response body,
 * and provides convenient methods to access the response data.
 */
public class MindsDbResponse {
    private final int statusCode;
    private final String rawResponse;
    private String message;
    private JsonNode jsonData;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new MindsDbResponse.
     *
     * @param statusCode the HTTP status code
     * @param rawResponse the raw response body
     */
    public MindsDbResponse(int statusCode, String rawResponse) {
        this.statusCode = statusCode;
        this.rawResponse = rawResponse;
        this.objectMapper = new ObjectMapper();
        
        // Try to parse JSON response
        try {
            if (rawResponse != null && !rawResponse.trim().isEmpty()) {
                this.jsonData = objectMapper.readTree(rawResponse);
            }
        } catch (Exception e) {
            // If JSON parsing fails, jsonData will remain null
            this.jsonData = null;
        }
    }

    /**
     * Gets the HTTP status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the raw response body as a string.
     *
     * @return the raw response
     */
    public String getRawResponse() {
        return rawResponse;
    }

    /**
     * Gets the parsed JSON data from the response.
     *
     * @return the JSON data, or null if parsing failed
     */
    public JsonNode getJsonData() {
        return jsonData;
    }

    /**
     * Gets a custom message set by the SDK.
     *
     * @return the message, or null if not set
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets a custom message for this response.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Checks if the response indicates success (status code 2xx).
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Checks if the response contains valid JSON data.
     *
     * @return true if JSON data is available, false otherwise
     */
    public boolean hasJsonData() {
        return jsonData != null;
    }

    /**
     * Gets a specific field from the JSON response.
     *
     * @param fieldName the name of the field
     * @return the JsonNode for the field, or null if not found
     */
    public JsonNode getField(String fieldName) {
        if (jsonData != null) {
            return jsonData.get(fieldName);
        }
        return null;
    }

    /**
     * Gets a string value from the JSON response.
     *
     * @param fieldName the name of the field
     * @return the string value, or null if not found
     */
    public String getStringField(String fieldName) {
        JsonNode field = getField(fieldName);
        return field != null ? field.asText() : null;
    }

    /**
     * Gets an integer value from the JSON response.
     *
     * @param fieldName the name of the field
     * @return the integer value, or 0 if not found
     */
    public int getIntField(String fieldName) {
        JsonNode field = getField(fieldName);
        return field != null ? field.asInt() : 0;
    }

    /**
     * Gets a double value from the JSON response.
     *
     * @param fieldName the name of the field
     * @return the double value, or 0.0 if not found
     */
    public double getDoubleField(String fieldName) {
        JsonNode field = getField(fieldName);
        return field != null ? field.asDouble() : 0.0;
    }

    /**
     * Gets a boolean value from the JSON response.
     *
     * @param fieldName the name of the field
     * @return the boolean value, or false if not found
     */
    public boolean getBooleanField(String fieldName) {
        JsonNode field = getField(fieldName);
        return field != null ? field.asBoolean() : false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MindsDbResponse{");
        sb.append("statusCode=").append(statusCode);
        if (message != null) {
            sb.append(", message='").append(message).append('\'');
        }
        if (rawResponse != null) {
            sb.append(", rawResponse='").append(rawResponse.length() > 100 ? 
                rawResponse.substring(0, 100) + "..." : rawResponse).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }
}