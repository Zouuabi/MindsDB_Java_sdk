# Prediction Project

# MindsDB SDK

This project provides a comprehensive SOAP-based SDK for interacting with the MindsDB API. It offers a complete set of functionalities for machine learning model management, database operations, and predictions.

## Features

### Model Management
- **Create and Train Models**: Create new ML models and train them on your data
- **List Models**: Get all available models in your MindsDB project
- **Model Details**: Retrieve detailed information about specific models
- **Model State**: Check the current state and training status of models
- **Model Accuracy**: Get performance metrics and accuracy information
- **Retrain Models**: Update existing models with new data
- **Delete Models**: Remove models that are no longer needed

### Database Operations
- **Connect Databases**: Connect to various database engines (PostgreSQL, MySQL, etc.)
- **List Databases**: View all connected databases
- **Disconnect Databases**: Remove database connections
- **Get Tables**: List all tables in a specific database
- **Table Schema**: Get detailed schema information for tables

### Prediction Capabilities
- **Single Predictions**: Make individual predictions using trained models
- **Batch Predictions**: Process multiple records at once for efficient bulk predictions
- **Custom Queries**: Execute custom SQL queries for advanced use cases

### View Management
- **Create Views**: Create SQL views for complex data transformations
- **Drop Views**: Remove views when no longer needed

## Available Operations

The SDK exposes the following SOAP operations:

1. `listModels()` - Get all available models
2. `listDatabases()` - Get all connected databases
3. `deleteModel(modelName)` - Delete a specific model
4. `getModelDetails(modelName)` - Get detailed model information
5. `retrainModel(modelName, databaseName, tableName)` - Retrain an existing model
6. `getTables(databaseName)` - Get tables from a database
7. `getTableSchema(databaseName, tableName)` - Get table schema
8. `executeCustomQuery(sqlQuery)` - Execute custom SQL queries
9. `makeBatchPrediction(modelName, targetColumn, records)` - Batch predictions
10. `getModelAccuracy(modelName)` - Get model performance metrics
11. `disconnectDatabase(databaseName)` - Disconnect from a database
12. `createView(viewName, sqlQuery)` - Create a SQL view
13. `dropView(viewName)` - Drop a SQL view
14. `makePrediction(modelName, targetColumn, conditions)` - Single prediction
15. `connectDatabase(user, password, host, port, databaseName, engine, schema)` - Connect to database
16. `modelState(modelName)` - Get model state
17. `createAndTrainModel(modelName, databaseName, tableName, columnToPredict)` - Create and train model
18. `makePredictionv2(modelName, targetColumn, conditions)` - Enhanced prediction method

## Prerequisites

- Java 8 or higher
- Maven 3.6.0 or higher
- MindsDB instance running (default: http://127.0.0.1:47334)

## Setup

1. Clone the repository:

   ```sh
   git clone https://github.com/yourusername/soaproject.git
   cd soaproject
   ```
2. Build the project using Maven:

   ```sh
   mvn clean install
   ```

3. Deploy the WAR file to your application server (Tomcat, etc.)

4. Access the WSDL at: `http://your-server:port/Prediction/services/dbx?wsdl`

## Usage Examples

### Connecting to a Database
```java
// Connect to PostgreSQL database
String result = sdk.connectDatabase(
    "username", 
    "password", 
    "localhost", 
    "5432", 
    "sales_db", 
    "postgres", 
    "public"
);
```

### Creating and Training a Model
```java
// Create a model to predict customer churn
String result = sdk.createAndTrainModel(
    "churn_predictor",
    "sales_db",
    "customer_data",
    "will_churn"
);
```

### Making Predictions
```java
// Single prediction
Map<String, Object> conditions = new HashMap<>();
conditions.put("age", 35);
conditions.put("income", 50000);
conditions.put("tenure_months", 24);

String prediction = sdk.makePredictionv2(
    "churn_predictor",
    "will_churn",
    conditions
);
```

### Batch Predictions
```java
// Prepare multiple records
List<Map<String, Object>> records = Arrays.asList(
    Map.of("age", 25, "income", 45000, "tenure_months", 12),
    Map.of("age", 45, "income", 75000, "tenure_months", 36),
    Map.of("age", 35, "income", 60000, "tenure_months", 24)
);

String batchResults = sdk.makeBatchPrediction(
    "churn_predictor",
    "will_churn",
    records
);
```

## Testing

The project includes comprehensive unit tests and integration tests:

```sh
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=DbxImplementationTest

# Run integration tests
mvn test -Dtest=MindsDbSdkIntegrationTest
```

## Architecture

The SDK is built using:
- **Apache Axis2**: For SOAP web service implementation
- **Jackson**: For JSON processing
- **Java HTTP Client**: For REST API communication with MindsDB
- **JUnit 5 & Mockito**: For comprehensive testing

## Error Handling

The SDK includes robust error handling for:
- Network connectivity issues
- Invalid model names or database connections
- Malformed queries
- Authentication failures
- Timeout scenarios

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.