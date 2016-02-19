#!/bin/bash

create_db() {
  if [ "$EUID" -ne 0 ]
  then
    printf "Please run as root\n" >&2
    return 1
  fi

  if [ "$#" -lt 2 ]
  then
    printf "Usage: $0 dbname user\n" >&2
    return 1
  fi

  local db_name="$1"
  local user="$2"

  printf "Creating database '$db_name' ...\n"
  su - postgres -c "createdb --owner=\"$user\" \"$db_name\""

  printf "Creating extensions ...\n"
  su - postgres -c "psql --dbname=\"$db_name\" -c \"CREATE EXTENSION IF NOT EXISTS \\\"pgcrypto\\\"; CREATE EXTENSION IF NOT EXISTS \\\"uuid-ossp\\\";\""
}

run_sql_file() {
  if [ "$#" -lt 4 ]
  then
    printf "Usage: $0 db user pw file\n" >&2
    return 1
  fi

  local db_name="$1"
  local user="$2"
  local pw="$3"
  local sql_file="$4"

  local sql=$( readlink -f "$sql_file" )

  printf "Creating tables ...\n"
  PGPASSWORD="$pw" psql --dbname="$db_name" --username="$user" --file="$sql"
}

create_user() {
  if [ "$EUID" -ne 0 ]
  then
    printf "Please run as root\n" >&2
    return 1
  fi

  if [ "$#" -lt 2 ]
  then
    printf "Usage: $0 user pw\n" >&2
    return 1
  fi

  local user="$1"
  local pw="$2"

  printf "Creating user '$user' with password '$pw' ...\n"
  su - postgres -c "psql -c \"CREATE user $user WITH PASSWORD '$pw';\""
}

