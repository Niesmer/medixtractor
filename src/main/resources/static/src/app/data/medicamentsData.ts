export interface ActiveSubstance {
  name: string;
  dosage: string;
}

export interface Composition {
  id: string;
  substances: ActiveSubstance[];
}

export interface Presentation {
  id: string;
  packaging: string;
  price: string;
  reimbursement: string;
  cipCode: string;
}

export interface Medicament {
  id: string;
  name: string;
  pharmaceuticalForm: string;
  administrationRoute: string;
  status: 'Autorisé' | 'Retiré' | 'Suspendu';
  laboratory: string;
  activeSubstances: string[];
  compositions: Composition[];
  presentations: Presentation[];
  authorization: string;
  marketingDate?: string;
}

export const medicaments: Medicament[] = [
  {
    id: '1',
    name: 'DOLIPRANE 1000 mg',
    pharmaceuticalForm: 'Comprimé',
    administrationRoute: 'Orale',
    status: 'Autorisé',
    laboratory: 'SANOFI AVENTIS FRANCE',
    activeSubstances: ['Paracétamol'],
    authorization: 'AMM-1234567',
    marketingDate: '1995-03-15',
    compositions: [
      {
        id: 'c1',
        substances: [
          { name: 'Paracétamol', dosage: '1000 mg' }
        ]
      }
    ],
    presentations: [
      {
        id: 'p1',
        packaging: 'Boîte de 8 comprimés',
        price: '2,18 €',
        reimbursement: '65%',
        cipCode: '3400930000000'
      },
      {
        id: 'p2',
        packaging: 'Boîte de 16 comprimés',
        price: '3,85 €',
        reimbursement: '65%',
        cipCode: '3400930000001'
      }
    ]
  },
  {
    id: '2',
    name: 'EFFERALGAN 500 mg',
    pharmaceuticalForm: 'Comprimé effervescent',
    administrationRoute: 'Orale',
    status: 'Autorisé',
    laboratory: 'UPSA SAS',
    activeSubstances: ['Paracétamol'],
    authorization: 'AMM-2345678',
    marketingDate: '1998-06-20',
    compositions: [
      {
        id: 'c2',
        substances: [
          { name: 'Paracétamol', dosage: '500 mg' }
        ]
      }
    ],
    presentations: [
      {
        id: 'p3',
        packaging: 'Boîte de 16 comprimés effervescents',
        price: '2,95 €',
        reimbursement: '65%',
        cipCode: '3400930100000'
      }
    ]
  },
  {
    id: '3',
    name: 'AMOXICILLINE BIOGARAN 1 g',
    pharmaceuticalForm: 'Comprimé pelliculé',
    administrationRoute: 'Orale',
    status: 'Autorisé',
    laboratory: 'BIOGARAN',
    activeSubstances: ['Amoxicilline'],
    authorization: 'AMM-3456789',
    marketingDate: '2005-09-10',
    compositions: [
      {
        id: 'c3',
        substances: [
          { name: 'Amoxicilline trihydrate', dosage: '1 g' }
        ]
      }
    ],
    presentations: [
      {
        id: 'p4',
        packaging: 'Boîte de 6 comprimés',
        price: '2,50 €',
        reimbursement: '65%',
        cipCode: '3400930200000'
      },
      {
        id: 'p5',
        packaging: 'Boîte de 14 comprimés',
        price: '4,20 €',
        reimbursement: '65%',
        cipCode: '3400930200001'
      }
    ]
  },
  {
    id: '4',
    name: 'LEVOTHYROX 50 µg',
    pharmaceuticalForm: 'Comprimé sécable',
    administrationRoute: 'Orale',
    status: 'Autorisé',
    laboratory: 'MERCK SANTE',
    activeSubstances: ['Lévothyroxine sodique'],
    authorization: 'AMM-4567890',
    marketingDate: '2000-02-14',
    compositions: [
      {
        id: 'c4',
        substances: [
          { name: 'Lévothyroxine sodique', dosage: '50 µg' }
        ]
      }
    ],
    presentations: [
      {
        id: 'p6',
        packaging: 'Boîte de 30 comprimés',
        price: '1,88 €',
        reimbursement: '65%',
        cipCode: '3400930300000'
      }
    ]
  },
  {
    id: '5',
    name: 'VENTOLINE 100 µg/dose',
    pharmaceuticalForm: 'Suspension pour inhalation',
    administrationRoute: 'Inhalée',
    status: 'Autorisé',
    laboratory: 'GLAXOSMITHKLINE',
    activeSubstances: ['Salbutamol'],
    authorization: 'AMM-5678901',
    marketingDate: '1992-11-05',
    compositions: [
      {
        id: 'c5',
        substances: [
          { name: 'Salbutamol sulfate', dosage: '100 µg par dose' }
        ]
      }
    ],
    presentations: [
      {
        id: 'p7',
        packaging: 'Flacon de 200 doses',
        price: '2,48 €',
        reimbursement: '65%',
        cipCode: '3400930400000'
      }
    ]
  },
  {
    id: '6',
    name: 'ATORVASTATINE MYLAN 20 mg',
    pharmaceuticalForm: 'Comprimé pelliculé',
    administrationRoute: 'Orale',
    status: 'Autorisé',
    laboratory: 'MYLAN SAS',
    activeSubstances: ['Atorvastatine'],
    authorization: 'AMM-6789012',
    marketingDate: '2010-04-22',
    compositions: [
      {
        id: 'c6',
        substances: [
          { name: 'Atorvastatine calcique', dosage: '20 mg' }
        ]
      }
    ],
    presentations: [
      {
        id: 'p8',
        packaging: 'Boîte de 28 comprimés',
        price: '3,45 €',
        reimbursement: '65%',
        cipCode: '3400930500000'
      },
      {
        id: 'p9',
        packaging: 'Boîte de 90 comprimés',
        price: '9,85 €',
        reimbursement: '65%',
        cipCode: '3400930500001'
      }
    ]
  },
  {
    id: '7',
    name: 'SERETIDE 250 µg/25 µg',
    pharmaceuticalForm: 'Poudre pour inhalation',
    administrationRoute: 'Inhalée',
    status: 'Autorisé',
    laboratory: 'GLAXOSMITHKLINE',
    activeSubstances: ['Fluticasone', 'Salmétérol'],
    authorization: 'AMM-7890123',
    marketingDate: '2003-07-18',
    compositions: [
      {
        id: 'c7',
        substances: [
          { name: 'Propionate de fluticasone', dosage: '250 µg' },
          { name: 'Salmétérol xinafoate', dosage: '25 µg' }
        ]
      }
    ],
    presentations: [
      {
        id: 'p10',
        packaging: 'Dispositif Diskus 60 doses',
        price: '38,35 €',
        reimbursement: '65%',
        cipCode: '3400930600000'
      }
    ]
  },
  {
    id: '8',
    name: 'METFORMINE ARROW 850 mg',
    pharmaceuticalForm: 'Comprimé pelliculé',
    administrationRoute: 'Orale',
    status: 'Autorisé',
    laboratory: 'ARROW GENERIQUES',
    activeSubstances: ['Metformine'],
    authorization: 'AMM-8901234',
    marketingDate: '2008-01-30',
    compositions: [
      {
        id: 'c8',
        substances: [
          { name: 'Chlorhydrate de metformine', dosage: '850 mg' }
        ]
      }
    ],
    presentations: [
      {
        id: 'p11',
        packaging: 'Boîte de 30 comprimés',
        price: '2,15 €',
        reimbursement: '65%',
        cipCode: '3400930700000'
      },
      {
        id: 'p12',
        packaging: 'Boîte de 90 comprimés',
        price: '5,45 €',
        reimbursement: '65%',
        cipCode: '3400930700001'
      }
    ]
  },
  {
    id: '9',
    name: 'LISINOPRIL TEVA 20 mg',
    pharmaceuticalForm: 'Comprimé sécable',
    administrationRoute: 'Orale',
    status: 'Autorisé',
    laboratory: 'TEVA SANTE',
    activeSubstances: ['Lisinopril'],
    authorization: 'AMM-9012345',
    marketingDate: '2009-05-12',
    compositions: [
      {
        id: 'c9',
        substances: [
          { name: 'Lisinopril dihydrate', dosage: '20 mg' }
        ]
      }
    ],
    presentations: [
      {
        id: 'p13',
        packaging: 'Boîte de 28 comprimés',
        price: '3,12 €',
        reimbursement: '65%',
        cipCode: '3400930800000'
      }
    ]
  },
  {
    id: '10',
    name: 'IBUPROFENE ZENTIVA 400 mg',
    pharmaceuticalForm: 'Comprimé pelliculé',
    administrationRoute: 'Orale',
    status: 'Autorisé',
    laboratory: 'ZENTIVA FRANCE',
    activeSubstances: ['Ibuprofène'],
    authorization: 'AMM-0123456',
    marketingDate: '2006-08-25',
    compositions: [
      {
        id: 'c10',
        substances: [
          { name: 'Ibuprofène', dosage: '400 mg' }
        ]
      }
    ],
    presentations: [
      {
        id: 'p14',
        packaging: 'Boîte de 12 comprimés',
        price: '2,05 €',
        reimbursement: 'Non remboursé',
        cipCode: '3400930900000'
      },
      {
        id: 'p15',
        packaging: 'Boîte de 30 comprimés',
        price: '4,15 €',
        reimbursement: 'Non remboursé',
        cipCode: '3400930900001'
      }
    ]
  }
];

export const activeSubstancesList = [
  'Paracétamol',
  'Amoxicilline',
  'Lévothyroxine sodique',
  'Salbutamol',
  'Atorvastatine',
  'Fluticasone',
  'Salmétérol',
  'Metformine',
  'Lisinopril',
  'Ibuprofène'
];
