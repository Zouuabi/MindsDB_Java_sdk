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

public class DbxImplementation {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public DbxImplementation() {

        this.httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();
        this.objectMapper = new ObjectMapper();
    }

    public String modelState(String modelName) throws Exception {
        String url = "http://127.0.0.1:47334/api/projects/mindsdb/models/" + modelName + "/describe";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("attribute", "info");

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .method("GET", HttpRequest.BodyPublishers.ofString(jsonBody))
            .timeout(Duration.ofSeconds(20))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String createAndTrainModel(String modelName, String databaseName, String tableName, String columnToPredict) throws Exception {
        String url = "http://127.0.0.1:47334/api/projects/mindsdb/models";

        // Create the SQL query with proper escaping
        String sqlQuery = String.format(
            "CREATE MODEL mindsdb.%s FROM %s (SELECT * FROM %s) PREDICT %s;",
            modelName, databaseName, tableName, columnToPredict
        );

        // Use ObjectNode for better control over JSON structure
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("query", sqlQuery);
        requestBody.put("using", objectMapper.createObjectNode());  // Add empty using object if needed

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .timeout(Duration.ofSeconds(20))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return "Model created and Getting trained successfully";
    }

    public String connectDatabase(String user, String password, String host, String port, String databasename, String engine, String schema) throws Exception {
        String url = "http://127.0.0.1:47334/api/databases";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("password", password);
        parameters.put("host", host);
        parameters.put("port", port);
        parameters.put("database", databasename);
        parameters.put("schema", schema);

        Map<String, Object> databaseConfig = new HashMap<>();
        databaseConfig.put("name", databasename);
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
            .timeout(Duration.ofSeconds(20))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

public String makePredictionv2(String modelName, String targetColumn, Map<String, Object> conditions) throws Exception {
    Map<String, Object> requestBody = new HashMap<>();
    
    // Build the WHERE clause from conditions
    StringBuilder whereClause = new StringBuilder();
    for (Map.Entry<String, Object> condition : conditions.entrySet()) {
        if (whereClause.length() > 0) {
            whereClause.append(" AND ");
        }
        whereClause.append(condition.getKey())
                  .append("="); // Start the condition

        // Check if the value is a String or a Number
        Object value = condition.getValue();
        if (value instanceof Number) {
            whereClause.append(value); // No quotes for numbers
        } else {
            whereClause.append("'").append(value).append("'"); // Add quotes for strings
        }
    }
    
    // Construct the query
    String query = String.format(
        "SELECT %s FROM mindsdb.%s WHERE %s;",
        targetColumn,
        modelName,
        whereClause.toString()
    );
    
    requestBody.put("query", query);
    String jsonBody = objectMapper.writeValueAsString(requestBody);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://127.0.0.1:47334/api/sql/query"))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .timeout(Duration.ofSeconds(20))
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
}
   public String makePrediction(String modelName, String targetColumn, Map<String, String> conditions) throws Exception {
    Map<String, Object> requestBody = new HashMap<>();
    

    StringBuilder whereClause = new StringBuilder();
    for (Map.Entry<String, String> condition : conditions.entrySet()) {
        if (whereClause.length() > 0) {
            whereClause.append(" AND ");
        }
        whereClause.append(condition.getKey())
                  .append("=")
                  .append(condition.getValue());
    }
    
 
    String query = String.format(
        "SELECT %s FROM mindsdb.%s WHERE %s;",
        targetColumn,
        modelName,
        whereClause.toString()
    );
    
    requestBody.put("query", query);
    String jsonBody = objectMapper.writeValueAsString(requestBody);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://127.0.0.1:47334/api/sql/query"))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .timeout(Duration.ofSeconds(10))
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
}
}
