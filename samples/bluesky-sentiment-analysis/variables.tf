variable "gcp-project" {
  type      = string
  sensitive = true
  description = "The GCP Project to deploy the app to"
}

variable "gcp-region" {
  type      = string
  sensitive = true
  description = "The GCP Region to deploy the app to"
  
}

variable "gcp-zone" {
  type      = string
  sensitive = true
  description = "The GCP Zone to deploy the app to"
  
}

variable "dns-zone-name" {
  type      = string
  sensitive = true
  description = "The DNS Zone to deploy the app to"  
}

variable "subdomain" {
  type      = string
  sensitive = true
  description = "The subdomain to deploy the app to"
  
}