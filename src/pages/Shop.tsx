import { useState, useEffect } from "react";

interface Cosmetic {
  id: string;
  name: string;
  type: "cape" | "wings" | "hat" | "aura";
  price: number;
  color: string;
  emoji: string;
  description: string;
}

const COSMETICS: Cosmetic[] = [
  { id: "cape_blossom", name: "Cherry Blossom Cape", type: "cape", price: 0, color: "#FFB7C9", emoji: "blossom", description: "Flowing cape with cherry blossom petals" },
  { id: "cape_midnight", name: "Midnight Cape", type: "cape", price: 0, color: "#2d1b3d", emoji: "moon", description: "Dark purple cape with stars" },
  { id: "cape_flame", name: "Flame Cape", type: "cape", price: 100, color: "#FF6633", emoji: "fire", description: "Fiery animated cape" },
  { id: "cape_ice", name: "Frost Cape", type: "cape", price: 100, color: "#66CCFF", emoji: "ice", description: "Icy crystalline cape" },
  { id: "cape_rainbow", name: "Rainbow Cape", type: "cape", price: 200, color: "#FF66AA", emoji: "rainbow", description: "Color-shifting rainbow cape" },
  { id: "wings_angel", name: "Angel Wings", type: "wings", price: 0, color: "#FFFFFF", emoji: "angel", description: "White feathered angel wings" },
  { id: "wings_dragon", name: "Dragon Wings", type: "wings", price: 150, color: "#AA33FF", emoji: "dragon", description: "Purple dragon wings" },
  { id: "wings_butterfly", name: "Butterfly Wings", type: "wings", price: 100, color: "#FFB7C9", emoji: "butterfly", description: "Delicate pink butterfly wings" },
  { id: "hat_crown", name: "Golden Crown", type: "hat", price: 200, color: "#FFD700", emoji: "crown", description: "A majestic golden crown" },
  { id: "hat_halo", name: "Halo", type: "hat", price: 50, color: "#FFFFAA", emoji: "halo", description: "Glowing halo above your head" },
  { id: "hat_horns", name: "Devil Horns", type: "hat", price: 75, color: "#FF4444", emoji: "horns", description: "Red devil horns" },
  { id: "aura_petals", name: "Petal Aura", type: "aura", price: 0, color: "#FFB7C9", emoji: "petals", description: "Cherry blossom petals around you" },
  { id: "aura_flames", name: "Flame Aura", type: "aura", price: 150, color: "#FF6633", emoji: "fire", description: "Fire particles swirling around you" },
  { id: "aura_sparkle", name: "Sparkle Aura", type: "aura", price: 100, color: "#AADDFF", emoji: "sparkle", description: "Glittering sparkle particles" },
];

const TYPE_LABELS: Record<string, string> = { cape: "Capes", wings: "Wings", hat: "Hats", aura: "Auras" };

export function ShopPage() {
  const [points, setPoints] = useState(500);
  const [owned, setOwned] = useState<string[]>([]);
  const [equipped, setEquipped] = useState<Record<string, string>>({});
  const [filter, setFilter] = useState<string>("all");

  useEffect(() => {
    const saved = localStorage.getItem("bloom-cosmetics");
    if (saved) {
      const data = JSON.parse(saved);
      setPoints(data.points ?? 500);
      setOwned(data.owned ?? []);
      setEquipped(data.equipped ?? {});
    }
  }, []);

  function save(p: number, o: string[], e: Record<string, string>) {
    setPoints(p); setOwned(o); setEquipped(e);
    localStorage.setItem("bloom-cosmetics", JSON.stringify({ points: p, owned: o, equipped: e }));
  }

  function buy(c: Cosmetic) {
    if (c.price > points) return;
    save(points - c.price, [...owned, c.id], equipped);
  }

  function equip(c: Cosmetic) {
    const newEquipped = { ...equipped };
    if (newEquipped[c.type] === c.id) delete newEquipped[c.type];
    else newEquipped[c.type] = c.id;
    save(points, owned, newEquipped);
  }

  const isOwned = (id: string) => owned.includes(id) || COSMETICS.find(c => c.id === id)?.price === 0;
  const isEquipped = (id: string) => Object.values(equipped).includes(id);

  const filtered = filter === "all" ? COSMETICS : COSMETICS.filter(c => c.type === filter);

  return (
    <div className="fade-in" style={{ display: "flex", flexDirection: "column", height: "100%", padding: "28px", gap: "16px", overflowY: "auto" }}>
      {/* Header */}
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
        <div>
          <h2 className="page-title">Cosmetics Shop</h2>
          <p className="page-subtitle">Customize your character</p>
        </div>
        <div className="bloom-card" style={{
          padding: "10px 18px", display: "flex", alignItems: "center", gap: "8px",
        }}>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="3" fill="#FFB7C9"/>
            <ellipse cx="12" cy="6" rx="2.5" ry="3.5" fill="#FFB7C9" opacity="0.6"/>
            <ellipse cx="17" cy="10" rx="2.5" ry="3.5" fill="#F8A4B8" opacity="0.5" transform="rotate(72 12 12)"/>
            <ellipse cx="15" cy="17" rx="2.5" ry="3.5" fill="#FFD1DC" opacity="0.4" transform="rotate(144 12 12)"/>
            <ellipse cx="9" cy="17" rx="2.5" ry="3.5" fill="#F8A4B8" opacity="0.5" transform="rotate(216 12 12)"/>
            <ellipse cx="7" cy="10" rx="2.5" ry="3.5" fill="#FFB7C9" opacity="0.6" transform="rotate(288 12 12)"/>
          </svg>
          <span style={{ fontSize: "18px", fontWeight: "800", color: "var(--pink-light)" }}>{points}</span>
          <span style={{ fontSize: "11px", color: "var(--text-muted)" }}>points</span>
        </div>
      </div>

      {/* Filter tabs */}
      <div style={{ display: "flex", gap: "4px" }}>
        {["all", "cape", "wings", "hat", "aura"].map(t => (
          <button key={t} onClick={() => setFilter(t)} style={{
            padding: "7px 16px", borderRadius: "8px",
            background: filter === t ? "rgba(255,176,192,0.1)" : "transparent",
            border: filter === t ? "1px solid rgba(255,176,192,0.15)" : "1px solid transparent",
            color: filter === t ? "var(--pink-light)" : "var(--text-dim)",
            fontSize: "12px", fontWeight: "600", cursor: "pointer",
            transition: "all 0.15s", fontFamily: "inherit",
          }}
          onMouseEnter={e => { if (filter !== t) e.currentTarget.style.color = "var(--text)"; }}
          onMouseLeave={e => { if (filter !== t) e.currentTarget.style.color = "var(--text-dim)"; }}
          >
            {t === "all" ? "All" : TYPE_LABELS[t]}
          </button>
        ))}
      </div>

      {/* Grid */}
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(190px, 1fr))", gap: "10px" }}>
        {filtered.map(c => {
          const own = isOwned(c.id);
          const eq = isEquipped(c.id);
          return (
            <div key={c.id} className="bloom-card" style={{
              padding: "16px", position: "relative",
              borderColor: eq ? "rgba(255,176,192,0.25)" : undefined,
              background: eq ? "rgba(255,176,192,0.06)" : undefined,
            }}>
              {/* Type badge */}
              <div style={{
                position: "absolute", top: "10px", right: "10px",
                fontSize: "10px", color: "var(--text-dim)",
                background: "rgba(0,0,0,0.3)", padding: "2px 8px", borderRadius: "4px",
                fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.05em",
              }}>
                {c.type}
              </div>

              {/* Color swatch icon */}
              <div style={{
                width: "48px", height: "48px", margin: "0 auto 10px",
                borderRadius: "12px",
                background: `linear-gradient(135deg, ${c.color}33, ${c.color}15)`,
                border: `1px solid ${c.color}40`,
                display: "flex", alignItems: "center", justifyContent: "center",
                filter: own ? "none" : "grayscale(0.6) opacity(0.5)",
              }}>
                <div style={{
                  width: "20px", height: "20px", borderRadius: "50%",
                  background: c.color, opacity: own ? 0.8 : 0.4,
                }} />
              </div>

              {/* Name */}
              <div style={{ fontSize: "13px", fontWeight: "700", color: "var(--text)", textAlign: "center", marginBottom: "4px" }}>
                {c.name}
              </div>
              <div style={{ fontSize: "11px", color: "var(--text-faint)", textAlign: "center", marginBottom: "12px", lineHeight: 1.4 }}>
                {c.description}
              </div>

              {/* Action */}
              {own ? (
                <button onClick={() => equip(c)} className="bloom-btn" style={{
                  width: "100%", padding: "8px",
                  background: eq ? "linear-gradient(135deg, var(--pink), var(--pink-soft))" : "rgba(255,255,255,0.04)",
                  color: eq ? "#1a0f1a" : "var(--text-muted)",
                  fontSize: "12px",
                }}>
                  {eq ? "Equipped" : "Equip"}
                </button>
              ) : (
                <button onClick={() => buy(c)} disabled={c.price > points}
                  className="bloom-btn-ghost"
                  style={{
                    width: "100%", padding: "8px",
                    borderColor: c.price > points ? "rgba(255,255,255,0.04)" : "rgba(255,176,192,0.15)",
                    color: c.price > points ? "var(--text-faint)" : "var(--pink)",
                    cursor: c.price > points ? "not-allowed" : "pointer",
                    display: "flex", alignItems: "center", justifyContent: "center", gap: "6px",
                    fontSize: "12px",
                  }}
                >
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                    <circle cx="12" cy="12" r="3" fill="currentColor" opacity="0.6"/>
                    <ellipse cx="12" cy="6" rx="2.5" ry="3.5" fill="currentColor" opacity="0.4"/>
                    <ellipse cx="17" cy="10" rx="2.5" ry="3.5" fill="currentColor" opacity="0.3" transform="rotate(72 12 12)"/>
                  </svg>
                  {c.price}
                </button>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
