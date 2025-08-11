package com.dbx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MindsDB Java SDK - A comprehensive client library for interacting with MindsDB API.
 * 
 * This SDK provides a complete set of methods for:
 * - Model management (create, train, delete, retrain)
 * - Database operations (connect, disconnect, query)
 * - Predictions (single and batch)
 * - View management
 * - Custom query execution
 * 
 * @author MindsDB SDK Team
 * @version 2.0
 */
public class MindsDbSdk {
    private static final Logger logger = LoggerFactory.getLogger(MindsDbSdk.class);
    
    private final String baseUrl;
    private final Duration timeout;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new MindsDB SDK instance with default configuration.
     * Uses localhost:47334 as the default MindsDB server.
     */
    public MindsDbSdk() {
        this("http://127.0.0.1:47334/api", Duration.ofSeconds(30));
    }

    /**
     * Creates a new MindsDB SDK instance with custom server URL.
     *
     * @param baseUrl the base URL of the MindsDB server (e.g., "http://localhost:47334/api")
     */
    public MindsDbSdk(String baseUrl) {
        this(baseUrl, Duration.ofSeconds(30));
    }

    /**
     * Creates a new MindsDB SDK instance with custom configuration.
     *
     * @param baseUrl the base URL of the MindsDB server
     * @param timeout the request timeout duration
     */
    public MindsDbSdk(String baseUrl, Duration timeout) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.timeout = timeout;
        this.httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(timeout)
            .build();
        this.objectMapper = new ObjectMapper();
        
        logger.info("MindsDB SDK initialized with base URL: {}", this.baseUrl);
    }

    /**
     * Constructor for testing purposes.
     *
     * @param baseUrl the base URL of the MindsDB server
     * @param timeout the request timeout duration
     * @param httpClient the HttpClient to use
     * @param objectMapper the ObjectMapper to use
     */
    MindsDbSdk(String baseUrl, Duration timeout, HttpClient httpClient, ObjectMapper objectMapper) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.timeout = timeout;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Lists all available models in the MindsDB project.
     *
     * @return MindsDbResponse containing list of models
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse listModels() throws MindsDbException {
        logger.debug("Listing all models");
        String url = String.format("%s/projects/mindsdb/models", baseUrl);
        return executeGetRequest(url);
    }

    /**
     * Lists all connected databases.
     *
     * @return MindsDbResponse containing list of databases
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse listDatabases() throws MindsDbException {
        logger.debug("Listing all databases");
        String url = String.format("%s/databases", baseUrl);
        return executeGetRequest(url);
    }

    /**
     * Deletes a specified model.
     *
     * @param modelName the name of the model to delete
     * @return MindsDbResponse with operation result
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse deleteModel(String modelName) throws MindsDbException {
        logger.debug("Deleting model: {}", modelName);
        validateParameter(modelName, "modelName");
        
        String query = String.format("DROP MODEL mindsdb.%s;", modelName);
        MindsDbResponse response = executeQuery(query);
        response.setMessage(String.format("Model '%s' deleted successfully", modelName));
        return response;
    }

    /**
     * Gets detailed information about a specific model including training status.
     *
     * @param modelName the name of the model
     * @return MindsDbResponse with detailed model information
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse getModelDetails(String modelName) throws MindsDbException {
        logger.debug("Getting details for model: {}", modelName);
        validateParameter(modelName, "modelName");
        
        String url = String.format("%s/projects/mindsdb/models/%s", baseUrl, modelName);
        return executeGetRequest(url);
    }

    /**
     * Retrains an existing model with new data.
     *
     * @param modelName the name of the model to retrain
     * @param databaseName the name of the database
     * @param tableName the name of the table
     * @return MindsDbResponse with operation result
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse retrainModel(String modelName, String databaseName, String tableName) throws MindsDbException {
        logger.debug("Retraining model: {} with data from {}.{}", modelName, databaseName, tableName);
        validateParameter(modelName, "modelName");
        validateParameter(databaseName, "databaseName");
        validateParameter(tableName, "tableName");
        
        String query = String.format(
            "RETRAIN mindsdb.%s FROM %s (SELECT * FROM %s);",
            modelName, databaseName, tableName
        );
        MindsDbResponse response = executeQuery(query);
        response.setMessage(String.format("Model '%s' retrained successfully", modelName));
        return response;
    }

    /**
     * Gets tables from a specific database.
     *
     * @param databaseName the name of the database
     * @return MindsDbResponse containing list of tables
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse getTables(String databaseName) throws MindsDbException {
        logger.debug("Getting tables from database: {}", databaseName);
        validateParameter(databaseName, "databaseName");
        
        String query = String.format("SHOW TABLES FROM %s;", databaseName);
        return executeQuery(query);
    }

    /**
     * Gets schema information for a specific table.
     *
     * @param databaseName the name of the database
     * @param tableName the name of the table
     * @return MindsDbResponse containing table schema
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse getTableSchema(String databaseName, String tableName) throws MindsDbException {
        logger.debug("Getting schema for table: {}.{}", databaseName, tableName);
        validateParameter(databaseName, "databaseName");
        validateParameter(tableName, "tableName");
        
        String query = String.format("DESCRIBE %s.%s;", databaseName, tableName);
        return executeQuery(query);
    }

    /**
     * Executes a custom SQL query.
     *
     * @param sqlQuery the SQL query to execute
     * @return MindsDbResponse with query results
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse executeCustomQuery(String sqlQuery) throws MindsDbException {
        logger.debug("Executing custom query: {}", sqlQuery);
        validateParameter(sqlQuery, "sqlQuery");
        
        return executeQuery(sqlQuery);
    }

    /**
     * Makes batch predictions for multiple records.
     *
     * @param modelName the name of the model
     * @param targetColumn the target column to predict
     * @param records list of condition maps for batch prediction
     * @return MindsDbResponse with batch prediction results
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse makeBatchPrediction(String modelName, String targetColumn, List<Map<String, Object>> records) throws MindsDbException {
        logger.debug("Making batch prediction with model: {} for {} records", modelName, records.size());
        validateParameter(modelName, "modelName");
        validateParameter(targetColumn, "targetColumn");
        
        if (records == null || records.isEmpty()) {
            throw new MindsDbException("Records list cannot be null or empty");
        }
        
        StringBuilder unionQuery = new StringBuilder();
        
        for (int i = 0; i < records.size(); i++) {
            if (i > 0) {
                unionQuery.append(" UNION ALL ");
            }
            
            StringBuilder selectClause = new StringBuilder("SELECT ");
            Map<String, Object> record = records.get(i);
            
            boolean first = true;
            for (Map.Entry<String, Object> entry : record.entrySet()) {
                if (!first) {
                    selectClause.append(", ");
                }
                Object value = entry.getValue();
                if (value instanceof Number) {
                    selectClause.append(value).append(" AS ").append(entry.getKey());
                } else {
                    selectClause.append("'").append(value).append("' AS ").append(entry.getKey());
                }
                first = false;
            }
            
            unionQuery.append(selectClause);
        }
        
        String query = String.format(
            "SELECT %s FROM mindsdb.%s WHERE (%s) IN (%s);",
            targetColumn,
            modelName,
            String.join(", ", records.get(0).keySet()),
            unionQuery.toString()
        );
        
        return executeQuery(query);
    }

    /**
     * Gets model training accuracy and performance metrics.
     *
     * @param modelName the name of the model
     * @return MindsDbResponse with model accuracy metrics
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse getModelAccuracy(String modelName) throws MindsDbException {
        logger.debug("Getting accuracy for model: {}", modelName);
        validateParameter(modelName, "modelName");
        
        String url = String.format("%s/projects/mindsdb/models/%s/describe", baseUrl, modelName);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("attribute", "accuracy");
        
        return executePostRequest(url, requestBody);
    }

    /**
     * Disconnects from a specified database.
     *
     * @param databaseName the name of the database to disconnect
     * @return MindsDbResponse with operation result
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse disconnectDatabase(String databaseName) throws MindsDbException {
        logger.debug("Disconnecting database: {}", databaseName);
        validateParameter(databaseName, "databaseName");
        
        String url = String.format("%s/databases/%s", baseUrl, databaseName);
        MindsDbResponse response = executeDeleteRequest(url);
        response.setMessage(String.format("Database '%s' disconnected successfully", databaseName));
        return response;
    }

    /**
     * Creates a view in MindsDB.
     *
     * @param viewName the name of the view
     * @param sqlQuery the SQL query for the view
     * @return MindsDbResponse with operation result
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse createView(String viewName, String sqlQuery) throws MindsDbException {
        logger.debug("Creating view: {}", viewName);
        validateParameter(viewName, "viewName");
        validateParameter(sqlQuery, "sqlQuery");
        
        String query = String.format("CREATE VIEW mindsdb.%s AS (%s);", viewName, sqlQuery);
        MindsDbResponse response = executeQuery(query);
        response.setMessage(String.format("View '%s' created successfully", viewName));
        return response;
    }

    /**
     * Drops a view from MindsDB.
     *
     * @param viewName the name of the view to drop
     * @return MindsDbResponse with operation result
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse dropView(String viewName) throws MindsDbException {
        logger.debug("Dropping view: {}", viewName);
        validateParameter(viewName, "viewName");
        
        String query = String.format("DROP VIEW mindsdb.%s;", viewName);
        MindsDbResponse response = executeQuery(query);
        response.setMessage(String.format("View '%s' dropped successfully", viewName));
        return response;
    }

    /**
     * Retrieves the state of a specified model.
     *
     * @param modelName the name of the model
     * @return MindsDbResponse with model state information
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse getModelState(String modelName) throws MindsDbException {
        logger.debug("Getting state for model: {}", modelName);
        validateParameter(modelName, "modelName");
        
        String url = String.format("%s/projects/mindsdb/models/%s/describe", baseUrl, modelName);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("attribute", "info");
        
        return executePostRequest(url, requestBody);
    }

    /**
     * Creates and trains a new model.
     *
     * @param modelName the name of the model
     * @param databaseName the name of the database
     * @param tableName the name of the table
     * @param columnToPredict the column to predict
     * @return MindsDbResponse with operation result
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse createAndTrainModel(String modelName, String databaseName, String tableName, String columnToPredict) throws MindsDbException {
        logger.debug("Creating and training model: {} to predict {} from {}.{}", modelName, columnToPredict, databaseName, tableName);
        validateParameter(modelName, "modelName");
        validateParameter(databaseName, "databaseName");
        validateParameter(tableName, "tableName");
        validateParameter(columnToPredict, "columnToPredict");
        
        String url = String.format("%s/projects/mindsdb/models", baseUrl);

        String sqlQuery = String.format(
            "CREATE MODEL mindsdb.%s FROM %s (SELECT * FROM %s) PREDICT %s;",
            modelName, databaseName, tableName, columnToPredict
        );

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("query", sqlQuery);
        requestBody.put("using", objectMapper.createObjectNode());

        MindsDbResponse response = executePostRequest(url, requestBody);
        response.setMessage("Model created and training started successfully");
        return response;
    }

    /**
     * Connects to a specified database.
     *
     * @param user the database user
     * @param password the database password
     * @param host the database host
     * @param port the database port
     * @param databaseName the name of the database
     * @param engine the database engine
     * @param schema the database schema
     * @return MindsDbResponse with connection result
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse connectDatabase(String user, String password, String host, String port, String databaseName, String engine, String schema) throws MindsDbException {
        logger.debug("Connecting to database: {} on {}:{}", databaseName, host, port);
        validateParameter(user, "user");
        validateParameter(password, "password");
        validateParameter(host, "host");
        validateParameter(port, "port");
        validateParameter(databaseName, "databaseName");
        validateParameter(engine, "engine");
        
        String url = String.format("%s/databases", baseUrl);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("password", password);
        parameters.put("host", host);
        parameters.put("port", port);
        parameters.put("database", databaseName);
        if (schema != null && !schema.trim().isEmpty()) {
            parameters.put("schema", schema);
        }

        Map<String, Object> databaseConfig = new HashMap<>();
        databaseConfig.put("name", databaseName);
        databaseConfig.put("engine", engine);
        databaseConfig.put("parameters", parameters);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("database", databaseConfig);

        return executePostRequest(url, requestBody);
    }

    /**
     * Makes a prediction using the specified model and conditions.
     *
     * @param modelName the name of the model
     * @param targetColumn the target column to predict
     * @param conditions the conditions for the prediction
     * @return MindsDbResponse with prediction result
     * @throws MindsDbException if an error occurs during the request
     */
    public MindsDbResponse makePrediction(String modelName, String targetColumn, Map<String, Object> conditions) throws MindsDbException {
        logger.debug("Making prediction with model: {} for target: {}", modelName, targetColumn);
        validateParameter(modelName, "modelName");
        validateParameter(targetColumn, "targetColumn");
        
        if (conditions == null || conditions.isEmpty()) {
            throw new MindsDbException("Conditions cannot be null or empty");
        }
        
        String query = buildQuery(modelName, targetColumn, conditions);
        return executeQuery(query);
    }

    private String buildQuery(String modelName, String targetColumn, Map<String, Object> conditions) {
        StringBuilder whereClause = new StringBuilder();
        for (Map.Entry<String, Object> condition : conditions.entrySet()) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(condition.getKey()).append("=");

            Object value = condition.getValue();
            if (value instanceof Number) {
                whereClause.append(value);
            } else {
                whereClause.append("'").append(value).append("'");
            }
        }

        return String.format(
            "SELECT %s FROM mindsdb.%s WHERE %s;",
            targetColumn,
            modelName,
            whereClause.toString()
        );
    }

    private MindsDbResponse executeQuery(String query) throws MindsDbException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        
        String url = String.format("%s/sql/query", baseUrl);
        return executePostRequest(url, requestBody);
    }

    private MindsDbResponse executeGetRequest(String url) throws MindsDbException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .timeout(timeout)
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new MindsDbResponse(response.statusCode(), response.body());
            
        } catch (Exception e) {
            logger.error("Error executing GET request to {}: {}", url, e.getMessage());
            throw new MindsDbException("Failed to execute GET request: " + e.getMessage(), e);
        }
    }

    private MindsDbResponse executePostRequest(String url, Object requestBody) throws MindsDbException {
        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(timeout)
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new MindsDbResponse(response.statusCode(), response.body());
            
        } catch (Exception e) {
            logger.error("Error executing POST request to {}: {}", url, e.getMessage());
            throw new MindsDbException("Failed to execute POST request: " + e.getMessage(), e);
        }
    }

    private MindsDbResponse executeDeleteRequest(String url) throws MindsDbException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .DELETE()
                .timeout(timeout)
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new MindsDbResponse(response.statusCode(), response.body());
            
        } catch (Exception e) {
            logger.error("Error executing DELETE request to {}: {}", url, e.getMessage());
            throw new MindsDbException("Failed to execute DELETE request: " + e.getMessage(), e);
        }
    }

    private void validateParameter(String parameter, String parameterName) throws MindsDbException {
        if (parameter == null || parameter.trim().isEmpty()) {
            throw new MindsDbException(parameterName + " cannot be null or empty");
        }
    }

    /**
     * Gets the base URL of the MindsDB server.
     *
     * @return the base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Gets the configured timeout duration.
     *
     * @return the timeout duration
     */
    public Duration getTimeout() {
        return timeout;
    }
}