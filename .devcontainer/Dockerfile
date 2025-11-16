# .devcontainer/Dockerfile
FROM mcr.microsoft.com/devcontainers/go:2-1.24-bookworm

# Install dependencies for manual k6 installation (curl, tar)
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    tar \
    && rm -rf /var/lib/apt/lists/*

# Manually install k6
ARG K6_VERSION="v1.4.0"
ARG K6_ARCH="linux-arm64"
RUN curl -L "https://github.com/grafana/k6/releases/download/${K6_VERSION}/k6-${K6_VERSION}-${K6_ARCH}.tar.gz" -o /tmp/k6.tar.gz \
    && tar -xzf /tmp/k6.tar.gz -C /tmp \
    && mv /tmp/k6-${K6_VERSION}-${K6_ARCH}/k6 /usr/local/bin/k6 \
    && rm /tmp/k6.tar.gz \
    && rm -rf /tmp/k6-${K6_VERSION}-${K6_ARCH}