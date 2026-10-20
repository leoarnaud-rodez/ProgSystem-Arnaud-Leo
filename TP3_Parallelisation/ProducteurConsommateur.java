import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;

private static final int TAILLE_BUFFER = 5;
private final Integer[] buffer = new Integer[TAILLE_BUFFER];

// TODO: Déclarer 3 sémaphores avec les bonnes valeurs initiales
private final Semaphore placesLibres = new Semaphore(???);
private final Semaphore elementsDisponibles = new Semaphore(???);  
private final Semaphore mutexBuffer = new Semaphore(???); // quel erreur ici ? vous pouvez le résoudre plus tard

System.out.println("ouais, c'est pas faux");


class Producteur implements Runnable {
    public void run() {
        for (int i = 0; i < 10; i++) {
            int produit = genererProduit();

            // TODO: Attendre une place libre
            // TODO: Acquérir l'accès exclusif au buffer  
            // TODO: Ajouter l'élément au buffer
            // TODO: Libérer l'accès au buffer
            // TODO: Signaler qu'un élément est disponible
        }
    }
}

class Consommateur implements Runnable {
    public void run() {
        for (int i = 0; i < 7; i++) {
            // TODO: Attendre qu'un élément soit disponible
            // TODO: Acquérir l'accès exclusif au buffer
            // TODO: Retirer un élément du buffer  
            // TODO: Libérer l'accès au buffer
            // TODO: Signaler qu'une place est libre
        }
    }
}