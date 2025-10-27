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

### Run tests with Prometheus metrics

Start Docker containers with a k6 profile set, e.g.:
```shell
docker compose --profile synchronous --profile k6 up -d
```

Run tests with Prometheus metrics enabled:
```shell
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
k6 run -o experimental-prometheus-rw <test-file>.js
```

Access Grafana dashboard or Prometheus query console:

| Service    | URL                              |
|------------|----------------------------------|
| Grafana    | http://localhost:3000/dashboards |
| Prometheus | http://localhost:9090/query      |