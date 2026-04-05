import { LaunchState } from "../App";
import { PetalCanvas } from "../components/PetalCanvas";

interface Props {
  launch: LaunchState;
  versions: string[];
  selectedVersion: string;
  onVersionChange: (v: string) => void;
  onPlay: () => void;
}

export function PlayPage({ launch, versions, selectedVersion, onVersionChange, onPlay }: Props) {
  const isLoading = launch.phase === "loading";
  const isDone = launch.phase === "done";
  const isError = launch.phase === "error";
  const canPlay = launch.phase === "idle" || launch.phase === "error";

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", overflowY: "auto", padding: "20px", gap: "12px" }}>

      {/* Hero Banner */}
      <div style={{
        position: "relative", borderRadius: "12px", overflow: "hidden",
        height: "200px", flexShrink: 0,
        background: "linear-gradient(135deg, #1a1025 0%, #2d1b3d 50%, #4a2040 100%)",
        border: "1px solid rgba(255,176,192,0.1)",
      }}>
        <PetalCanvas />
        <div style={{
          position: "relative", zIndex: 2, height: "100%",
          display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center",
          pointerEvents: "auto",
        }}>
          <div style={{ fontSize: "18px", fontWeight: "300", color: "#ccc", marginBottom: "4px" }}>
            Minecraft {selectedVersion}
          </div>
          <select
            value={selectedVersion}
            onChange={e => onVersionChange(e.target.value)}
            disabled={isLoading}
            style={{
              background: "rgba(0,0,0,0.5)", border: "1px solid rgba(255,176,192,0.2)",
              color: "#fff", borderRadius: "8px", padding: "10px 24px",
              fontSize: "14px", fontWeight: "600", cursor: "pointer", outline: "none",
            }}
          >
            {versions.map(v => <option key={v} value={v} style={{ background: "#1a1025" }}>{v}</option>)}
          </select>
        </div>
      </div>

      {/* Launch Button */}
      <button
        onClick={canPlay ? onPlay : undefined}
        disabled={!canPlay}
        style={{
          width: "100%", padding: "16px", flexShrink: 0,
          background: canPlay
            ? "linear-gradient(135deg, #FFB7C9, #F8A4B8, #E8899A)"
            : "rgba(255,176,192,0.15)",
          color: canPlay ? "#1a0f1a" : "#998899",
          border: "none", borderRadius: "10px",
          fontSize: "16px", fontWeight: "800", letterSpacing: "0.1em",
          cursor: canPlay ? "pointer" : "not-allowed",
          boxShadow: canPlay ? "0 4px 20px rgba(255,176,192,0.25)" : "none",
          display: "flex", alignItems: "center", justifyContent: "center", gap: "8px",
        }}
      >
        🌸 {isDone ? "LAUNCHED" : isLoading ? "LAUNCHING..." : isError ? "RETRY" : "LAUNCH FABRIC"}
      </button>

      {/* Progress / Error */}
      {isLoading && (
        <div style={{ flexShrink: 0 }}>
          <div style={{ height: "3px", background: "rgba(255,255,255,0.05)", borderRadius: "2px", overflow: "hidden" }}>
            <div style={{
              height: "100%", width: `${launch.progress}%`,
              background: "linear-gradient(90deg, #F8A4B8, #FFD1DC)",
              transition: "width 0.4s ease",
            }} />
          </div>
          <div style={{ fontSize: "11px", color: "#776070", marginTop: "4px" }}>{launch.status}</div>
        </div>
      )}
      {isError && <div style={{ fontSize: "12px", color: "#FF8888", flexShrink: 0 }}>{launch.status}</div>}

      {/* Bottom cards */}
      <div style={{ display: "flex", gap: "12px", flexShrink: 0 }}>
        <div style={{
          flex: 1, borderRadius: "10px",
          background: "rgba(255,255,255,0.03)", border: "1px solid rgba(255,176,192,0.08)",
          padding: "16px",
        }}>
          <div style={{ fontSize: "11px", fontWeight: "800", letterSpacing: "0.1em", color: "#998899", marginBottom: "12px" }}>
            LATEST FROM BLOOM
          </div>
          <div style={{ fontSize: "13px", color: "#776070", lineHeight: 1.5 }}>
            Welcome to Bloom Client! Your cherry blossom Minecraft experience.
          </div>
          <div style={{ fontSize: "12px", color: "#554455", marginTop: "8px" }}>
            Press Right Shift in-game to open the module menu.
          </div>
        </div>
        <div style={{
          flex: 1, borderRadius: "10px",
          background: "rgba(255,255,255,0.03)", border: "1px solid rgba(255,176,192,0.08)",
          padding: "16px",
        }}>
          <div style={{ fontSize: "11px", fontWeight: "800", letterSpacing: "0.1em", color: "#998899", marginBottom: "12px" }}>
            POPULAR SERVERS
          </div>
          {[
            { name: "Hypixel", players: "45,231" },
            { name: "BedWars Practice", players: "2,104" },
            { name: "PvP Legacy", players: "891" },
          ].map((s, i) => (
            <div key={i} style={{
              display: "flex", justifyContent: "space-between",
              padding: "5px 0", borderBottom: i < 2 ? "1px solid rgba(255,176,192,0.05)" : "none",
            }}>
              <span style={{ fontSize: "12px", color: "#caa" }}>{s.name}</span>
              <span style={{ fontSize: "11px", color: "#55DD88" }}>● {s.players}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
