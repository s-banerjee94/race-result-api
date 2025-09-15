#!/bin/bash
# Start the Spring Boot application

APP_DIR="/opt/race-result-api"
JAR_FILE="$APP_DIR/race-result-api.jar"
PID_FILE="$APP_DIR/app.pid"
LOG_DIR="/var/log/race-result-api"

echo "Starting race-result-api application..."

# Check if JAR file exists
if [ ! -f "$JAR_FILE" ]; then
    echo "ERROR: JAR file not found at $JAR_FILE"
    exit 1
fi

# Ensure log directory exists
mkdir -p $LOG_DIR

# Set environment variables for production
export SPRING_PROFILES_ACTIVE=prod
export JAVA_OPTS="-Xms512m -Xmx1024m -server"

# Start the application in background
cd $APP_DIR
nohup java -jar $JAR_FILE > /dev/null 2>&1 &

# Save PID
echo $! > $PID_FILE

# Wait a moment and check if the process started successfully
sleep 5
if ps -p $(cat $PID_FILE) > /dev/null 2>&1; then
    echo "Application started successfully with PID: $(cat $PID_FILE)"
else
    echo "ERROR: Failed to start application"
    cat $LOG_DIR/startup.log
    exit 1
fi

echo "Application startup completed"