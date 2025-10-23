# Use Bookworm version of Postgres image to have Debian packages as an option.
FROM postgres:18.0-bookworm

# Install Java. Consider downloading it externally later on to avoid the redundant downloads.
RUN apt-get update; \
    apt-get install -y wget apt-transport-https gpg; \
    wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | gpg --dearmor | tee /etc/apt/trusted.gpg.d/adoptium.gpg > /dev/null; \
    echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | tee /etc/apt/sources.list.d/adoptium.list; \
    apt-get update; \
    apt-get install -y temurin-25-jre

# Add Postgres scripts to populate the shema and data.
COPY content/docker-entrypoint-initdb.d/* /docker-entrypoint-initdb.d

EXPOSE 5432
