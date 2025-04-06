package logic;

import model.Client;

public interface ClientCompletionListener {
    void clientCompleted(Client client, int waitingTime);
}