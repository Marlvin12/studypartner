Study Partner Finder Application
Overview

Study Partner Finder is a JavaFX application designed to help students connect with peers who are studying the same subjects and have compatible schedules. The application allows users to create profiles, find study partners based on shared interests and availability, and communicate through an integrated messaging system.
Features

    User registration and authentication
    Profile management with academic subjects and availability scheduling
    Smart matching algorithm to find compatible study partners
    Real-time messaging system
    Intuitive and responsive user interface

    Java JDK 11 or higher
    Maven 3.6 or higher

Setup Instructions
1. Clone the Repository

bash

git clone <repository-url>
cd studypartner

2. Install Dependencies

The project uses Maven for dependency management. All required dependencies are specified in the pom.xml file.
3. Run the Application

bash

mvn javafx:run

4. Build the Application

bash

mvn clean package

Development Guidelines
Adding New Features

    Create or modify model classes in the model package
    Update service classes as needed in the service package
    Design the UI using FXML (add new views to the resources/views directory)
    Implement controller logic in the controller package
    Update CSS styles in resources/css/styles.css

Coding Conventions

    Follow Java naming conventions
    Add JavaDoc comments for all public methods
    Use MVC (Model-View-Controller) pattern
    Handle exceptions properly
    Validate user input

Testing

The application includes sample data for testing. Use these credentials to log in:

    Email: alice@example.com, Password: password
    Email: bob@example.com, Password: password

Troubleshooting
Common Issues

    Build Errors: Make sure Maven is installed correctly and your JDK version matches the one specified in pom.xml
    Runtime Errors: Check that all the resources (CSS, FXML, images) are in the correct directories
    JavaFX Issues: Ensure that JavaFX dependencies are correctly resolved by Maven

Debugging Tips

    Check console output for error messages
    Use the DialogUtils class for displaying error information to users
    Set proper breakpoints in your IDE to trace execution flow

Future Enhancements

    Database integration for persistent storage
    Advanced filtering options for partner matching
    Group study session coordination
    Calendar integration
    Video chat capabilities

Contributing

    Create a branch for your feature
    Write clean, well-documented code
    Test thoroughly
    Submit a pull request with a clear description of your changes

License

[Your license information here]
