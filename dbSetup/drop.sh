#!/bin/bash

if [ "$EUID" -ne 0 ]
then
  printf "Please run as root\n" >&2
  exit 1
fi

su - postgres -c "dropdb testdb"
su - postgres -c "dropdb maindb"
su - postgres -c "dropuser rlwebapp"

