FROM hashicorp/terraform:1.9

ENV KEYCLOAK_ADMIN_USER ${KEYCLOAK_ADMIN_USER}
ENV KEYCLOAK_ADMIN_PASSWORD ${KEYCLOAK_ADMIN_PASSWORD}
ENV KEYCLOAK_URL ${KEYCLOAK_URL}

RUN apk update && apk upgrade && apk add curl && apk add dos2unix

COPY entrypoint.sh .
COPY provider.tf .
COPY keycloak.tf .

RUN dos2unix ./entrypoint.sh

ENTRYPOINT ["ash", "entrypoint.sh"]
