# Outputs pour les informations importantes
output "instance_id" {
  description = "OCID de l'instance Artventuria"
  value       = oci_core_instance.artventuria_instance.id
}

output "public_ip" {
  description = "Adresse IP publique de l'instance Artventuria"
  value       = oci_core_instance.artventuria_instance.public_ip
}

output "instance_state" {
  description = "État de l'instance Artventuria"
  value       = oci_core_instance.artventuria_instance.state
}

output "ssh_connection" {
  description = "Command to connect to the instance via SSH"
  value       = "ssh opc@${oci_core_instance.artventuria_instance.public_ip}"
}

output "api_url" {
  description = "URL of the Artventuria API"
  value       = "https://api.${var.ses_domain_name}"
}
