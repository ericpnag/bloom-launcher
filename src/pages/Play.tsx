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
  const canPlay = launch.phase === "idle" || launch.phase === "error";

  function handleClick() {
    window.alert("Button clicked! Phase: " + launch.phase + ", Version: " + selectedVersion);
    if (canPlay) onPlay();
  }

  return (
    <div style={{ padding: "20px", display: "flex", flexDirection: "column", gap: "12px", height: "100%", overflowY: "auto" }}>

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
        }}>
          <div style={{ fontSize: "18px", fontWeight: "300", color: "#ccc", marginBottom: "4px" }}>
            Minecraft {selectedVersion}
          </div>
          <select
            value={selectedVersion}
            onChange={e => onVersionChange(e.target.value)}
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

      {/* Launch Button - simple direct onclick */}
      <button
        type="button"
        onClick={handleClick}
        style={{
          width: "100%", padding: "18px", flexShrink: 0,
          background: canPlay
            ? "linear-gradient(135deg, #FFB7C9, #F8A4B8, #E8899A)"
            : "rgba(255,176,192,0.15)",
          color: canPlay ? "#1a0f1a" : "#998899",
          border: "none", borderRadius: "10px",
          fontSize: "16px", fontWeight: "800", letterSpacing: "0.1em",
          cursor: "pointer",
          zIndex: 10,
          position: "relative",
        }}
      >
        {launch.phase === "done" ? "LAUNCHED" : launch.phase === "loading" ? "LAUNCHING..." : launch.phase === "error" ? "RETRY - " + launch.status : "LAUNCH FABRIC"}
      </button>

      {/* Progress */}
      {launch.phase === "loading" && (
        <div>
          <div style={{ height: "4px", background: "rgba(255,255,255,0.05)", borderRadius: "2px", overflow: "hidden" }}>
            <div style={{
              height: "100%", width: `${launch.progress}%`,
              background: "linear-gradient(90deg, #F8A4B8, #FFD1DC)",
              transition: "width 0.4s ease",
            }} />
          </div>
          <div style={{ fontSize: "12px", color: "#776070", marginTop: "4px" }}>{launch.status}</div>
        </div>
      )}
      {launch.phase === "error" && (
        <div style={{ fontSize: "12px", color: "#FF8888", padding: "8px", background: "rgba(255,0,0,0.1)", borderRadius: "6px" }}>
          {launch.status}
        </div>
      )}

      {/* Info cards */}
      <div style={{ display: "flex", gap: "12px", flexShrink: 0 }}>
        <div style={{
          flex: 1, borderRadius: "10px",
          background: "rgba(255,255,255,0.03)", border: "1px solid rgba(255,176,192,0.08)",
          padding: "16px",
        }}>
          <div style={{ fontSize: "11px", fontWeight: "800", letterSpacing: "0.1em", color: "#998899", marginBottom: "8px" }}>
            BLOOM CLIENT
          </div>
          <div style={{ fontSize: "12px", color: "#776070", lineHeight: 1.5 }}>
            Press Right Shift in-game for modules. 11 modules available.
          </div>
        </div>
        <div style={{
          flex: 1, borderRadius: "10px",
          background: "rgba(255,255,255,0.03)", border: "1px solid rgba(255,176,192,0.08)",
          padding: "16px",
        }}>
          <div style={{ fontSize: "11px", fontWeight: "800", letterSpacing: "0.1em", color: "#998899", marginBottom: "8px" }}>
            STATUS
          </div>
          <div style={{ fontSize: "12px", color: "#776070" }}>
            Phase: {launch.phase} | Version: {selectedVersion} | Versions loaded: {versions.length}
          </div>
        </div>
      </div>
    </div>
  );
}
