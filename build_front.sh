#!/usr/bin/env bash

set -e  # exit immediately if a command exits with a non-zero status
set -x  # print all executed commands on terminal

export $(grep -v '^#' .env | xargs)

docker build -f ./front/.Dockerfile -t 768305773658.dkr.ecr.us-east-1.amazonaws.com/ucu-class/front:$STUDENT_NAME-0.1 ./front