package logic;

import model.Client;
import model.Server;

import java.util.List;

public class EmptyQueueStrategy implements Strategy {

    @Override
    public void addClient(List<Server> servers, Client client) {
        // Iterăm prin toate serverele; presupunem că Scheduler a sincronizat întreaga secțiune,
        // deci starea cozii nu se va schimba în timpul acestei operații.
        for (Server server : servers) {
            if (server.isQueueEmpty()) {
                server.addClient(client);
                return;
            }
        }
        // Fallback: dacă niciun server nu pare gol (ceea ce nu ar trebui să se întâmple),
        // adăugăm clientul în prima coadă.

        //servers.get(0).addClient(client);
    }
}
