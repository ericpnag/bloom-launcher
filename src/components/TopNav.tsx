import type { Page } from "../App";

const TABS: { id: Page; label: string }[] = [
  { id: "play", label: "Play" },
  { id: "mods", label: "Mods" },
  { id: "installed", label: "Installed" },
  { id: "shaders", label: "Shaders" },
  { id: "texturepacks", label: "Textures" },
  { id: "shop", label: "Shop" },
  { id: "console", label: "Console" },
  { id: "settings", label: "Settings" },
];

interface Props {
  page: Page;
  onNavigate: (p: Page) => void;
  account: { username: string } | null;
  onLogin: () => void;
  onLogout: () => void;
}

export function TopNav({ page, onNavigate, account, onLogin, onLogout }: Props) {
  return (
    <header style={{
      display: "flex", alignItems: "center", height: "48px", minHeight: "48px",
      background: "rgba(10,6,17,0.85)", backdropFilter: "blur(20px)",
      borderBottom: "1px solid rgba(255,176,192,0.04)",
      padding: "0 16px", gap: "4px",
      position: "relative", zIndex: 10,
      // @ts-ignore
      WebkitAppRegion: "drag",
    }}>
      {/* Logo */}
      <div style={{ display: "flex", alignItems: "center", gap: "8px", marginRight: "20px" }}>
        <div style={{
          width: "24px", height: "24px", borderRadius: "8px",
          background: "linear-gradient(135deg, rgba(255,176,192,0.15), rgba(255,176,192,0.05))",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="3.5" fill="#FFB7C9" opacity="0.9"/>
            <ellipse cx="12" cy="5.5" rx="3" ry="4" fill="#FFB7C9" opacity="0.6"/>
            <ellipse cx="18" cy="9.5" rx="3" ry="4" fill="#F8A4B8" opacity="0.5" transform="rotate(72 12 12)"/>
            <ellipse cx="15.5" cy="17" rx="3" ry="4" fill="#FFD1DC" opacity="0.4" transform="rotate(144 12 12)"/>
            <ellipse cx="8.5" cy="17" rx="3" ry="4" fill="#F8A4B8" opacity="0.5" transform="rotate(216 12 12)"/>
            <ellipse cx="6" cy="9.5" rx="3" ry="4" fill="#FFB7C9" opacity="0.6" transform="rotate(288 12 12)"/>
          </svg>
        </div>
        <span style={{
          fontWeight: "800", fontSize: "12px", letterSpacing: "0.16em",
          color: "var(--pink-200)",
        }}>
          BLOOM
        </span>
      </div>

      {/* Tabs */}
      <nav style={{ display: "flex", gap: "1px", flex: 1,
        // @ts-ignore
        WebkitAppRegion: "no-drag",
      }}>
        {TABS.map(({ id, label }) => {
          const active = page === id;
          return (
            <button key={id} onClick={() => onNavigate(id)} style={{
              padding: "6px 12px",
              background: active ? "rgba(255,176,192,0.08)" : "transparent",
              color: active ? "var(--pink-200)" : "var(--text-muted)",
              border: "none",
              borderRadius: "6px",
              fontSize: "11.5px", fontWeight: active ? "700" : "500",
              cursor: "pointer", transition: "all 0.15s",
              fontFamily: "inherit",
              letterSpacing: "0.01em",
            }}
            onMouseEnter={e => { if (!active) { e.currentTarget.style.color = "var(--text-secondary)"; e.currentTarget.style.background = "rgba(255,255,255,0.02)"; } }}
            onMouseLeave={e => { if (!active) { e.currentTarget.style.color = "var(--text-muted)"; e.currentTarget.style.background = "transparent"; } }}
            >
              {label}
            </button>
          );
        })}
      </nav>

      {/* Account */}
      <div style={{ display: "flex", alignItems: "center",
        // @ts-ignore
        WebkitAppRegion: "no-drag",
      }}>
        {account ? (
          <div style={{ display: "flex", alignItems: "center", gap: "8px", cursor: "pointer", padding: "4px 8px", borderRadius: "8px", transition: "background 0.15s" }}
            onClick={onLogout}
            onMouseEnter={e => e.currentTarget.style.background = "rgba(255,255,255,0.03)"}
            onMouseLeave={e => e.currentTarget.style.background = "transparent"}
          >
            <div style={{
              width: "26px", height: "26px", borderRadius: "7px",
              background: "linear-gradient(135deg, rgba(255,176,192,0.12), rgba(255,176,192,0.06))",
              border: "1px solid rgba(255,176,192,0.1)",
              display: "flex", alignItems: "center", justifyContent: "center",
              fontSize: "11px", fontWeight: "700", color: "var(--pink-300)",
            }}>
              {account.username[0]?.toUpperCase()}
            </div>
            <span style={{ fontSize: "12px", fontWeight: "600", color: "var(--text-secondary)" }}>{account.username}</span>
          </div>
        ) : (
          <button onClick={onLogin} style={{
            background: "rgba(255,176,192,0.06)", border: "1px solid rgba(255,176,192,0.1)",
            color: "var(--pink-300)", borderRadius: "7px", padding: "5px 14px",
            fontSize: "11px", fontWeight: "600", cursor: "pointer", fontFamily: "inherit",
            transition: "all 0.15s",
          }}
          onMouseEnter={e => { e.currentTarget.style.background = "rgba(255,176,192,0.1)"; e.currentTarget.style.borderColor = "rgba(255,176,192,0.2)"; }}
          onMouseLeave={e => { e.currentTarget.style.background = "rgba(255,176,192,0.06)"; e.currentTarget.style.borderColor = "rgba(255,176,192,0.1)"; }}
          >
            Sign In
          </button>
        )}
      </div>
    </header>
  );
}
