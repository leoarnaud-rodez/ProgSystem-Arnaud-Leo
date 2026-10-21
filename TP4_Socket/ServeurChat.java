import java.io.*;
import java.net.*;
import java.util.*;

public class ServeurChat {
    // Liste thread-safe des flux de sortie
    private static final List<PrintWriter> clients = 
        Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Serveur Chat démarré");

        while (true) {
            Socket client = serverSocket.accept();
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            
            // TODO : Ajout du client à la liste partagée (accès synchronisé)
            synchronized (clients) {
                clients.add(out);
            }

            System.out.println("Client connecté. Total : " + clients.size());
            new Thread(new ChatHandler(client, out)).start();
        }
    }

    static class ChatHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;

        public ChatHandler(Socket socket, PrintWriter out) {
            this.socket = socket;
            this.out = out;
        }

        @Override
        public void run() {
            // TODO : Traitement de la réception et diffusion des messages
            try {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
                );

                String ligne;
                while ((ligne = in.readLine()) != null) {
                    System.out.println("Message reçu : " + ligne);
                    broadcast(ligne);
                }
            } catch (IOException e) {
                System.out.println("Erreur / Déconnexion du client");
            } finally {
                // Retrait obligatoire du client de la liste et fermeture du socket
                synchronized (clients) {
                    clients.remove(out);
                }
                System.out.println("Client déconnecté. Total : " + clients.size());
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }

        private void broadcast(String message) {
            synchronized (clients) {
                for (PrintWriter writer : clients) {
                    writer.println(">>> " + message);
                }
            }
        }
    }
}