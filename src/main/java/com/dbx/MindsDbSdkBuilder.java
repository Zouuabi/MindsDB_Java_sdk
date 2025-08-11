package com.dbx;

import java.time.Duration;

/**
 * Builder class for creating MindsDbSdk instances with custom configuration.
 * 
 * This builder provides a fluent API for configuring the SDK with various options
 * such as custom server URLs, timeouts, and other settings.
 */
public class MindsDbSdkBuilder {
    
    private String baseUrl = "http://127.0.0.1:47334/api";
    private Duration timeout = Duration.ofSeconds(30);

    /**
     * Sets the base URL for the MindsDB server.
     *
     * @param baseUrl the base URL (e.g., "http://localhost:47334/api")
     * @return this builder instance
     */
    public MindsDbSdkBuilder baseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Base URL cannot be null or empty");
        }
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * Sets the request timeout duration.
     *
     * @param timeout the timeout duration
     * @return this builder instance
     */
    public MindsDbSdkBuilder timeout(Duration timeout) {
        if (timeout == null || timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException("Timeout must be positive");
        }
        this.timeout = timeout;
        return this;
    }

    /**
     * Sets the request timeout in seconds.
     *
     * @param timeoutSeconds the timeout in seconds
     * @return this builder instance
     */
    public MindsDbSdkBuilder timeoutSeconds(long timeoutSeconds) {
        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException("Timeout seconds must be positive");
        }
        this.timeout = Duration.ofSeconds(timeoutSeconds);
        return this;
    }

    /**
     * Sets the request timeout in milliseconds.
     *
     * @param timeoutMillis the timeout in milliseconds
     * @return this builder instance
     */
    public MindsDbSdkBuilder timeoutMillis(long timeoutMillis) {
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException("Timeout milliseconds must be positive");
        }
        this.timeout = Duration.ofMillis(timeoutMillis);
        return this;
    }

    /**
     * Builds and returns a new MindsDbSdk instance with the configured settings.
     *
     * @return a new MindsDbSdk instance
     */
    public MindsDbSdk build() {
        return new MindsDbSdk(baseUrl, timeout);
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new MindsDbSdkBuilder
     */
    public static MindsDbSdkBuilder create() {
        return new MindsDbSdkBuilder();
    }
}