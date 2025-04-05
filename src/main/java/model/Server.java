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

    public void addClient(Client client){
        //add client to queue
        //increment the waiting period
    }

    public void run() {
        while(true){
            //take next client from queue
            //stop the thread for a time equal with the client's service time
            //decrement the waiting period
        }
    }

    public BlockingQueue<Client> getClients() { //nu sunt sigur cum ar trebui sa arate aceasta metoda
                                                //ce tip returnat ar trebui sa aiba ?
        return clients;
    }

    public int getId() {
        return ID;
    }
}
