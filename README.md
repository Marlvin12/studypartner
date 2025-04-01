Study Partner Finder Application
Overview
Study Partner Finder is a JavaFX application designed to help students connect with peers who are studying the same subjects and have compatible schedules. The application allows users to create profiles, find study partners based on shared interests and availability, and communicate through an integrated messaging system.
Features


User registration and authentication: Create an account or log in securely
![Login](https://github.com/user-attachments/assets/efebde00-0678-4a9a-ab9f-9ad327ae726d)

![logout](https://github.com/user-attachments/assets/18afb8a9-98f0-441b-98d5-383fbb065161)

Profile management: Edit personal information, add academic subjects, and set availability schedules
![dashboard](https://github.com/user-attachments/assets/ce845b10-4adf-4174-97b7-1dbc27585ac6)
![dashboard2](https://github.com/user-attachments/assets/022e18ea-9fa4-4ab0-91a2-a515af8bf471)

Smart matching algorithm: Find compatible study partners based on subject overlap and time availability
![find_partner](https://github.com/user-attachments/assets/c9b67403-b42a-49e2-a715-8d7ca73dc187)


Real-time messaging system: Communicate directly with potential study partners
![messages](https://github.com/user-attachments/assets/7a0d64d3-b195-4deb-8049-246c8562017c)



Intuitive user interface: Clean, responsive design for a seamless user experience

Prerequisites

Java JDK 11 or higher
JavaFX 17 or higher
Maven 3.6 or higher

Setup Instructions
1. Clone the Repository
   
git clone https://github.com/yourusername/studypartner.git
cd studypartner

2. Install JavaFX SDK

Download JavaFX SDK from https://gluonhq.com/products/javafx/
Extract it to a location on your computer

3. Running the Application
Option A: Using Maven
mvn clean compile exec:java -Dexec.mainClass="studypartner.StudyPartnerApp"

Option B: Using the run script (Recommended for macOS users)

Create a file called run.sh with the following content:

#!/bin/bash

# Set the path to your JavaFX SDK - update this to your actual path!
JAVAFX_PATH=/path/to/your/javafx-sdk/lib

# Compile the project
mvn clean compile

# Run the application with special flags
java \
  --module-path "$JAVAFX_PATH" \
  --add-modules javafx.controls,javafx.fxml \
  -Dglass.disableThreadChecks=true \
  -Dprism.order=sw \
  -cp target/classes studypartner.StudyPartnerApp

Make the script executable:
chmod +x run.sh
Run the application:
./run.sh
