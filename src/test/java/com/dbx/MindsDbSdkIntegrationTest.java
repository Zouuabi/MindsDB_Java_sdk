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

/**
 * Integration tests for MindsDB SDK functionality.
 * These tests simulate real-world usage scenarios.
 */
class MindsDbSdkIntegrationTest {

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
        sdk = new MindsDbSdk("http://test.mindsdb.com/api", Duration.ofSeconds(30), httpClient, objectMapper);
    }

    @Test
    @DisplayName("Complete ML Workflow: Connect DB -> Create Model -> Train -> Predict")
    void testCompleteMLWorkflow() throws Exception {
        // Mock responses for each step
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        
        // Step 1: Connect to database
        when(httpResponse.body()).thenReturn("{\"status\":\"connected\"}");
        MindsDbResponse connectResult = sdk.connectDatabase("user", "pass", "localhost", "5432", "sales_db", "postgres", "public");
        assertTrue(connectResult.isSuccess());
        
        // Step 2: Create and train model
        when(httpResponse.body()).thenReturn("{\"status\":\"training\"}");
        MindsDbResponse createResult = sdk.createAndTrainModel("sales_predictor", "sales_db", "sales_data", "revenue");
        assertTrue(createResult.isSuccess());
        assertEquals("Model created and training started successfully", createResult.getMessage());
        
        // Step 3: Check model state
        when(httpResponse.body()).thenReturn("{\"status\":\"complete\",\"accuracy\":0.92}");
        MindsDbResponse stateResult = sdk.getModelState("sales_predictor");
        assertTrue(stateResult.isSuccess());
        assertEquals("complete", stateResult.getStringField("status"));
        assertEquals(0.92, stateResult.getDoubleField("accuracy"), 0.001);
        
        // Step 4: Make prediction
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("product_category", "electronics");
        conditions.put("season", "holiday");
        when(httpResponse.body()).thenReturn("{\"revenue\":15000.50}");
        MindsDbResponse predictionResult = sdk.makePrediction("sales_predictor", "revenue", conditions);
        assertTrue(predictionResult.isSuccess());
        assertEquals(15000.50, predictionResult.getDoubleField("revenue"), 0.01);
        
        // Verify all interactions
        verify(httpClient, times(4)).send(any(), any());
    }

    @Test
    @DisplayName("Database Management Workflow: List -> Connect -> Get Tables -> Get Schema")
    void testDatabaseManagementWorkflow() throws Exception {
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        
        // List existing databases
        when(httpResponse.body()).thenReturn("{\"databases\":[{\"name\":\"existing_db\"}]}");
        MindsDbResponse listResult = sdk.listDatabases();
        assertTrue(listResult.isSuccess());
        
        // Connect new database
        when(httpResponse.body()).thenReturn("{\"status\":\"connected\"}");
        MindsDbResponse connectResult = sdk.connectDatabase("admin", "secret", "db.example.com", "3306", "new_db", "mysql", "main");
        assertTrue(connectResult.isSuccess());
        
        // Get tables from database
        when(httpResponse.body()).thenReturn("{\"tables\":[{\"name\":\"customers\"},{\"name\":\"orders\"}]}");
        MindsDbResponse tablesResult = sdk.getTables("new_db");
        assertTrue(tablesResult.isSuccess());
        
        // Get schema for specific table
        when(httpResponse.body()).thenReturn("{\"columns\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"name\",\"type\":\"varchar\"}]}");
        MindsDbResponse schemaResult = sdk.getTableSchema("new_db", "customers");
        assertTrue(schemaResult.isSuccess());
        
        verify(httpClient, times(4)).send(any(), any());
    }

    @Test
    @DisplayName("Model Lifecycle Management: Create -> Train -> Evaluate -> Retrain -> Delete")
    void testModelLifecycleManagement() throws Exception {
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        
        // Create model
        when(httpResponse.body()).thenReturn("{\"status\":\"created\"}");
        MindsDbResponse createResult = sdk.createAndTrainModel("customer_churn", "crm_db", "customer_data", "will_churn");
        assertTrue(createResult.isSuccess());
        assertEquals("Model created and training started successfully", createResult.getMessage());
        
        // Get model details
        when(httpResponse.body()).thenReturn("{\"name\":\"customer_churn\",\"status\":\"complete\",\"training_time\":\"2h 15m\"}");
        MindsDbResponse detailsResult = sdk.getModelDetails("customer_churn");
        assertTrue(detailsResult.isSuccess());
        assertEquals("customer_churn", detailsResult.getStringField("name"));
        
        // Get model accuracy
        when(httpResponse.body()).thenReturn("{\"accuracy\":0.87,\"precision\":0.85,\"recall\":0.89}");
        MindsDbResponse accuracyResult = sdk.getModelAccuracy("customer_churn");
        assertTrue(accuracyResult.isSuccess());
        assertEquals(0.87, accuracyResult.getDoubleField("accuracy"), 0.001);
        
        // Retrain model with new data
        when(httpResponse.body()).thenReturn("{\"status\":\"retraining\"}");
        MindsDbResponse retrainResult = sdk.retrainModel("customer_churn", "crm_db", "updated_customer_data");
        assertTrue(retrainResult.isSuccess());
        assertEquals("Model 'customer_churn' retrained successfully", retrainResult.getMessage());
        
        // Delete model
        when(httpResponse.body()).thenReturn("{\"status\":\"deleted\"}");
        MindsDbResponse deleteResult = sdk.deleteModel("customer_churn");
        assertTrue(deleteResult.isSuccess());
        assertEquals("Model 'customer_churn' deleted successfully", deleteResult.getMessage());
        
        verify(httpClient, times(5)).send(any(), any());
    }

    @Test
    @DisplayName("Batch Prediction Workflow for Multiple Records")
    void testBatchPredictionWorkflow() throws Exception {
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        
        // Prepare batch data
        Map<String, Object> customer1 = new HashMap<>();
        customer1.put("age", 25);
        customer1.put("income", 50000);
        customer1.put("tenure", 12);
        
        Map<String, Object> customer2 = new HashMap<>();
        customer2.put("age", 45);
        customer2.put("income", 75000);
        customer2.put("tenure", 36);
        
        Map<String, Object> customer3 = new HashMap<>();
        customer3.put("age", 35);
        customer3.put("income", 60000);
        customer3.put("tenure", 24);
        
        List<Map<String, Object>> customers = Arrays.asList(customer1, customer2, customer3);
        
        // Mock batch prediction response
        when(httpResponse.body()).thenReturn(
            "{\"predictions\":[" +
            "{\"will_churn\":\"no\",\"confidence\":0.85}," +
            "{\"will_churn\":\"yes\",\"confidence\":0.72}," +
            "{\"will_churn\":\"no\",\"confidence\":0.91}" +
            "]}"
        );
        
        MindsDbResponse batchResult = sdk.makeBatchPrediction("customer_churn", "will_churn", customers);
        assertTrue(batchResult.isSuccess());
        assertTrue(batchResult.getRawResponse().contains("predictions"));
        
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("View Management: Create -> Use -> Drop")
    void testViewManagement() throws Exception {
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        
        // Create view
        when(httpResponse.body()).thenReturn("{\"status\":\"created\"}");
        MindsDbResponse createViewResult = sdk.createView("high_value_customers", 
            "SELECT * FROM customers WHERE total_spent > 10000");
        assertTrue(createViewResult.isSuccess());
        assertEquals("View 'high_value_customers' created successfully", createViewResult.getMessage());
        
        // Use view in custom query
        when(httpResponse.body()).thenReturn("{\"data\":[{\"customer_id\":1,\"name\":\"John Doe\",\"total_spent\":15000}]}");
        MindsDbResponse queryResult = sdk.executeCustomQuery("SELECT * FROM mindsdb.high_value_customers LIMIT 10");
        assertTrue(queryResult.isSuccess());
        
        // Drop view
        when(httpResponse.body()).thenReturn("{\"status\":\"dropped\"}");
        MindsDbResponse dropViewResult = sdk.dropView("high_value_customers");
        assertTrue(dropViewResult.isSuccess());
        assertEquals("View 'high_value_customers' dropped successfully", dropViewResult.getMessage());
        
        verify(httpClient, times(3)).send(any(), any());
    }

    @Test
    @DisplayName("Error Handling: Invalid Model Name")
    void testErrorHandlingInvalidModel() throws Exception {
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(404);
        when(httpResponse.body()).thenReturn("{\"error\":\"Model not found\",\"code\":404}");
        
        MindsDbResponse result = sdk.getModelState("non_existent_model");
        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertTrue(result.getRawResponse().contains("error"));
        
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Custom Query Execution with Complex SQL")
    void testCustomQueryExecution() throws Exception {
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        
        String complexQuery = """
            SELECT 
                p.product_name,
                AVG(s.revenue) as avg_revenue,
                COUNT(*) as sales_count
            FROM sales_db.sales s
            JOIN sales_db.products p ON s.product_id = p.id
            WHERE s.sale_date >= '2024-01-01'
            GROUP BY p.product_name
            ORDER BY avg_revenue DESC
            LIMIT 10
            """;
        
        when(httpResponse.body()).thenReturn(
            "{\"data\":[" +
            "{\"product_name\":\"Premium Widget\",\"avg_revenue\":1250.50,\"sales_count\":45}," +
            "{\"product_name\":\"Standard Widget\",\"avg_revenue\":850.25,\"sales_count\":78}" +
            "]}"
        );
        
        MindsDbResponse result = sdk.executeCustomQuery(complexQuery);
        assertTrue(result.isSuccess());
        assertTrue(result.getRawResponse().contains("Premium Widget"));
        
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("SDK Builder Integration Test")
    void testSdkBuilderIntegration() throws Exception {
        // Create SDK using builder
        MindsDbSdk customSdk = MindsDbSdkBuilder.create()
            .baseUrl("http://custom.mindsdb.com/api")
            .timeoutSeconds(60)
            .build();

        assertEquals("http://custom.mindsdb.com/api", customSdk.getBaseUrl());
        assertEquals(Duration.ofSeconds(60), customSdk.getTimeout());
    }

    @Test
    @DisplayName("Exception Handling Integration Test")
    void testExceptionHandlingIntegration() throws Exception {
        // Test parameter validation
        assertThrows(MindsDbException.class, () -> sdk.deleteModel(null));
        assertThrows(MindsDbException.class, () -> sdk.deleteModel(""));
        assertThrows(MindsDbException.class, () -> sdk.makePrediction("model", "target", null));
        assertThrows(MindsDbException.class, () -> sdk.makePrediction("model", "target", new HashMap<>()));
        
        // Test batch prediction with empty records
        assertThrows(MindsDbException.class, () -> 
            sdk.makeBatchPrediction("model", "target", Arrays.asList()));
    }
}