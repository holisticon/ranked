import { Player } from '../types/types';

interface Dimension {
  description: string;
  unit?: string;
}

type DimensionValue = number | string | Player;

export interface ChartData2D<T extends DimensionValue, S extends DimensionValue> {
  dimensions: [Dimension, Dimension];
  entries: Array<[T, S]>;
}

export interface ChartData3D<T extends DimensionValue, S extends DimensionValue, R extends DimensionValue> {
  dimensions: [Dimension, Dimension, Dimension];
  entries: Array<[T, S, R]>;
}
