import { useState, useMemo } from "react";
import { useSearchParams, Link } from "react-router";
import { Search, Filter, X, Pill, ChevronRight, ChevronDown } from "lucide-react";
import { Card, CardContent } from "./ui/card";
import { Badge } from "./ui/badge";
import { Button } from "./ui/button";
import { Checkbox } from "./ui/checkbox";
import { medicaments, activeSubstancesList } from "../data/medicamentsData";

export function SearchResults() {
  const [searchParams] = useSearchParams();
  const query = searchParams.get("q") || "";
  
  const [selectedSubstances, setSelectedSubstances] = useState<string[]>([]);
  const [selectedStatuses, setSelectedStatuses] = useState<string[]>([]);
  const [selectedForms, setSelectedForms] = useState<string[]>([]);
  const [showFilters, setShowFilters] = useState(true);
  const [showMoreSubstances, setShowMoreSubstances] = useState(false);

  // Get unique pharmaceutical forms
  const pharmaceuticalForms = useMemo(() => {
    return [...new Set(medicaments.map(m => m.pharmaceuticalForm))];
  }, []);

  const statuses = ['Autorisé', 'Retiré', 'Suspendu'];

  // Limit substances to 8 initially
  const MAX_VISIBLE_SUBSTANCES = 8;
  const visibleSubstances = showMoreSubstances 
    ? activeSubstancesList 
    : activeSubstancesList.slice(0, MAX_VISIBLE_SUBSTANCES);
  const hasMoreSubstances = activeSubstancesList.length > MAX_VISIBLE_SUBSTANCES;

  // Filter medicaments
  const filteredMedicaments = useMemo(() => {
    return medicaments.filter(med => {
      // Search query filter
      const matchesQuery = 
        med.name.toLowerCase().includes(query.toLowerCase()) ||
        med.laboratory.toLowerCase().includes(query.toLowerCase()) ||
        med.activeSubstances.some(s => s.toLowerCase().includes(query.toLowerCase()));

      // Active substance filter
      const matchesSubstance = selectedSubstances.length === 0 || 
        med.activeSubstances.some(s => selectedSubstances.includes(s));

      // Status filter
      const matchesStatus = selectedStatuses.length === 0 || 
        selectedStatuses.includes(med.status);

      // Form filter
      const matchesForm = selectedForms.length === 0 || 
        selectedForms.includes(med.pharmaceuticalForm);

      return matchesQuery && matchesSubstance && matchesStatus && matchesForm;
    });
  }, [query, selectedSubstances, selectedStatuses, selectedForms]);

  const toggleFilter = (filterArray: string[], setFilter: (val: string[]) => void, value: string) => {
    if (filterArray.includes(value)) {
      setFilter(filterArray.filter(v => v !== value));
    } else {
      setFilter([...filterArray, value]);
    }
  };

  const clearAllFilters = () => {
    setSelectedSubstances([]);
    setSelectedStatuses([]);
    setSelectedForms([]);
  };

  const hasActiveFilters = selectedSubstances.length > 0 || selectedStatuses.length > 0 || selectedForms.length > 0;

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Autorisé': return 'bg-green-100 text-green-700 border-green-200';
      case 'Retiré': return 'bg-red-100 text-red-700 border-red-200';
      case 'Suspendu': return 'bg-orange-100 text-orange-700 border-orange-200';
      default: return 'bg-gray-100 text-gray-700 border-gray-200';
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
          {filteredMedicaments.length} médicament{filteredMedicaments.length !== 1 ? 's' : ''} trouvé{filteredMedicaments.length !== 1 ? 's' : ''} pour "{query}"
        </p>
      </div>

      <div className="flex gap-6">
        {/* Filters Sidebar */}
        <aside className={`${showFilters ? 'w-64' : 'w-0'} transition-all duration-300 overflow-hidden flex-shrink-0`}>
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

              {/* Active Substances Filter */}
              <div className="mb-6">
                <h3 className="font-medium text-sm text-gray-700 mb-3">Substance active</h3>
                <div className="space-y-2">
                  {visibleSubstances.map((substance) => (
                    <label 
                      key={substance}
                      className="flex items-center gap-2 cursor-pointer hover:bg-gray-50 p-1 rounded"
                    >
                      <Checkbox
                        checked={selectedSubstances.includes(substance)}
                        onCheckedChange={() => toggleFilter(selectedSubstances, setSelectedSubstances, substance)}
                      />
                      <span className="text-sm text-gray-700">{substance}</span>
                    </label>
                  ))}
                </div>
                {hasMoreSubstances && (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setShowMoreSubstances(!showMoreSubstances)}
                    className="w-full mt-2 text-blue-600 hover:text-blue-700 hover:bg-blue-50"
                  >
                    <ChevronDown className={`w-4 h-4 mr-1 transition-transform ${showMoreSubstances ? 'rotate-180' : ''}`} />
                    {showMoreSubstances ? 'Moins de filtres' : `+ ${activeSubstancesList.length - MAX_VISIBLE_SUBSTANCES} de filtres`}
                  </Button>
                )}
              </div>

              {/* Status Filter */}
              <div className="mb-6">
                <h3 className="font-medium text-sm text-gray-700 mb-3">Statut</h3>
                <div className="space-y-2">
                  {statuses.map((status) => (
                    <label 
                      key={status}
                      className="flex items-center gap-2 cursor-pointer hover:bg-gray-50 p-1 rounded"
                    >
                      <Checkbox
                        checked={selectedStatuses.includes(status)}
                        onCheckedChange={() => toggleFilter(selectedStatuses, setSelectedStatuses, status)}
                      />
                      <span className="text-sm text-gray-700">{status}</span>
                    </label>
                  ))}
                </div>
              </div>

              {/* Pharmaceutical Form Filter */}
              <div>
                <h3 className="font-medium text-sm text-gray-700 mb-3">Forme pharmaceutique</h3>
                <div className="space-y-2">
                  {pharmaceuticalForms.map((form) => (
                    <label 
                      key={form}
                      className="flex items-center gap-2 cursor-pointer hover:bg-gray-50 p-1 rounded"
                    >
                      <Checkbox
                        checked={selectedForms.includes(form)}
                        onCheckedChange={() => toggleFilter(selectedForms, setSelectedForms, form)}
                      />
                      <span className="text-sm text-gray-700">{form}</span>
                    </label>
                  ))}
                </div>
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
            {showFilters ? 'Masquer les filtres' : 'Afficher les filtres'}
          </Button>

          {/* Active Filters Pills */}
          {hasActiveFilters && (
            <div className="flex flex-wrap gap-2 mb-4">
              {selectedSubstances.map(substance => (
                <Badge 
                  key={substance} 
                  variant="secondary"
                  className="flex items-center gap-1"
                >
                  {substance}
                  <button 
                    onClick={() => toggleFilter(selectedSubstances, setSelectedSubstances, substance)}
                    className="ml-1 hover:bg-gray-300 rounded-full p-0.5"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </Badge>
              ))}
              {selectedStatuses.map(status => (
                <Badge 
                  key={status} 
                  variant="secondary"
                  className="flex items-center gap-1"
                >
                  {status}
                  <button 
                    onClick={() => toggleFilter(selectedStatuses, setSelectedStatuses, status)}
                    className="ml-1 hover:bg-gray-300 rounded-full p-0.5"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </Badge>
              ))}
              {selectedForms.map(form => (
                <Badge 
                  key={form} 
                  variant="secondary"
                  className="flex items-center gap-1"
                >
                  {form}
                  <button 
                    onClick={() => toggleFilter(selectedForms, setSelectedForms, form)}
                    className="ml-1 hover:bg-gray-300 rounded-full p-0.5"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </Badge>
              ))}
            </div>
          )}

          {/* Medicaments List */}
          {filteredMedicaments.length > 0 ? (
            <div className="space-y-3">
              {filteredMedicaments.map((med) => (
                <Link key={med.id} to={`/drug/${med.id}`}>
                  <Card className="hover:shadow-md transition-shadow cursor-pointer">
                    <CardContent className="p-5">
                      <div className="flex items-start justify-between gap-4">
                        <div className="flex items-start gap-4 flex-1">
                          <div className="w-12 h-12 bg-blue-50 rounded-lg flex items-center justify-center flex-shrink-0">
                            <Pill className="w-6 h-6 text-blue-600" />
                          </div>
                          
                          <div className="flex-1">
                            <div className="flex items-center gap-2 mb-1">
                              <h3 className="font-semibold text-gray-900">{med.name}</h3>
                              <Badge className={`${getStatusColor(med.status)} border text-xs`}>
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
                        
                        <ChevronRight className="w-5 h-5 text-gray-400 flex-shrink-0" />
                      </div>
                    </CardContent>
                  </Card>
                </Link>
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="p-12 text-center">
                <Search className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                <p className="text-gray-500 mb-2">Aucun médicament trouvé</p>
                <p className="text-sm text-gray-400">
                  Essayez de modifier vos critères de recherche ou de filtrage
                </p>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}