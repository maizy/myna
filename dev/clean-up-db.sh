#!/bin/bash

set -e

DEV_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "${DEV_DIR}"

if ! command -v docker-compose &> /dev/null; then
  docker-compose -f db.yml --project-name myna_dev down
else
  docker compose -f db.yml --project-name myna_dev down
fi

rm -rf "${DEV_DIR}/data/postgres/data" >/dev/null 2>&1
rm -f "${DEV_DIR}/data/redis/dump.rdb" >/dev/null 2>&1
