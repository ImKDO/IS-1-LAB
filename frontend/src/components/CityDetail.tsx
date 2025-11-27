import { useState, useEffect } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { cityApi } from "../api/cityApi";
import { City } from "../types";

export default function CityDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [city, setCity] = useState<City | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadCity();
  }, [id]);

  const loadCity = async () => {
    try {
      const response = await cityApi.getById(Number(id));
      setCity(response.data);
    } catch (error) {
      console.error("Error loading city:", error);
      alert("Failed to load city");
      navigate("/");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!confirm("Are you sure you want to delete this city?")) return;
    try {
      await cityApi.delete(Number(id));
      navigate("/");
    } catch (error) {
      console.error("Error deleting city:", error);
      alert("Failed to delete city");
    }
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (!city) return <div className="container">City not found</div>;

  return (
    <div className="container">
      <div className="detail-card">
        <div className="detail-header">
          <h2>{city.name}</h2>
          <div>
            <Link to={`/city/edit/${city.id}`} className="btn btn-warning">
              <i className="fas fa-edit"></i> Edit
            </Link>
            <button onClick={handleDelete} className="btn btn-danger">
              <i className="fas fa-trash"></i> Delete
            </button>
          </div>
        </div>

        <div className="detail-grid">
          <div className="detail-item">
            <span className="detail-label">ID:</span>
            <span>{city.id}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Name:</span>
            <span>{city.name}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Coordinates:</span>
            <span>
              ({city.coordinates.x}, {city.coordinates.y})
            </span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Area:</span>
            <span>{city.area}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Population:</span>
            <span>{city.population}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Climate:</span>
            <span>{city.climate}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Government:</span>
            <span>{city.government || "N/A"}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Standard of Living:</span>
            <span>{city.standardOfLiving}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Capital:</span>
            <span
              className={
                city.capital ? "badge badge-success" : "badge badge-secondary"
              }
            >
              {city.capital ? "Yes" : "No"}
            </span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Meters Above Sea Level:</span>
            <span>{city.metersAboveSeaLevel || "N/A"}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Establishment Date:</span>
            <span>{city.establishmentDate || "N/A"}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Creation Date:</span>
            <span>{new Date(city.creationDate!).toLocaleString()}</span>
          </div>
          {city.governor && (
            <div className="detail-item">
              <span className="detail-label">Governor Height:</span>
              <span>{city.governor.height}</span>
            </div>
          )}
        </div>

        <div className="detail-actions">
          <Link to="/" className="btn btn-outline">
            <i className="fas fa-arrow-left"></i> Back to List
          </Link>
        </div>
      </div>
    </div>
  );
}
