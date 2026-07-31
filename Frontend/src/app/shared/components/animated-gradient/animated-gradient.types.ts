export type PatternShape = 'Checks' | 'Stripes' | 'Edge';

export const PatternShapes: Record<PatternShape, number> = {
  Checks: 0,
  Stripes: 1,
  Edge: 2,
};

export type PresetName = 'Aurora' | 'Oceanic' | 'Amber' | 'Toxic' | 'Ghost';

export interface PresetParams {
  color1: string;
  color2: string;
  color3: string;
  rotation: number;
  proportion: number;
  scale: number;
  speed: number;
  distortion: number;
  swirl: number;
  swirlIterations: number;
  softness: number;
  offset: number;
  shape: PatternShape;
  shapeSize: number;
}

export const presets: Record<PresetName, PresetParams> = {
  Aurora: {
    color1: '#0a001a', color2: '#1a0b2e', color3: '#f20089',
    rotation: -45, proportion: 60, scale: 0.6, speed: 15,
    distortion: 40, swirl: 80, swirlIterations: 10, softness: 100,
    offset: 200, shape: 'Edge', shapeSize: 50,
  },
  Oceanic: {
    color1: '#000814', color2: '#001d3d', color3: '#00b4d8',
    rotation: 0, proportion: 70, scale: 0.4, speed: 10,
    distortion: 15, swirl: 50, swirlIterations: 12, softness: 80,
    offset: 150, shape: 'Checks', shapeSize: 30,
  },
  Amber: {
    color1: '#140c00', color2: '#4a2500', color3: '#f57c00',
    rotation: 120, proportion: 80, scale: 0.8, speed: 20,
    distortion: 25, swirl: 60, swirlIterations: 8, softness: 90,
    offset: 500, shape: 'Stripes', shapeSize: 40,
  },
  Toxic: {
    color1: '#050d05', color2: '#0a240a', color3: '#39ff14',
    rotation: -90, proportion: 55, scale: 0.5, speed: 25,
    distortion: 60, swirl: 100, swirlIterations: 15, softness: 70,
    offset: -100, shape: 'Edge', shapeSize: 20,
  },
  Ghost: {
    color1: '#0a0a0a', color2: '#1c1c1c', color3: '#a3a3a3',
    rotation: 45, proportion: 50, scale: 0.3, speed: 8,
    distortion: 10, swirl: 30, swirlIterations: 5, softness: 100,
    offset: 0, shape: 'Checks', shapeSize: 60,
  },
};

export interface CustomConfig {
  preset: 'custom';
  color1: string;
  color2: string;
  color3: string;
  rotation?: number;
  proportion?: number;
  scale?: number;
  speed?: number;
  distortion?: number;
  swirl?: number;
  swirlIterations?: number;
  softness?: number;
  offset?: number;
  shape?: PatternShape;
  shapeSize?: number;
}

export interface PresetConfig {
  preset: PresetName;
  speed?: number;
}

export type GradientConfig = CustomConfig | PresetConfig;

export interface NoiseConfig {
  opacity: number;
  scale?: number;
}

export function resolveParams(config: GradientConfig): PresetParams {
  if (config.preset === 'custom') {
    return {
      color1: config.color1,
      color2: config.color2,
      color3: config.color3,
      rotation: config.rotation ?? 0,
      proportion: config.proportion ?? 35,
      scale: config.scale ?? 1,
      speed: config.speed ?? 25,
      distortion: config.distortion ?? 12,
      swirl: config.swirl ?? 80,
      swirlIterations: config.swirlIterations ?? 10,
      softness: config.softness ?? 100,
      offset: config.offset ?? 0,
      shape: config.shape ?? 'Checks',
      shapeSize: config.shapeSize ?? 10,
    };
  }
  const preset = presets[config.preset] || presets['Aurora'];
  return {
    ...preset,
    speed: config.speed ?? preset.speed,
  };
}

export function hexToRgba(hex: string): [number, number, number, number] {
  let r = 0, g = 0, b = 0, a = 1;

  if (hex.startsWith('rgba(')) {
    const parts = hex.slice(5, -1).split(',');
    r = parseInt(parts[0] ?? '0') / 255;
    g = parseInt(parts[1] ?? '0') / 255;
    b = parseInt(parts[2] ?? '0') / 255;
    a = parseFloat(parts[3] ?? '1');
  } else if (hex.startsWith('rgb(')) {
    const parts = hex.slice(4, -1).split(',');
    r = parseInt(parts[0] ?? '0') / 255;
    g = parseInt(parts[1] ?? '0') / 255;
    b = parseInt(parts[2] ?? '0') / 255;
  } else if (hex.startsWith('hsla(') || hex.startsWith('hsl(')) {
    const isHsla = hex.startsWith('hsla(');
    const parts = hex.slice(isHsla ? 5 : 4, -1).split(',');
    const h = parseFloat(parts[0] ?? '0') / 360;
    const s = parseFloat(parts[1] ?? '0') / 100;
    const l = parseFloat(parts[2] ?? '0') / 100;
    a = isHsla ? parseFloat(parts[3] ?? '1') : 1;
    [r, g, b] = hslToRgb(h, s, l);
  } else if (hex.startsWith('#')) {
    const c = hex.slice(1);
    if (c.length === 3) {
      r = parseInt(c.charAt(0) + c.charAt(0), 16) / 255;
      g = parseInt(c.charAt(1) + c.charAt(1), 16) / 255;
      b = parseInt(c.charAt(2) + c.charAt(2), 16) / 255;
    } else if (c.length >= 6) {
      r = parseInt(c.slice(0, 2), 16) / 255;
      g = parseInt(c.slice(2, 4), 16) / 255;
      b = parseInt(c.slice(4, 6), 16) / 255;
      if (c.length === 8) {
        a = parseInt(c.slice(6, 8), 16) / 255;
      }
    }
  }

  return [r, g, b, a];
}

function hslToRgb(h: number, s: number, l: number): [number, number, number] {
  let r: number, g: number, b: number;

  if (s === 0) {
    r = g = b = l;
  } else {
    const hue2rgb = (p: number, q: number, t: number) => {
      if (t < 0) t += 1;
      if (t > 1) t -= 1;
      if (t < 1 / 6) return p + (q - p) * 6 * t;
      if (t < 1 / 2) return q;
      if (t < 2 / 3) return p + (q - p) * (2 / 3 - t) * 6;
      return p;
    };

    const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
    const p = 2 * l - q;
    r = hue2rgb(p, q, h + 1 / 3);
    g = hue2rgb(p, q, h);
    b = hue2rgb(p, q, h - 1 / 3);
  }

  return [r, g, b];
}
