import { useState } from "react";
import { invoke } from "@tauri-apps/api/core";
import axios from "axios";

interface Shader { project_id: string; title: string; description: string; downloads: number; icon_url?: string; }

interface Props {
  versions: string[];
  selectedVersion: string;
  onVersionChange: (v: string) => void;
}

export function ShadersPage({ versions, selectedVersion }: Props) {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<Shader[]>([]);
  const [loading, setLoading] = useState(false);
  const [loaded, setLoaded] = useState(false);
  const [searchVersion, setSearchVersion] = useState(selectedVersion);
  const [installing, setInstalling] = useState<Record<string, "loading" | "done" | "error">>({});

  async function search(q?: string) {
    setLoading(true);
    try {
      const res = await axios.get("https://api.modrinth.com/v2/search", {
        params: {
          query: q ?? query,
          facets: JSON.stringify([["project_type:shader"],[`versions:${searchVersion}`]]),
          limit: 20, index: "downloads",
        },
      });
      setResults(res.data.hits);
      setLoaded(true);
    } finally { setLoading(false); }
  }

  if (!loaded && !loading) search("");

  async function handleInstall(shader: Shader) {
    setInstalling(prev => ({ ...prev, [shader.project_id]: "loading" }));
    try {
      await invoke("install_mod", { projectId: shader.project_id, mcVersion: searchVersion });
      setInstalling(prev => ({ ...prev, [shader.project_id]: "done" }));
    } catch {
      setInstalling(prev => ({ ...prev, [shader.project_id]: "error" }));
    }
  }

  function label(id: string) {
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
        <h2 className="page-title">Shader Packs</h2>
        <p className="page-subtitle">Browse shaders from Modrinth</p>
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
          placeholder="Search shaders..."
          style={{ flex: 1 }}
        />
        <button className="pulsar-btn" onClick={() => search(query)} disabled={loading}>
          {loading ? "..." : "Search"}
        </button>
      </div>

      <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
        {results.map(s => (
          <div key={s.project_id} className="pulsar-list-item">
            {s.icon_url
              ? <img src={s.icon_url} alt="" style={{ width: "40px", height: "40px", borderRadius: "6px", objectFit: "cover", flexShrink: 0 }} />
              : <div className="pulsar-icon-placeholder" />
            }
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontWeight: "600", fontSize: "13px", color: "var(--text)", marginBottom: "2px" }}>{s.title}</div>
              <div style={{ fontSize: "11px", color: "var(--text-dim)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{s.description}</div>
            </div>
            <div style={{ fontSize: "11px", color: "var(--text-faint)", flexShrink: 0 }}>{(s.downloads / 1000).toFixed(0)}k</div>
            <button
              className={installClass(s.project_id)}
              onClick={() => handleInstall(s)}
              disabled={installing[s.project_id] === "loading" || installing[s.project_id] === "done"}
            >{label(s.project_id)}</button>
          </div>
        ))}
        {results.length === 0 && !loading && loaded && (
          <div className="pulsar-empty">No shaders found</div>
        )}
      </div>
    </div>
  );
}
