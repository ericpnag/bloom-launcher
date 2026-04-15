import { useState, useEffect, useRef } from "react";
import { invoke } from "@tauri-apps/api/core";
import axios from "axios";

interface Pack { project_id: string; title: string; description: string; downloads: number; icon_url?: string; }

interface Props {
  versions: string[];
  selectedVersion: string;
  onVersionChange: (v: string) => void;
}

export function TexturePacksPage({ versions, selectedVersion }: Props) {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<Pack[]>([]);
  const [loading, setLoading] = useState(false);
  const [loaded, setLoaded] = useState(false);
  const [searchVersion, setSearchVersion] = useState(selectedVersion);
  const [installing, setInstalling] = useState<Record<string, "loading" | "done" | "error">>({});
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, []);

  async function search(q?: string, ver?: string) {
    setLoading(true);
    const v = ver ?? searchVersion;
    try {
      const res = await axios.get("https://api.modrinth.com/v2/search", {
        params: {
          query: q ?? query,
          facets: JSON.stringify([["project_type:resourcepack"],[`versions:${v}`]]),
          limit: 20,
          index: "downloads",
        },
      });
      setResults(res.data.hits);
      setLoaded(true);
    } finally {
      setLoading(false);
    }
  }

  if (!loaded && !loading) { search(""); }

  async function handleInstall(pack: Pack) {
    setInstalling(prev => ({ ...prev, [pack.project_id]: "loading" }));
    try {
      await invoke("install_resourcepack", { projectId: pack.project_id, mcVersion: searchVersion });
      setInstalling(prev => ({ ...prev, [pack.project_id]: "done" }));
    } catch (e) {
      console.error("Install failed:", e);
      setInstalling(prev => ({ ...prev, [pack.project_id]: "error" }));
    }
  }

  function installLabel(id: string) {
    const s = installing[id];
    if (s === "loading") return "Installing...";
    if (s === "done") return "Installed";
    if (s === "error") return "Failed";
    return "Install";
  }

  function installClass(id: string) {
    const s = installing[id];
    if (s === "done") return "pulsar-btn-install installed";
    if (s === "error") return "pulsar-btn-install failed";
    if (s === "loading") return "pulsar-btn-install loading";
    return "pulsar-btn-install";
  }

  return (
    <div className="fade-in" style={{ display: "flex", flexDirection: "column", height: "100%", padding: "28px", gap: "16px", overflowY: "auto" }}>
      {/* Header row */}
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
        <div>
          <h2 className="page-title" style={{ marginBottom: "2px" }}>Texture Packs</h2>
          <p className="page-subtitle" style={{ margin: 0 }}>Browse resource packs from Modrinth</p>
        </div>

        {/* Custom version picker */}
        <div ref={dropdownRef} style={{ position: "relative" }}>
          <button
            onClick={() => setDropdownOpen(o => !o)}
            style={{
              display: "flex", alignItems: "center", gap: "10px",
              background: dropdownOpen ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.04)",
              border: `1px solid ${dropdownOpen ? "rgba(255,255,255,0.18)" : "rgba(255,255,255,0.08)"}`,
              borderRadius: "10px", padding: "9px 14px",
              color: "#fff", cursor: "pointer", fontFamily: "inherit",
              transition: "all 0.15s",
              fontSize: "13px", fontWeight: "600",
              minWidth: "140px", justifyContent: "space-between",
            }}
            onMouseEnter={e => { if (!dropdownOpen) { e.currentTarget.style.background = "rgba(255,255,255,0.07)"; e.currentTarget.style.borderColor = "rgba(255,255,255,0.14)"; }}}
            onMouseLeave={e => { if (!dropdownOpen) { e.currentTarget.style.background = "rgba(255,255,255,0.04)"; e.currentTarget.style.borderColor = "rgba(255,255,255,0.08)"; }}}
          >
            <span style={{ display: "flex", alignItems: "center", gap: "8px" }}>
              <svg width="14" height="14" viewBox="0 0 16 16" fill="none" style={{ opacity: 0.7 }}>
                <rect x="1" y="5" width="14" height="10" rx="1" fill="#7b5c3c"/>
                <rect x="1" y="1" width="14" height="5" rx="1" fill="#4a8c3f"/>
                <rect x="1" y="4" width="14" height="3" fill="#5fa34a"/>
              </svg>
              {searchVersion}
            </span>
            <svg width="10" height="10" viewBox="0 0 10 10" fill="none"
              style={{ opacity: 0.5, transform: dropdownOpen ? "rotate(180deg)" : "rotate(0deg)", transition: "transform 0.15s" }}>
              <path d="M2 3.5L5 6.5L8 3.5" stroke="white" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </button>

          {dropdownOpen && (
            <div style={{
              position: "absolute", top: "calc(100% + 6px)", right: 0,
              background: "rgba(10,10,10,0.97)", border: "1px solid rgba(255,255,255,0.1)",
              borderRadius: "10px", padding: "4px",
              minWidth: "140px", zIndex: 50,
              boxShadow: "0 8px 32px rgba(0,0,0,0.6)",
              backdropFilter: "blur(12px)",
            }}>
              {versions.map(v => (
                <button key={v} onClick={() => {
                  setSearchVersion(v);
                  setDropdownOpen(false);
                  setLoaded(false);
                  setInstalling({});
                  search("", v);
                }} style={{
                  display: "flex", alignItems: "center", justifyContent: "space-between",
                  width: "100%", padding: "8px 12px", borderRadius: "7px",
                  background: v === searchVersion ? "rgba(255,255,255,0.08)" : "transparent",
                  border: "none", color: v === searchVersion ? "#fff" : "var(--text-secondary)",
                  fontSize: "13px", fontWeight: v === searchVersion ? "700" : "500",
                  cursor: "pointer", fontFamily: "inherit", textAlign: "left",
                  transition: "all 0.1s",
                }}
                onMouseEnter={e => { if (v !== searchVersion) e.currentTarget.style.background = "rgba(255,255,255,0.05)"; }}
                onMouseLeave={e => { if (v !== searchVersion) e.currentTarget.style.background = "transparent"; }}
                >
                  {v}
                  {v === searchVersion && (
                    <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                      <path d="M2 6l3 3 5-5" stroke="white" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                  )}
                </button>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Search bar */}
      <div style={{ display: "flex", gap: "8px" }}>
        <input
          className="pulsar-input"
          value={query}
          onChange={e => setQuery(e.target.value)}
          onKeyDown={e => e.key === "Enter" && search(query)}
          placeholder="Search texture packs..."
          style={{ flex: 1 }}
        />
        <button className="pulsar-btn" onClick={() => search(query)} disabled={loading}>
          {loading ? "..." : "Search"}
        </button>
      </div>

      <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
        {results.map(pack => (
          <div key={pack.project_id} className="pulsar-list-item">
            {pack.icon_url
              ? <img src={pack.icon_url} alt="" style={{ width: "40px", height: "40px", borderRadius: "6px", objectFit: "cover", flexShrink: 0 }} />
              : <div className="pulsar-icon-placeholder" />
            }
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontWeight: "600", fontSize: "13px", color: "var(--text-primary)", marginBottom: "2px" }}>{pack.title}</div>
              <div style={{ fontSize: "11px", color: "var(--text-muted)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{pack.description}</div>
            </div>
            <div style={{ fontSize: "11px", color: "var(--text-faint)", flexShrink: 0 }}>{(pack.downloads / 1000).toFixed(0)}k</div>
            <button
              className={installClass(pack.project_id)}
              onClick={() => handleInstall(pack)}
              disabled={installing[pack.project_id] === "loading" || installing[pack.project_id] === "done"}
            >
              {installLabel(pack.project_id)}
            </button>
          </div>
        ))}
        {results.length === 0 && !loading && loaded && (
          <div className="pulsar-empty">No texture packs found</div>
        )}
      </div>
    </div>
  );
}
