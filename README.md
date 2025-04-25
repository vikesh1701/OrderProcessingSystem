# OrderProcessingSystem

**Requirements**
The system fulfills the following requirements:
Core Features:
  -Create an Order: Customers can place orders containing multiple items.
  -Retrieve Order Details: Fetch order details using a unique order ID.
  -Update Order Status: Support order statuses (PENDING, PROCESSING, SHIPPED, DELIVERED) with manual updates and an automated job to transition PENDING to PROCESSING every 
   5 minutes.
  -List All Orders: Retrieve all orders, with optional filtering by status.
  -Cancel an Order: Allow cancellation of orders only in PENDING status.
  
Technical Requirements:
  -Built using Java 21 and Spring Boot 3.3.4.
  -Persist data using JPA/Hibernate with an H2 in-memory database.
  -Expose functionality via REST APIs.
  -Include comprehensive unit and integration tests using JUnit 5.
  -Use AI tools (e.g., Cursor AI, ChatGPT) for code generation, with manual validation to ensure correctness.

**System Architecture**
Technology Stack
  Language: Java 21
  Framework: Spring Boot 3.3.4
  Dependencies:
    -Spring Web: For REST API development.
    -Spring Data JPA: For database operations.
    -H2 Database: In-memory database for testing.
    -Spring Boot Starter Test: For unit and integration testing.
  Build Tool: Maven
  Testing: JUnit 5, AssertJ, Mockito, Spring Test
  IDE Support: Compatible with IntelliJ IDEA, Eclipse, etc.

 **Architecture Diagram**
[Client] <--> [REST API (OrderController)] <--> [Business Logic (OrderService)] <--> [Data Access (OrderRepository)] <--> [H2 Database]
                                                                 |
                                                                 v
                                                  [Background Job (OrderStatusUpdateJob)]
**Components**
Model Layer:
  -Order: Represents an order with attributes like ID, customer ID, status, creation/update timestamps, and a list of items.
  -OrderItem: Represents an item in an order with product name, quantity, and price.
  -OrderStatus: Enum defining possible statuses (PENDING, PROCESSING, SHIPPED, DELIVERED).
Repository Layer:
   OrderRepository: Spring Data JPA repository for CRUD operations and status-based queries.
Service Layer:
  -OrderService: Handles business logic for creating, retrieving, updating, listing, and canceling orders.
  -OrderStatusUpdateJob: Scheduled task to update PENDING orders to PROCESSING every 5 minutes.
Controller Layer:
  -OrderController: Exposes REST endpoints for order operations.
  -GlobalExceptionHandler: Handles exceptions to return appropriate HTTP status codes.
Configuration:
  -H2 database configuration via application.properties.
  -Scheduling enabled with @EnableScheduling.

**Implementation Details**

**Project Structure**
com.ecommerce.orderprocessing
├── model
│   ├── Order.java
│   ├── OrderItem.java
│   └── OrderStatus.java
├── repository
│   └── OrderRepository.java
├── service
│   ├── OrderService.java
│   └── OrderStatusUpdateJob.java
├── controller
│   ├── OrderController.java
│   └── GlobalExceptionHandler.java
├── OrderProcessingApplication.java
└── resources
    └── application.properties
    └── request
      └── json

**Key Implementations**
Entity Models
Order:
Fields: id, customerId, status, createdAt, updatedAt, items.
Relationships: One-to-Many with OrderItem (cascade all operations).
Annotations: @Entity, @Enumerated(EnumType.STRING) for status.
OrderItem:
Fields: id, productName, quantity, price.
Annotations: @Entity.
OrderStatus:
Enum: PENDING, PROCESSING, SHIPPED, DELIVERED.
Repository
OrderRepository extends JpaRepository<Order, Long>.
Custom method: findByStatus(OrderStatus status) for status-based filtering.
Service
OrderService:
createOrder: Sets PENDING status and timestamps, saves the order.
getOrderById: Retrieves an order or throws NoSuchElementException.
getAllOrders: Lists all orders or filters by status.
updateOrderStatus: Updates status and timestamps.
cancelOrder: Deletes PENDING orders or throws IllegalStateException for non-PENDING orders.
OrderStatusUpdateJob:
Uses @Scheduled(fixedDelay = 300000) to run every 5 minutes.
Updates all PENDING orders to PROCESSING.
Controller
OrderController:
Endpoints:
POST /api/orders: Create an order.
GET /api/orders/{orderId}: Retrieve order by ID.
GET /api/orders: List orders, optional status query parameter.
PUT /api/orders/{orderId}/status: Update order status.
DELETE /api/orders/{orderId}: Cancel an order.
GlobalExceptionHandler:
Handles NoSuchElementException (404) and IllegalStateException (400).

**Configuration**
application.properties:
Configures H2 in-memory database (jdbc:h2:mem:testdb).
Enables H2 console for debugging (spring.h2.console.enabled=true).
@EnableScheduling on the main application class to enable the background job.

**API Endpoints**
Method
Endpoint
Description
Request Body
Response
POST
/api/orders
Create a new order
Order (JSON)
Order (JSON)
GET
/api/orders/{orderId}
Retrieve order by ID
None
Order (JSON)
GET
/api/orders?status={status}
List orders, optional status filter
None
List of Order (JSON)
PUT
/api/orders/{orderId}/status
Update order status
OrderStatus (JSON)
Order (JSON)
DELETE
/api/orders/{orderId}
Cancel an order (PENDING only)
None
204 No Content
4.4 Error Handling
404 Not Found: When an order ID does not exist.
400 Bad Request: When attempting to cancel a non-PENDING order.
500 Internal Server Error: For unexpected errors (minimized through validation).

**Testing Strategy**
Test Coverage
The system includes extensive unit and integration tests to validate all requirements:
Unit Tests:
OrderServiceTest: Tests business logic with mocked OrderRepository.
Covers: Order creation, retrieval, status updates, listing, cancellation (success and failure cases).
Integration Tests:
OrderControllerTest: Tests REST endpoints using MockMvc.
Covers: API responses, HTTP status codes, and error handling.
OrderRepositoryTest: Tests database operations using @DataJpaTest.
Covers: Persistence and retrieval of orders and items.
OrderStatusUpdateJobTest: Tests the background job.
Covers: Automatic status updates for PENDING orders.

**Test Cases**
Requirement
Test Cases
Create an order
- Create order with items, verify PENDING status and timestamps.
- API: POST returns created order with correct data.
Retrieve order details
- Get order by ID (success, failure for non-existent ID).
- API: GET returns order or 404.
Update order status
- Update status, verify new status and updated timestamp.
- API: PUT updates status or returns 404.
- Job: PENDING orders transition to PROCESSING, non-PENDING unaffected.
List all orders
- List all orders, list by status.
- API: GET returns all or filtered orders.
Cancel an order
- Cancel PENDING order, fail for non-PENDING, fail for non-existent ID.
- API: DELETE returns 204, 400, or 404 as appropriate.
  
**Testing Tools**
  JUnit 5: Test framework.
  AssertJ: Fluent assertions for readable tests.
  Mockito: Mocking dependencies in unit tests.
  Spring Test: @SpringBootTest, @DataJpaTest, @AutoConfigureMockMvc for integration tests.

**AI Tool Usage**
AI tools were used to accelerate development:
Cursor AI:
Generated: Project structure, pom.xml, repository, controller, and test skeletons.
Issues: Outdated dependencies, incorrect REST annotations, incomplete test mocks.
Resolutions: Updated dependencies to Spring Boot 3.3.4, corrected annotations, and added edge-case tests.
ChatGPT:
Generated: Entity classes, service logic, scheduled task, and test cases.
Issues: Missing JPA cascading rules, incorrect enum handling, lack of exception handling.
Resolutions: Added CascadeType.ALL, used @Enumerated(EnumType.STRING), and implemented proper validation.
All AI-generated code was reviewed and enhanced to meet requirements and ensure quality.

**Deployment and Usage**
  Prerequisites
    Java 21
    Maven 3.8+
    IDE (e.g., IntelliJ IDEA) or command-line tools

**Setup Instructions**
Clone the repository:
bash
git clone <repository-url>
Navigate to the project directory:
bash
cd order-processing
Build the project:
bash
mvn clean install
Run the application:
bash
mvn spring-boot:run

**Accessing the Application**
H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:testdb, username: sa, password: empty).
API Base URL: http://localhost:8080/api/orders.

**Test endpoints using tools like Postman or curl**.

**Example API Requests**
Create an Order:
bash
curl -X POST http://localhost:8080/api/orders \
-H "Content-Type: application/json" \
-d '{"customerId":"CUST123","items":[{"productName":"Laptop","quantity":1,"price":1000.0}]}'
Retrieve an Order:
bash
curl http://localhost:8080/api/orders/1
List Orders by Status:
bash
curl http://localhost:8080/api/orders?status=PENDING
Update Order Status:
bash
curl -X PUT http://localhost:8080/api/orders/1/status \
-H "Content-Type: application/json" \
-d '"SHIPPED"'
Cancel an Order:
bash
curl -X DELETE http://localhost:8080/api/orders/1

**Future Enhancements**
Authentication/Authorization: Add JWT or OAuth2 for secure access.
Pagination: Support paginated order listing for scalability.
Production Database: Replace H2 with PostgreSQL or MySQL.
Event-Driven Architecture: Use Kafka or RabbitMQ for order events.
Monitoring: Integrate Prometheus and Grafana for metrics and logging.

**Conclusion**
The Order Processing System is a fully functional, well-tested backend application that meets all specified requirements. It leverages Spring Boot for rapid development, JPA for data persistence, and JUnit for comprehensive testing. The use of AI tools accelerated development, with manual refinements ensuring quality. The system is ready for production use with minor adjustments for scalability and security.
For further details or support, contact the development team or refer to the source code.
This documentation provides a clear, structured overview of the project, suitable for inclusion in a project repository or handover to stakeholders. Let me know if you need additional sections or specific details!
