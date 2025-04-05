# Project description

A simple URL shortener that allows users to convert long links into shorter, more manageable ones. The service supports tracking, expiration dates and URL management.

# Run/debug code in IntelliJ
* Install Java 21
* Build the code
    * IntelliJ will build it automatically
    * If you want to build it from command line and also run unit tests, run: ```./gradlew build```
* Create an IntelliJ run configuration for a Jar application
    * Add in the configuration the JAR path to the build folder `./build/libs/hello-0.0.1-SNAPSHOT.jar`
* Start the MongoDB container using docker compose
    * ```docker-compose up -d mongo```
* Run/debug your IntelliJ run configuration

# Running the project

* You can access the API endpoints at:
    * http://localhost:8080/
* You can access the MongoDB Admin UI at:
  * http://localhost:8090/

# Running the tests

* Add a JUnit test configuration in IntelliJ
