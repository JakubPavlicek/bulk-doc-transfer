"use client";

import { useCallback, useMemo, useState } from "react";
import { createSubmission } from "../lib/api";

type Mode = "sync" | "async";

export interface SubmissionFormProps {
  onSubmitted?: (opts: { mode: Mode }) => void;
}

export default function SubmissionForm({ onSubmitted }: SubmissionFormProps) {
  const [mode, setMode] = useState<Mode>("sync");
  const [email, setEmail] = useState("");
  const [subject, setSubject] = useState("");
  const [description, setDescription] = useState("");
  const [files, setFiles] = useState<File[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const subjectRemaining = useMemo(
    () => Math.max(0, 2000 - subject.length),
    [subject],
  );

  const onFileChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files) return;
    const next = Array.from(e.target.files);
    setFiles(next);
  }, []);

  const onDrop = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    const dropped = Array.from(e.dataTransfer.files || []);
    setFiles(dropped);
  }, []);

  const onDragOver = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
  }, []);

  const resetMessages = () => {
    setMessage(null);
    setError(null);
  };

  const onSubmit = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      resetMessages();
      if (!email || !subject || files.length === 0) {
        setError("Email, subject and at least one file are required.");
        return;
      }
      if (subject.length > 2000) {
        setError("Subject must be at most 2000 characters.");
        return;
      }
      try {
        setSubmitting(true);
        const res = await createSubmission(
          { email, subject, description, files },
          { mode },
        );
        if (mode === "sync") {
          if (res.ok) setMessage("Submission saved successfully.");
        } else {
          if (res.status === 202 || res.ok)
            setMessage("Submission accepted. Tracking asynchronously...");
        }
        onSubmitted?.({ mode });
      } catch (err) {
        setError((err as Error).message);
      } finally {
        setSubmitting(false);
      }
    },
    [email, subject, description, files, mode, onSubmitted],
  );

  return (
    <form onSubmit={onSubmit} className="w-full max-w-3xl flex flex-col gap-4">
      <div className="flex gap-4 items-center">
        <label className="flex items-center gap-2">
          <input
            type="radio"
            name="mode"
            value="sync"
            checked={mode === "sync"}
            onChange={() => setMode("sync")}
          />
          <span style={{ color: "var(--primary)" }}>Sync</span>
        </label>
        <label className="flex items-center gap-2">
          <input
            type="radio"
            name="mode"
            value="async"
            checked={mode === "async"}
            onChange={() => setMode("async")}
          />
          <span style={{ color: "var(--primary)" }}>Async</span>
        </label>
      </div>

      <div className="flex flex-col gap-1">
        <label className="font-medium" style={{ color: "var(--primary)" }}>
          Email
        </label>
        <input
          type="email"
          className="border rounded px-3 py-2"
          style={{ borderColor: "var(--border)" }}
          placeholder="you@example.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
      </div>

      <div className="flex flex-col gap-1">
        <label className="font-medium" style={{ color: "var(--primary)" }}>
          Title (max 2000)
        </label>
        <input
          type="text"
          className="border rounded px-3 py-2"
          style={{ borderColor: "var(--border)" }}
          placeholder="Enter title"
          value={subject}
          onChange={(e) => setSubject(e.target.value)}
          maxLength={2000}
          required
        />
        <span className="text-xs" style={{ color: "var(--primary-accent)" }}>
          {subjectRemaining} characters left
        </span>
      </div>

      <div className="flex flex-col gap-1">
        <label className="font-medium" style={{ color: "var(--primary)" }}>
          Description
        </label>
        <textarea
          className="border rounded px-3 py-2 min-h-24"
          style={{ borderColor: "var(--border)" }}
          placeholder="Optional description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
      </div>

      <div className="flex flex-col gap-2">
        <label className="font-medium" style={{ color: "var(--primary)" }}>
          Files
        </label>
        <input type="file" multiple onChange={onFileChange} />
        <div
          className="border border-dashed rounded p-4 text-center text-sm"
          style={{
            borderColor: "var(--border)",
            color: "var(--primary-accent)",
          }}
          onDrop={onDrop}
          onDragOver={onDragOver}
        >
          Drag and drop files here
        </div>
        {files.length > 0 && (
          <ul
            className="text-sm list-disc pl-5"
            style={{ color: "var(--primary)" }}
          >
            {files.map((f) => (
              <li key={`${f.name}-${f.size}`}>
                {f.name} ({f.type || "unknown"})
              </li>
            ))}
          </ul>
        )}
      </div>

      <div className="flex items-center gap-3">
        <button
          type="submit"
          className="brand-button rounded px-4 py-2 disabled:opacity-50"
          disabled={submitting}
        >
          {submitting
            ? mode === "sync"
              ? "Saving..."
              : "Submitting..."
            : mode === "sync"
            ? "Save synchronously"
            : "Submit asynchronously"}
        </button>
        {message && (
          <span className="text-sm" style={{ color: "#0c7a43" }}>
            {message}
          </span>
        )}
        {error && (
          <span className="text-sm" style={{ color: "#b91c1c" }}>
            {error}
          </span>
        )}
      </div>
    </form>
  );
}
