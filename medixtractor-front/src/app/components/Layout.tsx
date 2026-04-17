import { Outlet, Link, useNavigate, useSearchParams, useLocation } from "react-router";
import { Search, Pill, X, LogOut } from "lucide-react";
import { Input } from "./ui/input";
import { Button } from "./ui/button";
import { useState, useEffect, useRef } from "react";
import { medicaments } from "../data/medicamentsData";
import { logout } from "../services/api";

export function Layout() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const [searchValue, setSearchValue] = useState("");
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [focusedIndex, setFocusedIndex] = useState(-1);
  const searchRef = useRef<HTMLDivElement>(null);

  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<any>(null);
  const [showUserMenu, setShowUserMenu] = useState(false);

  // Check authentication on mount and when location changes
  useEffect(() => {
    const token = localStorage.getItem("authToken");
    const userData = localStorage.getItem("user");
    if (token && userData) {
      setIsAuthenticated(true);
      try {
        setUser(JSON.parse(userData));
      } catch (e) {
        setIsAuthenticated(false);
      }
    } else {
      setIsAuthenticated(false);
      setUser(null);
    }
  }, [location]);

  useEffect(() => {
    const query = searchParams.get("q");
    if (query) {
      setSearchValue(query);
    } else if (location.pathname === "/") {
      setSearchValue("");
    }
  }, [searchParams, location]);

  // Close suggestions when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (searchRef.current && !searchRef.current.contains(event.target as Node)) {
        setShowSuggestions(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Get search suggestions
  const suggestions = searchValue.trim().length >= 2
    ? medicaments
        .filter(med =>
          med.name.toLowerCase().includes(searchValue.toLowerCase()) ||
          med.activeSubstances.some(s => s.toLowerCase().includes(searchValue.toLowerCase()))
        )
        .slice(0, 6)
    : [];

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    // Allow empty search - navigate to search page with or without query
    const query = searchValue.trim();
    navigate(`/search${query ? `?q=${encodeURIComponent(query)}` : ""}`);
    setShowSuggestions(false);
  };

  const handleSuggestionClick = (medName: string) => {
    setSearchValue(medName);
    navigate(`/search?q=${encodeURIComponent(medName)}`);
    setShowSuggestions(false);
  };

  const clearSearch = () => {
    setSearchValue("");
    setShowSuggestions(false);
    setFocusedIndex(-1);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (!showSuggestions || suggestions.length === 0) return;

    switch (e.key) {
      case "ArrowDown":
        e.preventDefault();
        setFocusedIndex(prev => (prev < suggestions.length - 1 ? prev + 1 : prev));
        break;
      case "ArrowUp":
        e.preventDefault();
        setFocusedIndex(prev => (prev > 0 ? prev - 1 : -1));
        break;
      case "Enter":
        if (focusedIndex >= 0 && focusedIndex < suggestions.length) {
          e.preventDefault();
          handleSuggestionClick(suggestions[focusedIndex].name);
        }
        break;
      case "Escape":
        setShowSuggestions(false);
        setFocusedIndex(-1);
        break;
    }
  };

  const handleLogout = async () => {
    await logout();
    setIsAuthenticated(false);
    setUser(null);
    setShowUserMenu(false);
    navigate("/");
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Top Navigation */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-50 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            {/* Logo */}
            <Link to="/" className="flex items-center gap-2 hover:opacity-80 transition-opacity flex-shrink-0">
              <div className="w-10 h-10 bg-blue-600 rounded-lg flex items-center justify-center">
                <Pill className="w-6 h-6 text-white" />
              </div>
              <span className="text-xl font-semibold text-gray-900">Medixtractor</span>
            </Link>

            {/* Search Bar */}
            <div className="flex-1 max-w-2xl mx-8" ref={searchRef}>
              <form onSubmit={handleSearch} className="relative">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400 z-10" />
                  <Input
                    type="text"
                    placeholder="Rechercher un médicament, substance active..."
                    value={searchValue}
                    onChange={(e) => {
                      setSearchValue(e.target.value);
                      setShowSuggestions(true);
                      setFocusedIndex(-1);
                    }}
                    onFocus={() => {
                      if (searchValue.trim().length >= 2) {
                        setShowSuggestions(true);
                      }
                    }}
                    onKeyDown={handleKeyDown}
                    className="pl-10 pr-10 h-11 bg-gray-50 border-gray-200 focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all"
                  />
                  {searchValue && (
                    <button
                      type="button"
                      onClick={clearSearch}
                      className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 z-10"
                    >
                      <X className="w-4 h-4" />
                    </button>
                  )}
                </div>

                {/* Suggestions Dropdown */}
                {showSuggestions && suggestions.length > 0 && (
                  <div className="absolute top-full left-0 right-0 mt-2 bg-white border border-gray-200 rounded-lg shadow-lg overflow-hidden z-50">
                    <div className="py-2">
                      {suggestions.map((med, index) => (
                        <button
                          key={med.id}
                          type="button"
                          onClick={() => handleSuggestionClick(med.name)}
                          className={`w-full px-4 py-2.5 text-left hover:bg-gray-50 transition-colors flex items-start gap-3 ${
                            index === focusedIndex ? 'bg-blue-50' : ''
                          }`}
                        >
                          <Pill className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
                          <div className="flex-1 min-w-0">
                            <p className="font-medium text-gray-900 truncate">{med.name}</p>
                            <p className="text-xs text-gray-500 truncate">
                              {med.activeSubstances.join(", ")} • {med.laboratory}
                            </p>
                          </div>
                        </button>
                      ))}
                    </div>
                    <div className="border-t border-gray-100 px-4 py-2 bg-gray-50">
                      <button
                        type="submit"
                        className="text-sm text-blue-600 hover:text-blue-700 font-medium"
                      >
                        Voir tous les résultats pour "{searchValue}"
                      </button>
                    </div>
                  </div>
                )}
              </form>
            </div>

            {/* Auth Section */}
            <div className="flex items-center gap-3 flex-shrink-0">
              {isAuthenticated && user ? (
                <div className="relative">
                  <button
                    onClick={() => setShowUserMenu(!showUserMenu)}
                    className="flex items-center gap-3 hover:opacity-80 transition-opacity"
                  >
                    <div className="text-right hidden md:block">
                      <p className="text-sm font-medium text-gray-900">{user.fullName}</p>
                      <p className="text-xs text-gray-500">{user.role}</p>
                    </div>
                    <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                      <span className="text-sm font-semibold text-blue-600">
                        {user.fullName.split(" ").map((n: string) => n[0]).join("").toUpperCase()}
                      </span>
                    </div>
                  </button>

                  {/* User Menu Dropdown */}
                  {showUserMenu && (
                    <div className="absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-lg overflow-hidden z-50">
                      <button
                        onClick={handleLogout}
                        className="w-full px-4 py-3 text-left text-gray-700 hover:bg-gray-50 transition-colors flex items-center gap-2 font-medium"
                      >
                        <LogOut className="w-4 h-4" />
                        Déconnexion
                      </button>
                    </div>
                  )}
                </div>
              ) : (
                <div className="flex items-center gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => navigate("/login")}
                  >
                    Se connecter
                  </Button>
                  <Button
                    size="sm"
                    onClick={() => navigate("/signup")}
                  >
                    S'inscrire
                  </Button>
                </div>
              )}
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main>
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-gray-200 mt-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <p className="text-sm text-gray-500">
              © 2026 Medixtractor - Outil de consultation des médicaments
            </p>
            <p className="text-xs text-gray-400">
              Données issues de la base publique des médicaments
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
}
