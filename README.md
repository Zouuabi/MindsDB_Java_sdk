# MindsDB Java SDK

A comprehensive Java SDK for interacting with the MindsDB API. This SDK provides a complete set of functionalities for machine learning model management, database operations, and predictions.

## Features

### 🤖 Model Management
- **Create and Train Models**: Create new ML models and train them on your data
- **List Models**: Get all available models in your MindsDB project
- **Model Details**: Retrieve detailed information about specific models
- **Model State**: Check the current state and training status of models
- **Model Accuracy**: Get performance metrics and accuracy information
- **Retrain Models**: Update existing models with new data
- **Delete Models**: Remove models that are no longer needed

### 🗄️ Database Operations
- **Connect Databases**: Connect to various database engines (PostgreSQL, MySQL, etc.)
- **List Databases**: View all connected databases
- **Disconnect Databases**: Remove database connections
- **Get Tables**: List all tables in a specific database
- **Table Schema**: Get detailed schema information for tables

### 🔮 Prediction Capabilities
- **Single Predictions**: Make individual predictions using trained models
- **Batch Predictions**: Process multiple records at once for efficient bulk predictions
- **Custom Queries**: Execute custom SQL queries for advanced use cases

### 📊 View Management
- **Create Views**: Create SQL views for complex data transformations
- **Drop Views**: Remove views when no longer needed

## Quick Start

### Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.dbx</groupId>
    <artifactId>mindsdb-java-sdk</artifactId>
    <version>2.0</version>
</dependency>
```

### Basic Usage

```java
import com.dbx.MindsDbSdk;
import com.dbx.MindsDbSdkBuilder;
import com.dbx.MindsDbResponse;
import com.dbx.MindsDbException;

// Create SDK instance with default configuration
MindsDbSdk sdk = new MindsDbSdk();

// Or use builder for custom configuration
MindsDbSdk customSdk = MindsDbSdkBuilder.create()
    .baseUrl("http://your-mindsdb-server.com/api")
    .timeoutSeconds(60)
    .build();
```

## Usage Examples

### Connecting to a Database

```java
try {
    MindsDbResponse response = sdk.connectDatabase(
        "username", 
        "password", 
        "localhost", 
        "5432", 
        "sales_db", 
        "postgres", 
        "public"
    );
    
    if (response.isSuccess()) {
        System.out.println("Database connected successfully!");
    }
} catch (MindsDbException e) {
    System.err.println("Failed to connect: " + e.getMessage());
}
```

### Creating and Training a Model

```java
try {
    MindsDbResponse response = sdk.createAndTrainModel(
        "churn_predictor",
        "sales_db",
        "customer_data",
        "will_churn"
    );
    
    System.out.println("Model creation status: " + response.getMessage());
} catch (MindsDbException e) {
    System.err.println("Model creation failed: " + e.getMessage());
}
```

### Making Predictions

#### Single Prediction
```java
Map<String, Object> conditions = new HashMap<>();
conditions.put("age", 35);
conditions.put("income", 50000);
conditions.put("tenure_months", 24);

try {
    MindsDbResponse prediction = sdk.makePrediction(
        "churn_predictor",
        "will_churn",
        conditions
    );
    
    if (prediction.isSuccess()) {
        System.out.println("Prediction: " + prediction.getRawResponse());
    }
} catch (MindsDbException e) {
    System.err.println("Prediction failed: " + e.getMessage());
}
```

#### Batch Predictions
```java
List<Map<String, Object>> records = Arrays.asList(
    Map.of("age", 25, "income", 45000, "tenure_months", 12),
    Map.of("age", 45, "income", 75000, "tenure_months", 36),
    Map.of("age", 35, "income", 60000, "tenure_months", 24)
);

try {
    MindsDbResponse batchResults = sdk.makeBatchPrediction(
        "churn_predictor",
        "will_churn",
        records
    );
    
    System.out.println("Batch predictions: " + batchResults.getRawResponse());
} catch (MindsDbException e) {
    System.err.println("Batch prediction failed: " + e.getMessage());
}
```

### Working with Models

```java
// List all models
MindsDbResponse models = sdk.listModels();
System.out.println("Available models: " + models.getRawResponse());

// Get model details
MindsDbResponse details = sdk.getModelDetails("churn_predictor");
System.out.println("Model status: " + details.getStringField("status"));

// Check model accuracy
MindsDbResponse accuracy = sdk.getModelAccuracy("churn_predictor");
System.out.println("Model accuracy: " + accuracy.getDoubleField("accuracy"));

// Retrain model
MindsDbResponse retrain = sdk.retrainModel("churn_predictor", "sales_db", "new_customer_data");
System.out.println(retrain.getMessage());
```

### Database Operations

```java
// List databases
MindsDbResponse databases = sdk.listDatabases();

// Get tables from a database
MindsDbResponse tables = sdk.getTables("sales_db");

// Get table schema
MindsDbResponse schema = sdk.getTableSchema("sales_db", "customers");

// Execute custom query
MindsDbResponse queryResult = sdk.executeCustomQuery(
    "SELECT COUNT(*) as total_customers FROM sales_db.customers"
);
```

### View Management

```java
// Create a view
MindsDbResponse createView = sdk.createView(
    "high_value_customers",
    "SELECT * FROM customers WHERE total_spent > 10000"
);

// Use the view
MindsDbResponse viewData = sdk.executeCustomQuery(
    "SELECT * FROM mindsdb.high_value_customers LIMIT 10"
);

// Drop the view
MindsDbResponse dropView = sdk.dropView("high_value_customers");
```

## Response Handling

The SDK returns `MindsDbResponse` objects that provide convenient methods to access response data:

```java
MindsDbResponse response = sdk.listModels();

// Check if request was successful
if (response.isSuccess()) {
    // Get raw JSON response
    String rawJson = response.getRawResponse();
    
    // Access specific fields
    String status = response.getStringField("status");
    int count = response.getIntField("count");
    double accuracy = response.getDoubleField("accuracy");
    boolean active = response.getBooleanField("active");
    
    // Check if response has JSON data
    if (response.hasJsonData()) {
        JsonNode jsonData = response.getJsonData();
        // Work with Jackson JsonNode
    }
}
```

## Error Handling

The SDK uses `MindsDbException` for error handling:

```java
try {
    MindsDbResponse response = sdk.createAndTrainModel("model", "db", "table", "target");
} catch (MindsDbException e) {
    System.err.println("Error: " + e.getMessage());
    
    // Check if it's an HTTP error
    if (e.hasHttpDetails()) {
        System.err.println("HTTP Status: " + e.getStatusCode());
        System.err.println("Response: " + e.getResponseBody());
    }
    
    // Check for underlying cause
    if (e.getCause() != null) {
        System.err.println("Caused by: " + e.getCause().getMessage());
    }
}
```

## Configuration

### Using Builder Pattern

```java
MindsDbSdk sdk = MindsDbSdkBuilder.create()
    .baseUrl("http://your-mindsdb-server.com/api")
    .timeoutSeconds(60)
    .build();
```

### Constructor Options

```java
// Default configuration (localhost:47334, 30s timeout)
MindsDbSdk sdk = new MindsDbSdk();

// Custom server URL
MindsDbSdk sdk = new MindsDbSdk("http://your-server.com/api");

// Custom URL and timeout
MindsDbSdk sdk = new MindsDbSdk("http://your-server.com/api", Duration.ofSeconds(60));
```

## Prerequisites

- Java 8 or higher
- MindsDB instance running and accessible
- Network connectivity to MindsDB server

## Dependencies

- Jackson for JSON processing
- SLF4J for logging
- Java HTTP Client (built-in since Java 11)

## Testing

The SDK includes comprehensive unit tests and integration tests:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MindsDbSdkTest

# Run integration tests
mvn test -Dtest=MindsDbSdkIntegrationTest
```

## Logging

The SDK uses SLF4J for logging. Configure your logging framework (Logback, Log4j, etc.) to control log output:

```xml
<!-- logback.xml example -->
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="com.dbx" level="DEBUG"/>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:
- Create an issue on GitHub
- Check the MindsDB documentation
- Review the test cases for usage examples