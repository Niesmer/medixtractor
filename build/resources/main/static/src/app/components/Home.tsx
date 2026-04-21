import { useState } from "react";
import { useNavigate } from "react-router";
import { Search, Pill, Filter, FileText, CheckCircle } from "lucide-react";
import { Input } from "./ui/input";
import { Button } from "./ui/button";
import { Card, CardContent } from "./ui/card";

export function Home() {
  const [searchQuery, setSearchQuery] = useState("");
  const navigate = useNavigate();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
    }
  };

  const features = [
    {
      icon: Search,
      title: "Recherche avancée",
      description: "Recherchez rapidement n'importe quel médicament par son nom ou sa substance active"
    },
    {
      icon: Filter,
      title: "Filtrage précis",
      description: "Filtrez les résultats par forme pharmaceutique, laboratoire et statut"
    },
    {
      icon: FileText,
      title: "Informations complètes",
      description: "Consultez toutes les informations détaillées : composition, posologie, présentations"
    },
    {
      icon: CheckCircle,
      title: "Base de données officielle",
      description: "Accès aux données publiques officielles des médicaments autorisés"
    }
  ];

  const quickSearches = [
    "Paracétamol",
    "Amoxicilline",
    "Ibuprofène",
    "Levothyrox",
    "Ventoline",
    "Atorvastatine"
  ];

  return (
    <div className="min-h-[calc(100vh-8rem)]">
      {/* Hero Section */}
      <div className="bg-gradient-to-b from-blue-50 to-white py-16 sm:py-24">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <div className="inline-flex items-center gap-2 bg-blue-100 text-blue-700 px-4 py-2 rounded-full text-sm font-medium mb-6">
            <Pill className="w-4 h-4" />
            Base de données médicaments
          </div>
          
          <h1 className="text-4xl sm:text-5xl font-bold text-gray-900 mb-6">
            Consultation rapide des<br />médicaments
          </h1>
          
          <p className="text-xl text-gray-600 mb-12 max-w-2xl mx-auto">
            Accédez instantanément aux informations détaillées de tous les médicaments 
            référencés dans la base publique
          </p>

          {/* Search Box */}
          <form onSubmit={handleSearch} className="max-w-2xl mx-auto">
            <div className="relative">
              <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 w-6 h-6 text-gray-400" />
              <Input
                type="text"
                placeholder="Rechercher un médicament par nom..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-14 pr-4 h-14 text-lg bg-white shadow-lg border-gray-200 focus:ring-2 focus:ring-blue-500"
              />
              <Button 
                type="submit"
                className="absolute right-2 top-1/2 transform -translate-y-1/2 h-10"
              >
                Rechercher
              </Button>
            </div>
          </form>

          {/* Quick Searches */}
          <div className="mt-6">
            <p className="text-sm text-gray-500 mb-3">Recherches populaires :</p>
            <div className="flex flex-wrap justify-center gap-2">
              {quickSearches.map((term) => (
                <button
                  key={term}
                  onClick={() => navigate(`/search?q=${encodeURIComponent(term)}`)}
                  className="px-3 py-1 bg-white border border-gray-200 rounded-full text-sm text-gray-700 hover:border-blue-500 hover:text-blue-600 transition-colors"
                >
                  {term}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="text-center mb-12">
          <h2 className="text-3xl font-semibold text-gray-900 mb-4">
            Fonctionnalités
          </h2>
          <p className="text-lg text-gray-600">
            Un outil complet pour les professionnels de santé
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {features.map((feature) => {
            const Icon = feature.icon;
            return (
              <Card key={feature.title} className="border-gray-200 hover:shadow-lg transition-shadow">
                <CardContent className="p-6">
                  <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4">
                    <Icon className="w-6 h-6 text-blue-600" />
                  </div>
                  <h3 className="font-semibold text-gray-900 mb-2">
                    {feature.title}
                  </h3>
                  <p className="text-sm text-gray-600">
                    {feature.description}
                  </p>
                </CardContent>
              </Card>
            );
          })}
        </div>
      </div>

      {/* Stats Section */}
      <div className="bg-blue-600 text-white py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 text-center">
            <div>
              <p className="text-4xl font-bold mb-2">10 000+</p>
              <p className="text-blue-100">Médicaments référencés</p>
            </div>
            <div>
              <p className="text-4xl font-bold mb-2">500+</p>
              <p className="text-blue-100">Laboratoires</p>
            </div>
            <div>
              <p className="text-4xl font-bold mb-2">100%</p>
              <p className="text-blue-100">Base de données officielle</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
