import { useState, useEffect } from "react";
import { invoke } from "@tauri-apps/api/core";
import { listen } from "@tauri-apps/api/event";
import { TopNav } from "./components/TopNav";
import { PetalCanvas } from "./components/PetalCanvas";
import { HeroBanner } from "./components/HeroBanner";
import { LoginModal } from "./components/LoginModal";
import "./App.css";

export type Page = "play" | "mods" | "installed" | "shaders" | "texturepacks" | "shop" | "console" | "settings";
export type LaunchPhase = "idle" | "loading" | "done" | "error";

export interface LaunchState {
  phase: LaunchPhase;
  progress: number;
  status: string;
}

export interface AccountInfo {
  username: string;
  uuid: string;
  accessToken: string;
}

export default function App() {
  const [versions, setVersions] = useState<string[]>(["1.21.11"]);
  const [selectedVersion, setSelectedVersion] = useState("1.21.11");
  const [phase, setPhase] = useState<LaunchPhase>("idle");
  const [progress, setProgress] = useState(0);
  const [status, setStatus] = useState("Ready");
  const [account, setAccount] = useState<AccountInfo | null>(null);
  const [loginModal, setLoginModal] = useState<{ phase: "waiting" | "code" | "error"; code?: string; url?: string; error?: string } | null>(null);
  const [page, setPage] = useState<Page>("play");

  useEffect(() => {
    invoke<any>("get_account").then(a => {
      if (a) setAccount({ username: a.username, uuid: a.uuid, accessToken: a.access_token });
    }).catch(() => {});

    invoke<string[]>("get_versions").then(v => {
      setVersions(v);
      if (v.includes("1.21.11")) setSelectedVersion("1.21.11");
      else if (v.length) setSelectedVersion(v[0]);
    }).catch(() => {});

    listen<{ url: string; code: string }>("auth_code", e => {
      setLoginModal({ phase: "code", url: e.payload.url, code: e.payload.code });
    });
    listen<any>("auth_success", e => {
      const acc = { username: e.payload.username, uuid: e.payload.uuid, accessToken: e.payload.accessToken };
      setAccount(acc);
      invoke("save_account", { account: { username: acc.username, uuid: acc.uuid, access_token: acc.accessToken, refresh_token: e.payload.refreshToken } });
      setLoginModal(null);
    });
    listen<string>("auth_error", e => {
      setLoginModal({ phase: "error", error: e.payload });
    });
  }, []);

  async function handleLaunch() {
    setPhase("loading");
    setProgress(0);
    setStatus("Starting...");

    const unlisteners: Array<() => void> = [];
    const cleanup = () => { for (const fn of unlisteners) fn(); };

    const u1 = await listen<{ pct: number; msg: string }>("launch_progress", e => {
      setProgress(e.payload.pct);
      setStatus(e.payload.msg);
    });
    unlisteners.push(u1);

    const u2 = await listen<any>("launch_done", () => {
      setPhase("done");
      setStatus("Game launched!");
      setTimeout(() => { setPhase("idle"); setStatus("Ready"); cleanup(); }, 3000);
    });
    unlisteners.push(u2);

    const u3 = await listen<{ error: string }>("launch_error", e => {
      setPhase("error");
      setStatus(e.payload.error);
      cleanup();
    });
    unlisteners.push(u3);

    try {
      await invoke("launch_minecraft", {
        version: selectedVersion,
        username: account?.username ?? null,
        uuid: account?.uuid ?? null,
        accessToken: account?.accessToken ?? null,
      });
    } catch (e: any) {
      setPhase("error");
      setStatus(String(e));
      cleanup();
    }
  }

  async function handleLogin() {
    setLoginModal({ phase: "waiting" });
    try { await invoke("start_microsoft_login"); } catch (e: any) { setLoginModal({ phase: "error", error: String(e) }); }
  }

  const canPlay = phase === "idle" || phase === "error";

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100vh", width: "100vw", position: "relative" }}>
      {/* Subtle petal animation */}
      <div style={{ position: "fixed", inset: 0, pointerEvents: "none", zIndex: 0, opacity: 0.25 }}>
        <PetalCanvas />
      </div>

      <TopNav
        page={page}
        onNavigate={setPage}
        account={account}
        onLogin={handleLogin}
        onLogout={() => { invoke("logout"); setAccount(null); }}
      />

      {/* Content */}
      <div style={{ flex: 1, overflow: "auto", position: "relative", zIndex: 1 }}>
        {page === "play" && (
          <div className="fade-in" style={{ padding: "24px", display: "flex", flexDirection: "column", gap: "16px" }}>
            {/* Hero Banner with animated flowers */}
            <HeroBanner
              selectedVersion={selectedVersion}
              versions={versions}
              onVersionChange={setSelectedVersion}
            />

            {/* Launch Button */}
            <button
              onClick={canPlay ? handleLaunch : undefined}
              disabled={!canPlay}
              style={{
                width: "100%", padding: "16px", border: "none", borderRadius: "var(--radius)",
                background: canPlay
                  ? "linear-gradient(135deg, var(--pink-300), var(--pink-400), var(--pink-500))"
                  : "rgba(255,176,192,0.06)",
                color: canPlay ? "#1a0a12" : "var(--text-faint)",
                fontSize: "13px", fontWeight: "800", letterSpacing: "0.14em",
                cursor: canPlay ? "pointer" : "default",
                boxShadow: canPlay ? "0 4px 20px var(--pink-glow)" : "none",
                transition: "all 0.25s ease",
                fontFamily: "inherit",
                animation: phase === "idle" ? "pulse-glow 4s ease-in-out infinite" : "none",
                textTransform: "uppercase",
              }}
              onMouseEnter={e => { if (canPlay) { e.currentTarget.style.transform = "translateY(-1px)"; e.currentTarget.style.boxShadow = "0 8px 32px rgba(255,176,192,0.3)"; }}}
              onMouseLeave={e => { if (canPlay) { e.currentTarget.style.transform = "translateY(0)"; e.currentTarget.style.boxShadow = "0 4px 20px var(--pink-glow)"; }}}
            >
              {phase === "done" ? "Minecraft is Running" : phase === "loading" ? "Launching..." : phase === "error" ? "Retry" : "Launch Game"}
            </button>

            {/* Progress */}
            {phase === "loading" && (
              <div className="fade-in" style={{ padding: "0 2px" }}>
                <div style={{ height: "3px", background: "rgba(255,255,255,0.03)", borderRadius: "4px", overflow: "hidden" }}>
                  <div style={{
                    height: "100%", width: `${progress}%`,
                    background: "linear-gradient(90deg, var(--pink-400), var(--pink-200))",
                    transition: "width 0.4s ease",
                    borderRadius: "4px",
                  }} />
                </div>
                <div style={{ fontSize: "11px", color: "var(--text-muted)", marginTop: "8px" }}>{status}</div>
              </div>
            )}

            {/* Error */}
            {phase === "error" && (
              <div className="fade-in" style={{
                fontSize: "12px", color: "var(--accent-red)", padding: "12px 16px",
                background: "rgba(255,80,80,0.04)", border: "1px solid rgba(255,80,80,0.1)",
                borderRadius: "var(--radius-sm)", lineHeight: 1.6,
              }}>
                {status}
              </div>
            )}

            {/* Info cards */}
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px" }}>
              <div className="bloom-card" style={{ padding: "18px 20px" }}>
                <div style={{ fontSize: "10px", fontWeight: "700", letterSpacing: "0.1em", color: "var(--text-muted)", marginBottom: "10px", textTransform: "uppercase" }}>
                  Bloom Client
                </div>
                <div style={{ fontSize: "12px", color: "var(--text-muted)", lineHeight: 1.7 }}>
                  Press <span style={{ color: "var(--pink-300)", fontWeight: "600", background: "rgba(255,176,192,0.08)", padding: "1px 6px", borderRadius: "4px", fontSize: "11px" }}>Right Shift</span> in-game for modules
                </div>
              </div>
              <div className="bloom-card" style={{ padding: "18px 20px" }}>
                <div style={{ fontSize: "10px", fontWeight: "700", letterSpacing: "0.1em", color: "var(--text-muted)", marginBottom: "10px", textTransform: "uppercase" }}>
                  Servers
                </div>
                {[
                  { name: "Hypixel", players: "45,231" },
                  { name: "BedWars Practice", players: "2,104" },
                  { name: "PvP Legacy", players: "891" },
                ].map((s, i) => (
                  <div key={i} style={{
                    display: "flex", justifyContent: "space-between", alignItems: "center",
                    padding: "3px 0",
                  }}>
                    <span style={{ fontSize: "12px", color: "var(--text-secondary)" }}>{s.name}</span>
                    <span style={{ fontSize: "10px", color: "var(--accent-green)", fontWeight: "600", fontVariantNumeric: "tabular-nums" }}>{s.players}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {page === "mods" && <LazyPage name="ModStore" />}
        {page === "installed" && <LazyPage name="InstalledMods" />}
        {page === "shaders" && <LazyPage name="Shaders" />}
        {page === "texturepacks" && <LazyPage name="TexturePacks" />}
        {page === "shop" && <LazyPage name="Shop" />}
        {page === "console" && <LazyPage name="Console" />}
        {page === "settings" && <LazyPage name="Settings" />}
      </div>

      {loginModal && <LoginModal {...loginModal} onClose={() => setLoginModal(null)} />}
    </div>
  );
}

function LazyPage({ name }: { name: string }) {
  const [Comp, setComp] = useState<any>(null);
  useEffect(() => {
    import(`./pages/${name}.tsx`).then(m => {
      const key = Object.keys(m).find(k => k.endsWith("Page") || k === "ShopPage" || k === "ConsolePage") || Object.keys(m)[0];
      setComp(() => m[key]);
    });
  }, [name]);
  if (!Comp) return (
    <div style={{ display: "flex", alignItems: "center", justifyContent: "center", height: "100%", color: "var(--text-faint)" }}>
      <div style={{ textAlign: "center" }}>
        <div style={{ fontSize: "24px", marginBottom: "8px", opacity: 0.5 }}>loading...</div>
      </div>
    </div>
  );
  return <Comp versions={["1.21.1","1.21.11"]} selectedVersion="1.21.11" onVersionChange={() => {}} />;
}
