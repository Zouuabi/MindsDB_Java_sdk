package com.dbx;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;

class MindsDbSdkBuilderTest {

    @Test
    @DisplayName("Should create builder with default values")
    void testCreateBuilderWithDefaults() {
        MindsDbSdk sdk = MindsDbSdkBuilder.create().build();

        assertEquals("http://127.0.0.1:47334/api", sdk.getBaseUrl());
        assertEquals(Duration.ofSeconds(30), sdk.getTimeout());
    }

    @Test
    @DisplayName("Should set custom base URL")
    void testSetCustomBaseUrl() {
        String customUrl = "http://custom.mindsdb.com/api";
        MindsDbSdk sdk = MindsDbSdkBuilder.create()
            .baseUrl(customUrl)
            .build();

        assertEquals(customUrl, sdk.getBaseUrl());
    }

    @Test
    @DisplayName("Should throw exception for null base URL")
    void testNullBaseUrl() {
        assertThrows(IllegalArgumentException.class, () -> 
            MindsDbSdkBuilder.create().baseUrl(null));
    }

    @Test
    @DisplayName("Should throw exception for empty base URL")
    void testEmptyBaseUrl() {
        assertThrows(IllegalArgumentException.class, () -> 
            MindsDbSdkBuilder.create().baseUrl(""));
        
        assertThrows(IllegalArgumentException.class, () -> 
            MindsDbSdkBuilder.create().baseUrl("   "));
    }

    @Test
    @DisplayName("Should set custom timeout duration")
    void testSetCustomTimeout() {
        Duration customTimeout = Duration.ofSeconds(60);
        MindsDbSdk sdk = MindsDbSdkBuilder.create()
            .timeout(customTimeout)
            .build();

        assertEquals(customTimeout, sdk.getTimeout());
    }

    @Test
    @DisplayName("Should set timeout in seconds")
    void testSetTimeoutSeconds() {
        long timeoutSeconds = 45;
        MindsDbSdk sdk = MindsDbSdkBuilder.create()
            .timeoutSeconds(timeoutSeconds)
            .build();

        assertEquals(Duration.ofSeconds(timeoutSeconds), sdk.getTimeout());
    }

    @Test
    @DisplayName("Should set timeout in milliseconds")
    void testSetTimeoutMillis() {
        long timeoutMillis = 15000;
        MindsDbSdk sdk = MindsDbSdkBuilder.create()
            .timeoutMillis(timeoutMillis)
            .build();

        assertEquals(Duration.ofMillis(timeoutMillis), sdk.getTimeout());
    }

    @Test
    @DisplayName("Should throw exception for null timeout")
    void testNullTimeout() {
        assertThrows(IllegalArgumentException.class, () -> 
            MindsDbSdkBuilder.create().timeout(null));
    }

    @Test
    @DisplayName("Should throw exception for negative timeout")
    void testNegativeTimeout() {
        assertThrows(IllegalArgumentException.class, () -> 
            MindsDbSdkBuilder.create().timeout(Duration.ofSeconds(-1)));
    }

    @Test
    @DisplayName("Should throw exception for zero timeout")
    void testZeroTimeout() {
        assertThrows(IllegalArgumentException.class, () -> 
            MindsDbSdkBuilder.create().timeout(Duration.ZERO));
    }

    @Test
    @DisplayName("Should throw exception for negative timeout seconds")
    void testNegativeTimeoutSeconds() {
        assertThrows(IllegalArgumentException.class, () -> 
            MindsDbSdkBuilder.create().timeoutSeconds(-1));
        
        assertThrows(IllegalArgumentException.class, () -> 
            MindsDbSdkBuilder.create().timeoutSeconds(0));
    }

    @Test
    @DisplayName("Should throw exception for negative timeout milliseconds")
    void testNegativeTimeoutMillis() {
        assertThrows(IllegalArgumentException.class, () -> 
            MindsDbSdkBuilder.create().timeoutMillis(-1));
        
        assertThrows(IllegalArgumentException.class, () -> 
            MindsDbSdkBuilder.create().timeoutMillis(0));
    }

    @Test
    @DisplayName("Should chain builder methods")
    void testBuilderChaining() {
        String customUrl = "http://test.mindsdb.com/api";
        Duration customTimeout = Duration.ofSeconds(120);
        
        MindsDbSdk sdk = MindsDbSdkBuilder.create()
            .baseUrl(customUrl)
            .timeout(customTimeout)
            .build();

        assertEquals(customUrl, sdk.getBaseUrl());
        assertEquals(customTimeout, sdk.getTimeout());
    }

    @Test
    @DisplayName("Should create multiple independent builders")
    void testMultipleBuilders() {
        MindsDbSdk sdk1 = MindsDbSdkBuilder.create()
            .baseUrl("http://server1.com/api")
            .timeoutSeconds(30)
            .build();

        MindsDbSdk sdk2 = MindsDbSdkBuilder.create()
            .baseUrl("http://server2.com/api")
            .timeoutSeconds(60)
            .build();

        assertEquals("http://server1.com/api", sdk1.getBaseUrl());
        assertEquals("http://server2.com/api", sdk2.getBaseUrl());
        assertEquals(Duration.ofSeconds(30), sdk1.getTimeout());
        assertEquals(Duration.ofSeconds(60), sdk2.getTimeout());
    }
}