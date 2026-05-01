CREATE TABLE medicament (
    cis INTEGER PRIMARY KEY,
    nom TEXT,
    forme TEXT,
    voie TEXT,
    statut TEXT,
    procedure TEXT,
    commercialisation TEXT,
    date_amm TEXT,
    laboratoire TEXT
);

CREATE TABLE presentation (
    cip TEXT PRIMARY KEY,
    cis INTEGER,
    prix REAL,
    remboursement TEXT
);

CREATE TABLE composition (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cis INTEGER,
    substance TEXT,
    dosage REAL,
    unite TEXT
);