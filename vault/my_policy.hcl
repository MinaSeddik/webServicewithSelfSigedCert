
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

