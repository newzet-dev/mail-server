#!/bin/bash
set -e

cd /home/ubuntu/app

aws s3 cp s3://newzet-config/.env .env
echo "IMAGE_TAG=$1" >> .env
echo "APP_VERSION=$2" >> .env

PROJECT_NAME="newzet"
docker-compose -p $PROJECT_NAME -f docker-compose.yml stop spring-server --timeout 60 || true
docker-compose -p $PROJECT_NAME -f docker-compose.yml --env-file .env up -d

timeout 60s bash -c "until curl -s http://localhost:8080/actuator/health | grep -q '\"status\":\"UP\"'; do sleep 5; echo 'Waiting for health check...'; done"
if [ $? -ne 0 ]; then
  echo "Health check failed"
  docker-compose -p $PROJECT_NAME -f docker-compose.yml stop spring-server
  exit 1
fi

rm -f .env
