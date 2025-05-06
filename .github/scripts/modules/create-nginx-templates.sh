#!/bin/bash

#################################################
# MODULE: NGINX TEMPLATES CREATION
# This module creates the Nginx configuration templates
#################################################

# Parameters
DOMAIN=${1:-"api.artventuria.com"}
PROXY_PORT=${2:-"8001"}
OUTPUT_DIR=${3:-"."}

# Function to create HTTP configuration file
create_http_config() {
  local config_file="${OUTPUT_DIR}/nginx-http.conf"
  
  echo "Creating HTTP configuration for ${DOMAIN}..."
  
  cat > "${config_file}" << EOL
server {
    listen 80;
    server_name ${DOMAIN};
    location / {
        proxy_pass http://localhost:${PROXY_PORT};
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOL

  if [ -f "${config_file}" ]; then
    echo "HTTP configuration created successfully at ${config_file}"
    return 0
  else
    echo "Failed to create HTTP configuration"
    return 1
  fi
}

# Function to create HTTPS/SSL configuration file
create_ssl_config() {
  local config_file="${OUTPUT_DIR}/nginx-ssl.conf"
  
  echo "Creating SSL configuration for ${DOMAIN}..."
  
  cat > "${config_file}" << EOL
server {
    listen 80;
    server_name ${DOMAIN};
    return 301 https://\$server_name\$request_uri;
}
server {
    listen 443 ssl;
    server_name ${DOMAIN};
    ssl_certificate /etc/letsencrypt/live/${DOMAIN}/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/${DOMAIN}/privkey.pem;
    location / {
        proxy_pass http://localhost:${PROXY_PORT};
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOL

  if [ -f "${config_file}" ]; then
    echo "SSL configuration created successfully at ${config_file}"
    return 0
  else
    echo "Failed to create SSL configuration"
    return 1
  fi
}

# Create both configurations
create_http_config
create_ssl_config

echo "Nginx configuration templates created successfully."
