Prerequisites:

- k6 installed — https://grafana.com/docs/k6/latest/set-up/install-k6/
- All required containers are running
- Test data must be downloaded from Google Drive before running the tests (and stored in folder `/data`)
  - Currently this is a manual step (Google Drive link: [TBD](https://drive.google.com/drive/folders/1T-LTIugKDPR-o2Ch8HhBXq3eGhhgWLlr?usp=sharing))
  - Data includes multiple files (images, .txt, .docx, etc.) of various sizes
- Run scripts with: `k6 run <test-file>.js`

All tests files are divided into 5 categories (based on their size)

| Category |     Size     |
| -------- | :----------: |
| 1        |    < 1 KB    |
| 2        |  1 - 10 KB   |
| 3        | 10 - 100 KB  |
| 4        | 100KB - 1MB  |
| 5        | 1 MB - 10 MB |
