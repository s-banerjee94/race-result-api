#!/bin/bash
# Validate that the application is running properly

APP_NAME="race-result-api"
PID_FILE="/opt/race-result-api/app.pid"
MAX_ATTEMPTS=30
API_ENDPOINT="http://localhost:8080/api/v1/events"

echo "Validating $APP_NAME service..."

# Check if PID file exists and process is running
if [ ! -f "$PID_FILE" ]; then
    echo "ERROR: PID file not found"
    exit 1
fi

PID=$(cat $PID_FILE)
if ! ps -p $PID > /dev/null 2>&1; then
    echo "ERROR: Process with PID $PID is not running"
    exit 1
fi

echo "Process is running with PID: $PID"

# Wait for application to be ready
echo "Waiting for application to be ready..."
for i in $(seq 1 $MAX_ATTEMPTS); do
    # Check if port 8080 is listening
    if netstat -tln | grep -q ":8080 "; then
        echo "Application is listening on port 8080"
        break
    elif [ $i -eq $MAX_ATTEMPTS ]; then
        echo "ERROR: Application failed to start listening on port 8080 after $MAX_ATTEMPTS attempts"
        echo "Application logs:"
        tail -20 /opt/race-result-api/log/marathon.log || true
        exit 1
    else
        echo "Attempt $i/$MAX_ATTEMPTS: Waiting for application to start..."
        sleep 10
    fi
done

# Test API endpoint (may fail due to authentication, but should return HTTP response)
echo "Testing API endpoint..."
HTTP_STATUS=$(curl -o /dev/null -s -w "%{http_code}" "$API_ENDPOINT" || echo "000")

if [ "$HTTP_STATUS" = "000" ]; then
    echo "ERROR: No HTTP response from API endpoint"
    exit 1
elif [ "$HTTP_STATUS" = "200" ] || [ "$HTTP_STATUS" = "401" ] || [ "$HTTP_STATUS" = "403" ]; then
    echo "API endpoint responding (HTTP $HTTP_STATUS) - Application is healthy"
else
    echo "WARNING: API endpoint returned HTTP $HTTP_STATUS - Check application logs"
fi

echo "Service validation completed successfully"