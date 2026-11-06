
package clientPackage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientUDP {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in);
             DatagramSocket client = new DatagramSocket()) {

            // 1. Saisie du nom du client
            System.out.print("Donner votre nom : ");
            String nom = scanner.nextLine();

            // 2. Adresse du serveur
            InetAddress adresseServeur = InetAddress.getByName("localhost");

            // 3. Enregistrement du client auprès du serveur
            String messageInscription = nom + " : JOIN";

            byte[] donneesInscription =
                    messageInscription.getBytes(StandardCharsets.UTF_8);

            DatagramPacket paquetInscription = new DatagramPacket(
                    donneesInscription,
                    donneesInscription.length,
                    adresseServeur,
                    1234
            );

            // Envoi du message d'inscription
            client.send(paquetInscription);

            // 4. Création d'un thread pour recevoir les messages
            Thread reception = new Thread(() -> {

                byte[] buffer = new byte[1024];

                try {
                    while (true) {

                        // Préparation du paquet de réception
                        DatagramPacket paquet = new DatagramPacket(
                                buffer, buffer.length);

                        // Réception d'un message
                        client.receive(paquet);

                        // Conversion du message en texte
                        String message = new String(
                                paquet.getData(),
                                paquet.getOffset(),
                                paquet.getLength(),
                                StandardCharsets.UTF_8
                        );

                        // Affichage du message reçu
                        System.out.println("\n" + message);
                        System.out.print("Votre message : ");
                    }

                } catch (IOException e) {
                    if (!client.isClosed()) {
                        System.out.println("Erreur de réception : " + e.getMessage());
                    }
                }
            });

            // 5. Démarrage du thread de réception
            reception.start();

            // 6. Boucle pour envoyer plusieurs messages
            while (true) {

                System.out.print("Votre message : ");
                String texte = scanner.nextLine();

                // Quitter le chat
                if (texte.equalsIgnoreCase("exit")) {
                    System.out.println("Déconnexion...");
                    client.close();
                    break;
                }

                // Préparation du message avec le nom du client
                String message = nom + " : " + texte;

                // Conversion du message en octets
                byte[] donnees = message.getBytes(StandardCharsets.UTF_8);

                // Préparation du paquet à envoyer
                DatagramPacket paquet = new DatagramPacket(
                        donnees,
                        donnees.length,
                        adresseServeur,
                        1234
                );

                // Envoi du message au serveur
                client.send(paquet);
            }

        } catch (IOException e) {
            System.out.println("Erreur client : " + e.getMessage());
        }
    }
}
