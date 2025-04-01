#!/bin/bash

# Set the path to your JavaFX SDK - updated to match your actual location
JAVAFX_PATH=/Users/marlvingoremusandu/Downloads/javafx-sdk-17.0.14/lib

# If the first path doesn't exist, try the alternative
if [ ! -d "$JAVAFX_PATH" ]; then
    JAVAFX_PATH=/Users/marlvingoremusandu/Downloads/javafx-sdk-24/lib
    echo "Using alternative JavaFX path: $JAVAFX_PATH"
else
    echo "Using JavaFX from: $JAVAFX_PATH"
fi

# Compile the project
mvn clean compile

# Run the application with special macOS flags
java \
  --module-path "$JAVAFX_PATH" \
  --add-modules javafx.controls,javafx.fxml \
  -Dglass.disableThreadChecks=true \
  -Dprism.order=sw \
  -cp target/classes studypartner.StudyPartnerApp
