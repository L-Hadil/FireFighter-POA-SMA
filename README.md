# Firefighter vs Fire Agents Game

Ce projet a été réalisé dans le cadre du cours HAI716. Il s'agit d'un jeu simulant un scénario où un agent pompier doit protéger des objectifs contre un agent feu dans un environnement dynamique ou statique.

## Versions du Projet

1. **Version avec Environnement Statique**
   - **Branche :** `statique`
   - Cette version simule un environnement où les obstacles et les objectifs sont fixes et ne changent pas au cours de la simulation.
   - **Exécution :** La classe principale est `FirefighterGame`. Pour lancer le jeu, suivez les étapes ci-dessous.

2. **Version avec Environnement Dynamique (Version Finale)**
   - **Branche :** `main`
   - Cette version introduit un environnement dynamique avec des éléments évolutifs, dont un objectif spécial **"Humain"** :
      - Un objectif "Humain" apparaît au bout de certains temps ou tours de jeu, ajoutant un défi supplémentaire.
      - Les agents (pompier et feu) doivent adapter leur stratégie en fonction de cet objectif dynamique.
   - **Exécution :** La classe principale reste `FirefighterGame`. Les étapes d'exécution sont les mêmes que pour la version statique.

## Présentation du Projet

Un fichier PDF nommé **`Presentation.pdf`** est disponible dans le dépôt. Ce document contient une présentation détaillée du projet, incluant les objectifs, les fonctionnalités et les particularités des deux versions.

## Prérequis

- Java SDK 17 installé sur votre machine.

## Installation et Exécution

1. Clonez le dépôt GitHub :
   ```bash
   git clone https://github.com/L-Hadil/FireFighter-POA-SMA
   ```

2. Accédez à la branche souhaitée :
   - Pour la version statique :
     ```bash
     git checkout statique
     ```
   - Pour la version dynamique (par défaut) :
     ```bash
     git checkout main
     ```

3. Compilez la classe principale :
   ```bash
   javac FirefighterGame.java
   ```

4. Exécutez le jeu :
   ```bash
   java FirefighterGame
   ```

## Instructions pour Modifier le Nombre d'Objectifs

Dans les deux versions, vous pouvez ajuster le nombre d'objectifs pour tester le comportement des agents. Voici comment faire :

1. **Ouvrez la Classe `FirefighterGame`**  
   Modifiez la constante `OBJECTIVES_COUNT` :
   ```java
   private static final int OBJECTIVES_COUNT = 10;
   ```
   Changez la valeur pour refléter le nombre d'objectifs souhaité.

2. **Recompilez et Exécutez le Code** :
   Suivez les étapes d'exécution pour observer les changements.

## Participants

- Hadil LADJ
- - Malik MANSOUR
- Mohamed Aziz Belhaj Hassine

