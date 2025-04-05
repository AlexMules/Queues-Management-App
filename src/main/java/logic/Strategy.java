package logic;

import model.Client;

import java.util.List;

public interface Strategy {
    public void addClient(List<Client> clients, Client c);
}
