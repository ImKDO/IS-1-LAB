export interface Coordinates {
  id?: number;
  x: number;
  y: number;
}

export interface Human {
  id?: number;
  name?: string;
  height: number;
}

export enum Climate {
  RAIN_FOREST = "RAIN_FOREST",
  MONSOON = "MONSOON",
  TUNDRA = "TUNDRA",
  DESERT = "DESERT",
}

export enum Government {
  ARISTOCRACY = "ARISTOCRACY",
  GERONTOCRACY = "GERONTOCRACY",
  DICTATORSHIP = "DICTATORSHIP",
  KLEPTOCRACY = "KLEPTOCRACY",
  PUPPET_STATE = "PUPPET_STATE",
}

export enum StandardOfLiving {
  HIGH = "HIGH",
  MEDIUM = "MEDIUM",
  ULTRA_LOW = "ULTRA_LOW",
  NIGHTMARE = "NIGHTMARE",
}

export interface City {
  id?: number;
  name: string;
  coordinates: Coordinates;
  creationDate?: string;
  area: number;
  population: number;
  establishmentDate?: string;
  capital: boolean;
  metersAboveSeaLevel?: number;
  climate: Climate;
  government?: Government;
  standardOfLiving: StandardOfLiving;
  governor?: Human;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
