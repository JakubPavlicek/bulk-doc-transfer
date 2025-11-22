import { SharedArray } from "k6/data";
import http from "k6/http";
import { sleep } from "k6";

/** Scenario configurations */
export const SCENARIO_CONFIGS = {
  // Low stress - 1 file
  // 20 req/s * 120s = 2,400 submissions. Max runtime: ~20 minutes
  "low-1file": {
    fileCount: 1,
    rate: 20,
    timeUnit: "1s",
    duration: "120s",
    allowedTimeInSeconds: 1200, // 120s duration + 1080s (18 min) buffer - total ~20 min
    pollInterval: 3, // Fast polling for low stress
    maxPollAttempts: 360, // Covers 1080s processing buffer (360 * 3s = 1080s)
  }, // 20 req/s

  // Medium stress - 1 file
  // 50 req/s * 120s = 6,000 submissions. Max runtime: ~22 minutes
  //   "mid-1file": {
  //     fileCount: 1,
  //     rate: 50,
  //     timeUnit: "1s",
  //     duration: "120s",
  //     allowedTimeInSeconds: 1320, // 120s duration + 1200s (20 min) buffer - total ~22 min
  //     pollInterval: 5, // Moderate polling
  //     maxPollAttempts: 240, // Covers 1200s processing buffer (240 * 5s = 1200s)
  //   }, // 50 req/s

  //   // High stress - 1 file
  //   // 100 req/s * 120s = 12,000 submissions. Max runtime: ~25 minutes
  //   "high-1file": {
  //     fileCount: 1,
  //     rate: 100,
  //     timeUnit: "1s",
  //     duration: "120s",
  //     allowedTimeInSeconds: 1500, // 120s duration + 1380s (23 min) buffer - total ~25 min
  //     pollInterval: 8, // Slower polling for high stress
  //     maxPollAttempts: 172, // Covers 1380s processing buffer (172 * 8s = 1376s ≈ 1380s)
  //   }, // 100 req/s

  //   // Low stress - 3 files
  //   // 15 req/s * 120s = 1,800 submissions. Max runtime: ~20 minutes
  //   "low-3files": {
  //     fileCount: 3,
  //     rate: 15,
  //     timeUnit: "1s",
  //     duration: "120s",
  //     allowedTimeInSeconds: 1200, // 120s duration + 1080s (18 min) buffer - total ~20 min
  //     pollInterval: 3, // Slightly slower due to more files
  //     maxPollAttempts: 360, // Covers 1080s processing buffer (360 * 3s = 1080s)
  //   }, // 15 req/s

  //   // Medium stress - 3 files
  //   // 40 req/s * 120s = 4,800 submissions. Max runtime: ~22 minutes
  //   "mid-3files": {
  //     fileCount: 3,
  //     rate: 40,
  //     timeUnit: "1s",
  //     duration: "120s",
  //     allowedTimeInSeconds: 1320, // 120s duration + 1200s (20 min) buffer - total ~22 min
  //     pollInterval: 6, // Moderate polling with multiple files
  //     maxPollAttempts: 200, // Covers 1200s processing buffer (200 * 6s = 1200s)
  //   }, // 40 req/s

  //   // High stress - 3 files
  //   // 80 req/s * 120s = 9,600 submissions. Max runtime: ~25 minutes
  //   "high-3files": {
  //     fileCount: 3,
  //     rate: 80,
  //     timeUnit: "1s",
  //     duration: "120s",
  //     allowedTimeInSeconds: 1500, // 120s duration + 1380s (23 min) buffer - total ~25 min
  //     pollInterval: 10, // Slower polling for high stress + multiple files
  //     maxPollAttempts: 138, // Covers 1380s processing buffer (138 * 10s = 1380s)
  //   }, // 80 req/s

  //   // Low stress - 5 files
  //   // 10 req/s * 120s = 1,200 submissions. Max runtime: ~20 minutes
  //   "low-5files": {
  //     fileCount: 5,
  //     rate: 10,
  //     timeUnit: "1s",
  //     duration: "120s",
  //     allowedTimeInSeconds: 1200, // 120s duration + 1080s (18 min) buffer - total ~20 min
  //     pollInterval: 4, // Slower due to many files
  //     maxPollAttempts: 270, // Covers 1080s processing buffer (270 * 4s = 1080s)
  //   }, // 10 req/s

  //   // Medium stress - 5 files
  //   // 30 req/s * 120s = 3,600 submissions. Max runtime: ~22 minutes
  //   "mid-5files": {
  //     fileCount: 5,
  //     rate: 30,
  //     timeUnit: "1s",
  //     duration: "120s",
  //     allowedTimeInSeconds: 1320, // 120s duration + 1200s (20 min) buffer - total ~22 min
  //     pollInterval: 7, // Moderate polling with many files
  //     maxPollAttempts: 171, // Covers 1200s processing buffer (171 * 7s = 1197s ≈ 1200s)
  //   }, // 30 req/s

  //   // High stress - 5 files
  //   // 60 req/s * 120s = 7,200 submissions. Max runtime: ~25 minutes
  //   "high-5files": {
  //     fileCount: 5,
  //     rate: 60,
  //     timeUnit: "1s",
  //     duration: "120s",
  //     allowedTimeInSeconds: 1500, // 120s duration + 1380s (23 min) buffer - total ~25 min
  //     pollInterval: 12, // Slowest polling for highest stress + many files
  //     maxPollAttempts: 115, // Covers 1380s processing buffer (115 * 12s = 1380s)
  //   }, // 60 req/s
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
