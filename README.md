# Medixtractor

Application composee de :

- `medixtractor-back` : API Spring Boot / SQLite
- `medixtractor-front` : frontend Vite / React

## Prerequis

- Java 21
- Node.js et npm

## Lancer le backend

Depuis la racine du depot :

```powershell
cd .\medixtractor-back
.\gradlew.bat bootRun
```

Le backend ecoute sur `http://localhost:8080`.

## Lancer le frontend

Depuis la racine du depot :

```powershell
cd .\medixtractor-front
npm.cmd install
npm.cmd run dev
```

Le frontend Vite ecoute en general sur `http://localhost:5173`.

Sous PowerShell, `npm run ...` peut echouer a cause de l'Execution Policy. `npm.cmd` contourne ce probleme.

## Import BDPM

Deux modes d'import sont disponibles :

- import local depuis `medixtractor-back/data/bdpm`
- import distant via telechargement depuis le site officiel BDPM

Import local :

```text
POST /api/imports/bdpm?sourceDir=chemin\vers\un\dossier
```

Le dossier doit contenir :

- `CIS_bdpm.txt`
- `CIS_CIP_bdpm.txt`
- `CIS_COMPO_bdpm.txt`

Par defaut, le backend pointe sur `data/bdpm`, donc les fichiers BDPM presents dans `medixtractor-back/data/bdpm` peuvent etre importes directement.

Import distant :

```text
POST /api/imports/bdpm/remote
```

Cet endpoint telecharge les fichiers BDPM officiels dans le cache configure puis lance l'import.
Les fichiers telecharges sont stockes dans `medixtractor-back/data/bdpm-cache`.

## Comportement au demarrage

Par defaut, `medixtractor.bdpm.default-source-dir=data/bdpm`.

Cela signifie :

- si la base SQLite est vide et que `data/bdpm` contient les 3 fichiers BDPM, le backend peut les importer automatiquement au demarrage
- si la base contient deja des donnees, aucun re-import automatique n'est fait
- l'import distant reste disponible a tout moment via `/api/imports/bdpm/remote`
