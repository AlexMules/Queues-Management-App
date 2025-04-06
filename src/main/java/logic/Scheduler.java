package logic;

import model.Client;
import model.Server;

import java.util.ArrayList;
import java.util.List;

//Sends clients to servers according to the established strategy
public class Scheduler {
    private List<Server> servers;
    private Strategy strategy;
    private final Object lock = new Object();

    public Scheduler() {
        servers = new ArrayList<Server>();
    }

    // metoda care schimba strategia în functie de politica aleasa
    public void changeStrategy(SelectionPolicy policy) {
        switch(policy) {
            case EMPTY_QUEUE:
                strategy = new EmptyQueueStrategy();
                break;
            case SHORTEST_TIME:
                strategy = new ShortestTimeStrategy();
                break;
        }
    }

    // adauga clientul in coada corespunzatoare
    public void dispatchClient(Client client) {
        synchronized (lock) {
            boolean anyEmpty = false;
            for (Server server : servers) {
                if (server.isQueueEmpty()) {
                    anyEmpty = true;
                    break;
                }
            }
            if (anyEmpty) {
                changeStrategy(SelectionPolicy.EMPTY_QUEUE);
            } else {
                changeStrategy(SelectionPolicy.SHORTEST_TIME);
            }
            strategy.addClient(servers, client);
        }
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }
}
