# Instance Compute Oracle Cloud (ARM Ampere)
resource "oci_core_instance" "artventuria_instance" {
  availability_domain = var.availability_domain
  compartment_id      = var.compartment_id
  display_name        = "artventuria-api-server"
  shape               = var.instance_shape

  shape_config {
    ocpus         = var.instance_ocpus
    memory_in_gbs = var.instance_memory_in_gbs
  }

  create_vnic_details {
    subnet_id        = var.subnet_id
    assign_public_ip = true
  }

  source_details {
    source_type = "image"
    source_id   = var.image_id
  }

  metadata = {
    ssh_authorized_keys = var.ssh_public_key
  }

  # Inizialiation script to install Docker and Docker Compose
  extended_metadata = {
    user_data = base64encode(<<-EOF
      #!/bin/bash
      # Update the system
      dnf update -y
      
      # Install Docker
      dnf config-manager --add-repo=https://download.docker.com/linux/centos/docker-ce.repo
      dnf install -y docker-ce docker-ce-cli containerd.io
      systemctl enable docker
      systemctl start docker
      
      # Install Docker Compose
      curl -L "https://github.com/docker/compose/releases/download/v2.17.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
      chmod +x /usr/local/bin/docker-compose
      
      # Install Nginx
      dnf install -y nginx
      systemctl enable nginx
      systemctl start nginx
      
      # Create the deployment directory
      mkdir -p /home/opc/artventuria-deploy
      chown opc:opc /home/opc/artventuria-deploy
    EOF
    )
  }
}

# Règles de Sécurité (équivalent Security Group)
resource "oci_core_network_security_group" "artventuria_nsg" {
  compartment_id = var.compartment_id
  vcn_id         = data.oci_core_vcn.existing_vcn.id
  display_name   = "artventuria-security-group"
}

# Règle pour SSH (port 22)
resource "oci_core_network_security_group_security_rule" "allow_ssh" {
  network_security_group_id = oci_core_network_security_group.artventuria_nsg.id
  direction                 = "INGRESS"
  protocol                  = "6" # TCP
  source                    = "0.0.0.0/0"
  source_type               = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      min = 22
      max = 22
    }
  }
}

# Règle pour HTTP (port 80)
resource "oci_core_network_security_group_security_rule" "allow_http" {
  network_security_group_id = oci_core_network_security_group.artventuria_nsg.id
  direction                 = "INGRESS"
  protocol                  = "6" # TCP
  source                    = "0.0.0.0/0"
  source_type               = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      min = 80
      max = 80
    }
  }
}

# Règle pour HTTPS (port 443)
resource "oci_core_network_security_group_security_rule" "allow_https" {
  network_security_group_id = oci_core_network_security_group.artventuria_nsg.id
  direction                 = "INGRESS"
  protocol                  = "6" # TCP
  source                    = "0.0.0.0/0"
  source_type               = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      min = 443
      max = 443
    }
  }
}

# Référence au VCN existant
data "oci_core_vcn" "existing_vcn" {
  compartment_id = var.compartment_id
  # Vous devrez connaître l'OCID du VCN ou son display_name
  # display_name = "votre-vcn-name"
  # Alternativement, vous pouvez créer un nouveau VCN
}

# Mise à jour des enregistrements DNS sur AWS Route53
data "aws_route53_zone" "artventuria" {
  name         = var.ses_domain_name
  private_zone = false
  provider     = aws.primary
}

# Mise à jour de l'enregistrement A pour l'API
resource "aws_route53_record" "api" {
  zone_id  = data.aws_route53_zone.artventuria.zone_id
  name     = "api.${var.ses_domain_name}"
  type     = "A"
  ttl      = "300"
  records  = [oci_core_instance.artventuria_instance.public_ip]
  provider = aws.primary
}

# Garder SES et autres configurations Route53 existantes
# Nous allons simplement mettre à jour l'adresse IP pour pointer vers Oracle Cloud
