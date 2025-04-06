package logic;

import model.Client;

public interface ClientCompletion {
    void clientCompleted(Client client, int waitingTime);
}