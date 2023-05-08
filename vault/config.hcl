disable_mlock = true
disable_cache = true
ui            = true

max_lease_ttl     = "7200h"
default_lease_ttl = "7200h"

#log_level         = "Debug"
log_level = "INFO"

storage "file" {
  path = "./vault/data"
}

listener "tcp" {
  address     = "0.0.0.0:8200"
  tls_disable = "true"
}

#listener "tcp" {
#  address                  = "0.0.0.0:8200"
#  tls_disable              = 0
#  tls_cert_file            = "/opt/vault/tls/vault-cert.crt"
#  tls_key_file             = "/opt/vault/tls/vault-key.key"
#  tls_client_ca_file       = "/opt/vault/tls/vault-ca.crt"
#  tls_disable_client_certs = "true"
#}

# Creating policies

path "secret/data/*" {
  # here are all possible capabilities
  capabilities = ["create", "update", "delete", "read", "list", "deny", "sudo"]
}

path "secret/data/foo" {
  capabilities = ["read"]
}

path "secret/*" {
  capabilities = ["read"]
}