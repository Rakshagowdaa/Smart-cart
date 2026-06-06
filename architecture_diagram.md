# Smart Cart Architecture Diagram

This architecture visually maps out the internal design of the **Smart Cart** microservices ecosystem. It details how the API Gateway manages ingress traffic, the Discovery Server connects nodes, how internal services query dependencies using Feign, and the dedicated MySQL databases holding independent state.

```mermaid
graph TD
    %% Base styling
    classDef default fill:#f9f9f9,stroke:#333,stroke-width:2px;
    classDef gateway fill:#e1f5fe,stroke:#0288d1,stroke-width:2px;
    classDef eureka fill:#fff3e0,stroke:#f57c00,stroke-width:2px;
    classDef service fill:#e8f5e9,stroke:#388e3c,stroke-width:2px;
    classDef db fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px;
    classDef external fill:#eceff1,stroke:#607d8b,stroke-width:2px;

    Client((Client / Frontend / Postman)):::external
    
    Gateway["API Gateway (Port 8080)"]:::gateway
    Eureka(("Eureka Discovery Server (Port 8761)")):::eureka

    %% Services
    subgraph Microservices Cluster
        Auth["Auth Service (8081)"]:::service
        User["User Service (8082)"]:::service
        Product["Product Service (8083)"]:::service
        Cart["Cart Service (8084)"]:::service
        Order["Order Service (8085)"]:::service
        Payment["Payment Service (8086)"]:::service
        Notification["Notification Service (8087)"]:::service
    end

    %% Databases
    UserDB[("User DB (MySQL)")]:::db
    ProductDB[("Product DB (MySQL)")]:::db
    CartDB[("Cart DB (MySQL)")]:::db
    OrderDB[("Order DB (MySQL)")]:::db
    PaymentDB[("Payment DB (MySQL)")]:::db
    NotificationDB[("Notification DB (MySQL)")]:::db

    Razorpay(("Razorpay API")):::external

    %% Routing
    Client -->|REST / HTTP| Gateway
    Gateway -->|/api/auth/**| Auth
    Gateway -->|/api/users/**| User
    Gateway -->|/api/products/**| Product
    Gateway -->|/api/cart/**| Cart
    Gateway -->|/api/orders/**| Order
    Gateway -->|/api/payments/**| Payment

    %% Discovery
    Auth -.->|Registers| Eureka
    User -.->|Registers| Eureka
    Product -.->|Registers| Eureka
    Cart -.->|Registers| Eureka
    Order -.->|Registers| Eureka
    Payment -.->|Registers| Eureka
    Notification -.->|Registers| Eureka
    Gateway -.->|Fetches Registry| Eureka

    %% Inter-service Feign Communication
    Auth -->|Feign: Get User Details| User
    Cart -->|Feign: Verify Product/Stock| Product
    Order -->|Feign: Fetch Cart Items| Cart
    Order -->|Feign: Verify Product Details| Product

    %% DB Connections
    User -->|JPA / JDBC| UserDB
    Product -->|JPA / JDBC| ProductDB
    Cart -->|JPA / JDBC| CartDB
    Order -->|JPA / JDBC| OrderDB
    Payment -->|JPA / JDBC| PaymentDB
    Notification -->|JPA / JDBC| NotificationDB

    %% External integrations
    Payment -->|Validates Transaction| Razorpay
```

### Key Highlights
- **Single Point of Entry:** The **API Gateway** intercepts all client requests, executing rate limiting, global CORS processing, and delegating traffic to specific sub-systems.
- **Service Registration:** Every single node registers itself with **Eureka**. This dynamically allows the API Gateway and internally communicating services to find instance IP assignments safely on the fly.
- **Independent State Management:** Following genuine microservices philosophies, bounded contexts correctly manage their own unique **MySQL Databases**.
- **Internal RPC (Feign):** The graph visually demonstrates the newly load-balanced lines of internal requests connecting disparate microservice ecosystems securely (e.g. `Order` service directly talking to `Cart`).
