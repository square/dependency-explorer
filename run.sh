#!/bin/bash

DEFAULT_MODULES_FILE=""
INPUT_PROJECTS=()

# Parse arguments
for arg in "$@"; do
  case "$arg" in
    --default-modules=*)
      DEFAULT_MODULES_FILE="${arg#*=}"
      ;;
    *)
      INPUT_PROJECTS+=("$arg")
      ;;
  esac
done

# Ensure at least one input project is provided
if [[ ${#INPUT_PROJECTS[@]} -eq 0 ]]; then
  echo "Usage: $0 <input-project> [<input-project> ...] [--default-modules=/path/to/file]"
  exit 1
fi

# Validate the default modules file if provided
if [[ -n "$DEFAULT_MODULES_FILE" && ! -f "$DEFAULT_MODULES_FILE" ]]; then
  echo "Error: File '$DEFAULT_MODULES_FILE' not found."
  exit 1
fi

# Build command
CMD=(java -jar app/binary/lib/dependency-explorer-1.0.1-minified.jar gradle-projects)

# Append input projects
CMD+=("${INPUT_PROJECTS[@]}")

# Append default modules file if provided
if [[ -n "$DEFAULT_MODULES_FILE" ]]; then
  CMD+=(--default-modules-file "$DEFAULT_MODULES_FILE")
fi

# Execute the command
"${CMD[@]}"
