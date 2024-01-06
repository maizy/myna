#!/bin/bash

set -e

DEV_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "${DEV_DIR}"

if ! command -v docker-compose &> /dev/null; then
  docker-compose -f db.yml --project-name myna_dev exec redis redis-cli -a devtest $@
else
  docker compose -f db.yml --project-name myna_dev exec redis redis-cli -a devtest $@
fi
