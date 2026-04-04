import { useParams, Link } from "react-router";
import { 
  User, 
  Calendar, 
  Phone, 
  Mail, 
  MapPin, 
  Droplet, 
  AlertCircle,
  FileText,
  ArrowLeft
} from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card";
import { Badge } from "./ui/badge";
import { Button } from "./ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "./ui/tabs";
import { patients, appointments, medicalRecords } from "../data/mockData";

export function PatientDetails() {
  const { id } = useParams();
  const patient = patients.find(p => p.id === id);
  const patientAppointments = appointments.filter(a => a.patientId === id);
  const patientRecords = medicalRecords.filter(r => r.patientId === id);

  if (!patient) {
    return (
      <div className="p-8">
        <Card>
          <CardContent className="p-12 text-center">
            <User className="w-12 h-12 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500">Patient non trouvé</p>
            <Link to="/patients" className="text-blue-600 hover:text-blue-700 mt-4 inline-block">
              Retour à la liste
            </Link>
          </CardContent>
        </Card>
      </div>
    );
  }

  const age = new Date().getFullYear() - new Date(patient.dateOfBirth).getFullYear();

  return (
    <div className="p-8">
      <Link 
        to="/patients" 
        className="flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-6"
      >
        <ArrowLeft className="w-4 h-4" />
        Retour aux patients
      </Link>

      {/* Patient Header */}
      <Card className="mb-6">
        <CardContent className="p-6">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-4">
              <div className="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center">
                <User className="w-10 h-10 text-blue-600" />
              </div>
              <div>
                <h1 className="text-3xl font-semibold mb-1">
                  {patient.firstName} {patient.lastName}
                </h1>
                <p className="text-gray-600">{age} ans • {patient.gender}</p>
                <Badge 
                  variant={patient.status === 'active' ? 'default' : 'secondary'}
                  className="mt-2"
                >
                  {patient.status === 'active' ? 'Actif' : 'Inactif'}
                </Badge>
              </div>
            </div>
            <Button>Modifier</Button>
          </div>
        </CardContent>
      </Card>

      {/* Patient Info Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Informations de contact</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            <div className="flex items-center gap-3">
              <Phone className="w-5 h-5 text-gray-400" />
              <div>
                <p className="text-sm text-gray-500">Téléphone</p>
                <p className="font-medium">{patient.phone}</p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <Mail className="w-5 h-5 text-gray-400" />
              <div>
                <p className="text-sm text-gray-500">Email</p>
                <p className="font-medium">{patient.email}</p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <MapPin className="w-5 h-5 text-gray-400" />
              <div>
                <p className="text-sm text-gray-500">Adresse</p>
                <p className="font-medium">{patient.address}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Informations médicales</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            <div className="flex items-center gap-3">
              <Droplet className="w-5 h-5 text-gray-400" />
              <div>
                <p className="text-sm text-gray-500">Groupe sanguin</p>
                <p className="font-medium">{patient.bloodType}</p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <Calendar className="w-5 h-5 text-gray-400" />
              <div>
                <p className="text-sm text-gray-500">Date de naissance</p>
                <p className="font-medium">
                  {new Date(patient.dateOfBirth).toLocaleDateString('fr-FR')}
                </p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <Calendar className="w-5 h-5 text-gray-400" />
              <div>
                <p className="text-sm text-gray-500">Dernière visite</p>
                <p className="font-medium">
                  {new Date(patient.lastVisit).toLocaleDateString('fr-FR')}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Alertes médicales</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <div className="flex items-center gap-2 mb-2">
                <AlertCircle className="w-5 h-5 text-red-500" />
                <p className="font-medium">Allergies</p>
              </div>
              {patient.allergies.length > 0 ? (
                <div className="flex flex-wrap gap-1">
                  {patient.allergies.map((allergy, index) => (
                    <Badge key={index} variant="destructive" className="text-xs">
                      {allergy}
                    </Badge>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-gray-500">Aucune allergie connue</p>
              )}
            </div>
            <div>
              <div className="flex items-center gap-2 mb-2">
                <FileText className="w-5 h-5 text-orange-500" />
                <p className="font-medium">Conditions</p>
              </div>
              {patient.conditions.length > 0 ? (
                <div className="flex flex-wrap gap-1">
                  {patient.conditions.map((condition, index) => (
                    <Badge key={index} variant="secondary" className="text-xs">
                      {condition}
                    </Badge>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-gray-500">Aucune condition médicale</p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Tabs */}
      <Tabs defaultValue="appointments" className="w-full">
        <TabsList>
          <TabsTrigger value="appointments">Rendez-vous</TabsTrigger>
          <TabsTrigger value="records">Dossiers médicaux</TabsTrigger>
        </TabsList>

        <TabsContent value="appointments">
          <Card>
            <CardHeader>
              <CardTitle>Historique des rendez-vous</CardTitle>
            </CardHeader>
            <CardContent>
              {patientAppointments.length > 0 ? (
                <div className="space-y-4">
                  {patientAppointments.map((appointment) => (
                    <div 
                      key={appointment.id}
                      className="p-4 border rounded-lg hover:bg-gray-50 transition-colors"
                    >
                      <div className="flex items-start justify-between mb-2">
                        <div>
                          <p className="font-medium">{appointment.type}</p>
                          <p className="text-sm text-gray-600">{appointment.reason}</p>
                        </div>
                        <Badge 
                          variant={
                            appointment.status === 'scheduled' ? 'default' :
                            appointment.status === 'completed' ? 'secondary' :
                            'destructive'
                          }
                        >
                          {appointment.status === 'scheduled' ? 'Programmé' :
                           appointment.status === 'completed' ? 'Terminé' :
                           'Annulé'}
                        </Badge>
                      </div>
                      <div className="flex items-center gap-4 text-sm text-gray-600">
                        <span>Dr. {appointment.doctor.replace('Dr. ', '')}</span>
                        <span>•</span>
                        <span>{new Date(appointment.date).toLocaleDateString('fr-FR')}</span>
                        <span>•</span>
                        <span>{appointment.time}</span>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-gray-500 text-center py-8">Aucun rendez-vous</p>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="records">
          <Card>
            <CardHeader>
              <CardTitle>Dossiers médicaux</CardTitle>
            </CardHeader>
            <CardContent>
              {patientRecords.length > 0 ? (
                <div className="space-y-4">
                  {patientRecords.map((record) => (
                    <div 
                      key={record.id}
                      className="p-4 border rounded-lg hover:bg-gray-50 transition-colors"
                    >
                      <div className="flex items-start justify-between mb-3">
                        <div>
                          <p className="font-medium">{record.type}</p>
                          <p className="text-sm text-gray-600">
                            {new Date(record.date).toLocaleDateString('fr-FR')} • {record.doctor}
                          </p>
                        </div>
                      </div>
                      <div className="space-y-2">
                        <div>
                          <p className="text-sm font-medium text-gray-700">Diagnostic:</p>
                          <p className="text-sm text-gray-600">{record.diagnosis}</p>
                        </div>
                        <div>
                          <p className="text-sm font-medium text-gray-700">Traitement:</p>
                          <p className="text-sm text-gray-600">{record.treatment}</p>
                        </div>
                        <div>
                          <p className="text-sm font-medium text-gray-700">Notes:</p>
                          <p className="text-sm text-gray-600">{record.notes}</p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-gray-500 text-center py-8">Aucun dossier médical</p>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
