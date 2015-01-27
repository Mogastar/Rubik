# Rubik's Cube

Nous avons suivi la méthode proposé par Richard E. Korf pour chercher une solution optimale du Rubik’s Cube. L’algorithm utilisé est IDA* (iterative deepening depth-first search) avec une fonction heuristique basée sur une grande base de données, ceci dit un coût de mémoire très large. Cette base de données enregistre le nombre exact de coups pour résoudre plusieurs sous-cubes. Bien que notre programme est fortement limité par les ordinateurs de test, on verra qu’une taille raisonnable de la base de données nous a permis d’atteindre des améliorations importantes. On doit croire qu’avec un équipement plus performant et une meilleure optimisation, cette méthode nous apportera une solution optimale de tout Rubik’s Cube.

Le consigne d'utilisation du code se trouve à la fin de notre rapport et vous trouverez les explications du code soit dans le rapport aussi, soit dans le commentaire du code source.

Sur un ordinateur portable raisonnable, ce programme permet de trouver une solution de 13 étapes en 10 secondes environ. Si vous avez le temps, vous pouvez raffiner le Pattern Database. J'ai lancé l'initialisation pendant 15 heures sur un PC pour générer un Pattern Database de profondeur 9, sachant que 10 est la profondeur ultime.
