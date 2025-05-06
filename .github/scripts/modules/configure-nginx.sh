#!/bin/bash

#################################################
# MODULE: NGINX CONFIGURATION
# This module handles Nginx configuration for HTTP and HTTPS
#################################################

# Parameters
DOMAIN=${1:-"api.artventuria.com"}
IS_SSL_ENABLED=${2:-false}
NGINX_HTTP_CONF=${3:-"nginx-http.conf"}
NGINX_SSL_CONF=${4:-"nginx-ssl.conf"}

# Configure Nginx
echo "Configuring Nginx for ${DOMAIN}..."

# Create Nginx directory if it doesn't exist
sudo mkdir -p /etc/nginx/conf.d

# Set the appropriate configuration based on SSL status
if [ "${IS_SSL_ENABLED}" = true ] && [ -f "/etc/letsencrypt/live/${DOMAIN}/fullchain.pem" ]; then
  echo "Using SSL configuration for Nginx."
  sudo cp ${NGINX_SSL_CONF} /etc/nginx/conf.d/${DOMAIN}.conf
else
  echo "Using HTTP configuration for Nginx."
  sudo cp ${NGINX_HTTP_CONF} /etc/nginx/conf.d/${DOMAIN}.conf
fi

# Restart Nginx to apply changes
sudo systemctl restart nginx

if [ $? -eq 0 ]; then
  echo "Nginx configuration applied successfully."
  return 0
else
  echo "Failed to apply Nginx configuration."
  return 1
fi
