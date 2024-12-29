package com.dbx;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

class DbxImplementationTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private DbxImplementation dbxImplementation;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        dbxImplementation = new DbxImplementation(httpClient, objectMapper);
    }

    @Test
    void testModelState() throws Exception {
        String modelName = "testModel";
        String expectedResponse = "{\"status\":\"success\"}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.modelState(modelName);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testCreateAndTrainModel() throws Exception {
        String modelName = "testModel";
        String databaseName = "testDB";
        String tableName = "testTable";
        String columnToPredict = "testColumn";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);

        String response = dbxImplementation.createAndTrainModel(modelName, databaseName, tableName, columnToPredict);
        assertEquals("Model created and getting trained successfully", response);
    }

    @Test
    void testConnectDatabase() throws Exception {
        String user = "testUser";
        String password = "testPassword";
        String host = "localhost";
        String port = "5432";
        String databaseName = "testDB";
        String engine = "postgres";
        String schema = "public";
        String expectedResponse = "{\"status\":\"connected\"}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.connectDatabase(user, password, host, port, databaseName, engine, schema);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testMakePrediction() throws Exception {
        String modelName = "testModel";
        String targetColumn = "testColumn";
        Map<String, String> conditions = new HashMap<>();
        conditions.put("condition1", "value1");
        String expectedResponse = "{\"prediction\":\"value\"}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.makePrediction(modelName, targetColumn, conditions);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testMakePredictionv2() throws Exception {
        String modelName = "testModel";
        String targetColumn = "testColumn";
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("condition1", "value1");
        String expectedResponse = "{\"prediction\":\"value\"}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.makePredictionv2(modelName, targetColumn, conditions);
        assertEquals(expectedResponse, response);
    }
}
