# Medixtractor

Application Spring Boot en Java pour importer la base officielle BDPM francaise, la stocker en SQLite selon le schema simple du projet, puis consulter les medicaments via une interface web.

## Prerequis

- Java 21 ou plus recent

## Import BDPM (en ligne)

Par defaut, l'application peut telecharger les fichiers BDPM depuis le site officiel du gouvernement et les mettre en cache dans `data/bdpm-cache`.

Endpoint : `POST /api/imports/bdpm/remote`

Les URLs et le cache sont configurables dans `src/main/resources/application.properties` (prefixe `medixtractor.bdpm.remote.*`).

## Import BDPM (local, optionnel)

Vous pouvez aussi importer depuis un dossier local contenant :

- `CIS_bdpm.txt` (specialites)
- `CIS_CIP_bdpm.txt` (presentations)
- `CIS_COMPO_bdpm.txt` (compositions)

Endpoint : `POST /api/imports/bdpm?sourceDir=...`

## Lancement (backend)

```powershell
.\gradlew.bat bootRun
```

Puis ouvrir `http://localhost:8080`.

## Notes

- Le backend respecte le schema SQL actuel `medicament`, `presentation`, `composition`.
- Le dossier `src/main/resources/static/src` est conserve comme source de reference frontend.
- L'interface effectivement servie par Spring est `src/main/resources/static/index.html`.
