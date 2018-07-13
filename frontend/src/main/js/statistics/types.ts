import { Player, Team } from '../types/types';

interface Dimension {
  description: string;
  unit?: string;
}

type DimensionValue = number | string | Date | Player | Team;

export interface ChartData2D<T extends DimensionValue, S extends DimensionValue> {
  dimensions: [Dimension, Dimension];
  entries: Array<[T, S]>;
}

export interface ChartData3D<T extends DimensionValue, S extends DimensionValue, R extends DimensionValue> {
  dimensions: [Dimension, Dimension, Dimension];
  entries: Array<[T, S, R]>;
}

export interface GameStatistics {
  wonPercent: number;
  played: number;
  avgTime: number;
}

export interface SetStatistics {
  wonPercent: number;
  played: number;
  avgTime: number;
}

export interface GoalStatistics {
  scored: number;
  ratio: number;
  avgTimeToScore: number;
}

export interface PlayerProfileData {
  gameStatistics: GameStatistics;
  setStatistics: SetStatistics;
  goalStatistics: GoalStatistics;
  eloData: ChartData2D<Date, number>;
}
