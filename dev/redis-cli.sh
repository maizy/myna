#!/bin/bash

set -e

DEV_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "${DEV_DIR}"

docker compose -f db.yml --project-name myna_dev exec redis redis-cli -a devtest $@
