#!/bin/bash

#################################################
# MODULE: DOCKER DEPLOYMENT
# This module handles Docker login and deployment of containers
#################################################

# Parameters
DOCKER_USER=${1:-"${DOCKER_USERNAME}"}
DOCKER_PASS=${2:-"${DOCKER_PASSWORD}"}
COMPOSE_FILE=${3:-"docker-compose.prod.yml"}

# Log into Docker Hub
echo "Logging into Docker Hub..."
echo "${DOCKER_PASS}" | docker login -u "${DOCKER_USER}" --password-stdin

if [ $? -ne 0 ]; then
  echo "Failed to log into Docker Hub."
  return 1
fi

# Deploy the Docker container
echo "Deploying containers with Docker Compose..."

# Stop and remove existing docker-compose services
docker-compose -f ${COMPOSE_FILE} down || true

# Force remove any stuck containers with the name artventuria-api
docker rm -f artventuria-api || true

# Pull latest images
docker-compose -f ${COMPOSE_FILE} pull

# Start containers
docker-compose -f ${COMPOSE_FILE} up -d

if [ $? -eq 0 ]; then
  echo "Docker containers deployed successfully."
  return 0
else
  echo "Failed to deploy Docker containers."
  return 1
fi
