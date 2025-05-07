#!/bin/bash

#################################################
# ARTVENTURIA API DEPLOYMENT SCRIPT (OCI)
# Main orchestration script that uses modular components
#################################################

set -e

# Base paths and configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MODULES_DIR="${SCRIPT_DIR}/modules"
DOMAIN="api.artventuria.com"

echo "Starting deployment for ${DOMAIN}..."

#################################################
# SECTION: NGINX CONFIGURATION TEMPLATES
#################################################
echo "Creating Nginx configuration templates..."

# Use the module to create Nginx configuration templates
chmod +x ${MODULES_DIR}/create-nginx-templates.sh
source ${MODULES_DIR}/create-nginx-templates.sh "${DOMAIN}" "8001" "."

#################################################
# SECTION: INITIAL CONNECTION AND FILE TRANSFER
#################################################
echo "Establishing connection and transferring files..."

# Initiate ControlMaster with a simple SSH connection
ssh oci "echo Connected"

# Copy the docker-compose file, nginx configurations, and modules to the server
ssh oci "mkdir -p ~/artventuria-deploy/modules"
scp docker-compose.prod.yml nginx-http.conf nginx-ssl.conf oci:~/artventuria-deploy/
scp ${MODULES_DIR}/* oci:~/artventuria-deploy/modules/

#################################################
# SECTION: SERVER DEPLOYMENT EXECUTION
#################################################
echo "Executing deployment on server..."

ssh oci << EOSSH
  set -e
  cd ~/artventuria-deploy
  
  # Make all module scripts executable
  chmod +x modules/*.sh
  
  #################################################
  # SUBSECTION: ENVIRONMENT SETUP
  #################################################
  echo "Setting up environment variables..."
  
  # Create an environment file with sensitive variables
  cat > .env << EOL
DOCKER_USERNAME=$DOCKER_USERNAME
GITHUB_REF_NAME=$GITHUB_REF_NAME
DATABASE_PASSWORD=$DATABASE_PASSWORD
JWT_SECRET=$JWT_SECRET
FIREBASE_CONFIG_PATH=$FIREBASE_CONFIG_PATH
FIREBASE_PROJECT_ID=$FIREBASE_PROJECT_ID
AWS_SES_REGION=$AWS_SES_REGION
AWS_SES_FROM_EMAIL=$AWS_SES_FROM_EMAIL
FRONTEND_URL=$FRONTEND_URL
CORS_ALLOWED_ORIGINS=$CORS_ALLOWED_ORIGINS
EOL

  #################################################
  # SUBSECTION: DEPENDENCIES INSTALLATION
  #################################################
  echo "Installing required dependencies..."
  source modules/install-dependencies.sh
  
  #################################################
  # SUBSECTION: NGINX CONFIGURATION
  #################################################
  echo "Configuring Nginx (initial HTTP setup)..."
  source modules/configure-nginx.sh "${DOMAIN}" false "nginx-http.conf" "nginx-ssl.conf"
  
  #################################################
  # SUBSECTION: SSL CERTIFICATE CONFIGURATION
  #################################################
  echo "Setting up complete SSL process..."
  source modules/setup-ssl.sh "${DOMAIN}" "$CERTBOT_EMAIL" "$AWS_ACCESS_KEY_ID" "$AWS_SECRET_ACCESS_KEY" "nginx-http.conf" "nginx-ssl.conf"
  
  #################################################
  # SUBSECTION: LOGS DIRECTORY SETUP
  #################################################
  echo "Creating directory for application logs..."
  sudo mkdir -p /var/log/artventuria
  sudo chmod 777 /var/log/artventuria
  
  #################################################
  # SUBSECTION: DOCKER DEPLOYMENT
  #################################################
  echo "Deploying Docker containers..."
  source modules/setup-docker.sh "$DOCKER_USERNAME" "$DOCKER_PASSWORD" "docker-compose.prod.yml"
  
  #################################################
  # SUBSECTION: SYSTEMD SERVICE CONFIGURATION
  #################################################
  echo "Configuring systemd service..."
  source modules/configure-systemd.sh "artventuria-api" "/home/opc/artventuria-deploy" "docker-compose.prod.yml"
EOSSH
