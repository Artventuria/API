#!/bin/bash

#################################################
# MODULE: SELINUX CONFIGURATION
# This module configures SELinux to allow Nginx to connect to custom ports
#################################################

echo "Configuring SELinux for Nginx..."

# Allow Nginx to connect to network ports
sudo setsebool -P httpd_can_network_connect 1

echo "SELinux configuration for Nginx completed successfully."
