package com.dbx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation class for interacting with the MindsDB API.
 */
public class DbxImplementation {
    private static final String BASE_URL = "http://127.0.0.1:47334/api";
    private static final Duration TIMEOUT = Duration.ofSeconds(20);
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public DbxImplementation() {
        this.httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(TIMEOUT)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Constructor for testing purposes.
     *
     * @param httpClient the HttpClient to use
     * @param objectMapper the ObjectMapper to use
     */
    public DbxImplementation(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves the state of a specified model.
     *
     * @param modelName the name of the model
     * @return the model state as a JSON string
     * @throws Exception if an error occurs during the request
     */
    public String modelState(String modelName) throws Exception {
        String url = String.format("%s/projects/mindsdb/models/%s/describe", BASE_URL, modelName);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("attribute", "info");

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .method("GET", HttpRequest.BodyPublishers.ofString(jsonBody))
            .timeout(TIMEOUT)
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     * Creates and trains a new model.
     *
     * @param modelName the name of the model
     * @param databaseName the name of the database
     * @param tableName the name of the table
     * @param columnToPredict the column to predict
     * @return a success message
     * @throws Exception if an error occurs during the request
     */
    public String createAndTrainModel(String modelName, String databaseName, String tableName, String columnToPredict) throws Exception {
        String url = String.format("%s/projects/mindsdb/models", BASE_URL);

        String sqlQuery = String.format(
            "CREATE MODEL mindsdb.%s FROM %s (SELECT * FROM %s) PREDICT %s;",
            modelName, databaseName, tableName, columnToPredict
        );

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("query", sqlQuery);
        requestBody.put("using", objectMapper.createObjectNode());

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .timeout(TIMEOUT)
            .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return "Model created and getting trained successfully";
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
     * @return the response body as a JSON string
     * @throws Exception if an error occurs during the request
     */
    public String connectDatabase(String user, String password, String host, String port, String databaseName, String engine, String schema) throws Exception {
        String url = String.format("%s/databases", BASE_URL);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("password", password);
        parameters.put("host", host);
        parameters.put("port", port);
        parameters.put("database", databaseName);
        parameters.put("schema", schema);

        Map<String, Object> databaseConfig = new HashMap<>();
        databaseConfig.put("name", databaseName);
        databaseConfig.put("engine", engine);
        databaseConfig.put("parameters", parameters);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("database", databaseConfig);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .timeout(TIMEOUT)
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     * Makes a prediction using the specified model and conditions.
     *
     * @param modelName the name of the model
     * @param targetColumn the target column to predict
     * @param conditions the conditions for the prediction
     * @return the prediction result as a JSON string
     * @throws Exception if an error occurs during the request
     */
    public String makePrediction(String modelName, String targetColumn, Map<String, String> conditions) throws Exception {
        String query = buildQuery(modelName, targetColumn, conditions);
        return executeQuery(query);
    }

    /**
     * Makes a prediction using the specified model and conditions (version 2).
     *
     * @param modelName the name of the model
     * @param targetColumn the target column to predict
     * @param conditions the conditions for the prediction
     * @return the prediction result as a JSON string
     * @throws Exception if an error occurs during the request
     */
    public String makePredictionv2(String modelName, String targetColumn, Map<String, Object> conditions) throws Exception {
        String query = buildQueryV2(modelName, targetColumn, conditions);
        return executeQuery(query);
    }

    private String buildQuery(String modelName, String targetColumn, Map<String, String> conditions) {
        StringBuilder whereClause = new StringBuilder();
        for (Map.Entry<String, String> condition : conditions.entrySet()) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(condition.getKey())
                      .append("=")
                      .append(condition.getValue());
        }

        return String.format(
            "SELECT %s FROM mindsdb.%s WHERE %s;",
            targetColumn,
            modelName,
            whereClause.toString()
        );
    }

    private String buildQueryV2(String modelName, String targetColumn, Map<String, Object> conditions) {
        StringBuilder whereClause = new StringBuilder();
        for (Map.Entry<String, Object> condition : conditions.entrySet()) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(condition.getKey())
                      .append("=");

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

    private String executeQuery(String query) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(String.format("%s/sql/query", BASE_URL)))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .timeout(TIMEOUT)
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
