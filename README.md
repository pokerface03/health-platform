# Health Platform

A modular, containerized platform for collecting, processing and serving health-related data. This repository contains Docker-compose based orchestration, service definitions, MQTT broker configuration, database initialization and an Nginx reverse-proxy. It is designed to be run locally with Docker Compose or deployed to a Docker Swarm cluster (see branch `deploy/dockerSwarm`).

> NOTE: This README combines information located across repository branches

## Key features
- Service-oriented layout under `services/` (each service in its own folder)
- Centralized orchestration via `docker-compose.yml`
- Persistent data and DB initialization under `db/`
- MQTT broker configuration under `mosquitto/`
- Nginx reverse-proxy configuration under `nginx/`
- Environment example stored in `envbackup`

## Repository layout
- `.gitignore` — git ignores
- `docker-compose.yml` — primary compose file used to run the platform locally
- `envbackup` — example of environment variables required by the stack (copy to `.env`)
- `db/` — database init scripts and related assets
- `mosquitto/` — Mosquitto MQTT configuration and persistent data
- `nginx/` — Nginx configuration for proxy / TLS / routing
- `services/` — microservices and their source/configuration (each service directory represents an app)
- `deploy/dockerSwarm` (branch) — stack definitions and docs for Docker Swarm deployments

## Requirements
- Docker (20.x+ recommended)
- Docker Compose v1.27+ or Docker Compose V2
- (Optional) Docker Swarm for production-like deployments
- Linux / macOS / Windows with Docker Desktop

## Quick Start — local (Docker Compose)

1. Clone the repository
   - git clone https://github.com/pokerface03/health-platform.git
   - cd health-platform

2. Copy environment template
   - cp envbackup .env
   - Edit `.env` and provide secrets / credentials (DB passwords, API keys). Do NOT commit secrets.

3. Build and start the stack
   - docker compose build --pull
   - docker compose up -d

4. Check logs
   - docker compose logs -f
   - Or for a specific service: docker compose logs -f <service_name>

5. Stop and remove containers (and optionally volumes)
   - docker compose down
   - docker compose down -v  # removes volumes

Notes:
- If you use older Docker Compose, you may need `docker-compose` (with a hyphen) instead of `docker compose`.
- Some services may create / expect volumes under `db/` or other directories. Use `docker compose down -v` when you want a clean start.

## Deploy to Docker Swarm (branch: deploy/dockerSwarm)
A dedicated branch contains stack files and instructions for deploying to a Docker Swarm cluster.

Typical steps (on a manager node):
- git checkout deploy/dockerSwarm
- docker stack deploy -c docker-compose.yml health-platform

Review the `deploy/dockerSwarm` branch for stack-specific variables, secrets management, and any additional compose overrides.

## Environment variables
The file `envbackup` in the repository contains the example environment variables used by the stack. Copy it to `.env` and fill in values appropriate for your environment. Important values typically include:
- Database credentials (DB_USER, DB_PASSWORD, DB_NAME, etc.)
- MQTT credentials
- Service-specific API keys and hostnames
- Ports and domain names used by Nginx

Do not commit production credentials to the repository.

## Services overview
Each directory under `services/` corresponds to a logical component of the platform. Typical responsibilities include:
- Data ingestion (subscribing to MQTT topics)
- Processing / aggregation services
- API / frontend services (served behind Nginx)
- Worker / background jobs

Inspect each service folder for README, Dockerfile, and configuration to understand build/run requirements.

## MQTT (Mosquitto)
The repository includes a `mosquitto/` directory with config files for the MQTT broker. By default the broker runs as a container and persists data/config in that folder (as mapped volumes). Secure the broker for production (use TLS, ACLs, and strong credentials).

## Database
Database initialization scripts live under `db/`. Backups and persistent volumes are managed via Docker volumes configured in `docker-compose.yml`. Review the SQL or migration scripts to understand schema and seeding behavior.

## Nginx
Nginx is used for reverse proxying and TLS termination (if configured). The `nginx/` directory contains configs and example cert locations. Update `nginx` configs and `.env` domain values prior to production use.

## Development
- To add or modify a service, create or edit the folder under `services/<service_name>` and update `docker-compose.yml` if necessary.
- Use logs and docker compose commands for iterative debugging:
  - docker compose up --build <service_name>
  - docker compose logs -f <service_name>
- Keep configuration configurable via environment variables and avoid hardcoding secrets.

## Troubleshooting
- Port conflicts: ensure required ports (HTTP, MQTT, DB) are free on the host.
- Permissions: volumes mounted from the host can require uid/gid alignment.
- If a container fails to start, check `docker compose logs <container>` for stack traces.
- Database connectivity issues: verify `.env` credentials and network names used in `docker-compose.yml`.

## Contributing
Contributions are welcome. Suggested workflow:
- Fork the repository
- Create a feature branch
- Add tests or documentation for changes
- Open a pull request describing your change


## Security
- Do not store secrets in the repo. Use environment variables, Docker secrets, or a secret manager for production.
- Use TLS for MQTT and HTTP in production.
- Limit access to management endpoints and admin interfaces.


---

This README is intended to be a practical, consolidated starting point for anyone using the repository on the `main` branch. For branch-specific deployment details and production-ready overrides, consult the `deploy/dockerSwarm` branch and the individual service folders.



Last updated: 2026-01-30,
Author: pokerface03,
For questions or support, open an issue in the repository.
