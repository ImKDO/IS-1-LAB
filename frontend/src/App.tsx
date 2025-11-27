import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import CityList from "./components/CityList";
import CityForm from "./components/CityForm";
import CityDetail from "./components/CityDetail";
import SpecialOperations from "./components/SpecialOperations";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <div className="app">
        <nav className="navbar">
          <div className="container">
            <Link to="/" className="navbar-brand">
              City Management System
            </Link>
            <div className="navbar-nav">
              <Link to="/special-operations" className="nav-link">
                Special Operations
              </Link>
            </div>
          </div>
        </nav>

        <main className="main-content">
          <Routes>
            <Route path="/" element={<CityList />} />
            <Route path="/city/new" element={<CityForm />} />
            <Route path="/city/edit/:id" element={<CityForm />} />
            <Route path="/city/:id" element={<CityDetail />} />
            <Route path="/special-operations" element={<SpecialOperations />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
