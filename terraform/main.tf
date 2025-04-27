resource "aws_security_group" "artventuria-sg" {
  name = "artventuria-sg"

  ingress {
    description = "Allow SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Allow HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Allow HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "web_server" {
  ami             = "ami-04a790ca5ad2f097c"  # AMI Amazon Linux 2 AMI (HVM) - Kernel 5.10, SSD Volume Type
  instance_type   = var.instance_type
  key_name        = var.key_name
  security_groups = [aws_security_group.artventuria-sg.name]

  tags = {
    Name = "Artventuria-API-Server"
  }
}

# Configuration SES
resource "aws_ses_domain_identity" "artventuria" {
  domain = var.ses_domain_name
  provider = aws.primary
}

# Virginia region resources removed to simplify configuration

resource "aws_ses_domain_dkim" "artventuria" {
  domain = aws_ses_domain_identity.artventuria.domain
  provider = aws.primary
}

# Virginia region DKIM resources removed to simplify configuration

# Custom MAIL FROM domain configuration
resource "aws_ses_domain_mail_from" "artventuria" {
  domain           = aws_ses_domain_identity.artventuria.domain
  mail_from_domain = "mail.${var.ses_domain_name}"
  provider         = aws.primary
}

# Virginia region MAIL FROM resources removed to simplify configuration

resource "aws_ses_email_identity" "sender" {
  email = var.ses_sender_email
  provider = aws.primary
}

# IAM Role pour SES
resource "aws_iam_role" "ses_role" {
  name = "artventuria-ses-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ses.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy" "ses_policy" {
  name = "artventuria-ses-policy"
  role = aws_iam_role.ses_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ses:SendEmail",
          "ses:SendRawEmail"
        ]
        Resource = "*"
      }
    ]
  })
}

# Configuration Route 53
resource "aws_route53_zone" "primary" {
  name = var.ses_domain_name
}

# Certificat SSL pour le domaine
resource "aws_acm_certificate" "api_cert" {
  domain_name       = "api.${var.ses_domain_name}"
  validation_method = "DNS"
  
  lifecycle {
    create_before_destroy = true
  }
}

# Validation DNS du certificat
resource "aws_route53_record" "api_cert_validation" {
  for_each = {
    for dvo in aws_acm_certificate.api_cert.domain_validation_options : dvo.domain_name => {
      name   = dvo.resource_record_name
      record = dvo.resource_record_value
      type   = dvo.resource_record_type
    }
  }

  allow_overwrite = true
  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = aws_route53_zone.primary.zone_id
}

resource "aws_acm_certificate_validation" "api_cert" {
  certificate_arn         = aws_acm_certificate.api_cert.arn
  validation_record_fqdns = [for record in aws_route53_record.api_cert_validation : record.fqdn]
}

# Enregistrement A pour l'API
resource "aws_route53_record" "api" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = "api.${var.ses_domain_name}"
  type    = "A"
  ttl     = "300"
  records = [aws_instance.web_server.public_ip]
}

# Configuration des enregistrements DKIM pour SES
resource "aws_route53_record" "dkim" {
  count   = 3
  zone_id = aws_route53_zone.primary.zone_id
  name    = "${element(aws_ses_domain_dkim.artventuria.dkim_tokens, count.index)}._domainkey"
  type    = "CNAME"
  ttl     = "600"
  records = ["${element(aws_ses_domain_dkim.artventuria.dkim_tokens, count.index)}.dkim.amazonses.com"]
}

# Enregistrement de vérification SES
resource "aws_route53_record" "ses_verification" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = "_amazonses.${var.ses_domain_name}"
  type    = "TXT"
  ttl     = "600"
  records = [aws_ses_domain_identity.artventuria.verification_token]
}

# Enregistrement MX pour Gmail
resource "aws_route53_record" "mx" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = var.ses_domain_name
  type    = "MX"
  ttl     = "3600"
  records = [
    "1 ASPMX.L.GOOGLE.COM",
    "5 ALT1.ASPMX.L.GOOGLE.COM",
    "5 ALT2.ASPMX.L.GOOGLE.COM",
    "10 ALT3.ASPMX.L.GOOGLE.COM",
    "10 ALT4.ASPMX.L.GOOGLE.COM"
  ]
}

# Enregistrement MX pour le custom MAIL FROM domain
resource "aws_route53_record" "mail_from_mx" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = "mail.${var.ses_domain_name}"
  type    = "MX"
  ttl     = "600"
  records = ["10 feedback-smtp.${var.region}.amazonses.com"]
}

# Enregistrement SPF pour Gmail et SES
resource "aws_route53_record" "spf" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = var.ses_domain_name
  type    = "TXT"
  ttl     = "3600"
  records = ["v=spf1 include:_spf.google.com include:amazonses.com ~all"]
}

# Enregistrement DMARC
resource "aws_route53_record" "dmarc" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = "_dmarc.${var.ses_domain_name}"
  type    = "TXT"
  ttl     = 300
  records = ["v=DMARC1; p=none;"]
}

# Enregistrement A pour le serveur web
resource "aws_route53_record" "www" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = "www.${var.ses_domain_name}"
  type    = "A"
  ttl     = "300"
  records = [aws_instance.web_server.public_ip]
}

# Enregistrement A pour le domaine racine
resource "aws_route53_record" "root" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = var.ses_domain_name
  type    = "A"
  ttl     = "300"
  records = [aws_instance.web_server.public_ip]
}
