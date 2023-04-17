# Description

This repository is used to store the code I wrote while learning microservices.

# 2. Prepare Environment

## 2.1 Postgres database

I will use docker to install Postgres database, following is the `docker-compose.yaml`.

```yaml
version: "3.9"

services:
  postgres:
    image: postgres:15.2
    container_name: postgres
    ports:
      - "5432:5432"
    environment:
      - "POSTGRES_USER=postgres"
      - "POSTGRES_PASSWORD=postgres"
    volumes:
      - "${PWD}/data:/var/lib/postgresql/data"
```

The table structure and the corresponding data are as follows:

```postgresql
drop table if exists public.organizations;
create table if not exists public.organizations
(
    organization_id text collate pg_catalog."default" not null,
    name            text collate pg_catalog."default",
    contact_name    text collate pg_catalog."default",
    contact_email   text collate pg_catalog."default",
    contact_phone   text collate pg_catalog."default",
    constraint organizations_key primary key (organization_id)
) tablespace pg_default;

alter table public.organizations
    owner to postgres;

drop table if exists public.license;
create table if not exists public.licenses
(
    license_id      text collate pg_catalog."default" not null,
    organization_id text collate pg_catalog."default" not null,
    description     text collate pg_catalog."default",
    product_name    text collate pg_catalog."default" not null,
    license_type    text collate pg_catalog."default" not null,
    comment         text collate pg_catalog."default",
    constraint license_key primary key (license_id),
    constraint license_organization_id_fkey foreign key (organization_id)
        references public.organizations (organization_id) match simple
        on update no action
        on delete no action not deferrable
) tablespace pg_default;

alter table public.licenses
    owner to postgres;

INSERT INTO public.organizations
VALUES ('e6a625cc-718b-48c2-ac76-1dfdff9a531e', 'Ostock', 'Illary Huaylupo', 'illaryhs@gmail.com', '888888888');
INSERT INTO public.organizations
VALUES ('d898a142-de44-466c-8c88-9ceb2c2429d3', 'OptimaGrowth', 'Admin', 'illaryhs@gmail.com', '888888888');
INSERT INTO public.organizations
VALUES ('e839ee96-28de-4f67-bb79-870ca89743a0', 'Ostock', 'Illary Huaylupo', 'illaryhs@gmail.com', '888888888');
INSERT INTO public.licenses
VALUES ('f2a9c9d4-d2c0-44fa-97fe-724d77173c62', 'd898a142-de44-466c-8c88-9ceb2c2429d3', 'Software Product', 'Ostock',
        'complete', 'I AM DEV');
INSERT INTO public.licenses
VALUES ('279709ff-e6d5-4a54-8b55-a5c37542025b', 'e839ee96-28de-4f67-bb79-870ca89743a0', 'Software Product', 'Ostock',
        'complete', 'I AM DEV');
```

## 2.2 Vault

The `docker-compose.yaml` of Vault is as follows:

```yaml
version: "3.9"

services:
  vault:
    container_name: vault
    image: hashicorp/vault:1.13
    ports:
      - "8200:8200"
    environment:
      - "VAULT_DEV_ROOT_TOKEN_ID=root"
```

## 2.3 keycloak

```yaml
version: "3.9"

services:
  keycloak:
    container_name: keycloak
    image: bitnami/keycloak:21.0.2-debian-11-r2
    ports:
      - "10000:8080"
    environment:
      - "KEYCLOAK_ADMIN_USER=admin"
      - "KEYCLOAK_ADMIN_PASSWORD=admin"
      - "KEYCLOAK_MANAGEMENT_USER=admin"
      - "KEYCLOAK_MANAGEMENT_PASSWORD=admin"
      - "KEYCLOAK_DATABASE_VENDOR=dev-file"
```

### What's the client in Keycloak?

Clients in Keycloak are entities that can request user authentication. The clients are often the applications or services that we want to secure by providing a single sign-on (SSO) solution.


### Testing the client

To confirm the client was created successfully, you can use the SPA testing application on the [Keycloak website](https://www.keycloak.org/app/)

### Inspect endpoints

The endpoints located in the `Realm Setting` menu.

### Reference Document

- [Spring Boot Configuration](https://www.keycloak.org/docs/latest/securing_apps/index.html#_spring_boot_adapter)
- [Spring Boot Integration](https://www.keycloak.org/docs/latest/securing_apps/index.html#spring-boot-integration)
- [Java Adapter Configurations](https://www.keycloak.org/docs/latest/securing_apps/index.html#_java_adapter_config)
- [Server Administration Guide](https://keycloak.org/docs/latest/server_admin/)

# Keycloak Access Token

```text
1ahXcWZqcHtW4wRY6ENCfgEKlpOQCCeZ.eyJleHAiOjE2ODExODEzMDcsImlhdCI6MTY4MTE4MTAwNywiYXV0aF90aW1lIjoxNjgxMTgxMDA3LCJqdGkiOiJlYTBlYTE3NS1mNWRmLTQ3YjUtYjQ1MC0wNzI3Y2Q2MWFhZDgiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjEwMDAwL3JlYWxtcy9vc3RvY2siLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiMTgyZTlkNTYtOTBhZi00Njc2LWI0NTQtY2E4Mzg5ZTM1ZDM3IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoib3N0b2NrIiwic2Vzc2lvbl9zdGF0ZSI6IjY3MmYzMjk3LTk3YWYtNDkwNC04ZTcxLWNkZGM3OWMzZjRlMCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDcyIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLW9zdG9jayIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJvc3RvY2siOnsicm9sZXMiOlsiQURNSU4iXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6IjY3MmYzMjk3LTk3YWYtNDkwNC04ZTcxLWNkZGM3OWMzZjRlMCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW4iLCJnaXZlbl9uYW1lIjoiIiwiZmFtaWx5X25hbWUiOiIifQ.ekVEXu70jNbWqF-AB8dhsfJ28uwuVHbQV-7WA34tY2M13byzztybHJo9wCmvRvjiRV8nYW1eS6_knjvlWeF8Tm0j0gG8_alIl5pO8rzxqZ0gkmxxxEbt5cWQkchlND3BJtiWgxkKm1RIB32t_QY2YakZpf8Cgvs35qdgL93_ehlTugNGH-2guHDcuiGeWGlB0HaZcQFTSOo9PjsdvMfhRp9zl3v1JVZZecJjWKmmyfWD_W58Li_OM4Q8DEPlFSdP6WGaadUwzzExHyUZzRnzpEsCr0n-6j4uWZ4SPejRFuxzHbSBE6o4_ZXL-GU7KLVIr_VTkp2AB6IyKCM7n8XH3g
```
## 2.4 kafka

```yaml
version: "3"

networks:
  default:
    name: microservices
    external: true


services:
  kafka:
    image: bitnami/kafka:3.4.0
    container_name: kafka
    ports:
      - '9092:9092'
    volumes:
      - ${PWD}/data:/bitnami/kafka
    environment:
      - KAFKA_BROKER_ID=1
      - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092
      - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://127.0.0.1:9092
      - KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181
      - ALLOW_PLAINTEXT_LISTENER=yes
    depends_on:
      - zookeeper

  zookeeper:
    image: bitnami/zookeeper:3.8.1
    container_name: zookeeper
    ports:
      - '2181:2181'
    environment:
      - ALLOW_ANONYMOUS_LOGIN=yes
```

## 2.5 Redis

```yaml
version: "3"

networks:
  default:
    name: microservices
    external: true

services:
  redis:
    image: redis:7.0.10
    container_name: redis
    ports:
      - "6379:6379"
```