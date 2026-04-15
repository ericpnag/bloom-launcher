import { useState } from "react";
import { invoke } from "@tauri-apps/api/core";
import axios from "axios";

interface Mod { slug: string; project_id: string; title: string; description: string; downloads: number; icon_url?: string; author: string; }

interface Props {
  versions: string[];
  selectedVersion: string;
  onVersionChange: (v: string) => void;
}

export function ModStorePage({ versions, selectedVersion }: Props) {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<Mod[]>([]);
  const [loading, setLoading] = useState(false);
  const [loaded, setLoaded] = useState(false);
  const [searchVersion, setSearchVersion] = useState(selectedVersion);
  const [installing, setInstalling] = useState<Record<string, "loading" | "done" | "error">>({});

  async function search(q?: string, ver?: string) {
    setLoading(true);
    const v = ver ?? searchVersion;
    try {
      const res = await axios.get("https://api.modrinth.com/v2/search", {
        params: {
          query: q ?? query,
          facets: JSON.stringify([["project_type:mod"],[`versions:${v}`],["categories:fabric"]]),
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

  async function handleInstall(mod: Mod) {
    setInstalling(prev => ({ ...prev, [mod.project_id]: "loading" }));
    try {
      await invoke("install_mod", { projectId: mod.project_id, mcVersion: searchVersion });
      setInstalling(prev => ({ ...prev, [mod.project_id]: "done" }));
    } catch (e) {
      console.error("Install failed:", e);
      setInstalling(prev => ({ ...prev, [mod.project_id]: "error" }));
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
      <div>
        <h2 className="page-title">Mod Store</h2>
        <p className="page-subtitle">Browse Fabric mods from Modrinth</p>
      </div>

      <div style={{ display: "flex", gap: "8px" }}>
        <select
          className="pulsar-select"
          value={searchVersion}
          onChange={e => { setSearchVersion(e.target.value); setLoaded(false); setInstalling({}); }}
        >
          {versions.map(v => <option key={v} value={v}>{v}</option>)}
        </select>
        <input
          className="pulsar-input"
          value={query}
          onChange={e => setQuery(e.target.value)}
          onKeyDown={e => e.key === "Enter" && search(query)}
          placeholder="Search mods..."
          style={{ flex: 1 }}
        />
        <button className="pulsar-btn" onClick={() => search(query)} disabled={loading}>
          {loading ? "..." : "Search"}
        </button>
      </div>

      <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
        {results.map(mod => (
          <div key={mod.project_id} className="pulsar-list-item">
            {mod.icon_url
              ? <img src={mod.icon_url} alt="" style={{ width: "40px", height: "40px", borderRadius: "6px", objectFit: "cover", flexShrink: 0 }} />
              : <div className="pulsar-icon-placeholder" />
            }
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontWeight: "600", fontSize: "13px", color: "var(--text)", marginBottom: "2px" }}>{mod.title}</div>
              <div style={{ fontSize: "11px", color: "var(--text-dim)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{mod.description}</div>
            </div>
            <div style={{ fontSize: "11px", color: "var(--text-faint)", flexShrink: 0 }}>{(mod.downloads / 1000).toFixed(0)}k</div>
            <button
              className={installClass(mod.project_id)}
              onClick={() => handleInstall(mod)}
              disabled={installing[mod.project_id] === "loading" || installing[mod.project_id] === "done"}
            >
              {installLabel(mod.project_id)}
            </button>
          </div>
        ))}
        {results.length === 0 && !loading && loaded && (
          <div className="pulsar-empty">No mods found</div>
        )}
      </div>
    </div>
  );
}
