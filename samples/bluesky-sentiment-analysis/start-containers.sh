#!/bin/bash

./gradlew clean bootJar
docker compose build
docker compose up --scale consumer=5
