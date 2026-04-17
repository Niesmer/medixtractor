# Medixtractor

Application Spring Boot en Java pour importer la base officielle BDPM francaise, la stocker en SQLite selon le schema simple du projet, puis consulter les medicaments via une interface web.

## Prerequis

- Java 21 ou plus recent

## Fichiers BDPM attendus

Placer dans `data/bdpm` ou dans un autre dossier :

- `CIS_bdpm.txt`
- `CIS_CIP_bdpm.txt`
- `CIS_COMPO_bdpm.txt`

Source officielle :
`http://base-donnees-publique.medicaments.gouv.fr/telechargement`

## Lancement

```powershell
.\gradlew.bat bootRun
```

Puis ouvrir `http://localhost:8080`.

## Notes

- Le backend respecte le schema SQL actuel `medicament`, `presentation`, `composition`.
- Le dossier `src/main/resources/static/src` est conserve comme source de reference frontend.
- L'interface effectivement servie par Spring est `src/main/resources/static/index.html`.
