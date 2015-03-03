#!/bin/bash

if [ "$EUID" -ne 0 ]
then
  printf "Please run as root\n" >&2
  exit 1
fi

case $# in
  1)
    RAND=`date | md5sum`
    PW=${RAND:4:16}
    CREATE_USER=true
    ;;
  2)
    PW="$2"
    CREATE_USER=false
    ;;
  *)
    printf "Usage: $0 dbname [password]\n" >&2
    exit 1
    ;;
esac

DB_NAME="$1"
SCHEMA=rl
USER=rlwebapp

TABLES_SQL=$( readlink -f ./tables.sql )
FUNCTIONS_SQL=$( readlink -f ./functions.sql )
POPULATE_SQL=$( readlink -f ./populate.sql )

if ("$CREATE_USER" = true)
then
  printf "Creating user '$USER' with password '$PW' ...\n"
  su - postgres -c "psql -c \"CREATE USER $USER WITH PASSWORD '$PW';\""
fi

printf "Creating database '$DB_NAME' ...\n"
su - postgres -c "createdb --owner=\"$USER\" \"$DB_NAME\""

printf "Creating extensions ...\n"
su - postgres -c "psql --dbname=\"$DB_NAME\" -c \"CREATE EXTENSION IF NOT EXISTS \\\"pgcrypto\\\";\"CREATE EXTENSION IF NOT EXISTS \\\"uuid-ossp\\\";\""

printf "Creating tables ...\n"
PGPASSWORD="$PW" psql --dbname="$DB_NAME" --username="$USER" --file="$TABLES_SQL"

printf "Creating functions ...\n"
PGPASSWORD="$PW" psql --dbname="$DB_NAME" --username="$USER" --file="$FUNCTIONS_SQL"

printf "Populating ...\n"
PGPASSWORD="$PW" psql --dbname="$DB_NAME" --username="$USER" --file="$POPULATE_SQL"
