import http from "k6/http";
import { check, sleep } from "k6";
import { FormData } from "https://jslib.k6.io/formdata/0.0.2/index.js";
import { Trend } from "k6/metrics";
import exec from "k6/execution";
import {
  buildSequentialScenarios,
  selectFiles,
  extractSubmissionId,
  pollSubmission,
  SCENARIO_CONFIGS,
} from "./utils.js";

// const APP = "wildfly";
const APP = "rabbitmq";

const totalTimeTrend = new Trend("totalTime");

export const options = {
  scenarios: buildSequentialScenarios(APP),
  thresholds: {
    http_req_duration: ["p(95)<5000"],
    http_req_failed: ["rate<0.1"],
  },
};

const BASE_URL =
  APP === "wildfly" ? "http://localhost:8080" : "http://localhost:8020";

export default function () {
  const iterationId = __ITER;
  const scenarioName = exec.scenario.name;
  const scenarioConfig = SCENARIO_CONFIGS[scenarioName];
  const { selectedFiles, fileSize } = selectFiles(
    scenarioConfig.fileCount,
    iterationId
  );

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
  console.log(
    `Sending POST request with ${selectedFiles.length} file(s) totaling ${fileSize} bytes`
  );
  const response = http.post(`${BASE_URL}/api/v1/submissions`, fd.body(), {
    headers: {
      "Content-Type": "multipart/form-data; boundary=" + fd.boundary,
    },
    tags: {
      scenario: scenarioName,
      fileCount: scenarioConfig.fileCount,
      fileSize: fileSize,
      app: APP,
      rate: scenarioConfig.rate,
    },
  });
  console.log(
    `Received response with status ${response.status} for submission`
  );

  const submissionId = extractSubmissionId(response);

  sleep(3); // Short sleep before polling - electronic signature, malware scan, etc.
  const pollResult = submissionId
    ? pollSubmission(
        submissionId,
        startTime,
        totalTimeTrend,
        BASE_URL,
        scenarioConfig.pollInterval,
        scenarioConfig.maxPollAttempts,
        APP,
        scenarioName,
        scenarioConfig.rate,
        scenarioConfig.fileCount,
        fileSize
      )
    : { success: false, attempts: 0 };

  check(
    response,
    {
      "status is 202": (r) => r.status === 202,
      "has submissionId": () => submissionId !== null,
    },
    {
      app: APP,
      scenario: scenarioName,
      rate: scenarioConfig.rate,
      fileCount: scenarioConfig.fileCount,
      fileSize: fileSize,
    }
  );

  check(
    pollResult,
    {
      "submission saved within timeout": (res) => res.success,
    },
    {
      app: APP,
      scenario: scenarioName,
      rate: scenarioConfig.rate,
      fileCount: scenarioConfig.fileCount,
      fileSize: fileSize,
    }
  );
}
