package logic;

import model.Client;
import model.Server;

import java.util.List;

public interface Strategy {
    public void addClient(List<Server> servers, Client client);
}
