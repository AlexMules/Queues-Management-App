package model;

import logic.SimulationClock;
import logic.ClientCompletionListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class Server implements Runnable {
    private final int ID;
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingPeriod;
    private SimulationClock clock;
    private volatile boolean running = true;
    private Client processingClient; // Clientul care se procesează momentan
    private CyclicBarrier barrier;
    private ClientCompletionListener completionListener;

    public Server(int ID, SimulationClock clock, CyclicBarrier barrier, ClientCompletionListener completionListener) {
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
                        processingClient = clients.poll();
                        if (processingClient != null) {
                            processClient();
                        }
                    } else {
                        processClient();
                    }
                    if (processingClient != null && processingClient.getRemainingServiceTime() <= 0) {
                        int finishTime = clock.getCurrentTime();
                        int waitingTime = finishTime - processingClient.getServiceTime() - processingClient.getArrivalTime();
                        // Notifică finalizarea clientului
                        completionListener.clientCompleted(processingClient, waitingTime);
                        processingClient = null;
                    }
                }
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
