#!/bin/bash

echo DB_HOST=$(echo "$DATABASE_URL" | sed -n 's/.*@\(.*\):[0-9]*\/.*/\1/p') >> .env
echo DB_PORT=$(echo "$DATABASE_URL" | sed -n 's/.*:\([0-9]*\)\/.*/\1/p') >> .env
echo DB_USERNAME=$(echo "$DATABASE_URL" | sed -n 's/.*:\/\/\([^:]*\):[^@]*@.*/\1/p') >> .env
echo DB_PASSWORD=$(echo "$DATABASE_URL" | sed -n 's/.*:\(.*\)@.*/\1/p') >> .env
echo DB_DATABASE=$(echo "$DATABASE_URL" | sed -n 's/.*\/\(.*\)/\1/p') >> .env
