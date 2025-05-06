#!/bin/bash

#################################################
# MODULE: COMPLETE SSL SETUP
# This module handles the entire SSL configuration process:
# - Obtaining certificates via Let's Encrypt with DNS-01 (Route53)
# - Configuring Nginx to use SSL certificates
# - Setting up SELinux permissions for Nginx
# - Creating automatic renewal cron jobs
#################################################

# Parameters
DOMAIN=${1:-"api.artventuria.com"}
EMAIL_VAR=${2:-"${CERTBOT_EMAIL}"}
AWS_ACCESS_KEY=${3:-"${AWS_ACCESS_KEY_ID}"}
AWS_SECRET_KEY=${4:-"${AWS_SECRET_ACCESS_KEY}"}
HTTP_CONF=${5:-"nginx-http.conf"}
SSL_CONF=${6:-"nginx-ssl.conf"}

echo "Starting complete SSL setup for ${DOMAIN}..."

#################################################
# PART 1: OBTAIN SSL CERTIFICATES
#################################################
obtain_certificates() {
  # Check if certificates already exist - using sudo to handle permissions
  if sudo [ -f "/etc/letsencrypt/live/${DOMAIN}/fullchain.pem" ]; then
    echo "SSL certificates for ${DOMAIN} already exist."
    return 0
  fi

  echo "Obtaining SSL certificates for ${DOMAIN}..."
  
  # Prepare the email argument
  EMAIL_ARG=""
  if [ -n "${EMAIL_VAR}" ]; then
    EMAIL_ARG="-m ${EMAIL_VAR}"
  else
    EMAIL_ARG="--register-unsafely-without-email"
  fi
  
  # Run certbot with DNS validation (Route53)
  echo "Running Docker-based certbot with Route53 DNS validation..."
  DOCKER_CMD="sudo docker run --rm \
    -v \"/etc/letsencrypt:/etc/letsencrypt\" \
    -v \"/var/lib/letsencrypt:/var/lib/letsencrypt\" \
    -e \"AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY}\" \
    -e \"AWS_SECRET_ACCESS_KEY=${AWS_SECRET_KEY}\" \
    certbot/dns-route53 certonly --authenticator dns-route53 --installer none \
    -d ${DOMAIN} --non-interactive --agree-tos ${EMAIL_ARG}"
  
  echo "Executing: $DOCKER_CMD"
  eval $DOCKER_CMD
  CERT_STATUS=$?
  
  if [ $CERT_STATUS -ne 0 ]; then
    echo "Warning: Certbot command exited with status $CERT_STATUS - this might be OK if certificates already exist."
  fi
  
  # Even if certbot reports "Certificate not yet due for renewal",
  # we consider this a success since it means certificates exist
  
  # Final verification that certificates exist - using sudo for permission issues
  if sudo [ -f "/etc/letsencrypt/live/${DOMAIN}/fullchain.pem" ]; then
    echo "SSL certificates verified."
    return 0
  else
    echo "SSL certificates not found after execution."
    return 1
  fi
}

#################################################
# PART 2: CONFIGURE NGINX WITH SSL
#################################################
configure_nginx_ssl() {
  echo "Configuring Nginx with SSL for ${DOMAIN}..."
  
  # Create Nginx directory if it doesn't exist
  sudo mkdir -p /etc/nginx/conf.d
  
  # Configure Nginx with SSL
  sudo cp ${SSL_CONF} /etc/nginx/conf.d/${DOMAIN}.conf
  
  # Restart Nginx to apply changes
  sudo systemctl restart nginx
  
  if [ $? -eq 0 ]; then
    echo "Nginx configured successfully with SSL."
    return 0
  else
    echo "Failed to configure Nginx with SSL."
    return 1
  fi
}

#################################################
# PART 3: CONFIGURE SELINUX FOR NGINX
#################################################
configure_selinux() {
  echo "Configuring SELinux to allow Nginx connections..."
  
  # Allow Nginx to connect to network ports
  sudo setsebool -P httpd_can_network_connect 1
  
  echo "SELinux configured successfully for Nginx."
  return 0
}

#################################################
# PART 4: SETUP AUTOMATIC CERTIFICATE RENEWAL
#################################################
setup_renewal() {
  echo "Setting up automatic certificate renewal..."
  
  # Create renewal script
  cat > ~/certbot-renew.sh << EOF
#!/bin/bash
docker run --rm \
  -v "/etc/letsencrypt:/etc/letsencrypt" \
  -v "/var/lib/letsencrypt:/var/lib/letsencrypt" \
  -e "AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY}" \
  -e "AWS_SECRET_ACCESS_KEY=${AWS_SECRET_KEY}" \
  certbot/dns-route53 renew --non-interactive
if [ \$? -eq 0 ]; then
  # Only restart nginx if certificates were actually renewed
  sudo systemctl reload nginx
fi
EOF
  
  # Make the script executable
  chmod +x ~/certbot-renew.sh
  
  # Create a cron file for weekly renewal
  echo "30 2 * * 1 /home/opc/certbot-renew.sh >> /var/log/certbot-renewal.log 2>&1" | sudo tee /etc/cron.d/certbot-renewal
  sudo chmod 644 /etc/cron.d/certbot-renewal
  
  echo "Automatic certificate renewal configured successfully."
  return 0
}

#################################################
# MAIN EXECUTION
#################################################

# Step 1: Attempt to obtain or verify SSL certificates
obtain_certificates
CERT_RESULT=$?

# If certificates exist or were successfully obtained - using sudo to check
if sudo [ -f "/etc/letsencrypt/live/${DOMAIN}/fullchain.pem" ]; then
  # Step 2: Configure Nginx to use SSL
  configure_nginx_ssl
  
  # Step 3: Configure SELinux
  configure_selinux
  
  # Step 4: Setup automatic renewal
  setup_renewal
  
  echo "Complete SSL setup finished successfully for ${DOMAIN}."
  return 0
else
  # Something went wrong - certificates weren't found
  echo "SSL certificates were not found or could not be obtained. Setup incomplete."
  return 1
fi
