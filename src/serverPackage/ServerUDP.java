
package serverPackage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ServerUDP {

    public static void main(String[] args) {

        // Création du serveur UDP
        try (DatagramSocket serveur = new DatagramSocket(
                new InetSocketAddress("0.0.0.0", 1234))) {

            System.out.println("Serveur UDP en attente des clients...");

            // Liste pour stocker les adresses des clients
            List<SocketAddress> clients = new ArrayList<>();

            // Tableau pour recevoir les messages
            byte[] buffer = new byte[1024];

            // Boucle pour recevoir plusieurs messages
            while (true) {

                // Préparation du paquet de réception
                DatagramPacket paquet = new DatagramPacket(
                        buffer, buffer.length);

                // Réception du message
                serveur.receive(paquet);

                // Récupération de l'adresse du client
                SocketAddress adresseClient = paquet.getSocketAddress();

                // Ajout du client dans la liste s'il n'existe pas
                if (!clients.contains(adresseClient)) {
                    clients.add(adresseClient);
                    System.out.println("Nouveau client : " + adresseClient);
                }

                // Conversion des données reçues en texte
                String message = new String(
                        paquet.getData(),
                        paquet.getOffset(),
                        paquet.getLength(),
                        StandardCharsets.UTF_8
                );

                // Affichage du message reçu
                System.out.println("Message reçu : " + message);

                // Envoi du message à tous les autres clients
                byte[] donnees = message.getBytes(StandardCharsets.UTF_8);

                for (SocketAddress client : clients) {

                    // Ne pas envoyer le message à son expéditeur
                    if (!client.equals(adresseClient)) {

                        DatagramPacket paquetEnvoi = new DatagramPacket(
                                donnees,
                                donnees.length,
                                client
                        );

                        serveur.send(paquetEnvoi);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Erreur serveur : " + e.getMessage());
        }
    }
}
