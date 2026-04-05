import { useState, useEffect, useRef } from "react";
import { listen } from "@tauri-apps/api/event";

interface LogEntry {
  time: string;
  level: "info" | "warn" | "error" | "debug";
  message: string;
}

export function ConsolePage() {
  const [logs, setLogs] = useState<LogEntry[]>([]);
  const [filter, setFilter] = useState("");
  const [levels, setLevels] = useState({ info: true, warn: true, error: true, debug: true });
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // Listen for launch progress as log entries
    const unsubs: (() => void)[] = [];

    listen<{ pct: number; msg: string }>("launch_progress", e => {
      addLog("info", e.payload.msg);
    }).then(fn => unsubs.push(fn));

    listen<string>("auth_error", e => {
      addLog("error", `Auth error: ${e.payload}`);
    }).then(fn => unsubs.push(fn));

    listen<any>("auth_success", e => {
      addLog("info", `Logged in as ${e.payload.username}`);
    }).then(fn => unsubs.push(fn));

    listen<any>("auth_code", e => {
      addLog("info", `Auth code: ${e.payload.code} — go to ${e.payload.url}`);
    }).then(fn => unsubs.push(fn));

    // Add initial log
    addLog("info", "Bloom Client started");

    return () => unsubs.forEach(fn => fn());
  }, []);

  function addLog(level: LogEntry["level"], message: string) {
    const time = new Date().toLocaleTimeString("en-US", { hour12: false });
    setLogs(prev => [...prev.slice(-500), { time, level, message }]);
    setTimeout(() => bottomRef.current?.scrollIntoView({ behavior: "smooth" }), 50);
  }

  function clearLogs() { setLogs([]); }

  function copyLogs() {
    const text = logs.map(l => `[${l.time}] [${l.level.toUpperCase()}] ${l.message}`).join("\n");
    navigator.clipboard.writeText(text);
  }

  const filtered = logs.filter(l =>
    levels[l.level] && (!filter || l.message.toLowerCase().includes(filter.toLowerCase()))
  );

  const levelColor = { info: "#55DD88", warn: "#DDBB55", error: "#FF6666", debug: "#998899" };

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", padding: "20px", gap: "12px" }}>
      {/* Header */}
      <div style={{ display: "flex", alignItems: "center", gap: "12px", flexShrink: 0, flexWrap: "wrap" }}>
        {/* Level toggles */}
        {(["info", "warn", "error", "debug"] as const).map(level => (
          <label key={level} style={{
            display: "flex", alignItems: "center", gap: "4px", cursor: "pointer",
            fontSize: "12px", color: levels[level] ? levelColor[level] : "#554455",
          }}>
            <input type="checkbox" checked={levels[level]}
              onChange={() => setLevels(prev => ({ ...prev, [level]: !prev[level] }))}
              style={{ accentColor: levelColor[level] }}
            />
            {level.charAt(0).toUpperCase() + level.slice(1)}
          </label>
        ))}

        <div style={{ flex: 1 }} />

        <button onClick={clearLogs} style={{
          background: "rgba(255,255,255,0.05)", border: "1px solid rgba(255,176,192,0.1)",
          color: "#998899", borderRadius: "6px", padding: "6px 12px", fontSize: "11px", cursor: "pointer",
        }}>Clear Logs</button>

        <button onClick={copyLogs} style={{
          background: "rgba(255,255,255,0.05)", border: "1px solid rgba(255,176,192,0.1)",
          color: "#998899", borderRadius: "6px", padding: "6px 12px", fontSize: "11px", cursor: "pointer",
        }}>Copy Logs</button>
      </div>

      {/* Search */}
      <input value={filter} onChange={e => setFilter(e.target.value)}
        placeholder="Search Logs"
        style={{
          background: "rgba(255,255,255,0.04)", border: "1px solid rgba(255,176,192,0.1)",
          color: "#fff", borderRadius: "6px", padding: "8px 12px", fontSize: "12px",
          outline: "none", flexShrink: 0,
        }}
      />

      {/* Stats bar */}
      <div style={{
        display: "flex", gap: "24px", padding: "8px 12px", flexShrink: 0,
        background: "rgba(255,255,255,0.03)", borderRadius: "6px",
        fontSize: "12px", fontWeight: "700",
      }}>
        <span style={{ color: "#caa" }}>Entries: <span style={{ color: "#998899" }}>{filtered.length}</span></span>
        <span style={{ color: "#caa" }}>Errors: <span style={{ color: "#FF6666" }}>{logs.filter(l => l.level === "error").length}</span></span>
        <span style={{ color: "#caa" }}>Warnings: <span style={{ color: "#DDBB55" }}>{logs.filter(l => l.level === "warn").length}</span></span>
      </div>

      {/* Log output */}
      <div style={{
        flex: 1, overflow: "auto", borderRadius: "8px",
        background: "rgba(0,0,0,0.3)", border: "1px solid rgba(255,176,192,0.06)",
        padding: "8px", fontFamily: "monospace", fontSize: "11px", lineHeight: 1.6,
      }}>
        {filtered.length === 0 && (
          <div style={{ color: "#554455", padding: "20px", textAlign: "center" }}>No logs yet — launch the game to see output</div>
        )}
        {filtered.map((log, i) => (
          <div key={i} style={{ color: levelColor[log.level], borderBottom: "1px solid rgba(255,255,255,0.02)", padding: "2px 0" }}>
            <span style={{ color: "#554455" }}>[{log.time}]</span>{" "}
            <span style={{ color: levelColor[log.level], fontWeight: "700" }}>[{log.level.toUpperCase()}]</span>{" "}
            <span style={{ color: log.level === "error" ? "#FF8888" : "#caa" }}>{log.message}</span>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>
    </div>
  );
}
