import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { cityApi, transformApi } from "../api/cityApi";
import { City, Page } from "../types";

export default function CityList() {
  const [cities, setCities] = useState<Page<City> | null>(null);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState<"asc" | "desc">("asc");
  const [nameFilter, setNameFilter] = useState("");
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState<{
    message: string;
    type: "success" | "error";
  } | null>(null);

  const showNotification = (message: string, type: "success" | "error") => {
    setNotification({ message, type });
    setTimeout(() => setNotification(null), 5000);
  };

  useEffect(() => {
    loadCities();
  }, [page, size, sortBy, sortDir, nameFilter]);

  const loadCities = async () => {
    setLoading(true);
    try {
      console.log("Loading cities with params:", {
        page,
        size,
        sortBy,
        sortDir,
        nameFilter,
      });
      const response = await cityApi.getAll({
        page,
        size,
        sortBy,
        sortDir,
        name: nameFilter || undefined,
      });
      console.log("Cities loaded:", response.data);
      setCities(response.data);
    } catch (error: any) {
      console.error("Error loading cities:", error);
      console.error("Error response:", error.response);
      let errorMsg = "Failed to load cities";
      if (!error.response) {
        errorMsg = "Cannot connect to server. Is backend running on port 8080?";
      } else {
        errorMsg = `Server error: ${error.response.status}`;
      }
      showNotification(errorMsg, "error");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this city?")) return;
    try {
      await cityApi.delete(id);
      showNotification("City deleted successfully", "success");
      loadCities();
    } catch (error) {
      console.error("Error deleting city:", error);
      showNotification("Failed to delete city", "error");
    }
  };

  const handleFilterSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    loadCities();
  };

  const handleFileImport = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    try {
      const text = await file.text();
      const citiesToImport = JSON.parse(text);

      if (!Array.isArray(citiesToImport)) {
        showNotification("File must contain an array of cities", "error");
        return;
      }

      console.log(`Sending ${citiesToImport.length} cities to Transform Service...`);
      
      // Send to Transform Service for validation and processing
      showNotification(
        `Processing ${citiesToImport.length} cities through Transform Service...`,
        "success"
      );

      const response = await transformApi.validateAndTransform(citiesToImport);
      const result = response.data;

      console.log("Transform result:", result);

      let message = `Import complete!`;
      if (result.stats) {
        message = `Import complete! Total: ${result.stats.totalRecords}, Valid: ${result.stats.validRecords}`;
        if (result.stats.invalidRecords > 0) {
          message += `, Invalid: ${result.stats.invalidRecords}`;
        }
        if (result.stats.duplicatesInFile > 0) {
          message += `, Duplicates: ${result.stats.duplicatesInFile}`;
        }
      }

      if (result.errors && result.errors.length > 0) {
        const errorSummary = result.errors.slice(0, 3).map((err: any) => 
          `Row ${err.rowIndex}: ${err.message}`
        ).join("\n");
        message += `\n\nErrors:\n${errorSummary}`;
        if (result.errors.length > 3) {
          message += `\n...and ${result.errors.length - 3} more errors`;
        }
      }

      showNotification(
        message,
        result.stats?.invalidRecords === 0 ? "success" : "error"
      );

      setTimeout(() => {
        loadCities();
      }, 2000);

    } catch (error: any) {
      console.error("Error importing file:", error);
      const errorMsg = error.response?.data?.message || error.message || "Unknown error";
      showNotification(
        `Failed to import file: ${errorMsg}`,
        "error"
      );
    }

    // Reset file input
    e.target.value = "";
  };

  return (
    <div className="container">
      {notification && (
        <div
          className={`notification ${notification.type}`}
          style={{
            padding: "1rem",
            marginBottom: "1rem",
            borderRadius: "8px",
            backgroundColor:
              notification.type === "success" ? "#d4edda" : "#f8d7da",
            color: notification.type === "success" ? "#155724" : "#721c24",
            border: `1px solid ${
              notification.type === "success" ? "#c3e6cb" : "#f5c6cb"
            }`,
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <span>{notification.message}</span>
          <button
            onClick={() => setNotification(null)}
            style={{
              background: "none",
              border: "none",
              fontSize: "1.2rem",
              cursor: "pointer",
              color: "inherit",
            }}
          >
            ×
          </button>
        </div>
      )}
      <div className="header">
        <h2>Cities</h2>
        <div style={{ display: "flex", gap: "0.5rem" }}>
          <label className="btn btn-secondary" style={{ cursor: "pointer" }}>
            <i className="fas fa-upload"></i> Import Cities
            <input
              type="file"
              accept=".json"
              onChange={handleFileImport}
              style={{ display: "none" }}
            />
          </label>
          <Link to="/city/new" className="btn btn-primary">
            <i className="fas fa-plus"></i> Add New City
          </Link>
        </div>
      </div>

      <div className="filter-card">
        <form onSubmit={handleFilterSubmit}>
          <div className="filter-grid">
            <div>
              <label>Filter by Name</label>
              <input
                type="text"
                value={nameFilter}
                onChange={(e) => setNameFilter(e.target.value)}
                placeholder="Enter city name"
              />
            </div>
            <div>
              <label>Sort By</label>
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
              >
                <option value="id">ID</option>
                <option value="name">Name</option>
                <option value="population">Population</option>
                <option value="area">Area</option>
                <option value="creationDate">Creation Date</option>
              </select>
            </div>
            <div>
              <label>Direction</label>
              <select
                value={sortDir}
                onChange={(e) => setSortDir(e.target.value as "asc" | "desc")}
              >
                <option value="asc">Ascending</option>
                <option value="desc">Descending</option>
              </select>
            </div>
            <div>
              <label>Page Size</label>
              <select
                value={size}
                onChange={(e) => setSize(Number(e.target.value))}
              >
                <option value="5">5</option>
                <option value="10">10</option>
                <option value="20">20</option>
                <option value="50">50</option>
              </select>
            </div>
            <div>
              <label>&nbsp;</label>
              <button type="submit" className="btn btn-outline">
                Filter
              </button>
            </div>
          </div>
        </form>
      </div>

      {loading ? (
        <div className="loading">Loading...</div>
      ) : (
        <>
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Coordinates</th>
                  <th>Area</th>
                  <th>Population</th>
                  <th>Climate</th>
                  <th>Capital</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {cities?.content.map((city) => (
                  <tr key={city.id}>
                    <td>{city.id}</td>
                    <td>{city.name}</td>
                    <td>
                      ({city.coordinates.x}, {city.coordinates.y})
                    </td>
                    <td>{city.area}</td>
                    <td>{city.population}</td>
                    <td>{city.climate}</td>
                    <td>
                      <span
                        className={
                          city.capital
                            ? "badge badge-success"
                            : "badge badge-secondary"
                        }
                      >
                        {city.capital ? "Yes" : "No"}
                      </span>
                    </td>
                    <td>
                      <Link
                        to={`/city/${city.id}`}
                        className="btn btn-sm btn-info"
                      >
                        <i className="fas fa-eye"></i>
                      </Link>
                      <Link
                        to={`/city/edit/${city.id}`}
                        className="btn btn-sm btn-warning"
                      >
                        <i className="fas fa-edit"></i>
                      </Link>
                      <button
                        onClick={() => handleDelete(city.id!)}
                        className="btn btn-sm btn-danger"
                      >
                        <i className="fas fa-trash"></i>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {cities && cities.totalPages > 1 && (
            <div className="pagination">
              <button
                onClick={() => setPage(page - 1)}
                disabled={page === 0}
                className="btn btn-outline"
              >
                Previous
              </button>
              {Array.from({ length: cities.totalPages }, (_, i) => (
                <button
                  key={i}
                  onClick={() => setPage(i)}
                  className={`btn ${
                    i === page ? "btn-primary" : "btn-outline"
                  }`}
                >
                  {i + 1}
                </button>
              ))}
              <button
                onClick={() => setPage(page + 1)}
                disabled={page === cities.totalPages - 1}
                className="btn btn-outline"
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
