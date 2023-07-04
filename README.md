# Temzone - Android

[![Version](https://img.shields.io/badge/version-0.1.0-orange)](https://github.com/Temtem-Interactive-Map/Temzone-Android)
[![Build](https://img.shields.io/github/actions/workflow/status/Temtem-Interactive-Map/Temzone-Android/main.yml?branch=main)](https://github.com/Temtem-Interactive-Map/Temzone-Android/actions/workflows/main.yml)
[![Quality](https://img.shields.io/codefactor/grade/github/Temtem-Interactive-Map/Temzone-Android)](https://www.codefactor.io/repository/github/temtem-interactive-map/temzone-android)

Welcome to Temzone Android, a native Android application built with Kotlin from Temtem Interactive Map.

## Getting started

This guide will help you get up and running the Android application in just a few minutes.

### Prerequisites

Before getting started, make sure you have the following tools installed on your development machine:

- Ensure you have [Android Studio](https://developer.android.com/studio) installed and properly configured. Android Studio provides the necessary tools, SDKs, and emulators to build and test Android applications.
- To run and test the application, you'll need either a physical Android device connected to your development machine or an Android emulator set up and running.

### Setting up application

To set up the Android application properly, you'll need to follow these steps:

- Before building the application, make sure you have the `google-services.json` file properly placed in the appropriate location in your project. This file is essential for integrating Firebase services with your app, such as Firebase Cloud Messaging, Firebase Authentication, etc. If you haven't done so already, you can obtain this file by creating a new project on the [Firebase console](https://console.firebase.google.com) and following the provided instructions.
- Create the `local.properties` file in the project's root directory if it doesn't already exist. This file will be used to store sensitive or environment-specific information. In the local.properties file, add the following lines with the appropriate URLs:
  - `DEBUG_BASE_URL`: This variable represents the base URL for the debug environment. It should contain the base URL of the Temzone API instance.
  - `RELEASE_BASE_URL`: This variable represents the base URL for the release environment. It should contain the base URL of the Temzone API instance.

With these prerequisites in place, you can proceed with the setup of the Android application and ensure that the correct configurations are applied based on the environment (debug or release) during development and deployment.

### Running tests

To execute instrumented tests for the Android application, follow these steps:

- Ensure that you have the necessary development environment set up, including the Android SDK and Gradle.
- Connect a physical device to your computer or start an Android emulator.
- Open a terminal or command prompt in the root directory of the project.
- Use the following command to run the instrumented tests:

```
./gradlew connectedAndroidTest
```

This command will trigger the Gradle build process and start running all the instrumented tests in the project on the connected device or emulator.

Instrumented tests are designed to simulate real interactions with the application on a device, providing a more comprehensive evaluation of its functionality and behavior. As a result, they might take longer to complete than local unit tests. It's essential to have a stable testing environment to ensure reliable and accurate test results.

## License

This project is licensed under the terms of the [MIT license](https://github.com/Temtem-Interactive-Map/Temzone-Android/blob/main/LICENSE).
