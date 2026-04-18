import { useState, useEffect } from "react";
import { useSearchParams, Link, useNavigate } from "react-router";
import { Search, Filter, X, Pill, ChevronRight, AlertCircle, Star } from "lucide-react";
import { Card, CardContent } from "./ui/card";
import { Badge } from "./ui/badge";
import { Button } from "./ui/button";
import { Alert, AlertDescription } from "./ui/alert";
import { searchMedicaments, getCompatibleFilters, getFavoriteCis, addFavorite, removeFavorite, type Medicament, type FilterParams } from "../services/api";

export function SearchResults() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const query = searchParams.get("q") || "";

  const [results, setResults] = useState<Medicament[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Single selection for each filter (like dropdowns)
  const [filterSubstance, setFilterSubstance] = useState("");
  const [filterForme, setFilterForme] = useState("");
  const [filterStatut, setFilterStatut] = useState("");
  const [filterRembourse, setFilterRembourse] = useState("");
  const [filterLaboratoire, setFilterLaboratoire] = useState("");

  const [availableFilters, setAvailableFilters] = useState({
    substances: [] as string[],
    formes: [] as string[],
    statuts: [] as string[],
    laboratoires: [] as string[]
  });

  const [showFilters, setShowFilters] = useState(true);
  const [favoriteCis, setFavoriteCis] = useState<Set<string>>(new Set());
  const [favoritesLoading, setFavoritesLoading] = useState(false);

  const isAuthenticated = Boolean(localStorage.getItem("authToken"));

  useEffect(() => {
    const loadFavorites = async () => {
      if (!isAuthenticated) {
        setFavoriteCis(new Set());
        return;
      }

      setFavoritesLoading(true);
      try {
        const cisValues = await getFavoriteCis();
        setFavoriteCis(new Set(cisValues.map(String)));
      } catch {
        // If favorites fail, don't block search.
        setFavoriteCis(new Set());
      } finally {
        setFavoritesLoading(false);
      }
    };

    loadFavorites();
  }, [isAuthenticated]);

  // Perform search whenever filters change
  useEffect(() => {
    const performSearch = async () => {
      setLoading(true);
      setError("");

      try {
        const params: FilterParams = {
          query: query || undefined,
          substance: filterSubstance || undefined,
          forme: filterForme || undefined,
          statut: filterStatut || undefined,
          rembourse: filterRembourse || undefined,
          laboratoire: filterLaboratoire || undefined
        };

        console.log("Search params:", params);
        const data = await searchMedicaments(params);
        console.log("Search results:", data);
        setResults(data.slice(0, 50)); // Limit to 50 results like the original

        // Fetch compatible filters
        await refreshCompatibleFilters(params);
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : "Search failed";
        console.error("Search error:", errorMessage);
        setError(errorMessage);
        setResults([]);
      } finally {
        setLoading(false);
      }
    };

    performSearch();
  }, [query, filterSubstance, filterStatut, filterForme, filterRembourse, filterLaboratoire]);

  const refreshCompatibleFilters = async (params: FilterParams) => {
    try {
      const filters = await getCompatibleFilters(params);
      setAvailableFilters({
        substances: filters.substances || [],
        formes: filters.formes || [],
        statuts: filters.statuts || [],
        laboratoires: filters.laboratoires || []
      });
    } catch (err) {
      console.error("Failed to fetch compatible filters:", err);
    }
  };

  const clearAllFilters = () => {
    setFilterSubstance("");
    setFilterStatut("");
    setFilterForme("");
    setFilterRembourse("");
    setFilterLaboratoire("");
  };

  const hasActiveFilters =
    filterSubstance || filterStatut || filterForme || filterRembourse || filterLaboratoire;

  const getStatusColor = (status: string) => {
    const lowerStatus = status?.toLowerCase() || "";
    if (lowerStatus.includes("retir")) return "bg-red-100 text-red-700 border-red-200";
    if (lowerStatus.includes("suspend")) return "bg-orange-100 text-orange-700 border-orange-200";
    return "bg-green-100 text-green-700 border-green-200";
  };

  const toggleFavorite = async (cis: string) => {
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }

    const key = String(cis);
    const isFav = favoriteCis.has(key);

    // Optimistic UI.
    setFavoriteCis((prev) => {
      const next = new Set(prev);
      if (isFav) next.delete(key);
      else next.add(key);
      return next;
    });

    try {
      if (isFav) await removeFavorite(key);
      else await addFavorite(key);
    } catch {
      // Rollback on error.
      setFavoriteCis((prev) => {
        const next = new Set(prev);
        if (isFav) next.add(key);
        else next.delete(key);
        return next;
      });
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-3xl font-semibold text-gray-900 mb-2">
          Résultats de recherche
        </h1>
        <p className="text-gray-600">
          {loading ? (
            "Recherche en cours..."
          ) : (
            <>
              {results.length} médicament{results.length !== 1 ? "s" : ""} trouvé{results.length !== 1 ? "s" : ""} pour "{query}"
            </>
          )}
        </p>
      </div>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <div className="flex gap-6">
        {/* Filters Sidebar */}
        <aside className={`${showFilters ? "w-64" : "w-0"} transition-all duration-300 overflow-hidden flex-shrink-0`}>
          <Card className="sticky top-20">
            <CardContent className="p-4">
              <div className="flex items-center justify-between mb-4">
                <h2 className="font-semibold text-gray-900 flex items-center gap-2">
                  <Filter className="w-4 h-4" />
                  Filtres
                </h2>
                {hasActiveFilters && (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={clearAllFilters}
                    className="h-auto py-1 px-2 text-xs"
                  >
                    Effacer
                  </Button>
                )}
              </div>

              {/* Substance Filter */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Substance
                </label>
                <select
                  value={filterSubstance}
                  onChange={(e) => setFilterSubstance(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md bg-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Toutes les substances</option>
                  {availableFilters.substances.map((substance) => (
                    <option key={substance} value={substance}>
                      {substance}
                    </option>
                  ))}
                </select>
                <p className="text-xs text-gray-500 mt-1">
                  Les options se réduisent automatiquement selon les résultats possibles.
                </p>
              </div>

              {/* Form Filter */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Forme pharmaceutique
                </label>
                <select
                  value={filterForme}
                  onChange={(e) => setFilterForme(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md bg-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Toutes les formes</option>
                  {availableFilters.formes.map((forme) => (
                    <option key={forme} value={forme}>
                      {forme}
                    </option>
                  ))}
                </select>
                <p className="text-xs text-gray-500 mt-1">
                  Les formes longues sont abrégées pour garder une page lisible.
                </p>
              </div>

              {/* Status Filter */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Statut
                </label>
                <select
                  value={filterStatut}
                  onChange={(e) => setFilterStatut(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md bg-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Tous les statuts</option>
                  {availableFilters.statuts.map((statut) => (
                    <option key={statut} value={statut}>
                      {statut}
                    </option>
                  ))}
                </select>
              </div>

              {/* Filtre remboursement */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Remboursement
                </label>
                <select
                  value={filterRembourse}
                  onChange={(e) => setFilterRembourse(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md bg-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Tous</option>
                  <option value="oui">Oui</option>
                  <option value="non">Non</option>
                </select>
              </div>

              {/* Laboratory Filter */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Laboratoire
                </label>
                <select
                  value={filterLaboratoire}
                  onChange={(e) => setFilterLaboratoire(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md bg-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Tous les laboratoires</option>
                  {availableFilters.laboratoires.map((lab) => (
                    <option key={lab} value={lab}>
                      {lab}
                    </option>
                  ))}
                </select>
              </div>
            </CardContent>
          </Card>
        </aside>

        {/* Results */}
        <div className="flex-1">
          {/* Toggle Filters Button */}
          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowFilters(!showFilters)}
            className="mb-4"
          >
            <Filter className="w-4 h-4 mr-2" />
            {showFilters ? "Masquer les filtres" : "Afficher les filtres"}
          </Button>

          {/* Active Filters Pills */}
          {hasActiveFilters && (
            <div className="flex flex-wrap gap-2 mb-4">
              {filterSubstance && (
                <Badge
                  variant="secondary"
                  className="flex items-center gap-1"
                >
                  Substance: {filterSubstance}
                  <button
                    onClick={() => setFilterSubstance("")}
                    className="ml-1 hover:bg-gray-300 rounded-full p-0.5"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </Badge>
              )}
              {filterStatut && (
                <Badge
                  variant="secondary"
                  className="flex items-center gap-1"
                >
                  Statut: {filterStatut}
                  <button
                    onClick={() => setFilterStatut("")}
                    className="ml-1 hover:bg-gray-300 rounded-full p-0.5"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </Badge>
              )}
              {filterForme && (
                <Badge
                  variant="secondary"
                  className="flex items-center gap-1"
                >
                  Forme: {filterForme}
                  <button
                    onClick={() => setFilterForme("")}
                    className="ml-1 hover:bg-gray-300 rounded-full p-0.5"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </Badge>
              )}
              {filterRembourse && (
                <Badge
                  variant="secondary"
                  className="flex items-center gap-1"
                >
                  RemboursÃ©: {filterRembourse === "oui" ? "Oui" : "Non"}
                  <button
                    onClick={() => setFilterRembourse("")}
                    className="ml-1 hover:bg-gray-300 rounded-full p-0.5"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </Badge>
              )}
              {filterLaboratoire && (
                <Badge
                  variant="secondary"
                  className="flex items-center gap-1"
                >
                  Labo: {filterLaboratoire}
                  <button
                    onClick={() => setFilterLaboratoire("")}
                    className="ml-1 hover:bg-gray-300 rounded-full p-0.5"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </Badge>
              )}
            </div>
          )}

          {/* Medicaments List */}
          {!loading && results.length > 0 ? (
            <div className="space-y-3">
              {results.map((med) => (
                <Link key={med.cis} to={`/drug/${med.cis}`}>
                  <Card className="hover:shadow-md transition-shadow cursor-pointer">
                    <CardContent className="p-5">
                      <div className="flex items-start justify-between gap-4">
                        <div className="flex items-start gap-4 flex-1">
                          <div className="w-12 h-12 bg-blue-50 rounded-lg flex items-center justify-center flex-shrink-0">
                            <Pill className="w-6 h-6 text-blue-600" />
                          </div>

                          <div className="flex-1">
                            <div className="flex items-center gap-2 mb-1">
                              <h3 className="font-semibold text-gray-900">
                                {med.name}
                              </h3>
                              <Badge
                                className={`${getStatusColor(
                                  med.status
                                )} border text-xs`}
                              >
                                {med.status}
                              </Badge>
                            </div>

                            <p className="text-sm text-gray-600 mb-2">
                              {med.pharmaceuticalForm} • {med.administrationRoute}
                            </p>

                            <div className="flex flex-wrap gap-2 mb-2">
                              {med.activeSubstances.map((substance, idx) => (
                                <Badge key={idx} variant="outline" className="text-xs">
                                  {substance}
                                </Badge>
                              ))}
                            </div>

                            <p className="text-xs text-gray-500">
                              {med.laboratory}
                            </p>
                          </div>
                        </div>

                        <button
                          type="button"
                          disabled={favoritesLoading}
                          onClick={(e) => {
                            e.preventDefault();
                            e.stopPropagation();
                            toggleFavorite(String(med.cis));
                          }}
                          className="p-2 rounded-md hover:bg-gray-50 disabled:opacity-50"
                          title={favoriteCis.has(String(med.cis)) ? "Retirer des favoris" : "Ajouter aux favoris"}
                        >
                          <Star
                            className={
                              favoriteCis.has(String(med.cis))
                                ? "w-5 h-5 text-yellow-500 fill-yellow-500"
                                : "w-5 h-5 text-gray-400"
                            }
                          />
                        </button>

                        <ChevronRight className="w-5 h-5 text-gray-400 flex-shrink-0" />
                      </div>
                    </CardContent>
                  </Card>
                </Link>
              ))}
            </div>
          ) : !loading && results.length === 0 && !error ? (
            <Card>
              <CardContent className="p-12 text-center">
                <Search className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                <p className="text-gray-500 mb-2">Aucun médicament trouvé</p>
                <p className="text-sm text-gray-400">
                  Essayez de modifier vos critères de recherche ou de filtrage
                </p>
              </CardContent>
            </Card>
          ) : loading ? (
            <Card>
              <CardContent className="p-12 text-center">
                <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                <p className="text-gray-500 mt-4">Recherche en cours...</p>
              </CardContent>
            </Card>
          ) : null}

          {results.length === 50 && (
            <Alert className="mt-4 bg-blue-50 border-blue-200">
              <AlertCircle className="h-4 w-4 text-blue-600" />
              <AlertDescription className="text-blue-900">
                50 médicaments affichés maximum. Affinez les filtres pour voir moins de résultats.
              </AlertDescription>
            </Alert>
          )}
        </div>
      </div>
    </div>
  );
}
