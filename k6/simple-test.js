import http from "k6/http";
import { check } from "k6";

const txtFileData = open("data/file1.jpg", "b");

export const options = {
  vus: 10,
  iterations: 100,
};

const BASE_URL = "http://synchronous-app:8010";

export default function () {
  const data = {
    email: "jpvlck@students.zcu.cz",
    subject: "Single file test",
    description: "Testing with just one file.",
    files: http.file(txtFileData, "file1.jpg"),
  };

  console.log("data prepares", data);

  console.log(`Uploading single file: file1.jpg`);

  const response = http.post(`${BASE_URL}/api/v1/submissions`, data);

  console.log(`Response status: ${response.status}`);
  console.log(`Response body: ${response.body}`);

  check(response, {
    "status is 202": (r) => r.status === 202,
  });
}
