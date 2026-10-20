/*
 * CompteurDangereux.java                           17 oct 2026
 * IUT de Rodez, pas de copyright (ni de "copyleft")
 */

/**
 * race condition
 *
 * @author Léo Arnaud
 */
public class CompteurDangereux {

    private static int compteurGlobal = 0; // Variable partagée MODIFIABLE

    static class Incrementeur implements Runnable {

        private final String nom;
        private final int nombreIncrements;

        public Incrementeur(String nom, int nombreIncrements) {
            this.nom = nom;
            this.nombreIncrements = nombreIncrements;
        }

        @Override
        public void run() {
            for (int i = 0; i < nombreIncrements; i++) {
                // c'est une opération non atomique qui fait lecture -> ajout -> écriture
                compteurGlobal++; // RACE CONDITION ICI !
            }
            // System.out.println(nom + " terminé. Compteur vu : " + compteurGlobal);
        }
    }

    public static int getCompteur() {
        return compteurGlobal;
    }

    public static void resetCompteur() {
        compteurGlobal = 0;
    }
}