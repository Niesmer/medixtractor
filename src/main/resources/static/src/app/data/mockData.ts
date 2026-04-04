export interface Patient {
  id: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  phone: string;
  email: string;
  address: string;
  bloodType: string;
  allergies: string[];
  conditions: string[];
  lastVisit: string;
  nextAppointment?: string;
  status: 'active' | 'inactive';
}

export interface Appointment {
  id: string;
  patientId: string;
  patientName: string;
  date: string;
  time: string;
  type: string;
  doctor: string;
  status: 'scheduled' | 'completed' | 'cancelled';
  reason: string;
}

export interface MedicalRecord {
  id: string;
  patientId: string;
  patientName: string;
  date: string;
  type: string;
  doctor: string;
  diagnosis: string;
  treatment: string;
  notes: string;
}

export const patients: Patient[] = [
  {
    id: '1',
    firstName: 'Marie',
    lastName: 'Dupont',
    dateOfBirth: '1985-03-15',
    gender: 'Femme',
    phone: '01 23 45 67 89',
    email: 'marie.dupont@email.com',
    address: '12 rue de Paris, 75001 Paris',
    bloodType: 'A+',
    allergies: ['Pénicilline', 'Arachides'],
    conditions: ['Hypertension'],
    lastVisit: '2026-03-15',
    nextAppointment: '2026-04-10',
    status: 'active',
  },
  {
    id: '2',
    firstName: 'Jean',
    lastName: 'Martin',
    dateOfBirth: '1978-07-22',
    gender: 'Homme',
    phone: '01 34 56 78 90',
    email: 'jean.martin@email.com',
    address: '45 avenue des Champs, 75008 Paris',
    bloodType: 'O-',
    allergies: [],
    conditions: ['Diabète type 2'],
    lastVisit: '2026-03-20',
    nextAppointment: '2026-04-05',
    status: 'active',
  },
  {
    id: '3',
    firstName: 'Sophie',
    lastName: 'Bernard',
    dateOfBirth: '1992-11-08',
    gender: 'Femme',
    phone: '01 45 67 89 01',
    email: 'sophie.bernard@email.com',
    address: '78 boulevard Saint-Germain, 75006 Paris',
    bloodType: 'B+',
    allergies: ['Latex'],
    conditions: [],
    lastVisit: '2026-02-28',
    status: 'active',
  },
  {
    id: '4',
    firstName: 'Pierre',
    lastName: 'Lefebvre',
    dateOfBirth: '1965-05-30',
    gender: 'Homme',
    phone: '01 56 78 90 12',
    email: 'pierre.lefebvre@email.com',
    address: '23 rue Victor Hugo, 75016 Paris',
    bloodType: 'AB+',
    allergies: ['Aspirine'],
    conditions: ['Asthme', 'Cholestérol'],
    lastVisit: '2026-03-10',
    nextAppointment: '2026-04-15',
    status: 'active',
  },
  {
    id: '5',
    firstName: 'Isabelle',
    lastName: 'Moreau',
    dateOfBirth: '1988-09-12',
    gender: 'Femme',
    phone: '01 67 89 01 23',
    email: 'isabelle.moreau@email.com',
    address: '56 rue de Rivoli, 75004 Paris',
    bloodType: 'A-',
    allergies: [],
    conditions: [],
    lastVisit: '2026-01-15',
    status: 'inactive',
  },
];

export const appointments: Appointment[] = [
  {
    id: '1',
    patientId: '1',
    patientName: 'Marie Dupont',
    date: '2026-04-10',
    time: '09:00',
    type: 'Consultation',
    doctor: 'Dr. Laurent',
    status: 'scheduled',
    reason: 'Suivi hypertension',
  },
  {
    id: '2',
    patientId: '2',
    patientName: 'Jean Martin',
    date: '2026-04-05',
    time: '10:30',
    type: 'Contrôle',
    doctor: 'Dr. Rousseau',
    status: 'scheduled',
    reason: 'Suivi diabète',
  },
  {
    id: '3',
    patientId: '4',
    patientName: 'Pierre Lefebvre',
    date: '2026-04-15',
    time: '14:00',
    type: 'Consultation',
    doctor: 'Dr. Dubois',
    status: 'scheduled',
    reason: 'Renouvellement ordonnance',
  },
  {
    id: '4',
    patientId: '1',
    patientName: 'Marie Dupont',
    date: '2026-03-15',
    time: '11:00',
    type: 'Consultation',
    doctor: 'Dr. Laurent',
    status: 'completed',
    reason: 'Consultation générale',
  },
  {
    id: '5',
    patientId: '3',
    patientName: 'Sophie Bernard',
    date: '2026-04-08',
    time: '15:30',
    type: 'Vaccination',
    doctor: 'Dr. Martin',
    status: 'scheduled',
    reason: 'Rappel vaccin',
  },
];

export const medicalRecords: MedicalRecord[] = [
  {
    id: '1',
    patientId: '1',
    patientName: 'Marie Dupont',
    date: '2026-03-15',
    type: 'Consultation',
    doctor: 'Dr. Laurent',
    diagnosis: 'Hypertension artérielle contrôlée',
    treatment: 'Ramipril 5mg - 1 comprimé par jour',
    notes: 'Tension artérielle stable. Continuer le traitement actuel.',
  },
  {
    id: '2',
    patientId: '2',
    patientName: 'Jean Martin',
    date: '2026-03-20',
    type: 'Contrôle',
    doctor: 'Dr. Rousseau',
    diagnosis: 'Diabète type 2 bien équilibré',
    treatment: 'Metformine 850mg - 2 fois par jour',
    notes: 'HbA1c à 6.8%. Résultats satisfaisants.',
  },
  {
    id: '3',
    patientId: '3',
    patientName: 'Sophie Bernard',
    date: '2026-02-28',
    type: 'Consultation',
    doctor: 'Dr. Martin',
    diagnosis: 'Examen de routine',
    treatment: 'Aucun traitement nécessaire',
    notes: 'Bilan de santé complet. Tous les paramètres sont normaux.',
  },
  {
    id: '4',
    patientId: '4',
    patientName: 'Pierre Lefebvre',
    date: '2026-03-10',
    type: 'Consultation',
    doctor: 'Dr. Dubois',
    diagnosis: 'Asthme stable, dyslipidémie',
    treatment: 'Ventoline en cas de besoin, Statines 20mg',
    notes: 'Pas de crise d\'asthme depuis 2 mois. Cholestérol en amélioration.',
  },
];
