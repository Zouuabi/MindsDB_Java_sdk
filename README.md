# Prediction Project

This project provides a SOAP-based service for interacting with the MindsDB API to create and train models, connect to databases, and make predictions.

## Prerequisites

- Java 8 or higher
- Maven 3.6.0 or higher

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

## Running the Service

To run the SOAP service, use the following command:
```sh
mvn jetty:run
```

The service will be available at `http://localhost:8080/soaproject/services`.

## WSDL

The WSDL for the service can be accessed at:
```
http://localhost:8080/soaproject/services/YourServiceName?wsdl
```

## Usage

### Model State

Retrieve the state of a specified model:
```java
DbxImplementation dbx = new DbxImplementation();
String modelState = dbx.modelState("yourModelName");
System.out.println(modelState);
```

### Create and Train Model

Create and train a new model:
```java
DbxImplementation dbx = new DbxImplementation();
String response = dbx.createAndTrainModel("yourModelName", "yourDatabaseName", "yourTableName", "yourColumnToPredict");
System.out.println(response);
```

### Connect Database

Connect to a specified database:
```java
DbxImplementation dbx = new DbxImplementation();
String response = dbx.connectDatabase("user", "password", "host", "port", "databaseName", "engine", "schema");
System.out.println(response);
```

### Make Prediction

Make a prediction using the specified model and conditions:
```java
DbxImplementation dbx = new DbxImplementation();
Map<String, String> conditions = new HashMap<>();
conditions.put("condition1", "value1");
String prediction = dbx.makePrediction("yourModelName", "yourTargetColumn", conditions);
System.out.println(prediction);
```

### Make Prediction (Version 2)

Make a prediction using the specified model and conditions (version 2):
```java
DbxImplementation dbx = new DbxImplementation();
Map<String, Object> conditions = new HashMap<>();
conditions.put("condition1", "value1");
String prediction = dbx.makePredictionv2("yourModelName", "yourTargetColumn", conditions);
System.out.println(prediction);
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
