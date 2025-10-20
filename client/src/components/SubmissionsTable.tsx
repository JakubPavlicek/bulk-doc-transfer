"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import {
  SubmissionDetail,
  SubmissionState,
  isTerminalState,
  listSubmissions,
} from "../lib/api";

export interface SubmissionsTableProps {
  pollIntervalMs?: number;
}

function stateBadge(state: SubmissionState) {
  const base = "px-2 py-1 rounded text-xs";
  switch (state) {
    case "ACCEPTED":
      return (
        <span className={`${base} bg-blue-100 text-blue-800`}>ACCEPTED</span>
      );
    case "PROCESSED":
      return (
        <span className={`${base} bg-purple-100 text-purple-800`}>
          PROCESSED
        </span>
      );
    case "SAVED":
      return (
        <span className={`${base} bg-green-100 text-green-800`}>SAVED</span>
      );
    case "RESPONSE_SENT":
      return (
        <span className={`${base} bg-emerald-100 text-emerald-800`}>
          RESPONSE SENT
        </span>
      );
    default:
      return (
        <span className={`${base} bg-gray-100 text-gray-800`}>{state}</span>
      );
  }
}

export default function SubmissionsTable({
  pollIntervalMs = 4000,
}: SubmissionsTableProps) {
  const [rows, setRows] = useState<SubmissionDetail[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const timerRef = useRef<number | null>(null);

  const hasPending = useMemo(
    () => rows.some((r) => !isTerminalState(r.state)),
    [rows]
  );

  const fetchOnce = useCallback(async () => {
    try {
      setLoading(true);
      const data = await listSubmissions({ page: 0, size: 10 });
      setRows(data);
      setError(null);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchOnce();
  }, [fetchOnce]);

  useEffect(() => {
    if (timerRef.current) {
      window.clearInterval(timerRef.current);
      timerRef.current = null;
    }
    if (hasPending) {
      timerRef.current = window.setInterval(fetchOnce, pollIntervalMs);
    }
    return () => {
      if (timerRef.current) {
        window.clearInterval(timerRef.current);
        timerRef.current = null;
      }
    };
  }, [hasPending, fetchOnce, pollIntervalMs]);

  return (
    <div className="w-full max-w-5xl">
      <div className="flex items-center justify-between mb-2">
        <h2 className="font-semibold" style={{ color: "var(--primary)" }}>
          Submissions
        </h2>
        <button
          className="text-sm underline"
          style={{ color: "var(--primary-accent)" }}
          onClick={fetchOnce}
          disabled={loading}
        >
          Refresh
        </button>
      </div>
      {error && (
        <div className="text-sm mb-2" style={{ color: "#b91c1c" }}>
          {error}
        </div>
      )}
      <div className="overflow-x-auto">
        <table className="min-w-full border text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th
                className="text-left p-2 border"
                style={{ color: "var(--primary)" }}
              >
                ID
              </th>
              <th
                className="text-left p-2 border"
                style={{ color: "var(--primary)" }}
              >
                Reference
              </th>
              <th
                className="text-left p-2 border"
                style={{ color: "var(--primary)" }}
              >
                Created
              </th>
              <th
                className="text-left p-2 border"
                style={{ color: "var(--primary)" }}
              >
                State
              </th>
              <th
                className="text-left p-2 border"
                style={{ color: "var(--primary)" }}
              >
                Check
              </th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => (
              <tr key={r.id} className="odd:bg-white even:bg-gray-50">
                <td
                  className="p-2 border align-top"
                  style={{ color: "var(--primary)" }}
                >
                  {r.id}
                </td>
                <td
                  className="p-2 border align-top"
                  style={{ color: "var(--primary)" }}
                >
                  {r.referenceNumber}
                </td>
                <td className="p-2 border align-top">
                  {new Date(r.createdAt).toLocaleString()}
                </td>
                <td className="p-2 border align-top">{stateBadge(r.state)}</td>
                <td
                  className="p-2 border align-top"
                  style={{ color: "var(--primary-accent)" }}
                >
                  {r.checkResult}
                </td>
              </tr>
            ))}
            {rows.length === 0 && !loading && (
              <tr>
                <td colSpan={5} className="p-4 text-center text-gray-500">
                  No submissions found.
                </td>
              </tr>
            )}
            {loading && (
              <tr>
                <td colSpan={5} className="p-4 text-center text-gray-500">
                  Loading...
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
