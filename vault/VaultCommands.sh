# start vault server in dev mode
vault server -dev

# start vault server in dev mode with default root token
vault server -dev -dev-root-token-id="00000000-0000-0000-0000-000000000000"

# start vault server in production mode
vault server -config=config.hcl

# To initialize the server and get the tokens to unseal the vault
export VAULT_ADDR='http://127.0.0.1:8200'

#Note that you need to save the Unseal keys and the initial Root token so that you can
#re-initialize the vault after server restarting, otherwise you will lose the data


#Unseal Key 1: Rtz5plXuAqlxto33fe+6+gyiiHJsth48DZR9e65lc+Eq
#Unseal Key 2: 7mGY3SFcWyH039diX47uqLE1pVkBmRuT+6d+aXYBpulA
#Unseal Key 3: 6h19saFr40Ar3tHzAYxid/aEMRpZWhdQKTtnMK2wjRW/
#Unseal Key 4: W64c3t4hnVmGjCHpYe/ZUDzXJEGatZoGS9UAlm/1s2SI
#Unseal Key 5: R2uh5ZbxnWxEuPAcUD0krVQe52kD/izvJQa3IJ7XXc1k
#
#Initial Root Token: hvs.HNC3jOdXqjisRBYQbjCY6nJU


vault operator init

# by default, the server is sealed in the production mode, to unseal it
vault operator unseal

# to login to the server with root token
vault login <root-token>

# you can login with any other token
vault login <token>

# view current logged in token information
vault token lookup


# after running the server, check the status
vault status

# check the mounted engines, You can check your secret engine paths by running
vault secrets list -detailed

#or simply
vault secrets list


# you will find out that secret engine is not mounted by default in production mode
#You can enable secret engine for specific path
vault secrets enable -path=secret kv

#or any other pat like:
vault secrets enable -path=secret kv


# Write a secret to the Key/Value secrets engine
vault kv put secret/my-app key=value

# To retrieve secrets
vault kv get secret/my-app

# To delete specific secret path
vault kv delete secret/my-app

# You can login and authenticate with TOKEN or APPROLE

#list enabled authentication methods
vault auth list

# Create token with token policies = "root"
vault token create

# Create token with token specific policies
vault token create -policy=my-policy -policy=other-policy

# Create token with token specific valid period - 30 minutes
vault token create -period=30m

# yet another token creation example
vault token create --policy=test -period=5000h

# Revoke specific token
vault token revove <token>

# CREATING POLICIES
# ===================

# to list policies
vault policy list

# create a new policy
vault policy write my-policy my_policy.hcl

# to read specific policy
vault policy read my-policy

# to delete a policy
vault policy delete my-policy


# create token with specific policy
vault token create -policy="my-policy"


# authenticate with AppRole METHOD
# =================================
# enable approle auth method
vault auth enable approle

# to make sure it is enabled, list enabled authentication methods
vault auth list

# Create new approle, the syntax is as follow
vault write auth/approle/role/<role_name> token_policies="<policy name>"

# example create an approle called "jenkins" with "jenk_ro" policy
vault write auth/approle/role/jenkins token_policies="jenk_ro" token_ttl=1h token_max_ttl=4h

# to retrieve the approle info that is just created
vault read auth/approle/role/jenkins

# to retrieve the approle ID
vault read auth/approle/role/<approle_name>/role-id

# example
vault read auth/approle/role/jenkins/role-id

# to create approle secret (password) for approle id
vault write -f auth/approle/role/<approle_name>/secret-id

# example
vault write -f auth/approle/role/jenkins/secret-id

# to generate token for this approle using  role-id and secret-id
vault write auth/approle/login role_id="c6cc68c3-2106-1117-9b40-7741562a5a48" secret_id="8f451981-ee02-ad71-f74b-0b5ea8ab992e"

# to login with the generated token above
vault login token=<token>

# or
vault login
# then enter the token in the prompt

# view current logged in token information
vault token lookup

