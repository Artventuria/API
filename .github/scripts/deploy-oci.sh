#!/bin/bash
set -e

# Create nginx configuration files locally
cat > nginx-http.conf << 'EOL'
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

cat > nginx-ssl.conf << 'EOL'
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

# Initiate ControlMaster with a simple SSH connection
ssh oci "echo Connected"

# Copy the docker-compose file and nginx configurations to the server
ssh oci "mkdir -p ~/artventuria-deploy"
scp docker-compose.prod.yml nginx-http.conf nginx-ssl.conf oci:~/artventuria-deploy/

# Execute deployment on the server
ssh oci << EOSSH
  set -e
  cd ~/artventuria-deploy
  
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

  # Install nginx if not available
  if ! command -v nginx &> /dev/null; then
    sudo dnf update -y
    sudo dnf install -y nginx
  fi

  # Configure Nginx initial setup (HTTP only)
  sudo mkdir -p /etc/nginx/conf.d
  sudo cp ~/artventuria-deploy/nginx-http.conf /etc/nginx/conf.d/api.artventuria.com.conf
  sudo systemctl restart nginx

  # Certbot SSL configuration - only if certificates don't exist
  if [ ! -f "/etc/letsencrypt/live/api.artventuria.com/fullchain.pem" ]; then
    # Install Certbot if needed
    if ! command -v certbot &> /dev/null; then
      # Oracle Linux needs specific repos for certbot
      sudo dnf install -y oracle-epel-release-el8
      sudo dnf install -y python3-pip
      sudo pip3 install certbot certbot-nginx
      sudo ln -sf /usr/local/bin/certbot /usr/bin/certbot
    fi
    
    # Create virtual environment for Certbot and plugins
    sudo dnf install -y python3-virtualenv
    python3 -m virtualenv ~/certbot-venv
    source ~/certbot-venv/bin/activate
    
    # Install certbot and dns-route53 plugin in the virtual environment
    pip install certbot certbot-dns-route53
    
    # Create AWS credentials file for Route53 access
    mkdir -p ~/.aws
    cat > ~/.aws/credentials << AWSEOF
[default]
aws_access_key_id = ${AWS_ACCESS_KEY_ID}
aws_secret_access_key = ${AWS_SECRET_ACCESS_KEY}
AWSEOF
    chmod 600 ~/.aws/credentials
    
    # Run certbot with the virtual environment for DNS validation
    if [ -n "${CERTBOT_EMAIL}" ]; then
      sudo ~/certbot-venv/bin/certbot certonly --dns-route53 -d api.artventuria.com --non-interactive --agree-tos -m "${CERTBOT_EMAIL}"
    else
      sudo ~/certbot-venv/bin/certbot certonly --dns-route53 -d api.artventuria.com --non-interactive --agree-tos --register-unsafely-without-email
    fi
    
    # Deactivate the virtual environment
    deactivate
    
    # Update to SSL configuration if certificates were created
    if [ -f "/etc/letsencrypt/live/api.artventuria.com/fullchain.pem" ]; then
      sudo cp ~/artventuria-deploy/nginx-ssl.conf /etc/nginx/conf.d/api.artventuria.com.conf
      sudo systemctl restart nginx
    fi
  else
    # Certificates already exist, use SSL config
    sudo cp ~/artventuria-deploy/nginx-ssl.conf /etc/nginx/conf.d/api.artventuria.com.conf
    sudo systemctl restart nginx
  fi

  # Log into Docker Hub
  echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin

  # Deploy the Docker container
  # Stop and remove existing docker-compose services
  docker-compose -f docker-compose.prod.yml down || true
  # Force remove any stuck containers with the name artventuria-api
  docker rm -f artventuria-api || true
  # Pull latest images
  docker-compose -f docker-compose.prod.yml pull
  # Start containers
  docker-compose -f docker-compose.prod.yml up -d

  # Set up systemd service to manage the Docker Compose app
  sudo tee /etc/systemd/system/artventuria-api.service << EOL
[Unit]
Description=Artventuria API Docker Compose
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/home/opc/artventuria-deploy
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
