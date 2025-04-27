variable "region" {
  default = "eu-west-3"
}

variable "instance_type" {
  default = "t2.micro"
}

variable "key_name" {
  description = "Name of the SSH key to access the EC2 server"
}

variable "docker_username" {
  description = "Docker Hub username for CI/CD"
}

variable "docker_password" {
  description = "Docker Hub password or token for CI/CD"
  sensitive   = true
}

variable "ses_domain_name" {
  description = "Domain name for Amazon SES"
  type        = string
  default     = "artventuria.com"
}

variable "ses_sender_email" {
  description = "Sender email for Amazon SES"
  type        = string
}
