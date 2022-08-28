#!/bin/bash

# get log level
curl http://localhost:8080/actuator/loggers/com.example.springbootproject

# set log level to TRACE
curl -i -X POST -H 'Content-Type: application/json' -d '{"configuredLevel": "TRACE"}' http://localhost:8080/actuator/loggers/com.example.springbootproject





