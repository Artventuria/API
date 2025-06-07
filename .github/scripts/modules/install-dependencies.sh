#!/bin/bash

#################################################
# MODULE: INSTALL DEPENDENCIES
# This module installs Nginx, Certbot, and Docker if needed
#################################################

# Install nginx if not available
if ! command -v nginx &> /dev/null; then
  echo "Installing Nginx..."
  sudo dnf update -y
  sudo dnf install -y nginx
  echo "Nginx installation completed."
fi

# Install Certbot if needed
if ! command -v certbot &> /dev/null; then
  echo "Installing Certbot..."
  # Oracle Linux needs specific repos for certbot
  sudo dnf install -y oracle-epel-release-el8
  sudo dnf install -y python3-pip
  sudo pip3 install certbot certbot-nginx
  sudo ln -sf /usr/local/bin/certbot /usr/bin/certbot
  echo "Certbot installation completed."
fi

# Install Docker if not already installed
if ! command -v docker &> /dev/null; then
  echo "Installing Docker..."
  sudo dnf config-manager --add-repo=https://download.docker.com/linux/centos/docker-ce.repo
  sudo dnf install -y docker-ce docker-ce-cli containerd.io
  sudo systemctl enable docker
  sudo systemctl start docker
  echo "Docker installation completed."
fi

# Permissions for Docker socket
sudo chmod 666 /var/run/docker.sock

# Install PostgreSQL unaccent extension if psql and DB credentials are available
if command -v psql &> /dev/null && [[ -n "$DATABASE_NAME" && -n "$DATABASE_USER" ]]; then
  echo "Ensuring PostgreSQL 'unaccent' extension is enabled..."
  PGPASSWORD=${DATABASE_PASSWORD:-postgres} psql -h "${DATABASE_HOST:-localhost}" -U "$DATABASE_USER" -d "$DATABASE_NAME" -c "CREATE EXTENSION IF NOT EXISTS unaccent;" || echo "[WARN] Could not enable unaccent extension (check permissions or connection)."
else
  echo "[INFO] Skipping unaccent extension setup (psql or DB credentials not available)."
fi

echo "All required dependencies installed and configured."
