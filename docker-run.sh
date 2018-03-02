#!/usr/bin/env bash


# Starts all applications. Run 'mvn docker:build' in platform and webapp before this script to create docker images.

# Remove existing containers
docker-compose stop
docker-compose rm -f

## Start new containers

docker-compose up -d
