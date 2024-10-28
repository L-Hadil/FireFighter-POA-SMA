# Firefighter Game

Ce projet est un jeu simulant un scénario où un pompier doit protéger des objectifs contre le feu. Le jeu utilise Java et une structure orientée agent pour gérer les actions autonomes du pompier et du feu sur une grille.

## Configuration des Objectifs : Tester le Jeu avec un Nombre Variable d’Objectifs

Pour tester le comportement des agents et ajuster la difficulté du jeu, il est possible de modifier le nombre d'objectifs. Cette configuration permet d’expérimenter différentes stratégies et d’observer l’efficacité des agents dans divers scénarios.

## Prérequis
- Java SDK 17

## Installation et Exécution
1. Clonez le dépôt GitHub :
   ```bash
   git clone https://github.com/L-Hadil/FireFighter-POA-SMA
   ```
2. Compilez le code :
   ```bash
   javac FirefighterGame.java
   ```
3. Exécutez le jeu :
   ```bash
   java FirefighterGame
   ```

### Instructions pour Modifier le Nombre d'Objectifs
1. **Ouvrez la Classe `FirefighterGame`**  
   Dans votre éditeur, ouvrez le fichier `FirefighterGame.java`.

2. **Modifiez la Constante `OBJECTIVES_COUNT`**  
   Trouvez la ligne suivante dans la classe :
   ```java
   private static final int OBJECTIVES_COUNT = 10;
   ```
   Remplacez `10` par le nombre d'objectifs souhaité, par exemple :
   ```java
   private static final int OBJECTIVES_COUNT = 15;
   ```

3. **Recompilez le Code**  
   Enregistrez vos modifications et recompilez le projet :
   ```bash
   javac FirefighterGame.java
   ```

4. **Exécutez le Jeu**  
   Lancez le jeu pour observer le comportement des agents avec le nouveau nombre d'objectifs :
   ```bash
   java FirefighterGame
   ```

## Participants
- Hadil LADJ
- Mohamed Aziz Belhaj Hassine
- Malik MANSOUR
