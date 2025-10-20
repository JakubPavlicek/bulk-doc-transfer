export type SubmissionId = number;

export type SubmissionState =
  | "ACCEPTED"
  | "PROCESSED"
  | "SAVED"
  | "RESPONSE_SENT";

export type CheckResult = "OK" | "ELECTRONIC_SIGNATURE" | "MALWARE";

export interface Submitter {
  id: number;
  email: string;
}

export interface SubmissionFileItem {
  id: number;
  name: string;
  type: string;
  size: number;
  createdAt: string; // ISO date
}

export interface Submission {
  id: SubmissionId;
  submitter: Submitter;
  subject: string;
  description?: string;
  referenceNumber: string;
  createdAt: string; // ISO date
  state: SubmissionState;
  checkResult: CheckResult;
  files: SubmissionFileItem[];
}

export interface SubmissionDetail {
  id: SubmissionId;
  submitterId: number;
  referenceNumber: string;
  createdAt: string; // ISO date
  state: SubmissionState;
  checkResult: CheckResult;
}

export interface CreateSubmissionRequest {
  email: string;
  subject: string; // title, max 2000, validated in UI
  description?: string;
  files: File[];
}

export interface ListParams {
  submitterEmail?: string;
  state?: SubmissionState;
  page?: number;
  size?: number;
}

const API_BASE = "/api/v1";

function buildQuery(params: Record<string, unknown>): string {
  const qs = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => {
    if (v === undefined || v === null || v === "") return;
    qs.set(k, String(v));
  });
  const s = qs.toString();
  return s ? `?${s}` : "";
}

export async function listSubmissions(
  params: ListParams = {}
): Promise<SubmissionDetail[]> {
  const res = await fetch(
    `${API_BASE}/submissions${buildQuery({
      submitterEmail: params.submitterEmail,
      state: params.state,
      page: params.page,
      size: params.size,
    })}`
  );
  if (!res.ok) throw new Error(`Failed to list submissions: ${res.status}`);
  return res.json();
}

export async function getSubmissionById(id: SubmissionId): Promise<Submission> {
  const res = await fetch(`${API_BASE}/submissions/${id}`);
  if (!res.ok) throw new Error(`Failed to get submission ${id}: ${res.status}`);
  return res.json();
}

export async function createSubmission(
  req: CreateSubmissionRequest,
  options: { mode: "sync" | "async" } = { mode: "sync" }
): Promise<Response> {
  const formData = new FormData();
  formData.set("email", req.email);
  formData.set("subject", req.subject);
  if (req.description) formData.set("description", req.description);
  req.files.forEach((file) => formData.append("files", file));

  // Same endpoint for both; server differentiates by behavior.
  const res = await fetch(`${API_BASE}/submissions`, {
    method: "POST",
    body: formData,
  });

  if (options.mode === "sync") {
    if (!res.ok) throw new Error(`Failed to create submission: ${res.status}`);
  } else {
    // async: API returns 202 on accept per spec
    if (res.status !== 202 && !res.ok) {
      throw new Error(`Failed to accept submission: ${res.status}`);
    }
  }
  return res;
}

export function isTerminalState(state: SubmissionState): boolean {
  return (
    state === "SAVED" || state === "RESPONSE_SENT" || state === "PROCESSED"
  );
}
