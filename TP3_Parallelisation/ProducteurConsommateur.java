/*
 * ProducteurConsommateur.java
 * IUT de Rodez, pas de copyright (ni de "copyleft")
 */

import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;

public class ProducteurConsommateur {

    private static final int TAILLE_BUFFER = 5;
    private final Queue<Integer> buffer = new LinkedList<>();

    // Étape 1 : Déclaration des sémaphores
    private final Semaphore placesLibres = new Semaphore(TAILLE_BUFFER); // 5 au début
    private final Semaphore elementsDisponibles = new Semaphore(0);       // 0 au début
    private final Semaphore mutexBuffer = new Semaphore(1);              // 1 (mutex binaire)

    private int genererProduit() {
        return (int) (Math.random() * 100) + 1;
    }

    class Producteur implements Runnable {
        private final String nom;

        public Producteur(String nom) {
            this.nom = nom;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    int produit = genererProduit();

                    // 1. Attendre une place libre
                    placesLibres.acquire();

                    // 2. Acquérir l'accès exclusif au buffer
                    mutexBuffer.acquire();

                    // 3. Ajouter l'élément au buffer
                    buffer.add(produit);
                    System.out.println("[" + nom + "] Produit : " + produit + " | Taille buffer : " + buffer.size());

                    // 4. Libérer l'accès au buffer
                    mutexBuffer.release();

                    // 5. Signaler qu'un élément est disponible
                    elementsDisponibles.release();

                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.err.println(nom + " interrompu : " + e.getMessage());
                }
            }
        }
    }

    class Consommateur implements Runnable {
        private final String nom;

        public Consommateur(String nom) {
            this.nom = nom;
        }

        @Override
        public void run() {
            // passage de 7 à 10 car sinon le buffer et plein
            for (int i = 0; i < 10; i++) {
                try {
                    // 1. Attendre qu'un élément soit disponible
                    elementsDisponibles.acquire();

                    // 2. Acquérir l'accès exclusif au buffer
                    mutexBuffer.acquire();

                    // 3. Retirer un élément du buffer
                    Integer produit = buffer.poll();
                    System.out.println("  [" + nom + "] Consommé : " + produit + " | Taille buffer : " + buffer.size());

                    // 4. Libérer l'accès au buffer
                    mutexBuffer.release();

                    // 5. Signaler qu'une place est libre
                    placesLibres.release();

                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    System.err.println(nom + " interrompu : " + e.getMessage());
                }
            }
        }
    }

    public Producteur createProducteur(String nom) {
        return new Producteur(nom);
    }

    public Consommateur createConsommateur(String nom) {
        return new Consommateur(nom);
    }
}