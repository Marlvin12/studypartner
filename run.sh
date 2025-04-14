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

# Get the full Maven classpath
CP=$(mvn dependency:build-classpath -Dmdep.outputFile=cp.txt && cat cp.txt)

# Add your target/classes and JavaFX libs to the classpath
FULL_CP="target/classes:$CP:$JAVAFX_PATH/*"

# Run the app
java \
  --module-path "$JAVAFX_PATH" \
  --add-modules javafx.controls,javafx.fxml \
  -Dglass.disableThreadChecks=true \
  -Dprism.order=sw \
  -cp "$FULL_CP" \
  studypartner.StudyPartnerApp

rm cp.txt