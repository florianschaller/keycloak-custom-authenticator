terraform {
  required_version = ">= 1.4.2"

  required_providers {
    keycloak = {
      source  = "mrparkers/keycloak"
      version = "4.4.0"
    }
  }
}

variable "keycloak_username" {
  type = string
}
variable "keycloak_password" {
  type = string
}
variable "keycloak_host" {
  type = string
}

provider "keycloak" {
  client_timeout = 120
  client_id      = "admin-cli"
  username       = var.keycloak_username
  password       = var.keycloak_password
  url            = var.keycloak_host
}
