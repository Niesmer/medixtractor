import { useState, useEffect } from "react";
import { useNavigate } from "react-router";
import { Search, Pill, Filter, FileText, CheckCircle, Upload, AlertCircle, CheckCircle2, XCircle } from "lucide-react";
import { Input } from "./ui/input";
import { Button } from "./ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card";
import { Alert, AlertDescription } from "./ui/alert";
import { getDatabaseStatus, getStartupImportStatus, importBDPM, importBDPMRemote, type DatabaseStatus, type ImportStatus } from "../services/api";

export function Home() {
  const [searchQuery, setSearchQuery] = useState("");
  const [stats, setStats] = useState<DatabaseStatus | null>(null);
  const [importStatus, setImportStatus] = useState<ImportStatus | null>(null);
  const [sourceDir, setSourceDir] = useState("data/bdpm");
  const [isImporting, setIsImporting] = useState(false);
  const [importError, setImportError] = useState("");
  const [userRole, setUserRole] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    // Get user role
    try {
      const userData = localStorage.getItem("user");
      if (userData) {
        const user = JSON.parse(userData);
        setUserRole(user.role);
      }
    } catch (error) {
      console.error("Failed to parse user data:", error);
    }

    const loadData = async () => {
      try {
        const statusData = await getDatabaseStatus();
        setStats(statusData);
      } catch (error) {
        console.error("Failed to load database status:", error);
      }

      try {
        const startupData = await getStartupImportStatus();
        setImportStatus(startupData);
      } catch (error) {
        console.error("Failed to load startup import status:", error);
      }
    };

    loadData();
  }, []);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    const query = searchQuery.trim();
    navigate(`/search${query ? `?q=${encodeURIComponent(query)}` : ""}`);
  };

  const handleImport = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsImporting(true);
    setImportError("");

    try {
      const result = await importBDPM(sourceDir.trim());
      setImportStatus(result);

      try {
        const statusData = await getDatabaseStatus();
        setStats(statusData);
      } catch (error) {
        console.error("Failed to reload database status:", error);
      }
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : "Import failed";
      setImportError(errorMessage);
    } finally {
      setIsImporting(false);
    }
  };

  const handleRemoteImport = async () => {
    setIsImporting(true);
    setImportError("");

    try {
      const result = await importBDPMRemote(false);
      setImportStatus(result);

      try {
        const statusData = await getDatabaseStatus();
        setStats(statusData);
      } catch (error) {
        console.error("Failed to reload database status:", error);
      }
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : "Import failed";
      setImportError(errorMessage);
    } finally {
      setIsImporting(false);
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
              <p className="text-4xl font-bold mb-2">
                {stats?.medicaments ?? "..."}
              </p>
              <p className="text-blue-100">Médicaments en base</p>
            </div>
            <div>
              <p className="text-4xl font-bold mb-2">
                {stats?.presentations ?? "..."}
              </p>
              <p className="text-blue-100">Présentations en base</p>
            </div>
            <div>
              <p className="text-4xl font-bold mb-2">
                {stats?.compositions ?? "..."}
              </p>
              <p className="text-blue-100">Compositions en base</p>
            </div>
          </div>
        </div>
      </div>

      {/* Import Section - Only for Admin */}
      {userRole === "ADMIN" && (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {/* Import Card */}
          <Card className="border-gray-200">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Upload className="w-5 h-5 text-blue-600" />
                Import BDPM
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <p className="text-sm text-gray-600">
                Importez la BDPM directement depuis les liens officiels du gouvernement (téléchargement).
              </p>

              <div className="space-y-3">
                <Button
                  type="button"
                  onClick={handleRemoteImport}
                  disabled={isImporting}
                  className="w-full"
                >
                  {isImporting ? "Import en cours..." : "Importer depuis Internet"}
                </Button>

                <details className="rounded-lg border border-gray-200 bg-gray-50 p-3">
                  <summary className="cursor-pointer text-sm font-medium text-gray-800">
                    Import local (optionnel)
                  </summary>
                  <form onSubmit={handleImport} className="space-y-3 mt-3">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Dossier source
                      </label>
                      <Input
                        type="text"
                        value={sourceDir}
                        onChange={(e) => setSourceDir(e.target.value)}
                        placeholder="data/bdpm"
                        disabled={isImporting}
                      />
                      <p className="mt-1 text-xs text-gray-600">
                        Le dossier doit contenir <code className="bg-gray-100 px-1 py-0.5 rounded">CIS_bdpm.txt</code>,{" "}
                        <code className="bg-gray-100 px-1 py-0.5 rounded">CIS_CIP_bdpm.txt</code> et{" "}
                        <code className="bg-gray-100 px-1 py-0.5 rounded">CIS_COMPO_bdpm.txt</code>.
                      </p>
                    </div>

                    <Button type="submit" disabled={isImporting} className="w-full" variant="secondary">
                      {isImporting ? "Import en cours..." : "Importer depuis un dossier"}
                    </Button>
                  </form>
                </details>
              </div>

              {importError && (
                <Alert variant="destructive">
                  <XCircle className="h-4 w-4" />
                  <AlertDescription>{importError}</AlertDescription>
                </Alert>
              )}
            </CardContent>
          </Card>

          {/* Import Status Card */}
          <Card className="border-gray-200">
            <CardHeader>
              <CardTitle>État de l'import</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {importStatus ? (
                <>
                  <Alert className={importStatus.succes ? "border-green-200 bg-green-50" : "border-orange-200 bg-orange-50"}>
                    <div className="flex items-start gap-2">
                      {importStatus.succes ? (
                        <CheckCircle2 className="h-4 w-4 text-green-600 mt-0.5 flex-shrink-0" />
                      ) : (
                        <AlertCircle className="h-4 w-4 text-orange-600 mt-0.5 flex-shrink-0" />
                      )}
                      <AlertDescription className={importStatus.succes ? "text-green-800" : "text-orange-800"}>
                        {importStatus.message}
                      </AlertDescription>
                    </div>
                  </Alert>

                  {importStatus.sourceDir && (
                    <div className="p-3 bg-gray-50 rounded-lg">
                      <p className="text-sm text-gray-600">
                        <strong>Dossier :</strong> {importStatus.sourceDir}
                      </p>
                    </div>
                  )}

                  {importStatus.fichierMedicaments && (
                    <div className="space-y-2 text-sm">
                      <p className="font-medium text-gray-900">Fichier : {importStatus.fichierMedicaments.fichier}</p>
                      <ul className="space-y-1 text-gray-600">
                        <li>✓ Importées : {importStatus.fichierMedicaments.lignesImportees}</li>
                        <li>⊘ Ignorées : {importStatus.fichierMedicaments.lignesIgnorees}</li>
                        <li>✗ Invalides : {importStatus.fichierMedicaments.lignesInvalides}</li>
                        <li>📖 Lues : {importStatus.fichierMedicaments.lignesLues}</li>
                      </ul>
                    </div>
                  )}
                </>
              ) : (
                <div className="p-3 bg-gray-50 rounded-lg text-center text-gray-500">
                  <p className="text-sm">Vérification de l'import automatique...</p>
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
      )}
    </div>
  );
}
