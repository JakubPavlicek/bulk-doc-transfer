import http from "k6/http";
import { check } from "k6";
import { FormData } from "https://jslib.k6.io/formdata/0.0.2/index.js";

const testDir = "/data";
const files = [
  "file1.jpg",
  "file2.pdf",
  "file3.txt",
  "file4.xml",
  "file5.jpg",
  "file6.xml",
];

const file1 = open(`${testDir}/${files[0]}`, "b");
const file2 = open(`${testDir}/${files[1]}`, "b");

export const options = {
  vus: 1,
  iterations: 10,
};

const BASE_URL = "http://localhost:8010";

export default function () {
  const fd = new FormData();
  fd.append("email", "jpvlck@students.zcu.cz");
  fd.append("subject", "Single file test");
  fd.append("description", "Testing with just one file.");
  fd.append("files", http.file(file1, files[0], "image/jpeg"));
  fd.append("files", http.file(file2, files[1], "application/pdf"));

  console.log("FormData prepared");
  console.log(`Uploading files: ${files[0]}, ${files[1]}`);

  const response = http.post(`${BASE_URL}/api/v1/submissions`, fd.body(), {
    headers: { "Content-Type": "multipart/form-data; boundary=" + fd.boundary },
  });

  console.log(`Response status: ${response.status}`);
  console.log(`Response body: ${response.body}`);

  check(response, {
    "status is 202": (r) => r.status === 202,
  });
}
