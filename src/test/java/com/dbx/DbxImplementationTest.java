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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    void testListModels() throws Exception {
        String expectedResponse = "{\"models\":[{\"name\":\"model1\"},{\"name\":\"model2\"}]}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.listModels();
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testListDatabases() throws Exception {
        String expectedResponse = "{\"databases\":[{\"name\":\"db1\"},{\"name\":\"db2\"}]}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.listDatabases();
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testDeleteModel() throws Exception {
        String modelName = "testModel";
        String expectedResponse = "{\"status\":\"success\"}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.deleteModel(modelName);
        assertEquals("Model 'testModel' deleted successfully", response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testGetModelDetails() throws Exception {
        String modelName = "testModel";
        String expectedResponse = "{\"model\":{\"name\":\"testModel\",\"status\":\"complete\"}}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.getModelDetails(modelName);
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testRetrainModel() throws Exception {
        String modelName = "testModel";
        String databaseName = "testDB";
        String tableName = "testTable";
        String expectedResponse = "{\"status\":\"success\"}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.retrainModel(modelName, databaseName, tableName);
        assertEquals("Model 'testModel' retrained successfully", response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testGetTables() throws Exception {
        String databaseName = "testDB";
        String expectedResponse = "{\"tables\":[{\"name\":\"table1\"},{\"name\":\"table2\"}]}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.getTables(databaseName);
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testGetTableSchema() throws Exception {
        String databaseName = "testDB";
        String tableName = "testTable";
        String expectedResponse = "{\"schema\":[{\"column\":\"id\",\"type\":\"int\"}]}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.getTableSchema(databaseName, tableName);
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testExecuteCustomQuery() throws Exception {
        String sqlQuery = "SELECT * FROM testTable";
        String expectedResponse = "{\"data\":[{\"id\":1,\"name\":\"test\"}]}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.executeCustomQuery(sqlQuery);
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
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

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.makeBatchPrediction(modelName, targetColumn, records);
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testGetModelAccuracy() throws Exception {
        String modelName = "testModel";
        String expectedResponse = "{\"accuracy\":0.95,\"metrics\":{\"precision\":0.94,\"recall\":0.96}}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.getModelAccuracy(modelName);
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testDisconnectDatabase() throws Exception {
        String databaseName = "testDB";
        String expectedResponse = "{\"status\":\"disconnected\"}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.disconnectDatabase(databaseName);
        assertEquals("Database 'testDB' disconnected successfully", response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testCreateView() throws Exception {
        String viewName = "testView";
        String sqlQuery = "SELECT * FROM testTable WHERE condition = 'value'";
        String expectedResponse = "{\"status\":\"success\"}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.createView(viewName, sqlQuery);
        assertEquals("View 'testView' created successfully", response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testDropView() throws Exception {
        String viewName = "testView";
        String expectedResponse = "{\"status\":\"success\"}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.dropView(viewName);
        assertEquals("View 'testView' dropped successfully", response);
        verify(httpClient, times(1)).send(any(), any());
    }
    @Test
    void testModelState() throws Exception {
        String modelName = "testModel";
        String expectedResponse = "{\"status\":\"success\"}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.modelState(modelName);
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testCreateAndTrainModel() throws Exception {
        String modelName = "testModel";
        String databaseName = "testDB";
        String tableName = "testTable";
        String columnToPredict = "testColumn";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);

        String response = dbxImplementation.createAndTrainModel(modelName, databaseName, tableName, columnToPredict);
        assertEquals("Model created and getting trained successfully", response);
        verify(httpClient, times(1)).send(any(), any());
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

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.connectDatabase(user, password, host, port, databaseName, engine, schema);
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testMakePrediction() throws Exception {
        String modelName = "testModel";
        String targetColumn = "testColumn";
        Map<String, String> conditions = new HashMap<>();
        conditions.put("condition1", "value1");
        String expectedResponse = "{\"prediction\":\"value\"}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.makePrediction(modelName, targetColumn, conditions);
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testMakePredictionv2() throws Exception {
        String modelName = "testModel";
        String targetColumn = "testColumn";
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("condition1", "value1");
        String expectedResponse = "{\"prediction\":\"value\"}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.makePredictionv2(modelName, targetColumn, conditions);
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    void testMakeBatchPredictionWithEmptyRecords() throws Exception {
        String modelName = "testModel";
        String targetColumn = "testColumn";
        List<Map<String, Object>> records = Arrays.asList();

        assertThrows(IndexOutOfBoundsException.class, () -> {
            dbxImplementation.makeBatchPrediction(modelName, targetColumn, records);
        });
    }

    @Test
    void testMakeBatchPredictionWithMixedDataTypes() throws Exception {
        String modelName = "testModel";
        String targetColumn = "testColumn";
        
        Map<String, Object> record = new HashMap<>();
        record.put("stringFeature", "testValue");
        record.put("intFeature", 42);
        record.put("doubleFeature", 3.14);
        record.put("booleanFeature", true);
        
        List<Map<String, Object>> records = Arrays.asList(record);
        String expectedResponse = "{\"predictions\":[{\"testColumn\":\"result\"}]}";

        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String response = dbxImplementation.makeBatchPrediction(modelName, targetColumn, records);
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(), any());
    }
}
