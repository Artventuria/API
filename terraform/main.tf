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
