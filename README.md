# Permission

## Overview

The Water Permission System is a core component of the Water framework, designed to provide robust and flexible permission management for applications built on the platform. It enables fine-grained control over who can access specific resources and perform particular actions, ensuring security and compliance.

This repository houses the complete implementation of the Permission system, including APIs, data models, services, integration components, and a permission manager. It offers a comprehensive solution for managing permissions based on users, roles, resource ownership, and sharing status. The system is designed to be modular and extensible, allowing developers to easily integrate it into their Water-based applications and customize it to meet their specific needs.

The primary goal of the Permission system is to centralize and streamline permission management, reducing the complexity of securing applications and ensuring consistent enforcement of access control policies. It provides a clear and well-defined API for checking permissions, making it easy for developers to integrate permission checks into their code.

## Technology Stack

The Water Permission System is built using the following technologies:

*   **Language:** Java
*   **Frameworks:**
    *   Spring Framework (for dependency injection, AOP, and REST API development in the `Permission-service-spring` module)
    *   Jakarta Persistence API (JPA) for data persistence
*   **Libraries:**
    *   SLF4J for logging
    *   Atteo Class Index for compile-time class indexing
    *   Lombok for reducing boilerplate code
    *   Hibernate as the JPA implementation
    *   HSQLDB for in-memory testing
    *   Swagger-jaxrs and org.springdoc for generating OpenAPI documentation
    *   Karate DSL for API testing
    *   JUnit Jupiter for unit testing
    *   Mockito for mocking
    *   org.bouncycastle and com.nimbusds for encryption and JWT handling
    *   biz.aQute.bnd.builder for building OSGi bundles
    *   Jackson for JSON processing
    *   org.reflections for runtime metadata analysis
    *   Apache CXF & Jetty for testing REST services
*   **Tools:**
    *   Gradle for build automation
    *   SonarQube for code quality analysis

## Directory Structure

The project's directory structure is organized as follows:

```
Permission/
├── build.gradle                      - Root build configuration file for the entire project.
├── gradle.properties                 - Gradle properties for configuring the build environment.
├── settings.gradle                   - Settings file for multi-module Gradle project.
├── Permission-api/                   - Defines the API interfaces and data models.
│   ├── build.gradle                  - Build configuration for the Permission-api module.
│   ├── src/
│   │   ├── main/java/it/water/permission/api/ - Java source files for the API module.
│   │   │   ├── PermissionApi.java        - Interface defining high-level permission operations.
│   │   │   ├── PermissionRepository.java   - Interface for permission repository operations.
│   │   │   ├── PermissionSystemApi.java    - Interface for system-level permission management.
│   │   │   ├── rest/
│   │   │   │   ├── PermissionRestApi.java  - Interface defining REST API endpoints.
│   └── ...
├── Permission-model/                 - Defines the data model for permissions.
│   ├── build.gradle                  - Build configuration for the Permission-model module.
│   ├── src/
│   │   ├── main/java/it/water/permission/model/ - Java source files for the model module.
│   │   │   ├── WaterPermission.java      - Represents the permission entity.
│   │   │   ├── PermissionsActions.java   - Defines constants for permission actions.
│   └── ...
├── Permission-service/               - Implements the core logic and services.
│   ├── build.gradle                  - Build configuration for the Permission-service module.
│   ├── src/
│   │   ├── main/java/it/water/permission/service/ - Java source files for the service module.
│   │   │   ├── PermissionRepositoryImpl.java - Implements PermissionRepository using JPA/Hibernate.
│   │   │   ├── PermissionIntegrationLocalClient.java - Local permission check implementation.
│   │   │   ├── PermissionServiceImpl.java  - Implements PermissionApi.
│   │   │   ├── PermissionSystemServiceImpl.java - Implements PermissionSystemApi.
│   │   │   ├── rest/
│   │   │   │   ├── PermissionRestControllerImpl.java - REST API implementation.
│   │   ├── test/java/it/water/permission/ - Test classes for the service module.
│   │   ├── test/resources/
│   │   │   ├── it.water.application.properties - Application properties for testing.
│   │   │   ├── karate-config.js            - Karate DSL configuration for API testing.
│   └── ...
├── Permission-integration/           - Provides integration components.
│   ├── build.gradle                  - Build configuration for the Permission-integration module.
│   ├── src/
│   │   ├── main/java/it/water/permission/service/integration/ - Java source files for integration.
│   │   │   ├── PermissionIntegrationRestClient.java - REST client for the Permission system.
│   │   ├── test/java/it/water/permission/ - Test classes for the integration module.
│   │   ├── test/resources/
│   │   │   ├── it.water.application.properties - Application properties for testing.
│   └── ...
├── Permission-manager/               - Contains the PermissionManager implementation.
│   ├── build.gradle                  - Build configuration for the Permission-manager module.
│   ├── src/
│   │   ├── main/java/it/water/permission/manager/ - Java source files for the manager.
│   │   │   ├── PermissionManagerDefault.java - Core permission checking logic.
│   │   ├── test/java/it/water/permission/manager/ - Test classes for the manager module.
│   │   ├── test/resources/
│   │   │   ├── it.water.application.properties - Application properties for testing.
│   │   │   ├── META-INF/persistence.xml   - JPA persistence configuration for testing.
│   └── ...
├── Permission-service-spring/        - Spring-based implementation of the Permission service.
│   ├── build.gradle                  - Build configuration for the Permission-service-spring module.
│   ├── src/
│   │   ├── main/java/it/water/permission/ - Java source files for the Spring service.
│   │   │   ├── PermissionApplication.java  - Main class to start the Spring Boot application.
│   │   │   ├── api/rest/spring/
│   │   │   │   ├── PermissionSpringRestApi.java - REST API endpoints for Spring.
│   │   │   │   ├── PermissionSpringRestControllerImpl.java - REST API implementation for Spring.
│   │   ├── main/resources/
│   │   │   ├── application.properties    - Spring Boot application properties.
│   │   ├── test/java/it/water/permission/ - Test classes for the Spring service module.
│   │   ├── test/resources/
│   │   │   ├── karate-config.js            - Karate DSL configuration for API testing.
│   └── ...
└── README.md                         - Project documentation.
```

## Getting Started

To get started with the Water Permission System, follow these steps:

1.  **Prerequisites:**
    *   Java Development Kit (JDK) 11 or higher
    *   Gradle 7.0 or higher
    *   An IDE such as IntelliJ IDEA or Eclipse is recommended for development.

2.  **Clone the Repository:**
    ```bash
    git clone https://github.com/Water-Framework/Permission.git
    cd Permission
    ```

3.  **Build the Project:**
    ```bash
    gradle build
    ```
    This command compiles the code, runs the tests, and generates the build artifacts for each module.

4.  **Run Tests:**
    ```bash
    gradle test
    ```
    This command executes all the unit and integration tests in the project.

5.  **Publishing to Maven Repository (Optional):**

    To publish the artifacts to a Maven repository, you need to configure the repository settings in the `build.gradle` file. The project is configured to publish to a Nexus repository. Set the following properties either as system properties or directly in the `gradle.properties` file:

    ```properties
    publishRepoUsername=your_nexus_username
    publishRepoPassword=your_nexus_password
    ```

    Then, run the following command:

    ```bash
    gradle publish
    ```

### Module Usage

Each module in the Water Permission System serves a specific purpose and can be used independently or in combination with other modules.

*   **Permission-api:** This module defines the core APIs for the Permission system. It contains interfaces for managing permissions, checking access, and retrieving permission information. To use this module, add it as a dependency to your project:

    ```gradle
    dependencies {
        implementation project(':Permission-api')
    }
    ```
    This module provides the foundational interfaces for interacting with the Permission system.

*   **Permission-model:** This module defines the data model for permissions, including the `WaterPermission` entity and related classes. To use this module, add it as a dependency to your project:

    ```gradle
    dependencies {
        implementation project(':Permission-model')
    }
    ```
    This module provides the data structures used by the Permission system.

*   **Permission-service:** This module implements the core logic and services for managing permissions. It provides implementations of the APIs defined in the `Permission-api` module. To use this module, add it as a dependency to your project:

    ```gradle
    dependencies {
        implementation project(':Permission-service')
    }
    ```
    This module provides the core business logic for the Permission system.  It requires a JPA provider (like Hibernate) to be configured.

*   **Permission-integration:** This module provides integration components for interacting with the Permission system from other modules. It includes a REST client for accessing the Permission system's REST API. To use this module, add it as a dependency to your project:

    ```gradle
    dependencies {
        implementation project(':Permission-integration')
    }
    ```

    This module is useful for integrating the Permission system with other applications or services via REST.

*   **Permission-manager:** This module contains the `PermissionManagerDefault` class, which implements the `PermissionManager` interface, providing the core permission checking logic. To use this module, add it as a dependency to your project:

    ```gradle
    dependencies {
        implementation project(':Permission-manager')
    }
    ```

    This module provides the central component for permission management and is typically used by applications that need to enforce access control policies.  It requires implementations of `UserIntegrationClient`, `RoleIntegrationClient`, `SharedEntityIntegrationClient`, and `PermissionIntegrationClient` to function.

*   **Permission-service-spring:** This module provides a Spring-based implementation of the Permission service. It can be deployed as a standalone Spring Boot application or integrated into an existing Spring application. To use this module, add it as a dependency to your project:

    ```gradle
    dependencies {
        implementation project(':Permission-service-spring')
    }
    ```

    This module provides a convenient way to deploy the Permission system as a Spring Boot application.  It requires a database to be configured in the `application.properties` file.

### Minimal Usage Patterns

*   **Setting up the Spring-based Permission Service:**

    To set up the `Permission-service-spring` module, you need to configure the database connection and other properties in the `application.properties` file. For example:

    ```properties
    spring.datasource.driver-class-name=org.hsqldb.jdbcDriver
    spring.datasource.username=sa
    spring.datasource.password=
    spring.datasource.url=jdbc:hsqldb:mem:testdb
    spring.jpa.hibernate.ddl-auto=create-drop
    server.servlet.context-path=/water
    water.testMode=false
    water.keystore.password=water.
    water.keystore.alias=server-cert
    water.keystore.file=src/test/resources/certs/server.keystore
    water.private.key.password=water.
    water.rest.security.jwt.duration.millis=3600000
    ```

    This configuration sets up an in-memory HSQLDB database for development and configures the JWT settings for REST API security.

*   **Using the Permission Manager:**

    To use the `PermissionManagerDefault` class, you need to inject the required dependencies, such as `UserIntegrationClient`, `RoleIntegrationClient`, and `PermissionIntegrationClient`. For example:

    ```java
    @Inject
    @Setter
    private PermissionIntegrationClient permissionIntegrationClient;

    @Inject
    @Setter
    private UserIntegrationClient userIntegrationClient;

    @Inject
    @Setter
    private RoleIntegrationClient roleIntegrationClient;

    @Inject
    @Setter
    private ActionsManager actionsManager;

    @Inject
    @Setter
    private ComponentRegistry componentRegistry;
    ```

    Then, you can use the `checkPermission` method to check if a user has permission to perform a specific action on a resource.

    ```java
    boolean hasPermission = permissionManager.checkPermission(username, resource, action);
    ```

## Functional Analysis

### 1. Main Responsibilities of the System

The primary responsibilities of the Water Permission System are:

*   **Defining Permissions:** Providing a mechanism to define permissions for users and roles, specifying which actions they are allowed to perform on specific resources.
*   **Checking Permissions:** Implementing a robust and efficient way to check if a user has permission to perform a specific action on a resource.
*   **Managing Permissions:** Providing APIs and services for managing permissions, including creating, updating, and deleting permissions.
*   **Enforcing Permissions:** Ensuring that access control policies are consistently enforced across the Water platform.
*   **Integrating with Other Systems:** Providing integration components for interacting with other systems, such as user management and resource management.
*   **Handling Resource Ownership and Sharing:** Incorporating resource ownership and sharing status into permission checks, allowing for more granular access control.

The system provides foundational services for managing permissions, including interfaces for defining permissions, checking access, and retrieving permission information. It also provides abstractions for interacting with different types of resources and users.

### 2. Problems the System Solves

The Water Permission System solves the following problems:

*   **Centralized Permission Management:** It provides a centralized location for managing permissions, reducing the complexity of securing applications and ensuring consistent enforcement of access control policies.
*   **Fine-Grained Access Control:** It enables fine-grained control over who can access specific resources and perform particular actions, allowing for more precise access control policies.
*   **Resource Ownership and Sharing:** It incorporates resource ownership and sharing status into permission checks, allowing for more granular access control based on resource ownership and sharing.
*   **Integration with Other Systems:** It provides integration components for interacting with other systems, such as user management and resource management, simplifying the integration of permission management into existing applications.
*   **Scalability and Performance:** It is designed to be scalable and performant, ensuring that permission checks can be performed efficiently even in large and complex systems.

For example, in a content management system built on the Water platform, the Permission system can be used to control who can create, edit, and delete content based on their roles and the ownership of the content. This ensures that only authorized users can modify content, preventing unauthorized access and data breaches.

### 3. Interaction of Modules and Components

The different modules and components of the Water Permission System interact with each other to provide the overall functionality of the system.

*   The **API Layer (`Permission-api`)** defines the interfaces for interacting with the Permission system. These interfaces are used by other modules to access permission-related functionalities.
*   The **Service Layer (`Permission-service` and `Permission-service-spring`)** implements the business logic for permission management. It uses the Repository layer to access and manipulate permission data in the database.
*   The **Repository Layer (`PermissionRepositoryImpl`)** provides data access operations, interacting with the database using JPA and Hibernate.
*   The **Integration Layer (`Permission-integration`)** provides components for accessing the Permission system's REST API from other modules.
*   The **Manager Layer (`Permission-manager`)** provides the core permission checking logic, utilizing the Service and Integration layers to retrieve user, role, and permission information.

The interaction flow is as follows:

1.  A component (e.g., a service or controller) needs to check if a user has permission to perform a specific action on a resource.
2.  The component calls the `checkPermission` method of the `PermissionManagerDefault` class.
3.  The `PermissionManagerDefault` retrieves user information using the `UserIntegrationClient` and role information using the `RoleIntegrationClient`.
4.  The `PermissionManagerDefault` retrieves permission information using the `PermissionIntegrationClient`, which in turn interacts with the `PermissionSystemApi` through the `PermissionIntegrationLocalClient`.
5.  The `PermissionManagerDefault` evaluates the user's permissions based on their roles, the resource, and the action being performed. It considers factors like resource ownership, sharing status, and specific entity permissions.
6.  The `PermissionManagerDefault` returns a boolean value indicating whether the user has the required permission.

This layered architecture promotes loose coupling and separation of concerns, making the system more maintainable and extensible.

### 4. User-Facing vs. System-Facing Functionalities

The Water Permission System provides both user-facing and system-facing functionalities.

*   **User-Facing Functionalities:**
    *   **REST API Endpoints:** The `Permission-service` and `Permission-service-spring` modules expose REST API endpoints for managing permissions. These endpoints can be used by administrators to create, update, and delete permissions.
    *   **UI Components:** UI components can be built on top of the REST API endpoints to provide a user-friendly interface for managing permissions.

*   **System-Facing Functionalities:**
    *   **Permission Checking API:** The `PermissionManager` interface provides a system-facing API for checking permissions. This API is used by other components in the Water platform to enforce access control policies.
    *   **Integration Clients:** The `UserIntegrationClient`, `RoleIntegrationClient`, and `PermissionIntegrationClient` interfaces provide system-facing APIs for interacting with other systems, such as user management and resource management.
    *   **Background Jobs:** Background jobs can be used to synchronize permissions with other systems or to perform other maintenance tasks.

The user-facing functionalities provide a way for administrators to manage permissions, while the system-facing functionalities provide a way for other components in the Water platform to enforce access control policies.

Additionally, the `FrameworkComponent` annotation systematically applies common behaviors across implementing classes, ensuring consistent functionality.

## Architectural Patterns and Design Principles Applied

The Water Permission System applies several architectural patterns and design principles:

*   **Layered Architecture:** The project is divided into multiple layers (API, Service, Repository, Integration, Manager) to separate concerns and improve maintainability.
*   **Dependency Injection (DI):** The Spring Framework is used to manage dependencies between components, promoting loose coupling and testability. The `@Inject` annotation is used to inject dependencies into components.
*   **Interface-Based Programming:** Components interact with each other through interfaces, allowing for flexible implementations and easier testing.
*   **RESTful API:** The project exposes RESTful APIs for managing permissions, enabling integration with other systems.
*   **Microservices Architecture (Potential):** The modular structure of the project, with separate modules for API, Service, and Integration, suggests a potential for deploying these modules as independent microservices.
*   **Principle of Least Privilege:** The system enforces the principle of least privilege by granting users only the permissions they need to perform their tasks.
*   **Separation of Concerns:** Each module and component has a specific responsibility, promoting code clarity and maintainability.
*   **Role-Based Access Control (RBAC):** Permissions are assigned to roles, and users are assigned to roles. This allows for efficient management of permissions for large numbers of users.
*   **Interceptor Pattern:** The `Core-interceptors` module (from `it.water.core`) is used to intercept method calls and perform actions before or after the method is executed. This can be used to implement cross-cutting concerns, such as logging and security.
*   **Event-Driven Architecture:** The system can be extended to use an event-driven architecture to notify other components when permissions are changed. This can be used to invalidate caches or update other systems.

## Code Quality Analysis

Based on the SonarQube report:

*   **Bugs:** 0 - No bugs were identified, indicating a stable codebase.
*   **Vulnerabilities:** 0 - No vulnerabilities were found, suggesting good security practices.
*   **Code Smells:** 0 - No code smells were detected, reflecting clean and maintainable code.
*   **Code Coverage:** 80.3% - This is a good level of coverage, indicating that most of the code is well-tested.
*   **Duplication:** 0.0% - No duplicated code was found, indicating good code reuse and maintainability.

These metrics indicate that the Water Permission System has excellent code quality, with no identified bugs, vulnerabilities, code smells, or duplicated code. The code coverage is reasonably high, suggesting that the system is well-tested. This contributes to the project's maintainability, reliability, and security.

## Weaknesses and Areas for Improvement

The Water Permission System is a well-designed and implemented system, but there are some areas that could be improved:

*   [ ] **Enhance Documentation:** While the code is well-structured, detailed documentation for each module and API would improve developer onboarding and ease of integration. Focus on providing clear examples and use cases.
*   [ ] **Expand Test Coverage:** Although the current code coverage is good (80.3%), aim to increase it further, especially for complex or critical modules. Consider using mutation testing to identify gaps in test coverage.
*   [ ] **Implement Caching:** Implement caching mechanisms to improve the performance of permission checks, especially in high-volume scenarios.
*   [ ] **Add Auditing:** Implement auditing capabilities to track permission changes and access attempts. This would improve security and compliance.
*   [ ] **Improve Error Handling:** Review and improve error handling throughout the system, providing more informative error messages and better handling of unexpected exceptions.
*   [ ] **Refactor Complex Modules:** While no code smells were detected, identify and refactor any modules that are particularly complex or difficult to understand.
*   [ ] **Implement Fine-Grained Permissions for Actions:** Currently, permissions are checked based on the action ID. Consider implementing more fine-grained permissions for actions, such as allowing different levels of access for different users or roles.
*   [ ] **Add Support for Custom Resource Types:** Currently, the system supports a limited number of resource types. Consider adding support for custom resource types, allowing developers to define their own resources and permissions.
*   [ ] **Establish SonarQube Monitoring:** Set up alerts and thresholds in SonarQube to proactively monitor code quality and maintain current standards as the project evolves.
*   [ ] **Address Potential Performance Bottlenecks:** Investigate and address any potential performance bottlenecks, especially in the `PermissionManagerDefault` class, which is responsible for permission checking.

## Further Areas of Investigation

The following architectural or technical elements warrant additional exploration or clarification:

*   **Performance Optimization:** Investigate potential performance bottlenecks in the permission checking logic and explore optimization techniques such as caching and indexing.
*   **Scalability Considerations:** Evaluate the scalability of the system and identify potential areas for improvement, such as using a distributed cache or database.
*   **Integration with External Systems:** Explore integration options with other systems, such as identity providers and access management systems.
*   **Advanced Permission Models:** Research and evaluate advanced permission models, such as attribute-based access control (ABAC) and relationship-based access control (ReBAC).
*   **Dynamic Permission Updates:** Investigate the possibility of dynamically updating permissions without requiring a restart of the application.
*   **Security Hardening:** Perform a thorough security review of the system and implement any necessary security hardening measures.
*    **Investigate branch coverage**: Investigate why branch coverage is N/A and if it can be enabled to give more detailed coverage reports.

## Attribution

Generated with the support of ArchAI, an automated documentation system.