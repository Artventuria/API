#!/bin/bash

#################################################
# MODULE: CRON JOBS CONFIGURATION
# This module sets up automated tasks like SSL certificate renewal
#################################################

# Parameters
DOMAIN=${1:-"api.artventuria.com"}
AWS_ACCESS_KEY=${2:-"${AWS_ACCESS_KEY_ID}"}
AWS_SECRET_KEY=${3:-"${AWS_SECRET_ACCESS_KEY}"}

echo "Setting up automatic certificate renewal..."

# Create renewal script with AWS credentials 
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

# Create a cron file directly in /etc/cron.d (same as manual setup)
echo "30 2 * * 1 /home/opc/certbot-renew.sh >> /var/log/certbot-renewal.log 2>&1" | sudo tee /etc/cron.d/certbot-renewal
sudo chmod 644 /etc/cron.d/certbot-renewal

echo "Automatic renewal setup complete. Certificates will be checked weekly."
