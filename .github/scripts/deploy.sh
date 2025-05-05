#!/bin/bash
set -e

# Initiate ControlMaster with a simple SSH connection
ssh ec2 "echo Connected"

# Copy the docker-compose file to the server
ssh ec2 "mkdir -p ~/artventuria-deploy"
scp docker-compose.prod.yml ec2:~/artventuria-deploy/

# Execute deployment on the server
ssh ec2 << 'EOSSH'
  set -e
  cd ~/artventuria-deploy
  
  # Create an environment file with sensitive variables
  cat > .env << 'EOL'
DOCKER_USERNAME=$DOCKER_USERNAME
GITHUB_REF_NAME=$GITHUB_REF_NAME
DATABASE_PASSWORD=$DATABASE_PASSWORD
JWT_SECRET=$JWT_SECRET
EOL

  # Install docker-compose if not available
  if ! command -v docker compose &> /dev/null; then
    sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
  fi

  # Install nginx if not available
  if ! command -v nginx &> /dev/null; then
    sudo yum update -y
    sudo amazon-linux-extras install nginx1 -y
  fi

  # Configure Nginx initial setup (HTTP only)
  sudo tee /etc/nginx/conf.d/api.artventuria.com.conf << 'EOL'
server {
    listen 80;
    server_name api.artventuria.com;
    location / {
        proxy_pass http://localhost:8001;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOL

  # Restart nginx with HTTP configuration
  sudo systemctl restart nginx

  # Install Certbot and request SSL certificate
  # First enable EPEL repository
  sudo amazon-linux-extras install epel -y
  # Install pip if not already installed
  sudo yum -y install python3-pip
  # Install certbot via pip with compatible dependencies
  # First downgrade urllib3 to a version compatible with the system's OpenSSL
  sudo pip3 install 'urllib3<2.0'
  # Then install certbot
  sudo pip3 install certbot certbot-nginx
  # Create necessary directory if it doesn't exist
  sudo mkdir -p /etc/letsencrypt/live/api.artventuria.com/
  
  if [ ! -f "/etc/letsencrypt/live/api.artventuria.com/fullchain.pem" ]; then
    # Use full path to certbot since pip installs it to /usr/local/bin which may not be in PATH
    # Check if CERTBOT_EMAIL is set and not empty
    if [ -n "${CERTBOT_EMAIL}" ]; then
      sudo /usr/local/bin/certbot --nginx -d api.artventuria.com --non-interactive --agree-tos -m "${CERTBOT_EMAIL}"
    else
      # Run without email argument if it's not set
      sudo /usr/local/bin/certbot --nginx -d api.artventuria.com --non-interactive --agree-tos --register-unsafely-without-email
    fi
  fi

  # Now update Nginx configuration with SSL if certificates were obtained
  if [ -f "/etc/letsencrypt/live/api.artventuria.com/fullchain.pem" ]; then
    sudo tee /etc/nginx/conf.d/api.artventuria.com.conf << 'EOL'
server {
    listen 80;
    server_name api.artventuria.com;
    return 301 https://$server_name$request_uri;
}
server {
    listen 443 ssl;
    server_name api.artventuria.com;
    ssl_certificate /etc/letsencrypt/live/api.artventuria.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.artventuria.com/privkey.pem;
    location / {
        proxy_pass http://localhost:8001;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOL
    sudo systemctl restart nginx  # Restart nginx to apply the SSL config
  fi

  # Log into Docker Hub again
  echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin

  # Pull and deploy the Docker container
  docker compose -f docker-compose.prod.yml down || true
  docker compose -f docker-compose.prod.yml pull
  docker compose -f docker-compose.prod.yml up -d

  # Set up systemd service to manage the Docker Compose app
  sudo tee /etc/systemd/system/artventuria-api.service << 'EOL'
[Unit]
Description=Artventuria API Docker Compose
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/home/ec2-user/artventuria-deploy
ExecStart=/usr/local/bin/docker-compose -f docker-compose.prod.yml up -d
ExecStop=/usr/local/bin/docker-compose -f docker-compose.prod.yml down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
EOL

  # Enable the systemd service to start on boot
  sudo systemctl daemon-reload
  sudo systemctl enable artventuria-api.service
EOSSH
