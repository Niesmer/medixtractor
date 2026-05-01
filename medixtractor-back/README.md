# Medixtractor Backend

API Spring Boot qui importe la base BDPM dans SQLite et expose les endpoints de recherche medicament.

## Prerequis

- Java 21

## Lancement

Depuis ce dossier :

```powershell
.\gradlew.bat bootRun
```

L'application demarre sur `http://localhost:8080`.

## Import BDPM local

Endpoint :

```text
POST /api/imports/bdpm?sourceDir=...
```

Le dossier source doit contenir :

- `CIS_bdpm.txt`
- `CIS_CIP_bdpm.txt`
- `CIS_COMPO_bdpm.txt`

Par defaut, le projet fournit aussi ces fichiers dans `data/bdpm` et la configuration pointe vers ce dossier.

## Import BDPM distant

Endpoint :

```text
POST /api/imports/bdpm/remote
```

Par defaut, les fichiers telecharges sont mis en cache dans `data/bdpm-cache`.
Les URLs et timeouts sont configurables via `medixtractor.bdpm.remote.*` dans `src/main/resources/application.properties`.

## Comportement au demarrage

Par defaut, `medixtractor.bdpm.default-source-dir=data/bdpm`.

Si la base SQLite est vide, le backend peut importer automatiquement les fichiers presents dans `data/bdpm`.
Si la base contient deja des donnees, aucun import automatique supplementaire n'est lance.
L'import distant reste disponible separement via `/api/imports/bdpm/remote`.
