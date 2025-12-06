### Non-Elasticsearch Metrics Setup

#### Devcontainer Setup

⚠️ **Only for k6 tests**

⚠️ **Make sure you have Docker installed and running**

- Install [Dev Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) extension.
- Open the project in VSCode.
- Press `F1` and select `Dev Containers: Reopen in Container`.
- Now you can run k6 tests from VSCode:
    ```shell
    # Synchronous test
    ./k6 run -e APP=synchronous synchronous.js
    # or RabbitMQ test
    ./k6 run -e APP=rabbitmq asynchronous.js
    # or WildFly test
    ./k6 run -e APP=wildfly asynchronous.js
    ```
- To exit the Dev Container, press `F1` and select `Dev Containers: Reopen Folder Locally`.

#### Local Setup

1. Install [k6](https://grafana.com/docs/k6/latest/set-up/install-k6/)
2. Run tests:
    ```shell
    # Synchronous test
    k6 run -e APP=synchronous synchronous.js
    # or RabbitMQ test
    k6 run -e APP=rabbitmq asynchronous.js
    # or WildFly test
    k6 run -e APP=wildfly asynchronous.js
    ```

### Elasticsearch Metrics Setup

#### Devcontainer Setup

- Run test with metrics exporting to Elasticsearch:
```shell
K6_ELASTICSEARCH_URL=http://elasticsearch-sare:9200 \
./k6 run -e APP=synchronous synchronous.js -o output-elasticsearch
```

#### Local Setup

1. Build the k6 version with Elasticsearch support:
- This will create a `k6` binary in the current directory.
  ```shell
  # Install xk6
  go install go.k6.io/xk6/cmd/xk6@latest
    
  # Build the k6 binary
  xk6 build --with github.com/elastic/xk6-output-elasticsearch
    
  ... [INFO] Build environment ready
  ... [INFO] Building k6
  ... [INFO] Build complete: ./k6
  ```
- For additional information, see [k6 Elasticsearch documentation](https://grafana.com/docs/k6/latest/results-output/real-time/elasticsearch/).

2. Run tests and export metrics to Elasticsearch:
    ```shell
    K6_ELASTICSEARCH_URL=http://elasticsearch-sare:9200 \
    ./k6 run -e APP=synchronous synchronous.js -o output-elasticsearch
    ```

Access Kibana or Elasticsearch at the following URLs:

| Service       | URL                   |
|---------------|-----------------------|
| Kibana        | http://localhost:5601 |
| Elasticsearch | http://localhost:9200 |