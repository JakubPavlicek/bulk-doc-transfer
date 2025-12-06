import { SharedArray } from "k6/data";
import http from "k6/http";
import { sleep } from "k6";

// The App to be tested. One of: synchronous, rabbitmq, wildfly
export const APP = __ENV.APP || 'synchronous';

export const BASE_URL = getBaseUrl(APP);

function getBaseUrl(app) {
  switch (app) {
    case "synchronous": return "http://localhost:8010";
    case "rabbitmq": return "http://localhost:8020";
    case "wildfly": return "http://localhost:8080";
  }
}

/** Scenario configurations */
export const SCENARIO_CONFIGS = {
  // Low stress - 1 file (common single file submission)
  // 20 req/s * 60s = 1,200 submissions. Max runtime: ~20 minutes
  "low-1file": {
    fileCount: 1,
    rate: 20,
    timeUnit: "1s",
    duration: "60s",
    allowedTimeInSeconds: 1200, // 60s duration + 1140s (19 min) buffer - total ~20 min
    pollInterval: 3, // Fast polling for low stress
    maxPollAttempts: 380, // Covers 1140s processing buffer (380 * 3s = 1140s)
  }, // 20 req/s

  // Medium stress - 3 files (typical multi-file submission)
  // 40 req/s * 60s = 2,400 submissions. Max runtime: ~22 minutes
  "mid-3files": {
    fileCount: 3,
    rate: 40,
    timeUnit: "1s",
    duration: "60s",
    allowedTimeInSeconds: 1320, // 60s duration + 1260s (21 min) buffer - total ~22 min
    pollInterval: 6, // Moderate polling with multiple files
    maxPollAttempts: 210, // Covers 1260s processing buffer (210 * 6s = 1260s)
  }, // 40 req/s

  // High stress - 3 files (peak load with typical submission)
  // 80 req/s * 60s = 4,800 submissions. Max runtime: ~25 minutes
  "high-3files": {
    fileCount: 3,
    rate: 80,
    timeUnit: "1s",
    duration: "60s",
    allowedTimeInSeconds: 1500, // 60s duration + 1440s (24 min) buffer - total ~25 min
    pollInterval: 10, // Slower polling for high stress + multiple files
    maxPollAttempts: 144, // Covers 1440s processing buffer (144 * 10s = 1440s)
  }, // 80 req/s

  // Medium stress - 5 files (maximum files, moderate load)
  // 30 req/s * 60s = 1,800 submissions. Max runtime: ~22 minutes
  "mid-5files": {
    fileCount: 5,
    rate: 30,
    timeUnit: "1s",
    duration: "60s",
    allowedTimeInSeconds: 1320, // 60s duration + 1260s (21 min) buffer - total ~22 min
    pollInterval: 7, // Moderate polling with many files
    maxPollAttempts: 180, // Covers 1260s processing buffer (180 * 7s = 1260s)
  }, // 30 req/s

  // High stress - 5 files (maximum files, peak load)
  // 60 req/s * 60s = 3,600 submissions. Max runtime: ~25 minutes
  "high-5files": {
    fileCount: 5,
    rate: 60,
    timeUnit: "1s",
    duration: "60s",
    allowedTimeInSeconds: 1500, // 60s duration + 1440s (24 min) buffer - total ~25 min
    pollInterval: 12, // Slowest polling for highest stress + many files
    maxPollAttempts: 120, // Covers 1440s processing buffer (120 * 12s = 1440s)
  }, // 60 req/s
};

// Default poll configuration (fallback if not specified in scenario)
const DEFAULT_POLL_INTERVAL = 5;
const DEFAULT_MAX_POLL_ATTEMPTS = 10;

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
    scenarios[name] = {
      executor: "constant-arrival-rate",
      rate: scenario.rate,
      timeUnit: scenario.timeUnit || "1s",
      duration: scenario.duration,
      preAllocatedVUs: 10,
      maxVUs: 100000,
      startTime: `${offsetSeconds}s`,
      tags: {
        scenarioType: name,
      },
      gracefulStop: `${scenario.allowedTimeInSeconds}s`,
    };
    offsetSeconds += scenario.allowedTimeInSeconds;
  }
  return scenarios;
}

export function pollSubmission(
  submissionId,
  startTime,
  totalTimeTrend,
  BASE_URL,
  pollInterval = DEFAULT_POLL_INTERVAL,
  maxPollAttempts = DEFAULT_MAX_POLL_ATTEMPTS
) {
  for (let attempts = 1; attempts <= maxPollAttempts; attempts++) {
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
    if (attempts < maxPollAttempts) {
      sleep(pollInterval);
    }
  }
  return { success: false, attempts: maxPollAttempts };
}
