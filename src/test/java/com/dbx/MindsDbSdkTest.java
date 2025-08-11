package com.dbx;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MindsDbSdkTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private MindsDbSdk sdk;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        sdk = new MindsDbSdk("http://test.mindsdb.com/api", Duration.ofSeconds(10), httpClient, objectMapper);
    }

    @Test
    @DisplayName("Should list models successfully")
    void testListModels() throws Exception {
        String expectedResponse = "{\"models\":[{\"name\":\"model1\"},{\"name\":\"model2\"}]}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.listModels();
        
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getRawResponse());
        assertTrue(response.hasJsonData());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should list databases successfully")
    void testListDatabases() throws Exception {
        String expectedResponse = "{\"databases\":[{\"name\":\"db1\"},{\"name\":\"db2\"}]}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.listDatabases();
        
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getRawResponse());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should delete model successfully")
    void testDeleteModel() throws Exception {
        String modelName = "testModel";
        String expectedResponse = "{\"status\":\"success\"}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.deleteModel(modelName);
        
        assertTrue(response.isSuccess());
        assertEquals("Model 'testModel' deleted successfully", response.getMessage());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should throw exception for null model name")
    void testDeleteModelWithNullName() {
        assertThrows(MindsDbException.class, () -> sdk.deleteModel(null));
        assertThrows(MindsDbException.class, () -> sdk.deleteModel(""));
        assertThrows(MindsDbException.class, () -> sdk.deleteModel("   "));
    }

    @Test
    @DisplayName("Should get model details successfully")
    void testGetModelDetails() throws Exception {
        String modelName = "testModel";
        String expectedResponse = "{\"model\":{\"name\":\"testModel\",\"status\":\"complete\"}}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.getModelDetails(modelName);
        
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getRawResponse());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should retrain model successfully")
    void testRetrainModel() throws Exception {
        String modelName = "testModel";
        String databaseName = "testDB";
        String tableName = "testTable";
        String expectedResponse = "{\"status\":\"success\"}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.retrainModel(modelName, databaseName, tableName);
        
        assertTrue(response.isSuccess());
        assertEquals("Model 'testModel' retrained successfully", response.getMessage());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should get tables successfully")
    void testGetTables() throws Exception {
        String databaseName = "testDB";
        String expectedResponse = "{\"tables\":[{\"name\":\"table1\"},{\"name\":\"table2\"}]}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.getTables(databaseName);
        
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getRawResponse());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should get table schema successfully")
    void testGetTableSchema() throws Exception {
        String databaseName = "testDB";
        String tableName = "testTable";
        String expectedResponse = "{\"schema\":[{\"column\":\"id\",\"type\":\"int\"}]}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.getTableSchema(databaseName, tableName);
        
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getRawResponse());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should execute custom query successfully")
    void testExecuteCustomQuery() throws Exception {
        String sqlQuery = "SELECT * FROM testTable";
        String expectedResponse = "{\"data\":[{\"id\":1,\"name\":\"test\"}]}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.executeCustomQuery(sqlQuery);
        
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getRawResponse());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should make batch prediction successfully")
    void testMakeBatchPrediction() throws Exception {
        String modelName = "testModel";
        String targetColumn = "testColumn";
        
        Map<String, Object> record1 = new HashMap<>();
        record1.put("feature1", "value1");
        record1.put("feature2", 123);
        
        Map<String, Object> record2 = new HashMap<>();
        record2.put("feature1", "value2");
        record2.put("feature2", 456);
        
        List<Map<String, Object>> records = Arrays.asList(record1, record2);
        String expectedResponse = "{\"predictions\":[{\"testColumn\":\"result1\"},{\"testColumn\":\"result2\"}]}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.makeBatchPrediction(modelName, targetColumn, records);
        
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getRawResponse());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should throw exception for empty batch prediction records")
    void testMakeBatchPredictionWithEmptyRecords() {
        String modelName = "testModel";
        String targetColumn = "testColumn";
        List<Map<String, Object>> records = Arrays.asList();

        assertThrows(MindsDbException.class, () -> 
            sdk.makeBatchPrediction(modelName, targetColumn, records));
    }

    @Test
    @DisplayName("Should get model accuracy successfully")
    void testGetModelAccuracy() throws Exception {
        String modelName = "testModel";
        String expectedResponse = "{\"accuracy\":0.95,\"metrics\":{\"precision\":0.94,\"recall\":0.96}}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.getModelAccuracy(modelName);
        
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getRawResponse());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should disconnect database successfully")
    void testDisconnectDatabase() throws Exception {
        String databaseName = "testDB";
        String expectedResponse = "{\"status\":\"disconnected\"}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.disconnectDatabase(databaseName);
        
        assertTrue(response.isSuccess());
        assertEquals("Database 'testDB' disconnected successfully", response.getMessage());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should create view successfully")
    void testCreateView() throws Exception {
        String viewName = "testView";
        String sqlQuery = "SELECT * FROM testTable WHERE condition = 'value'";
        String expectedResponse = "{\"status\":\"success\"}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.createView(viewName, sqlQuery);
        
        assertTrue(response.isSuccess());
        assertEquals("View 'testView' created successfully", response.getMessage());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should drop view successfully")
    void testDropView() throws Exception {
        String viewName = "testView";
        String expectedResponse = "{\"status\":\"success\"}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.dropView(viewName);
        
        assertTrue(response.isSuccess());
        assertEquals("View 'testView' dropped successfully", response.getMessage());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should get model state successfully")
    void testGetModelState() throws Exception {
        String modelName = "testModel";
        String expectedResponse = "{\"status\":\"complete\",\"accuracy\":0.92}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.getModelState(modelName);
        
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getRawResponse());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should create and train model successfully")
    void testCreateAndTrainModel() throws Exception {
        String modelName = "testModel";
        String databaseName = "testDB";
        String tableName = "testTable";
        String columnToPredict = "testColumn";
        String expectedResponse = "{\"status\":\"training\"}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.createAndTrainModel(modelName, databaseName, tableName, columnToPredict);
        
        assertTrue(response.isSuccess());
        assertEquals("Model created and training started successfully", response.getMessage());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should connect database successfully")
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
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.connectDatabase(user, password, host, port, databaseName, engine, schema);
        
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getRawResponse());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should make prediction successfully")
    void testMakePrediction() throws Exception {
        String modelName = "testModel";
        String targetColumn = "testColumn";
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("condition1", "value1");
        conditions.put("condition2", 42);
        String expectedResponse = "{\"prediction\":\"value\"}";

        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        MindsDbResponse response = sdk.makePrediction(modelName, targetColumn, conditions);
        
        assertTrue(response.isSuccess());
        assertEquals(expectedResponse, response.getRawResponse());
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Should throw exception for null prediction conditions")
    void testMakePredictionWithNullConditions() {
        String modelName = "testModel";
        String targetColumn = "testColumn";

        assertThrows(MindsDbException.class, () -> 
            sdk.makePrediction(modelName, targetColumn, null));
        
        assertThrows(MindsDbException.class, () -> 
            sdk.makePrediction(modelName, targetColumn, new HashMap<>()));
    }

    @Test
    @DisplayName("Should handle HTTP error responses")
    void testHttpErrorResponse() throws Exception {
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(404);
        when(httpResponse.body()).thenReturn("{\"error\":\"Model not found\"}");

        MindsDbResponse response = sdk.listModels();
        
        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
        assertTrue(response.hasJsonData());
    }

    @Test
    @DisplayName("Should get configuration values")
    void testGetConfiguration() {
        assertEquals("http://test.mindsdb.com/api", sdk.getBaseUrl());
        assertEquals(Duration.ofSeconds(10), sdk.getTimeout());
    }
}