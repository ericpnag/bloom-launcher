import axios from 'axios';
import type { ModResult, TexturePackResult } from '../types';

const BASE = 'https://api.modrinth.com/v2';

export async function searchMods(query: string, mcVersion: string): Promise<ModResult[]> {
  const res = await axios.get(`${BASE}/search`, {
    params: { query, facets: JSON.stringify([['project_type:mod'], [`versions:${mcVersion}`]]), limit: 20 },
  });
  return res.data.hits.map((h: any) => ({
    id: h.project_id, title: h.title, description: h.description,
    downloads: h.downloads, iconUrl: h.icon_url ?? undefined, source: 'modrinth' as const,
  }));
}

export async function searchTexturePacks(query: string, mcVersion: string): Promise<TexturePackResult[]> {
  const res = await axios.get(`${BASE}/search`, {
    params: { query, facets: JSON.stringify([['project_type:resourcepack'], [`versions:${mcVersion}`]]), limit: 20 },
  });
  return res.data.hits.map((h: any) => ({
    id: h.project_id, title: h.title, description: h.description,
    thumbnailUrl: h.icon_url ?? undefined, resolution: '16x', downloads: h.downloads,
  }));
}

export async function getModVersions(projectId: string, mcVersion: string): Promise<any[]> {
  const res = await axios.get(`${BASE}/project/${projectId}/version`, {
    params: { game_versions: JSON.stringify([mcVersion]), loaders: JSON.stringify(['fabric']) },
  });
  return res.data;
}
