import axios from "axios";
import { City, Page, Climate } from "../types";

const api = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API Error:", error);
    console.error("API Error Response:", error.response);
    console.error("API Error Data:", error.response?.data);
    return Promise.reject(error);
  }
);

export const cityApi = {
  getAll: (params?: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: string;
    name?: string;
  }) => api.get<Page<City>>("/cities", { params }),

  getById: (id: number) => api.get<City>(`/cities/${id}`),

  create: (city: City) => api.post<City>("/cities", city),

  update: (id: number, city: City) => api.put<City>(`/cities/${id}`, city),

  delete: (id: number) => api.delete(`/cities/${id}`),

  searchByName: (name: string) =>
    api.get<City[]>(`/cities/search`, { params: { name } }),

  getByClimate: (climate: Climate) =>
    api.get<City[]>(`/cities/climate/${climate}`),

  deleteByClimate: (climate: Climate) =>
    api.delete(`/cities/climate/${climate}`),

  getAverageSeaLevel: () => api.get<number>("/cities/average-sea-level"),

  relocatePopulation: (fromCityId: number, toCityId: number) =>
    api.post("/cities/relocate-population", null, {
      params: { fromCityId, toCityId },
    }),

  relocateToMinPopulation: (fromCityId: number) =>
    api.post("/cities/relocate-population-to-min", null, {
      params: { fromCityId },
    }),
};
