package model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private int ID;
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingPeriod;

    public Server(int ID){
        this.ID = ID;
        this.clients = new LinkedBlockingQueue<Client>();
        this.waitingPeriod = new AtomicInteger(0);
    }

    public void run() {
        //inca nu am nimic aici
    }

    public int getId() {
        return ID;
    }
}
