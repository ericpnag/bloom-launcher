import { vi, describe, it, expect } from 'vitest';
import axios from 'axios';
import { searchMods, searchTexturePacks } from '../../src/api/modrinth';

vi.mock('axios');

describe('searchMods', () => {
  it('returns mapped mod results', async () => {
    vi.mocked(axios.get).mockResolvedValue({
      data: {
        hits: [{ project_id: 'abc', title: 'Sodium', description: 'Fast', downloads: 1000000, icon_url: null }],
      },
    });
    const results = await searchMods('sodium', '1.21.1');
    expect(results).toHaveLength(1);
    expect(results[0].title).toBe('Sodium');
    expect(results[0].source).toBe('modrinth');
  });
});

describe('searchTexturePacks', () => {
  it('returns texture pack results', async () => {
    vi.mocked(axios.get).mockResolvedValue({
      data: { hits: [{ project_id: 'tp1', title: 'Faithful', description: 'Clean', downloads: 500000, icon_url: null }] },
    });
    const results = await searchTexturePacks('faithful', '1.21.1');
    expect(results[0].title).toBe('Faithful');
  });
});
