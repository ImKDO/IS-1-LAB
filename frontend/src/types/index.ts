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
  TROPICAL_SAVANNA = "TROPICAL_SAVANNA",
  OCEANIC = "OCEANIC",
  POLAR_ICECAP = "POLAR_ICECAP",
}

export enum Government {
  CORPORATOCRACY = "CORPORATOCRACY",
  PUPPET_STATE = "PUPPET_STATE",
  MERITOCRACY = "MERITOCRACY",
}

export enum StandardOfLiving {
  VERY_LOW = "VERY_LOW",
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
