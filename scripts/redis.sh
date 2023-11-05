#!/bin/bash


# Restart Redis
sudo systemctl restart redis-server

# start redis client
redis-cli

# Remove everything from Redis
flushall

# Fetch all available keys
KEYS *
# keys starting with letter N
KEYS N*

# Fetch Specific key
GET key_name_to_retrieve

# Remove specific key
DEL key_name_to_remove

# Get the type of specific key
TYPE key_name


# CHECK expiration / TTL of a specific key
# if value = -1, then it has no expiration policy set
TTL key_name

