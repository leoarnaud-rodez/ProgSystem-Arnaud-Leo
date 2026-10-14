public class Calcule extends Thread {
    // TODO: Déclarer les attributs nécessaires

    private static final int[] DONNEES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    // private static final int MULTIPLICATEUR = 1000; -> a passer dans le constructeur
    
    private final int valeur;
    private final int multiplicateur;
    private final int index;
    private long resultat;

    public Calcule(int valeur, int multiplicateur, int index) {
        this.valeur = valeur;
        this.multiplicateur = multiplicateur;
        this.index = index;
        this.resultat = 0;
    }

    @Override
    public void run() {
        // Même logique que dans la boucle interne de CalculSequentiel
        for (int j = 0; j < multiplicateur; j++) {
            resultat += valeur * valeur + valeur;
        }
        System.out.println("Traitement de " + valeur);

    }

    public static void main(String[] args) {
        
        // test de thread comme ca on vérifie si ca marche 
        Calcule testThread = new Calcule(5, 1000, 1);

        // lancement du thread
        testThread.start();

        try {
            testThread.join();
        } catch (InterruptedException e) {
            System.err.println("Y'a eu interrpution : " + e.getMessage());
        }

        System.out.println("resultat = " + testThread.getResultat());
    }

    public long getResultat() {
        return resultat;
    }
}