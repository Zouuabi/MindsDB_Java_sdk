package com.dbx;

/**
 * Exception thrown when an error occurs while interacting with the MindsDB API.
 * 
 * This exception provides detailed information about what went wrong during
 * API operations, including the original cause if available.
 */
public class MindsDbException extends Exception {
    
    private final int statusCode;
    private final String responseBody;

    /**
     * Creates a new MindsDbException with a message.
     *
     * @param message the error message
     */
    public MindsDbException(String message) {
        super(message);
        this.statusCode = -1;
        this.responseBody = null;
    }

    /**
     * Creates a new MindsDbException with a message and cause.
     *
     * @param message the error message
     * @param cause the underlying cause
     */
    public MindsDbException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.responseBody = null;
    }

    /**
     * Creates a new MindsDbException with HTTP details.
     *
     * @param message the error message
     * @param statusCode the HTTP status code
     * @param responseBody the response body
     */
    public MindsDbException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Creates a new MindsDbException with HTTP details and cause.
     *
     * @param message the error message
     * @param statusCode the HTTP status code
     * @param responseBody the response body
     * @param cause the underlying cause
     */
    public MindsDbException(String message, int statusCode, String responseBody, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Gets the HTTP status code associated with this exception.
     *
     * @return the status code, or -1 if not available
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the response body associated with this exception.
     *
     * @return the response body, or null if not available
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * Checks if this exception has HTTP details.
     *
     * @return true if status code and response body are available
     */
    public boolean hasHttpDetails() {
        return statusCode != -1 && responseBody != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MindsDbException: ").append(getMessage());
        
        if (hasHttpDetails()) {
            sb.append(" [HTTP ").append(statusCode).append("]");
            if (responseBody != null && !responseBody.trim().isEmpty()) {
                sb.append(" Response: ").append(responseBody.length() > 200 ? 
                    responseBody.substring(0, 200) + "..." : responseBody);
            }
        }
        
        if (getCause() != null) {
            sb.append(" Caused by: ").append(getCause().getMessage());
        }
        
        return sb.toString();
    }
}