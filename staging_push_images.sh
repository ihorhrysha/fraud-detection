#!/usr/bin/env bash

sbt docker
# login to ecr registry
$(aws ecr get-login --region us-east-1 --no-include-email)

sbt dockerPush