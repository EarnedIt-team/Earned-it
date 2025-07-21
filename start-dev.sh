#!/bin/bash
./gradlew bootJar
docker compose -f docker-compose.dev.yml up -d --build
