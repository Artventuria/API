#!/bin/bash

#################################################
# MODULE: SYSTEMD SERVICE CONFIGURATION
# This module sets up a systemd service for the application
#################################################

# Parameters
APP_NAME=${1:-"artventuria-api"}
WORKING_DIR=${2:-"/home/opc/artventuria-deploy"}
COMPOSE_FILE=${3:-"docker-compose.prod.yml"}

echo "Setting up systemd service for ${APP_NAME}..."

# Create the systemd service file
sudo tee /etc/systemd/system/${APP_NAME}.service << EOL
[Unit]
Description=${APP_NAME} Docker Compose
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=${WORKING_DIR}
ExecStart=/usr/local/bin/docker-compose -f ${COMPOSE_FILE} up -d
ExecStop=/usr/local/bin/docker-compose -f ${COMPOSE_FILE} down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
EOL

# Reload systemd configuration
sudo systemctl daemon-reload

# Enable the service to start on boot
sudo systemctl enable ${APP_NAME}.service

echo "Systemd service for ${APP_NAME} has been configured and enabled."
