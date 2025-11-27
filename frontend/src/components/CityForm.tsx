import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { cityApi } from "../api/cityApi";
import { City, Climate, Government, StandardOfLiving, Human } from "../types";

export default function CityForm() {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const isEdit = !!id;

  const [formData, setFormData] = useState<City>({
    name: "",
    coordinates: { x: 0, y: 0 },
    area: 0,
    population: 0,
    capital: false,
    climate: Climate.TROPICAL_SAVANNA,
    standardOfLiving: StandardOfLiving.VERY_LOW,
    governor: { height: 0 },
  });

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [notification, setNotification] = useState<{
    message: string;
    type: "success" | "error";
  } | null>(null);

  const showNotification = (message: string, type: "success" | "error") => {
    setNotification({ message, type });
    setTimeout(() => setNotification(null), 5000);
  };

  useEffect(() => {
    if (isEdit) {
      loadCity();
    }
  }, [id]);

  const loadCity = async () => {
    try {
      const response = await cityApi.getById(Number(id));
      setFormData(response.data);
    } catch (error) {
      console.error("Error loading city:", error);
      showNotification("Failed to load city", "error");
    }
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value, type } = e.target;
    const checked = (e.target as HTMLInputElement).checked;

    if (name.startsWith("coordinates.")) {
      const field = name.split(".")[1];
      setFormData((prev) => ({
        ...prev,
        coordinates: { ...prev.coordinates, [field]: parseFloat(value) || 0 },
      }));
    } else if (name.startsWith("governor.")) {
      const field = name.split(".")[1];
      setFormData((prev) => ({
        ...prev,
        governor: {
          ...(prev.governor || { height: 0 }),
          [field]: field === "height" ? parseFloat(value) || 0 : value,
        } as Human,
      }));
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]:
          type === "checkbox"
            ? checked
            : type === "number"
            ? parseFloat(value) || 0
            : value,
      }));
    }
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) newErrors.name = "Name is required";
    if (formData.area <= 0) newErrors.area = "Area must be greater than 0";
    if (formData.population <= 0)
      newErrors.population = "Population must be greater than 0";
    if (formData.coordinates.x < -686.99)
      newErrors["coordinates.x"] = "X must be greater than -687";
    if (formData.coordinates.y < -448.99)
      newErrors["coordinates.y"] = "Y must be greater than -449";
    if (
      !formData.governor ||
      !formData.governor.height ||
      formData.governor.height <= 0
    ) {
      newErrors["governor.height"] = "Governor height must be greater than 0";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const validateCoordinates = async (): Promise<boolean> => {
    try {
      // Get all cities to check for coordinate conflicts
      const response = await cityApi.getAll({ size: 1000 });
      const cities = response.data.content;

      const conflictingCity = cities.find((city) => {
        // Skip the current city if editing
        if (isEdit && city.id === Number(id)) {
          return false;
        }
        // Check if coordinates match (with small tolerance for floating point)
        return (
          Math.abs(city.coordinates.x - formData.coordinates.x) < 0.001 &&
          Math.abs(city.coordinates.y - formData.coordinates.y) < 0.001
        );
      });

      if (conflictingCity) {
        setErrors((prev) => ({
          ...prev,
          "coordinates.x": `Coordinates already occupied by "${conflictingCity.name}" (ID: ${conflictingCity.id})`,
          "coordinates.y": `Coordinates already occupied by "${conflictingCity.name}" (ID: ${conflictingCity.id})`,
        }));
        showNotification(
          `Coordinates (${formData.coordinates.x}, ${formData.coordinates.y}) are already occupied by city "${conflictingCity.name}"`,
          "error"
        );
        return false;
      }
      return true;
    } catch (error) {
      console.error("Error validating coordinates:", error);
      // If validation check fails, let server validate
      return true;
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) {
      console.log("Validation failed");
      return;
    }

    // Validate coordinates uniqueness
    const coordinatesValid = await validateCoordinates();
    if (!coordinatesValid) {
      console.log("Coordinate validation failed");
      return;
    }

    // Clean up data before sending
    const dataToSend: City = {
      ...formData,
      government: formData.government || undefined,
      governor: formData.governor?.height
        ? formData.governor
        : { height: formData.governor?.height || 0 },
    };

    try {
      console.log("Submitting city data:", JSON.stringify(dataToSend, null, 2));
      let response;
      if (isEdit) {
        response = await cityApi.update(Number(id), dataToSend);
        console.log("Update response:", response.data);
      } else {
        response = await cityApi.create(dataToSend);
        console.log("Create response:", response.data);
      }
      showNotification("City saved successfully!", "success");
      setTimeout(() => navigate("/"), 1500);
    } catch (error: any) {
      console.error("Error saving city:", error);
      console.error("Error response:", error.response);
      console.error("Error data:", error.response?.data);

      let errorMessage = "Unexpected error";

      if (error.response) {
        // Server responded with error
        const serverError = error.response.data;
        if (serverError.details && Array.isArray(serverError.details)) {
          errorMessage = serverError.details.join(", ");
        } else {
          errorMessage =
            serverError.message ||
            serverError.error ||
            JSON.stringify(serverError) ||
            `Server error: ${error.response.status}`;
        }
      } else if (error.request) {
        // Request made but no response
        errorMessage =
          "No response from server. Is the backend running on port 8080?";
      } else {
        // Something else happened
        errorMessage = error.message || "Unknown error";
      }

      showNotification(`Error: ${errorMessage}`, "error");
    }
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
      <div className="form-card">
        <h4>{isEdit ? "Edit City" : "Create New City"}</h4>
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>City Name *</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className={errors.name ? "error" : ""}
              />
              {errors.name && <span className="error-text">{errors.name}</span>}
            </div>
            <div className="form-group">
              <label>Area *</label>
              <input
                type="number"
                name="area"
                step="0.01"
                value={formData.area}
                onChange={handleChange}
                className={errors.area ? "error" : ""}
              />
              {errors.area && <span className="error-text">{errors.area}</span>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Population *</label>
              <input
                type="number"
                name="population"
                value={formData.population}
                onChange={handleChange}
                className={errors.population ? "error" : ""}
              />
              {errors.population && (
                <span className="error-text">{errors.population}</span>
              )}
            </div>
            <div className="form-group">
              <label>Meters Above Sea Level</label>
              <input
                type="number"
                name="metersAboveSeaLevel"
                step="0.01"
                value={formData.metersAboveSeaLevel || ""}
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Climate *</label>
              <select
                name="climate"
                value={formData.climate}
                onChange={handleChange}
              >
                {Object.values(Climate).map((c) => (
                  <option key={c} value={c}>
                    {c}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Government</label>
              <select
                name="government"
                value={formData.government || ""}
                onChange={handleChange}
              >
                <option value="">Select Government</option>
                {Object.values(Government).map((g) => (
                  <option key={g} value={g}>
                    {g}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Standard of Living *</label>
              <select
                name="standardOfLiving"
                value={formData.standardOfLiving}
                onChange={handleChange}
              >
                {Object.values(StandardOfLiving).map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>X Coordinate *</label>
              <input
                type="number"
                name="coordinates.x"
                step="0.01"
                value={formData.coordinates.x}
                onChange={handleChange}
                className={errors["coordinates.x"] ? "error" : ""}
              />
              {errors["coordinates.x"] && (
                <span className="error-text">{errors["coordinates.x"]}</span>
              )}
            </div>
            <div className="form-group">
              <label>Y Coordinate *</label>
              <input
                type="number"
                name="coordinates.y"
                step="0.01"
                value={formData.coordinates.y}
                onChange={handleChange}
                className={errors["coordinates.y"] ? "error" : ""}
              />
              {errors["coordinates.y"] && (
                <span className="error-text">{errors["coordinates.y"]}</span>
              )}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Governor Height *</label>
              <input
                type="number"
                name="governor.height"
                step="0.01"
                value={formData.governor?.height || ""}
                onChange={handleChange}
                className={errors["governor.height"] ? "error" : ""}
              />
              {errors["governor.height"] && (
                <span className="error-text">{errors["governor.height"]}</span>
              )}
            </div>
            <div className="form-group">
              <label>Establishment Date</label>
              <input
                type="date"
                name="establishmentDate"
                value={formData.establishmentDate || ""}
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="checkbox-label">
              <input
                type="checkbox"
                name="capital"
                checked={formData.capital}
                onChange={handleChange}
              />
              Capital City
            </label>
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={() => navigate("/")}
              className="btn btn-secondary"
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              {isEdit ? "Update" : "Create"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
