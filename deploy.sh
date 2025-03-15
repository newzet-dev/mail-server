#!/bin/bash
set -e

cd /home/ubuntu/app

docker-compose -f docker-compose.yml down || true

aws s3 cp s3://${AWS_S3_CONFIG_BUCKET}/.env .env
echo "IMAGE_TAG=$1" >> .env

docker-compose -f docker-compose.yml up -d

timeout 60s bash -c "until curl -s http://localhost:8080/actuator/health | grep -q '\"status\":\"UP\"'; do sleep 5; echo 'Waiting for health check...'; done"
if [ $? -ne 0 ]; then
  echo "Health check failed"
  docker-compose -f docker-compose.yml down
  exit 1
fi

rm -f .env
