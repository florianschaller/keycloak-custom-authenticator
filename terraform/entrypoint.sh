#!/bin/ash

echo "Waiting for KEYCLOAK_URL $KEYCLOAK_URL response ..."
ash -c "while ! wget -q --spider ${KEYCLOAK_URL}/realms/master/; do sleep 2; done;"

terraform init

terraform apply -auto-approve \
-var "keycloak_username=${KEYCLOAK_ADMIN_USER}" \
-var "keycloak_password=${KEYCLOAK_ADMIN_PASSWORD}" \
-var "keycloak_host=${KEYCLOAK_URL}"
