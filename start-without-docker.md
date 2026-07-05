# SmartCart – Run Without Docker (Windows)

This guide lets you run the SmartCart project on any Windows machine **without Docker**.

---

## Prerequisites – Install These First

### 1. Java 17 (JDK)
- Download: https://adoptium.net/temurin/releases/?version=17
- Choose **Windows x64 .msi** installer
- After install, verify: open CMD and type `java -version` → should say `17.x.x`

### 2. MySQL 8
- Download: https://dev.mysql.com/downloads/installer/
- Choose **MySQL Installer for Windows**
- During setup, set **root password = `root`** (matches project config)
- After install, MySQL runs automatically on port `3306`

### 3. Apache Kafka (with Zookeeper)
- Download: https://kafka.apache.org/downloads → latest **Binary** (e.g., `kafka_2.13-3.6.x.tgz`)
- Extract to `C:\kafka`
- To start Zookeeper (open CMD as Administrator):
  ```
  C:\kafka\bin\windows\zookeeper-server-start.bat C:\kafka\config\zookeeper.properties
  ```
- To start Kafka (open a **new** CMD window):
  ```
  C:\kafka\bin\windows\kafka-server-start.bat C:\kafka\config\server.properties
  ```
- Keep **both windows open** — Kafka runs on port `9092`

### 4. Node.js 18+
- Download: https://nodejs.org → **LTS version**
- After install, verify: `node -v` and `npm -v`

### 5. Maven (optional — project has built-in mvnw)
- The project already has `mvnw.cmd` in each service folder, so Maven install is **not required**

---

## Starting the Project

### Step 1 – Start MySQL and Kafka
Make sure MySQL and Kafka are running **before** starting the services.

- MySQL: starts automatically after install (check Windows Services)
- Kafka: run the two CMD commands from Prerequisites Step 3

### Step 2 – Run the Startup Script
Double-click **`start-all.bat`** from the project root folder.

This script will:
1. Start **Eureka Discovery Server** (port 8761) and wait 20s for it to be ready
2. Start **API Gateway** (port 8080)
3. Start all **microservices** (ports 8081–8089)
4. Start the **React frontend** (port 5173)

Each service opens in its own CMD window so you can monitor logs.

### Step 3 – Open the App
Once all services are running (takes about 2-3 minutes total):

| URL | What it is |
|-----|-----------|
| http://localhost:5173 | Frontend (main app) |
| http://localhost:8761 | Eureka dashboard |
| http://localhost:8080 | API Gateway |

---

## Service Port Reference

| Service | Port |
|---------|------|
| Discovery Server (Eureka) | 8761 |
| API Gateway | 8080 |
| Auth Service | 8081 |
| User Service | 8082 |
| Product Service | 8083 |
| Cart Service | 8084 |
| Order Service | 8085 |
| Payment Service | 8086 |
| Notification Service | 8087 |
| Wishlist Service | 8088 |
| Vendor Service | 8089 |
| Frontend (React) | 5173 |

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `java not found` | Install Java 17 and add to PATH |
| `Cannot connect to MySQL` | Check MySQL is running in Windows Services |
| `Cannot connect to Kafka` | Make sure Zookeeper is running first, then Kafka |
| Port already in use | Run: `netstat -ano | findstr :8080` then `taskkill /PID [pid] /F` |
| Services fail to register in Eureka | Wait longer, Eureka takes 20-30s to start |

---

## Stopping All Services

Close each CMD window, or run this in a new CMD:

```
taskkill /F /FI "WINDOWTITLE eq Discovery Server*"
taskkill /F /FI "WINDOWTITLE eq API Gateway*"
taskkill /F /FI "WINDOWTITLE eq Auth Service*"
taskkill /F /FI "WINDOWTITLE eq User Service*"
taskkill /F /FI "WINDOWTITLE eq Product Service*"
taskkill /F /FI "WINDOWTITLE eq Cart Service*"
taskkill /F /FI "WINDOWTITLE eq Order Service*"
taskkill /F /FI "WINDOWTITLE eq Payment Service*"
taskkill /F /FI "WINDOWTITLE eq Notification Service*"
taskkill /F /FI "WINDOWTITLE eq Wishlist Service*"
taskkill /F /FI "WINDOWTITLE eq Vendor Service*"
taskkill /F /FI "WINDOWTITLE eq Frontend*"
```
