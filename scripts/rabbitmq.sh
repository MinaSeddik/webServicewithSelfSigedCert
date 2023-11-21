#!/bin/bash


sudo service rabbitmq-server restart

# to peacefully and safely shut down rabbitmq
sudo rabbitmqctl stop



rabbitmqadmin -u guest -p guest declare exchange name=my_commandline-direct type=direct

# Create Exchange:
rabbitmqadmin -u {user} -p {password} -V {vhost} declare exchange name={name} type={type}

# Create Queue:
rabbitmqadmin -u {user} -p {password} -V {vhost} declare queue name={name}

# Bind Queue to Exchange:
rabbitmqadmin -u {user} -p {password} -V {vhost} declare binding source={Exchange} destination={queue}

# To list vhosts
rabbitmqctl list_vhosts

#To create a vhost simply run
rabbitmqctl add_vhost [vhost_name]

#Deleting a vhost is similarly simple:
rabbitmqctl delete_vhost [vhost_name]

# list queues
rabbitmqctl list_queues
rabbitmqctl list_queues -p [vhost_name]
rabbitmqctl list_queues name messages consumers memory
rabbitmqctl list_queues name durable auto_delete


# list exchanges
rabbitmqctl list_exchanges
rabbitmqctl list_exchanges name type durable auto_delete

# list bindings
rabbitmqctl list_bindings



# configure rabbitmq cluster
##############################
sudo rabbitmqctl stop
sudo service rabbitmq-server stop

#to start rabbitmq server as a single node
sudo rabbitmq-server

# don't forget to disable all plugins

# login as root
su -

RABBITMQ_NODE_PORT=5672
RABBITMQ_NODENAME=rabbit
rabbitmq-server -detached

RABBITMQ_NODE_PORT=5673
RABBITMQ_NODENAME=rabbit_1
rabbitmq-server -detached

RABBITMQ_NODE_PORT=5674
RABBITMQ_NODENAME=rabbit_2
rabbitmq-server -detached

# stop and reset metadata of rabbit_1
rabbitmqctl -n rabbit_1@mina-server stop_app
rabbitmqctl -n rabbit_1@mina-server reset

# you’re ready to join it to the first cluster node
rabbitmqctl -n rabbit_1@mina-server join_cluster rabbit@mina-server

# start rabbit_1 again
rabbitmqctl -n rabbit_1@mina-server start_app


# stop and reset metadata of rabbit_2
rabbitmqctl -n rabbit_2@mina-server stop_app
rabbitmqctl -n rabbit_2@mina-server reset

# you’re ready to join it to the first cluster node
rabbitmqctl -n rabbit_2@mina-server join_cluster rabbit@mina-server

# start rabbit_1 again
rabbitmqctl -n rabbit_2@mina-server start_app

# to get cluster status
rabbitmqctl cluster_status





