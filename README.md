<<<<<<< HEAD
# EventWave

EventWave est une application Android qui permet de découvrir et de suivre les événements à proximité de votre position. L'application utilise l'API Eventbrite pour récupérer les événements et Google Maps pour l'affichage cartographique.

## Fonctionnalités

- Affichage des événements à proximité
- Filtrage par catégorie (Musique, Théâtre, Gaming, Conférence, Sports)
- Affichage sur une carte interactive
- Gestion des favoris
- Calcul d'itinéraire vers l'événement
- Recherche d'événements

## Configuration requise

- Android Studio Arctic Fox ou plus récent
- Android SDK 28 (Android 9.0) ou plus récent
- Google Play Services
- Clé API Eventbrite
- Clé API Google Maps

## Installation

1. Clonez le dépôt :
```bash
git clone https://github.com/votre-username/EventWave.git
```

2. Ouvrez le projet dans Android Studio

3. Configurez les clés API :
   - Dans `app/src/main/java/com/example/eventwave/repository/EventRepository.java`, remplacez `YOUR_EVENTBRITE_API_KEY` par votre clé API Eventbrite
   - Dans `app/src/main/AndroidManifest.xml`, remplacez `YOUR_GOOGLE_MAPS_API_KEY` par votre clé API Google Maps

4. Synchronisez le projet avec Gradle

5. Exécutez l'application sur un émulateur ou un appareil physique

## Utilisation

1. Lancez l'application
2. Accordez les permissions de localisation lorsque demandé
3. Les événements à proximité seront automatiquement chargés
4. Utilisez les filtres en haut de l'écran pour affiner la recherche
5. Appuyez sur un événement pour voir plus de détails
6. Utilisez le bouton étoile pour ajouter/retirer un événement des favoris
7. Appuyez sur le bouton carte pour voir les événements sur une carte
8. Dans la vue carte, appuyez sur un marqueur pour voir les détails de l'événement
9. Utilisez le bouton "Obtenir l'itinéraire" pour ouvrir Google Maps avec l'itinéraire vers l'événement

## Architecture

L'application suit l'architecture MVVM (Model-View-ViewModel) et utilise les composants Android suivants :

- Room Database pour le stockage local
- Retrofit pour les appels API
- Google Maps SDK pour l'affichage cartographique
- ViewModel et LiveData pour la gestion des données
- RecyclerView pour l'affichage des listes
- Material Design pour l'interface utilisateur

## Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :

1. Fork le projet
2. Créer une branche pour votre fonctionnalité
3. Commiter vos changements
4. Pousser vers la branche
5. Ouvrir une Pull Request

