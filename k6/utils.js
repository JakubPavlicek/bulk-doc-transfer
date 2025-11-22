import { SharedArray } from "k6/data";
import http from "k6/http";
import { sleep } from "k6";

/** Scenario configurations */
export const SCENARIO_CONFIGS = {
  // Nízký rate s 1 souborem
  "low-1file": { fileCount: 1, rate: 2, timeUnit: "10s", duration: "60s" }, // 0.2 req/s
  // Střední rate s 1 souborem
  "mid-1file": { fileCount: 1, rate: 1, timeUnit: "1s", duration: "60s" }, // 1 req/s
  // Vyšší rate s 1 souborem
  "high-1file": { fileCount: 1, rate: 5, timeUnit: "1s", duration: "60s" }, // 5 req/s

  // Nízký rate s 3 soubory
  "low-3files": { fileCount: 3, rate: 2, timeUnit: "10s", duration: "60s" }, // 0.2 req/s
  // Střední rate s 3 soubory
  "mid-3files": { fileCount: 3, rate: 1, timeUnit: "1s", duration: "60s" }, // 1 req/s

  // Nízký rate s 5 soubory
  "low-5files": { fileCount: 5, rate: 2, timeUnit: "10s", duration: "60s" }, // 0.2 req/s
  // Střední rate s 5 soubory
  "mid-5files": { fileCount: 5, rate: 1, timeUnit: "1s", duration: "60s" }, // 1 req/s
};

const SCENARIO_GAP_SECONDS = 5;
const POLL_INTERVAL = 5;
const MAX_POLL_ATTEMPTS = 10;

/** File contents */

const testDir = "./data";
const files = [
  "file1.jpg",
  "file2.pdf",
  "file3.txt",
  "file4.xml",
  "file5.jpg",
  "file6.xml",
];

/** Shared functions */

const fileContents = new SharedArray("fileContents", function () {
  return files.map((filename) => ({
    name: filename,
    content: open(`${testDir}/${filename}`),
    type: getContentType(filename),
  }));
});

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

export function extractSubmissionId(response) {
  try {
    const body = JSON.parse(response.body);
    return body.submissionId ?? null;
  } catch {
    return null;
  }
}

export function selectFiles(count, iteration) {
  const maxFiles = Math.min(count, 5);
  const selected = [];
  for (let i = 0; i < maxFiles; i++) {
    const index = (iteration * maxFiles + i) % fileContents.length;
    selected.push(fileContents[index]);
  }
  const fileSize = selected.reduce((sum, f) => sum + f.content.length, 0);

  return { selectedFiles: selected, fileSize };
}

export function buildSequentialScenarios() {
  const scenarios = {};
  let offsetSeconds = 0;
  for (const name of Object.keys(SCENARIO_CONFIGS)) {
    const scenario = SCENARIO_CONFIGS[name];
    const durationSeconds = parseInt(scenario.duration);
    scenarios[name] = {
      executor: "constant-arrival-rate",
      rate: scenario.rate,
      timeUnit: scenario.timeUnit || "1s",
      duration: scenario.duration,
      preAllocatedVUs: 10,
      maxVUs: 50,
      startTime: `${offsetSeconds}s`,
      tags: {
        scenarioType: name,
      },
      gracefulStop: "100s",
    };
    offsetSeconds += durationSeconds + SCENARIO_GAP_SECONDS;
  }
  return scenarios;
}

export function pollSubmission(
  submissionId,
  startTime,
  totalTimeTrend,
  BASE_URL
) {
  for (let attempts = 1; attempts <= MAX_POLL_ATTEMPTS; attempts++) {
    const pollResponse = http.get(
      `${BASE_URL}/api/v1/submissions/${submissionId}`
    );
    const parseredBody = JSON.parse(pollResponse.body);
    console.log(
      `Status odpovědi při dotazování na stav: ${pollResponse.status} - submissionId: ${submissionId}`
    );
    if (pollResponse.status === 200 && parseredBody.savedAt !== null) {
      console.log(
        `Successfully retrieved submission: ${submissionId} in ${attempts} attempts - ${parseredBody.savedAt} `
      );
      console.log(
        `Start time: ${startTime} - Saved at: ${parseredBody.savedAt}`
      );
      const savedAtDate = new Date(parseredBody.savedAt);
      totalTimeTrend.add(savedAtDate.getTime() - startTime.getTime());
      console.log(
        `Total time for submission ${submissionId}: ${
          savedAtDate.getTime() - startTime.getTime()
        } ms`
      );
      return { success: true, attempts };
    }
    if (attempts < MAX_POLL_ATTEMPTS) {
      sleep(POLL_INTERVAL);
    }
  }
  return { success: false, attempts: MAX_POLL_ATTEMPTS };
}
