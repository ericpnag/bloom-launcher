export interface Instance {
  id: string;
  mcVersion: string;
  name: string;
  fabricVersion: string;
  modsPath: string;
  lastPlayed?: string;
}

export interface ModResult {
  id: string;
  title: string;
  description: string;
  downloads: number;
  iconUrl?: string;
  source: 'modrinth' | 'curseforge';
}

export interface TexturePackResult {
  id: string;
  title: string;
  description: string;
  thumbnailUrl?: string;
  resolution: string;
  downloads: number;
}

export interface Account {
  username: string;
  uuid: string;
  accessToken: string;
  pulsarToken: string;
}

export type Page = 'home' | 'library' | 'modstore' | 'texturepacks' | 'versions' | 'settings';
