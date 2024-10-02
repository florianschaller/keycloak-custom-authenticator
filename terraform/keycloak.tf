resource "keycloak_realm" "hive" {
  realm                          = "Hive"
  display_name                   = "Hive"
  enabled                        = true
  registration_email_as_username = false
  login_with_email_allowed       = false
  duplicate_emails_allowed       = true
  reset_password_allowed         = false
  edit_username_allowed          = false
  registration_allowed           = false
  verify_email                   = false
  remember_me                    = true
  # login_theme                  = "hive-admin"
}

resource "keycloak_openid_client" "hive-suite-frontend" {
  name                  = "Hive Suite Frontend"
  access_type           = "PUBLIC"
  client_id             = "hive-suite-frontend"
  realm_id              = keycloak_realm.hive.id
  client_secret         = "top-secret-front-client-secret"
  standard_flow_enabled = true
  valid_redirect_uris   = [
    "*",
  ]
  web_origins = [
    "*",
  ]
}

resource "keycloak_user" "user1" {
  realm_id = keycloak_realm.hive.id
  username = "user1"
  email    = "user1@example.com"
  enabled  = true
  initial_password {
    value     = "password123"
    temporary = false
  }
}