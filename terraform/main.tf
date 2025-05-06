
# Variable for storing the Oracle Cloud instance IP
variable "oracle_instance_ip" {
  description = "Oracle Cloud instance IP"
  type        = string
  default     = "89.168.44.86"
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

# Record A for API (to Oracle Cloud)
resource "aws_route53_record" "api" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = "api.${var.ses_domain_name}"
  type    = "A"
  ttl     = "300"
  records = [var.oracle_instance_ip]
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

# Record for SES verification
resource "aws_route53_record" "ses_verification" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = "_amazonses.${var.ses_domain_name}"
  type    = "TXT"
  ttl     = "600"
  records = [aws_ses_domain_identity.artventuria.verification_token]
}

# Record MX for Gmail
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

# Record MX for custom MAIL FROM domain
resource "aws_route53_record" "mail_from_mx" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = "mail.${var.ses_domain_name}"
  type    = "MX"
  ttl     = "600"
  records = ["10 feedback-smtp.${var.region}.amazonses.com"]
}

# Record SPF for Gmail and SES
resource "aws_route53_record" "spf" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = var.ses_domain_name
  type    = "TXT"
  ttl     = "3600"
  records = ["v=spf1 include:_spf.google.com include:amazonses.com ~all"]
}

# Record DMARC
resource "aws_route53_record" "dmarc" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = "_dmarc.${var.ses_domain_name}"
  type    = "TXT"
  ttl     = 300
  records = ["v=DMARC1; p=none;"]
}

# Record A for web server (pointing to Oracle Cloud)
resource "aws_route53_record" "www" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = "www.${var.ses_domain_name}"
  type    = "A"
  ttl     = "300"
  records = [var.oracle_instance_ip]
}

# Record A for root domain (pointing to Oracle Cloud)
resource "aws_route53_record" "root" {
  zone_id = aws_route53_zone.primary.zone_id
  name    = var.ses_domain_name
  type    = "A"
  ttl     = "300"
  records = [var.oracle_instance_ip]
}
