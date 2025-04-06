package logic;

import model.Client;
import model.Server;

import java.util.List;

public class EmptyQueueStrategy implements Strategy {

    @Override
    public void addClient(List<Server> servers, Client client) {
        for (Server server : servers) {
            if (server.isQueueEmpty()) {
                server.addClient(client);
                return;
            }
        }
    }
}
