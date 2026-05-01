# Medixtractor - Synthese pour presentation

## 1. Qu'est-ce que Medixtractor ?

Medixtractor est une application web de consultation de medicaments construite autour de la Base de Donnees Publique des Medicaments (BDPM).

L'application permet de :

- centraliser les donnees officielles des medicaments dans une base locale SQLite
- rechercher rapidement un medicament ou une substance active
- filtrer les resultats pour affiner l'analyse
- consulter une fiche detaillee par medicament
- gerer des favoris pour un utilisateur connecte
- administrer l'import des donnees BDPM depuis un dossier local ou via telechargement distant

## 2. Problematique adressee

La consultation des informations medicamenteuses est souvent dispersee, peu ergonomique ou trop brute pour un usage rapide.

Medixtractor repond a ce besoin en proposant :

- une interface simple de recherche
- un acces local et structure a des donnees officielles
- une navigation rapide entre recherche, filtres et fiche detaillee
- un socle pouvant evoluer vers un outil d'aide a la decision ou de veille pharmaceutique

## 3. Ce que l'application fait aujourd'hui

### 3.1 Import et alimentation de la base

L'application sait importer les fichiers officiels BDPM suivants :

- `CIS_bdpm.txt`
- `CIS_CIP_bdpm.txt`
- `CIS_COMPO_bdpm.txt`

Deux modes d'import existent :

- import local depuis un dossier
- import distant avec telechargement des fichiers officiels et mise en cache

Fonctionnement actuel :

- la base SQLite locale stocke les medicaments, les presentations commerciales et les compositions
- un import automatique peut etre lance au demarrage si la base est vide
- l'application affiche l'etat de la base et le resultat du dernier import

### 3.2 Recherche de medicaments

L'utilisateur peut rechercher :

- par nom de medicament
- par substance active

La recherche renvoie une liste de resultats avec :

- le nom du medicament
- son statut administratif
- sa forme pharmaceutique
- sa voie d'administration
- le laboratoire
- les substances actives principales

### 3.3 Filtrage avance

Les resultats peuvent etre filtres par :

- substance
- forme pharmaceutique
- statut
- remboursement
- laboratoire

Le systeme propose des filtres compatibles dynamiques :

- les choix de filtres se mettent a jour selon les resultats possibles
- cela evite de selectionner des combinaisons incoherentes

### 3.4 Consultation detaillee d'un medicament

Chaque fiche medicament affiche :

- l'identifiant CIS
- le nom complet
- le statut administratif
- la forme pharmaceutique
- la voie d'administration
- le laboratoire exploitant
- la date de mise sur le marche
- les substances actives
- la composition detaillee avec dosage
- les presentations commerciales
- le code CIP
- le prix
- le niveau ou statut de remboursement

### 3.5 Comptes utilisateurs

L'application integre un systeme de comptes avec :

- creation de compte
- connexion
- deconnexion

Les roles actuellement prevus sont :

- `ADMIN`
- `DOCTOR`
- `PHARMACIST`

Pour les profils medecin et pharmacien :

- une saisie de numero SIREN ou SIRET est demandee
- une verification peut etre faite via l'API INSEE

### 3.6 Gestion des favoris

Un utilisateur connecte peut :

- ajouter un medicament a ses favoris depuis les resultats
- ajouter ou retirer un favori depuis la fiche detaillee
- retrouver la liste de ses favoris dans une page dediee

Cette fonctionnalite permet de constituer une selection personnelle de medicaments frequemment consultes.

### 3.7 Indicateurs visibles sur la page d'accueil

La page d'accueil donne une vision immediate de la base avec :

- le nombre de medicaments
- le nombre de presentations
- le nombre de compositions
- l'etat du dernier import

## 4. Valeur ajoutee du projet

Medixtractor apporte plusieurs benefices concrets :

- rapidite d'acces a l'information
- structuration des donnees officielles
- ergonomie de recherche superieure a une lecture brute de fichiers
- base locale exploitable pour des usages metier
- fondation technique pour des evolutions plus ambitieuses en sante numerique

## 5. Architecture technique

Le projet est compose de deux briques principales :

- `medixtractor-back` : backend Spring Boot avec SQLite
- `medixtractor-front` : frontend React avec Vite

### Backend

Le backend assure :

- l'import et la transformation des fichiers BDPM
- l'exposition des API REST
- la recherche et le filtrage des medicaments
- la gestion des comptes utilisateurs
- la gestion des favoris

### Frontend

Le frontend assure :

- l'interface de recherche
- l'affichage des resultats et des fiches detaillees
- l'authentification utilisateur
- l'administration simple des imports
- la consultation et la gestion des favoris

### Base de donnees

La persistance repose actuellement sur SQLite, ce qui permet :

- une mise en place simple
- un usage local ou de demonstration rapide
- un deploiement leger

## 6. Cas d'usage possibles

Le projet peut servir a :

- demontrer une chaine complete d'exploitation de donnees publiques de sante
- faciliter la consultation de medicaments par un professionnel
- preparer une base pour un outil metier en pharmacie ou en cabinet
- illustrer un projet full stack avec import, recherche, securisation et persistance

## 7. Perspectives d'evolution

### 7.1 Evolutions fonctionnelles a court terme

- ajouter une recherche encore plus fine par code CIS, code CIP, laboratoire ou voie d'administration
- afficher davantage d'informations reglementaires et cliniques si disponibles
- enrichir la page detail avec contre-indications, interactions ou liens vers notices officielles
- ameliorer l'autocompletion en la branchant directement sur la base reelle
- permettre l'export de resultats ou de favoris en PDF ou CSV

### 7.2 Evolutions pour les utilisateurs metier

- creer des espaces personnalises selon le role utilisateur
- ajouter un historique de recherches
- permettre la constitution de listes partagees de medicaments
- ajouter des annotations personnelles sur une fiche medicament
- proposer des tableaux de bord usage ou statistiques

### 7.3 Evolutions techniques

- migrer de SQLite vers PostgreSQL ou MySQL pour une montee en charge
- renforcer la securite avec une vraie gestion JWT, expiration de session et controle d'acces plus strict
- restreindre certaines actions sensibles, comme l'import, a des utilisateurs autorises
- industrialiser les tests backend et ajouter des tests frontend
- mettre en place une CI/CD pour automatiser build, test et deploiement

### 7.4 Evolutions data et interconnexions

- synchroniser automatiquement les mises a jour de la BDPM
- tracer les dates de mise a jour et l'origine des donnees
- connecter d'autres sources officielles de sante
- croiser les donnees medicaments avec d'autres referentiels utiles

### 7.5 Evolutions produit

- transformer l'application en outil d'aide a la prescription ou a la dispensation
- proposer une version mobile ou tablette
- concevoir une interface adaptee aux professionnels de sante en situation de consultation rapide
- integrer des alertes intelligentes sur disponibilite, remboursement ou statut administratif

## 8. Limites actuelles a pouvoir presenter

Le projet est deja solide pour une demonstration, mais certaines limites peuvent etre mentionnees de facon transparente :

- la base de donnees est orientee usage local et demonstration
- la securite utilisateur reste simple
- certaines pages presentes dans le front semblent encore preparatoires ou non connectees au coeur metier
- l'application est centree sur la consultation de la BDPM, pas encore sur l'aide clinique avancee

## 9. Proposition de message de conclusion pour la presentation

Medixtractor est une application full stack qui transforme des donnees publiques complexes en un outil de consultation clair, rapide et exploitable.

Le projet demontre deja :

- l'import de donnees officielles
- la structuration en base locale
- la recherche et le filtrage avance
- l'acces a une fiche detaillee
- la gestion de comptes et de favoris

La suite naturelle du projet consiste a faire evoluer Medixtractor d'un outil de consultation vers une plateforme metier plus complete, plus securisee et plus intelligente pour les usages de sante.
