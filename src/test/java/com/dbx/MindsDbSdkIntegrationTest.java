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

    private DbxImplementation sdk;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        sdk = new DbxImplementation(httpClient, objectMapper);
    }

    @Test
    @DisplayName("Complete ML Workflow: Connect DB -> Create Model -> Train -> Predict")
    void testCompleteMLWorkflow() throws Exception {
        // Mock responses for each step
        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        
        // Step 1: Connect to database
        when(httpResponse.body()).thenReturn("{\"status\":\"connected\"}");
        String connectResult = sdk.connectDatabase("user", "pass", "localhost", "5432", "sales_db", "postgres", "public");
        assertNotNull(connectResult);
        
        // Step 2: Create and train model
        when(httpResponse.body()).thenReturn("{\"status\":\"training\"}");
        String createResult = sdk.createAndTrainModel("sales_predictor", "sales_db", "sales_data", "revenue");
        assertEquals("Model created and getting trained successfully", createResult);
        
        // Step 3: Check model state
        when(httpResponse.body()).thenReturn("{\"status\":\"complete\",\"accuracy\":0.92}");
        String stateResult = sdk.modelState("sales_predictor");
        assertNotNull(stateResult);
        
        // Step 4: Make prediction
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("product_category", "electronics");
        conditions.put("season", "holiday");
        when(httpResponse.body()).thenReturn("{\"revenue\":15000.50}");
        String predictionResult = sdk.makePredictionv2("sales_predictor", "revenue", conditions);
        assertNotNull(predictionResult);
        
        // Verify all interactions
        verify(httpClient, times(4)).send(any(), any());
    }

    @Test
    @DisplayName("Database Management Workflow: List -> Connect -> Get Tables -> Get Schema")
    void testDatabaseManagementWorkflow() throws Exception {
        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        
        // List existing databases
        when(httpResponse.body()).thenReturn("{\"databases\":[{\"name\":\"existing_db\"}]}");
        String listResult = sdk.listDatabases();
        assertNotNull(listResult);
        
        // Connect new database
        when(httpResponse.body()).thenReturn("{\"status\":\"connected\"}");
        String connectResult = sdk.connectDatabase("admin", "secret", "db.example.com", "3306", "new_db", "mysql", "main");
        assertNotNull(connectResult);
        
        // Get tables from database
        when(httpResponse.body()).thenReturn("{\"tables\":[{\"name\":\"customers\"},{\"name\":\"orders\"}]}");
        String tablesResult = sdk.getTables("new_db");
        assertNotNull(tablesResult);
        
        // Get schema for specific table
        when(httpResponse.body()).thenReturn("{\"columns\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"name\",\"type\":\"varchar\"}]}");
        String schemaResult = sdk.getTableSchema("new_db", "customers");
        assertNotNull(schemaResult);
        
        verify(httpClient, times(4)).send(any(), any());
    }

    @Test
    @DisplayName("Model Lifecycle Management: Create -> Train -> Evaluate -> Retrain -> Delete")
    void testModelLifecycleManagement() throws Exception {
        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        
        // Create model
        when(httpResponse.body()).thenReturn("{\"status\":\"created\"}");
        String createResult = sdk.createAndTrainModel("customer_churn", "crm_db", "customer_data", "will_churn");
        assertEquals("Model created and getting trained successfully", createResult);
        
        // Get model details
        when(httpResponse.body()).thenReturn("{\"name\":\"customer_churn\",\"status\":\"complete\",\"training_time\":\"2h 15m\"}");
        String detailsResult = sdk.getModelDetails("customer_churn");
        assertNotNull(detailsResult);
        
        // Get model accuracy
        when(httpResponse.body()).thenReturn("{\"accuracy\":0.87,\"precision\":0.85,\"recall\":0.89}");
        String accuracyResult = sdk.getModelAccuracy("customer_churn");
        assertNotNull(accuracyResult);
        
        // Retrain model with new data
        when(httpResponse.body()).thenReturn("{\"status\":\"retraining\"}");
        String retrainResult = sdk.retrainModel("customer_churn", "crm_db", "updated_customer_data");
        assertEquals("Model 'customer_churn' retrained successfully", retrainResult);
        
        // Delete model
        when(httpResponse.body()).thenReturn("{\"status\":\"deleted\"}");
        String deleteResult = sdk.deleteModel("customer_churn");
        assertEquals("Model 'customer_churn' deleted successfully", deleteResult);
        
        verify(httpClient, times(5)).send(any(), any());
    }

    @Test
    @DisplayName("Batch Prediction Workflow for Multiple Records")
    void testBatchPredictionWorkflow() throws Exception {
        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        
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
        
        String batchResult = sdk.makeBatchPrediction("customer_churn", "will_churn", customers);
        assertNotNull(batchResult);
        assertTrue(batchResult.contains("predictions"));
        
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("View Management: Create -> Use -> Drop")
    void testViewManagement() throws Exception {
        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        
        // Create view
        when(httpResponse.body()).thenReturn("{\"status\":\"created\"}");
        String createViewResult = sdk.createView("high_value_customers", 
            "SELECT * FROM customers WHERE total_spent > 10000");
        assertEquals("View 'high_value_customers' created successfully", createViewResult);
        
        // Use view in custom query
        when(httpResponse.body()).thenReturn("{\"data\":[{\"customer_id\":1,\"name\":\"John Doe\",\"total_spent\":15000}]}");
        String queryResult = sdk.executeCustomQuery("SELECT * FROM mindsdb.high_value_customers LIMIT 10");
        assertNotNull(queryResult);
        
        // Drop view
        when(httpResponse.body()).thenReturn("{\"status\":\"dropped\"}");
        String dropViewResult = sdk.dropView("high_value_customers");
        assertEquals("View 'high_value_customers' dropped successfully", dropViewResult);
        
        verify(httpClient, times(3)).send(any(), any());
    }

    @Test
    @DisplayName("Error Handling: Invalid Model Name")
    void testErrorHandlingInvalidModel() throws Exception {
        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        when(httpResponse.body()).thenReturn("{\"error\":\"Model not found\",\"code\":404}");
        
        String result = sdk.modelState("non_existent_model");
        assertTrue(result.contains("error") || result.contains("not found"));
        
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Custom Query Execution with Complex SQL")
    void testCustomQueryExecution() throws Exception {
        when(httpClient.send(any(), any())).thenReturn((HttpResponse) httpResponse);
        
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
        
        String result = sdk.executeCustomQuery(complexQuery);
        assertNotNull(result);
        assertTrue(result.contains("Premium Widget"));
        
        verify(httpClient, times(1)).send(any(), any());
    }
}