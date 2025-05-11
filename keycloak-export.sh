#!/bin/bash

set -e  # Fail if unexpected commands fail

# Usage function
usage() {
  echo "Usage: $0 [REALM_NAME (optional)] <CONTAINER_NAME>"
  echo
  echo "REALM_NAME:  Name of the realm to export. If omitted, exports all realms."
  echo "CONTAINER_NAME: Name or ID of the running Keycloak Docker container."
  echo
  echo "Example:"
  echo "  $0 myrealm my-keycloak"
  echo "  $0 my-keycloak   (export all realms)"
  exit 1
}

# Check arguments
if [ $# -lt 1 ]; then
  usage
fi

if [ $# -eq 1 ]; then
  REALM_NAME=""
  CONTAINER_NAME="$1"
else
  REALM_NAME="$1"
  CONTAINER_NAME="$2"
fi

EXPORT_DIR="/tmp"
EXPORT_PATH_ON_HOST="./keycloak-exports"
mkdir -p "$EXPORT_PATH_ON_HOST"  # Make sure output directory exists

# Detect which image is used by the container
IMAGE_NAME=$(docker inspect --format='{{.Config.Image}}' "$CONTAINER_NAME" 2>/dev/null)
if [[ "$IMAGE_NAME" == *bitnami* ]]; then
  KC_PATH="/opt/bitnami/keycloak/bin/kc.sh"
elif [[ "$IMAGE_NAME" == *quay.io/keycloak/keycloak* ]] || [[ "$IMAGE_NAME" == *keycloak/keycloak* ]]; then
  KC_PATH="/opt/keycloak/bin/kc.sh"
else
  echo "⚠️  Warning: Unknown Keycloak image. Defaulting to Bitnami path."
  KC_PATH="/opt/bitnami/keycloak/bin/kc.sh"
fi

# Build export command
EXPORT_CMD="${KC_PATH} export --dir ${EXPORT_DIR} --users=realm_file"
if [ -n "$REALM_NAME" ]; then
  EXPORT_CMD="${EXPORT_CMD} --realm ${REALM_NAME}"
fi

echo "Starting export inside container (will ignore errors if Keycloak is running)..."

# Run export, but ignore failure
set +e  # Temporarily disable exit-on-error
docker exec "$CONTAINER_NAME" bash -c "$EXPORT_CMD"
EXPORT_EXIT_CODE=$?
set -e  # Re-enable exit-on-error

if [ $EXPORT_EXIT_CODE -ne 0 ]; then
  echo "⚠️ Warning: Export command failed (expected if Keycloak server is running). Continuing to copy any files..."
fi

# Now copy whatever files exist
if [ -n "$REALM_NAME" ]; then
  EXPORT_FILE="${REALM_NAME}-realm.json"
  echo "Trying to copy ${EXPORT_FILE} from container..."
  docker cp "${CONTAINER_NAME}:${EXPORT_DIR}/${EXPORT_FILE}" "${EXPORT_PATH_ON_HOST}/${EXPORT_FILE}" && \
  echo "✅ Copied ${EXPORT_FILE} to ${EXPORT_PATH_ON_HOST}/" || \
  echo "❌ Failed to copy ${EXPORT_FILE}. It may not exist."
else
  echo "Trying to copy all realm files..."
  FILES=$(docker exec "$CONTAINER_NAME" bash -c "ls ${EXPORT_DIR}/*.json" 2>/dev/null || true)

  if [ -z "$FILES" ]; then
    echo "❌ No realm export files found in container."
    exit 1
  fi

  for FILE in $FILES; do
    FILENAME=$(basename "$FILE")
    docker cp "${CONTAINER_NAME}:${EXPORT_DIR}/${FILENAME}" "${EXPORT_PATH_ON_HOST}/${FILENAME}" && \
    echo "✅ Copied ${FILENAME} to ${EXPORT_PATH_ON_HOST}/" || \
    echo "❌ Failed to copy ${FILENAME}. It may not exist."
  done
fi

echo "🏁 Script completed."
