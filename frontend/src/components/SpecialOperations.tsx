import { useState, useEffect } from "react";
import { cityApi } from "../api/cityApi";
import { City, Climate } from "../types";

export default function SpecialOperations() {
  const [cities, setCities] = useState<City[]>([]);
  const [selectedClimate, setSelectedClimate] = useState<Climate>(
    Climate.TROPICAL_SAVANNA
  );
  const [fromCityId, setFromCityId] = useState("");
  const [toCityId, setToCityId] = useState("");
  const [avgSeaLevel, setAvgSeaLevel] = useState<number | null>(null);

  useEffect(() => {
    loadCities();
  }, []);

  const loadCities = async () => {
    try {
      const response = await cityApi.getAll({ size: 1000 });
      setCities(response.data.content);
    } catch (error) {
      console.error("Error loading cities:", error);
    }
  };

  const handleGetByClimate = async () => {
    try {
      const response = await cityApi.getByClimate(selectedClimate);
      alert(
        `Found ${response.data.length} cities with ${selectedClimate} climate`
      );
      console.log(response.data);
    } catch (error) {
      console.error("Error:", error);
      alert("Failed to get cities by climate");
    }
  };

  const handleDeleteByClimate = async () => {
    if (!confirm(`Delete all cities with ${selectedClimate} climate?`)) return;
    try {
      await cityApi.deleteByClimate(selectedClimate);
      alert("Cities deleted successfully");
      loadCities();
    } catch (error) {
      console.error("Error:", error);
      alert("Failed to delete cities");
    }
  };

  const handleGetAvgSeaLevel = async () => {
    try {
      const response = await cityApi.getAverageSeaLevel();
      setAvgSeaLevel(response.data);
    } catch (error) {
      console.error("Error:", error);
      alert("Failed to get average sea level");
    }
  };

  const handleRelocatePopulation = async () => {
    if (!fromCityId || !toCityId) {
      alert("Please select both cities");
      return;
    }
    try {
      await cityApi.relocatePopulation(Number(fromCityId), Number(toCityId));
      alert("Population relocated successfully");
      loadCities();
    } catch (error) {
      console.error("Error:", error);
      alert("Failed to relocate population");
    }
  };

  const handleRelocateToMin = async () => {
    if (!fromCityId) {
      alert("Please select a city");
      return;
    }
    try {
      await cityApi.relocateToMinPopulation(Number(fromCityId));
      alert("Population relocated to city with minimum population");
      loadCities();
    } catch (error) {
      console.error("Error:", error);
      alert("Failed to relocate population");
    }
  };

  return (
    <div className="container">
      <h2>Special Operations</h2>

      <div className="operations-grid">
        <div className="operation-card">
          <h4>Get Cities by Climate</h4>
          <select
            value={selectedClimate}
            onChange={(e) => setSelectedClimate(e.target.value as Climate)}
          >
            {Object.values(Climate).map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
          <button onClick={handleGetByClimate} className="btn btn-primary">
            Get Cities
          </button>
        </div>

        <div className="operation-card">
          <h4>Delete Cities by Climate</h4>
          <select
            value={selectedClimate}
            onChange={(e) => setSelectedClimate(e.target.value as Climate)}
          >
            {Object.values(Climate).map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
          <button onClick={handleDeleteByClimate} className="btn btn-danger">
            Delete Cities
          </button>
        </div>

        <div className="operation-card">
          <h4>Average Meters Above Sea Level</h4>
          {avgSeaLevel !== null && (
            <div className="result-box">
              Average: {avgSeaLevel.toFixed(2)} m
            </div>
          )}
          <button onClick={handleGetAvgSeaLevel} className="btn btn-primary">
            Calculate
          </button>
        </div>

        <div className="operation-card">
          <h4>Relocate Population</h4>
          <div>
            <label>From City:</label>
            <select
              value={fromCityId}
              onChange={(e) => setFromCityId(e.target.value)}
            >
              <option value="">Select City</option>
              {cities.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name} (Pop: {c.population})
                </option>
              ))}
            </select>
          </div>
          <div>
            <label>To City:</label>
            <select
              value={toCityId}
              onChange={(e) => setToCityId(e.target.value)}
            >
              <option value="">Select City</option>
              {cities.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name} (Pop: {c.population})
                </option>
              ))}
            </select>
          </div>
          <button
            onClick={handleRelocatePopulation}
            className="btn btn-primary"
          >
            Relocate
          </button>
        </div>

        <div className="operation-card">
          <h4>Relocate to Min Population City</h4>
          <div>
            <label>From City:</label>
            <select
              value={fromCityId}
              onChange={(e) => setFromCityId(e.target.value)}
            >
              <option value="">Select City</option>
              {cities.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name} (Pop: {c.population})
                </option>
              ))}
            </select>
          </div>
          <button onClick={handleRelocateToMin} className="btn btn-primary">
            Relocate to Min
          </button>
        </div>
      </div>
    </div>
  );
}
