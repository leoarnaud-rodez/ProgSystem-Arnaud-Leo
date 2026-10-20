/*
 * CalculParallele.java                             15 oct 2026
 * IUT de Rodez, pas de copyright (ni de "copyleft")
 */

/**
 * plusieurs threads pour faire 10 calculs
 *
 * @author Léo Arnaud
 */
public class CalculParallele {

    private static final int[] DONNEES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private static final int MULTIPLICATEUR = 1000;

    public static void main(String[] args) {
        System.out.println("Calcul Parallèle");

        long debut = System.nanoTime();
        long sommeTotal = 0;

        // Étape 1 : Initialisez un tableau de threads
        Calcule[] threads = new Calcule[DONNEES.length];

        // Étape 2 & 3 : Démarrez tous les threads en parallèle
        for (int i = 0; i < DONNEES.length; i++) {
            threads[i] = new Calcule(DONNEES[i], MULTIPLICATEUR, i);
            threads[i].start(); // L'OS lance le fil d'exécution immédiatement
        }

        // Étape 4 : Attendez la fin de tous les threads
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrompu : " + e.getMessage());
            }
        }

        // Étape 5 : Récupérez et additionnez
        for (int i = 0; i < threads.length; i++) {
            sommeTotal += threads[i].getResultat();
        }

        long duree = (System.nanoTime() - debut) / 1_000_000;
        System.out.println("Résultat total : " + sommeTotal);
        System.out.println("Durée : " + duree + " ms");
    }
}