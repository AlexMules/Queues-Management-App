package logic;

import model.Client;
import model.Server;

import java.util.List;

public class ShortestTimeStrategy implements Strategy {

    @Override
    public void addClient(List<Server> servers, Client client) {
        Server bestServer = null;
        int minWaitingTime = Integer.MAX_VALUE;

        // Iterăm prin servere pentru a găsi cel cu cel mai mic timp de așteptare
        for (Server server : servers) {
            int waitingTime = server.getWaitingTime();
            if (waitingTime < minWaitingTime) {
                minWaitingTime = waitingTime;
                bestServer = server;
            }
        }

        // Adăugăm clientul în serverul selectat (dacă a fost găsit)
        if (bestServer != null) {
            bestServer.addClient(client);
        }
    }
}
