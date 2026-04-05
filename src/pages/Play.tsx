import type { LaunchState } from "../App";
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
  const isIdle = launch.phase === "idle";

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", overflow: "auto" }}>
      {/* Main content area */}
      <div style={{ display: "flex", flex: 1, padding: "20px", gap: "16px", minHeight: 0 }}>

        {/* Left: Hero + Launch */}
        <div style={{ flex: 1, display: "flex", flexDirection: "column", gap: "12px" }}>

          {/* Hero Banner */}
          <div style={{
            position: "relative", borderRadius: "12px", overflow: "hidden",
            height: "220px", minHeight: "220px",
            background: "linear-gradient(135deg, #1a1025 0%, #2d1b3d 50%, #4a2040 100%)",
            border: "1px solid rgba(255,176,192,0.1)",
          }}>
            <PetalCanvas />
            <div style={{
              position: "absolute", inset: 0,
              display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center",
              zIndex: 1,
            }}>
              <div style={{
                fontSize: "18px", fontWeight: "300", color: "#ccc", marginBottom: "4px",
              }}>
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
                  backdropFilter: "blur(8px)", textAlign: "center",
                }}
              >
                {versions.map(v => <option key={v} value={v} style={{ background: "#1a1025" }}>{v}</option>)}
              </select>
            </div>

            {/* Side icons */}
            <div style={{
              position: "absolute", right: "16px", top: "50%", transform: "translateY(-50%)",
              display: "flex", flexDirection: "column", gap: "12px", zIndex: 1,
            }}>
              {["💬", "👥", ">_"].map((icon, i) => (
                <div key={i} style={{
                  width: "36px", height: "36px", borderRadius: "8px",
                  background: "rgba(0,0,0,0.4)", border: "1px solid rgba(255,176,192,0.1)",
                  display: "flex", alignItems: "center", justifyContent: "center",
                  fontSize: "14px", color: "#998899", cursor: "pointer",
                }}>{icon}</div>
              ))}
            </div>
          </div>

          {/* Launch Button */}
          <button
            onClick={isIdle || isError ? onPlay : undefined}
            disabled={isLoading || isDone}
            style={{
              width: "100%", padding: "16px",
              background: isLoading || isDone
                ? "rgba(255,176,192,0.15)"
                : "linear-gradient(135deg, #FFB7C9, #F8A4B8, #E8899A)",
              color: isLoading || isDone ? "#998899" : "#1a0f1a",
              border: "none", borderRadius: "10px",
              fontSize: "16px", fontWeight: "800", letterSpacing: "0.1em",
              cursor: isLoading || isDone ? "not-allowed" : "pointer",
              boxShadow: isIdle || isError ? "0 4px 20px rgba(255,176,192,0.25)" : "none",
              transition: "all 0.2s",
              display: "flex", alignItems: "center", justifyContent: "center", gap: "8px",
            }}
            onMouseEnter={e => { if (isIdle || isError) { e.currentTarget.style.transform = "translateY(-1px)"; e.currentTarget.style.boxShadow = "0 6px 30px rgba(255,176,192,0.4)"; }}}
            onMouseLeave={e => { e.currentTarget.style.transform = ""; e.currentTarget.style.boxShadow = isIdle || isError ? "0 4px 20px rgba(255,176,192,0.25)" : "none"; }}
          >
            <span style={{ fontSize: "18px" }}>🌸</span>
            {isDone ? "LAUNCHED" : isLoading ? "LAUNCHING..." : isError ? "RETRY" : "LAUNCH FABRIC"}
          </button>

          {/* Progress bar */}
          {(isLoading || isDone) && (
            <div>
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
          {isError && (
            <div style={{ fontSize: "11px", color: "#FF8888" }}>{launch.status}</div>
          )}

          {/* Bottom cards */}
          <div style={{ display: "flex", gap: "12px", flex: 1, minHeight: "140px" }}>
            {/* News card */}
            <div style={{
              flex: 1, borderRadius: "10px",
              background: "rgba(255,255,255,0.03)", border: "1px solid rgba(255,176,192,0.08)",
              padding: "16px", overflow: "hidden",
            }}>
              <div style={{ fontSize: "11px", fontWeight: "800", letterSpacing: "0.1em", color: "#998899", marginBottom: "12px" }}>
                LATEST FROM BLOOM
              </div>
              <div style={{ fontSize: "13px", color: "#776070", lineHeight: 1.5 }}>
                Welcome to Bloom Client! Your cherry blossom Minecraft experience.
              </div>
              <div style={{ fontSize: "12px", color: "#554455", marginTop: "8px" }}>
                Modules: Toggle Sprint, FPS Display, Fullbright, Zoom, Coordinates, Armor Status
              </div>
              <div style={{ fontSize: "12px", color: "#554455", marginTop: "4px" }}>
                Press Right Shift in-game to open the module menu.
              </div>
            </div>

            {/* Quick links card */}
            <div style={{
              flex: 1, borderRadius: "10px",
              background: "rgba(255,255,255,0.03)", border: "1px solid rgba(255,176,192,0.08)",
              padding: "16px",
            }}>
              <div style={{ fontSize: "11px", fontWeight: "800", letterSpacing: "0.1em", color: "#998899", marginBottom: "12px" }}>
                POPULAR SERVERS
              </div>
              {[
                { name: "Hypixel", ip: "mc.hypixel.net", players: "45,231" },
                { name: "BedWars Practice", ip: "bedwarspractice.club", players: "2,104" },
                { name: "PvP Legacy", ip: "eu.pvplegacy.net", players: "891" },
                { name: "MMC", ip: "mmc.net", players: "634" },
              ].map((s, i) => (
                <div key={i} style={{
                  display: "flex", alignItems: "center", justifyContent: "space-between",
                  padding: "6px 0", borderBottom: i < 3 ? "1px solid rgba(255,176,192,0.05)" : "none",
                }}>
                  <span style={{ fontSize: "12px", color: "#caa" }}>{s.name}</span>
                  <span style={{ fontSize: "11px", color: "#55DD88" }}>● {s.players}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right: Bloom logo decoration */}
        <div style={{
          width: "200px", minWidth: "200px",
          display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center",
          opacity: 0.15,
        }}>
          <div style={{ fontSize: "120px" }}>🌸</div>
        </div>
      </div>

      {/* Footer */}
      <div style={{
        padding: "8px 20px", borderTop: "1px solid rgba(255,176,192,0.06)",
        display: "flex", justifyContent: "center",
        fontSize: "11px", color: "#443344",
      }}>
        © Bloom Client 2026. Cherry blossom Minecraft experience.
      </div>
    </div>
  );
}
