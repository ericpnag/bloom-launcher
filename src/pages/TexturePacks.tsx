import { useState } from "react";
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
    if (s === "done") return "bloom-btn-install installed";
    if (s === "error") return "bloom-btn-install failed";
    if (s === "loading") return "bloom-btn-install loading";
    return "bloom-btn-install";
  }

  return (
    <div className="fade-in" style={{ display: "flex", flexDirection: "column", height: "100%", padding: "28px", gap: "16px", overflowY: "auto" }}>
      <div>
        <h2 className="page-title">Texture Packs</h2>
        <p className="page-subtitle">Browse resource packs from Modrinth</p>
      </div>

      <div style={{ display: "flex", gap: "8px" }}>
        <select
          className="bloom-select"
          value={searchVersion}
          onChange={e => { setSearchVersion(e.target.value); setLoaded(false); setInstalling({}); }}
        >
          {versions.map(v => <option key={v} value={v}>{v}</option>)}
        </select>
        <input
          className="bloom-input"
          value={query}
          onChange={e => setQuery(e.target.value)}
          onKeyDown={e => e.key === "Enter" && search(query)}
          placeholder="Search texture packs..."
          style={{ flex: 1 }}
        />
        <button className="bloom-btn" onClick={() => search(query)} disabled={loading}>
          {loading ? "..." : "Search"}
        </button>
      </div>

      <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
        {results.map(pack => (
          <div key={pack.project_id} className="bloom-list-item">
            {pack.icon_url
              ? <img src={pack.icon_url} alt="" style={{ width: "40px", height: "40px", borderRadius: "6px", objectFit: "cover", flexShrink: 0 }} />
              : <div className="bloom-icon-placeholder" />
            }
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontWeight: "600", fontSize: "13px", color: "var(--text)", marginBottom: "2px" }}>{pack.title}</div>
              <div style={{ fontSize: "11px", color: "var(--text-dim)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{pack.description}</div>
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
          <div className="bloom-empty">No texture packs found</div>
        )}
      </div>
    </div>
  );
}
