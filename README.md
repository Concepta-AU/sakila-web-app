Sakila Sample Web App
=====================

This project aims to create a sample web application that can be used to test web application test frameworks easily,
in particular: our own [playwright-kotlin](https://github.com/Concepta-AU/playwright-kotlin). The goal is to have a
single Docker image that starts a fully functional web application with enough complexity to make it useful for 
validating a test framework in terms of correctness and design.

It is based on the [Sakila sample database](https://dev.mysql.com/doc/sakila/en/) in its 
[PostgreSQL version](https://github.com/devrimgunduz/pagila).

This is a Kotlin Multiplatform project targeting Web, Server.

* [/server](./server/src/main/kotlin) is for the Ktor server application.

* [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

* [/webApp](./webApp) contains web React application. It uses the Kotlin/JS library produced
  by the [shared](./shared) module.

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

1. Install [Node.js](https://nodejs.org/en/download) (which includes `npm`)
2. Build Kotlin/JS shared code:
    - on macOS/Linux
      ```shell
      ./gradlew :shared:jsBrowserDevelopmentLibraryDistribution
      ```
    - on Windows
      ```shell
      .\gradlew.bat :shared:jsBrowserDevelopmentLibraryDistribution
      ```
3. Build and run the web application
   ```shell
   npm install
   npm run start
   ```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
