import http from "k6/http";
import { check } from "k6";

const txtManifest = JSON.parse(open("data/txt/manifest.json"));
const smallestTxtFile = txtManifest.files.find((f) => f.category === "1");
const txtFileData = open(`data/txt/${smallestTxtFile.name}`, "b");

export const options = {
  vus: 10,
  iterations: 100,
};

const BASE_URL = "http://localhost:8020";

export default function () {
  const data = {
    email: "jpvlck@students.zcu.cz",
    subject: "Single file test",
    description: "Testing with just one file.",
    files: http.file(txtFileData, smallestTxtFile.name),
  };

  console.log("data prepares", data);

  console.log(`Uploading single file: ${smallestTxtFile.name}`);

  const response = http.post(`${BASE_URL}/api/v1/submissions`, data);

  console.log(`Response status: ${response.status}`);
  console.log(`Response body: ${response.body}`);

  check(response, {
    "status is 202": (r) => r.status === 202,
  });
}
