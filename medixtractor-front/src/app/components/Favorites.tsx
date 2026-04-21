import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router";
import { AlertCircle, HeartOff, Pill, Star } from "lucide-react";

import { Alert, AlertDescription } from "./ui/alert";
import { Badge } from "./ui/badge";
import { Button } from "./ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card";
import { getFavorites, removeFavorite, type Medicament } from "../services/api";

export function Favorites() {
  const navigate = useNavigate();
  const [favorites, setFavorites] = useState<Medicament[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("authToken");
    if (!token) {
      navigate("/login");
      return;
    }

    const loadFavorites = async () => {
      try {
        setLoading(true);
        setError("");
        const data = await getFavorites();
        setFavorites(data);
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : "Impossible de charger les favoris.";
        setError(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    loadFavorites();
  }, [navigate]);

  const handleRemove = async (cis: string) => {
    try {
      await removeFavorite(cis);
      setFavorites((prev) => prev.filter((item) => String(item.cis) !== cis));
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : "Impossible de retirer ce favori.";
      setError(errorMessage);
    }
  };

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-6 flex items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-semibold text-gray-900">Favoris</h1>
          <p className="text-gray-600">Retrouve ici les medicaments enregistres dans ton compte.</p>
        </div>
        <Button variant="outline" onClick={() => navigate("/search")}>
          Retour a la recherche
        </Button>
      </div>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {loading ? (
        <Card>
          <CardContent className="p-12 text-center">
            <div className="inline-block h-8 w-8 animate-spin rounded-full border-b-2 border-blue-600" />
            <p className="mt-4 text-gray-500">Chargement des favoris...</p>
          </CardContent>
        </Card>
      ) : favorites.length === 0 ? (
        <Card>
          <CardContent className="p-12 text-center">
            <HeartOff className="mx-auto mb-4 h-12 w-12 text-gray-300" />
            <p className="mb-2 text-gray-700">Aucun favori pour le moment.</p>
            <p className="mb-6 text-sm text-gray-500">
              Ajoute des medicaments a tes favoris depuis la recherche ou la fiche detail.
            </p>
            <Button onClick={() => navigate("/search")}>Explorer les medicaments</Button>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4">
          {favorites.map((medicament) => (
            <Card key={medicament.cis} className="border-gray-200">
              <CardHeader className="pb-0">
                <div className="flex items-start justify-between gap-4">
                  <div className="flex items-center gap-3">
                    <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-blue-50">
                      <Pill className="h-6 w-6 text-blue-600" />
                    </div>
                    <div>
                      <CardTitle className="text-lg leading-tight">
                        <Link to={`/drug/${medicament.cis}`} className="hover:text-blue-700">
                          {medicament.name}
                        </Link>
                      </CardTitle>
                      <p className="mt-1 text-sm text-gray-500">{medicament.laboratory}</p>
                    </div>
                  </div>

                  <Button variant="ghost" size="sm" onClick={() => handleRemove(String(medicament.cis))}>
                    <Star className="mr-2 h-4 w-4 fill-yellow-500 text-yellow-500" />
                    Retirer
                  </Button>
                </div>
              </CardHeader>

              <CardContent className="pt-4">
                <div className="mb-3 flex flex-wrap gap-2">
                  {medicament.activeSubstances.map((substance) => (
                    <Badge key={`${medicament.cis}-${substance}`} variant="outline">
                      {substance}
                    </Badge>
                  ))}
                </div>

                <p className="text-sm text-gray-600">
                  {medicament.pharmaceuticalForm} • {medicament.administrationRoute}
                </p>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
