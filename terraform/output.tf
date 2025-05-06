output "oracle_instance_ip" {
  value = var.oracle_instance_ip
  description = "IP Address of the Oracle Cloud instance used for DNS registrations"
}

output "api_domain" {
  value = "api.${var.ses_domain_name}"
  description = "API Artventuria domain"
}
