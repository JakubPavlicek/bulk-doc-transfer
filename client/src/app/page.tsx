import SubmissionForm from "../components/SubmissionForm";
import SubmissionsTable from "../components/SubmissionsTable";

export default function Home() {
  return (
    <div className="min-h-screen p-8 pb-20 sm:p-12 font-sans">
      <main className="mx-auto max-w-6xl flex flex-col gap-10">
        <header className="flex items-end justify-between brand-section p-5">
          <div className="flex flex-col">
            <h1
              className="text-2xl font-semibold"
              style={{ color: "var(--primary)" }}
            >
              Bulk Document Transfer
            </h1>
            <p className="text-sm" style={{ color: "var(--primary-accent)" }}>
              Create a submission and upload files (sync or async)
            </p>
          </div>
        </header>

        <section className="brand-card p-6">
          <SubmissionForm />
        </section>

        <section className="brand-card p-6">
          <SubmissionsTable />
        </section>
      </main>
    </div>
  );
}
