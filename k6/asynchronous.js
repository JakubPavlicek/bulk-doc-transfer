import http from "k6/http";
import { check, sleep } from "k6";
import { FormData } from "https://jslib.k6.io/formdata/0.0.2/index.js";
import { Trend } from 'k6/metrics';
import exec from 'k6/execution';

const APP = "wildfly";
// const APP = "rabbitmq"

const totalTimeTrend = new Trend('totalTime');


const testDir = "./data";
const files = [
  "file1.jpg",
  "file2.pdf",
  "file3.txt",
  "file4.xml",
  "file5.jpg",
  "file6.xml",
];

const fileContents = files.map((filename) => ({
  name: filename,
  content: open(`${testDir}/${filename}`, "b"),
  type: getContentType(filename),
}));

function getContentType(filename) {
  const ext = filename.split(".").pop().toLowerCase();
  const types = {
    jpg: "image/jpeg",
    jpeg: "image/jpeg",
    pdf: "application/pdf",
    txt: "text/plain",
    xml: "application/xml",
  };
  return types[ext] || "application/octet-stream";
}

const SCENARIO_CONFIGS = {
  "rate-1-file": { fileCount: 1, rate: 1, duration: "30s", durationSeconds: 30, preAllocatedVUs: 2, maxVUs: 5 },
  "rate-2-file": { fileCount: 1, rate: 2, duration: "30s", durationSeconds: 30, preAllocatedVUs: 4, maxVUs: 8 },
  "rate-5-3files": { fileCount: 3, rate: 5, duration: "30s", durationSeconds: 30, preAllocatedVUs: 8, maxVUs: 16 },
  "rate-10-5files": { fileCount: 5, rate: 10, duration: "30s", durationSeconds: 30, preAllocatedVUs: 12, maxVUs: 24 },
};

const SCENARIO_GAP_SECONDS = 5;

export const options = {
  scenarios: buildSequentialScenarios(),
  thresholds: {
    http_req_duration: ["p(95)<5000"],
    http_req_failed: ["rate<0.1"],
  },
};

function buildSequentialScenarios() {
  const scenarios = {};
  let offsetSeconds = 0;
  for (const name of Object.keys(SCENARIO_CONFIGS)) {
    const scenario = SCENARIO_CONFIGS[name];
    scenarios[name] = {
      executor: "constant-arrival-rate",
      rate: scenario.rate,
      timeUnit: "1s",
      duration: scenario.duration,
      startTime: `${offsetSeconds}s`,
      preAllocatedVUs: scenario.preAllocatedVUs ?? scenario.rate * 2,
      maxVUs: scenario.maxVUs ?? scenario.preAllocatedVUs ?? scenario.rate * 4,
      tags: {
        scenarioType: name,
      },
    };
    offsetSeconds += scenario.durationSeconds + SCENARIO_GAP_SECONDS;
  }
  return scenarios;
}
const BASE_URL = APP === "wildfly" ? "http://jms-app:8080" : "http://rabbitmq-app:8020";
const POLL_INTERVAL = 5;
const MAX_POLL_ATTEMPTS = 10;

export default function () {
  const iterationId = __ITER;
  const scenarioName = exec.scenario.name;
  const scenarioConfig = SCENARIO_CONFIGS[scenarioName];
  const {selectedFiles, fileSize} = selectFiles(scenarioConfig.fileCount, iterationId);

  const fd = new FormData();
  fd.append("email", "jpvlck@students.zcu.cz");
  fd.append("subject", `Test ${scenarioName} - Iteration ${iterationId}`);
  fd.append(
    "description",
    `Benchmarking ${selectedFiles.length} file(s), iteration ${iterationId}`
  );

  selectedFiles.forEach((file) => {
    fd.append("files", http.file(file.content, file.name, file.type));
  });

  const startTime = new Date();
  console.log(`Sending POST request with ${selectedFiles.length} file(s) totaling ${fileSize} bytes`);
  const response = http.post(`${BASE_URL}/api/v1/submissions`, fd.body(), {
    headers: {
      "Content-Type": "multipart/form-data; boundary=" + fd.boundary,
    },
    tags: {
      scenario: scenarioName,
      fileCount: selectedFiles.length.toString(),
      fileSize: fileSize,
      app: APP,
    },
  });
  console.log(`Received response with status ${response.status} for submission`);

  const submissionId = extractSubmissionId(response);

  sleep(3); // Short sleep before polling - electronic signature, malware scan, etc.
  const pollResult = submissionId
    ? pollSubmission(submissionId, startTime, totalTimeTrend)
    : { success: false, attempts: 0 };

  check(response, {
    "status is 202": (r) => r.status === 202,
    "has submissionId": () => submissionId !== null,
  });

  check(pollResult, {
    "submission saved within timeout": (res) => res.success,
  });
}

function extractSubmissionId(response) {
  try {
    const body = JSON.parse(response.body);
    return body.submissionId ?? null;
  } catch {
    return null;
  }
}

function pollSubmission(submissionId, startTime, totalTimeTrend) {
  for (let attempts = 1; attempts <= MAX_POLL_ATTEMPTS; attempts++) {
    const pollResponse = http.get(
      `${BASE_URL}/api/v1/submissions/${submissionId}`
    );
    const parseredBody = JSON.parse(pollResponse.body);
    console.log(`Status odpovědi při dotazování na stav: ${pollResponse.status} - submissionId: ${submissionId}`);
    if (pollResponse.status === 200 && parseredBody.savedAt !== null) {
      console.log(`Successfully retrieved submission: ${submissionId} in ${attempts} attempts - ${parseredBody.savedAt} `);
      console.log(`Start time: ${startTime} - Saved at: ${parseredBody.savedAt}`);
      const savedAtDate = new Date(parseredBody.savedAt);
      totalTimeTrend.add(savedAtDate.getTime() - startTime.getTime());
      console.log(`Total time for submission ${submissionId}: ${savedAtDate.getTime() - startTime.getTime()} ms`);
      return { success: true, attempts };
    }
    if (attempts < MAX_POLL_ATTEMPTS) {
      sleep(POLL_INTERVAL);
    }
  }
  return { success: false, attempts: MAX_POLL_ATTEMPTS };
}

function selectFiles(count, iteration) {
  const maxFiles = Math.min(count, 5);
  const selected = [];
  for (let i = 0; i < maxFiles; i++) {
    const index = (iteration * maxFiles + i) % fileContents.length;
    selected.push(fileContents[index]);
  }
  const fileSize = selected.reduce((sum, f) => sum + f.content.byteLength, 0);

  return {selectedFiles: selected, fileSize};
}
