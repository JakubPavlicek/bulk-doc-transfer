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
  APP,
  BASE_URL,
  SCENARIO_CONFIGS,
} from "./utils.js";

const totalTimeTrend = new Trend("totalTime");

export const options = {
  scenarios: buildSequentialScenarios(),
  thresholds: {
    http_req_duration: ["p(95)<5000"],
    http_req_failed: ["rate<0.1"],
  },
};

export default function () {
  const iterationId = __ITER;
  const scenarioName = exec.scenario.name;
  const scenarioConfig = SCENARIO_CONFIGS[scenarioName];
  const { selectedFiles, fileSize } = selectFiles(
    scenarioConfig.fileCount,
    iterationId
  );
  console.log(
    `Iteration ${iterationId} - Scenario: ${scenarioName} - Selected ${selectedFiles.length} file(s) with total size ${fileSize} bytes`
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

  console.log(
    `Sending POST request with ${selectedFiles.length} file(s) totaling ${fileSize} bytes`
  );
  const startTime = new Date();
  const response = http.post(`${BASE_URL}/api/v1/submissions`, fd.body(), {
    headers: {
      "Content-Type": "multipart/form-data; boundary=" + fd.boundary,
    },
    tags: {
      scenario: scenarioName,
      fileCount: selectedFiles.length.toString(),
      fileSize: fileSize,
      app: "synchronous",
    },
  });
  console.log(
    `Received response with status ${response.status} for submission`
  );

  const submissionId = extractSubmissionId(response);
  const pollResult = submissionId
    ? pollSubmission(
        submissionId,
        startTime,
        totalTimeTrend,
        BASE_URL,
        scenarioConfig.pollInterval,
        scenarioConfig.maxPollAttempts
      )
    : { success: false, attempts: 0 };

  check(response, {
    "status is 202": (r) => r.status === 202,
    "has submissionId": (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.submissionId !== undefined;
      } catch {
        return false;
      }
    },
  });

  check(pollResult, {
    "submission saved within timeout": (res) => res.success,
  });
}
