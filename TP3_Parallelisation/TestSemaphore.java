/*
 * TestSemaphore.java
 * IUT de Rodez, pas de copyright (ni de "copyleft")
 */

public class TestSemaphore {

    public static void main(String[] args) {
        System.out.println("SCÉNARIO 1 ÉQUILIBRÉ (2 Producteurs, 2 Consommateurs)");
        ProducteurConsommateur sys1 = new ProducteurConsommateur();

        Thread p1 = new Thread(sys1.createProducteur("Prod-1"));
        Thread p2 = new Thread(sys1.createProducteur("Prod-2"));
        Thread c1 = new Thread(sys1.createConsommateur("Cons-1"));
        Thread c2 = new Thread(sys1.createConsommateur("Cons-2"));

        p1.start(); p2.start(); c1.start(); c2.start();

        try {
            p1.join(); p2.join(); c1.join(); c2.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\nSCÉNARIO 2 DÉSÉQUILIBRÉ (5 Producteurs, 2 Consommateurs)");
        System.out.println("Observation : Le buffer va se remplir vite et bloquer les producteurs.");
        ProducteurConsommateur sys2 = new ProducteurConsommateur();

        for (int i = 1; i <= 5; i++) {
            new Thread(sys2.createProducteur("Prod-Ressource-" + i)).start();
        }
        for (int i = 1; i <= 2; i++) {
            new Thread(sys2.createConsommateur("Cons-Ressource-" + i)).start();
        }
    }
}