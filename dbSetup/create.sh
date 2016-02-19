#!/bin/bash

output_file=./output.txt

if [ "$EUID" -ne 0 ]
then
  printf "Please run as root\n" >&2
  exit 1
fi

case $# in
  0)
    rand=$(echo -n "$(date) $RANDOM" | md5sum)
    pw=${rand:4:16}
    ;;
  1)
    pw="$1"
    ;;
esac

source ./database.sh

date >> $output_file

create_user rlwebapp "$pw" >> $output_file

setup_db() {
  db_name="$1"

  create_db $db_name rlwebapp >> $output_file

  run_sql_file $db_name rlwebapp "$pw" sql/tables.sql >> $output_file
  run_sql_file $db_name rlwebapp "$pw" sql/functions.sql >> $output_file
  run_sql_file $db_name rlwebapp "$pw" sql/populate.sql >> $output_file
}

setup_db maindb
setup_db testdb

