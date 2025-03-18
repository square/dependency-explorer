#!/bin/bash

# Ensure an argument is provided for which modules to explore
if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <modules>"
  exit 1
fi

java -jar app/binary/lib/dependency-explorer-1.0.0-minified.jar gradle-projects "$1"
