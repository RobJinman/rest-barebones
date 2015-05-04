#!/bin/bash

if [ "$EUID" -ne 0 ]
then
  printf "Please run as root\n" >&2
  exit 1
fi

if [ "$#" -lt 1 ]
then
  printf "Usage: $0 dbname\n" >&2
  exit 1
fi

DB_NAME="$1"

su - postgres -c "dropdb \"$DB_NAME\""
su - postgres -c "dropuser rlwebapp"
