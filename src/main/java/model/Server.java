package model;

import logic.ClientCompletion;
import logic.SimulationClock;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

//server este echivalent cu o coada (queue)
public class Server implements Runnable {
    private final int ID;
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingPeriod;
    private SimulationClock clock; // simulation clock-ul coordoneaza evolutia in timp a simularii
    private volatile boolean running = true;
    private Client processingClient; //clientul care se proceseaza
    private CyclicBarrier barrier; //barrier pentru sincronizarea servers
    private ClientCompletion completionListener; //referinta la interfata ClientCompletion

    public Server(int ID, SimulationClock clock, CyclicBarrier barrier, ClientCompletion completionListener) {
        this.ID = ID;
        this.clients = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
        this.clock = clock;
        this.barrier = barrier;
        this.completionListener = completionListener;
    }

    public void addClient(Client client) {
        clients.add(client);
        waitingPeriod.addAndGet(client.getServiceTime());
    }

    private void processClient() {
        processingClient.decrementRemainingServiceTime();
        waitingPeriod.decrementAndGet();
    }

    @Override
    public void run() {
        try {
            while (running) {
                synchronized (clock.getLock()) {
                    clock.getLock().wait();

                    if (processingClient == null) {
                        processingClient = clients.poll(); //extragem primul client din coada pentru procesare
                        if (processingClient != null) {
                            processClient();
                        }
                    } else {
                        processClient();
                    }

                    //daca clientul a fost procesat, trimitem informatiile necesare pentru statistici (average times)
                    if (processingClient != null && processingClient.getRemainingServiceTime() <= 0) {
                        int finishTime = clock.getCurrentTime();
                        int waitingTime = finishTime - processingClient.getServiceTime() - processingClient.getArrivalTime();
                        completionListener.clientCompleted(processingClient, waitingTime);
                        processingClient = null;
                    }
                }

                try {
                    barrier.await(); //asteapta dupa celelalte thread-uri
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //opreste server-ul
    public void stopServer() {
        running = false;
        synchronized (clock.getLock()) {
            clock.getLock().notifyAll();
        }
    }

    public boolean isQueueEmpty() {
        return clients.isEmpty() && processingClient == null;
    }

    public Client getProcessingClient() {
        return processingClient;
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
