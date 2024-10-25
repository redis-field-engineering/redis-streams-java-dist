variable "aws_dns_zone" {
  type      = string
  sensitive = true
  description = "The DNS zone you're going to use for the app"
}

variable "aws_region" {
  type      = string
  sensitive = true
  description = "The AWS region to deploy the app to"
  
}

variable "ssh_key_file"{ 
  type = string
  description = "Path to the SSH public key file"
}