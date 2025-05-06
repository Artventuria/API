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
  # Check if certificates already exist
  if [ ! -f "/etc/letsencrypt/live/${DOMAIN}/fullchain.pem" ]; then
    echo "Obtaining SSL certificates for ${DOMAIN}..."
    
    # Prepare the email argument
    EMAIL_ARG=""
    if [ -n "${EMAIL_VAR}" ]; then
      EMAIL_ARG="-m ${EMAIL_VAR}"
    else
      EMAIL_ARG="--register-unsafely-without-email"
    fi
    
    # Run certbot with DNS validation (Route53)
    sudo docker run --rm \
      -v "/etc/letsencrypt:/etc/letsencrypt" \
      -v "/var/lib/letsencrypt:/var/lib/letsencrypt" \
      -e "AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY}" \
      -e "AWS_SECRET_ACCESS_KEY=${AWS_SECRET_KEY}" \
      certbot/dns-route53 certonly --authenticator dns-route53 --installer none \
      -d ${DOMAIN} --non-interactive --agree-tos ${EMAIL_ARG}
    
    if [ $? -ne 0 ]; then
      echo "Failed to obtain SSL certificates."
      return 1
    fi
    
    # Verify certificates were created
    if [ ! -f "/etc/letsencrypt/live/${DOMAIN}/fullchain.pem" ]; then
      echo "SSL certificates not found after execution."
      return 1
    fi
    
    echo "SSL certificates obtained successfully."
  else
    echo "SSL certificates for ${DOMAIN} already exist."
  fi
  
  return 0
}

#################################################
# PART 2: CONFIGURE NGINX WITH SSL
#################################################
configure_nginx_ssl() {
  echo "Configuring Nginx with SSL for ${DOMAIN}..."
  
  # Create Nginx directory if it doesn't exist
  sudo mkdir -p /etc/nginx/conf.d
  
  # Configure Nginx with SSL
  sudo cp ~/${SSL_CONF} /etc/nginx/conf.d/${DOMAIN}.conf
  
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

# Step 1: Obtain SSL certificates
obtain_certificates
if [ $? -ne 0 ]; then
  echo "Failed to obtain SSL certificates. Aborting SSL setup."
  return 1
fi

# Check if certificates exist now (either they existed before or were just created)
if [ -f "/etc/letsencrypt/live/${DOMAIN}/fullchain.pem" ]; then
  # Step 2: Configure Nginx to use SSL
  configure_nginx_ssl
  if [ $? -ne 0 ]; then
    echo "Warning: Failed to configure Nginx with SSL."
  fi
  
  # Step 3: Configure SELinux
  configure_selinux
  
  # Step 4: Setup automatic renewal
  setup_renewal
  
  echo "Complete SSL setup finished successfully for ${DOMAIN}."
  return 0
else
  echo "SSL certificates were not found. Setup incomplete."
  return 1
fi
