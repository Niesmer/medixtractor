import { useParams, Link } from "react-router";
import { useState, useEffect } from "react";
import { ArrowLeft, Pill, Calendar, Building2, Beaker, FileText, Package, Euro, AlertCircle } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card";
import { Badge } from "./ui/badge";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "./ui/table";
import { Alert, AlertDescription } from "./ui/alert";
import { getMedicamentDetail, type MedicamentDetail } from "../services/api";

export function DrugDetail() {
  const { id } = useParams();
  const [medicament, setMedicament] = useState<MedicamentDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadMedicament = async () => {
      if (!id) {
        setError("Medicament ID not provided");
        setLoading(false);
        return;
      }

      try {
        const data = await getMedicamentDetail(id);
        setMedicament(data);
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : "Failed to load medicament";
        setError(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    loadMedicament();
  }, [id]);

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Card>
          <CardContent className="p-12 text-center">
            <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
            <p className="text-gray-500 mt-4">Chargement du médicament...</p>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (error || !medicament) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Card>
          <CardContent className="p-12 text-center">
            <Pill className="w-12 h-12 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500 mb-4">
              {error || "Médicament non trouvé"}
            </p>
            <Link to="/search?q=" className="text-blue-600 hover:text-blue-700">
              Retour à la recherche
            </Link>
          </CardContent>
        </Card>
      </div>
    );
  }

  const getStatusColor = (status: string) => {
    const lowerStatus = status?.toLowerCase() || "";
    if (lowerStatus.includes("retir")) return "bg-red-100 text-red-700 border-red-200";
    if (lowerStatus.includes("suspend")) return "bg-orange-100 text-orange-700 border-orange-200";
    return "bg-green-100 text-green-700 border-green-200";
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Back Button */}
      <Link
        to="/search?q="
        className="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-6"
      >
        <ArrowLeft className="w-4 h-4" />
        Retour aux résultats
      </Link>

      {/* Header Card */}
      <Card className="mb-6">
        <CardContent className="p-6">
          <div className="flex items-start gap-4">
            <div className="w-16 h-16 bg-blue-100 rounded-xl flex items-center justify-center flex-shrink-0">
              <Pill className="w-8 h-8 text-blue-600" />
            </div>

            <div className="flex-1">
              <div className="flex items-start justify-between mb-3">
                <div>
                  <h1 className="text-3xl font-bold text-gray-900 mb-2">
                    {medicament.name}
                  </h1>
                  <div className="flex items-center gap-2 flex-wrap">
                    <Badge className={`${getStatusColor(medicament.status)} border`}>
                      {medicament.status}
                    </Badge>
                    <span className="text-sm text-gray-500">
                      CIS: {medicament.cis}
                    </span>
                  </div>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mt-4">
                <div className="flex items-center gap-2">
                  <FileText className="w-5 h-5 text-gray-400" />
                  <div>
                    <p className="text-xs text-gray-500">Forme pharmaceutique</p>
                    <p className="text-sm font-medium text-gray-900">
                      {medicament.pharmaceuticalForm}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <Package className="w-5 h-5 text-gray-400" />
                  <div>
                    <p className="text-xs text-gray-500">Voie d'administration</p>
                    <p className="text-sm font-medium text-gray-900">
                      {medicament.administrationRoute}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <Building2 className="w-5 h-5 text-gray-400" />
                  <div>
                    <p className="text-xs text-gray-500">Laboratoire</p>
                    <p className="text-sm font-medium text-gray-900">
                      {medicament.laboratory}
                    </p>
                  </div>
                </div>

                {medicament.marketingDate && (
                  <div className="flex items-center gap-2">
                    <Calendar className="w-5 h-5 text-gray-400" />
                    <div>
                      <p className="text-xs text-gray-500">Mise sur le marché</p>
                      <p className="text-sm font-medium text-gray-900">
                        {new Date(medicament.marketingDate).toLocaleDateString(
                          "fr-FR"
                        )}
                      </p>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Two Column Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column - Compositions */}
        <div className="lg:col-span-2 space-y-6">
          {/* Composition Card */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Beaker className="w-5 h-5 text-blue-600" />
                Composition
              </CardTitle>
            </CardHeader>
            <CardContent>
              {medicament.compositions && medicament.compositions.length > 0 ? (
                <div className="space-y-4">
                  {medicament.compositions.map((composition, idx) => (
                    <div key={idx} className="border-l-4 border-blue-500 pl-4">
                      <h3 className="font-medium text-gray-900 mb-3">
                        Substances actives :
                      </h3>
                      <div className="space-y-2">
                        <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                          <span className="font-medium text-gray-900">
                            {composition.substance}
                          </span>
                          <Badge variant="outline" className="bg-white">
                            {composition.dosage}
                          </Badge>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-gray-500">Aucune composition disponible.</p>
              )}
            </CardContent>
          </Card>

          {/* Presentations Card */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Package className="w-5 h-5 text-blue-600" />
                Présentations commerciales
              </CardTitle>
            </CardHeader>
            <CardContent>
              {medicament.presentations && medicament.presentations.length > 0 ? (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Code CIP</TableHead>
                      <TableHead>Prix</TableHead>
                      <TableHead>Remboursement</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {medicament.presentations.map((presentation, idx) => (
                      <TableRow key={idx}>
                        <TableCell className="font-mono text-sm">
                          {presentation.cip || "N/A"}
                        </TableCell>
                        <TableCell>
                          <div className="flex items-center gap-1">
                            <Euro className="w-4 h-4 text-gray-400" />
                            {presentation.prix || "N/A"}
                          </div>
                        </TableCell>
                        <TableCell>
                          <Badge className="bg-green-100 text-green-700">
                            {presentation.remboursement || "N/A"}
                          </Badge>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              ) : (
                <p className="text-gray-500">
                  Aucune présentation disponible.
                </p>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Right Column - Quick Info */}
        <div className="space-y-6">
          {/* Active Substances */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Substances actives</CardTitle>
            </CardHeader>
            <CardContent>
              {medicament.activeSubstances &&
              medicament.activeSubstances.length > 0 ? (
                <div className="space-y-2">
                  {medicament.activeSubstances.map((substance, idx) => (
                    <div
                      key={idx}
                      className="p-3 bg-blue-50 rounded-lg border border-blue-100"
                    >
                      <p className="text-sm font-medium text-blue-900">
                        {substance}
                      </p>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-gray-500 text-sm">
                  Aucune substance active.
                </p>
              )}
            </CardContent>
          </Card>

          {/* Information Card */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Informations</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <p className="text-xs text-gray-500 mb-1">Statut administratif</p>
                <Badge className={`${getStatusColor(medicament.status)} border`}>
                  {medicament.status}
                </Badge>
              </div>

              <div>
                <p className="text-xs text-gray-500 mb-1">Code CIS</p>
                <p className="text-sm font-medium text-gray-900 font-mono">
                  {medicament.cis}
                </p>
              </div>

              {medicament.procedure && (
                <div>
                  <p className="text-xs text-gray-500 mb-1">Procedure</p>
                  <p className="text-sm font-medium text-gray-900">
                    {medicament.procedure}
                  </p>
                </div>
              )}

              {medicament.marketingDate && (
                <div>
                  <p className="text-xs text-gray-500 mb-1">
                    Date de mise sur le marché
                  </p>
                  <p className="text-sm font-medium text-gray-900">
                    {new Date(medicament.marketingDate).toLocaleDateString(
                      "fr-FR",
                      {
                        day: "numeric",
                        month: "long",
                        year: "numeric"
                      }
                    )}
                  </p>
                </div>
              )}

              <div>
                <p className="text-xs text-gray-500 mb-1">Laboratoire exploitant</p>
                <p className="text-sm font-medium text-gray-900">
                  {medicament.laboratory}
                </p>
              </div>
            </CardContent>
          </Card>

          {/* Notice */}
          <Card className="bg-blue-50 border-blue-200">
            <CardContent className="p-4">
              <p className="text-xs text-blue-900">
                <strong>Note :</strong> Les informations présentées proviennent
                de la base publique des médicaments. Pour toute prescription,
                veuillez consulter la notice officielle et les recommandations
                de l'ANSM.
              </p>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
