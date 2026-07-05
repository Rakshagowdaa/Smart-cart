@echo off
title SmartCart - Starting All Services
color 0A

echo ============================================
echo     SmartCart - Local Startup Script
echo ============================================
echo.
echo [!] Make sure MySQL is running on port 3306
echo [!] Make sure Kafka is running on port 9092
echo [!] Make sure Java 17 is installed
echo [!] Make sure Node.js is installed
echo.
pause

set ROOT=%~dp0

echo.
echo [1/10] Starting Discovery Server (Eureka)...
start "Discovery Server :8761" cmd /k "cd /d %ROOT%discovery-server && mvnw.cmd spring-boot:run"
echo Waiting 20 seconds for Eureka to be ready...
timeout /t 20 /nobreak > nul

echo.
echo [2/10] Starting API Gateway...
start "API Gateway :8080" cmd /k "cd /d %ROOT%api-gateway && mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak > nul

echo.
echo [3/10] Starting Auth Service...
start "Auth Service :8081" cmd /k "cd /d %ROOT%auth-service && mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak > nul

echo.
echo [4/10] Starting User Service...
start "User Service :8082" cmd /k "cd /d %ROOT%user-service && mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak > nul

echo.
echo [5/10] Starting Product Service...
start "Product Service :8083" cmd /k "cd /d %ROOT%product-service && mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak > nul

echo.
echo [6/10] Starting Cart Service...
start "Cart Service :8084" cmd /k "cd /d %ROOT%cart-service && mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak > nul

echo.
echo [7/10] Starting Order Service...
start "Order Service :8085" cmd /k "cd /d %ROOT%order-service && mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak > nul

echo.
echo [8/10] Starting Payment Service...
start "Payment Service :8086" cmd /k "cd /d %ROOT%payment-service && mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak > nul

echo.
echo [9/10] Starting Notification Service...
start "Notification Service :8087" cmd /k "cd /d %ROOT%notification-service && mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak > nul

echo.
echo [10/10] Starting Wishlist, Vendor Services...
start "Wishlist Service :8088" cmd /k "cd /d %ROOT%wishlist-service && mvnw.cmd spring-boot:run"
timeout /t 3 /nobreak > nul
start "Vendor Service :8089" cmd /k "cd /d %ROOT%vendor-service && mvnw.cmd spring-boot:run"
timeout /t 3 /nobreak > nul

echo.
echo ============================================
echo  Starting Frontend (React + Vite)...
echo ============================================
start "Frontend :5173" cmd /k "cd /d %ROOT%frontend && npm install && npm run dev"

echo.
echo ============================================
echo  All services are starting!
echo.
echo  Frontend :  http://localhost:5173
echo  API Gateway: http://localhost:8080
echo  Eureka:      http://localhost:8761
echo ============================================
echo.
pause
