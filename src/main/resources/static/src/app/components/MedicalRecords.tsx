import { useState } from "react";
import { FileText, Search, Calendar, User } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card";
import { Input } from "./ui/input";
import { Badge } from "./ui/badge";
import { medicalRecords } from "../data/mockData";

export function MedicalRecords() {
  const [searchQuery, setSearchQuery] = useState("");

  const filteredRecords = medicalRecords.filter(record => 
    record.patientName.toLowerCase().includes(searchQuery.toLowerCase()) ||
    record.diagnosis.toLowerCase().includes(searchQuery.toLowerCase()) ||
    record.type.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const sortedRecords = [...filteredRecords].sort((a, b) => 
    new Date(b.date).getTime() - new Date(a.date).getTime()
  );

  return (
    <div className="p-8">
      <div className="mb-8">
        <h1 className="text-3xl font-semibold mb-2">Dossiers médicaux</h1>
        <p className="text-gray-600">Historique des consultations et traitements</p>
      </div>

      {/* Search */}
      <Card className="mb-6">
        <CardContent className="p-6">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
            <Input
              placeholder="Rechercher dans les dossiers médicaux..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
          </div>
        </CardContent>
      </Card>

      {/* Records List */}
      <div className="space-y-4">
        {sortedRecords.map((record) => (
          <Card key={record.id} className="hover:shadow-md transition-shadow">
            <CardContent className="p-6">
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-start gap-4">
                  <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center flex-shrink-0">
                    <FileText className="w-6 h-6 text-purple-600" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <h3 className="font-semibold text-lg">{record.type}</h3>
                      <Badge variant="outline">{record.doctor}</Badge>
                    </div>
                    <div className="flex items-center gap-4 text-sm text-gray-500">
                      <div className="flex items-center gap-1">
                        <User className="w-4 h-4" />
                        {record.patientName}
                      </div>
                      <span>•</span>
                      <div className="flex items-center gap-1">
                        <Calendar className="w-4 h-4" />
                        {new Date(record.date).toLocaleDateString('fr-FR', {
                          day: 'numeric',
                          month: 'long',
                          year: 'numeric'
                        })}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div className="space-y-3 pl-16">
                <div>
                  <p className="text-sm font-medium text-gray-700 mb-1">Diagnostic:</p>
                  <p className="text-sm text-gray-600">{record.diagnosis}</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-700 mb-1">Traitement:</p>
                  <p className="text-sm text-gray-600">{record.treatment}</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-700 mb-1">Notes:</p>
                  <p className="text-sm text-gray-600">{record.notes}</p>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}

        {sortedRecords.length === 0 && (
          <Card>
            <CardContent className="p-12 text-center">
              <FileText className="w-12 h-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">Aucun dossier médical trouvé</p>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}
