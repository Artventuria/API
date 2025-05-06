# Variables OCI
variable "compartment_id" {
  description = "Compartment OCID where resources will be created"
  type        = string
}

variable "availability_domain" {
  description = "Availability domain for the VM (ex: 'kryO:EU-FRANKFURT-1-AD-1')"
  type        = string
}

variable "subnet_id" {
  description = "OCID of the subnet where the VM will be deployed"
  type        = string
}

variable "image_id" {
  description = "OCID of the Oracle Linux image for ARM64"
  type        = string
  # Image Oracle Linux 8 pour Ampere (ARM) - Vous devrez la remplacer par l'OCID correct pour votre région
  default     = "ocid1.image.oc1.eu-frankfurt-1.aaaaaaaaqvixfep6clxvr6jdkeqxjnegrq2qpwsakpkiout4voi2rl6goxeq"
}

variable "ssh_public_key" {
  description = "SSH public key for VM access"
  type        = string
}

variable "instance_shape" {
  description = "Instance shape (VM.Standard.A1.Flex for Ampere ARM)"
  type        = string
  default     = "VM.Standard.A1.Flex"
}

variable "instance_ocpus" {
  description = "Number of OCPUs for the instance Flex"
  type        = number
  default     = 2
}

variable "instance_memory_in_gbs" {
  description = "Memory in GB for the instance Flex"
  type        = number
  default     = 12
}

# Variables AWS (for Route53 and SES)
variable "aws_region" {
  description = "AWS region for Route53 and SES"
  type        = string
  default     = "eu-west-3"
}

variable "ses_domain_name" {
  description = "Domain name for Amazon SES"
  type        = string
  default     = "artventuria.com"
}

variable "ses_sender_email" {
  description = "Email sender for Amazon SES"
  type        = string
}


