variable "git_pat" {
  type      = string
  sensitive = true
  description = "GitHub Personal Access Token"
}

variable "ssh_key_file"{ 
  type = string
  description = "Path to the SSH public key file"
}