/*
 * TestCompteur.java                                        20 oct 2026
 * IUT de Rodez, pas de copyright (ni de "copyleft")
 */

/**
 * teste CompteurSecurise.java
 *
 * @author Léo Arnaud
 */
public class TestMutex {

    private static final int NB_TESTS = 50;
    private static final int NB_THREADS = 9;
    private static final int INCREMENTS_PAR_THREAD = 1_111_111;
    private static final int VALEUR_ATTENDUE = NB_THREADS * INCREMENTS_PAR_THREAD;

    public static void main(String[] args) {
        System.out.println("=== TEST DE COMPTEUR DANGEREUX ===");

        int succes = 0;
        int minObtenu = Integer.MAX_VALUE;
        int maxObtenu = Integer.MIN_VALUE;

        for (int test = 1; test <= NB_TESTS; test++) {
            CompteurSecurise.resetCompteur();

            Thread[] threads = new Thread[NB_THREADS];

            // 1. Création des threads avec le calcul dangereux
            for (int i = 0; i < NB_THREADS; i++) {
                Runnable worker = new CompteurSecurise.Incrementeur("Thread-" + i, INCREMENTS_PAR_THREAD);
                threads[i] = new Thread(worker);
            }

            // démarage des threads
            for (int i = 0; i < NB_THREADS; i++) {
                threads[i].start();
            }

            // on attend la fin des threads
            for (int i = 0; i < NB_THREADS; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    System.err.println("Thread interrompu : " + e.getMessage());
                }
            }

            // récuperer et comparer le résultat avec ce qui est attendu
            int resultat = CompteurSecurise.getCompteur();

            if (resultat == VALEUR_ATTENDUE) {
                succes++;
            }
            if (resultat < minObtenu) {
                minObtenu = resultat;
            }
            if (resultat > maxObtenu) {
                maxObtenu = resultat;
            }

            System.out.println("Test " + test + " / " + NB_TESTS + " : " + resultat 
                               + " (attendu : " + VALEUR_ATTENDUE + ")");
        }

        // stats
        double tauxSucces = ((double) succes / NB_TESTS) * 100.0;
        System.out.println("\nBILAN DES ERREURS");
        System.out.println("Succès : " + succes + " / " + NB_TESTS + " (" + tauxSucces + " %)");
        System.out.println("Valeur min observée : " + minObtenu);
        System.out.println("Valeur max observée : " + maxObtenu);
    }
}