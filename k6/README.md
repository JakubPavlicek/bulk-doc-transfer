### Prerequisites:

- k6 installed - [Install](https://grafana.com/docs/k6/latest/set-up/install-k6/)
- All required containers are running
- Test data must be downloaded from Google Drive before running the tests (and stored in folder `/data`)
  - Currently, this is a manual step (Google Drive link: [TBD](https://drive.google.com/drive/folders/1T-LTIugKDPR-o2Ch8HhBXq3eGhhgWLlr?usp=sharing))
  - Data includes multiple files (images, .txt, .docx, etc.) of various sizes

### Running tests

```shell
k6 run <test-file>.js
```

All tests files are divided into five categories (based on their size)

| Category  |     Size      |
|-----------|:-------------:|
| 1         |    < 1 KB     |
| 2         |   1 - 10 KB   |
| 3         |  10 - 100 KB  |
| 4         |  100KB - 1MB  |
| 5         | 1 MB - 10 MB  |

### Run tests and export metrics to Elasticsearch

For additional information, see [k6 documentation](https://grafana.com/docs/k6/latest/results-output/real-time/elasticsearch/).

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

2. Run tests and export metrics to Elasticsearch:
    ```shell
    K6_ELASTICSEARCH_URL=http://elasticsearch-sare:9200 \
    ./k6 run simple-test.js -o output-elasticsearch
    ```

Access Grafana dashboard or Prometheus query console:

| Service       | URL                   |
|---------------|-----------------------|
| Kibana        | http://localhost:5601 |
| Elasticsearch | http://localhost:9200 |