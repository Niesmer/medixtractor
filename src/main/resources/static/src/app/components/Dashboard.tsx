import { Users, Calendar, FileText, Activity } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line } from 'recharts';
import { patients, appointments } from "../data/mockData";
import { Link } from "react-router";

export function Dashboard() {
  const activePatients = patients.filter(p => p.status === 'active').length;
  const upcomingAppointments = appointments.filter(a => a.status === 'scheduled').length;
  const todayAppointments = appointments.filter(a => {
    const today = new Date().toISOString().split('T')[0];
    return a.date === today && a.status === 'scheduled';
  }).length;

  const monthlyData = [
    { month: 'Jan', consultations: 45, patients: 12 },
    { month: 'Fév', consultations: 52, patients: 15 },
    { month: 'Mar', consultations: 48, patients: 18 },
    { month: 'Avr', consultations: 38, patients: 10 },
  ];

  const weeklyData = [
    { day: 'Lun', patients: 8 },
    { day: 'Mar', patients: 12 },
    { day: 'Mer', patients: 10 },
    { day: 'Jeu', patients: 15 },
    { day: 'Ven', patients: 11 },
    { day: 'Sam', patients: 6 },
    { day: 'Dim', patients: 3 },
  ];

  const stats = [
    {
      title: 'Patients actifs',
      value: activePatients.toString(),
      icon: Users,
      color: 'text-blue-600',
      bgColor: 'bg-blue-50',
    },
    {
      title: 'Rendez-vous à venir',
      value: upcomingAppointments.toString(),
      icon: Calendar,
      color: 'text-green-600',
      bgColor: 'bg-green-50',
    },
    {
      title: "Aujourd'hui",
      value: todayAppointments.toString(),
      icon: Activity,
      color: 'text-purple-600',
      bgColor: 'bg-purple-50',
    },
    {
      title: 'Dossiers',
      value: patients.length.toString(),
      icon: FileText,
      color: 'text-orange-600',
      bgColor: 'bg-orange-50',
    },
  ];

  const upcomingAppointmentsList = appointments
    .filter(a => a.status === 'scheduled')
    .sort((a, b) => new Date(a.date + ' ' + a.time).getTime() - new Date(b.date + ' ' + b.time).getTime())
    .slice(0, 5);

  return (
    <div className="p-8">
      <div className="mb-8">
        <h1 className="text-3xl font-semibold mb-2">Tableau de bord</h1>
        <p className="text-gray-600">Vue d'ensemble de votre cabinet médical</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <Card key={stat.title}>
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-gray-600 mb-1">{stat.title}</p>
                    <p className="text-3xl font-semibold">{stat.value}</p>
                  </div>
                  <div className={`${stat.bgColor} ${stat.color} p-3 rounded-lg`}>
                    <Icon className="w-6 h-6" />
                  </div>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <Card>
          <CardHeader>
            <CardTitle>Activité mensuelle</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={monthlyData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="consultations" fill="#3b82f6" name="Consultations" />
                <Bar dataKey="patients" fill="#10b981" name="Nouveaux patients" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Patients par jour (semaine)</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={weeklyData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="day" />
                <YAxis />
                <Tooltip />
                <Line type="monotone" dataKey="patients" stroke="#8b5cf6" strokeWidth={2} name="Patients" />
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>

      {/* Upcoming Appointments */}
      <Card>
        <CardHeader>
          <CardTitle>Prochains rendez-vous</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {upcomingAppointmentsList.map((appointment) => (
              <div 
                key={appointment.id} 
                className="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
              >
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                    <Calendar className="w-6 h-6 text-blue-600" />
                  </div>
                  <div>
                    <p className="font-medium">{appointment.patientName}</p>
                    <p className="text-sm text-gray-600">{appointment.reason}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="font-medium">{appointment.time}</p>
                  <p className="text-sm text-gray-600">
                    {new Date(appointment.date).toLocaleDateString('fr-FR', { 
                      day: 'numeric', 
                      month: 'short',
                      year: 'numeric'
                    })}
                  </p>
                </div>
              </div>
            ))}
            <Link 
              to="/appointments" 
              className="block text-center text-blue-600 hover:text-blue-700 font-medium pt-2"
            >
              Voir tous les rendez-vous →
            </Link>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
