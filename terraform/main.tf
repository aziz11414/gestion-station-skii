# Configuration Terraform pour la gestion de l'infrastructure AWS EKS
# Module AWI 2 / DevOps - Seance 11

resource "aws_security_group" "devops_sg" {
  name        = "gestion-station-skii-sg"
  description = "Security group for Spring Boot Kubernetes Pods"

  ingress {
    description = "HTTP Traffic"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "gestion-station-skii-sg"
    Environment = "DevOps"
  }
}
