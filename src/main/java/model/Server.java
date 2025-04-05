package model;

import logic.SimulationClock;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class Server implements Runnable {
    private final int ID;
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingPeriod;
    private SimulationClock clock; // Referință la ceasul simulării
    private volatile boolean running = true;
    private Client currentClient; // Clientul care se procesează momentan
    private CyclicBarrier barrier; // Bariera pentru sincronizarea tick-urilor

    public Server(int ID, SimulationClock clock, CyclicBarrier barrier) {
        this.ID = ID;
        this.clients = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
        this.clock = clock;
        this.barrier = barrier;
    }

    public void addClient(Client client) {
        clients.add(client);
        waitingPeriod.addAndGet(client.getServiceTime());
    }

    @Override
    public void run() {
        try {
            while (running) {
                synchronized (clock.getLock()) {
                    clock.getLock().wait();
                    // Procesează tick-ul curent
                    if (currentClient == null) {
                        currentClient = clients.poll();
                        if (currentClient != null) {
                            currentClient.decrementRemainingServiceTime();
                            waitingPeriod.decrementAndGet();
                        }
                    } else {
                        currentClient.decrementRemainingServiceTime();
                        waitingPeriod.decrementAndGet();
                    }
                    if (currentClient != null && currentClient.getRemainingServiceTime() <= 0) {
                        currentClient = null;
                    }
                }
                // Așteaptă ca toate thread-urile să fi procesat tick-ul
                try {
                    barrier.await();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stopServer() {
        running = false;
        synchronized (clock.getLock()) {
            clock.getLock().notifyAll();
        }
    }

    public boolean isQueueEmpty() {
        return clients.isEmpty() && currentClient == null;
    }

    public Client getCurrentClient() {
        return currentClient;
    }

    public int getWaitingTime() {
        return waitingPeriod.get();
    }

    public BlockingQueue<Client> getClients() {
        return clients;
    }

    public int getId() {
        return ID;
    }
}
