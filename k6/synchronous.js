import http from "k6/http";
import { check } from "k6";
import { FormData } from "https://jslib.k6.io/formdata/0.0.2/index.js";
import { Trend } from 'k6/metrics';

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

const SCENARIOS = {
  light: { fileCount: 1, vus: 1, duration: "30s", rate: 1 },
  normal: { fileCount: 3, vus: 5, duration: "30s", rate: 5 },
  heavy: { fileCount: 5, vus: 10, duration: "30s", rate: 10 },
};

const ACTIVE_SCENARIO = __ENV.SCENARIO || "light";
const config = SCENARIOS[ACTIVE_SCENARIO];

export const options = {
  scenarios: {
    [ACTIVE_SCENARIO]: {
      executor: "shared-iterations",
      vus: config.vus,
      iterations: 20,
      maxDuration: "10m",
    },
  },
  thresholds: {
    http_req_duration: ["p(95)<5000"],
    http_req_failed: ["rate<0.1"],
  },
};

const BASE_URL = "http://synchronous-app:8010";
const POLL_INTERVAL = 5;
const MAX_POLL_ATTEMPTS = 10;
export default function () {
  const iterationId = __ITER;
  const {selectedFiles, fileSize} = selectFiles(config.fileCount, iterationId);

  const fd = new FormData();
  fd.append("email", "jpvlck@students.zcu.cz");
  fd.append("subject", `Test ${ACTIVE_SCENARIO} - Iteration ${iterationId}`);
  fd.append(
    "description",
    `Benchmarking ${selectedFiles.length} file(s), iteration ${iterationId}`
  );

  selectedFiles.forEach((file) => {
    fd.append("files", http.file(file.content, file.name, file.type));
  });

  const startTime = new Date();
  const response = http.post(`${BASE_URL}/api/v1/submissions`, fd.body(), {
    headers: {
      "Content-Type": "multipart/form-data; boundary=" + fd.boundary,
    },
    tags: {
      scenario: ACTIVE_SCENARIO,
      fileCount: selectedFiles.length.toString(),
      fileSize: fileSize,
      app: "synchronous",
    },
  });
  
  const submissionId = extractSubmissionId(response);
  const pollResult = submissionId
    ? pollSubmission(submissionId, startTime, totalTimeTrend)
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

function extractSubmissionId(response) {
  try {
    const body = JSON.parse(response.body);
    return body.submissionId ?? null;
  } catch {
    return null;
  }
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
