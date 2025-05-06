terraform {
  required_providers {
    oci = {
      source  = "oracle/oci"
      version = ">= 4.0.0"
    }
    # Keep AWS for Route53 and SES
    aws = {
      source  = "hashicorp/aws"
      version = ">= 4.0.0"
    }
  }
}

provider "oci" {
  # Configuration via env variables or ~/.oci/config
  # OCI_TENANCY_OCID, OCI_USER_OCID, OCI_PRIVATE_KEY_PATH, OCI_FINGERPRINT, OCI_REGION
}

provider "aws" {
  region = var.aws_region
  alias  = "primary"
  # AWS credentials via env variables or ~/.aws/credentials
}
