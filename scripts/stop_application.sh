#!/bin/bash
# Stop the Spring Boot application

APP_NAME="race-result-api"
PID_FILE="/opt/race-result-api/app.pid"

echo "Stopping $APP_NAME application..."

# Check if PID file exists
if [ -f "$PID_FILE" ]; then
    PID=$(cat $PID_FILE)

    # Check if process is running
    if ps -p $PID > /dev/null 2>&1; then
        echo "Found running process with PID: $PID"
        kill -TERM $PID

        # Wait for graceful shutdown (max 30 seconds)
        for i in {1..30}; do
            if ! ps -p $PID > /dev/null 2>&1; then
                echo "Application stopped gracefully"
                break
            fi
            sleep 1
        done

        # Force kill if still running
        if ps -p $PID > /dev/null 2>&1; then
            echo "Force killing application..."
            kill -9 $PID
        fi
    else
        echo "Process with PID $PID not found"
    fi

    rm -f $PID_FILE
else
    echo "PID file not found"
fi

# Kill any remaining Java processes for this application
pkill -f "race-result-api" || true

echo "Application stop completed"