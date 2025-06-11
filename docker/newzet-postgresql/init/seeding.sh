#!/bin/bash

set -e

script_root_dir="/docker-entrypoint-initdb.d/init-db"
db_name="$POSTGRES_DB"

db_sqls=(
  "origin.sql"
)

db_sqls_extra=( \
  $( \
    find "$script_root_dir/" -type f -regex ".*changes-[0-9]+\.sql$" | sort \
  ) \
)

echo "--- Seeding PostgreSQL database: $db_name ---"
echo "Extra SQL files to apply: $db_sqls_extra"

sqlExecutePostgres() {
  sqls=("${@}")

  for file_path in "${sqls[@]}"
  do
   echo "- importing: $file_path"
   PGPASSWORD="$POSTGRES_PASSWORD" psql -U "$POSTGRES_USER" -d "$db_name" -f "$file_path"
  done
}

sqlExecutePostgres "$script_root_dir/origin.sql"

if [ ${#db_sqls_extra[@]} -gt 0 ]; then
    sqlExecutePostgres "${db_sqls_extra[@]}"
fi

echo "--- PostgreSQL database seeding complete ---"
