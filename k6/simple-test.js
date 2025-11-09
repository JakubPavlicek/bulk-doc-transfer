import http from "k6/http";
import { check } from "k6";
import { FormData } from "https://jslib.k6.io/formdata/0.0.2/index.js";

const txtManifest = JSON.parse(open("data/txt/manifest.json"));
const smallestTxtFile = txtManifest.files.find((f) => f.category === "1");
const smallestTxtFile2 = txtManifest.files.find((f) => f.category === "2");
const txtFileData = open(`data/txt/${smallestTxtFile.name}`, "b");
const txtFileData2 = open(`data/txt/${smallestTxtFile2.name}`, "b");

export const options = {
  vus: 10,
  iterations: 10,
};

const BASE_URL = "http://localhost:8010";

export default function () {
  const fd = new FormData();
  fd.append("email", "jpvlck@students.zcu.cz");
  fd.append("subject", "Single file test");
  fd.append("description", "Testing with just one file.");
  fd.append(
    "files",
    http.file(txtFileData, smallestTxtFile.name, "text/plain")
  );
  fd.append(
    "files",
    http.file(txtFileData2, smallestTxtFile2.name, "text/plain")
  );

  console.log("FormData prepared");
  console.log(
    `Uploading files: ${smallestTxtFile.name}, ${smallestTxtFile2.name}`
  );

  const response = http.post(`${BASE_URL}/api/v1/submissions`, fd.body(), {
    headers: { "Content-Type": "multipart/form-data; boundary=" + fd.boundary },
  });

  console.log(`Response status: ${response.status}`);
  console.log(`Response body: ${response.body}`);

  check(response, {
    "status is 202": (r) => r.status === 202,
  });
}
